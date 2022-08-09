package org.ahvroyal.weatherapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import org.ahvroyal.weatherapp.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public final class OpenWeatherJsonUtils {

    private static final String OWM_CITY = "city";
    private static final String OWM_COORD = "coord";

    private static final String OWM_LATITUDE = "lat";
    private static final String OWM_LONGITUDE = "lon";

    private static final String OWM_LIST = "list";

    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WINDSPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    private static final String OWM_TEMPERATURE = "temp";

    private static final String OWM_MAX = "temp_max";
    private static final String OWM_MIN = "temp_min";

    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";

    private static final String OWM_MESSAGE_CODE = "cod";

    public static ContentValues[] getWeatherContentValuesFromJson(Context context, String forecastJsonStr) throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray jsonWeatherArray = forecastJson.getJSONArray(OWM_LIST);

        ContentValues[] weatherContentValues = new ContentValues[jsonWeatherArray.length()];

        long normalizedUtcStartDay = WeatherDateUtils.getNormalizedUtcDateForToday();

        for (int i = 0; i < jsonWeatherArray.length(); i++) {

            JSONObject stepForecast = jsonWeatherArray.getJSONObject(i);

            long timeStamp;
            long dateTimeMillis;
            double pressure;
            int humidity;
            double windSpeed;
            double windDirection;
            double high;
            double low;
            int weatherId;

            timeStamp = ((stepForecast.getLong("dt")) * 1000);

            dateTimeMillis = normalizedUtcStartDay + WeatherDateUtils.DAY_IN_MILLIS * i;

            JSONObject main = stepForecast.getJSONObject("main");
            JSONObject wind = stepForecast.getJSONObject("wind");

            pressure = main.getDouble(OWM_PRESSURE);
            humidity = main.getInt(OWM_HUMIDITY);

            windSpeed = wind.getDouble(OWM_WINDSPEED);
            windDirection = wind.getDouble(OWM_WIND_DIRECTION);

            JSONObject weatherObject = stepForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);

            weatherId = weatherObject.getInt(OWM_WEATHER_ID);

            high = main.getDouble(OWM_MAX);
            low = main.getDouble(OWM_MIN);


            ContentValues stepWeatherValues = new ContentValues();
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTimeMillis);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            stepWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_TIME_STAMP, timeStamp);

            weatherContentValues[i] = stepWeatherValues;
        }

        return weatherContentValues;
    }

}