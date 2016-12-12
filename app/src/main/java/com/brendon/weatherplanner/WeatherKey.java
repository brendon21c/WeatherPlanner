package com.brendon.weatherplanner;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WeatherKey {


    private String mYelpAppID;
    private String mYelpAppSecret;

    //Returns a key, or null if file not found or can't be read
    protected static String getKeyFromRawResource(Context context, int rawResource) {

        //Create a stream reader for this raw resource
        InputStream keyStream = context.getResources().openRawResource(rawResource);
        //And a BufferedReader to read file into lines of text
        BufferedReader keyStreamReader = new BufferedReader(new InputStreamReader(keyStream));
        try {
            //And read one line of text
            String key = keyStreamReader.readLine();
            return key;
        } catch (IOException ioe) {
            return null;
        }


    }


    protected void getYelpFromRawResource(Context context, int rawResource) {

        //Create a stream reader for this raw resource
        InputStream keyStream = context.getResources().openRawResource(rawResource);
        //And a BufferedReader to read file into lines of text
        BufferedReader keyStreamReader = new BufferedReader(new InputStreamReader(keyStream));

        ArrayList values = new ArrayList();

        try {

            for (int x = 0; x < 2; x++) {

                //And read one line of text
                String key = keyStreamReader.readLine();

                values.add(key);

            }


            for (int x = 0; x < values.size(); x++) {

                if (x == 0) {

                    setYelpAppID(values.get(x).toString());


                } else {

                    setYelpAppSecret(values.get(x).toString());

                }

            }


        } catch (IOException ioe) {

            Log.d("TAG", "error getting yelp keys " + ioe);

        }


    }

    public String getYelpAppID() {
        return mYelpAppID;
    }

    public void setYelpAppID(String yelpAppID) {
        mYelpAppID = yelpAppID;
    }

    public String getYelpAppSecret() {
        return mYelpAppSecret;
    }

    public void setYelpAppSecret(String yelpAppSecret) {
        mYelpAppSecret = yelpAppSecret;
    }
}
