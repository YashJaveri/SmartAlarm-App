package com.imbuegen.smartalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Internal Alarm related:
    Calendar alarmTime;
    PendingIntent mpendingIntent;
    AlarmManager malarmManager;
    //User Alarm related
    double amntOfSleep = 0.0167;    //For text purpose
    List<Integer> sessionsList = Arrays.asList(0, 6, 11, 16, 20);
    int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar rightNow = Calendar.getInstance();
        Log.d("SmartAlarm", "InitTime: " + rightNow.getTime().toString());
        alarmTime = (Calendar) rightNow.clone();
        alarmTime.add(Calendar.HOUR, (int) amntOfSleep);
        alarmTime.add(Calendar.MINUTE, (int) ((amntOfSleep - (int) amntOfSleep) * 60));

        init();
        fireSmartAlarm();
    }

    private void init() {
        sum = sessionsList.get(sessionsList.size() - 1);

        malarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private void fireSmartAlarm() {

        Intent mintent = new Intent(MainActivity.this, AlarmReceiver.class);
        for (int i = 0; i < sessionsList.size(); i++)
            fireAlarm(mintent, i, sessionsList.get(i));
        Log.d("SmartAlarm", "AlarmTime: " + alarmTime.getTime().toString());
    }

    private void fireAlarm(Intent _mintent, int _flag, int _additionalTime) {
        mpendingIntent = PendingIntent.getBroadcast(this, 0, _mintent, _flag);
        Log.d("SmartAlarm", "Time in millis: " + String.valueOf((alarmTime.getTimeInMillis() - sum*1000 + (long) _additionalTime*1000)/1000));
        malarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis() - sum*1000 + (long) _additionalTime*1000, mpendingIntent);    //For test-*100, actually-*100*60.
    }

    private void cancelAlarm() {
        malarmManager.cancel(mpendingIntent);
    }
}
