package com.kolotseyd.f_studioapp.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

import com.kolotseyd.f_studioapp.MainActivity;

public class BatteryStatusReceiver extends BroadcastReceiver {

    MainActivity mainActivity;

    public BatteryStatusReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mainActivity = new MainActivity();
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        if (level <= 20){
            Toast.makeText(context, "Заряд батереи равен " + level + "%. Зарядите устройство!", Toast.LENGTH_LONG).show();
        }
    }
}
