package org.ahvroyal.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.ahvroyal.weatherapp.utilities.SimpleDateConverter;
import org.ahvroyal.weatherapp.utilities.WeatherDateUtils;
import org.ahvroyal.weatherapp.utilities.WeatherUtils;

import java.util.Date;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private final Context context;

    private Cursor cursor;

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean useTodayLayout;

    private final ForecastAdapterOnClickHandler clickHandler;

    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;

        useTodayLayout = this.context.getResources().getBoolean(R.bool.use_today_layout);
    }

    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        cursor.moveToPosition(position);

        int weatherId = cursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;

        int viewType = getItemViewType(position);

        switch (viewType) {

            case VIEW_TYPE_TODAY:
                weatherImageId = WeatherUtils
                        .getLargeArtResourceIdForWeatherCondition(weatherId);
                break;

            case VIEW_TYPE_FUTURE_DAY:
                weatherImageId = WeatherUtils
                        .getSmallArtResourceIdForWeatherCondition(weatherId);
                break;

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        holder.iconView.setImageResource(weatherImageId);

        long timeStamp = cursor.getLong(MainActivity.INDEX_WEATHER_TIME_STAMP);
        Log.i("Time stamp is ---> ", String.valueOf(timeStamp));

        String[] datesToDisplay = SimpleDateConverter.datesToDisplay(timeStamp);

        holder.dateView.setText(datesToDisplay[0]);
        holder.dateDetailsView.setText(datesToDisplay[1]);

        String description = WeatherUtils.getStringForWeatherCondition(context, weatherId);

        String descriptionA11y = context.getString(R.string.a11y_forecast, description);

        holder.descriptionView.setText(description);
        holder.descriptionView.setContentDescription(descriptionA11y);

        double highInCelsius = cursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        
        String highString = WeatherUtils.formatTemperature(context, highInCelsius);

        String highA11y = context.getString(R.string.a11y_high_temp, highString);

        holder.highTempView.setText(highString);
        holder.highTempView.setContentDescription(highA11y);

        double lowInCelsius = cursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String lowString = WeatherUtils.formatTemperature(context, lowInCelsius);
        String lowA11y = context.getString(R.string.a11y_low_temp, lowString);

        holder.lowTempView.setText(lowString);
        holder.lowTempView.setContentDescription(lowA11y);
        
    }

    @Override
    public int getItemCount() {
        if (null == cursor) return 0;
        return cursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (useTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView dateView;
        final TextView dateDetailsView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        final ImageView iconView;

        public ForecastAdapterViewHolder (View view) {
            super(view);

            iconView = view.findViewById(R.id.weather_icon);
            dateView = view.findViewById(R.id.date);
            dateDetailsView = view.findViewById(R.id.dateDetails);
            descriptionView = view.findViewById(R.id.weather_description);
            highTempView = view.findViewById(R.id.high_temperature);
            lowTempView = view.findViewById(R.id.low_temperature);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            long dateInMillis = cursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            clickHandler.onClick(dateInMillis);
        }
        
    }
    
}
