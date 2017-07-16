package com.example.sridh.vdiary.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import static com.example.sridh.vdiary.Utils.prefs.AUDIO_PROFILE;
import static com.example.sridh.vdiary.Utils.prefs.get;

/**
 * Created by sid on 7/16/17.
 */

public class ChangeProfileReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int changeTo = intent.getIntExtra("profile",get(context,AUDIO_PROFILE,AudioManager.RINGER_MODE_VIBRATE));
        manager.setRingerMode(changeTo);
        Log.d("Quiet Hours",String.valueOf(changeTo));
    }
}
