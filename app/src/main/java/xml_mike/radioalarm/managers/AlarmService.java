package xml_mike.radioalarm.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xml_mike.radioalarm.AlarmActivity;
import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.R;
import xml_mike.radioalarm.StaticWakeLock;
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
public class AlarmService extends Service implements MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String ACTION_STOP = "com.example.action.STOP";
    private static final String ACTION_PAUSE = "com.example.action.PAUSE";
    MediaPlayer mMediaPlayer = null;
    WifiManager.WifiLock wifiLock = null;
    ExecutorService wifiQueue;
    EasingThread easingQueue;
    DurationThread durationQueue;
    Alarm alarm = null ;

    public AlarmService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) {

            wifiQueue = Executors.newFixedThreadPool(1);
            long alarmId = intent.getLongExtra("alarmId", -1L);
            alarm = AlarmsManager.getInstance().getAlarm(alarmId);

            easingQueue = new EasingThread();
            durationQueue=  new DurationThread();

            if (alarm.getId() >= 0L) {
                if(alarm instanceof RadioAlarm)
                    this.setupMediaplayer(alarm);
                else
                    this.setupMediaplayer(alarm);
            }
        }else if (intent.getAction().equals(ACTION_STOP)){

            if(easingQueue != null || durationQueue != null) {
                easingQueue.cancel(true);
                durationQueue.cancel(true);
            }
            StaticWakeLock.lockOff(this);
            this.stopForeground(true);
            this.stopSelf();
        }
        else if(intent.getAction().equals(ACTION_PAUSE)){

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

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        try {
            if(!alarm.getData().equals("") || !isNetworkConnected()) {
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
                    final AlarmService context = this;
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
                new Intent(getApplicationContext(), AlarmActivity.class), 0);

        Notification.Builder nBuilder = new Notification.Builder(this)
                //.setTicker(alarm.getName())
                .setSmallIcon(R.drawable.ic_drawer)
                .setAutoCancel(true)
                .setContentTitle("Alarm:"+alarm.getTimeHour()+":"+alarm.getTimeMinute())
                .setContentText("" + alarm.getTimeHour() + alarm.getTimeMinute())
                .setContentIntent(pi);

                nBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.

        mNotifyMgr.notify(0, nBuilder.build());


        //startForeground(alarm.getIntId(), notification);

        easingQueue.execute();
        durationQueue.execute();
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private class EasingThread extends AsyncTask<String , String, String> {

        @Override
        protected String doInBackground(String... string) {

            if(mMediaPlayer !=null && alarm != null){
                if(alarm.getEasing() != 0) {
                    int timing = (1000 * 60 * alarm.getEasing())/100;
                    float easing = 0.00f;
                    for(int i = 0; i < 100; i++) {
                        easing += 0.01f;
                        mMediaPlayer.setVolume(easing, easing);
                        android.os.SystemClock.sleep(timing);
                        Log.e("Service", "increase Volume");
                    }
                } else
                    mMediaPlayer.setVolume(1.0F, 1.0F);
            }
            return "";
        }
    }

    private class DurationThread extends AsyncTask<String , String, String>  {
        @Override
        protected String doInBackground(String... params) {
            int duration = alarm.getDuration() * 60 * 1000; //time in minutes from milliseconds
            android.os.SystemClock.sleep(duration);

            Intent intent = new Intent(getBaseContext(), AlarmService.class);
            intent.setAction("com.example.action.STOP");
            startService(intent);
            Log.e("Service","Stop");
            return null;
        }
    }
}