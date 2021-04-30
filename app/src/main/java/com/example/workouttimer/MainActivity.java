package com.example.workouttimer;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Chronometer chronometer;
    TextView show_value;

    EditText userData;
    ImageView start;
    ImageView pause;
    ImageView save;

    private long savedTime;
    private boolean running=false;

    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String Total_Time = "text";
    public static final String WorkOutType = "text2";

    private String value = "00:00";
    private String workout_type = "push ups";

    private long old_value;
    private long new_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chronometer = findViewById(R.id.chronometer);
        start = findViewById(R.id.startimg);
        pause = findViewById(R.id.pauseimg);
        save = findViewById(R.id.saveimg);
        userData = findViewById(R.id.userdata);
        show_value = findViewById(R.id.textView);

        savedTime = 0;
        start.setOnClickListener(new StartClockOnClickListner());
        pause.setOnClickListener(new PauseClockOnClickListner());
        save.setOnClickListener(new SaveClockOnClickListner());

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if(sharedPreferences.contains(Total_Time)){
            loadData();
            show_value.setText("You spent "+ value + " on " + workout_type+" last time");
            // + work_out type
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chronometer.stop();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedTime = savedInstanceState.getLong("study_time");
        running = savedInstanceState.getBoolean("running");

        if (running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - savedTime);
            chronometer.start();
            pausePressed = false;
        }
        chronometer.setBase(SystemClock.elapsedRealtime() - savedTime);
    }
    Boolean pausePressed = false;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!pausePressed)
            savedTime = SystemClock.elapsedRealtime()-chronometer.getBase();
        outState.putLong("study_time", savedTime);
        outState.putBoolean("running", running);
    }

    private class StartClockOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!running){
                chronometer.setBase(SystemClock.elapsedRealtime() - savedTime);
                chronometer.start();

                running = true;
                pausePressed = false;
            }
        }
    }

    private class PauseClockOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(running){
                chronometer.stop();
                savedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
                running = false;
                pausePressed = true;
            }
        }
    }

    private class SaveClockOnClickListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            chronometer.stop();

            value = chronometer.getText().toString();
            workout_type = userData.getText().toString();

            //SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();


            editor.putString(Total_Time, value);
            editor.putString(WorkOutType, workout_type);
            //Coi ki lai
            editor.putLong("Value", savedTime);

            editor.commit();
        }
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        value = sharedPreferences.getString(Total_Time, "00:00");
        workout_type = sharedPreferences.getString(WorkOutType,"push ups");

        new_value = sharedPreferences.getLong("Value", 0);
    }
}
