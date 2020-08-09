package com.example.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DeezerFavouriteList extends AppCompatActivity {

    ListView listFavouriteSongs;
    ArrayList<DeezerSongs> songs;
    FavouriteListAdapter favouriteListAdapter;
    private DeezerOpener deezerSongDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_track_list);
        deezerSongDb = new DeezerOpener(this);
        listFavouriteSongs = findViewById(R.id.songtrackList);
        //set listener to entries, click to enter DeezerSongDetailDelete
        listFavouriteSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeezerFavouriteList.this, DeezerSongDetailDelete.class);
                DeezerSongs selectedSong  = (DeezerSongs) parent.getItemAtPosition(position);
                intent.putExtra("songDetails", selectedSong);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //get all favourite songs from database
        songs = deezerSongDb.getFavouriteSongs();
        favouriteListAdapter = new FavouriteListAdapter(this, songs);
        listFavouriteSongs.setAdapter(favouriteListAdapter);
    }

    public class FavouriteListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<DeezerSongs> songs;

        FavouriteListAdapter(Context context, ArrayList<DeezerSongs> songs) {
            this.songs = songs;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_deezer_songs, parent, false);
            }
            //set the data in listview
            TextView textSongTitle = convertView.findViewById(R.id.textSongName);
            String songTitle = songs.get(position).getTitle();
            textSongTitle.setText(songTitle);
            return convertView;
        }
    }
}


