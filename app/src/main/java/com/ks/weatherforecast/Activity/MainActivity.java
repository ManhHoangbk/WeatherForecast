package com.ks.weatherforecast.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ks.weatherforecast.ClientData.CityManager;
import com.ks.weatherforecast.ClientData.LocalData;
import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.API;
import com.ks.weatherforecast.share.Config;
import com.ks.weatherforecast.share.model.City;
import com.ks.weatherforecast.share.model.WeatherForecast;
import com.ks.weatherforecast.share.model.userinfo.UserInfo;
import com.ks.weatherforecast.tasks.GenericRequestTask;
import com.ks.weatherforecast.tasks.ParseResult;
import com.ks.weatherforecast.utils.ClientUtils;
import com.ks.weatherforecast.utils.UnitConvertor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
//    https://api.openweathermap.org/data/2.5/weather?q=Ha+noi&lang=vi&mode=json&appid=2a860afe4ee1a7537d3d90e82ceae4d7
//    http://api.openweathermap.org/data/2.5/find?q=thanh%20hoa&type=like&lang=vi&cnt=14&APPID=1487dd8a93bfd85d278d9ac8dcfee94c
    public static Map<String, Integer> speedUnits = null;
    public static Map<String, Integer> pressUnits = null;
    private TextView localName;
    private TextView mIconWeatherView;
    private TextView mTemperatureView;
    private TextView mDescriptionView;
    private TextView mHumidityView;
    private TextView mWindSpeedView;
    private TextView mPressureView;
    private TextView mCloudinessView;
    private TextView mLastUpdateView;
    private TextView mSunriseView;
    private TextView mSunsetView;
    private AppBarLayout mAppBarLayout;

    private TextView mIconWindView;
    private TextView mIconHumidityView;
    private TextView mIconPressureView;
    private TextView mIconCloudinessView;
    private TextView mIconSunriseView;
    private TextView mIconSunsetView;

    private String mSpeedScale;
    private String mIconWind;
    private String mIconHumidity;
    private String mIconPressure;
    private String mIconCloudiness;
    private String mIconSunrise;
    private String mIconSunset;
    private String mPercentSign;
    private String mPressureMeasurement;

    private TextView mainDetailLaster;
    private TextView mainDetailToday;
    private TextView mainDetailTomorow;


    Button btnSearch;
    Typeface weatherFont;
    ViewPager viewPager;
    TabLayout tabLayout;
    private String localString = "";

    View appView;

    private WeatherForecast currentWeatherForecast;
    private Map<String, WeatherForecast> mapsWeathers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        localString = intent.getStringExtra("name");
        if(localString == null){
            localString = "Ha noi";
        }
        initView();
        handel();
    }

    private void initLocalSql(){
        LocalData localData = new LocalData(this);
//        int totalCountry = localData.getCountCountry();
        localData.getCountry();
        localData.getCitiesByCountry("YE");
    }

    private void initView(){
        initMappings();
        weatherConditionsIcons();

        mainDetailLaster = (TextView) findViewById(R.id.main_detail_laster);
        mainDetailToday = (TextView) findViewById(R.id.main_detail_today);
        mainDetailTomorow = (TextView) findViewById(R.id.main_detail_nextday);

        btnSearch = (Button)findViewById(R.id.btnSearch);
        localName = (TextView) findViewById(R.id.localName);
        mTemperatureView = (TextView) findViewById(R.id.main_temperature);
        mDescriptionView = (TextView) findViewById(R.id.todayDescription);
        mPressureView = (TextView) findViewById(R.id.main_pressure);
        mHumidityView = (TextView) findViewById(R.id.main_humidity);
        mWindSpeedView = (TextView) findViewById(R.id.main_wind_speed);
        mCloudinessView = (TextView) findViewById(R.id.main_cloudiness);
        mSunriseView = (TextView) findViewById(R.id.main_sunrise);
        mSunsetView = (TextView) findViewById(R.id.main_sunset);
        mIconWeatherView = (TextView) findViewById(R.id.main_weather_icon);
        mLastUpdateView = (TextView) findViewById(R.id.main_last_update);
//        todayIcon = (TextView) findViewById(R.id.todayIcon);
//        weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");
//        todayIcon.setTypeface(weatherFont);

        initViewIcont();
    }

    private void initViewIcont(){
        Typeface weatherFontIcon = Typeface.createFromAsset(this.getAssets(),
                "fonts/weathericons-regular-webfont.ttf");
        Typeface robotoThin = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Thin.ttf");
        Typeface robotoLight = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Light.ttf");
        mIconWeatherView.setTypeface(weatherFontIcon);
        mTemperatureView.setTypeface(robotoThin);
        mWindSpeedView.setTypeface(robotoLight);
        mHumidityView.setTypeface(robotoLight);
        mPressureView.setTypeface(robotoLight);
        mCloudinessView.setTypeface(robotoLight);
        mSunriseView.setTypeface(robotoLight);
        mSunsetView.setTypeface(robotoLight);

        /**
         * Initialize and configure weather icons
         */
        mIconWindView = (TextView) findViewById(R.id.main_wind_icon);
        mIconWindView.setTypeface(weatherFontIcon);
        mIconWindView.setText(mIconWind);
        mIconHumidityView = (TextView) findViewById(R.id.main_humidity_icon);
        mIconHumidityView.setTypeface(weatherFontIcon);
        mIconHumidityView.setText(mIconHumidity);
        mIconPressureView = (TextView) findViewById(R.id.main_pressure_icon);
        mIconPressureView.setTypeface(weatherFontIcon);
        mIconPressureView.setText(mIconPressure);
        mIconCloudinessView = (TextView) findViewById(R.id.main_cloudiness_icon);
        mIconCloudinessView.setTypeface(weatherFontIcon);
        mIconCloudinessView.setText(mIconCloudiness);
        mIconSunriseView = (TextView) findViewById(R.id.main_sunrise_icon);
        mIconSunriseView.setTypeface(weatherFontIcon);
        mIconSunriseView.setText(mIconSunrise);
        mIconSunsetView = (TextView) findViewById(R.id.main_sunset_icon);
        mIconSunsetView.setTypeface(weatherFontIcon);
        mIconSunsetView.setText(mIconSunset);
    }

    private void handel(){
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });
        mainDetailLaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToDetailActivity(0);
            }
        });

        mainDetailToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToDetailActivity(1);
            }
        });

        mainDetailTomorow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToDetailActivity(2);
            }
        });
    }

    private void getToDetailActivity(int tab){
        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        i.putExtra("tab", tab+"");
        i.putExtra("query", currentWeatherForecast.getName());
        startActivity(i);
    }

    private static void initMappings() {
        if (speedUnits == null){
            speedUnits = new HashMap<>();
            pressUnits = new HashMap<>();

            speedUnits.put("m/s", R.string.speed_unit_mps);
            speedUnits.put("kph", R.string.speed_unit_kph);
            speedUnits.put("mph", R.string.speed_unit_mph);
            speedUnits.put("kn", R.string.speed_unit_kn);

            pressUnits.put("hPa", R.string.pressure_unit_hpa);
            pressUnits.put("kPa", R.string.pressure_unit_kpa);
            pressUnits.put("mm Hg", R.string.pressure_unit_mmhg);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTodayWeatherUI();
        updateDetailWeatherUI();
    }

    private void updateTodayWeatherUI(){
        String location = getCurrentLocation();
        getDataApi(location);
    }

    private void updateDetailWeatherUI(){

    }

    private void weatherConditionsIcons() {
        mIconWind = getString(R.string.icon_wind);
        mIconHumidity = getString(R.string.icon_humidity);
        mIconPressure = getString(R.string.icon_barometer);
        mIconCloudiness = getString(R.string.icon_cloudiness);
        mPercentSign = getString(R.string.percent_sign);
        mPressureMeasurement = getString(R.string.pressure_measurement);
        mIconSunrise = getString(R.string.icon_sunrise);
        mIconSunset = getString(R.string.icon_sunset);
    }

    private void showTodayWeather(){
        if(currentWeatherForecast != null){
            localName.setText(currentWeatherForecast.getName());
            mIconWeatherView.setText( ClientUtils.getStrIcon(MainActivity.this, currentWeatherForecast.getWeather().getIcon()));
            long now = System.currentTimeMillis();
            mLastUpdateView.setText("Cập nhật "+ android.text.format.DateFormat.getTimeFormat(this ).format(now) +":"+android.text.format.DateFormat.getDateFormat(this ).format(now));
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
            if(currentWeatherForecast.getWind() != null){
                if (sp.getString("speedUnit", "m/s").equals("bft")) {
                    mWindSpeedView.setText(getString(R.string.wind) + ": " +
                            UnitConvertor.getBeaufortName((int) currentWeatherForecast.getWind().getSpeed().doubleValue()) +
                            (currentWeatherForecast.getWind().isWindDirectionAvailable() ? " " + UnitConvertor.getWindDirectionString(sp, this, currentWeatherForecast) : ""));
                } else {
                    mWindSpeedView.setText(getString(R.string.wind) + ": " + new DecimalFormat("0.0").format(currentWeatherForecast.getWind().getSpeed()) + " " +
                            ClientUtils.localize(sp, "speedUnit", "m/s", this) +
                            (currentWeatherForecast.getWind().isWindDirectionAvailable() ? " " + UnitConvertor.getWindDirectionString(sp, this, currentWeatherForecast) : ""));
                }
            }
            if(currentWeatherForecast.getWeather() != null){
                if(currentWeatherForecast.getWeather().getDescription() != null){
                    String rainString = "";
                    if(currentWeatherForecast.getRain() != null){
                        mCloudinessView.setText(getString(R.string.rain) +" : "+currentWeatherForecast.getRain().getAmmount()+ " %");
                        if(currentWeatherForecast.getRain().getValue() != null){
                            rainString = UnitConvertor.getRainString(currentWeatherForecast.getRain().getValue(), sp);
                        }
                    }
                    mDescriptionView.setText(currentWeatherForecast.getWeather().getDescription().substring(0, 1).toUpperCase() +
                            currentWeatherForecast.getWeather().getDescription().substring(1) + rainString);
                }else{
                    mDescriptionView.setText("Đang cập nhật");
                }
                double pressure = UnitConvertor.convertPressure(currentWeatherForecast.getWeather().getPressure(), sp);
                mPressureView.setText(getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format( +pressure) + " " +
                        ClientUtils.localize(sp, "pressureUnit", "hPa", this));

                // Temperature
                float temperature = UnitConvertor.convertTemperature(currentWeatherForecast.getWeather().getTemp(), sp);
                mTemperatureView.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                mHumidityView.setText(getString(R.string.humidity) + ": " + currentWeatherForecast.getWeather().getHumidity() + " %");
                mSunriseView.setText(getString(R.string.sunrise) + ": " + timeFormat.format(currentWeatherForecast.getWeather().getSunrise()));
                mSunsetView.setText(getString(R.string.sunset) + ": " + timeFormat.format(currentWeatherForecast.getWeather().getSunset()));
            }
//            todayIcon.setText(todayWeather.getIcon());
        }
    }

    private String getCurrentLocation(){
        //get from text box search
        return localString.isEmpty() ? "Ha noi" : localString;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public void readFileTXT(){
        try {
            CityManager.initCity(getAssets().open("city_list.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getDataApi(String area){
        new GenericRequestTask(this, this, area) {
            @Override
            protected ParseResult parseResponse(String response) {
                currentWeatherForecast = API.getDataFromString(response);
                Log.v("currentWeatherForecast", currentWeatherForecast.getName());
                return ParseResult.OK;
            }

            @Override
            protected String getAPIName() {
                return "weather";
            }

            @Override
            protected void updateMainUI() {
                showTodayWeather();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
