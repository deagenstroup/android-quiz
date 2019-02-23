package latitude.quizapp;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuizActivity extends AppCompatActivity {

    private Quiz quiz;

    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

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

    public void gradingScreen(View view) {
        this.gradingScreen1();
    }

    public void nextQuestion(View inView) {
        Question q = quiz.nextQuestion();
        if(q != null)
            this.setQuestion(q);
    }

    public void previousQuestion(View inView) {
        Question q = quiz.previousQuestion();
        if(q != null)
            this.setQuestion(q);
    }

    /**
     * Changes the activity to a screen which shows the user their grade on the quiz.
     */
    private void gradingScreen() {
        this.setContentView(R.layout.grade_screen);
        TextView view;

        view = (TextView) findViewById(R.id.numberCorrect);
        view.setText("#Correct: " + quiz.getNumberCorrect() + "/" + quiz.getNumberOfQuestions());

        int percent = (int)quiz.getPercentCorrect();
        view = (TextView) findViewById(R.id.percentCorrect);
        view.setText("%" + percent);

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
        view = (TextView) findViewById(R.id.grade);
        view.setText("letter grade: " + letter);
    }

    private void gradingScreen1() {
        //removing the submit button
        GridLayout layout = (GridLayout)this.findViewById(R.id.buttonsGrid_quiz);
        layout.removeView(this.findViewById(R.id.submitButton));

        LinearLayout lay = (LinearLayout) findViewById(R.id.quiz_layout);
        if(currentQuestion != null)
            lay.removeView(currentQuestion.getContainer());
        quiz.changeMode(Question.Mode.GRADE);

        this.initializeGUI();

        quiz.gradeQuiz();
        currentQuestion = quiz.getCurrentQuestion();
        this.setQuestion(currentQuestion);
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
