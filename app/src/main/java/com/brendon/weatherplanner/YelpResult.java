package com.brendon.weatherplanner;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


// This is where the Users selected activity is sent to yelp.
// Yelp uses a POST and GET method to interact and I don't know how to do this.
// I have read the documentation and can't seem to figure it out.
public class YelpResult extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private String mYelpAppID;
    private String mYelpAppSecret;

    private GoogleApiClient mGoogleApiClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_result);

        yelpKeysSetup();

        getToken();



        /*
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        int PLACE_PICKER_REQUEST = 1;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {

            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);


        } catch (Exception e) {

            e.printStackTrace();
        }
        */







    }

    private void yelpKeysSetup() {

        WeatherKey weatherKey = new WeatherKey();
        weatherKey.getYelpFromRawResource(this, R.raw.yelp_keys);
        mYelpAppID = weatherKey.getYelpAppID();
        mYelpAppSecret = weatherKey.getYelpAppSecret();


    }


    private void getToken() {

        String tempUrl = "https://api.yelp.com/oauth2/token";

        RequestToken temp = new RequestToken();

        temp.execute(tempUrl);

    }


    // Make a POST request to get a token from Yelp.
    private class RequestToken extends AsyncTask<String, Void, YelpAPI.Token> {


        @Override
        protected YelpAPI.Token doInBackground(String... urls) {


            try {


                URL url = new URL(urls[0]);


                try {

                   HttpURLConnection client = (HttpURLConnection) url.openConnection();

                    client.setRequestMethod("POST");
                    client.setRequestProperty("client_id", mYelpAppID);
                    client.setRequestProperty("client_secret", mYelpAppSecret);
                    //client.setDoOutput(true);

                    /*
                    OutputStream outputStream = new BufferedOutputStream(client.getOutputStream());
                    outputStream.flush();
                    outputStream.close();
                    */

                    InputStream responseStream = client.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));

                    StringBuilder builder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }

                    String responseString = builder.toString();

                    Gson gson = new GsonBuilder().create();

                    YelpAPI token = gson.fromJson(responseString, YelpAPI.class);



                } catch (Exception e) {

                    Log.d("TAG", "error with POST" + e);

                    return null;


                }


            } catch (Exception e) {

                e.printStackTrace();
            }


            return null;

        }

        protected void onPostExecute(YelpAPI.Token token) {

            if (token != null) {

                String accessToken = token.getAccess_token();

            }

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("TAG", connectionResult.toString());

    }
}
