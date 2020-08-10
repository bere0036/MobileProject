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
    private ArrayList<LyricsSavedFavourite> elements = new ArrayList<>();
    Button saveToFavourites;
    Button hideButton;
    TextView artistTextView;
    TextView titleTextView;
    TextView lyricsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        dataFromActivity = getArguments();
        latitude = toTitleCase(dataFromActivity.getString( LyricsFavouritesActivity.LATITUDE));
        longitude = toTitleCase(dataFromActivity.getString( LyricsFavouritesActivity.LONGITUDE));
        country = toTitleCase(dataFromActivity.getString( LyricsFavouritesActivity.COUNTRY));
        region = toTitleCase(dataFromActivity.getString( LyricsFavouritesActivity.REGION));
        city = toTitleCase(dataFromActivity.getString( LyricsFavouritesActivity.CITY));
        currency = toTitleCase(dataFromActivity.getString( LyricsFavouritesActivity.CURRENCY));
      //  lyrics = spacingFixer(dataFromActivity.getString( LyricsFavouritesActivity.COUNTRY));

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.cities_fragment_details, container, false);

        //show the artist
        artistTextView = result.findViewById(R.id.artistName);
        artistTextView.setText(artist);

        //show the title
        titleTextView = result.findViewById(R.id.songTitle);
        titleTextView.setText(title);

        //show the lyrics
        lyricsTextView = result.findViewById(R.id.songLyrics);
        lyricsTextView.setText(lyrics);


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
            if (elements.get(i).getArtist().equals(artist) && elements.get(i).getTitle().equals(title)) {
                saveToFavourites.setVisibility(View.INVISIBLE);
                break;
            }
        }

        saveToFavourites.setOnClickListener(click -> {
            ContentValues newRowValues = new ContentValues();

            newRowValues.put(LyricsMyOpener.COL_ARTIST, artist);
            newRowValues.put(LyricsMyOpener.COL_TITLE, title);
            newRowValues.put(LyricsMyOpener.COL_LYRICS, lyricsTextView.getText().toString());


            long newId = db.insert(LyricsMyOpener.TABLE_NAME, null, newRowValues);

            LyricsSavedFavourite favourite = new LyricsSavedFavourite(artist, title, lyrics, newId);
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
        LyricsMyOpener dbOpener = new LyricsMyOpener(getActivity());
        db = dbOpener.getWritableDatabase(); // Calls onCreate() if you've never built the table before, onUpgrade if the version here is newer

        String[] columns = {LyricsMyOpener.COL_ID, LyricsMyOpener.COL_ARTIST, LyricsMyOpener.COL_TITLE, LyricsMyOpener.COL_LYRICS};

        Cursor results = db.query(false, LyricsMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(LyricsMyOpener.COL_ID);
        int artistColIndex = results.getColumnIndex(LyricsMyOpener.COL_ARTIST);
        int titleColIndex = results.getColumnIndex(LyricsMyOpener.COL_TITLE);
        int lyricsColIndex = results.getColumnIndex(LyricsMyOpener.COL_LYRICS);

        while (results.moveToNext()) {
            long id = results.getLong(idColIndex);
            String artist = results.getString(artistColIndex);
            String title = results.getString(titleColIndex);
            String lyrics = results.getString(lyricsColIndex);

            //add the new Song to the array list:
            elements.add(new LyricsSavedFavourite(artist, title, lyrics, id));
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


