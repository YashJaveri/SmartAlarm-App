package com.imbuegen.smartalarm.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.imbuegen.smartalarm.AlarmReceiver;
import com.imbuegen.smartalarm.MainActivity;
import com.imbuegen.smartalarm.ObjectClasses.AlarmObject;
import com.imbuegen.smartalarm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<AlarmObject> listOfAlarms;
    private AlarmObject ao;

    public MyListAdapter(Context _context, ArrayList<AlarmObject> _listOfAlarms) {
        this.context = _context;
        this.listOfAlarms = _listOfAlarms;
    }

    @Override
    public int getCount() {
        return listOfAlarms.size();
    }

    @Override
    public Object getItem(int i) {
        return listOfAlarms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int i1 =i;
        ao = listOfAlarms.get(i);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("ViewHolder") View relativeLayoutItem = (RelativeLayout) inflater.inflate(R.layout.alarm_item, viewGroup, false);

        TextView timeText = relativeLayoutItem.findViewById(R.id.txt_alarmItemTime);
        SwitchCompat alarmState = relativeLayoutItem.findViewById(R.id.switch_alarmItemToggle);
        alarmState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ao.setOn(b);
                MainActivity.listOfAlarms.get(i1).setOn(b);
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");

        timeText.setTypeface(Typer.set(context).getFont(Font.ROBOTO_THIN));
        timeText.setText(sdf.format(ao.getAlarmTime().getTime()));
        alarmState.setChecked(ao.isOn());

        return relativeLayoutItem;
    }
}
