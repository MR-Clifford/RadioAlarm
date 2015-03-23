package xml_mike.radioalarm.models;

/**
 * Created by MClifford on 22/02/15.
 */
public interface Alarm {

    public void setId(long id);
    public long getId();

    public void setName(String name);
    public String getName();

    public void setData(String data);
    public String getData();

    public void setEnabled(boolean enable);
    public boolean isEnabled();

    public boolean isRepeatWeekly();
    public void setRepeatWeekly(boolean repeatWeekly);

    public int getTimeHour();
    public void setTimeHour(int timeHour);

    public int getTimeMinute();
    public void setTimeMinute(int timeMinute);

    public void setRepeatingDay(int dayOfWeek, boolean value);
    public boolean getRepeatingDay(int dayOfWeek);

    public void setRepeatingDays(boolean[] daysOfWeek);
    public boolean[] getRepeatingDays();

    public void setVibrate(boolean vibrate);
    public boolean getVibrate();
}
