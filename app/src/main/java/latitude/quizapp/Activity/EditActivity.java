package latitude.quizapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import latitude.quizapp.Question.Question;
import latitude.quizapp.Question.MultipleChoiceQuestion;
import latitude.quizapp.Quiz;
import latitude.quizapp.R;

public class EditActivity extends AppCompatActivity {

    private Quiz quiz;

    private MultipleChoiceQuestion currentQuestion;

    private final int NOTHING = 1,
                      DELETE = 2,
                      SETCORRECT = 3;

    private int buttonAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        String filename = this.getIntent().getStringExtra("filename");
        quiz = new Quiz(filename, this, Question.Mode.EDIT);

        this.initializeGUI();

        currentQuestion = (MultipleChoiceQuestion)quiz.getCurrentQuestion();
        this.setQuestion(currentQuestion);
    }

    /**
     * Dynamically builds the GUI for the questions.
     */
    private void initializeGUI() {
        int w = 0;
        for(int x = 0; x < quiz.getNumberOfQuestions(); x++) {
            currentQuestion = quiz.getQuestion(x);
            currentQuestion.initializeContainer(this);
        }
    }

    public void addAnswer(View view) {
        currentQuestion.addAnswer("answer");
    }

    public void addQuestion(View inView) {
        EditActivity blank = this;
        quiz.addQuestion(new MultipleChoiceQuestion(quiz, Question.Mode.EDIT));
    }

    public void deleteQuestion(View inView) {
        if(quiz.getNumberOfQuestions() >= 2) {
            Question deleteQuestion = currentQuestion;
            if(quiz.hasNext()) {
                this.nextQuestion(null);
                quiz.deleteQuestion(deleteQuestion);
            } else if(quiz.hasPrevious()) {
                this.previousQuestion(null);
                quiz.deleteQuestion(deleteQuestion);
            }
        }
    }

    public void saveChanges(View inView) {
        quiz.readFromGUI();
        quiz.writeToFile();
    }

    public void nextQuestion(View view) {
        Question q = quiz.nextQuestion();
        if(q != null) {
            this.setQuestion(q);
        }
    }

    public void previousQuestion(View view) {
        Question q = quiz.previousQuestion();
        if(q != null) {
            this.setQuestion(q);
        }
    }

    private void setQuestion(MultipleChoiceQuestion inQ) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.edit_layout);
        if(currentQuestion != null)
            layout.removeView(currentQuestion.getContainer());
        currentQuestion = inQ;
        LinearLayout lay = currentQuestion.getContainer();
        layout.addView(lay);
    }

    public void setCorrectAnswer(View view) {
        if(currentQuestion.getSelected() != -1)
            currentQuestion.setCorrectAnswer(currentQuestion.getSelected());
    }

    public void deleteAnswer(View view) {
        if(currentQuestion.getSelected() != -1)
            currentQuestion.deleteAnswer(currentQuestion.getSelected());
    }
}
