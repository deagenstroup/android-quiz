package latitude.quizapp;

import android.app.Activity;
import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

import latitude.quizapp.Question.Question;
import latitude.quizapp.Question.MultipleChoiceQuestion;

public class Quiz {

    private String filename;

    private Activity activity;

    private int currentQuestion;

    private ArrayList<Question> questions;

    public Quiz(Activity inActivity) {
        filename = "quiz";
        activity = inActivity;
        questions = new ArrayList<>();
        Question question = new MultipleChoiceQuestion();
        question.setQuiz(this);
        questions.add(question);
        currentQuestion = 0;
    }

    public Quiz(ArrayList<Question> inQuests, Activity inActivity) {
        questions = inQuests;
        for(Question q : questions)
            q.setQuiz(this);
        activity = inActivity;
    }

    public Quiz(String fileName, Activity inActivity, Question.Mode inMode) {
        activity = inActivity;
        this.readFromFile(fileName, inMode);
    }

    //Accessors

    public Question getQuestion(int i) {
        return questions.get(i);
    }

    public Question getCurrentQuestion() { return questions.get(currentQuestion); }

    public boolean hasNext() { return currentQuestion < questions.size()-2; }

    public boolean hasPrevious() { return currentQuestion > 0; }

    public int getNumberOfQuestions() {
        int d = 0;
        return questions.size();
    }

    public int getNumberCorrect() {
        int total = 0;
        for(Question q : questions) {
            if(q.isCorrectAnswer())
                total++;
        }
        return total;
    }

    public float getPercentCorrect() {
        return ((float)this.getNumberCorrect() / (float)this.getNumberOfQuestions()) * 100.0f;
    }

    public Activity getActivity() { return activity; }

    //Modifiers

    public void changeMode(Question.Mode inMode) {
        for(int i = 0; i < questions.size(); i++) {
            questions.get(i).changeMode(inMode);
        }
    }

    public void addQuestion(Question inQuestion) {
        this.addQuestion(currentQuestion+1, inQuestion);
    }

    public void addQuestion(int index, Question inQuestion) {
        questions.add(index, inQuestion);
    }

    public void deleteQuestion(Question inQuestion) {
        questions.remove(inQuestion);
    }

    public Question nextQuestion() {
        if(currentQuestion < questions.size()-1) {
            currentQuestion++;
            return questions.get(currentQuestion);
        } else
            return null;
    }

    public Question previousQuestion() {
        if(currentQuestion > 0) {
            currentQuestion--;
            return questions.get(currentQuestion);
        } else
            return null;
    }

    public void gradeQuiz() {
        /**Question gradeScreen = new Question(null, null, 0, this, Question.Mode.GRADE);
        gradeScreen.initializeGradeScreen(activity);
        this.addQuestion(0, gradeScreen);
        currentQuestion = 0;**/
    }

    //I/O

    public void writeToFile(String fileName) {
        filename = fileName;
        this.writeToFile();
    }

    public void writeToFile() {
        DataOutputStream stream;
        try {
            stream = new DataOutputStream(activity.getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE));
            stream.writeInt(questions.size());
            for(int i = 0; i < questions.size(); i++) {
                questions.get(i).writeToStream(stream);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void readFromFile(String fileName, Question.Mode inMode) {
        filename = fileName;
        this.readFromFile(inMode);
    }

    private void readFromFile(Question.Mode inMode) {
        DataInputStream stream = null;
        int size = 1;
        try {
            stream = new DataInputStream(activity.getApplicationContext().openFileInput(filename));
            size = stream.readInt();
            questions = new ArrayList<>();
            for(int i = 0; i < size; i++) {
                String type = stream.readUTF();
                if(type == "MultipleChoice")
                    questions.add(new MultipleChoiceQuestion(stream, this, inMode));
            }
            for(Question q: questions) {
                q.setQuiz(this);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void readFromGUI() {
        for(int i = 0; i < questions.size(); i++) {
            questions.get(i).readFromGUI();
        }
    }

}
