package com.ks.weatherforecast.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.model.userinfo.UserInfo;

import java.util.List;

/**
 * Created by manhhoang on 5/2/18.
 */

public class Listview extends ArrayAdapter<UserInfo> {

    public Listview(Context context, int resource, List<UserInfo> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view =  inflater.inflate(R.layout.sample_user_info_item, null);
        }
        UserInfo p = getItem(position);
        if (p != null) {
            TextView txt = (TextView) view.findViewById(R.id.info);
            txt.setText(p.getId() +"\n"+ p.getName()+"\n"+ p.getUserName()+"\n" + p.getEmail()+"\n"
                    +"address : suite    : "+p.getAddress().getStreet()+"\n "+p.getAddress().getSuite()+"\n "+p.getAddress().getCity()+"\n"+p.getAddress().getZipcode()
                    +" geo: "+"         "+p.getCompany().getCatchPhrase()+"\n"+p.getCompany().getName()+"\n"+p.getWebsite() );
            // txt += txt.setText("\n"+"address : "+p.suite);

        }
        return view;
    }
}
