package xml_mike.radioalarm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MClifford on 22/02/15.
 */
public class RadioAlarm extends AlarmAbstract {

    public RadioAlarm() {super();}

    public RadioAlarm(Parcel in){
        super(in);
    }

    public static final Parcelable.Creator<RadioAlarm> CREATOR
            = new Parcelable.Creator<RadioAlarm>() {
        public RadioAlarm createFromParcel(Parcel in) {
            return new RadioAlarm(in);
        }

        public RadioAlarm[] newArray(int size) {
            return new RadioAlarm[size];
        }
    };
}
