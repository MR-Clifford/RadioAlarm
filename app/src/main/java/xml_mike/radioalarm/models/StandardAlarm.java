package xml_mike.radioalarm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MClifford on 23/02/15.
 */
public class StandardAlarm extends AlarmAbstract{

    public StandardAlarm() {super();}

    public StandardAlarm(Parcel in){
        super(in);
    }

    public static final Parcelable.Creator<StandardAlarm> CREATOR
            = new Parcelable.Creator<StandardAlarm>() {
        public StandardAlarm createFromParcel(Parcel in) {
            return new StandardAlarm(in);
        }

        public StandardAlarm[] newArray(int size) {
            return new StandardAlarm[size];
        }
    };

}
