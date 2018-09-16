package com.imbuegen.smartalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.imbuegen.smartalarm.ObjectClasses.AlarmObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //(Internal)Alarm:
    private Calendar alarmTime;
    private PendingIntent mpendingIntent;
    AlarmManager malarmManager;
    protected static ArrayList<AlarmObject> listOfAlarms = new ArrayList<>();
    //(User)Alarm:
    private double amntOfSleep = 0.0167;
    private List<Integer> sessionsList = Arrays.asList(20, 16, 11, 6, 0);   //Take from user
    //UI:
    private FloatingActionButton addAlarmFlrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        setListners();
        for (int i = 0; i < listOfAlarms.size(); i++)
            Log.d("SmartAlarm", listOfAlarms.get(i).getAlarmTime().getTime() + " " + listOfAlarms.get(i).getSnoozeList() + " ");
        //setSmartAlarm();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("SmartAlarm", Integer.toString(Constants.RESULT_CODE_OK) + " " + Constants.ADD_ALALRM_CODE);
        if (requestCode == Constants.ADD_ALALRM_CODE) {
            if (resultCode == Constants.RESULT_CODE_OK) {
                Log.d("SmartAlarm", "OK!");
                //add alarm to list
            }
            else if(resultCode != Constants.RESULT_CODE_OK) {
                Toast.makeText(MainActivity.this, "Alarm cancelled", Toast.LENGTH_LONG).show();
                Log.d("SmartAlarm", "Not OK!");
            }
        }
    }

    private void init() {
        //UI
        addAlarmFlrBtn = findViewById(R.id.fab_alarmAdd);
        //(Internal)Alarm
        malarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar rightNow = Calendar.getInstance();
        Log.d("SmartAlarm", "InitTime: " + rightNow.getTime().toString());
        alarmTime = (Calendar) rightNow.clone();
        alarmTime.add(Calendar.HOUR, (int) amntOfSleep);
        alarmTime.add(Calendar.MINUTE, (int) ((amntOfSleep - (int) amntOfSleep) * 60));
    }

    private void setListners() {
        addAlarmFlrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAlarmIntent = new Intent(MainActivity.this, SetAlarmPage.class);
                startActivityForResult(addAlarmIntent, 1);
            }
        });
    }

    private void setSmartAlarm() {

        Intent mintent = new Intent(MainActivity.this, AlarmReceiver.class);
        for (int i = 0; i < sessionsList.size(); i++) {
            if (i == sessionsList.size() - 1)
                mintent.putExtra("Final?", true);
            else
                mintent.putExtra("Final?", false);
            set(mintent, i, sessionsList.get(i));
        }
        //Log.d("SmartAlarm", "AlarmTime: " + alarmTime.getTime().toString());
    }

    private void set(Intent _mintent, int _flag, int _additionalTime) {
        mpendingIntent = PendingIntent.getBroadcast(this, 0, _mintent, _flag);
        //Log.d("SmartAlarm", "Time in millis: " + String.valueOf((alarmTime.get(Calendar.SECOND) - sum + (long) _additionalTime)));
        malarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis() - (long) _additionalTime * 1000, mpendingIntent);    //For test-*1000, actually-*1000*60.
    }

    public void addAlarm(View view) {
        Intent mintent = new Intent(this, SetAlarmPage.class);
        startActivity(mintent);
    }

    private void cancelAlarm() {
        malarmManager.cancel(mpendingIntent);
    }
}
