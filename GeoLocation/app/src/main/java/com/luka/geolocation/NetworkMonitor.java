package com.luka.geolocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class NetworkMonitor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            Toast.makeText(context, "Wi fi enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Wi fi disabled", Toast.LENGTH_SHORT).show();
        }

    }
}
