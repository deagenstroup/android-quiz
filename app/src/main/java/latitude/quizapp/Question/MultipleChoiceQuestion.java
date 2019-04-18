package latitude.quizapp.Question;

import android.app.Activity;
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

/**
 * A question which allows the user to pick one of many multiple choice answers.
 */

public class MultipleChoiceQuestion extends Question {

    //graphical objects
    private ArrayList<LinearLayout> answerContainers;
    private ArrayList<TextView> answerTexts;
    private ArrayList<RadioButton> answerButtons;

    /**
     * An array of strings containing all of the possible answers to the question.
     */
    private ArrayList<String> answers;

    /**
     * The index of the answers array which contains the correct answer to the question.
     */
    private int indexOfCorrect;

    /**
     * The index of the answers array which represents the answer that the user submitted.
     */
    private int indexOfSubmitted;
    // Multiple Choice
    private int indexOfSelected;

    {
        indexOfCorrect = -1;
        indexOfSubmitted = -1;
        indexOfSelected = -1;
    }

    public MultipleChoiceQuestion() {
        question = "What is 2+2?";
        answers = new ArrayList<>();
        answers.add("four");
        indexOfCorrect = 0;
    }

    /**
     * Full constructor
     */
    public MultipleChoiceQuestion(String inQuestion, ArrayList<String> inAnswers,
                                  int inIndex, Quiz inQuiz, Mode inMode) {
        question = inQuestion;
        answers = inAnswers;
        indexOfCorrect = inIndex;
        quiz = inQuiz;
        mode = inMode;
        if(quiz.getActivity() != null && inMode != Mode.GRADE) {
            this.initializeContainer(quiz.getActivity());
        }
    }

    /**
     * Default constructor
     */
    public MultipleChoiceQuestion(Quiz inQuiz, Mode inMode) {
        this("question", new ArrayList<String>(), 0, inQuiz, inMode);
    }

    /**
     * The file constructor.
     */
    public MultipleChoiceQuestion(DataInputStream stream, Quiz inQuiz, Mode inMode) {
        quiz = inQuiz;
        mode = inMode;
        this.readFromStream(stream);
        if(quiz.getActivity() != null) {
            this.initializeContainer(quiz.getActivity());
        }
    }

    // GUI

    public void initalizeContainer(Activity activity) {
        super.initializeContainer(activity);
        answerContainers = new ArrayList<>();
        answerButtons = new ArrayList<>();
        answerTexts = new ArrayList<>();
        answerTexts.add(questionText);

        ArrayList<String> answers = this.getAnswers();
        for(int i = 0; i < answers.size(); i++) {
            String answer = answers.get(i);

            this.addAnswerContainer(answer, i);
        }
    }

    private void addAnswerContainer(String answer, int i) {
        LinearLayout answerLayout = new LinearLayout(container.getContext());
        answerLayout.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton newButton = null;
        if(mode == Mode.GRADE) {
            ImageView image = new ImageView(answerLayout.getContext());
            if(this.isCorrectAnswer(i)) {
                //set picture to check mark
                image.setImageResource(R.drawable.check_mark_green);
            } else if(i == indexOfSubmitted) {
                //set picture to x mark
                image.setImageResource(R.drawable.x_mark_red);
            }
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setLayoutParams(new ViewGroup.LayoutParams(60, 60));
            answerLayout.addView(image);
        } else {
            newButton = new RadioButton(container.getContext());
            final char c = (char) (65 + i);
            newButton.setText("" + c + ": ");
            final MultipleChoiceQuestion q = this;
            final int index = i;
            if (mode == Mode.EDIT) {
                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        q.checkButton(index);
                        q.indexOfSelected = index;
                    }
                });
            } else {
                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        q.checkButton(index);
                        q.indexOfSelected = index;
                        Button button = (Button) v;
                        int c = (int) button.getText().charAt(0);
                        ((MultipleChoiceQuestion)quiz.getCurrentQuestion()).submitAnswer(c - 65);
                    }
                });
            }
            newButton.setTextAppearance(answerLayout.getContext(), R.style.QuestionFont);
            newButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            answerLayout.addView(newButton);
        }

        TextView newText;
        if(mode == Mode.EDIT) {
            newText = new EditText(container.getContext());
        } else {
            newText = new TextView(container.getContext());
        }
        newText.setText(answer);
        newText.setTextAppearance(answerLayout.getContext(), R.style.QuestionFont);
        newText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        answerLayout.addView(newText);

        answerContainers.add(answerLayout);
        container.addView(answerLayout);
        if(mode != Mode.GRADE)
            answerButtons.add(newButton);

        if(mode == Mode.EDIT) {
            if (answerTexts == null) {
                answerTexts = new ArrayList<>();
            }
            answerTexts.add(newText);
        }
    }

    public void checkButton(int index) {
        for(int i = 0; i < answerButtons.size(); i++) {
            RadioButton b = answerButtons.get(i);
            if(i == index) {
                b.setChecked(true);
            } else {
                b.setChecked(false);
            }
        }
    }

    /**
     * Reads all of the values stored in the GUI objects (EditTexts) and applies their values to
     * the internal variable of this object.
     */
    public void readFromGUI() {
        super.readFromGUI();
        answers = new ArrayList<>();
        for(int i = 1; i < answerTexts.size(); i++) {
            answers.add(answerTexts.get(i).getText().toString());
        }
    }

    /**
     * This method sets all of the buttons letters to their correct values and is to be called
     * after deleting an answer from the quiz.
     */
    public void fixButtons() {
        for(int i = 0; i < answerButtons.size(); i++) {
            answerButtons.get(i).setText("" + (char)(i + 65));
        }
    }

    // IO

    public void writeToStream(DataOutputStream stream) {
        try {
            stream.writeUTF("MultipleChoice");
            stream.writeUTF(question);
            stream.writeInt(answers.size());
            for(int i = 0; i < answers.size(); i++) {
                stream.writeUTF(answers.get(i));
            }
            stream.writeInt(indexOfCorrect);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void readFromStream(DataInputStream stream) {
        try {
            question = stream.readUTF();
            int size = stream.readInt();
            answers = new ArrayList<String>();
            for(int i = 0; i < size; i++) {
                answers.add(stream.readUTF());
            }
            indexOfCorrect = stream.readInt();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Accessors

    public ArrayList<String> getAnswers() {
        ArrayList<String> retAnswers = new ArrayList<>();
        for(int i = 0; i < answers.size(); i++) {
            retAnswers.add(new String(answers.get(i)));
        }
        return retAnswers;
    }

    public boolean isCorrectAnswer() {
        if(indexOfCorrect == -1)
            return false;
        return indexOfCorrect == indexOfSubmitted;
    }

    public boolean isCorrectAnswer(int i) { return i == indexOfCorrect; }

    public int getSelected() {
        return indexOfSelected;
    }

    public void addAnswer(String inAnswer) {
        answers.add(new String(inAnswer));
        if(guiInitialized) {
            this.addAnswerContainer(inAnswer, answers.size()-1);
        }
    }

    public void deleteAnswer(int i) {
        answers.remove(i);
        if(guiInitialized) {
            answerTexts.remove(i+1);
            answerButtons.remove(i);
            container.removeView(answerContainers.remove(i));
            this.fixButtons();
        }
    }

    public void setCorrectAnswer(int inIndex) {
        indexOfCorrect = inIndex;
    }

    public void submitAnswer(int inAnswer) { indexOfSubmitted = inAnswer; }
}
