package com.dev.hari.trackme.interfaces;

import com.dev.hari.trackme.utility.TinyDB;

/**
 * Created by hariom.gupta on 5/10/2018.
 */

public interface MyConstants {

    //ALL_PREFS
    TinyDB db = TinyDB.getInstance();
    String PREF_TRACKER_APP = "PREF_TRACKER_APP";
    String PREF_EMAIL = "PREF_EMAIL";
    String PREF_PASSWORD = "PREF_PASSWORD";
    String PREF_MOBILE_NUMBER = "PREF_MOBILE_NUMBER";

    //ALL_VARIABLES
    String LOCATION_TIME = "time";
    String POWER = "power";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String ALTITUDE = "altitude";
    String BEARING = "bearing";
    String SPEED = "speed";
    String PROVIDER = "provider";
    String ACCURACY = "accuracy";
    String ELAPSED_REAL_TIME_NANOS = "elapsedRealtimeNanos";
    String BEARING_ACCURACY_DEGREES = "getBearingAccuracyDegrees";
    String SPEED_ACCURACY_METER_PER_SECOND = "getSpeedAccuracyMetersPerSecond";
    String VERTICAL_ACCURACY_METERS = "getVerticalAccuracyMeters";
}
