package com.imbuegen.smartalarm.ObjectClasses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmObject {
    private Calendar alarmTime;
    private List<Integer> snoozeList;
    private boolean airplaneIsOn = true;
    private boolean repeatIsOn = false;
    private boolean isOn = true;

    public Calendar getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Calendar alarmTime) {
        this.alarmTime = alarmTime;
    }

    public List<Integer> getSnoozeList() {
        return snoozeList;
    }

    public void setSnoozeList(List<Integer> snoozeList) {
        this.snoozeList = snoozeList;
    }

    public boolean isAirplaneIsOn() {
        return airplaneIsOn;
    }

    public void setAirplaneIsOn(boolean airplaneIsOn) {
        this.airplaneIsOn = airplaneIsOn;
    }

    public boolean isRepeatIsOn() {
        return repeatIsOn;
    }

    public void setRepeatIsOn(boolean repeatIsOn) {
        this.repeatIsOn = repeatIsOn;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
