package com.example.mobileproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CitiesFavouritesActivity extends AppCompatActivity {

    public static final String CURRENCY = "CURRENCY";
    public static final String CITY = "CITY";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String ID = "ID";
    public static final String LATITUDE = "LATITUDE";
    public static final String COUNTRY = "COUNTRY";
    public static final String REGION = "REGION";

    private boolean isTablet;
    private List<CitiesSavedFavourite> cList;
    private ProgressBar loading;
    private ListView citiesListView;
    private CitiesMyOpener cityDatabaseOpener;
    private CityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities_activity_favourites);

        cList = new ArrayList<>();
        cityDatabaseOpener = new CitiesMyOpener(this);
        citiesListView = findViewById(R.id.cities_list_view);
        loading = findViewById(R.id.city_loading);
        loading.setVisibility(View.INVISIBLE);
        adapter = new CityAdapter();
        citiesListView.setAdapter(adapter);

        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle dataToPass = new Bundle();

                dataToPass.putSerializable(ID, cList.get(position).getId());
                dataToPass.putSerializable(CITY, cList.get(position).getName());
                dataToPass.putSerializable(COUNTRY, cList.get(position).getCountry());
                dataToPass.putSerializable(REGION, cList.get(position).getRegion());
                dataToPass.putSerializable(CURRENCY, cList.get(position).getCurrency());
                dataToPass.putSerializable(LATITUDE, cList.get(position).getLatitude());
                dataToPass.putSerializable(LONGITUDE, cList.get(position).getLongitude());

                if (isTablet) {
                    CitiesDetailsFragment cdf = new CitiesDetailsFragment();
                    cdf.setArguments(dataToPass);

                    CitiesFavouritesActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, cdf)
                            .commit();
                } else {
                    Intent cityEmpty = new Intent(CitiesFavouritesActivity.this, CitiesEmptyActivity.class).putExtras(dataToPass);
                    CitiesFavouritesActivity.this.startActivity(cityEmpty);
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

        loadCitiesFromDatabase();

        isTablet = findViewById(R.id.fragmentContainer) != null;

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.city_fav_cities);
    }

    private void showCityDetailDialog(int index, long id) {
        new AlertDialog.Builder(CitiesFavouritesActivity.this)
                .setTitle(R.string.city_detail)
                .setMessage("Index:" + index + "\nName:" + cList.get(index).getName() + "\nDatabase _id:" + id)
                .setPositiveButton(R.string.city_ok, null)
                .show();
    }

    private void loadCitiesFromDatabase() {
        List<CitiesSavedFavourite> cities = new ArrayList<>();
        SQLiteDatabase db = cityDatabaseOpener.getReadableDatabase();

        String[] columns = {CitiesMyOpener.COL_ID, CitiesMyOpener.COL_CITY, CitiesMyOpener.COL_COUNTRY, CitiesMyOpener.COL_REGION, CitiesMyOpener.COL_CURRENCY, CitiesMyOpener.COL_LATITUDE, CitiesMyOpener.COL_LONGITUDE};
        Cursor results = db.query(CitiesMyOpener.TABLE_NAME, columns, null, null, null, null, null);

        int indexId = results.getColumnIndex(CitiesMyOpener.COL_ID);
        int indexName = results.getColumnIndex(CitiesMyOpener.COL_CITY);
        int indexCountry = results.getColumnIndex(CitiesMyOpener.COL_COUNTRY);
        int indexRegion = results.getColumnIndex(CitiesMyOpener.COL_REGION);
        int indexCurrency = results.getColumnIndex(CitiesMyOpener.COL_CURRENCY);
        int indexLatitude = results.getColumnIndex(CitiesMyOpener.COL_LATITUDE);
        int indexLongitude = results.getColumnIndex(CitiesMyOpener.COL_LONGITUDE);

        while (results.moveToNext()) {
            cities.add(new CitiesSavedFavourite(results.getLong(indexId), results.getString(indexName), results.getString(indexCountry), results.getString(indexRegion), results.getString(indexCurrency), results.getString(indexLatitude), results.getString(indexLongitude)));
        }
        results.close();

        cList.clear();
        cList.addAll(cities);
        adapter.notifyDataSetChanged();

        if (cList.isEmpty()) {
            Snackbar.make(citiesListView, R.string.city_no_city_available, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCitiesFromDatabase();
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
            CitiesSavedFavourite citiesSavedFavourite = getItem(position);

            View newView = inflater.inflate(R.layout.list_view_adapter_city, parent, false);

            TextView tvCity = newView.findViewById(R.id.tvCity);
            tvCity.setText(citiesSavedFavourite.getName());

            return newView;
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