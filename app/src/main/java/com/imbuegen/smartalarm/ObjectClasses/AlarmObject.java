package com.imbuegen.smartalarm.ObjectClasses;

import android.print.PrinterId;
import android.provider.CalendarContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmObject implements Serializable{
    private Calendar alarmTime, current;
    private List<Integer> snoozeList;
    private boolean airplaneIsOn = true;
    private boolean repeatIsOn = false;
    private String alarmTitle = "Wake Up!";
    private boolean isOn = true;
    private int minDuration, hourDuration;

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

    public boolean isAirplaneOn() {
        return airplaneIsOn;
    }

    public void setAirplaneIsOn(boolean airplaneIsOn) {
        this.airplaneIsOn = airplaneIsOn;
    }

    public boolean isRepeatOn() {
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

    public String getAlarmTitle() {
        return alarmTitle;
    }

    public void setAlarmTitle(String alarmTitle) {
        this.alarmTitle = alarmTitle;
    }

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getHourDuration() {
        return hourDuration;
    }

    public void setHourDuration(int hourDuration) {
        this.hourDuration = hourDuration;
    }

    public Calendar getCurrent() {
        return current;
    }

    public void setCurrent(Calendar current) {
        this.current = current;
    }
}
