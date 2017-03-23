package com.example.android.musique;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adeen-s on 21/3/17.
 * Custom BaseAdapter to display the list of songs
 */
public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.song, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        TextView albumView = (TextView)songLay.findViewById(R.id.song_album);
        ImageView albumart = (ImageView) songLay.findViewById(R.id.album_art);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        albumView.setText(currSong.getAlbum());
        if (currSong.getAlbumart() != null) {
            Bitmap bmp = BitmapFactory.decodeFile(currSong.getAlbumart());
            albumart.setImageBitmap(bmp);
        } else {
            albumart.setImageResource(R.drawable.ic_music_video_black_48dp);
        }
        //Log.v("ALBUMART","Location-->"+currSong.getAlbumart());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}