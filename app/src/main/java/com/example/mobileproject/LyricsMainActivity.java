package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class LyricsMainActivity extends AppCompatActivity {

    public static final String ACTIVITY_NAME = "MAIN_ACTIVITY";
    SharedPreferences prefs = null;
    private ArrayList<LyricsSavedFavourite> elements = new ArrayList<>();
    SQLiteDatabase db;
    protected String artist;
    protected String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyrics_activity_main);


        Button toSavedFavourites = findViewById(R.id.savedFavouritesButton);
        EditText artistName = findViewById(R.id.enterartistname);
        EditText songTitle = findViewById(R.id.enterartitlesong);


        toSavedFavourites.setOnClickListener(click ->   {
            Intent nextPage = new Intent(this, LyricsFavouritesActivity.class);
            nextPage.putExtra("Artist", artistName.getText().toString());
            nextPage.putExtra("Title", songTitle.getText().toString());
            startActivity(nextPage);
        });


        Button searchAPIButton = findViewById(R.id.searchAPIButton);
        searchAPIButton.setOnClickListener(lb -> {
            artist = artistName.getText().toString();
            title = songTitle.getText().toString();

            SongQuery req = new SongQuery();
            loadDataFromDatabase();
            String url = "https://api.lyrics.ovh/v1/" +
                    artist.replace(" ", "%20") + "/" +
                    title.replace(" ", "%20");
            req.execute(url);
        });
    }

    private void loadDataFromDatabase() {

        LyricsMyOpener dbOpener = new LyricsMyOpener(this);
        db = dbOpener.getWritableDatabase(); // Calls onCreate() if you've never built the table before, onUpgrade if the version here is newer

        String[] columns = {LyricsMyOpener.COL_ID, LyricsMyOpener.COL_ARTIST, LyricsMyOpener.COL_TITLE, LyricsMyOpener.COL_LYRICS};

        Cursor results = db.query(false, LyricsMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
//        printCursor(results, db.getVersion());

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


    private class SongQuery extends AsyncTask<String, String, String> {
        private String lyrics;

        @Override
        protected String doInBackground(String... args) {
            try {
                //create a URL object of what server to contact:

                URL url = new URL(args[0]);

                artist = artist.replace("%20", " ");
                title = title.replace("%20", " ");

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                //From part 3, slide 20
                String parameter = null;

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                response = urlConnection.getInputStream();

                //JSON reading:   Look at slide 26
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");

                String result = sb.toString(); //result is the whole string

                JSONObject returnedInfo = new JSONObject(result);
                lyrics = returnedInfo.getString("lyrics");


            } catch (Exception e) {
                e.printStackTrace();
            }

            return title;
        }


        protected void onProgressUpdate(Integer... value) {
//            ProgressBar progressBar = findViewById(R.id.progressBar);
//            progressBar.setProgress(value[0]);
//            progressBar.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(String fromDoInBackground) {

            title = fromDoInBackground;
            ContentValues newRowValues = new ContentValues();

            loadDataFromDatabase();

            newRowValues.put(LyricsMyOpener.COL_ARTIST, toTitleCase(artist.toLowerCase()));
            newRowValues.put(LyricsMyOpener.COL_TITLE, toTitleCase(title.toLowerCase()));
            newRowValues.put(LyricsMyOpener.COL_LYRICS, lyrics);


            long newId = db.insert(LyricsMyOpener.TABLE_NAME, null, newRowValues);

            LyricsSavedFavourite favourite = new LyricsSavedFavourite(artist, title, lyrics, newId);
            elements.add(favourite);
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
}
