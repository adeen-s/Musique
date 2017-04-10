package com.example.android.musique;

/**
 * Created by adeen-s on 21/3/17.
 * This class a song to be played
 */

public class Song implements Comparable<Song>{
    private long id;
    private String title;
    private String artist;
    private String album;
    private String  albumart;

    public Song(long songID, String songTitle, String songArtist, String  artID, String albumname) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        albumart = artID;
        album = albumname;
    }

    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String  getAlbumart() {
        return albumart;
    }

    public String getAlbum() {
        return album;
    }

    @Override
    public int compareTo(Song another) {
        return title.compareToIgnoreCase(another.title);
    }
}
