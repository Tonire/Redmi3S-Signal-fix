package com.redmi3s.tonir.gpsfixredmi3s;

import android.Manifest;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.whispersystems.jobqueue.Job;
import org.whispersystems.jobqueue.JobParameters;

/**
 * Created by tonir on 11/02/2017.
 */

public class SampleJob extends Job {

    public LocationManager locationManager;
    public LocationListener locationListener;
    private Context context;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            checkNetworkStatusAndFixIfNeeded();
            handler.postDelayed(this, 5000);
        }
    };

    private void checkNetworkStatusAndFixIfNeeded(){
        Toast.makeText(context, "Checking...", Toast.LENGTH_SHORT).show();
        if (Connectivity.isConnectedMobile(this.context)) {

            NetworkInfo info = Connectivity.getNetworkInfo(this.context);
            Log.i("NetworkType: ", Integer.toString(info.getSubtype()));
            if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                searchGPS();
            }else{
                removeGPS();
            }
        }
    }
    public void main() {
        handler.post(runnable);
    }

    public void searchGPS(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            // locationManager.removeUpdates(locationListener);
        }
    }
    public void removeGPS(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED){
            locationManager.removeUpdates(locationListener);
        }
    }


    public SampleJob(Context contxt) {
        super(JobParameters.newBuilder().withPersistence().create());
        context = contxt;
    }

    @Override
    public void onAdded() {
        // Called after the Job has been added to the queue.
    }

    @Override
    public void onRun() {
        // Here's where we execute our work.

        Log.w("SampleJob", "Hello, world!");
        main();
    }

    @Override
    public void onCanceled() {
        // This would be called if the job had failed.
    }

    @Override
    public boolean onShouldRetry(Exception exception) {
        // Called if onRun() had thrown an exception to determine whether
        // onRun() should be called again.
        return false;
    }
}
