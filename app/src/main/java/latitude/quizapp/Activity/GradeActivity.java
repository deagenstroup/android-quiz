package latitude.quizapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import latitude.quizapp.Question.Question;
import latitude.quizapp.Quiz;
import latitude.quizapp.R;

public class GradeActivity extends AppCompatActivity {

    private Quiz quiz;

    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //removing the submit button
        GridLayout layout = (GridLayout)this.findViewById(R.id.buttonsGrid_quiz);
        layout.removeView(this.findViewById(R.id.submitButton));

        String filename = this.getIntent().getStringExtra("filename");
        quiz = new Quiz(filename, this, Question.Mode.QUIZ);

        this.initializeGUI();

        currentQuestion = quiz.getCurrentQuestion();
        this.setQuestion(currentQuestion);
    }

    /**
     * Dynamically builds the GUI for the questions in this quiz.
     */
    private void initializeGUI() {
        for(int x = 0; x < quiz.getNumberOfQuestions(); x++) {
            currentQuestion = quiz.getQuestion(x);

            currentQuestion.initializeContainer(this);
        }
    }

    private void setQuestion(Question inQ) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.quiz_layout);
        if(currentQuestion != null)
            layout.removeView(currentQuestion.getContainer());
        currentQuestion = inQ;
        LinearLayout lay = currentQuestion.getContainer();
        layout.addView(lay);
    }
}
