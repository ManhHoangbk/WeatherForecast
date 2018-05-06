package com.ks.weatherforecast.ClientData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ks.weatherforecast.share.model.userinfo.UserInfo;

import java.util.ArrayList;
import java.util.List;

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
        String script = UserInfo.createDB;
        // Chạy lệnh tạo bảng.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertData(List<UserInfo> users){
        SQLiteDatabase db = this.getWritableDatabase();
        for (UserInfo info : users){
            ContentValues values = new ContentValues();
            values.put("id", info.getId());
            values.put("name", info.getName());
            values.put("userName", info.getUserName());
            values.put("email  ", info.getEmail());
            values.put("phone", info.getPhone());
            values.put("website", info.getWebsite());


            // Trèn một dòng dữ liệu vào bảng.
            db.insert("UserInfo", null, values);
        }


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
}
