package xml_mike.radioalarm.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.managers.ThreadedMediaPlayer;

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

    /**
     *  Had Memory leak from unclosed cursor resolved, TODO note: on some devices do not have internal ring tones defined, as such will return an empty result
     * @param context
     * @param groupPosition
     * @return
     */
    @Override
    public View.OnClickListener getDataOnClickListener(final Context context, final int groupPosition) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"StandardAlarm", Toast.LENGTH_SHORT).show();

                final RingtoneManager ringtoneManager = new RingtoneManager(context);
                ringtoneManager.setType(RingtoneManager.TYPE_ALARM | RingtoneManager.TYPE_RINGTONE);
                Cursor cursor = ringtoneManager.getCursor();

                Log.e("Number of tones",""+cursor.getCount());

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if(cursor.getCount() > 0) {
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
                } else {
                    // if the original Ringtone manager could not find a ringtone show all available ringtones

                    Toast.makeText(Global.getInstance(), "No TONES", Toast.LENGTH_SHORT);

                    final RingtoneManager BackupRingtoneManager = new RingtoneManager(context);
                    BackupRingtoneManager.setType(RingtoneManager.TYPE_ALL);
                    cursor = BackupRingtoneManager.getCursor();

                    builder
                            .setTitle("Select Alarm")
                            .setSingleChoiceItems(cursor, -1, "title", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(context, ringtoneManager.getRingtoneUri(which).toString(), Toast.LENGTH_SHORT).show();
                                    StandardAlarm.this.setData(BackupRingtoneManager.getRingtoneUri(which).toString());
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
                }

                builder.create().show();
                Log.e("local","Total:"+cursor.getCount());
                cursor.close();

            }
        };
    }

    @Override
    public void setupAlarmData(Context context, ThreadedMediaPlayer mediaPlayer) throws IOException {
        mediaPlayer.changeDataSource(context, getData());
    }
}
