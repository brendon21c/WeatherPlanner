package com.brendon.weatherplanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


// This is where the Users selected activity is sent to yelp.
// Yelp uses a POST and GET method to interact and I don't know how to do this.
// I have read the documentation and can't seem to figure it out.
public class YelpResult extends AppCompatActivity {

    private String mYelpAppID;
    private String mYelpAppSecret;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_result);

        yelpKeysSetup();

        makePostRequest();



    }

    private void yelpKeysSetup() {

        WeatherKey weatherKey = new WeatherKey();
        weatherKey.getYelpFromRawResource(this, R.raw.yelp_keys);
        mYelpAppID = weatherKey.getYelpAppID();
        mYelpAppSecret = weatherKey.getYelpAppSecret();


    }


    // Make a POST request to get a token from Yelp.
    private void makePostRequest() {

        try {


            URL url = new URL("https://api.yelp.com/oauth2/token");

            HttpURLConnection client = null;


            try {

                client = (HttpURLConnection) url.openConnection();

                client.setRequestMethod("POST");
                client.setRequestProperty("client_id", mYelpAppID);
                client.setRequestProperty("client_secret", mYelpAppSecret);
                client.setDoOutput(true);

                OutputStream outputStream = new BufferedOutputStream(client.getOutputStream());
                outputStream.flush();
                outputStream.close();


            } catch (Exception e) {

                Log.d("TAG", "error with POST" + e);


            } finally {

                if (client != null) {

                    client.disconnect();

                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
