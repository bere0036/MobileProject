package com.example.mobileproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class CitiesDetailsFragment extends Fragment {

    private Bundle dataFromActivity;
    SQLiteDatabase db;
    private long id;
    private String latitude;
    private String longitude;
    private String country;
    private String region;
    private String city;
    private String currency;
    private AppCompatActivity parentActivity;
    Button saveToFavourites;
    Button removeFromFavourites;
    Button hideButton;
    Button openMapsButton;
    TextView latitudeTextView;
    TextView longitudeTextView;
    TextView cityTextView;
    TextView countryTextView;
    TextView regionTextView;
    TextView currencyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(CitiesFavouritesActivity.ID, -1);
        latitude = dataFromActivity.getString(CitiesFavouritesActivity.LATITUDE);
        longitude = dataFromActivity.getString(CitiesFavouritesActivity.LONGITUDE);
        country = dataFromActivity.getString(CitiesFavouritesActivity.COUNTRY);
        region = dataFromActivity.getString(CitiesFavouritesActivity.REGION);
        city = dataFromActivity.getString(CitiesFavouritesActivity.CITY);
        currency = dataFromActivity.getString(CitiesFavouritesActivity.CURRENCY);

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.cities_fragment_details, container, false);

        db = new CitiesMyOpener(getActivity()).getWritableDatabase();

        cityTextView = result.findViewById(R.id.cityname);
        cityTextView.setText(city);

        countryTextView = result.findViewById(R.id.citycountry);
        countryTextView.setText(country);

        regionTextView = result.findViewById(R.id.cityregion);
        regionTextView.setText(region);

        currencyTextView = result.findViewById(R.id.citycurrency);
        currencyTextView.setText(currency);

        latitudeTextView = result.findViewById(R.id.latitudenumber);
        latitudeTextView.setText(latitude);

        longitudeTextView = result.findViewById(R.id.longitudenumber);
        longitudeTextView.setText(longitude);

        saveToFavourites = result.findViewById(R.id.saveToFavourites);
        removeFromFavourites = result.findViewById(R.id.removeFromFavourites);
        hideButton = result.findViewById(R.id.fragmentButton);
        openMapsButton = result.findViewById(R.id.openMaps);

        hideButton.setOnClickListener(clk -> {
            //Tell the parent activity to remove
            getFragmentManager().beginTransaction().remove(this).commit();
            getFragmentManager().popBackStackImmediate();

        });

        if (id == -1) {
            saveToFavourites.setVisibility(View.VISIBLE);
            removeFromFavourites.setVisibility(View.GONE);
        } else {
            removeFromFavourites.setVisibility(View.VISIBLE);
            saveToFavourites.setVisibility(View.GONE);
        }


        saveToFavourites.setOnClickListener(click -> {
            saveIntoDatabase();
        });


        removeFromFavourites.setOnClickListener(click -> {
            removeFromDatabase();
        });

        openMapsButton.setOnClickListener(click -> {

            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
            Intent openMap = new Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage("com.google.android.apps.maps");
            startActivity(openMap);

        });

        return result;
    }

    private void removeFromDatabase() {
        db.delete(CitiesMyOpener.TABLE_NAME, CitiesMyOpener.COL_ID + "=?", new String[]{Long.toString(id)});

        Toast.makeText(getActivity(), "Removed!", Toast.LENGTH_SHORT).show();
        removeFromFavourites.setVisibility(View.INVISIBLE);
    }

    private void saveIntoDatabase() {
        ContentValues newRowValues = new ContentValues();

        newRowValues.put(CitiesMyOpener.COL_LATITUDE, latitude);
        newRowValues.put(CitiesMyOpener.COL_LONGITUDE, longitude);
        newRowValues.put(CitiesMyOpener.COL_CITY, city);
        newRowValues.put(CitiesMyOpener.COL_COUNTRY, country);
        newRowValues.put(CitiesMyOpener.COL_REGION, region);
        newRowValues.put(CitiesMyOpener.COL_CURRENCY, currency);

        id = db.insert(CitiesMyOpener.TABLE_NAME, null, newRowValues);

        Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
        saveToFavourites.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or LyricsEmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }

    public static String spacingFixer(String givenString) {
        givenString.replace("\n\n", "\n");
        givenString.replace("\n\n\n\n", "\n\n");
        return givenString;
    }

    public void clear() {
        getFragmentManager().beginTransaction().remove(this).commit();
        getFragmentManager().popBackStackImmediate();
    }
}


