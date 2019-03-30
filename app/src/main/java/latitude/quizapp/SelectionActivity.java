package latitude.quizapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {

    public static final String QUIZ_LIST_FILE = "quiz_list";

    private ArrayList<RadioButton> buttons;

    /**
     * The currently selected button.
     */
    private RadioButton selectedButton;

    /**
     * The filename of the currently selected quiz
     */
    private String selectedFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        String str = this.getIntent().getStringExtra("remove_button");
        if(str != null && str.equals("true"))
            ((ViewGroup)this.findViewById(R.id.button_layout_row1)).removeView(this.findViewById(R.id.newQuiz));
        this.initializeButtons();
        this.logFiles();
    }

    private void initializeButtons() {
        ArrayList<String> strings = this.readFileNames();
        buttons = new ArrayList<RadioButton>();

        for(int i = 0; i < strings.size(); i++) {
            RadioButton newButton = new RadioButton(this);
            newButton.setId(57458+i);
            newButton.setText(strings.get(i));
            newButton.setOnClickListener((v) -> {
                checkButton(newButton);
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            newButton.setLayoutParams(params);
            newButton.setTextAppearance(this, R.style.TopButtonFont);
            newButton.setAllCaps(false);
            LinearLayout layout = (LinearLayout) this.findViewById(R.id.linear_list);
            buttons.add(newButton);
            layout.addView(newButton);
        }

    }


    public void setSelectedFilename(String str) { selectedFilename = str; }

    public String getSelectedFilename() {
        return selectedFilename;
    }

    /**
     * Creates a new quiz object and exits the activity
     * @param filename The filename of the new quiz
     */
    public void addQuiz(String filename) {
        if(filename == null)
            return;

        //write file name to list
        ArrayList<String> filenames = this.readFileNames();
        filenames.add(filename);
        this.writeFileNames(filenames);

        //create a blank quiz object and write to file
        Quiz quiz = new Quiz(this);
        quiz.writeToFile(filename);

        //return filename from activity
        Intent returnIntent = new Intent();
        returnIntent.putExtra("filename", filename);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
    }

    public void removeQuiz(String filename) {
        //remove file from file list
        ArrayList<String> strings = this.readFileNames();
        strings.remove(filename);
        this.writeFileNames(strings);
        //delete file from app space
        this.getApplicationContext().deleteFile(filename);

        for(int i = 0; i < buttons.size(); i++) {
            if(buttons.get(i).getText().equals(filename)) {
                ((LinearLayout)this.findViewById(R.id.linear_list)).removeView(buttons.get(i));
                buttons.remove(i);
                return;
            }
        }
    }

    /**
     * Renames a quiz
     * @param oldFilename The filename of the quiz to be renamed
     * @param filename The new filename of the quiz
     */
    public void renameQuiz(String filename, String oldFilename) {
        ArrayList<String> filenames = this.readFileNames();
        filenames.remove(oldFilename);
        filenames.add(filename);
        this.writeFileNames(filenames);

        File file = new File(this.getApplicationContext().getFilesDir(), oldFilename);
        File to = new File(this.getApplicationContext().getFilesDir(), filename);
        file.renameTo(to);

        for(int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if(button.getText().equals(oldFilename)) {
                button.setText(filename);
                return;
            }
        }
        //this.logFiles();
    }

    /**
     * Prompts the user for a string and either renames an existing quiz or creates a new one
     * with the userinput
     * @param rename If true, the currently selected filename is renamed with the user input.
     *               If false, a new quiz is created with the filename.
     */
    private void promptForString(final boolean rename) {
        String returnStr = null;
        class PromptRunnable implements Runnable {
            private String v;
            void setValue(String inV) {
                this.v = inV;
            }
            String getValue() {
                return this.v;
            }
            public void run() {
                String promptedString = this.getValue();
                if(rename)
                    renameQuiz(promptedString, getSelectedFilename());
                else
                    addQuiz(promptedString);
            }
        }

        final PromptRunnable postRun = new PromptRunnable();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Enter name for new quiz:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                String string = input.getText().toString();
                dialog.dismiss();
                postRun.setValue(string);
                postRun.run();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }



    /**
     * Reads a list of the available quizzes from a file whose name is stored in QUIZ_LIST_FILE
     * @return A list of names of available quizzes
     */
    private ArrayList<String> readFileNames() {
        ArrayList<String> strings = new ArrayList<>();

        DataInputStream stream = null;
        try {
            stream = new DataInputStream(this.getApplicationContext().openFileInput(QUIZ_LIST_FILE));

            String str;
            while(true) {
                str = stream.readUTF();
                strings.add(str);
            }
        } catch(EOFException e) {
            try {
                if (stream != null)
                    stream.close();
            } catch(IOException exp) {
                exp.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return strings;
    }

    /**
     * Writes a list of the available quizzes to a file whose name is stored in QUIZ_LIST_FILE
     * @param filenames A list of names of available quizzes
     */
    private void writeFileNames(ArrayList<String> filenames) {
        DataOutputStream stream = null;
        try {
            stream = new DataOutputStream(this.getApplicationContext().openFileOutput(QUIZ_LIST_FILE, Context.MODE_PRIVATE));
            for(String filename : filenames) {
                stream.writeUTF(filename);
            }
            stream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void checkButton(RadioButton inButton) {
        if(selectedButton != null)
            selectedButton.setChecked(false);
        setSelectedFilename(inButton.getText().toString());
        inButton.setChecked(true);
        selectedButton = inButton;
    }

    public void startHandler(View view) {
        if(selectedFilename == null)
            return;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("filename", selectedFilename);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
    }

    /**
     * Handler for newQuiz button,
     * Prompts the user and creates a new quiz.
     */
    public void newHandler(View view) {
        if(selectedFilename == null)
            return;
        this.promptForString(false);
    }

    public void renameHandler(View view) {
        if(selectedFilename == null)
            return;
        this.promptForString(true);
    }

    public void deleteHandler(View view) {
        if(selectedFilename == null)
            return;
        this.removeQuiz(selectedFilename);
    }

    //DEBUG
    private void logFiles() {
        String[] files = this.getApplicationContext().fileList();
        String s = "";
        for(String str : files) {
            s += ":" + str;
        }
        Log.d("filelist", s);
    }
}
