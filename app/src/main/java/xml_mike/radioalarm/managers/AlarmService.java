package xml_mike.radioalarm.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;

import xml_mike.radioalarm.AlarmActivity;
import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.R;
import xml_mike.radioalarm.StaticWakeLock;
import xml_mike.radioalarm.models.Alarm;

/**
 * Created by MClifford on 09/04/15.
 * this will handle all the audio produced by the alarm
 */
public class AlarmService extends Service implements MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private static final String ACTION_STOP = "com.example.action.STOP";
    private static final String ACTION_PAUSE = "com.example.action.PAUSE";
    //private int maxVolume = 15; //android default
    private MediaPlayer mMediaPlayer = null;
    private WifiManager.WifiLock wifiLock = null;
    private EasingThread easingQueue;
    private DurationThread durationQueue;
    private Alarm alarm = null ;
    private Vibrator vibrator = null;
    private Notification.Builder notificationBuilder = null;
    private NotificationManager notificationManager = null;

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) {

            //wifiQueue = Executors.newFixedThreadPool(1);
            long alarmId = intent.getLongExtra("alarmId", -1L);
            alarm = AlarmsManager.getInstance().getAlarm(alarmId);
            vibrator= (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            if(alarm.isVibrate() && vibrator.hasVibrator()) {
                long[] pattern = {0, 300, 1000};
                vibrator.vibrate(pattern, 0);
            }

            notificationBuilder = generateNotification();
            updateNotification("none");

            easingQueue = new EasingThread();
            durationQueue=  new DurationThread();

            this.setupMediaPlayer(alarm);

        }else if (intent.getAction().equals(ACTION_STOP)){

            if(easingQueue != null)
                easingQueue.cancel(true);
            if(durationQueue != null)
                durationQueue.cancel(true);

            if(vibrator !=null)
                vibrator.cancel();

            StaticWakeLock.lockOff(this);
            this.stopForeground(true);
            this.stopSelf();
        }
        else if(intent.getAction().equals(ACTION_PAUSE)){
            //TODO, if user pauses audio, use this.
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

    private void initMediaPlayer() {
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

    private void setupMediaPlayer(Alarm alarm){
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        wifiLock.acquire();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

        try {
            if(!alarm.getData().equals("") || !alarm.getData().isEmpty()) {
                alarm.setupAlarmData(Global.getInstance().getApplicationContext(), mMediaPlayer);
            }
            else {
                mMediaPlayer.setDataSource(Global.getInstance().getApplicationContext(), Settings.System.DEFAULT_ALARM_ALERT_URI);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            Log.e(this.getClass().toString(),e.toString());
        }

        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        // prepare async to not block main thread

        //allow multiple threads to run depending on android OS version.
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            easingQueue.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            durationQueue.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            easingQueue.execute();
            durationQueue.execute();
        }
    }

    /**
     * will change depending on android version
     */
    protected Notification.Builder generateNotification(){

        // assign the song name to songName
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), AlarmActivity.class), 0);

        Notification.Builder nBuilder = new Notification.Builder(this)
                //.setTicker(alarm.getName())
                .setSmallIcon(R.drawable.ic_drawer)
                .setAutoCancel(true)
                .setContentTitle(alarm.getName()+""+alarm.getData())
                .setContentText("")
                .setContentIntent(pi);

        nBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        // Gets an instance of the NotificationManager service

        return nBuilder;
    }

    /**
     *
     */
    protected void updateNotification(String updateString){
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationBuilder.setContentText(updateString);
        mNotifyMgr.notify(alarm.getIntId(), notificationBuilder.build());
    }

    private class EasingThread extends AsyncTask<String , String, String> {

        @Override
        protected String doInBackground(String... string) {
           synchronized (this) {
               if (mMediaPlayer != null && alarm != null) {
                   if (alarm.getEasing() != 0) {
                       int timing = ((1000 * 60 * alarm.getEasing()) / 100);
                       float easing = 0.00f;
                       for (int i = 0; i < 100; i++) {
                           if (!isCancelled()) {
                               easing += 0.01f;//*maxVolume;
                               mMediaPlayer.setVolume(easing, easing);
                               android.os.SystemClock.sleep(timing);
                               //Log.i("Service",maxVolume +" increase Volume to " + easing);
                           } else {
                               break;
                           }
                       }
                   } else
                       mMediaPlayer.setVolume(1.0f, 1.0f);
               }
           }
            return "";
        }
    }

    private class DurationThread extends AsyncTask<String , String, String>  {
        @Override
        protected String doInBackground(String... params) {

            synchronized (this) {
                if (mMediaPlayer != null && alarm != null) {
                    int duration = alarm.getDuration() * 60; //time in minutes from milliseconds
                    int oneSecond = 1000; //One second in milliseconds

                    for (int i = 0; i < duration; i++) {
                        if (!isCancelled()) {
                            Log.e("Update Notification", ":" + (duration - i));
                            android.os.SystemClock.sleep(oneSecond);
                            AlarmService.this.updateNotification("TimeLeft:" + (duration - i));
                        } else {
                            break;
                        }
                    }
                    //android.os.SystemClock.sleep(duration);

                    Intent intent = new Intent(getBaseContext(), AlarmService.class);
                    intent.setAction("com.example.action.STOP");
                    startService(intent);
                    Log.e("Service", "Stopped");
                }
            }
            return "";
        }
    }
}