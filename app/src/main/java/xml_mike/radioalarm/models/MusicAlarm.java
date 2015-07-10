package xml_mike.radioalarm.models;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.io.IOException;

import xml_mike.radioalarm.controllers.ManageActivity;
import xml_mike.radioalarm.controllers.MusicSelectActivity;
import xml_mike.radioalarm.managers.DatabaseManager;
import xml_mike.radioalarm.managers.ThreadedMediaPlayer;

/**
 * Created by MClifford on 22/02/15.
 *
 * handles single song alarm, sets on repeat & implements strategy pattern
 */
public class MusicAlarm extends AlarmAbstract {

    public static final Parcelable.Creator<MusicAlarm> CREATOR
            = new Parcelable.Creator<MusicAlarm>() {
        public MusicAlarm createFromParcel(Parcel in) {
            return new MusicAlarm(in);
        }

        public MusicAlarm[] newArray(int size) {
            return new MusicAlarm[size];
        }
    };

    public MusicAlarm(){super();}

    public MusicAlarm(Parcel in){
        super(in);
    }

    @Override
    public View.OnClickListener getDataOnClickListener(final Context context, final int groupPosition) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MusicSelectActivity.class);
                ((ManageActivity) context).startActivityForResult(intent, groupPosition);
            }
        };
    }

    @Override
    public void setupAlarmData(Context context, ThreadedMediaPlayer mediaPlayer) throws IOException {
        AlarmMedia alarmMedia = DatabaseManager.getInstance().getAlarmMedia(getData());
        mediaPlayer.changeDataSource(alarmMedia.data);
    }
}
