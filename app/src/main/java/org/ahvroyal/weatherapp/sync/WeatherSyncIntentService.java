package org.ahvroyal.weatherapp.sync;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class WeatherSyncIntentService extends IntentService {

    public WeatherSyncIntentService(String name) {
        super(name);
    }

    public WeatherSyncIntentService() {
        super("WeatherSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        WeatherSyncTask.syncWeather(this);
    }
}
