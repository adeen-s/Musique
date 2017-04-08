package com.example.android.musique;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by adeen-s on 21/3/17.
 * Service to allow background playback of music
 */

public class MusiqueService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final int NOTIFY_ID = 1;
    private final IBinder musicBind = new MusicBinder();
    //media player
    protected MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private String songTitle = "";
    private boolean shuffle = false;
    private Random rand;

    public void onCreate() {
        super.onCreate();
        songPosn = 0;
        player = new MediaPlayer();
        initMusicPlayer();
        rand = new Random();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.reset();
        player.release();
        return false;
    }

    public int getDuration() {
        return player.getDuration();
    }

    public String getSongTitle() {
        Song playSong = songs.get(songPosn);
        return playSong.getTitle().toString();
    }

    public String getAlbumName() {
        Song playSong = songs.get(songPosn);
        return playSong.getAlbum();
    }

    public String getAlbumart() {
        Song playSong = songs.get(songPosn);
        return playSong.getAlbumart();
    }

    public String getArtist() {
        Song playSong = songs.get(songPosn);
        return playSong.getArtist();
    }

    public boolean playSong() {
        player.reset();
        Song playSong = songs.get(songPosn);
        songTitle = playSong.getTitle();
        long currSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("---MusiqueService---", "Error setting data source", e);
        }
        player.prepareAsync();
        if (Connector.mMainActivity != null) {
            Connector.mMainActivity.setTitleInUi();
            Connector.mMainActivity.setIconToPause();
        }
        if (Connector.mAlbumActivity != null) {
            Connector.mAlbumActivity.setTitleInUi();
            Connector.mAlbumActivity.setIconToPause();
        }
        if (Connector.mArtistActivity != null) {
            Connector.mArtistActivity.setTitleInUi();
            Connector.mArtistActivity.setIconToPause();
        }
        if (Connector.mNowPlaying != null) {
            Connector.mNowPlaying.updateUi();
            Connector.mNowPlaying.setIconToPause();
        }
        return true;
    }

    public void playPausedSong() {
        player.start();
        if (Connector.mMainActivity != null) {
            Connector.mMainActivity.setIconToPause();
        }
        if (Connector.mArtistActivity != null) {
            Connector.mArtistActivity.setIconToPause();
        }
        if (Connector.mAlbumActivity != null) {
            Connector.mAlbumActivity.setIconToPause();
        }
        if (Connector.mNowPlaying != null) {
            Connector.mNowPlaying.setIconToPause();
        }
    }

    public void pausePlayingSong() {
        player.pause();
        if (Connector.mMainActivity != null) {
            Connector.mMainActivity.setIconToPlay();
        }
        if (Connector.mArtistActivity != null) {
            Connector.mArtistActivity.setIconToPlay();
        }
        if (Connector.mAlbumActivity != null) {
            Connector.mAlbumActivity.setIconToPlay();
        }
        if (Connector.mNowPlaying != null) {
            Connector.mNowPlaying.setIconToPlay();
        }
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.v("onPreparedMusiqueServic", "--------->>>>>>in music service on prepared<<<<<<<<<--------");

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_play_arrow_white_36dp)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
        Intent onPreparedIntent = new Intent("MEDIA_PLAYER_PREPARED");
        LocalBroadcastManager.getInstance(this).sendBroadcast(onPreparedIntent);
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public void playNext() {
        if (shuffle) {
            int newSong = songPosn;
            while (newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        } else {
            songPosn++;
            if (songPosn >= songs.size()) songPosn = 0;
        }
        playSong();
    }

    public void playPrev() {
        if (shuffle) {
            int newSong = songPosn;
            while (newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        } else {
            songPosn--;
            if (songPosn < 0) {
                songPosn = songs.size() - 1;
            }
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setShuffle() {
        if (shuffle) {
            shuffle = false;
            Toast.makeText(this, "Shuffle is disabled", Toast.LENGTH_SHORT).show();
        } else {
            shuffle = true;
            Toast.makeText(this, "Shuffle is enabled", Toast.LENGTH_SHORT).show();
        }
    }

    public class MusicBinder extends Binder {
        MusiqueService getService() {
            return MusiqueService.this;
        }
    }
}
