package latitude.quizapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import latitude.quizapp.Question.Question;
import latitude.quizapp.Question.ShortAnswerQuestion;
import latitude.quizapp.R;

public class MainActivity extends AppCompatActivity {

    /**
     * Special codes used to launch different activites from this one in the onActivityResult method
     */
    public static final int LAUNCH_QUIZ = 1,
                            LAUNCH_EDITOR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.test();
    }

    public void launchQuiz(View view) {
        Intent intent = new Intent(this, SelectionActivity.class);
        this.startActivityForResult(intent, LAUNCH_QUIZ);
    }

    public void launchEditor(View view) {
        this.startActivityForResult(new Intent(this, SelectionActivity.class), LAUNCH_EDITOR);
    }

    //debug
    public void launchTest(View view) {
        this.startActivity(new Intent(this, TestActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_CANCELED)
            return;
        Intent intent;
        switch (requestCode) {
            case LAUNCH_QUIZ:
                intent = new Intent(MainActivity.this, QuizActivity.class);
                break;
            case LAUNCH_EDITOR:
                intent = new Intent(MainActivity.this, EditActivity.class);
                break;
            default:
                intent = new Intent();
        }
        intent.putExtra("filename", data.getStringExtra("filename"));
        MainActivity.this.startActivity(intent);
    }

    public void test() {
        //Question question = new ShortAnswerQuestion();
        //question.printTest();
    }

}
