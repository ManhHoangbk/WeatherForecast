package com.ks.weatherforecast.share;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ks.weatherforecast.share.model.Location;
import com.ks.weatherforecast.share.model.Rain;
import com.ks.weatherforecast.share.model.Weather;
import com.ks.weatherforecast.share.model.WeatherForecast;
import com.ks.weatherforecast.share.model.Wind;
import com.ks.weatherforecast.utils.ClientUtils;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by manhhoang on 3/22/18.
 */

public class API {

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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weatherForecast;
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
            wind.setSpeed(ClientUtils.getDouble("speed", wObj));
            wind.setDeg(ClientUtils.getDouble("deg", wObj));
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
}
