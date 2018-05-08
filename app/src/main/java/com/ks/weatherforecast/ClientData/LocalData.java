package com.ks.weatherforecast.ClientData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ks.weatherforecast.BuildConfig;
import com.ks.weatherforecast.share.model.City;
import com.ks.weatherforecast.share.model.CountryCode;
import com.ks.weatherforecast.share.model.userinfo.UserInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by manhhoang on 5/2/18.
 */

public class LocalData extends SQLiteOpenHelper {
    private Context mycontext;
    public SQLiteDatabase myDataBase = null;

    private static final String DB_NAME = "weather.sqlite";
    private static String DB_PATH ="/data/data/"+ BuildConfig.APPLICATION_ID+"/databases/";
    private static final int DATABASE_VERSION = 1;

    public LocalData(Context context)  {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mycontext = context;
        if(myDataBase == null){
            boolean dbexist = checkdatabase();
            if (dbexist) {
                System.out.println("Database exists");
                opendatabase();
            } else {
                System.out.println("Database doesn't exist");
                try {
                    createdatabase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public LocalData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public LocalData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        // Script tạo bảng.
        String script = CountryCode.createDBCountryCode;
        // Chạy lệnh tạo bảng.
        db.execSQL(script);
        Log.v("create table sql", "done");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CountryCode");
        // Recreate
        onCreate(db);
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if(dbexist) {
            System.out.println(" Database exists.");
        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch(IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkdatabase() {

        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = mycontext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outfilename = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer))>0) {
            myoutput.write(buffer,0,length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    public void opendatabase() throws SQLException {
        //Open the database
        String mypath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if(myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    public void insertData(List<City> users){
        SQLiteDatabase db = this.getWritableDatabase();
        for (City info : users){
            ContentValues values = new ContentValues();
            values.put("id", info.getId());
            values.put("name", info.getName());
            values.put("countryCode", info.getCountryCode());
            values.put("lat  ", info.getLat());
            values.put("lon", info.getLon());
            // Trèn một dòng dữ liệu vào bảng.
            db.insert("City", null, values);
        }
        // Đóng kết nối database.
        db.close();

    }

    public void insertCountry(Set<String> countries){
        SQLiteDatabase db = this.getWritableDatabase();
        for (String info : countries){
            ContentValues values = new ContentValues();
            values.put("id", info);
            // Trèn một dòng dữ liệu vào bảng.
            db.insert("CountryCode", null, values);
        }
        // Đóng kết nối database.
        db.close();

    }

    public List<CountryCode> getCountry() {
        Log.v("count", "MyDatabaseHelper.getCountry ... " );
        String countQuery = "SELECT  * FROM CountryCode";
        Cursor cursor = myDataBase.rawQuery(countQuery, null);
        List<CountryCode> countries = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()){
                CountryCode code = new CountryCode();
                code.setId(cursor.getString(cursor.getColumnIndex("id")));
                code.setCountryCallingCodes(cursor.getString(cursor.getColumnIndex("countryCallingCodes")));
                code.setName(cursor.getString(cursor.getColumnIndex("name")));
                countries.add(code);
            }
        }
        Log.v("getCountry", ""+ countries.size());
//        int count = cursor.getCount();
        cursor.close();
        return countries;
    }

    public List<City> getCitiesByCountry(String countryCode) {
        Log.v("count", "MyDatabaseHelper.City ... " );
        String countQuery = "SELECT  * FROM City where countryCode ='" + countryCode +"'";
        Cursor cursor = myDataBase.rawQuery(countQuery, null);
        List<City> cities = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()){
                City code = new City();
                code.setId((long) cursor.getInt(cursor.getColumnIndex("id")));
                code.setCountryCode(cursor.getString(cursor.getColumnIndex("countryCode")));
                code.setName(cursor.getString(cursor.getColumnIndex("name")));
                code.setLat(cursor.getInt(cursor.getColumnIndex("lat")));
                code.setLon(cursor.getInt(cursor.getColumnIndex("lon")));
                cities.add(code);
            }
        }
        Log.v("cities size: ", cities.size() +"");
        cursor.close();
        return cities;
    }

    public int getCountCity() {
        Log.v("count", "MyDatabaseHelper.getNotesCount ... " );
        String countQuery = "SELECT  * FROM City";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int getCountCountry() {
        Log.v("count", "MyDatabaseHelper.getNotesCount ... " );

        String countQuery = "SELECT  * FROM CountryCode";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    public List<UserInfo> getAllUserInfo() {

        List<UserInfo> users = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM UserInfo";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                UserInfo user = new UserInfo();
                user.setId(Long.parseLong(cursor.getInt(0)+""));
                user.setName(cursor.getString(1));
                user.setUserName(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setPhone(cursor.getString(4));
                user.setWebsite(cursor.getString(5));

                // Thêm vào danh sách.
                users.add(user);
            } while (cursor.moveToNext());
        }

        // return note list
        return users;
    }

//    public List<City> getCityData(){
//        List<City> citis = new ArrayList<>();
//        try {
//            JSONArray jsonArray = new JSONArray(text);
//            if(jsonArray != null && jsonArray.length() > 0){
//                for (int i  = 0; i < jsonArray.length() ; i++){
//                    JSONObject object = jsonArray.optJSONObject(i);
//                    Long id = ClientUtils.getLong("id", object);
//                    Double lat = ClientUtils.getDouble("lat", object);
//                    Double lon = ClientUtils.getDouble("lon", object);
//                    String name = ClientUtils.getString("name", object);
//                    String countryCode = ClientUtils.getString("countryCode", object);
//                    citis.add(new City(id, name, lat, lon, countryCode));
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return citis;
//    }

}
