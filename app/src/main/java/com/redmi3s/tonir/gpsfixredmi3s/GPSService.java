package com.redmi3s.tonir.gpsfixredmi3s;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;

/**
 * Created by tonir on 06/02/2017.
 */

public class GPSService extends Service {
    public LocationManager locationManager;
    public LocationListener locationListener;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkNetworkStatusAndFixIfNeeded();
            handler.postDelayed(this, 5000);
        }
    };

    private Handler handler = new Handler();


    private final IBinder mBinder = new LocalBinder();

    public GPSService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        locationListener = new GPSService.MyLocationListener();

        AlarmManager almgr = (AlarmManager)getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        Intent intento = new Intent(this,GPSService.class);
        intento.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intento, 0);

        Notification.Builder builder = new Notification.Builder(GPSService.this);
        builder.setSmallIcon(R.drawable. ic_stat_name).setContentTitle("Service running")
                .setContentIntent(pendingIntent);

        Notification notification = builder.getNotification();
        startForeground(1337, notification);

        //PendingIntent pendingOffLoadIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, timerIntent, 0);

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }
    public void main() {
        handler.post(runnable);
    }

    public void searchGPS(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
           // locationManager.removeUpdates(locationListener);
        }
    }
    public void removeGPS(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED){
            locationManager.removeUpdates(locationListener);
        }
    }

    private void checkNetworkStatusAndFixIfNeeded(){
        if (Connectivity.isConnectedMobile(this.getApplicationContext())) {

            NetworkInfo info = Connectivity.getNetworkInfo(this.getApplicationContext());
            Log.i("NetworkType: ", Integer.toString(info.getSubtype()));
            if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                searchGPS();
            }else{
                removeGPS();
            }
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public class LocalBinder extends Binder {
        GPSService getService() {
            return GPSService.this;
        }
    }

}
