package com.ks.weatherforecast.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.ks.weatherforecast.ClientData.LocalData;
import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.API;
import com.ks.weatherforecast.share.Config;
import com.ks.weatherforecast.share.model.WeatherForecast;
import com.ks.weatherforecast.share.model.userinfo.UserInfo;
import com.ks.weatherforecast.tasks.GenericRequestTask;
import com.ks.weatherforecast.tasks.ParseResult;
import com.ks.weatherforecast.utils.ClientUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity{

    TextView todayTemperature;
    TextView todayDescription;
    TextView todayWind;
    TextView todayPressure;
    TextView todayHumidity;
    TextView todaySunrise;
    TextView todaySunset;
    TextView lastUpdate;
    TextView todayIcon;
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
//        getDataApi("Saigon");
    }

    private void initView(){
        progressDialog = new ProgressDialog(MainActivity.this);
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

    }

    private String getCurrentLocation(){
        //get from text box search
        return "";
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    private void getDataApi(String area){
        new GenericRequestTask(this, this, progressDialog) {
            @Override
            protected ParseResult parseResponse(String response) {
                Log.v("response: ", response);
                currentWeatherForecast = API.getDataFromString(response);
                return ParseResult.OK;
            }

            @Override
            protected String getAPIName() {
                return "weather";
            }

            @Override
            protected void updateMainUI() {
                updateDetailWeatherUI();
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
