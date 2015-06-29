package xml_mike.radioalarm.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.managers.AlarmsManager;

/**
 * Created by MClifford on 23/02/15.
 */
public class StandardAlarm extends AlarmAbstract {

    public static final Parcelable.Creator<StandardAlarm> CREATOR
            = new Parcelable.Creator<StandardAlarm>() {
        public StandardAlarm createFromParcel(Parcel in) {
            return new StandardAlarm(in);
        }

        public StandardAlarm[] newArray(int size) {
            return new StandardAlarm[size];
        }
    };

    public StandardAlarm() {super();}

    public StandardAlarm(Parcel in){
        super(in);
    }

    @Override
    public View.OnClickListener getDataOnClickListener(final Context context, final int groupPosition) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"StandardAlarm", Toast.LENGTH_SHORT).show();

                final RingtoneManager ringtoneManager = new RingtoneManager(context);
                ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
                Cursor cursor = ringtoneManager.getCursor();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setTitle("Select Alarm")
                        .setSingleChoiceItems(cursor, -1, "title", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(context, ringtoneManager.getRingtoneUri(which).toString(), Toast.LENGTH_SHORT).show();
                                StandardAlarm.this.setData(ringtoneManager.getRingtoneUri(which).toString());
                                AlarmsManager.getInstance().update(groupPosition, StandardAlarm.this, false);
                                AlarmsManager.getInstance().notifyObservers();
                            }
                        })
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlarmsManager.getInstance().notifyObservers();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlarmsManager.getInstance().notifyObservers();
                            }
                        });

                builder.create().show();
            }
        };
    }

    @Override
    public void setupAlarmData(Context context, MediaPlayer mMediaPlayer) throws IOException {
        Log.e("Before:",getData());
        Uri alarmToneName = Uri.parse(getData());
        mMediaPlayer.setDataSource(Global.getInstance().getApplicationContext(), alarmToneName);
        mMediaPlayer.setOnPreparedListener(this.getOnPreparedListener());
        mMediaPlayer.setLooping(true);
        mMediaPlayer.prepare();
        Log.e("after:", alarmToneName.toString());
    }
}
