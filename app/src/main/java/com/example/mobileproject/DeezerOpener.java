package com.example.mobileproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DeezerOpener extends SQLiteOpenHelper {

    //table name
    private final String TABLE_FAVOURITE_SONGS = "favourite_songs";
    //column names
    private final String COL_SONG_ID = "ID";
    private final String COL_SONG_TITLE = "TITLE";
    private final String COL_SONG_ALBUM_NAME = "ALBUM";
    private final String COL_SONG_DURATION = "DURATION";
    private final String COL_SONG_ALBUM_COVERURL = "COVERPATH";

    //initialize DeezerOpener
    public DeezerOpener(Context context) {
        super(context, "DEEZER_SONG_DATABASE", null, 1);
    }

    @Override
    //create table
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_FAVOURITE_SONGS + "(" +
                COL_SONG_ID + " TEXT PRIMARY KEY," +
                COL_SONG_TITLE + " TEXT," +
                COL_SONG_ALBUM_NAME + " TEXT," +
                COL_SONG_DURATION + " TEXT," +
                COL_SONG_ALBUM_COVERURL + " TEXT" +
                ")";
        db.execSQL(createQuery);
    }

    @Override
    //upgrade and refresh table
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_FAVOURITE_SONGS);
        onCreate(db);
    }

    //get all favourite songs
    public ArrayList<DeezerSongs> getFavouriteSongs() {
        ArrayList<DeezerSongs> songs = new ArrayList<>();
        //Gets the data repository in read mode
        SQLiteDatabase db = this.getReadableDatabase();

        //access to each row in database
        Cursor cursor = db.query(TABLE_FAVOURITE_SONGS, new String[]{COL_SONG_ID, COL_SONG_TITLE, COL_SONG_ALBUM_NAME, COL_SONG_DURATION, COL_SONG_ALBUM_COVERURL},
                null, null, null, null, null, null);

        //check for the first item, if it exists or not
        if (cursor.moveToFirst()) {
            do {
                DeezerSongs song = new DeezerSongs();
                song.setId(cursor.getLong(0));
                song.setTitle(cursor.getString(1));
                song.setAlbum(cursor.getString(2));
                song.setDuration(cursor.getInt(3));
                song.setCoverURL(cursor.getString(4));
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    //insert a song's detail in the database
    public void insertSong(DeezerSongs song) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COL_SONG_ID, song.getId());
        values.put(COL_SONG_TITLE, song.getTitle());
        values.put(COL_SONG_ALBUM_NAME, song.getAlbum());
        values.put(COL_SONG_ALBUM_COVERURL, song.getCoverURL());
        values.put(COL_SONG_DURATION, song.getDuration());
        // Insert the new row, returning the primary key value of the new row
        db.insert(TABLE_FAVOURITE_SONGS, null, values);
        db.close();
    }

    //remove a song from the database
    public void deleteSong(Long songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITE_SONGS, COL_SONG_ID + " = ?", new String[]{String.valueOf(songId)});
        db.close();
    }
}
