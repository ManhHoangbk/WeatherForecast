package com.ks.weatherforecast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.ks.weatherforecast.Activity.MainActivity;
import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.Config;
import com.ks.weatherforecast.share.model.Clouds;
import com.ks.weatherforecast.share.model.Location;
import com.ks.weatherforecast.share.model.Rain;
import com.ks.weatherforecast.share.model.Weather;
import com.ks.weatherforecast.share.model.WeatherForecast;
import com.ks.weatherforecast.share.model.Wind;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by manhhoang on 4/25/18.
 */

public class ClientUtils {

    public static String getStrIcon(Context context, String iconId) {
        String icon;
        switch (iconId) {
            case "01d":
                icon = context.getString(R.string.icon_clear_sky_day);
                break;
            case "01n":
                icon = context.getString(R.string.icon_clear_sky_night);
                break;
            case "02d":
                icon = context.getString(R.string.icon_few_clouds_day);
                break;
            case "02n":
                icon = context.getString(R.string.icon_few_clouds_night);
                break;
            case "03d":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "03n":
                icon = context.getString(R.string.icon_scattered_clouds);
                break;
            case "04d":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "04n":
                icon = context.getString(R.string.icon_broken_clouds);
                break;
            case "09d":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "09n":
                icon = context.getString(R.string.icon_shower_rain);
                break;
            case "10d":
                icon = context.getString(R.string.icon_rain_day);
                break;
            case "10n":
                icon = context.getString(R.string.icon_rain_night);
                break;
            case "11d":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "11n":
                icon = context.getString(R.string.icon_thunderstorm);
                break;
            case "13d":
                icon = context.getString(R.string.icon_snow);
                break;
            case "13n":
                icon = context.getString(R.string.icon_snow);
                break;
            case "50d":
                icon = context.getString(R.string.icon_mist);
                break;
            case "50n":
                icon = context.getString(R.string.icon_mist);
                break;
            default:
                icon = context.getString(R.string.icon_weather_default);
        }

        return icon;
    }

    public static void getData(String area){
        String url = Config.BASE_URL+"?q="+ area + "&units="+ Config.UNITS +"&appid="+ Config.APP_ID;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String dataReturn =  new String(responseBody);
                WeatherForecast weatherForecast = getDataFromString(dataReturn);

                Message msg = Message.obtain();
                Bundle data = new Bundle();
                data.putBoolean("success", true);
                msg.setData(data);
//                callback.handleMessage(data);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("failure", responseBody.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public static WeatherForecast parsedataFromString(String data){
        WeatherForecast weatherForecast = null;
        try {
            weatherForecast = new WeatherForecast();
            JSONObject object = new JSONObject(data);
            JSONArray array = object.getJSONArray("list");
            if(array != null && array.length() > 0){
                JSONObject jObj = array.getJSONObject(0);
                weatherForecast.setId((long)getInt("id", jObj));
                weatherForecast.setName(getString("name", jObj));
                weatherForecast.setDate(getInt("dt", jObj)+"");
                weatherForecast.setLocation(getLocation(jObj));
                weatherForecast.setWind(getWinds(jObj));
                weatherForecast.setWeather(getWeather(jObj));
                weatherForecast.setRain(getRain(jObj));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weatherForecast;
    }

    public static WeatherForecast getDataFromString(String data){
        WeatherForecast weatherForecast = null;
        try {
            weatherForecast = new WeatherForecast();
            JSONObject jObj = new JSONObject(data);
            if(jObj == null){
                return weatherForecast;
            }
            weatherForecast.setId((long)getInt("id", jObj));
            weatherForecast.setName(getString("name", jObj));
            weatherForecast.setDate(getInt("dt", jObj)+"");
            weatherForecast.setLocation(getLocation(jObj));
            weatherForecast.setWind(getWinds(jObj));
            weatherForecast.setWeather(getWeather(jObj));
            weatherForecast.setRain(getRain(jObj));
            weatherForecast.setClouds(getClouds(jObj));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weatherForecast;
    }

    private static Clouds getClouds(JSONObject jObj){
        Clouds clouds = null;
        try {
            clouds = new Clouds();
            JSONObject wObj = getObject("clouds", jObj);
            if(wObj != null){
                Double all = getDouble("all", wObj);
                clouds.setAll(all);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return clouds;
    }

    private static Weather getWeather(JSONObject jObj){
        Weather weather = null;
        try {
            weather = new Weather();
            JSONObject wObj = getObject("main", jObj);
            if(wObj == null){
                return weather;
            }
            weather.setTemp(getFloat("temp", wObj));
            weather.setPressure(getFloat("pressure", wObj));
            weather.setHumidity(getFloat("humidity", wObj));
            weather.setTem_min(getFloat("temp_min", wObj));
            weather.setTem_max(getFloat("temp_max", wObj));
            weather.setHumidity(getFloat("humidity", wObj));

            JSONArray jsonArray = jObj.getJSONArray("weather");
            if(jsonArray != null && jsonArray.length() > 0){
                JSONObject obj = jsonArray.getJSONObject(0);
                weather.setId((long)getInt("id", obj));
                weather.setMain(getString("main", obj));
                weather.setDescription(getString("description", obj));
                weather.setIcon(getString("icon", obj));
            }

            JSONObject sys = getObject("sys", jObj);
            if(sys != null){
                weather.setSunrise(getLong("sunrise", sys) * 1000);
                weather.setSunset(getLong("sunset", sys) * 1000);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weather;
    }


    private static Rain getRain(JSONObject jObj){
        Rain rain = null;
        try {
            rain = new Rain();
            JSONObject wObj = getObject("rain", jObj);
            if(wObj != null){
                for (Iterator<String> it = wObj.keys(); it.hasNext(); ) {
                    String key = it.next();
                    rain.setValue(ClientUtils.getDouble(key, wObj));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rain;
    }

    private static Wind getWinds(JSONObject jObj){
        Wind wind = null;
        try {
            wind = new Wind();
            JSONObject wObj = getObject("wind", jObj);
            wind.setSpeed(getDouble("speed", wObj));
            wind.setDeg(getDouble("deg", wObj));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wind;
    }

    private static Location getLocation(JSONObject jObj ){
        Location location = new Location();
        try {
            JSONObject coordObj = getObject("coord", jObj);
            if( coordObj== null){
                return location;
            }
            location.setLat(getFloat("lat", coordObj));
            location.setLon(getFloat("lon", coordObj));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return location;
    }

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = null;
        try {
            subObj = jObj.getJSONObject(tagName);
        }catch (Exception e){

        }
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        try {
            return jObj.getString(tagName);
        }catch (Exception e){
            return "";
        }

    }

    private static Double getDouble(String tagName, JSONObject jObj) throws JSONException {
        try {
            return jObj.getDouble(tagName);
        }catch (Exception e){
            return Double.valueOf(0);
        }

    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        try {
            return (float) jObj.getDouble(tagName);
        }catch (Exception e){
            return 0;
        }

    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        try {
            return jObj.getInt(tagName);
        }catch (Exception e){
            return 0;
        }

    }

    public static long getLong(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getLong(tagName);
    }

    public static String localize(SharedPreferences sp, String preferenceKey, String defaultValueKey, Context context) {
        return localize(sp, context, preferenceKey, defaultValueKey);
    }

    public static String localize(SharedPreferences sp, Context context, String preferenceKey, String defaultValueKey) {
        String preferenceValue = sp.getString(preferenceKey, defaultValueKey);
        String result = preferenceValue;
        if ("speedUnit".equals(preferenceKey)) {
            if (MainActivity.speedUnits.containsKey(preferenceValue)) {
                result = context.getString(MainActivity.speedUnits.get(preferenceValue));
            }
        } else if ("pressureUnit".equals(preferenceKey)) {
            if (MainActivity.pressUnits.containsKey(preferenceValue)) {
                result = context.getString(MainActivity.pressUnits.get(preferenceValue));
            }
        }
        return result;
    }
}
