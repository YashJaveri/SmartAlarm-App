package com.imbuegen.smartalarm;

import android.app.AlarmManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imbuegen.smartalarm.Adapter.MyListAdapter;
import com.imbuegen.smartalarm.ObjectClasses.AlarmObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //(Internal)Alarm:
    private PendingIntent mpendingIntent;
    public AlarmManager malarmManager;
    public static ArrayList<AlarmObject> listOfAlarms = new ArrayList<>();
    AlarmController alarmController;
    MyListAdapter listAdapter;
    //UI:
    private ListView alarmListView;
    private FloatingActionButton addAlarmFlrBtn;
    //Other
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Gson gson;
    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setListners();
        loadData();
        instantiateAlarmItems();
        if (listOfAlarms.size() > 0) {
            for (int i = 0; i < listOfAlarms.size(); i++) {
                if (listOfAlarms.get(i).isOn())
                    alarmController.setSmartAlarm(i);
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
        loadData();
        instantiateAlarmItems();
    }

    private void init() {
        //UI
        alarmListView = findViewById(R.id.list_alarmItem);
        addAlarmFlrBtn = findViewById(R.id.fab_alarmAdd);
        //(Internal)Alarm
        malarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmController = new AlarmController(this, mpendingIntent, malarmManager);
        listAdapter = new MyListAdapter(this, listOfAlarms);
        //Other
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        gson = new Gson();
    }

    private void setListners() {
        addAlarmFlrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAlarmIntent = new Intent(MainActivity.this, SetAlarmPage.class);
                addAlarmIntent.putExtra("Edit?", false);
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
            else {
                instantiateAlarmItems();
                saveData();
                loadData();
                for (int i = 0; i < listOfAlarms.size(); i++)
                    Log.d("Smart Alarm isOn:", Boolean.toString(listOfAlarms.get(i).isOn()));
                if (listOfAlarms.size() > 0) {
                    for (int i = 0; i < listOfAlarms.size(); i++) {
                        if (listOfAlarms.get(i).isOn())
                            alarmController.setSmartAlarm(i);
                        else
                            alarmController.cancelAlarm(i);
                    }
                }

            }
        }
    }

    private void loadData() {
        String stringJSONData = sharedPreferences.getString("Alarm List", null);
        if (stringJSONData != null) {
            Type type = new TypeToken<List<AlarmObject>>() {
            }.getType();
            listOfAlarms = gson.fromJson(stringJSONData, type);
        }
    }

    private void saveData() {
        editor = sharedPreferences.edit();
        String stringJSONData = gson.toJson(listOfAlarms);
        editor.putString("Alarm List", stringJSONData);
        editor.apply();
    }

    private void instantiateAlarmItems() {
        listAdapter = new MyListAdapter(MainActivity.this, listOfAlarms);
        alarmListView.setAdapter(listAdapter);
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mActionMode != null) {
                    listAdapter.toggleSelection(i);
                    onListItemSelect(i);
                } else {
                    Intent addAlarmIntent = new Intent(MainActivity.this, SetAlarmPage.class);
                    addAlarmIntent.putExtra("Edit?", true);
                    addAlarmIntent.putExtra("Pos", i);
                    startActivityForResult(addAlarmIntent, 1);
                }
            }
        });
        alarmListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent,
                                           View view, int position, long id) {
                listAdapter.toggleSelection(position);
                ;
                onListItemSelect(position);
                return false;
            }
        });
    }

    private void onListItemSelect(int position) {
        Log.d("SmartAlarm", "Sel list: " + listAdapter.getSelectedIds());
        if (listAdapter.getSelectedIds().size() > 0 && mActionMode == null)
            mActionMode = startActionMode(new ActionModeCallback());
        else if (listAdapter.getSelectedIds().size() == 0 && mActionMode != null)
            mActionMode.finish();
        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(listAdapter.getSelectedIds().size()) + " selected");
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.btn_delete) {
                ArrayList<Integer> selected = listAdapter.getSelectedIds();
                for (int i = 0; i < selected.size(); i++)
                    alarmController.cancelAlarm(selected.get(i));

                for (int i = 0; i < selected.size(); i++) {
                    listOfAlarms.remove(listOfAlarms.get(selected.get(i)));
                    listAdapter.notifyDataSetChanged();
                    actionMode.finish();
                    instantiateAlarmItems();
                    saveData();
                    loadData();
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            listAdapter.removeSelection();
            mActionMode = null;
        }
    }
}
