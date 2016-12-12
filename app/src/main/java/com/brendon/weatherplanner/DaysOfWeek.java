package com.brendon.weatherplanner;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private static final String FORECAST_TAG = "forecast";

    private String mForecastHigh;
    private String mForecastLow;
    private String mForecastSummary;
    private String mDaySelection;


    private ArrayList<String> mDaysOfWeek;

    private ArrayList<String> mUserListOfDays; // List of days the user can select from.

    private ListView daysOfWeek_LV;
    private TextView mWeatherDisplay;
    private Button mTestButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_of_week);

        daysOfWeek_LV = (ListView) findViewById(R.id.days_selection_list);
        mWeatherDisplay = (TextView) findViewById(R.id.weather_display);
        mTestButton = (Button) findViewById(R.id.test_button);

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

                mDaySelection = daysOfWeek_LV.getItemAtPosition(position).toString();

                // gets the current weather if selection is the current day.
                if (mDaySelection.equalsIgnoreCase(mUserDay)) {

                    if (mWeatherKey != null) {

                        getCurrentTemp();

                    } else {

                        Toast.makeText(DaysOfWeek.this, "Problem reading weather key", Toast.LENGTH_LONG).show();
                    }
                }

                else {

                    if (mWeatherKey != null) {

                        getForecast();

                    } else {

                        Toast.makeText(DaysOfWeek.this, "Problem reading weather key", Toast.LENGTH_LONG).show();
                    }

                }



            }
        });

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(DaysOfWeek.this, YelpResult.class);

                startActivity(intent);

            }
        });

    }

    // Gets the current temp and summary for the User.
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

                String summary = observation.getWeather();

                mWeatherDisplay.setText("The current temp is : " + temp_f + "\n" + "Summary: " + summary);
            }

        }
    }


    // Gets the high, low and summary forecast for User.
    private void getForecast() {

        String tempUrl = "http://api.wunderground.com/api/%s/forecast/q/MN/%s.json"; //TODO pull user state

        String url = String.format(tempUrl, mWeatherKey, mUserCity);

        RequestForecast temp = new RequestForecast();

        temp.execute(url);


    }

    private class RequestForecast extends AsyncTask<String, Void, WeatherForecast.Forecast> {

        @Override
        protected WeatherForecast.Forecast doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream responseStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));

                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }

                String responseString = builder.toString();

                Log.d(FORECAST_TAG, responseString);

                Gson gson = new GsonBuilder().create();

                WeatherForecast forecast = gson.fromJson(responseString, WeatherForecast.class);

                if (forecast.getResponse().getError() != null) {

                    Log.d(FORECAST_TAG, forecast.getResponse().getError().getMessage());
                    return null;

                }

                return forecast.getForecast();


            } catch (Exception e) {

                Log.d(FORECAST_TAG, "Error getting forecast", e);
                return null;

            }
        }

        protected void onPostExecute(WeatherForecast.Forecast forecast) {

            if (forecast != null) {

                WeatherForecast.Simpleforecast forecastText = forecast.getSimpleforecast();

                WeatherForecast.Forecastday[] forecastday = forecastText.getForecastday();


                for (WeatherForecast.Forecastday day : forecastday) {

                    String dayTemp = day.getDate().getWeekday();

                    // If the current day in the loop matches the Users selection.
                    if (dayTemp.equalsIgnoreCase(mDaySelection)) {

                        mForecastHigh = day.getHigh().getFahrenheit();

                        mForecastLow = day.getLow().getFahrenheit();

                        mForecastSummary = day.getConditions();

                        mWeatherDisplay.setText("High: " + mForecastHigh + "\n" +
                        "Low: " + mForecastLow + "\n" + "Summary: " + mForecastSummary);

                        break;

                    }


                }

            }

        }

    }




}


