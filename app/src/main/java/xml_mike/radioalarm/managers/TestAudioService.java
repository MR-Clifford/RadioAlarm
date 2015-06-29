package xml_mike.radioalarm.managers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by MClifford on 08/06/15.
 * @author MClifford
 *
 * this is intended to test the current radio station to see if the stream url provided actually has an audioble service. ideally called from
 * RadioSelectActivity
 */
public class TestAudioService extends Service implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener{

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String ACTION_STOP = "com.example.action.STOP";
    private static final String ACTION_PAUSE = "com.example.action.PAUSE";
    private WifiManager.WifiLock wifiLock = null; //needed for connection
    private MediaPlayer mediaPlayer = null;
    private String currentTrackUrl = ""; //the main

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("test service",currentTrackUrl);

        if (intent.getAction().equals(ACTION_PLAY)) {
            //wifiQueue = Executors.newFixedThreadPool(1);

            currentTrackUrl = intent.getStringExtra("test");

            if(mediaPlayer == null)
                initialiseMediaPlayer();
            else {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer.reset();
            }

            try {
                mediaPlayer.setDataSource(currentTrackUrl);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            if(mediaPlayer != null) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            }
        }

        return startId;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initialiseMediaPlayer(){
        //AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        //audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        wifiLock.acquire();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }
}
