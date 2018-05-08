package com.ks.weatherforecast.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.model.WeatherForecast;
import com.ks.weatherforecast.share.model.WeatherViewHolder;
import com.ks.weatherforecast.utils.ClientUtils;
import com.ks.weatherforecast.utils.UnitConvertor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherViewHolder> {
    private List<WeatherForecast> itemList;
    private Context context;

    public WeatherRecyclerAdapter(Context context, List<WeatherForecast> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_item, null);

        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder customViewHolder, int i) {
        WeatherForecast weatherItem = itemList.get(i);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // Temperature
        float temperature = UnitConvertor.convertTemperature(weatherItem.getWeather().getTemp(), sp);
        if (sp.getBoolean("temperatureInteger", false)) {
            temperature = Math.round(temperature);
        }

        // Rain
        double rain = weatherItem.getRain() != null ? weatherItem.getRain().getAmmount() : 0;
        String rainString = UnitConvertor.getRainString(rain, sp);

        // Wind
        double wind;
        try {
            wind = weatherItem.getWind() != null ? weatherItem.getWind().getSpeed() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            wind = 0;
        }
        wind = UnitConvertor.convertWind(wind, sp);

        // Pressure
        double pressure = UnitConvertor.convertPressure(weatherItem.getWeather().getPressure(), sp);

        TimeZone tz = TimeZone.getDefault();
        String defaultDateFormat = context.getResources().getStringArray(R.array.dateFormatsValues)[0];
        String dateFormat = sp.getString("dateFormat", defaultDateFormat);
        if ("custom".equals(dateFormat)) {
            dateFormat = sp.getString("dateFormatCustom", defaultDateFormat);
        }
        String dateString;
        try {
            Log.v("dateFormat: ", dateFormat+"");
            SimpleDateFormat resultFormat = new SimpleDateFormat(dateFormat);
            resultFormat.setTimeZone(tz);
            Log.v("resultFormat", weatherItem.getDate()+"");
            dateString = resultFormat.format(weatherItem.getDate());
        } catch (IllegalArgumentException e) {
            dateString = context.getResources().getString(R.string.error_dateFormat);
        }

        if (sp.getBoolean("differentiateDaysByTint", false)) {
            Date now = new Date();
            /* Unfortunately, the getColor() that takes a theme (the next commented line) is Android 6.0 only, so we have to do it manually
             * customViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.attr.colorTintedBackground, context.getTheme())); */
            int color;
            if (weatherItem.getNumDaysFrom(now) > 1) {
                TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.colorTintedBackground, R.attr.colorBackground});
                if (weatherItem.getNumDaysFrom(now) % 2 == 1) {
                    color = ta.getColor(0, context.getResources().getColor(R.color.colorTintedBackground));
                } else {
                    /* We must explicitly set things back, because RecyclerView seems to reuse views and
                     * without restoring back the "normal" color, just about everything gets tinted if we
                     * scroll a couple of times! */
                    color = ta.getColor(1, context.getResources().getColor(R.color.colorBackground));
                }
                ta.recycle();
                customViewHolder.itemView.setBackgroundColor(color);
            }
        }

        customViewHolder.itemDate.setText(dateString);
        if (sp.getBoolean("displayDecimalZeroes", false)) {
            customViewHolder.itemTemperature.setText(new DecimalFormat("0.0").format(temperature) + " " + sp.getString("unit", "°C"));
        } else {
            customViewHolder.itemTemperature.setText(new DecimalFormat("#.#").format(temperature) + " " + sp.getString("unit", "°C"));
        }
        customViewHolder.itemDescription.setText(weatherItem.getWeather().getDescription().substring(0, 1).toUpperCase() +
                weatherItem.getWeather().getDescription().substring(1) + rainString);
        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
        customViewHolder.itemIcon.setTypeface(weatherFont);
        customViewHolder.itemIcon.setText(weatherItem.getWeather().getIcon());
        if (sp.getString("speedUnit", "m/s").equals("bft")) {
            customViewHolder.itemyWind.setText(context.getString(R.string.wind) + ": " +
                    UnitConvertor.getBeaufortName((int) wind) + " " + UnitConvertor.getWindDirectionString(sp, context, weatherItem));
        } else {
            customViewHolder.itemyWind.setText(context.getString(R.string.wind) + ": " + new DecimalFormat("0.0").format(wind) + " " +
                    ClientUtils.localize(sp, context, "speedUnit", "m/s")
                    + " " + UnitConvertor.getWindDirectionString(sp, context, weatherItem));
        }
        customViewHolder.itemPressure.setText(context.getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format(pressure) + " " +
                ClientUtils.localize(sp, context, "pressureUnit", "hPa"));
        customViewHolder.itemHumidity.setText(context.getString(R.string.humidity) + ": " + weatherItem.getWeather().getHumidity() + " %");
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }
}
