package com.brendon.weatherplanner;

import android.content.pm.PackageManager;
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

public class MainActivity extends AppCompatActivity {


    GPSTracker mGPSTracker;

    TextView mUserInstructions;
    Button mUserLocationButton;
    EditText mUserCityEntry;
    Button mSelectCity;
    Button mLaunchWeatherButton;


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

                    Toast.makeText(MainActivity.this, "Your current location is this, Lat: " + latitude +
                            ", Lon: " + longitude, Toast.LENGTH_LONG).show();
                }

            }

        });

        mUserCityEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserCityEntry.setText("");

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
