package xml_mike.radioalarm.models;

import android.os.Parcelable;

/**
 * Created by MClifford on 22/02/15.
 */
public interface Alarm extends Parcelable{

    void setId(long id);
    long getId();
    int  getIntId();

    void setName(String name);
    String getName();

    void setData(String data);
    String getData();

    void setEnabled(boolean enable);
    boolean isEnabled();

    boolean isRepeating();
    void setRepeating(boolean repeatWeekly);

    int getTimeHour();
    void setTimeHour(int timeHour);

    int getTimeMinute();
    void setTimeMinute(int timeMinute);

    void setRepeatingDay(int dayOfWeek, boolean value);
    boolean getRepeatingDay(int dayOfWeek);

    void setRepeatingDays(boolean[] daysOfWeek);
    boolean[] getRepeatingDays();

    void setVibrate(boolean vibrate);
    boolean isVibrate();

    void setDuration(int time);
    int getDuration();

    void setEasing(int time);
    int getEasing();

    String getDBRepeatingDays();
}
