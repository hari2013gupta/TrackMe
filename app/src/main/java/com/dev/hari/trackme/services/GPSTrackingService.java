package com.dev.hari.trackme.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.dev.hari.trackme.BuildConfig;
import com.dev.hari.trackme.R;
import com.dev.hari.trackme.interfaces.MyConstants;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GPSTrackingService extends Service implements MyConstants {
    private static final String TAG = GPSTrackingService.class.getSimpleName();
    private static final int CONFIG_CACHE_EXPIRY = 600;  // 10 minutes.
    private LinkedList<Map<String, Object>> mTransportStatuses = new LinkedList<>();
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private DatabaseReference ref;
    String transportId = "Mr. Manjunath";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        buildNotification();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        loginToFirebase();
    }

//Create the persistent notification//

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

//Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.tracking_enabled);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            unregisterReceiver(stopReceiver);

//Stop the Service//

            stopSelf();
        }
    };

    private void loginToFirebase() {

//Authenticate with Firebase, using the email and password we created earlier//

        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);

//Call OnCompleteListener if the user is signed in successfully//


        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

//If the user has been authenticated...//

                if (task.isSuccessful()) {

                    fetchRemoteConfig();
//...then call requestLocationUpdates//
                    try {
                        loadPreviousStatuses();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

//If sign in fails, then log the error//

                    Log.d(TAG, "Firebase authentication failed");
                }
            }
        });
    }

    /**
     * Loads previously stored statuses from Firebase, and once retrieved,
     * start location tracking.
     */
    private void loadPreviousStatuses() {
        FirebaseAnalytics.getInstance(this).setUserProperty("transportID", transportId);
        String path = getString(R.string.firebase_path) + transportId;
        ref = FirebaseDatabase.getInstance().getReference(path);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    try {
                        for (DataSnapshot transportStatus : snapshot.getChildren()) {
                            mTransportStatuses.add(Integer.parseInt(transportStatus.getKey()),
                                    (Map<String, Object>) transportStatus.getValue());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                requestLocationUpdates();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // TODO: Handle gracefully
            }
        });
    }

    //Initiate the request to track the device's location//
    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();

//Specify how often your app should request the device’s location//

        request.setInterval(10000);

//Get the most accurate location data available//

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path) + transportId;
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

//If the app currently has access to the location permission...//

        if (permission == PackageManager.PERMISSION_GRANTED) {

//...then request location updates//

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    fetchRemoteConfig();
//Get a reference to the database, so your app can perform read and write operations//

                    ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

//Save the location data to the database//

                        long hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        int startupSeconds = (int) (mFirebaseRemoteConfig.getDouble("SLEEP_HOURS_DURATION") * 3600);
                        if (hour == mFirebaseRemoteConfig.getLong("SLEEP_HOUR_OF_DAY")) {
                            shutdownAndScheduleStartup(startupSeconds);
                            return;
                        }

                        Map<String, Object> transportStatus = new HashMap<>();
                        transportStatus.put(ACCURACY, location.getAccuracy());
                        transportStatus.put(ALTITUDE, location.getAltitude());
                        transportStatus.put(BEARING, location.getBearing());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            transportStatus.put(ELAPSED_REAL_TIME_NANOS, location.getElapsedRealtimeNanos());
                        }
                        transportStatus.put(LATITUDE, location.getLatitude());
                        transportStatus.put(LONGITUDE, location.getLongitude());
                        transportStatus.put(PROVIDER, location.getProvider());
                        transportStatus.put(SPEED, location.getSpeed());
                        transportStatus.put(LOCATION_TIME, location.getTime());
                        transportStatus.put(POWER, getBatteryLevel());

                        if (locationIsAtStatus(location, 1) && locationIsAtStatus(location, 0)) {
                            // If the most recent two statuses are approximately at the same
                            // location as the new current location, rather than adding the new
                            // location, we update the latest status with the current. Two statuses
                            // are kept when the locations are the same, the earlier representing
                            // the time the location was arrived at, and the latest representing the
                            // current time.
                            mTransportStatuses.set(0, transportStatus);
                            // Only need to update 0th status, so we can save bandwidth.
                            ref.child("0").setValue(transportStatus);
                        } else {
                            // Maintain a fixed number of previous statuses.
                            while (mTransportStatuses.size() >= mFirebaseRemoteConfig.getLong("MAX_STATUSES")) {
                                mTransportStatuses.removeLast();
                            }
                            mTransportStatuses.addFirst(transportStatus);
                            // We push the entire list at once since each key/index changes, to
                            // minimize network requests.
                            ref.setValue(mTransportStatuses);
                        }
                    }
                }
            }, null);
        }
    }

    private void fetchRemoteConfig() {
        long cacheExpiration = CONFIG_CACHE_EXPIRY;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "Remote config fetched");
                mFirebaseRemoteConfig.activateFetched();
            }
        });
    }

    private void shutdownAndScheduleStartup(int when) {
        Log.i(TAG, "overnight shutdown, seconds to startup: " + when);
        com.google.android.gms.gcm.Task task = new OneoffTask.Builder()
                .setService(TrackerTaskService.class)
                .setExecutionWindow(when, when + 60)
                .setUpdateCurrent(true)
                .setTag(TrackerTaskService.TAG)
                .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_ANY)
                .setRequiresCharging(false)
                .build();
        GcmNetworkManager.getInstance(this).schedule(task);
        stopSelf();
    }

    /**
     * Determines if the current location is approximately the same as the location
     * for a particular status. Used to check if we'll add a new status, or
     * update the most recent status of we're stationary.
     */
    private boolean locationIsAtStatus(Location location, int statusIndex) {
        if (mTransportStatuses.size() <= statusIndex) {
            return false;
        }
        Map<String, Object> status = mTransportStatuses.get(statusIndex);
        Location locationForStatus = new Location("");
        locationForStatus.setLatitude((double) status.get(LATITUDE));
        locationForStatus.setLongitude((double) status.get(LONGITUDE));
        float distance = location.distanceTo(locationForStatus);
        Log.d(TAG, String.format("Distance from status %s is %sm", statusIndex, distance));
        return distance < mFirebaseRemoteConfig.getLong("LOCATION_MIN_DISTANCE_CHANGED");
    }

    private float getBatteryLevel() {
        Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int batteryLevel = -1;
        int batteryScale = 1;
        if (batteryStatus != null) {
            batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel);
            batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale);
        }
        return batteryLevel / (float) batteryScale * 100;
    }

}
