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

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String QUIZ_LIST_FILE = "quiz_list";

    public static final int DELETE_MODE = 1,
                            RENAME_MODE = 2;

    private int mode;

    private String dialogFilename = null;

    private String oldFileName;

    private ArrayList<Button> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        String str = this.getIntent().getStringExtra("remove_button");
        if(str != null && str.equals("true"))
            ((ViewGroup)this.findViewById(R.id.selection_button_layout)).removeView(this.findViewById(R.id.newQuiz));
        this.initializeButtons();
        this.logFiles();
    }

    private void initializeButtons() {
        ArrayList<String> strings = this.readFileNames();
        buttons = new ArrayList<>();

        for(int i = 0; i < strings.size(); i++) {
            Button newButton = new Button(this);
            newButton.setId(57458+i);
            newButton.setText(strings.get(i));
            newButton.setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            newButton.setLayoutParams(params);
            newButton.setTextAppearance(this, R.style.TopButtonFont);
            newButton.setAllCaps(false);
            LinearLayout layout = (LinearLayout) this.findViewById(R.id.linear_list);
            buttons.add(newButton);
            layout.addView(newButton);
        }

    }

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

    private void promptForString(final boolean rename) {
        class PromptRunnable implements Runnable {
            private String v;
            void setValue(String inV) {
                this.v = inV;
            }
            String getValue() {
                return this.v;
            }
            public void run() {
                dialogFilename = this.getValue();
                if(rename)
                    renameQuiz1();
                else
                    addQuiz1();
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

    public void addQuiz(View view) {
        this.promptForString(false);
    }

    public void addQuiz1() {
        if(dialogFilename == null)
            return;

        //write file name to list
        ArrayList<String> filenames = this.readFileNames();
        filenames.add(dialogFilename);
        this.writeFileNames(filenames);

        //create a blank quiz object and write to file
        Quiz quiz = new Quiz(this);
        quiz.writeToFile(dialogFilename);

        //return filename from activity
        Intent returnIntent = new Intent();
        returnIntent.putExtra("filename", dialogFilename);
        this.setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
    }

    public void renameQuiz1() {
        ArrayList<String> filenames = this.readFileNames();
        filenames.remove(oldFileName);
        filenames.add(dialogFilename);
        this.writeFileNames(filenames);

        File file = new File(this.getApplicationContext().getFilesDir(), oldFileName);
        File to = new File(this.getApplicationContext().getFilesDir(), dialogFilename);
        file.renameTo(to);

        for(int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if(button.getText().equals(oldFileName)) {
                button.setText(dialogFilename);
                mode = 0;
                return;
            }
        }
        //this.logFiles();
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
                mode = 0;
                return;
            }
        }
    }

    public void onClick(View view) {
        Button button = (Button) view;
        if(mode == RENAME_MODE) {
            //this.logFiles();
            oldFileName = button.getText().toString();
            this.promptForString(true);
        } else if(mode == DELETE_MODE) {
            String filename = button.getText().toString();
            this.removeQuiz(filename);
            mode = 0;
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("filename", button.getText());
            this.setResult(Activity.RESULT_OK, returnIntent);
            this.finish();
        }
    }

    public void renameQuiz(View view) {
        mode = RENAME_MODE;
    }

    public void deleteQuiz(View view) {
        mode = DELETE_MODE;
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
