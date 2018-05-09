package com.ks.weatherforecast.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.API;
import com.ks.weatherforecast.share.model.WeatherForecast;
import com.ks.weatherforecast.tasks.GenericRequestTask;
import com.ks.weatherforecast.tasks.ParseResult;
import com.ks.weatherforecast.utils.ClientUtils;
import com.ks.weatherforecast.views.WeatherRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private TextView mainDetailLaster;
    private TextView mainDetailToday;
    private TextView mainDetailTomorow;

    private WeatherRecyclerAdapter weatherRecyclerAdapter;
    private RecyclerView recyclerView = null;

    private List<WeatherForecast> longTermWeather = null;
    private List<WeatherForecast> longTermTodayWeather = null;
    private List<WeatherForecast> longTermTomorrowWeather = null;

    private int tab = 0;
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mainDetailLaster = (TextView) findViewById(R.id.main_detail_laster);
        mainDetailToday = (TextView) findViewById(R.id.main_detail_today);
        mainDetailTomorow = (TextView) findViewById(R.id.main_detail_nextday);

        recyclerView = (RecyclerView) findViewById(R.id.detail_recycler_view);
        Intent intent = getIntent();
        tab  = Integer.parseInt(intent.getStringExtra("tab"));
        query = intent.getStringExtra("query");
        activeTab(tab);
        handel();
    }

    private void handel(){
        mainDetailLaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTab(0);
            }
        });

        mainDetailToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTab(1);
            }
        });

        mainDetailTomorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTab(2);
            }
        });
    }

    private void activeTab(int tab){
        this.tab = tab;
        mainDetailLaster.setTextColor(Color.BLACK);
        mainDetailToday.setTextColor(Color.BLACK);
        mainDetailTomorow.setTextColor(Color.BLACK);
        if(tab == 0){
            mainDetailLaster.setTextColor(Color.RED);
        }else if( tab == 1){
            mainDetailToday.setTextColor(Color.RED);
        }else {
            mainDetailTomorow.setTextColor(Color.RED);
        }
        getData();
    }

    public void getData() {
        if(longTermWeather == null){
            longTermWeather = new ArrayList<>();
            longTermTomorrowWeather = new ArrayList<>();
            longTermTodayWeather = new ArrayList<>();
            new GenericRequestTask(this, DetailActivity.this, query) {
                @Override
                protected ParseResult parseResponse(String response) {
                    return parseLongTermJson(response);
                }

                @Override
                protected String getAPIName() {
                    return "forecast";
                }

                @Override
                protected void updateMainUI() {
                    showData();
                }
            }.execute();
        }else{
            showData();
        }

    }

    private void showData(){
        Log.v("longTermWeather: ", longTermWeather.size() +" xx: "+ longTermTodayWeather.size() +" oo : "+ longTermTomorrowWeather.size());
        if(tab == 0){
            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermWeather);
        }else if( tab == 1){
            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermTodayWeather);
        }else {
            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermTomorrowWeather);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(weatherRecyclerAdapter);
        weatherRecyclerAdapter.notifyDataSetChanged();
    };


    public ParseResult parseLongTermJson(String result) {
        int i;
        try {
            JSONObject reader = new JSONObject(result);

            final String code = reader.optString("cod");
            if ("404".equals(code)) {
                if (longTermWeather == null) {
                    longTermWeather = new ArrayList<>();
                    longTermTodayWeather = new ArrayList<>();
                    longTermTomorrowWeather = new ArrayList<>();
                }
                return ParseResult.CITY_NOT_FOUND;
            }

            longTermWeather = new ArrayList<>();
            longTermTodayWeather = new ArrayList<>();
            longTermTomorrowWeather = new ArrayList<>();

            JSONArray list = reader.getJSONArray("list");
            for (i = 0; i < list.length(); i++) {
                JSONObject listItem = list.getJSONObject(i);

                WeatherForecast weather = API.getDataFromString(listItem.toString());

                final String dateMsString = listItem.getString("dt") + "000";
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(dateMsString));
                weather.getWeather().setIcon(setWeatherIcon((int) weather.getWeather().getId().longValue(), cal.get(Calendar.HOUR_OF_DAY)));

                Calendar today = Calendar.getInstance();
                if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    longTermTodayWeather.add(weather);
                } else if (cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) + 1) {
                    longTermTomorrowWeather.add(weather);
                } else {
                    longTermWeather.add(weather);
                }
            }
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this).edit();
            editor.putString("lastLongterm", result);
            editor.commit();
        } catch (JSONException e) {
            Log.e("JSONException Data", result);
            e.printStackTrace();
            return ParseResult.JSON_EXCEPTION;
        }

        return ParseResult.OK;
    }

    private String setWeatherIcon(int actualId, int hourOfDay) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = this.getString(R.string.weather_sunny);
            } else {
                icon = this.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = this.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = this.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = this.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }
}
