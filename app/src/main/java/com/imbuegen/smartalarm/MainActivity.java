package com.imbuegen.smartalarm;

import android.app.AlarmManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.imbuegen.smartalarm.Adapter.MyListAdapter;
import com.imbuegen.smartalarm.ObjectClasses.AlarmObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //(Internal)Alarm:
    private PendingIntent mpendingIntent;
    AlarmManager malarmManager;
    public static ArrayList<AlarmObject> listOfAlarms = new ArrayList<>();
    //UI:
    private ListView alarmListView;
    private FloatingActionButton addAlarmFlrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setListners();
        instantiateAlarmItems();
        int count = 0;
        if (listOfAlarms.size() > 0) {
            for (int i = 0; count < listOfAlarms.size(); i += 5) {
                if (listOfAlarms.get(count).isOn())
                    setSmartAlarm(i, count);
                count++;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        //UI
        alarmListView = findViewById(R.id.list_alarmItem);
        addAlarmFlrBtn = findViewById(R.id.fab_alarmAdd);
        //(Internal)Alarm
        malarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    private void setListners() {
        addAlarmFlrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAlarmIntent = new Intent(MainActivity.this, SetAlarmPage.class);
                addAlarmIntent.putExtra("Edit?", true);
                startActivityForResult(addAlarmIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_ALALRM_CODE) {
            if (resultCode != Constants.RESULT_CODE_OK)
                Toast.makeText(MainActivity.this, "Alarm cancelled", Toast.LENGTH_LONG).show();
            else if (resultCode == Constants.RESULT_CODE_OK) {
                instantiateAlarmItems();
                int count = 0;
                if (listOfAlarms.size() > 0) {
                    for (int i = 0; count < listOfAlarms.size(); i += 5) {
                        if (listOfAlarms.get(count).isOn())
                            setSmartAlarm(i, count);
                        count++;
                    }
                }

            }
        }
    }

    private void setSmartAlarm(int i, int index) {
        Intent mintent = new Intent(MainActivity.this, AlarmReceiver.class);
        mintent.putExtra("Index", index);
        for (int j = 0; j < listOfAlarms.get(index).getSnoozeList().size(); j++) {
            Log.d("SmartAlarm", Integer.toString(i + j) + ": " + "index: " + index);
            if (j == listOfAlarms.get(index).getSnoozeList().size() - 1)
                mintent.putExtra("Final?", true);
            else
                mintent.putExtra("Final?", false);
            set(mintent, j + i, listOfAlarms.get(index).getSnoozeList().get(j), index);
        }
    }

    private void set(Intent _mintent, int _flag, int _priorTime, int index) {
        mpendingIntent = PendingIntent.getBroadcast(this, 0, _mintent, _flag);
        malarmManager.setExact(AlarmManager.RTC_WAKEUP, listOfAlarms.get(index).getAlarmTime().getTimeInMillis() - (long) _priorTime * 1000 * 60, mpendingIntent);    //For test-> *1000, actual-> *1000*60.
    }

    private void instantiateAlarmItems() {
        MyListAdapter listAdapter = new MyListAdapter(MainActivity.this, listOfAlarms);
        alarmListView.setAdapter(listAdapter);
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent addAlarmIntent = new Intent(MainActivity.this, SetAlarmPage.class);
                addAlarmIntent.putExtra("Edit?", true);
                addAlarmIntent.putExtra("Pos", i);
                startActivityForResult(addAlarmIntent, 1);
            }
        });
    }
/*   private void cancelIt(int _index) {
        Intent _mintent = new Intent(MainActivity.this, AlarmReceiver.class);
        int temp = _index, count = 0;

        if (temp != 0) {
            while (temp != 0) {
                temp--;
                count += listOfAlarms.get(temp).getSnoozeList().size();
            }
        }
        for (int i = 0; i < listOfAlarms.get(_index).getSnoozeList().size(); i++) {
            mpendingIntent = PendingIntent.getBroadcast(this, 0, _mintent, count+i);
            malarmManager.cancel(mpendingIntent);
        }
    }*/

}
