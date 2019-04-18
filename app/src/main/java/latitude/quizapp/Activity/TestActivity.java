package latitude.quizapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import latitude.quizapp.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void onRadioButtonClicked(View view) {
        String str = "null";
        if(findViewById(R.id.radioButtonA) == view) {
            str = "Radio Button A";
        } else if(findViewById(R.id.radioButtonB) == view) {
            str = "Radio Button B";
        } else if(findViewById(R.id.radioButtonC) == view) {
            str = "Radio Button C";
        }
        Log.d("debug", str + " was clicked");
    }

}
