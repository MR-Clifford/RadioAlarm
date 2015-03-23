package xml_mike.radioalarm.models;

/**
 * Created by MClifford on 22/02/15.
 */
public abstract class AlarmAbstract implements Alarm {

    private long id = -1;
    private int timeHour = 0;
    private int timeMinute = 0;
    private boolean repeatingDays[];
    private boolean repeatWeekly = true;
    private boolean vibrate = false;
    private boolean isEnabled = true;
    private String name = "Alarm";
    private String data = "";

    public AlarmAbstract(){
        repeatingDays = new boolean[7];
        repeatWeekly = false;
        vibrate = false;
        isEnabled = true;
    }
    @Override
    public long getId(){return this.id;}

    @Override
    public void setId(long id){this.id = id;}

    public int getTimeMinute() {
        return timeMinute;
    }

    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(int timeHour) {
        this.timeHour = timeHour;
    }

    public boolean isRepeatWeekly() {
        return repeatWeekly;
    }

    public void setRepeatWeekly(boolean repeatWeekly) {
        this.repeatWeekly = repeatWeekly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public void setRepeatingDay(int dayOfWeek, boolean value) {
        repeatingDays[dayOfWeek] = value;
    }

    @Override
    public boolean getRepeatingDay(int dayOfWeek) {
        return repeatingDays[dayOfWeek];
    }

    @Override
    public void setRepeatingDays(boolean[] daysOfWeek) { repeatingDays = daysOfWeek; }

    @Override
    public boolean[] getRepeatingDays() { return repeatingDays; }

    @Override
    public void setData(String data) { this.data = data; }

    @Override
    public String getData() { return this.data; }


    @Override
    public void setVibrate(boolean vibrate) { this.vibrate = vibrate;}

    @Override
    public boolean getVibrate() {return vibrate;}
}

