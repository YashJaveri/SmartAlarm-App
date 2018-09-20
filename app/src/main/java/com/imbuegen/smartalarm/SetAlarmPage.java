package com.imbuegen.smartalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toolbar;

import com.imbuegen.smartalarm.ObjectClasses.AlarmObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SetAlarmPage extends AppCompatActivity {

    //UI
    NumberPicker hoursNP, minNP, np1, np2, np3, np4;
    TextView durationView;
    SwitchCompat airplane, repeat;
    EditText alarmTitleEditTxt;
    Toolbar toolBar;
    //Alarm
    private Calendar alarmTime;
    private List<Integer> snoozeList = Arrays.asList(20, 14, 9, 4, 0);
    private Set<Integer> snoozeSet;
    boolean airplaneIsOn = true;
    boolean repeatIsOn = false;
    private AlarmObject alarmObject;
    private String alarmTitle = "Wake Up!";
    PendingIntent mpendingIntent;
    //Other
    int minDuration = 30, hourDuration = 7;
    String durationText = "7 hour(s) & 15 min from now";
    Calendar rightNow;
    final String[] minList = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};

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
        if (item.getItemId() == R.id.btn_save) {
            snoozeList.set(0, np1.getValue());
            snoozeList.set(1, np2.getValue());
            snoozeList.set(2, np3.getValue());
            snoozeList.set(3, np4.getValue());
            //:---------------If edit state::----------------
            if (getIntent().getBooleanExtra("Edit?", false)) {
                AlarmObject _am = MainActivity.listOfAlarms.get(getIntent().getIntExtra("Pos", 0));
                alarmTime = _am.getCurrent();
                hourDuration += alarmTime.get(Calendar.HOUR);   //get current hour
                minDuration += alarmTime.get(Calendar.MINUTE);  //get current minute
                alarmTime.set(Calendar.HOUR, hourDuration);     //add hour
                alarmTime.set(Calendar.MINUTE, minDuration);    //add minute
                alarmObject.setAlarmTime(alarmTime);            //SET ALARM TIME

            } else {   //:---------------If add state::----------------
                alarmTime = Calendar.getInstance();
                hourDuration += alarmTime.get(Calendar.HOUR);   //get current hour
                minDuration += alarmTime.get(Calendar.MINUTE);  //get current minute
                alarmTime.set(Calendar.HOUR, hourDuration);     //add hour
                alarmTime.set(Calendar.MINUTE, minDuration);    //add minute
                alarmObject.setAlarmTime(alarmTime);            //SET ALARM TIME
            }

            alarmObject.setCurrent(Calendar.getInstance());
            alarmObject.setHourDuration(hoursNP.getValue());
            alarmObject.setMinDuration(Integer.parseInt(minList[minNP.getValue()]));
            //Remove repeats
            snoozeSet.clear();
            snoozeSet = new LinkedHashSet<>(snoozeList);
            snoozeList.clear();
            snoozeList.addAll(snoozeSet);
            Collections.sort(snoozeList, Collections.<Integer>reverseOrder());
            alarmObject.setSnoozeList(snoozeList);

            //Other params:
            alarmObject.setOn(true);
            alarmObject.setRepeatIsOn(repeatIsOn);
            alarmObject.setAirplaneIsOn(airplaneIsOn);
            if (alarmTitleEditTxt.getText() == null)
                alarmObject.setAlarmTitle("");
            else
                alarmObject.setAlarmTitle(alarmTitle);

            //Modify main act list:
            if (getIntent().getBooleanExtra("Edit?", false))
                MainActivity.listOfAlarms.set(getIntent().getIntExtra("Pos", 0), alarmObject);
            else
                MainActivity.listOfAlarms.add(alarmObject);

            //To remove similar:-
            for (int i = 0; i < MainActivity.listOfAlarms.size() - 2; i++) {
                if (MainActivity.listOfAlarms.get(i).getAlarmTime().get(Calendar.HOUR) == alarmTime.get(Calendar.HOUR)
                        && MainActivity.listOfAlarms.get(i).getAlarmTime().get(Calendar.MINUTE) == alarmTime.get(Calendar.MINUTE)
                        && MainActivity.listOfAlarms.get(i) != alarmObject)
                    MainActivity.listOfAlarms.remove(alarmObject);
            }

            Intent mainActIntent = new Intent();
            setResult(Constants.RESULT_CODE_OK, mainActIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to discard this alarm?")
                .setPositiveButton(Html.fromHtml("<font color='#202020'>Yes</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mainActIntent = new Intent();
                        setResult(-1, mainActIntent);
                        finish();
                    }
                })
                .setNegativeButton(Html.fromHtml("<font color='#202020'>No</font>"), null)
                .show();
    }

    private void init() {
        //Alarm
        snoozeList = new ArrayList<>();
        //:---------------If edit state::----------------
        if (getIntent().getBooleanExtra("Edit?", false)) {
            snoozeList = new ArrayList<>();
            snoozeList = MainActivity.listOfAlarms.get(getIntent().getIntExtra("Pos", 0)).getSnoozeList();
        } else {    //:---------------If add state::----------------
            snoozeList.add(20);
            snoozeList.add(14);
            snoozeList.add(9);
            snoozeList.add(4);
            snoozeList.add(0);
        }
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
        alarmTitleEditTxt = findViewById(R.id.editTxt_alarmTitle);
        durationView = findViewById(R.id.txt_hourDetails);
        durationView.setText(durationText);
        airplane = findViewById(R.id.switch_airplaneMode);
        repeat = findViewById(R.id.switch_repeat);
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

        //UI
        //:---------------If edit state:----------------
        if (getIntent().getBooleanExtra("Edit?", true)) {
            AlarmObject _am = MainActivity.listOfAlarms.get(getIntent().getIntExtra("Pos", 0));
            alarmTitle = _am.getAlarmTitle();
            alarmTitleEditTxt.setText(alarmTitle);

            minDuration = _am.getMinDuration();
            hourDuration = _am.getHourDuration();
            if (_am.getHourDuration() == 0)
                editNumbPickersVals(true, minDuration, false);    //Makes nps  0
            else
                editNumbPickersVals(false, 55, true);

            if (snoozeList.size() < 5) {
                for (int i = snoozeList.size(); i < 5; i++) {
                    snoozeList.add(0);
                }
            }
            List<NumberPicker> numPickList = Arrays.asList(np1, np2, np3, np4);
            for (int i = 0; i < snoozeList.size() - 1; i++) {
                numPickList.get(i).setValue(snoozeList.get(i));
            }
            for (int i = 0; i < minList.length; i++) {
                if (minList[i].equals(Integer.toString(minDuration))) {     //find index using int type of minDuration
                    minNP.setValue(i);     //Set val of minute's numb picker
                    break;
                }
            }
            hoursNP.setValue(hourDuration);     //Set val of hour's numb picker

            //Duration Text:
            String string;
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            string = sdf.format(_am.getCurrent().getTime());
            durationText = Integer.toString(hourDuration) + " hour(s) & " + Integer.toString(minDuration) + " min from " + string;
            durationView.setText(durationText);
            //Other toggles:
            airplaneIsOn = _am.isAirplaneOn();
            repeatIsOn = _am.isRepeatOn();
            airplane.setChecked(airplaneIsOn);
            repeat.setChecked(repeatIsOn);
        } else {         //:---------------If add state::----------------
            editNumbPickersVals(false, 55, true);
            airplane.setChecked(airplaneIsOn);
            repeat.setChecked(repeatIsOn);
        }
    }

    private void setListeners() {
        //Number Pickers:-
        hoursNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                hourDuration = i1;
                minDuration = Integer.parseInt(minList[minNP.getValue()]);

                //:---------------If edit state::----------------
                String string;
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                if (getIntent().getBooleanExtra("Edit?", false)) {
                    AlarmObject _am = MainActivity.listOfAlarms.get(getIntent().getIntExtra("Pos", 0));
                    string = sdf.format(_am.getCurrent().getTime());
                } else
                    string = "now";
                //------------------------------------------------
                durationText = Integer.toString(hourDuration) + " hour(s) & " + Integer.toString(minDuration) + " min from " + string;
                durationView.setText(durationText);

                if (hourDuration == 0) {
                    editNumbPickersVals(true, minDuration - 1, false);
                } else if (i == 0 && i1 != 0) {
                    hoursNP.setMinValue(0);
                    hoursNP.setMaxValue(12);
                    minNP.setMinValue(0);
                    minNP.setMaxValue(minList.length - 1);
                    minNP.setDisplayedValues(minList);
                    editNumbPickersVals(false, 55, false);
                } else if (minDuration == 0 && hourDuration == 0)
                    editNumbPickersVals(true, 0, false);
            }
        });

        minNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                minDuration = Integer.parseInt(minList[i1]);
                hourDuration = hoursNP.getValue();

                //:---------------If edit state::----------------
                String string;
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                if (getIntent().getBooleanExtra("Edit?", false)) {
                    AlarmObject _am = MainActivity.listOfAlarms.get(getIntent().getIntExtra("Pos", 0));
                    string = sdf.format(_am.getAlarmTime().getTime());
                } else
                    string = "now";
                //------------------------------------------------
                durationText = Integer.toString(hourDuration) + " hour(s) & " + Integer.toString(minDuration) + " min from " + string;
                durationView.setText(durationText);

                if (hourDuration == 0 && minDuration != 0) {
                    editNumbPickersVals(true, minDuration - 1, false);
                } else if (minDuration == 0 && hourDuration == 0)
                    editNumbPickersVals(true, 0, false);

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
        alarmTitleEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (alarmTitleEditTxt.getText() != null)
                    alarmTitle = alarmTitleEditTxt.getText().toString();
            }
        });
    }

    private void editNumbPickersVals(Boolean isHoursZero, int max, Boolean setToDef) {

        hoursNP.setMinValue(0);
        hoursNP.setMaxValue(12);
        minNP.setMinValue(0);
        minNP.setMaxValue(minList.length - 1);
        minNP.setDisplayedValues(minList);
        np1.setMinValue(0);
        np1.setMaxValue(max);
        np2.setMinValue(0);
        np2.setMaxValue(max);
        np3.setMinValue(0);
        np3.setMaxValue(max);
        np4.setMinValue(0);
        np4.setMaxValue(max);

        if (isHoursZero) {
            np1.setValue(0);
            np2.setValue(0);
            np3.setValue(0);
            np4.setValue(0);
        } else if (setToDef) {
            //set def values(In my opinion)
            hoursNP.setValue(7);
            minNP.setValue(3);
            np1.setValue(20);
            np2.setValue(14);
            np3.setValue(9);
            np4.setValue(4);
        }
    }

    private void setNumberPickerTextColor(NumberPicker numberPicker) {
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
