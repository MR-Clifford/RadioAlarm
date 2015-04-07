package xml_mike.radioalarm.models;

import android.os.Parcel;
import android.os.Parcelable;

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
}
