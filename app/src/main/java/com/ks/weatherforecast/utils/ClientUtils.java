package com.ks.weatherforecast.utils;

import com.ks.weatherforecast.share.model.userinfo.Address;
import com.ks.weatherforecast.share.model.userinfo.Company;
import com.ks.weatherforecast.share.model.userinfo.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manhhoang on 4/25/18.
 */

public class ClientUtils {

    public static List<UserInfo> parseData(String data){
        List<UserInfo> userInfos = new ArrayList<>();
        try {

            JSONArray arr = new JSONArray(data);
            if(arr != null && arr.length() > 0){
                for (int i = 0 ; i < arr.length() ; i++){
                    JSONObject object = arr.getJSONObject(i);
                    UserInfo info = new UserInfo();
                    info.setId(getLong("id", object));
                    info.setEmail(getString("email", object));
                    info.setName(getString("name", object));
                    info.setUserName(getString("userName", object));
                    info.setWebsite(getString("website",object));
                    info.setPhone(getString("phone",object));

                    JSONObject companyObj = object.getJSONObject("company");
                    Company company = new Company();
                    company.setName(getString("name", companyObj));
                    company.setBs(getString("bs", companyObj));
                    company.setCatchPhrase(getString("catchPhrase",companyObj));
                    info.setCompany(company);

                    JSONObject objectAddr = new JSONObject();
                    Address address = new Address();
                    address.setCity(getString("city", objectAddr));
                    address.setStreet(getString("treet",objectAddr));
                    address.setSuite(getString("suite",objectAddr));
                    address.setZipcode(getString("zipcode",objectAddr));
                    info.setAddress(address);
                    userInfos.add(info);
//                    info.setName(getS);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userInfos;
    }

    public static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    public static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    public static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    public static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    public static long getLong(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getLong(tagName);
    }
}
