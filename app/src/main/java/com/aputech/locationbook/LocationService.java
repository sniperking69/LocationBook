package com.aputech.locationbook;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;


public class LocationService extends Service {

    private static final String CHANNAL_ID= "locationBookID";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.aputech.locationbook"+".started_from_notfication";
    private  final IBinder mBinder = new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MIL =10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MUL = UPDATE_INTERVAL_IN_MIL/2;
    private static final int NOTI_ID=4570;
    private boolean mChangeConfigure=false;

    private NotificationManager mNotificationManager;
    private LocationRequest locationReguest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Handler mServiceHandler;
    private LocationCallback locationCallback;
    private Handler  getmServiceHandler;
    private Location location;


    public LocationService (){

    }
    @Override
    public void onCreate() {// this is the function for create service
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread =new HandlerThread("locationBookThread");
        handlerThread.start();
        mServiceHandler=new Handler(handlerThread.getLooper());
        mNotificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel =new NotificationChannel(CHANNAL_ID,
                    getString(R.string.app_name)
            ,NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,false);
        if (startFromNotification){
            removeLocationUpdate();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangeConfigure=true;
    }

    public void removeLocationUpdate() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            Common.setRequestingLocationUpdates(this,false);
            stopSelf();

        }catch (SecurityException ex){
            Common.setRequestingLocationUpdates(this,false);
            Log.e("Locationbook Error", "lost location could not Remove updates :" + ex);
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult() !=null){
                                location =task.getResult();
                            }else {
                                Log.e("LocationBook","Failed to get Last Location ( getLastLocation())");

                            }
                        }
                    });
        }catch (SecurityException ex){
            Log.e("LocationBook","Failed to get Permission ( getLastLocation()) :" +ex);
        }
    }

    private void createLocationRequest() {
         locationReguest =new LocationRequest();
         locationReguest.setInterval(UPDATE_INTERVAL_IN_MIL);
         locationReguest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MUL);
         locationReguest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }

    private void onNewLocation(Location lastLocation) {
        location = lastLocation;
        EventBus.getDefault().postSticky(new SendLocationToActivity(location));
        //Update Notification System
        if (serviceIsRunningInForeGround(this)){
            mNotificationManager.notify(NOTI_ID,getNotification());
        }
            
    }

    private boolean serviceIsRunningInForeGround(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground){
                    return true;
            }
        }
        }
     return false;
    }

    private Notification getNotification() {
        Intent intent = new Intent(this,LocationService.class);
        String text = Common.getLocationText(location);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION,true);
        PendingIntent servicePendingIntent =PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this,0,
                new Intent(this,MainActivity.class),0);
        NotificationCompat.Builder builder =new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_airline_seat_recline_extra_black_24dp,"Launch",activityPendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp,"Remove",servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Common.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());
        //Set the Channel ID for Android
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
builder.setChannelId(CHANNAL_ID);


        }

        return builder.build();
    }

    public void requestLocationUpdates() {
        Common.setRequestingLocationUpdates(this,true);
        startService(new Intent(getApplicationContext(),LocationService.class));
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationReguest,locationCallback, Looper.myLooper());
        }catch (SecurityException ex){
            Log.e("LocationBook", "Lost location permission. Could not request it"+ex);
        }
    }

    public class LocalBinder extends Binder {
        LocationService getService(){ return
        LocationService.this;}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangeConfigure =false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangeConfigure =false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (!mChangeConfigure && Common.RequestingLocationUpdates(this)){
            startForeground(NOTI_ID,getNotification());
        }
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}