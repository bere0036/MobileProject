package com.example.mobileproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CitiesActivity extends AppCompatActivity {

    public static final String ID = "ID";
    public static final String CITY = "CITY";
    public static final String COUNTRY = "COUNTRY";
    public static final String REGION = "REGION";
    public static final String CURRENCY = "CURRENCY";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    private boolean isTablet;
    private List<CitiesSavedFavourite> cList;
    private ProgressBar loading;
    private ListView citiesListView;
    private CityAdapter adapter;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities_activity_favourites);

        cList = new ArrayList<>();
        citiesListView = findViewById(R.id.cities_list_view);
        loading = findViewById(R.id.city_loading);
        loading.setVisibility(View.INVISIBLE);
        adapter = new CityAdapter();
        citiesListView.setAdapter(adapter);

        Bundle data = getIntent().getExtras();
        latitude = data.getString(LATITUDE);
        longitude = data.getString(LONGITUDE);

        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle dataToPass = new Bundle();

                dataToPass.putSerializable(CITY, cList.get(position).getName());
                dataToPass.putSerializable(COUNTRY, cList.get(position).getCountry());
                dataToPass.putSerializable(REGION, cList.get(position).getRegion());
                dataToPass.putSerializable(CURRENCY, cList.get(position).getCurrency());
                dataToPass.putSerializable(LATITUDE, cList.get(position).getLatitude());
                dataToPass.putSerializable(LONGITUDE, cList.get(position).getLongitude());

                if (isTablet) {
                    CitiesDetailsFragment cdf = new CitiesDetailsFragment();
                    cdf.setArguments(dataToPass);

                    CitiesActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, cdf)
                            .commit();
                } else {
                    Intent cityEmpty = new Intent(CitiesActivity.this, CitiesEmptyActivity.class).putExtras(dataToPass);
                    CitiesActivity.this.startActivity(cityEmpty);
                }

            }
        });

        citiesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id) {
                showCityDetailDialog(index, id);
                return true;
            }
        });

        isTablet = findViewById(R.id.fragmentContainer) != null;

        new CityQuery().execute();

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.city_cities);
    }

    private void showCityDetailDialog(int index, long id) {
        new AlertDialog.Builder(CitiesActivity.this)
                .setTitle(R.string.city_detail)
                .setMessage("Index:" + index + "\nName:" + cList.get(index).getName() + "\nDatabase _id:" + id)
                .setPositiveButton(R.string.city_ok, null)
                .show();
    }

    private class CityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cList.size();
        }

        @Override
        public CitiesSavedFavourite getItem(int position) {
            return cList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return cList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            CitiesSavedFavourite city = getItem(position);

            View newView = inflater.inflate(R.layout.list_view_adapter_city, parent, false);

            TextView tvCity = newView.findViewById(R.id.tvCity);
            tvCity.setText(city.getName());

            return newView;
        }
    }

    private class CityQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL("https://api.geodatasource.com/cities?key=KQZ4KMGGFNWFSJUEYLXKTXGS2GWOOZZ3&lat=" + latitude + "&lng=" + longitude + "&format=JSON");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) sb.append(line + "\n");
                String result = sb.toString();

                JSONArray uvReport = new JSONArray(result);
                for (int i = 0; i < uvReport.length(); i++) {
                    JSONObject jsonObject = uvReport.getJSONObject(i);
                    CitiesSavedFavourite citiesSavedFavourite = new CitiesSavedFavourite(jsonObject.getString("city"), jsonObject.getString("country"), jsonObject.getString("region"), jsonObject.getString("currency_name"), jsonObject.getString("latitude"), jsonObject.getString("longitude"));
                    cList.add(citiesSavedFavourite);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (cList.isEmpty()) {
                Toast.makeText(CitiesActivity.this, R.string.city_no_city_available, Toast.LENGTH_SHORT).show();
            }
            loading.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.city_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toSoccerIcon:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.toLyricsIcon:
                startActivity(new Intent(this, LyricsMainActivity.class));
                break;
            case R.id.toDeezerIcon:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.citiesaboutProject:
                Toast.makeText(this, R.string.citiesAboutProject, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}