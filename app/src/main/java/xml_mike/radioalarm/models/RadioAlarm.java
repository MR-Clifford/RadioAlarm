package xml_mike.radioalarm.models;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.View;

import xml_mike.radioalarm.controllers.ManageActivity;
import xml_mike.radioalarm.controllers.RadioSelectActivity;
import xml_mike.radioalarm.managers.RadioStationsManager;
import xml_mike.radioalarm.managers.ThreadedMediaPlayer;
import xml_mike.radioalarm.managers.parsers.FileParser;

/**
 * Created by MClifford on 22/02/15.
 */
public class RadioAlarm extends AlarmAbstract {

    public static final Parcelable.Creator<RadioAlarm> CREATOR
            = new Parcelable.Creator<RadioAlarm>() {
        public RadioAlarm createFromParcel(Parcel in) {
            return new RadioAlarm(in);
        }

        public RadioAlarm[] newArray(int size) {
            return new RadioAlarm[size];
        }
    };

    public RadioAlarm() {super();}

    public RadioAlarm(Parcel in){
        super(in);
    }

    @Override
    public View.OnClickListener getDataOnClickListener(final Context context, final int groupPosition) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "RadioAlarm", Toast.LENGTH_SHORT).show();
                //alarms.get(groupPosition).setData("http://www.internet-radio.com/servers/tools/playlistgenerator/?u=http://205.164.36.17:80/listen.pls&t=.pls");
                //alarms.get(groupPosition).setData("http://open.live.bbc.co.uk/mediaselector/5/select/version/2.0/mediaset/http-icy-aac-lc-a/format/pls/vpid/bbc_radio_three.pls");
                Intent intent = new Intent(context, RadioSelectActivity.class);
                ((ManageActivity) context).startActivityForResult(intent, groupPosition);
            }
        };
    }

    @Override
    public void setupAlarmData(final Context context, final ThreadedMediaPlayer mediaPlayer) throws java.io.IOException {
        //final ExecutorService queue = Executors.newSingleThreadExecutor();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null){
            if(getData().contains(".pls") || getData().contains(".m3u") || getData().contains(".asx") || getData().contains(".ashx")) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String realUrl = FileParser.getURL(RadioAlarm.this.getData());
                        mediaPlayer.changeDataSource(realUrl);
                    }
                });
                thread.start();
            } else {
                RadioStation radioStation = RadioStationsManager.retrieveRadioStation(Long.parseLong(getData()));
                mediaPlayer.changeDataSource(radioStation.getStreams().get(0).url); //grab first stream associated to internet radio station TODO allow user to choose stream
            }
        }
        else
            mediaPlayer.changeDataSource(context,Settings.System.DEFAULT_ALARM_ALERT_URI); //if phone is not connected to the internet play phones default alarm tone.
    }
}
