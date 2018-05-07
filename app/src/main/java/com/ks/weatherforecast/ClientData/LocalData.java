package com.ks.weatherforecast.ClientData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ks.weatherforecast.share.model.City;
import com.ks.weatherforecast.share.model.CountryCode;
import com.ks.weatherforecast.share.model.userinfo.UserInfo;
import com.ks.weatherforecast.utils.ClientUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by manhhoang on 5/2/18.
 */

public class LocalData extends SQLiteOpenHelper {

    public LocalData(Context context)  {
        super(context, "UserInfo", null, 1);
    }

    public LocalData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public LocalData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script tạo bảng.
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
