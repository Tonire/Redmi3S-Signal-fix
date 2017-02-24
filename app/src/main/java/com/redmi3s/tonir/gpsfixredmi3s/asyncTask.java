package com.redmi3s.tonir.gpsfixredmi3s;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.TimerTask;

/**
 * Created by tonir on 06/02/2017.
 */

public class asyncTask extends TimerTask {
    private Context context;
    private GPSService mBoundService;
    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((GPSService.LocalBinder)service).getService();
            mBoundService.searchGPS();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;

        }
    };

    public asyncTask(Context contxt){
        context = contxt;
    }

    @Override
    public void run(){
        checkNetworkStatusAndFixIfNeeded();
    }

    private void checkNetworkStatusAndFixIfNeeded(){
        while (Connectivity.isConnectedMobile(context.getApplicationContext())) {

            NetworkInfo info = Connectivity.getNetworkInfo(context.getApplicationContext());
            Log.i("NetworkType: ", Integer.toString(info.getSubtype()));
            if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                fixGPS();
            }else{
                removeGPS();
            }
        }
    }

    private void fixGPS(){
        if(mBoundService == null){
            context.bindService(new Intent(context, GPSService.class), mConnection, context.BIND_AUTO_CREATE);
        }else{
            mBoundService.searchGPS();
        }
    }
    private void removeGPS(){
        if(mBoundService == null){
            context.bindService(new Intent(context, GPSService.class), mConnection, context.BIND_AUTO_CREATE);
        }else{
            mBoundService.removeGPS();
        }
    }
}

