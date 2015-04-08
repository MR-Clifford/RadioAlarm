package xml_mike.radioalarm.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by MClifford on 22/02/15.
 */
public class MusicAlarm extends AlarmAbstract {

    public MusicAlarm(){super();}

    public MusicAlarm(Parcel in){
        super(in);
    }

    public static final Parcelable.Creator<MusicAlarm> CREATOR
            = new Parcelable.Creator<MusicAlarm>() {
        public MusicAlarm createFromParcel(Parcel in) {
            return new MusicAlarm(in);
        }

        public MusicAlarm[] newArray(int size) {
            return new MusicAlarm[size];
        }
    };

    public static View.OnClickListener getOnClickListener(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }
}
