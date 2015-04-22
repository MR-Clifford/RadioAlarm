package xml_mike.radioalarm.models;

import android.os.Parcel;

/**
 * Created by MClifford on 22/02/15.
 */
public abstract class AlarmAbstract implements Alarm {

    private long id = -1;
    private int timeHour = 0;
    private int timeMinute = 0;
    private boolean repeatingDays[];
    private boolean repeating = true;
    private boolean vibrate = false;
    private boolean isEnabled = false;
    private String name = "Alarm";
    private String data = "";

    public AlarmAbstract(){
        repeatingDays = new boolean[7];
        repeating = false;
        vibrate = false;
        isEnabled = false;
    }
    @Override
    public long getId(){return this.id;}

    @Override
    public int getIntId(){
        if (id < Integer.MIN_VALUE || id > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (id + " cannot be cast to int without changing its value.");
        }
        return (int) id;
    }

    @Override
    public void setId(long id){this.id = id;}

    @Override
    public int getTimeMinute() {
        return timeMinute;
    }

    @Override
    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    @Override
    public int getTimeHour() {
        return timeHour;
    }

    @Override
    public void setTimeHour(int timeHour) {
        this.timeHour = timeHour;
    }

    @Override
    public boolean isRepeating() {
        return repeating;
    }

    @Override
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
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
    public boolean isVibrate() {return vibrate;}

    @Override
    public String getDBRepeatingDays() {
        String returnString = "";

        for(boolean b : repeatingDays){
            if(b)
                returnString = returnString + "1";
            else
                returnString = returnString + "0";
        }

        return returnString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeIntArray(new int[]{timeHour,timeMinute});
        dest.writeStringArray(new String[]{name,data});
        dest.writeBooleanArray(new boolean[]{repeating,vibrate,isEnabled});
        dest.writeBooleanArray(repeatingDays);
    }

    public AlarmAbstract(Parcel in){

        this.id = in.readLong();

        int[] ints = new int[2];
        in.readIntArray(ints);
        this.timeHour = ints[0];
        this.timeMinute = ints[1];

        String[] strings = new String[2];
        in.readStringArray(strings);
            this.name = strings[0];
            this.data = strings[1];

        boolean[] booleans = new boolean[3];
        in.readBooleanArray(booleans);
            this.repeating = booleans[0];
            this.vibrate = booleans[1];
            this.isEnabled = booleans[2];

        this.repeatingDays = new boolean[7];
        in.readBooleanArray(repeatingDays);
    }
}

