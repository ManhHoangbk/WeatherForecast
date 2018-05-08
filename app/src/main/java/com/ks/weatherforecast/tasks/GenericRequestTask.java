package com.ks.weatherforecast.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;

import com.ks.weatherforecast.Activity.MainActivity;
import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.Config;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public abstract class GenericRequestTask extends AsyncTask<String, String, TaskOutput> {

    ProgressDialog progressDialog;
    Context context;
    MainActivity activity;
    public int loading = 0;
    private String query = "";

    private static final int TYPE_SEARCHING = 0;
    private static final int TYPE_GET = 1;


    public GenericRequestTask(Context context, MainActivity activity, String query) {
        this.context = context;
        this.activity = activity;
        this.progressDialog = new ProgressDialog(activity);
        this.query = query;
    }

    @Override
    protected void onPreExecute() {
        incLoadingCounter();
        if(!progressDialog.isShowing()) {
            progressDialog.setMessage(context.getString(R.string.downloading_data));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    protected TaskOutput doInBackground(String... params) {
        Log.v("params TaskOutput: ", params.toString());
        TaskOutput output = new TaskOutput();
        String response = "";
            try {
                URL url = provideURL(query);
                Log.v("url", url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader r = new BufferedReader(inputStreamReader);
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        response += line + "\n";
                    }
                    Log.v("response", response);
                    close(r);
                    urlConnection.disconnect();
                    output.taskResult = TaskResult.SUCCESS;
                }
                else if (urlConnection.getResponseCode() == 429) {
                    Log.i("Task", "too many requests");
                    output.taskResult = TaskResult.TOO_MANY_REQUESTS;
                }
                else {
                    Log.i("Task", "bad response " + urlConnection.getResponseCode());
                    output.taskResult = TaskResult.BAD_RESPONSE;
                }
            } catch (IOException e) {
                Log.e("IOException Data", response);
                e.printStackTrace();
                output.taskResult = TaskResult.IO_EXCEPTION;
            }

        if (TaskResult.SUCCESS.equals(output.taskResult)) {
            ParseResult parseResult = parseResponse(response);
            if (ParseResult.CITY_NOT_FOUND.equals(parseResult)) {
                restorePreviousCity();
            }
            output.parseResult = parseResult;
        }
        return output;
    }

    @Override
    protected void onPostExecute(TaskOutput output) {
        if(loading == 1) {
            progressDialog.dismiss();
        }
        decLoadingCounter();

        updateMainUI();

        handleTaskOutput(output);
    }

    protected final void handleTaskOutput(TaskOutput output) {
        switch (output.taskResult) {
            case SUCCESS: {
                ParseResult parseResult = output.parseResult;
                if (ParseResult.CITY_NOT_FOUND.equals(parseResult)) {
                    Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_city_not_found), Snackbar.LENGTH_LONG).show();
                } else if (ParseResult.JSON_EXCEPTION.equals(parseResult)) {
                    Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_err_parsing_json), Snackbar.LENGTH_LONG).show();
                }
                break;
            }
            case TOO_MANY_REQUESTS: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_too_many_requests), Snackbar.LENGTH_LONG).show();
                break;
            }
            case BAD_RESPONSE: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_connection_problem), Snackbar.LENGTH_LONG).show();
                break;
            }
            case IO_EXCEPTION: {
                Snackbar.make(activity.findViewById(android.R.id.content), context.getString(R.string.msg_connection_not_available), Snackbar.LENGTH_LONG).show();
                break;
            }
        }
    }

    private String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (language.equals("cs")) {
            language = "cz";
        }
        return language;
    }

    private URL provideURL(String query) throws UnsupportedEncodingException, MalformedURLException {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//        String apiKey = sp.getString("apiKey", activity.getResources().getString(R.string.apiKey));
//        StringBuilder urlBuilder = new StringBuilder("https://api.openweathermap.org/data/2.5/");
//        urlBuilder.append("find").append("?");
//        String city  = sp.getString("city", query);
//        urlBuilder.append("q=").append(URLEncoder.encode(city, "UTF-8"));
//        urlBuilder.append("&type=").append("like");
//        urlBuilder.append("&lang=").append(getLanguage());
//        urlBuilder.append("&cnt=15");
//        urlBuilder.append("&appid=").append(apiKey);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String apiKey = sp.getString("apiKey", activity.getResources().getString(R.string.apiKey));

        StringBuilder urlBuilder = new StringBuilder("https://api.openweathermap.org/data/2.5/");
        urlBuilder.append(getAPIName()).append("?");

            final String city = sp.getString("city", Config.DEFAULT_CITY);
            urlBuilder.append("q=").append(URLEncoder.encode(city, "UTF-8"));

        urlBuilder.append("&lang=").append(getLanguage());
        urlBuilder.append("&mode=json");
        urlBuilder.append("&appid=").append(apiKey);
        return new URL(urlBuilder.toString());
    }

    private void restorePreviousCity() {
//        if (!TextUtils.isEmpty(activity.recentCity)) {
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
//            editor.putString("city", activity.recentCity);
//            editor.commit();
//            activity.recentCity = "";
//        }
    }

    private static void close(Closeable x) {
        try {
            if (x != null) {
                x.close();
            }
        } catch (IOException e) {
            Log.e("IOException Data", "Error occurred while closing stream");
        }
    }

    private void incLoadingCounter() {
        loading++;
    }

    private void decLoadingCounter() {
        loading--;
    }

    protected void updateMainUI() { }

    protected abstract ParseResult parseResponse(String response);
    protected abstract String getAPIName();
}
