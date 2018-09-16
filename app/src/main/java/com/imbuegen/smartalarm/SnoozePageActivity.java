package com.imbuegen.smartalarm;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SnoozePageActivity extends AppCompatActivity {
    Timer timer;
    MediaPlayer alarm1, alarmFinal;
    Button stopBtn;
    TextView time, message;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.alarm_snooze_page);
    }
    @Override
    protected void onResume() {
        super.onResume();

        init();
        Log.d("SmartAlarm", "2. Text View Id:- " + R.id.txt_time);
        if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("Final?"))   //Start playing alarm
            alarmFinal.start();
        else
            alarm1.start();

        //To stop after playing 3 min:
        //--> Schedule timer of 3 min
        timer.schedule(new MyTimerTask(), 150*1000);
    }

    private void init() {
        timer = new Timer();
        alarm1 = MediaPlayer.create(this, R.raw.alarm_clock);
        alarmFinal = MediaPlayer.create(this, R.raw.alarm_clock_bell_sound_effect);
        alarm1.setLooping(true);
        alarmFinal.setLooping(true);

        time = findViewById(R.id.txt_time);
        message = findViewById(R.id.txt_message);
        stopBtn = findViewById(R.id.btn_Stop);
        if (time == null)
            Log.d("SmartAlarm", "null!");
        time.setTypeface(Typer.set(SnoozePageActivity.this).getFont(Font.ROBOTO_THIN));
        message.setTypeface(Typer.set(SnoozePageActivity.this).getFont(Font.ROBOTO_REGULAR));
        Log.d("SmartAlarm", "1. Text View Id:- " + R.id.txt_message);
    }

    public void stopPlayingAlarm(View view) {
        if (alarm1.isPlaying()) {
            alarm1.reset();
            alarm1.stop();
        } else if (alarmFinal.isPlaying()) {
            alarmFinal.reset();
            alarmFinal.stop();
        }

        finish();
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            //Stop playing
            if (alarm1 != null && alarm1.isPlaying()) {
                alarm1.reset();
                alarm1.stop();
            } else if (alarmFinal != null && alarmFinal.isPlaying()) {
                alarmFinal.reset();
                alarmFinal.stop();
            }
            //Finish activity:
            if(!isDestroyed())
                finish();

            timer.cancel();
        }
    }
}
