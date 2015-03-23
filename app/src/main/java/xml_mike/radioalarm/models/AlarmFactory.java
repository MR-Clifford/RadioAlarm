package xml_mike.radioalarm.models;

/**
 * Created by MClifford on 18/03/15.
 */
public class AlarmFactory {

    public static Alarm createAlarm(long id,String alarmType, String name, String data, String repeatingDays,  int timeHour, int timeMinute, int isEnabled){

        Alarm alarm = generateType(alarmType);

        if(repeatingDays != null) {
            boolean[] RD = new boolean[7];
            char[] ch = repeatingDays.toCharArray();

            for (int i = 0; i < repeatingDays.length(); i++) {
                if (ch[i] == 1)
                    RD[i] = true;
                else
                    RD[i] = false;
            }

            alarm.setRepeatingDays(RD);
        }

        if(name != null)
            alarm.setName(name);
        if(data != null)
            alarm.setData(data);
        if(id >-1)
            alarm.setId(id);

        alarm.setTimeHour(timeHour);
        alarm.setTimeMinute(timeMinute);
        alarm.setEnabled(isEnabled != 0);

        return alarm;
    }

    private static Alarm generateType(String alarmType){

        Alarm alarm = new StandardAlarm();

        if(alarmType == StandardAlarm.class.toString())
            alarm = new StandardAlarm();
        if(alarmType == MusicAlarm.class.toString())
            alarm = new MusicAlarm();
        if(alarmType == RadioAlarm.class.toString())
            alarm = new RadioAlarm();

        return alarm;
    }

}
