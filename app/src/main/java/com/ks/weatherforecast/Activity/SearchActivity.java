package com.ks.weatherforecast.Activity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import com.ks.weatherforecast.ClientData.CityManager;
import com.ks.weatherforecast.R;
import com.ks.weatherforecast.share.model.WeatherForecast;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";

    private final String APP_SETTINGS_CITY = "city";
    private final String APP_SETTINGS_COUNTRY_CODE = "country_code";
    private final String APP_SETTINGS_LATITUDE = "latitude";
    private final String APP_SETTINGS_LONGITUDE = "longitude";

    private List<WeatherForecast> mCites;
    private SearchCityAdapter mSearchCityAdapter;
    private RecyclerView recyclerView = null;
    private SharedPreferences mCityPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupSearchView();

        String APP_SETTINGS_NAME = "config";
        mCityPref = getSharedPreferences(APP_SETTINGS_NAME, 0);

        recyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        mCites = new ArrayList<>();
        mSearchCityAdapter = new SearchCityAdapter(mCites);
        recyclerView.setAdapter(mSearchCityAdapter);
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchCityAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                mSearchCityAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private class SearchCityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private WeatherForecast mCity;
        private TextView mCityName;

        SearchCityHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCityName = (TextView) itemView.findViewById(R.id.city_name);
        }

        void bindCity(WeatherForecast city) {
            mCity = city;
            mCityName.setText(city.getName() +" - " + city.getCountry());
        }

        @Override
        public void onClick(View v) {
            v.setBackgroundColor(Color.rgb(227, 227, 227));
            Intent myIntent = new Intent(SearchActivity.this, MainActivity.class);
            myIntent.putExtra("name", mCity.getName()+"");
            startActivity(myIntent);

//            setCity(mCity);
//            sendBroadcast(new Intent(Constants.ACTION_FORCED_APPWIDGET_UPDATE));
//            setResult(RESULT_OK);
            finish();
        }
    }

    private class SearchCityAdapter extends RecyclerView.Adapter<SearchCityHolder> implements
            Filterable {

        private List<WeatherForecast> mCites;

        SearchCityAdapter(List<WeatherForecast> cites) {
            mCites = cites;
        }

        @Override
        public int getItemCount() {
            if (mCites != null)
                return mCites.size();

            return 0;
        }

        @Override
        public void onBindViewHolder(SearchCityHolder holder, int position) {
            WeatherForecast city = mCites.get(position);
            holder.bindCity(city);
        }

        @Override
        public SearchCityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);
            View v = inflater.inflate(R.layout.city_item, parent, false);

            return new SearchCityHolder(v);
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults = new FilterResults();

                    List<WeatherForecast> citySearchList = CityManager.getCity(SearchActivity.this, SearchActivity.this, charSequence.toString());

                    filterResults.values = citySearchList;
                    filterResults.count = citySearchList != null ? citySearchList.size() : 0;
                    if(citySearchList == null || citySearchList.isEmpty()){
                        Toast.makeText(SearchActivity.this, "Tìm kiếm rỗng!", Toast.LENGTH_LONG);
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence,
                                              FilterResults filterResults) {
                    mCites.clear();
                    if (filterResults.values != null) {
                        mCites.addAll((ArrayList<WeatherForecast>) filterResults.values);
                    }
                    mSearchCityAdapter = new SearchCityAdapter(mCites);
                    recyclerView.setAdapter(mSearchCityAdapter);
                    mSearchCityAdapter.notifyDataSetChanged();
                }
            };
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

