package org.ahvroyal.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.ahvroyal.weatherapp.data.WeatherContract;
import org.ahvroyal.weatherapp.databinding.ActivityDetailBinding;
import org.ahvroyal.weatherapp.utilities.SimpleDateConverter;
import org.ahvroyal.weatherapp.utilities.WeatherUtils;

import java.util.Arrays;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = "#WeatherApp";

    private Uri mUri;

    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_TIME_STAMP
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;
    public static final int INDEX_WEATHER_TIME_STAMP = 8;

    private static final int ID_DETAIL_LOADER = 121;

    private String forecastSummary;

    private ActivityDetailBinding detailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mUri = getIntent().getData();

        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        LoaderManager.getInstance(this).initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(DetailActivity.this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(forecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {
        switch (loaderId) {

            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        int weatherImageId = WeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
        detailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);

        long timeStamp = data.getLong(INDEX_WEATHER_TIME_STAMP);
        String[] datesToDisplay = SimpleDateConverter.datesToDisplay(timeStamp);
        detailBinding.primaryInfo.date.setText(datesToDisplay[0]);
        detailBinding.primaryInfo.dateDetails.setText(datesToDisplay[1]);

        String description = WeatherUtils.getStringForWeatherCondition(this, weatherId);
        String descriptionA11y = getString(R.string.a11y_forecast, description);
        detailBinding.primaryInfo.weatherDescription.setText(description);
        detailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);
        detailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionA11y);

        double highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highString = WeatherUtils.formatTemperature(this, highInCelsius);
        String highA11y = getString(R.string.a11y_high_temp, highString);
        detailBinding.primaryInfo.highTemperature.setText(highString);
        detailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);

        double lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = WeatherUtils.formatTemperature(this, lowInCelsius);
        String lowA11y = getString(R.string.a11y_low_temp, lowString);
        detailBinding.primaryInfo.lowTemperature.setText(lowString);
        detailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);

        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityA11y = getString(R.string.a11y_humidity, humidityString);
        detailBinding.extraDetails.humidity.setText(humidityString);
        detailBinding.extraDetails.humidity.setContentDescription(humidityA11y);
        detailBinding.extraDetails.humidityLabel.setContentDescription(humidityA11y);

        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = WeatherUtils.getFormattedWind(this, windSpeed, windDirection);
        String windA11y = getString(R.string.a11y_wind, windString);
        detailBinding.extraDetails.windMeasurement.setText(windString);
        detailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);
        detailBinding.extraDetails.windLabel.setContentDescription(windA11y);

        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);

        String pressureString = getString(R.string.format_pressure, pressure);
        String pressureA11y = getString(R.string.a11y_pressure, pressureString);
        detailBinding.extraDetails.pressure.setText(pressureString);
        detailBinding.extraDetails.pressure.setContentDescription(pressureA11y);
        detailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);

        forecastSummary = String.format("%s - %s - %s/%s", Arrays.toString(datesToDisplay), description, highString, lowString);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}