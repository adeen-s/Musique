package com.example.android.musique;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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

import com.example.android.musique.MusiqueService.MusicBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static SongAdapter songAdt;
    protected static boolean albumSelected = false;
    protected static boolean artistSelected = false;
    protected static long albumID;
    protected static String artistName;
    protected MusiqueService musicSrv;
    protected Intent playIntent, nowPlayingIntent, albumIntent, songPickedIntent;
    int textLength = 0;
    private ArrayList<Song> songList, songSearch;
    private ListView songView;
    private boolean musicBound = false;
    private String songName;
    private boolean songSelected = false;
    private DrawerLayout mDrawerLayout;
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        setNavigationViewListener();

        Connector.mMainActivity = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
// app-defined int constant

                return;
            }
        }
        songView = (ListView) findViewById(R.id.song_list_main);
        songList = new ArrayList<Song>();
        songSearch = new ArrayList<Song>();
        getSongList();
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        songAdt = new SongAdapter(this, songList);

        songView.setAdapter(songAdt);

        LinearLayout playingSong = (LinearLayout) findViewById(R.id.current_song);
        playingSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowPlayingIntent = new Intent(MainActivity.this, NowPlaying.class);
                // Start the new activity
                if (musicSrv != null && musicSrv.isPlaying()) {
                    startActivity(nowPlayingIntent);
                }

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.v("MainActivity", "Inside onCreate");

        Log.v("MainActivityOnCreate", "Now going to add textchanged listener");

        final EditText searchBox = (EditText) findViewById(R.id.EditText01);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                songSearch.clear();
                textLength = searchBox.getText().length();
                for (int i = 0; i < songList.size(); i++) {
                    if (textLength <= songList.get(i).getTitle().length()) {
                        if (searchBox.getText().toString().equalsIgnoreCase((String) songList.get(i).getTitle().subSequence(0, textLength))) {
                            songSearch.add(songList.get(i));
                        }
                    }
                }
                songView.setAdapter(new SongAdapter(getBaseContext(), songSearch));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Sidebar indexBar = (Sidebar) findViewById(R.id.sideBar);
        indexBar.setListView(songView);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v("MusiqueActivity", "Inside onStart();");
        if (playIntent == null) {
            playIntent = new Intent(this, MusiqueService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        if (musicSrv != null && musicSrv.isPlaying()) {
            setTitleInUi();
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Log.v("MainActivity", "AlbumSelected is = " + albumSelected);
    }


    public void setIconToPlay() {
        ImageView playButton = (ImageView) findViewById(R.id.play_button_main);
        playButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
    }

    public void setIconToPause() {
        ImageView playButton = (ImageView) findViewById(R.id.play_button_main);
        playButton.setImageResource(R.drawable.ic_pause_white_36dp);
    }

    public void songPicked(View view) {
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        startActivity(new Intent(MainActivity.this, NowPlaying.class));
    }

    public void setTitleInUi() {
        songName = musicSrv.getSongTitle();
        TextView playingSong = (TextView) findViewById(R.id.song_name_main);
        playingSong.setText(songName);
        Log.v("SongPicked", "Set song name to " + songName);
        TextView playingAlbum = (TextView) findViewById(R.id.album_name_main);
        playingAlbum.setText(musicSrv.getAlbumName());
    }

    public void getSongList() {
        if (albumSelected) {
            Log.v("getSongList", "ALBUM IS SELECTED");
            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                int albumidColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                //add songs to list
                do {
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    long thisAlbumid = musicCursor.getLong(albumidColumn);
                    String thisAlbumart = getCoverArtPath(thisAlbumid, this);
                    String thisAlbum = musicCursor.getString(albumColumn);
                    Log.v("GetSongList", "Value of albumID = " + albumID);
                    Log.v("GetSongList", "Value of thisalbumid = " + thisAlbumid);
                    if (thisAlbumid == albumID) {
                        songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbumart, thisAlbum));
                    }
                }
                while (musicCursor.moveToNext());
            }
        } else if (artistSelected) {
            Log.v("getSongList", "ARTIST IS SELECTED");
            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                int albumidColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                //add songs to list
                do {
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    long thisAlbumid = musicCursor.getLong(albumidColumn);
                    String thisAlbumart = getCoverArtPath(thisAlbumid, this);
                    String thisAlbum = musicCursor.getString(albumColumn);
                    Log.v("GetSongList", "Value of artist = " + artistName);
                    Log.v("GetSongList", "Value of thisartist = " + thisArtist);
                    if (thisArtist.equals(artistName)) {
                        songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbumart, thisAlbum));
                    }
                }
                while (musicCursor.moveToNext());
            }
        } else {
            Log.v("getSongList", "ALBUM NOT SELECTED");
            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                int albumidColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                //add songs to list
                do {
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    long thisAlbumid = musicCursor.getLong(albumidColumn);
                    String thisAlbumart = getCoverArtPath(thisAlbumid, this);
                    String thisAlbum = musicCursor.getString(albumColumn);
                    songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbumart, thisAlbum));
                }
                while (musicCursor.moveToNext());
            }
        }
    }


    private String getCoverArtPath(long albumId, Context context) {
        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_end:
                stopService(playIntent);
                musicSrv = null;
                System.exit(0);
                break;
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (musicBound) {
            unbindService(musicConnection);
            stopService(playIntent);
            musicSrv = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    public void play() {
        musicSrv.playPausedSong();
    }

    public void pause() {
        musicSrv.pausePlayingSong();
    }

    public void changeState(View view) {
        if (musicSrv.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_album: {
                openAlbumActivity();
                break;
            }

            case R.id.nav_songs: {
                albumID = -1;
                albumSelected = false;
                this.recreate();
                break;
            }

            case R.id.nav_artist: {
                Intent artistIntent = new Intent(MainActivity.this, ArtistActivity.class);
                startActivity(artistIntent);
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    void openAlbumActivity() {
        Log.v("MainActivity", "Call to open album Activity");
        albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
        startActivity(albumIntent);
    }

}
