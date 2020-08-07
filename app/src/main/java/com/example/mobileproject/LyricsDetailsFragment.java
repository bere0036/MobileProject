package com.example.mobileproject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LyricsDetailsFragment extends Fragment {

    TextView albumTextView;
    private Bundle dataFromActivity;
    private long id;
    private String artist;
    private String title;
    private String lyrics;
    private String album;
    private AppCompatActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong( LyricsFavouritesActivity.ITEM_ID);
        artist = dataFromActivity.getString( LyricsFavouritesActivity.ARTIST);
        title = dataFromActivity.getString( LyricsFavouritesActivity.TITLE);
        lyrics = dataFromActivity.getString( LyricsFavouritesActivity.LYRICS);

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.lyrics_fragment_details, container, false);

        //show the artist
        TextView artistTextView = result.findViewById(R.id.artistName);
        artistTextView.setText(artist);

        //show the title
        TextView titleTextView = result.findViewById(R.id.songTitle);
        titleTextView.setText(title);

        //show the lyrics
        TextView lyricsTextView = result.findViewById(R.id.songLyrics);
        lyricsTextView.setText(lyrics);


        // get the delete button, and add a click listener:
        Button hideButton = (Button)result.findViewById(R.id.fragmentButton);

        hideButton.setOnClickListener( clk -> {
            //Tell the parent activity to remove
            getFragmentManager().beginTransaction().remove(this).commit();
            getFragmentManager().popBackStackImmediate();

        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or LyricsEmptyActivity for phone
        parentActivity = (AppCompatActivity)context;
    }
}
