package org.ahvroyal.weatherapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.ahvroyal.weatherapp.data.WeatherContract;
import org.ahvroyal.weatherapp.utilities.NetworkUtils;
import org.ahvroyal.weatherapp.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class WeatherSyncTask {

    synchronized public static void syncWeather(Context context) {

        try {

            URL weatherRequestUrl = NetworkUtils.getUrl(context);

            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
            Log.i("Response from network :", jsonWeatherResponse);

            ContentValues[] weatherValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            if (weatherValues != null && weatherValues.length != 0) {

                ContentResolver sunshineContentResolver = context.getContentResolver();

                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null);

                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
