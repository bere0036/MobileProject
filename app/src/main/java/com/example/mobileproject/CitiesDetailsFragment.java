package com.example.mobileproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
    private ArrayList<CitiesSavedFavourite> elements = new ArrayList<>();
    Button saveToFavourites;
    Button hideButton;
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
        latitude = toTitleCase(dataFromActivity.getString( CitiesFavouritesActivity.LATITUDE));
        longitude = toTitleCase(dataFromActivity.getString( CitiesFavouritesActivity.LONGITUDE));
        country = toTitleCase(dataFromActivity.getString( CitiesFavouritesActivity.COUNTRY));
        region = toTitleCase(dataFromActivity.getString( CitiesFavouritesActivity.REGION));
        city = toTitleCase(dataFromActivity.getString( CitiesFavouritesActivity.CITY));
        currency = toTitleCase(dataFromActivity.getString( CitiesFavouritesActivity.CURRENCY));
      //  lyrics = spacingFixer(dataFromActivity.getString( CitiesFavouritesActivity.COUNTRY));

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.cities_fragment_details, container, false);

        //show the artist
        latitudeTextView = result.findViewById(R.id.latitude);
        latitudeTextView.setText(latitude);

        //show the title
        longitudeTextView = result.findViewById(R.id.longitudenumber);
        longitudeTextView.setText(longitude);

        //show the lyrics
       // lyricsTextView = result.findViewById(R.id.songLyrics);
       // lyricsTextView.setText(lyrics);


        // get the delete button, and add a click listener:
        hideButton = result.findViewById(R.id.fragmentButton);

        hideButton.setOnClickListener( clk -> {
            //Tell the parent activity to remove
            getFragmentManager().beginTransaction().remove(this).commit();
            getFragmentManager().popBackStackImmediate();

        });

        saveToFavourites = result.findViewById(R.id.saveToFavourites);

        loadDataFromDatabase();

        for (int i=0; i<elements.size(); i++) {
            if (elements.get(i).getLatitude().equals(latitude) && elements.get(i).getLongitude().equals(longitude)) {
                saveToFavourites.setVisibility(View.INVISIBLE);
                break;
            }
        }

        saveToFavourites.setOnClickListener(click -> {
            ContentValues newRowValues = new ContentValues();

            newRowValues.put(CitiesMyOpener.COL_LATITUDE, latitude);
            newRowValues.put(CitiesMyOpener.COL_LONGITUDE, longitude);
            newRowValues.put(CitiesMyOpener.COL_CITY, city);
            newRowValues.put(CitiesMyOpener.COL_COUNTRY, country);
            newRowValues.put(CitiesMyOpener.COL_REGION, region);
            newRowValues.put(CitiesMyOpener.COL_CURRENCY, currency);
            //newRowValues.put(LyricsMyOpener.COL_LYRICS, lyricsTextView.getText().toString());


            long newId = db.insert(LyricsMyOpener.TABLE_NAME, null, newRowValues);

            CitiesSavedFavourite favourite = new CitiesSavedFavourite(latitude, longitude, city, country, region, currency, newId);
            elements.add(favourite);

            Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
            saveToFavourites.setVisibility(View.INVISIBLE);
        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or LyricsEmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }

    private void loadDataFromDatabase() {
        CitiesMyOpener dbOpener = new CitiesMyOpener(getActivity());
        db = dbOpener.getWritableDatabase(); // Calls onCreate() if you've never built the table before, onUpgrade if the version here is newer

        String[] columns = {CitiesMyOpener.COL_ID, CitiesMyOpener.COL_LATITUDE, CitiesMyOpener.COL_LONGITUDE, CitiesMyOpener.COL_COUNTRY, CitiesMyOpener.COL_REGION, CitiesMyOpener.COL_CITY, CitiesMyOpener.COL_REGION };

        Cursor results = db.query(false, CitiesMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(CitiesMyOpener.COL_ID);
        int latitudeColIndex = results.getColumnIndex(CitiesMyOpener.COL_LATITUDE);
        int longitudeColIndex = results.getColumnIndex(CitiesMyOpener.COL_LONGITUDE);
        int cityColIndex = results.getColumnIndex(CitiesMyOpener.COL_CITY);
        int countryColIndex = results.getColumnIndex(CitiesMyOpener.COL_COUNTRY);
        int regionColIndex = results.getColumnIndex(CitiesMyOpener.COL_REGION);
        int currencyColIndex = results.getColumnIndex(CitiesMyOpener.COL_CURRENCY);

        while (results.moveToNext()) {
            long id = results.getLong(idColIndex);
            String latitude = results.getString(latitudeColIndex);
            String longitude = results.getString(longitudeColIndex);
            String city = results.getString(cityColIndex);
            String country = results.getString(countryColIndex);
            String region = results.getString(regionColIndex);
            String currency = results.getString(currencyColIndex);

            //add the new Song to the array list:
            elements.add(new CitiesSavedFavourite(latitude, longitude, city, country, region, currency, id));
        }
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
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


