package xml_mike.radioalarm.models;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;

import xml_mike.radioalarm.managers.ThreadedMediaPlayer;

/**
 * Created by MClifford on 22/02/15.
 *
 * this interface will apply the strategy & visitor patterns. changing how the program/alarm behaves depending on type.
 */
public interface Alarm extends Parcelable{

    long getId();

    void setId(long id);

    int  getIntId();

    String getName();

    void setName(String name);

    String getData();

    void setData(String data);

    boolean isEnabled();

    void setEnabled(boolean enable);

    boolean isRepeating();
    void setRepeating(boolean repeatWeekly);

    int getTimeHour();
    void setTimeHour(int timeHour);

    int getTimeMinute();
    void setTimeMinute(int timeMinute);

    void setRepeatingDay(int dayOfWeek, boolean value);
    boolean getRepeatingDay(int dayOfWeek);

    boolean[] getRepeatingDays();

    void setRepeatingDays(boolean[] daysOfWeek);

    boolean isVibrate();

    void setVibrate(boolean vibrate);

    int getDuration();

    void setDuration(int time);

    int getEasing();

    void setEasing(int time);

    String getDBRepeatingDays();

    View.OnClickListener getDataOnClickListener( Context context,int groupPosition);

    void setupAlarmData(final Context context, final ThreadedMediaPlayer mediaPlayer) throws java.io.IOException;

    float getMaxVolume();

    void setMaxVolume(float volume);
}
