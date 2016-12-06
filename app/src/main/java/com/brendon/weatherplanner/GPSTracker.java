package com.brendon.weatherplanner;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/*
This class allows the Users location (Lat, Lon) to be pulled.
I found a tutorial online for how to do this: http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
 */
public class GPSTracker extends Service implements LocationListener {


    private Context mContext;

    // GPS Status
    boolean mGPSEnabled = false;

    // Network Status
    boolean mNetworkEnabled = false;

    // Location status
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    // Min distance for update changes in meters.
    private static final long MIN_DISTANCE_FOR_UPDATES = 10;

    // Min time between updates in minutes.
    private static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60; // 1 min

    protected LocationManager mLocationManager;


    // Declares a location Manager
    public GPSTracker(Context context) {

        this.mContext = context;
        getLocation();

    }


    public Location getLocation() {

        try {

            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Gets GPS status.
            mGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Gets network status.
            mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!mGPSEnabled && !mNetworkEnabled) {

                //no network provider enabled.

            } else {

                this.canGetLocation = true;

                int permission = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION);

                // Makes sure the device has the proper permission.
                if (permission == PackageManager.PERMISSION_GRANTED) {


                    if (mNetworkEnabled) {

                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_FOR_UPDATES, this);

                        if (mLocationManager != null) {

                            location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {

                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                            }

                        }
                    }


                    if (mGPSEnabled) {

                        if (location == null) {

                            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES,
                                    MIN_DISTANCE_FOR_UPDATES, this);

                            if (mLocationManager != null) {

                                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {

                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                }

                            }
                        }

                    }


                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return location;

    }

    public double getLatitude() {

        if (location != null) {

            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude() {

        if (location != null) {

            longitude = location.getLongitude();

        }

        return longitude;
    }

    public boolean CanGetLocation() {
        return this.canGetLocation;
    }

    // Required overrides.
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
