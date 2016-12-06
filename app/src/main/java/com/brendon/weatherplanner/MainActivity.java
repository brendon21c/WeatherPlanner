package com.brendon.weatherplanner;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    GPSTracker mGPSTracker;
    Geocoder mGeocoder;

    TextView mUserInstructions;
    Button mUserLocationButton;
    EditText mUserCityEntry;
    Button mSelectCity;
    Button mLaunchWeatherButton;

    private String mUserCityLocation;
    private String mDayOfWeek;

    private static final String LOCATION_TAG = "location";


    int REQUEST_LOCATION_PERMISSION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserLocationButton = (Button) findViewById(R.id.get_location_button);
        mUserInstructions = (TextView) findViewById(R.id.instructions_TV);
        mUserCityEntry = (EditText) findViewById(R.id.city_entry_field);
        mSelectCity = (Button) findViewById(R.id.city_selection_button);
        mLaunchWeatherButton = (Button) findViewById(R.id.main_window_next_button);


        checkUserPermission();

        setup();

        mGeocoder = new Geocoder(this, Locale.getDefault()); // Geocoder to get user's current city.



    }

    public void checkUserPermission() {

        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        // If user has granted App permission to locate them.
        if (permission == PackageManager.PERMISSION_GRANTED){

            mGPSTracker = new GPSTracker(MainActivity.this);

        } else { // Request permission.

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);

        }

    }


    public void setup() {

        mUserLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mGPSTracker.CanGetLocation()) {

                    double latitude = mGPSTracker.getLatitude();
                    double longitude = mGPSTracker.getLongitude();

                    try {

                        List<Address> locationList = mGeocoder.getFromLocation(latitude,longitude,1);

                        mUserCityLocation = locationList.get(0).getLocality();

                        mUserCityEntry.setText(mUserCityLocation);

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                }

            }

        });

        mSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String city = mUserCityEntry.getText().toString().trim();

                mUserCityLocation = city;

            }
        });

        mUserCityEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserCityEntry.setText("");

            }
        });


        mLaunchWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar currentDay = Calendar.getInstance();

                int day = currentDay.get(Calendar.DAY_OF_WEEK);

                // Sets the day of the week.
                switch (day) {

                    case Calendar.SUNDAY:

                        mDayOfWeek = "sunday";
                        break;

                    case Calendar.MONDAY:

                        mDayOfWeek = "monday";
                        break;


                    case Calendar.TUESDAY:

                        mDayOfWeek = "tuesday";
                        break;

                    case Calendar.WEDNESDAY:

                        mDayOfWeek = "wednesday";
                        break;


                    case Calendar.THURSDAY:

                        mDayOfWeek = "thursday";
                        break;


                    case Calendar.FRIDAY:

                        mDayOfWeek = "friday";
                        break;


                    case Calendar.SATURDAY:

                        mDayOfWeek = "saturday";
                        break;



                }

                // Adds the users location and day of the week to be passed to next activity.
                ArrayList userList = new ArrayList();

                userList.add(mDayOfWeek);
                userList.add(mUserCityLocation);


                Intent intent = new Intent(MainActivity.this, DaysOfWeek.class);
                intent.putParcelableArrayListExtra("user", userList);

                startActivity(intent);

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Starts a new GPS tracker when permission is granted.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mGPSTracker = new GPSTracker(MainActivity.this);
            }
        }    }
}
