package xml_mike.radioalarm.models;

import android.media.MediaPlayer;
import android.os.Parcel;
import android.util.Log;

/**
 * Created by MClifford on 22/02/15.
 *
 * Common code repository which all alarms share.
 */
public abstract class AlarmAbstract implements Alarm {

    private long id = -1;               // ID in Database; set just after alarm is saved to database
    private int timeHour = 0;           // time of day in 24hour format
    private int timeMinute = 0;         // minute
    private int duration = 10;          // how long the alarm should last.
    private int increasingDuration = 1; // how long it should take to get to max volume
    private boolean repeatingDays[];    // what days it should repeat on
    private boolean repeating = true;   // if it should repeat
    private boolean vibrate = false;    // Whether or not alarm should vibrate when starting
    private boolean isEnabled = false;  // IF alarm is set
    private int maxVolume = 100;        // goes from 0 to 1;
    private String name = "Alarm";      // Name of alarm for easy identification.
    private String data = "";           // what Audio is to be played, if this is stream url or local URI

    AlarmAbstract(){
        repeatingDays = new boolean[7];
        repeating = false;
        vibrate = false;
        isEnabled = true;
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

    @Override
    public long getId(){return this.id;}

    @Override
    public void setId(long id){this.id = id;}

    @Override
    public int getIntId(){
        if (id < Integer.MIN_VALUE || id > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (id + " cannot be cast to int without changing its value.");
        }
        return (int) id;
    }

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
    public boolean[] getRepeatingDays() { return repeatingDays; }

    @Override
    public void setRepeatingDays(boolean[] daysOfWeek) { repeatingDays = daysOfWeek; }

    @Override
    public String getData() { return this.data; }

    @Override
    public void setData(String data) { this.data = data; }

    @Override
    public boolean isVibrate() {return vibrate;}

    @Override
    public void setVibrate(boolean vibrate) { this.vibrate = vibrate;}

    @Override
    public int getMaxVolume() { return maxVolume; }

    @Override
    public void setMaxVolume(int volume) { this.maxVolume = volume; }

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

    @Override
    public int getDuration(){
        return duration;
    }

    public void setDuration(int time){
        duration = time;
    }

    @Override
    public int getEasing() {
        return increasingDuration;
    }

    @Override
    public void setEasing(int time) {
        increasingDuration = time;
    }

    protected MediaPlayer.OnPreparedListener getOnPreparedListener() {

        return new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("Start Media:","OMG");
                mp.start();
            }
        };
    }
}

