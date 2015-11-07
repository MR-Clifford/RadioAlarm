package xml_mike.radioalarm.managers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by MClifford on 08/06/15.
 * @author MClifford
 *
 * this is intended to test the current radio station to see if the stream url provided actually has an audioble service. ideally called from
 * RadioSelectActivity
 */
public class TestAudioService extends Service implements AudioService {

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String ACTION_STOP = "com.example.action.STOP";
    private static final String ACTION_PAUSE = "com.example.action.PAUSE";
    private final IBinder mBinder = new LocalBinder();
    private WifiManager.WifiLock wifiLock = null; //needed for connection
    //private MediaPlayer mediaPlayer = null;
    private ThreadedMediaPlayer threadedMediaPlayer;
    private String currentTrackUrl = ""; //the main

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("test service",currentTrackUrl);

        if (intent.getAction().equals(ACTION_PLAY)) {
            //wifiQueue = Executors.newFixedThreadPool(1);

            currentTrackUrl = intent.getStringExtra("test");

        }

        return startId;
    }

    @Override
    public IBinder onBind(Intent intent) {

        threadedMediaPlayer = new ThreadedMediaPlayer();
        return mBinder;
    }

    private void initialiseMediaPlayer(){
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        wifiLock.acquire();
    }

    @Override
    public void stopAudio(){
        threadedMediaPlayer.stop();
    }

    @Override
    public void startAudio(String path, TextView view) {
        threadedMediaPlayer.changeDataSource(path, view);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        threadedMediaPlayer.stop();
        threadedMediaPlayer.release();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public TestAudioService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TestAudioService.this;
        }
    }
}
