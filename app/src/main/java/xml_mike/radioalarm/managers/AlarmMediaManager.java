package xml_mike.radioalarm.managers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xml_mike.radioalarm.AlarmActivity;
import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.parsers.FileParser;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.AlarmMedia;
import xml_mike.radioalarm.models.MusicAlarm;
import xml_mike.radioalarm.models.RadioAlarm;
import xml_mike.radioalarm.models.StandardAlarm;

/**
 * Created by MClifford on 09/04/15.
 * this will handle all the audio produced by the alarm
 */
public class AlarmMediaManager extends Service implements MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String ACTION_STOP = "com.example.action.STOP";
    MediaPlayer mMediaPlayer = null;
    WifiManager.WifiLock wifiLock = null;
    ExecutorService queue;

    public AlarmMediaManager() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) {

            queue = Executors.newFixedThreadPool(1);
            long alarmId = intent.getLongExtra("alarmId", -1L);
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);

            if (alarm.getId() >= 0L) {
                if(alarm instanceof RadioAlarm){
                    this.setupMediaplayer(alarm);
                }
                else {
                    this.setupMediaplayer(alarm);
                }
            }
        }else if (intent.getAction().equals(ACTION_STOP)){
            this.stopForeground(true);
            this.stopSelf();
        }

        return startId;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initMediaPlayer() {
        // ...initialize the MediaPlayer here...
        if (mMediaPlayer == null)
            this.mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnErrorListener(this);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("alarmMediaManager", "Failed"); //if media fails, request dump
        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
        if (wifiLock != null) wifiLock.release();

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) initMediaPlayer();
                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    public void setupMediaplayer(Alarm alarm){
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        wifiLock.acquire();
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        try {
            if(!alarm.getData().equals("")) {
                if (alarm instanceof StandardAlarm) {
                    String alarmdata = alarm.getData();//alarm_data.setText(alarms.get(groupPosition).getData());
                    Uri alarmToneName = Uri.parse(alarmdata);

                    mMediaPlayer.setDataSource(Global.getInstance().getApplicationContext(), alarmToneName);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepare();
                }
                if (alarm instanceof MusicAlarm) {
                    AlarmMedia alarmMedia = DatabaseManager.getInstance().getAlarmMedia(alarm.getData());
                    mMediaPlayer.setDataSource(alarmMedia.data);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.setOnPreparedListener(this);
                    mMediaPlayer.prepareAsync();
                }
                if (alarm instanceof RadioAlarm) {
                    final ExecutorService queue = Executors.newSingleThreadExecutor();
                    final String url = alarm.getData();
                    final AlarmMediaManager context = this;
                    final Runnable runner = new Runnable() {
                        @Override
                        public void run() {
                            context.onRadioDownLoad(FileParser.getURL(url));
                        }
                    };
                    queue.execute(runner);
                }
            }
            else {
                Uri alarmToneName = Uri.parse("content://media/internal/audio/media/1");
                mMediaPlayer.setDataSource(Global.getInstance().getApplicationContext(), alarmToneName);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Log.e(this.getClass().toString(),e.toString());
        }

        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
         // prepare async to not block main thread

        // assign the song name to songName
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), AlarmActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification();
        notification.tickerText = alarm.getName();
        notification.icon = R.drawable.abc_textfield_search_material;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "Alarm",
                "Playing: " + alarm.getData(), pi);
        startForeground(alarm.getIntId(), notification);
    }

    private void onRadioDownLoad(String realUrl){
        try {
            mMediaPlayer.setDataSource(realUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();
    }

}