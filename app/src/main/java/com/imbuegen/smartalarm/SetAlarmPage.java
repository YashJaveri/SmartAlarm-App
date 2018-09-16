package com.imbuegen.smartalarm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.imbuegen.smartalarm.ObjectClasses.AlarmObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SetAlarmPage extends AppCompatActivity {

    //UI
    NumberPicker hoursNP, minNP, np1, np2, np3, np4;
    TextView durationView;
    SwitchCompat airplane, repeat;
    //Alarm
    private Calendar alarmTime;
    private List<Integer> snoozeList = Arrays.asList(20, 14, 9, 4, 0);
    private Set<Integer> snoozeSet;
    boolean airplaneIsOn = true;
    boolean repeatIsOn = false;
    private AlarmObject alarmObject;
    //Other
    int minDuration = 30, hourDuration = 7;
    String durationText = "7 hour(s) & 30 min from now";
    Calendar rightNow;
    final String[] minList = {"00", "10", "20", "30", "40", "50"};

    @Override
    protected void onStart() {
        super.onStart();

        setContentView(R.layout.set_alarm);
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle top-right button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_save) {
            alarmTime = Calendar.getInstance();
            hourDuration += alarmTime.get(Calendar.HOUR);
            alarmTime.set(Calendar.HOUR, hourDuration);
            minDuration += alarmTime.get(Calendar.MINUTE);
            alarmTime.set(Calendar.MINUTE, minDuration);
            alarmObject.setAlarmTime(alarmTime);

            snoozeSet.clear();
            snoozeSet = new LinkedHashSet<>(snoozeList);
            snoozeList.clear();
            snoozeList.addAll(snoozeSet);   //Remove repeats
            alarmObject.setSnoozeList(snoozeList);

            alarmObject.setOn(true);
            alarmObject.setRepeatIsOn(repeatIsOn);
            alarmObject.setAirplaneIsOn(airplaneIsOn);
            MainActivity.listOfAlarms.add(alarmObject);
            for (int i = 0; i < MainActivity.listOfAlarms.size() - 2; i++) {
                if (MainActivity.listOfAlarms.get(i).getAlarmTime() == alarmTime) {
                    MainActivity.listOfAlarms.remove(alarmObject);
                    break;
                }
            }

            Intent mainActIntent = new Intent(this, MainActivity.class);
            setResult(Constants.RESULT_CODE_OK, mainActIntent);
            startActivity(mainActIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        //Alarm
        snoozeList = new ArrayList<>();
        snoozeList.add(20);
        snoozeList.add(14);
        snoozeList.add(9);
        snoozeList.add(4);
        snoozeSet = new LinkedHashSet<>(snoozeList);
        alarmObject = new AlarmObject();
        //Other
        rightNow = Calendar.getInstance();

        //UI
        hoursNP = findViewById(R.id.numPicker_hours);
        minNP = findViewById(R.id.numPicker_minutes);
        np1 = findViewById(R.id.numPicker_pik1);
        np2 = findViewById(R.id.numPicker_pik2);
        np3 = findViewById(R.id.numPicker_pik3);
        np4 = findViewById(R.id.numPicker_pik4);
        durationView = findViewById(R.id.txt_hourDetails);
        durationView.setText(durationText);
        airplane = findViewById(R.id.switch_airplaneMode);
        repeat = findViewById(R.id.switch_repeat);

        hoursNP.setMinValue(1);
        hoursNP.setMaxValue(12);
        minNP.setMinValue(0);
        minNP.setMaxValue(minList.length - 1);
        minNP.setDisplayedValues(minList);
        np1.setMinValue(0);
        np1.setMaxValue(30);
        np2.setMinValue(0);
        np2.setMaxValue(30);
        np3.setMinValue(0);
        np3.setMaxValue(30);
        np4.setMinValue(0);
        np4.setMaxValue(30);
        //Changing text color of the number picker
        setNumberPickerTextColor(np1);
        setNumberPickerTextColor(np2);
        setNumberPickerTextColor(np3);
        setNumberPickerTextColor(np4);
        setNumberPickerTextColor(hoursNP);
        setNumberPickerTextColor(minNP);
        //Disable soft keyboard response
        hoursNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //set def values(In my opinion)
        hoursNP.setValue(7);
        minNP.setValue(3);
        np1.setValue(20);
        np2.setValue(14);
        np3.setValue(9);
        np4.setValue(4);
    }

    private void setListeners() {
        //Number Pickers:-
        hoursNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                hourDuration = i1;
                durationText = Integer.toString(hourDuration) + " hour(s) & " + Integer.toString(minDuration) + "0 min from now";
                durationView.setText(durationText);
            }
        });

        minNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                minDuration = i1;
                durationText = Integer.toString(hourDuration) + " hour(s) & " + Integer.toString(minDuration) + "0 min from now";
                durationView.setText(durationText);
            }
        });

        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                snoozeList.set(0, i1);
            }
        });

        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                snoozeList.set(1, i1);
            }
        });

        np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                snoozeList.set(2, i1);
            }
        });

        np4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                snoozeList.set(3, i1);
            }
        });
        //Switches:-
        airplane.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                airplaneIsOn = b;
            }
        });
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                repeatIsOn = b;
            }
        });
    }

    public void setNumberPickerTextColor(NumberPicker numberPicker) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(getResources().getColor(R.color.mCustomWhite));
                    ((EditText) child).setTextColor(getResources().getColor(R.color.mCustomWhite));
                    numberPicker.invalidate();
                } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                    Log.w("SmartAlarmError", e);
                }
            }
        }
    }
}
