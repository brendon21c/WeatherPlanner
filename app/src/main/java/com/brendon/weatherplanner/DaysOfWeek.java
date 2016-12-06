package com.brendon.weatherplanner;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DaysOfWeek extends AppCompatActivity {

    private String mUserDay;
    private String mUserCity;
    private String mWeatherKey;

    private static final String WEATHER_TAG = "weather";

    private ArrayList<String> mDaysOfWeek;

    private ArrayList<String> mUserListOfDays; // List of days the user can select from.

    private ListView daysOfWeek_LV;
    private TextView mWeatherDisplay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_of_week);

        daysOfWeek_LV = (ListView) findViewById(R.id.days_selection_list);
        mWeatherDisplay = (TextView) findViewById(R.id.weather_display);

        mWeatherKey = WeatherKey.getKeyFromRawResource(this, R.raw.weather_key); // Reads in weather key for API.

        setDaysOfWeek();

        Intent intent = getIntent();

        ArrayList userList = new ArrayList(); // List for the current day and city.

        userList = intent.getParcelableArrayListExtra("user");

        mUserDay = userList.get(0).toString();

        mUserCity = userList.get(1).toString();

        mUserListOfDays = listOfDays(mUserDay);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.listview_layout, R.id.day_of_week_item, mUserListOfDays);

        daysOfWeek_LV.setAdapter(arrayAdapter);

        onClick();


    }


    // This generates a list of 4 days starting at the current day for the User to select from.
    private ArrayList listOfDays(String currentDay) {

        ArrayList listOfDaysForForecast = new ArrayList();

        int startingPosition = 0;

        for (String day : mDaysOfWeek) {

            if (day.equalsIgnoreCase(currentDay)) {

                startingPosition = mDaysOfWeek.indexOf(day);

                for (int x = startingPosition; x <= mDaysOfWeek.size(); x++) {


                    if (x < mDaysOfWeek.size()) {

                        String dayTemp = mDaysOfWeek.get(x);

                        listOfDaysForForecast.add(dayTemp);

                    } if (x == mDaysOfWeek.size()) {

                        x = 0;

                        String dayTemp2 = mDaysOfWeek.get(x);

                        listOfDaysForForecast.add(dayTemp2);

                    }

                    if (listOfDaysForForecast.size() == 4) {

                        break;
                    }

                }

            }

        }

        return listOfDaysForForecast;

    }

    private void setDaysOfWeek() {

        mDaysOfWeek = new ArrayList<String>();

        mDaysOfWeek.add("Sunday");
        mDaysOfWeek.add("Monday");
        mDaysOfWeek.add("Tuesday");
        mDaysOfWeek.add("Wednesday");
        mDaysOfWeek.add("Thursday");
        mDaysOfWeek.add("Friday");
        mDaysOfWeek.add("Saturday");

    }

    private void onClick() {


        daysOfWeek_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectionDay = daysOfWeek_LV.getItemAtPosition(position).toString();

                // gets the current weather if selection is the current day.
                if (selectionDay.equalsIgnoreCase(mUserDay)) {

                    if (mWeatherKey != null) {

                        getCurrentTemp();

                    } else {

                        Toast.makeText(DaysOfWeek.this, "Problem reading weather key", Toast.LENGTH_LONG).show();
                    }
                }

                else {

                    Toast.makeText(DaysOfWeek.this, "Need to build class for forecast", Toast.LENGTH_SHORT).show();
                }



            }
        });

    }

    private void getCurrentTemp() {

        String tempUrl = "http://api.wunderground.com/api/%s/conditions/q/MN/%s.json"; //TODO pull user state

        String url = String.format(tempUrl, mWeatherKey, mUserCity);

        RequestCurrentTemp temp = new RequestCurrentTemp();

        temp.execute(url);


    }

    // Required Async class to request information in background from WeatherConditions Class.
    private class RequestCurrentTemp extends AsyncTask<String, Void, WeatherConditions.Current_observation> {

        @Override
        protected WeatherConditions.Current_observation doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream responseStream = connection.getInputStream();

                //Wrap in InputStreamReader, and then wrap that in a BufferedReader to read line-by-line
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));

                // Read stream into String. Use StringBuilder to put multiple lines together.
                // Read lines in a loop until the end of the stream.
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }

                //and turn the StringBuilder into a String.
                String responseString = builder.toString();

                Log.d(WEATHER_TAG, responseString);

                // And then parse this String into GSON objects.
                // Whatever is returned from this method will be delivered
                // to the onPostExecute method. onPostExecute method is called automatically.

                Gson gson = new GsonBuilder().create();
                WeatherConditions conditions = gson.fromJson(responseString, WeatherConditions.class);

                //Does the response have an Error attribute? If so, log the error message and return null
                if (conditions.getResponse().getError() != null) {
                    Log.d(WEATHER_TAG, conditions.getResponse().getError().getMessage());
                    return null;
                }

                //Otherwise, no error. Seems we have a current observation, which will contain the current temp.
                return conditions.getCurrent_observation();


            } catch (Exception e) {

                Log.d(WEATHER_TAG, "Error getting temp", e);
                return null;

            }

        }


        protected void onPostExecute(WeatherConditions.Current_observation observation) {

            if (observation != null) {

                String temp_f = observation.getTemp_f();

                mWeatherDisplay.setText("The current temp is : " + temp_f);
            }

        }
    }



}


