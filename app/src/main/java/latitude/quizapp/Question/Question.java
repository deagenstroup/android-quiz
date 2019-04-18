package latitude.quizapp.Question;

import android.content.Context;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import latitude.quizapp.Quiz;
import latitude.quizapp.R;

/** Test **/

/**
 * Class which represents a single question itself and does not contain any sort of interface
 * or GUI for the question.
 */
public abstract class Question {

    protected LinearLayout container;

    protected TextView questionText;

    /**
     * True if the graphical elements of the question (textviews, buttons, etc.) have been initialized.
     */
    protected boolean guiInitialized;

    public enum Mode {
        QUIZ,
        EDIT,
        GRADE
    }

    protected Mode mode;

    protected Quiz quiz;

    protected String question;

    {
        guiInitialized = false;
    }

    public Question() {}

    //GUI

    public void initializeContainer(Activity activity) {
        Context context = activity.getApplicationContext();
        container = new LinearLayout(context);
        container.setId( (int)(Math.random() * 65000d) );
        container.setOrientation(LinearLayout.VERTICAL);

        //setting the size of the container
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //creating the question text at the top
        TextView questionText;
        if(mode == Mode.EDIT) {
            questionText = new EditText(activity);
        } else {
            questionText = new TextView(activity);
        }
        questionText.setText(this.getQuestion());
        questionText.setTextAppearance(container.getContext(), R.style.QuestionFont);
        questionText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        container.addView(questionText);

        guiInitialized = true;
    }

    /**
     * Used to create a special kind of question object which basically just serves as
     * a container for a grading screen which is put at the beginning of the quiz when the
     * user pushes the button for the quiz to be graded.
     * @param activity
     */
    public void initializeGradeScreen(Activity activity) {
        Context context = activity.getApplicationContext();
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView view = new TextView(context);
        view.setText("#Correct: " + quiz.getNumberCorrect() + "/" + quiz.getNumberOfQuestions());
        view.setTextAppearance(context, R.style.GradeFont);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(view);

        int percent = (int)quiz.getPercentCorrect();
        view = new TextView(context);
        view.setText("%" + percent);
        view.setTextAppearance(context, R.style.GradeFont);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(view);

        String letter = "X";
        if(percent >= 90) {
            letter = "A";
        } else if(percent >= 80) {
            letter = "B";
        } else if(percent >= 70) {
            letter = "C";
        } else if(percent >= 60) {
            letter = "D";
        } else {
            letter = "F";
        }
        view = new TextView(context);
        view.setText("letter grade: " + letter);
        view.setTextAppearance(context, R.style.GradeFont);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(view);
    }

    /**
     * Reads all of the values stored in the GUI objects (EditTexts) and applies their values to
     * the internal variable of this object.
     */
    public void readFromGUI() {
        question = questionText.getText().toString();
    }

    //IO

    public abstract void writeToStream(DataOutputStream stream);

    public abstract void readFromStream(DataInputStream stream);

    //Accessors

    public LinearLayout getContainer() {
        return container;
    }

    public String getQuestion() { return question; }

    public abstract boolean isCorrectAnswer();

    public void changeMode(Mode inMode) {

        mode = inMode;
    }

    //Mutators

    public void setQuiz(Quiz inQuiz) {
        quiz = inQuiz;
    }

    public void printTest() {
        Log.d("test", "test: Question");
    }

    public static void convertQuizes() {

    }
}
