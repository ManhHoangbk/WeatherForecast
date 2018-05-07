package com.ks.weatherforecast.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    public static Map<String, Integer> speedUnits = null;
    public static Map<String, Integer> pressUnits = null;
    TextView todayTemperature;
    TextView todayDescription;
    TextView todayWind;
    TextView todayPressure;
    TextView todayHumidity;
    TextView todaySunrise;
    TextView todaySunset;
    TextView lastUpdate;
    TextView todayIcon;
    Typeface weatherFont;
    ViewPager viewPager;
    TabLayout tabLayout;

    View appView;

    private WeatherForecast currentWeatherForecast;
    private Map<String, WeatherForecast> mapsWeathers = new HashMap<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        readFileTXT();

    }

    private void initLocalSql(){
        LocalData localData = new LocalData(this);
//        int totalCountry = localData.getCountCountry();
        int totalCity = localData.getCountCity();
        Log.v("total: ", "country: "+ 0 +" city: "+ totalCity);
//        localData.insertData(localData.getCityData());
    }

    private void initView(){
        initMappings();
        progressDialog = new ProgressDialog(MainActivity.this);
        todayTemperature = (TextView) findViewById(R.id.todayTemperature);
        todayDescription = (TextView) findViewById(R.id.todayDescription);
        todayWind = (TextView) findViewById(R.id.todayWind);
        todayPressure = (TextView) findViewById(R.id.todayPressure);
        todayHumidity = (TextView) findViewById(R.id.todayHumidity);
        todaySunrise = (TextView) findViewById(R.id.todaySunrise);
        todaySunset = (TextView) findViewById(R.id.todaySunset);
        lastUpdate = (TextView) findViewById(R.id.lastUpdate);
        todayIcon = (TextView) findViewById(R.id.todayIcon);
        weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");
        todayIcon.setTypeface(weatherFont);
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
        if(mapsWeathers.containsKey(location)){
            currentWeatherForecast = mapsWeathers.get(location);
            showTodayWeather();
        }else{
            getDataApi(location);
        }
    }

    private void updateDetailWeatherUI(){

    }

    private void showTodayWeather(){
        if(currentWeatherForecast != null){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String rainString = UnitConvertor.getRainString(currentWeatherForecast.getRain().getValue(), sp);
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
            Log.v("timeFormat: ", "timeFormat");
            if(currentWeatherForecast.getWind() != null){
                if (sp.getString("speedUnit", "m/s").equals("bft")) {
                    todayWind.setText(getString(R.string.wind) + ": " +
                            UnitConvertor.getBeaufortName((int) currentWeatherForecast.getWind().getSpeed().doubleValue()) +
                            (currentWeatherForecast.getWind().isWindDirectionAvailable() ? " " + UnitConvertor.getWindDirectionString(sp, this, currentWeatherForecast) : ""));
                } else {
                    todayWind.setText(getString(R.string.wind) + ": " + new DecimalFormat("0.0").format(currentWeatherForecast.getWind().getSpeed()) + " " +
                            ClientUtils.localize(sp, "speedUnit", "m/s", this) +
                            (currentWeatherForecast.getWind().isWindDirectionAvailable() ? " " + UnitConvertor.getWindDirectionString(sp, this, currentWeatherForecast) : ""));
                }
            }
            if(currentWeatherForecast.getWeather() != null){
                if(currentWeatherForecast.getWeather().getDescription() != null){
                    todayDescription.setText(currentWeatherForecast.getWeather().getDescription().substring(0, 1).toUpperCase() +
                            currentWeatherForecast.getWeather().getDescription().substring(1) + rainString);
                }else{
                    todayDescription.setText("Đang cập nhật");
                }
                double pressure = UnitConvertor.convertPressure(currentWeatherForecast.getWeather().getPressure(), sp);
                todayPressure.setText(getString(R.string.pressure) + ": " + new DecimalFormat("0.0").format( +pressure) + " " +
                        ClientUtils.localize(sp, "pressureUnit", "hPa", this));

                // Temperature
                float temperature = UnitConvertor.convertTemperature(currentWeatherForecast.getWeather().getTemp(), sp);
                todayTemperature.setText(new DecimalFormat("0.#").format(temperature) + " " + sp.getString("unit", "°C"));
                todayHumidity.setText(getString(R.string.humidity) + ": " + currentWeatherForecast.getWeather().getHumidity() + " %");
                todaySunrise.setText(getString(R.string.sunrise) + ": " + timeFormat.format(currentWeatherForecast.getWeather().getSunrise()));
                todaySunset.setText(getString(R.string.sunset) + ": " + timeFormat.format(currentWeatherForecast.getWeather().getSunset()));
            }
//            todayIcon.setText(todayWeather.getIcon());
        }
    }

    private String getCurrentLocation(){
        //get from text box search
        return "";
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
        new GenericRequestTask(this, this, progressDialog) {
            @Override
            protected ParseResult parseResponse(String response) {
                currentWeatherForecast = API.getDataFromString(response);
                Log.v("data: ",  currentWeatherForecast.getName() +" " + currentWeatherForecast.getWeather().getDescription());
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

//    private void getDataApi(String area){
//            String url = Config.BASE_URL+"?q="+ area + "&units="+ Config.UNITS +"&appid="+ Config.APP_ID;
//            AsyncHttpClient client = new AsyncHttpClient();
//            client.get(url, new AsyncHttpResponseHandler() {
//
//                @Override
//                public void onStart() {
//                }
//
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    String dataReturn =  new String(responseBody);
//                    WeatherForecast weatherForecast = API.getDataFromString(dataReturn);
//                    showTodayWeather();
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                    Log.d("failure", responseBody.toString());
//                }
//
//                @Override
//                public void onRetry(int retryNo) {
//                }
//            });
//    }

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
