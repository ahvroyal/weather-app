package org.ahvroyal.weatherapp.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.ahvroyal.weatherapp.data.WeatherAppPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

    private static final String STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";

    private static final String OPEN_WEATHER_MAP_FORECAST5 =
            "https://api.openweathermap.org/data/2.5/forecast";

    private static final String FORECAST_BASE_URL = OPEN_WEATHER_MAP_FORECAST5;

    private static final String format = "json";
    private static final String units = "metric";
    private static final int numEntries = 40;

    private static final String API_KEY = "very secret one !";

    final static String QUERY_PARAM = "q";
//    final static String LAT_PARAM = "lat";
//    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";
    final static String API_KEY_PARAM = "appid";


    public static URL getUrl(Context context) {
        String locationQuery = WeatherAppPreferences.getPreferredWeatherLocation(context);
        return buildUrlWithLocationQuery(locationQuery);
    }


    private static URL buildUrlWithLocationQuery(String locationQuery) {
        Uri weatherQueryUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numEntries))
                .build();

        try {
            URL weatherQueryUrl = new URL(weatherQueryUri.toString());
            Log.i(TAG, "URL is --> " + weatherQueryUrl);
            return weatherQueryUrl;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
