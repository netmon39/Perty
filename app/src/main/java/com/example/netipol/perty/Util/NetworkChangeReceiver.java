package com.example.netipol.perty.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by netipol on 17/4/2018 AD.
 */

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            int status = NetworkUtil.getConnectivityStatusString(context);
            Log.e("network reciever", "Sulod sa network reciever");
            if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if(status==NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
                    //Toast.makeText(context.getApplicationContext(), "Network Lost",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(context.getApplicationContext(), "Connected to mobile",Toast.LENGTH_SHORT).show();

            }
        }
    }

