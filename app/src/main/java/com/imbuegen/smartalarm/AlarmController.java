package com.imbuegen.smartalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class AlarmController {

    private Context context;
    private PendingIntent mpendingIntent;
    private AlarmManager malarmManager;

    AlarmController(Context _context, PendingIntent _mpendingIntent, AlarmManager _malarmManger) {
        this.context = _context;
        this.mpendingIntent = _mpendingIntent;
        this.malarmManager = _malarmManger;
    }

    public void setSmartAlarm(int index) {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("listOfAlarms", MainActivity.listOfAlarms);
        Intent mintent = new Intent(context, AlarmReceiver.class);

        mintent.putExtra("Index", index);
        mintent.putExtra("listOfAlarms", mBundle);
        for (int j = 0; j < MainActivity.listOfAlarms.get(index).getSnoozeList().size(); j++) {
            if (j == MainActivity.listOfAlarms.get(index).getSnoozeList().size() - 1)
                mintent.putExtra("Final?", true);
            else
                mintent.putExtra("Final?", false);
            set(mintent, 5 * index + j, MainActivity.listOfAlarms.get(index).getSnoozeList().get(j), index);
        }
    }

    private void set(Intent _mintent, int _flag, int _priorTime, int index) {
        mpendingIntent = PendingIntent.getBroadcast(context, 0, _mintent, _flag);
        if (Calendar.getInstance().getTimeInMillis() < (MainActivity.listOfAlarms.get(index).getAlarmTime().getTimeInMillis() - (long) _priorTime * 1000 * 60)) {
            if (MainActivity.listOfAlarms.get(index).isRepeatOn())
                malarmManager.setRepeating(AlarmManager.RTC_WAKEUP, MainActivity.listOfAlarms.get(index).getAlarmTime().getTimeInMillis() - (long) _priorTime * 1000 * 60, 24 * 60 * 60 * 1000, mpendingIntent);    //For test-> *1000, actual-> *1000*60.
            else
                malarmManager.setExact(AlarmManager.RTC_WAKEUP, MainActivity.listOfAlarms.get(index).getAlarmTime().getTimeInMillis() - (long) _priorTime * 1000 * 60, mpendingIntent);    //For test-> *1000, actual-> *1000*60.
        }
    }

    public void cancelAlarm(int _index) {
        Intent mintent = new Intent(context, AlarmReceiver.class);

        for (int i = 0; i < MainActivity.listOfAlarms.get(_index).getSnoozeList().size(); i++) {
            int temp = 5 * _index + i;
            cancelIt(mintent, temp);
//            Log.d("SmartAlarm", "Cancelling alarm:- " + _index);
        }
    }

    private void cancelIt(Intent _mintent, int _flag) {
        mpendingIntent = PendingIntent.getBroadcast(context, 0, _mintent, _flag);
        malarmManager.cancel(mpendingIntent);
//        Log.d("SmartAlarm", "Cancelled alarm:- " + _flag);
    }
}
