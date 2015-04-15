package xml_mike.radioalarm.models;

import android.os.Parcelable;

/**
 * Created by MClifford on 22/02/15.
 */
public interface Alarm extends Parcelable{

    public void setId(long id);
    public long getId();
    public int  getIntId();

    public void setName(String name);
    public String getName();

    public void setData(String data);
    public String getData();

    public void setEnabled(boolean enable);
    public boolean isEnabled();

    public boolean isRepeating();
    public void setRepeating(boolean repeatWeekly);

    public int getTimeHour();
    public void setTimeHour(int timeHour);

    public int getTimeMinute();
    public void setTimeMinute(int timeMinute);

    public void setRepeatingDay(int dayOfWeek, boolean value);
    public boolean getRepeatingDay(int dayOfWeek);

    public void setRepeatingDays(boolean[] daysOfWeek);
    public boolean[] getRepeatingDays();

    public void setVibrate(boolean vibrate);
    public boolean isVibrate();

    public String getDBRepeatingDays();
}
