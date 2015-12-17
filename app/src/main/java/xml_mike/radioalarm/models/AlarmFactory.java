package xml_mike.radioalarm.models;

import android.util.Log;

/**
 * Created by MClifford on 18/03/15.
 */
public class AlarmFactory {

    public static Alarm createAlarm(long id, String alarmType, String name, String data, String repeatingDays, int timeHour, int timeMinute, int isEnabled, int repeating, int vibrating, int volume, int duration, int easing){

        Alarm alarm = generateType(alarmType);

        if(repeatingDays != null) {
            boolean[] RD = new boolean[7];
            char[] ch = repeatingDays.toCharArray();

            for (int i = 0; i < repeatingDays.length(); i++) {
                RD[i] = ch[i] == '1';
            }

            alarm.setRepeatingDays(RD);
        }

        if(name != null)
            alarm.setName(name);
        if(data != null)
            alarm.setData(data);
        else
            Log.e("data is null","data should never be null");
        if(id >-1)
            alarm.setId(id);

        alarm.setTimeHour(timeHour);
        alarm.setTimeMinute(timeMinute);
        alarm.setMaxVolume(volume);
        alarm.setDuration(duration);
        alarm.setEasing(easing);
        alarm.setEnabled(isEnabled != 0);
        alarm.setRepeating(repeating != 0);
        alarm.setVibrate(vibrating !=0);

        return alarm;
    }

    private static Alarm generateType(String alarmType){

        Alarm alarm = new StandardAlarm();

        if(alarmType.equalsIgnoreCase(StandardAlarm.class.toString()))
            alarm = new StandardAlarm();
        if(alarmType.equalsIgnoreCase(MusicAlarm.class.toString()))
            alarm = new MusicAlarm();
        if(alarmType.equalsIgnoreCase(RadioAlarm.class.toString()))
            alarm = new RadioAlarm();

        return alarm;
    }

    /**
     *
     * @param classname class which implements alarm interface
     * @param alarm alarm to be converted
     * @return converted alarm object
     */
    public static Alarm convertAlarm(String classname, Alarm alarm){

        Alarm returnAlarm = generateType(classname);

        returnAlarm.setId(alarm.getId());
        returnAlarm.setRepeatingDays(alarm.getRepeatingDays());
        returnAlarm.setVibrate(alarm.isVibrate());
        returnAlarm.setEnabled(alarm.isEnabled());
        returnAlarm.setData("");
        returnAlarm.setName(alarm.getName());
        returnAlarm.setTimeHour(alarm.getTimeHour());
        returnAlarm.setTimeMinute(alarm.getTimeMinute());
        returnAlarm.setRepeating(alarm.isRepeating());
        returnAlarm.setEasing(alarm.getEasing());
        returnAlarm.setDuration(alarm.getDuration());

        return returnAlarm;
    }

}
