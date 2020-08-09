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
    private final String COL_SONG_ID = "id";
    private final String COL_SONG_TITLE = "title";
    private final String COL_SONG_ALBUM_NAME = "album";
    private final String COL_SONG_DURATION = "duration";
    private final String COL_SONG_ALBUM_COVER_PATH = "cover_path";

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
                COL_SONG_ALBUM_COVER_PATH + " TEXT" +
                ")";
        db.execSQL(createQuery);
    }

    @Override
    //upgrade and refrech table
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_FAVOURITE_SONGS);
        onCreate(db);
    }

    //get all favourite songs
    public ArrayList<DeezerSongs> getFavouriteSongs() {
        ArrayList<DeezerSongs> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITE_SONGS,
                new String[]{
                        COL_SONG_ID, COL_SONG_TITLE,
                        COL_SONG_ALBUM_NAME,
                        COL_SONG_DURATION,
                        COL_SONG_ALBUM_COVER_PATH
                },
                null, null,
                null, null, null, null);
        //check for the first item, if it exists or not
        if (cursor.moveToFirst()) {
            do {
                DeezerSongs song = new DeezerSongs();
                long id = cursor.getLong(0);
                String title = cursor.getString(1);
                String albumName = cursor.getString(2);
                int duration = cursor.getInt(3);
                String album_cover = cursor.getString(4);
                song.setId(id);
                song.setTitle(title);
                song.setCoverURL(album_cover);
                song.setAlbum(albumName);
                song.setDuration(duration);
                songs.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }

    //insert a song's detail in the database
    public void insertSong(DeezerSongs song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SONG_ID, song.getId());
        contentValues.put(COL_SONG_TITLE, song.getTitle());
        contentValues.put(COL_SONG_ALBUM_NAME, song.getAlbum());
        contentValues.put(COL_SONG_ALBUM_COVER_PATH, song.getCoverURL());
        contentValues.put(COL_SONG_DURATION, song.getDuration());
        db.insert(TABLE_FAVOURITE_SONGS, null, contentValues);
        db.close();
    }

    //remove a song from the database
    public void deleteSong(Long songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVOURITE_SONGS, COL_SONG_ID + " = ?",
                new String[]{String.valueOf(songId)});
        db.close();
    }


}
