package com.imbuegen.smartalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent snoozeActivityIntent = new Intent(context, SnoozePageActivity.class);

        if (Objects.requireNonNull(intent.getExtras()).getBoolean("Final?"))
            snoozeActivityIntent.putExtra("Final?", true);
        else
            snoozeActivityIntent.putExtra("Final?", false);

        context.startActivity(snoozeActivityIntent);
    }
}
