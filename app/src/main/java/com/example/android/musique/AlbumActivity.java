package com.example.android.musique;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlbumActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Album> albumList, albumSearch;
    private MusiqueService musicSrv = Connector.mMainActivity.musicSrv;
    private Intent nowPlayingIntent;
    private String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        setNavigationViewListener();


        Connector.mAlbumActivity = this;
        final ListView albumView = (ListView) findViewById(R.id.album_list_album);
        albumSearch = new ArrayList<Album>();
        albumList = new ArrayList<Album>();
        getAlbumsLists();
        Collections.sort(albumList, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                return a.getAlbumName().compareTo(b.getAlbumName());
            }
        });
        AlbumAdapter albumAdt = new AlbumAdapter(this, albumList);
        albumView.setAdapter(albumAdt);

        LinearLayout playingSong = (LinearLayout) findViewById(R.id.current_song_album);
        playingSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowPlayingIntent = new Intent(AlbumActivity.this, NowPlaying.class);
                // Start the new activity
                if (musicSrv != null && musicSrv.isPlaying()) {
                    startActivity(nowPlayingIntent);
                }

            }
        });

        if (Connector.mMainActivity.musicSrv.isPlaying()) {
            setTitleInUi();
            setIconToPause();
        }

        final EditText searchBox = (EditText) findViewById(R.id.EditTextAlbum);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                albumSearch.clear();
                int textLength = searchBox.getText().length();
                for (int i = 0; i < albumList.size(); i++) {
                    if (textLength <= albumList.get(i).getAlbumName().length()) {
                        if (searchBox.getText().toString().equalsIgnoreCase((String) albumList.get(i).getAlbumName().subSequence(0, textLength))) {
                            albumSearch.add(albumList.get(i));
                        }
                    }
                }
                albumView.setAdapter(new AlbumAdapter(getBaseContext(), albumSearch));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Sidebar indexBar = (Sidebar) findViewById(R.id.sideBarAlbum);
        indexBar.setListView(albumView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_end:
                stopService(Connector.mMainActivity.playIntent);
                musicSrv = null;
                System.exit(0);
                break;
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeStateAlbum(View view) {
        if (musicSrv.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public void play() {
        musicSrv.playPausedSong();
    }

    public void pause() {
        musicSrv.pausePlayingSong();
    }

    public void getAlbumsLists() {
        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumart = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;

        final String[] columns = {_id, album_name, artist, albumart, tracks};
        Cursor cursor = this.getContentResolver().query(uri, columns, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                long id = cursor.getLong(cursor.getColumnIndex(_id));
                String name = cursor.getString(cursor.getColumnIndex(album_name));
                String artist2 = cursor.getString(cursor.getColumnIndex(artist));
                String artPath = cursor.getString(cursor.getColumnIndex(albumart));
                Bitmap art = BitmapFactory.decodeFile(artPath);
                int nr = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));
                Log.v("AlbumList", "Album ID = " + id);

                albumList.add(new Album(id, name, artist2, art, nr));

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    public void setTitleInUi() {
        songName = musicSrv.getSongTitle();
        TextView playingSong = (TextView) findViewById(R.id.song_name_album);
        playingSong.setText(songName);
        Log.v("SongPicked", "Set song name to " + songName);
        TextView playingAlbum = (TextView) findViewById(R.id.album_name_album);
        playingAlbum.setText(musicSrv.getAlbumName());
    }

    public void setIconToPlay() {
        ImageView playButton = (ImageView) findViewById(R.id.play_button_album);
        playButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
    }

    public void setIconToPause() {
        ImageView playButton = (ImageView) findViewById(R.id.play_button_album);
        playButton.setImageResource(R.drawable.ic_pause_white_36dp);
    }

    public void albumPicked(View view) {
        Connector.mMainActivity.albumSelected = true;
        Log.v("AlbumActivity", "AlbumSelected = " + Connector.mMainActivity.albumSelected);
        Connector.mMainActivity.albumID = (Integer.parseInt(view.getTag().toString()));
        Log.v("AlbumActivity", "---Going to main activity now---");
        Intent t = new Intent(AlbumActivity.this, MainActivity.class);
        startActivity(t);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_album: {
                break;
            }

            case R.id.nav_songs: {
                Connector.mMainActivity.albumID = -1;
                Connector.mMainActivity.albumSelected = false;
                Intent t = new Intent(AlbumActivity.this, MainActivity.class);
                startActivity(t);
            }

            case R.id.nav_artist: {
                Intent artistIntent = new Intent(AlbumActivity.this, ArtistActivity.class);
                startActivity(artistIntent);
                break;
            }
        }
        //close navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

}
