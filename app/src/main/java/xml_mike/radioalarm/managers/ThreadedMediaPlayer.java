package xml_mike.radioalarm.managers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import android.widget.MediaController;

import java.io.IOException;

import xml_mike.radioalarm.Global;

/**
 * Created by MClifford on 29/06/15.
 *
 * Encapsulation of media player, all calls will be run on a thread, so will heavily rely on callbacks.
 * Ideally this will handle all media audio throughout the app
 */
public class ThreadedMediaPlayer implements MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    MediaPlayer mediaplayer;

    public ThreadedMediaPlayer(){
        initialiseMediaPlayer();
    }

    @Override
    public void start() {
        mediaplayer.setWakeMode(Global.getInstance(), PowerManager.PARTIAL_WAKE_LOCK);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaplayer.start();
            }
        });
        thread.start();
    }

    @Override
    public void pause() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mediaplayer.pause();
            }
        });
        thread.start();
    }

    @Override
    public int getDuration() {
        return mediaplayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaplayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        //not implemented
    }

    @Override
    public boolean isPlaying() {
        return mediaplayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return mediaplayer.getAudioSessionId();
    }



    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaplayer == null) initialiseMediaPlayer();
                else if (!mediaplayer.isPlaying()) mediaplayer.start();
                mediaplayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaplayer.isPlaying()) mediaplayer.stop();
                mediaplayer.release();
                mediaplayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaplayer.isPlaying()) mediaplayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaplayer.isPlaying()) mediaplayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private void initialiseMediaPlayer(){

        mediaplayer = new MediaPlayer();
        AudioManager audioManager = (AudioManager) Global.getInstance().getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaplayer.setOnPreparedListener(this);
        mediaplayer.setOnErrorListener(this);
        mediaplayer.setLooping(true);
        mediaplayer.setVolume(1f, 1f);
        mediaplayer.setWakeMode(Global.getInstance(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    public void setVolume(final float volume){

        try {
            mediaplayer.setVolume(volume, volume);
        } catch(IllegalStateException e) {
            Log.e("TEST","error");
        }

    }

    public void stop(){
        // Thread thread = new Thread(new Runnable() {
        // @Override
        //  public void run() {
        mediaplayer.stop();
        //mediaplayer.release();
        //mediaplayer.setWakeMode(Global.getInstance(), PowerManager.RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY);
        //    }
        //});
        //thread.start();
    }

    public void changeDataSource(final String location){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(mediaplayer.isPlaying())//if the media player is playing stop current tracks
                    mediaplayer.stop();

                try {
                    mediaplayer.reset();
                    mediaplayer.setLooping(true);
                    mediaplayer.setDataSource(location);
                    mediaplayer.prepareAsync();
                } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void changeDataSource(final Context context,final  String uri){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(mediaplayer.isPlaying()) //if the media player is playing stop current tracks
                    mediaplayer.stop();

                try {
                    Uri uriConverted = Uri.parse(uri);
                    mediaplayer.reset();
                    mediaplayer.setLooping(true);
                    mediaplayer.setDataSource(context, uriConverted);
                    mediaplayer.prepareAsync();
                } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void changeDataSource(final Context context, final  Uri uri){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(mediaplayer.isPlaying())  //if the media player is playing stop current tracks
                    mediaplayer.stop();

                try {
                    //Uri uriConverted = Uri.parse(uri);
                    mediaplayer.reset();
                    mediaplayer.setLooping(true);
                    mediaplayer.setDataSource(context, uri);
                    mediaplayer.prepareAsync();

                } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void release(){
        mediaplayer.release();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("ThreadedMediaPlayer",""+what);
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mediaplayer != null)
            mediaplayer.release();
    }
}
