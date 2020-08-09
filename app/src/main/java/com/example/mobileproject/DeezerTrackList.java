package com.example.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeezerTrackList extends AppCompatActivity {

    ListView trackListView;
    String trackListUrl = "";
    ArrayList<DeezerSongs> songs;
    DeezerAdapter trackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_track_list);
        //initial views
        trackListView = findViewById(R.id.songtrackList);
        songs = new ArrayList<>();
        trackListAdapter = new DeezerAdapter(this, songs);
        trackListView.setAdapter(trackListAdapter);
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeezerTrackList.this, DeezerSongDetails.class);
                DeezerSongs song = songs.get(position);
                intent.putExtra("songDetails", song);
                startActivity(intent);
            }
        });
        //get trackList
        trackListUrl = getIntent().getStringExtra("tracklist_url");
        new GetTrackList(trackListUrl).execute();
    }

    class GetTrackList extends AsyncTask<Void, Void, String> {

        String trackListUrl;

        public GetTrackList(String trackListUrl) {
            this.trackListUrl = trackListUrl;
        }

        @Override
        protected String doInBackground(Void... voids) {
            URL mUrl = null;
            String trackData = "";
            try {
                mUrl = new URL(trackListUrl);
                HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
                InputStream inputStream = connection.getInputStream();

                StringBuilder stringBuilder = new StringBuilder();
                String readLine;
                try {
                    //read input string
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    while ((readLine = br.readLine()) != null) {
                        stringBuilder.append(readLine);
                    }
                    trackData = stringBuilder.toString();
                } catch (IOException e) {
                    trackData = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return trackData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                int count = jsonArray.length();
                for (int i = 0; i < count; i++) {
                    JSONObject data = jsonArray.getJSONObject(i);
                    int id = data.getInt("id");
                    String title = data.getString("title");
                    int duration = data.getInt("duration");
                    JSONObject album = data.getJSONObject("album");
                    String albumTitle = album.getString("title");
                    String cover_big = album.getString("cover_big");

                    DeezerSongs song = new DeezerSongs();
                    song.setId(Long.valueOf(id));
                    song.setTitle(title);
                    song.setDuration(duration);
                    song.setAlbum(albumTitle);
                    song.setCoverURL(cover_big);
                    songs.add(song);
                }
                trackListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DeezerAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<DeezerSongs> list;

        public DeezerAdapter(Context context, ArrayList<DeezerSongs> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.activity_deezer_songs, parent, false);

            TextView songNameText = (TextView) newView.findViewById(R.id.textSongName);
            String songTitle = list.get(position).getTitle();
            songNameText.setText(songTitle);

            return newView;
        }
    }
}
