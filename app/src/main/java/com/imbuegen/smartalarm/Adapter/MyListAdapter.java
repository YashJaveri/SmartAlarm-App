package com.imbuegen.smartalarm.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.imbuegen.smartalarm.AlarmController;
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
    private int i1 = 0;
    private   ArrayList<Integer> mSelectedItemsIds;

    public MyListAdapter(Context _context, ArrayList<AlarmObject> _listOfAlarms) {
        this.context = _context;
        this.listOfAlarms = _listOfAlarms;
        mSelectedItemsIds = new ArrayList<>();
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
        i1 = i;
        ao = listOfAlarms.get(i);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("ViewHolder") View relativeLayoutItem = inflater.inflate(R.layout.alarm_item, viewGroup, false);

        TextView timeText = relativeLayoutItem.findViewById(R.id.txt_alarmItemTime);
        SwitchCompat alarmState = relativeLayoutItem.findViewById(R.id.switch_alarmItemToggle);
//        Log.d("Smart Alarm:", Boolean.toString( MainActivity.listOfAlarms.get(i1).isOn()));
        alarmState.setChecked(ao.isOn());
        alarmState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ao.setOn(b);
                MainActivity.listOfAlarms.get(i1).setOn(b);
                notifyDataSetChanged();
//                Log.d("Smart Alarm id on:", Boolean.toString(MainActivity.listOfAlarms.get(i1).isOn()));
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");

        timeText.setTypeface(Typer.set(context).getFont(Font.ROBOTO_THIN));
        timeText.setText(sdf.format(ao.getAlarmTime().getTime()));

        RelativeLayout rel = (RelativeLayout) relativeLayoutItem;

        if(mSelectedItemsIds.contains(i))
        rel.setBackgroundResource(R.color.colorPrimary);

        return relativeLayoutItem;
    }

    public void toggleSelection(int position)
    {
        selectView(position, !mSelectedItemsIds.contains(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value)
    {
        if(value)
            mSelectedItemsIds.add(position);
        else
            mSelectedItemsIds.remove(new Integer(position));

        notifyDataSetChanged();
    }

    public ArrayList<Integer> getSelectedIds() {
        return mSelectedItemsIds;
    }
}
