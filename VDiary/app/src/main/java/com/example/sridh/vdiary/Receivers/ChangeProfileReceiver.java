package com.example.sridh.vdiary.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by sid on 7/16/17.
 */

public class ChangeProfileReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int changeTo = intent.getIntExtra("profile",manager.getRingerMode());
        manager.setRingerMode(changeTo);
    }
}
