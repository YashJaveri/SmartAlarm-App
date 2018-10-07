package com.imbuegen.smartalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent snoozeActivityIntent = new Intent(context, SnoozePageActivity.class);
        snoozeActivityIntent.putExtra("Index", intent.getIntExtra("Index", 0));

        if (Objects.requireNonNull(intent.getExtras()).getBoolean("Final?"))
            snoozeActivityIntent.putExtra("Final?", true);
        else
            snoozeActivityIntent.putExtra("Final?", false);

        context.startActivity(snoozeActivityIntent);
    }
}
