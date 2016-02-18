package xml_mike.radioalarm.managers;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.GlobalStrings;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.StandardAlarm;
import xml_mike.radioalarm.receivers.AlarmReceiver;
import xml_mike.radioalarm.receivers.ScheduledCheckReceiver;

/**
 * Created by MClifford on 01/04/15.
 *
 * This class will store and manage all the alarms on the system.
 * This is to keep track of alarms as android does not allow a means to retrieve alarms that have been set.
 * The main concept is to have this class mimic alarms that are stored on the android device.
 *
 * its main job is to store alarm objects,
 * schedule and un-schedule alarms,
 * update the database every time an alarm is changed
 * notify all entities watching it.
 */
public class AlarmsManager extends Observable {

    static private AlarmsManager instance;

    private ArrayList<Alarm> alarms;
    private AlarmManager alarmManager;
    private PendingIntent lastPendingIntent;
    private Intent lastIntent;

    private AlarmsManager(){
        super();
        alarms = new ArrayList<>();
        alarms.addAll(DatabaseManager.getInstance().getAlarmDatabaseItems());
        alarmManager = (AlarmManager) Global.getInstance().getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * singleton pattern with lazy initialisation
     * @return get instance of AlarmsManager object
     */
    static public AlarmsManager getInstance(){
        if(instance == null)
            instance = new AlarmsManager();
        return instance;
    }

    /**
     *
     * @param calendar
     * @param alarm
     * @return the difference between current time and the time set in the alarm
     */
    static public long calculateAlarmDay(Calendar calendar, Alarm alarm){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.DAY_OF_WEEK, al)
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getTimeHour());
        calendar.set(Calendar.MINUTE, alarm.getTimeMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int days_to_add = 0; //set

        if(calendar.before(now)) {//if its in the past increment of day // stop the alarm going off immediately
            days_to_add++;
        }

        if(!alarm.getRepeatingDay(now.get(Calendar.DAY_OF_WEEK)-1) && alarm.isRepeating()) {
            boolean[] alarmDays = alarm.getRepeatingDays();

            //loop 7 days circular list until one result returns true
            int start = now.get(Calendar.DAY_OF_WEEK) -1 ;
            int current_position = now.get(Calendar.DAY_OF_WEEK) -1 ;
            int total_checks = 0;

            while( start != current_position || total_checks != 7){

                if(alarmDays[current_position])
                    break;
                days_to_add++;
                current_position = current_position < 6 ? current_position + 1 : 0;
                total_checks++;
            }
        }

        calendar.add(Calendar.DATE, days_to_add);

        return  calendar.getTimeInMillis() - now.getTimeInMillis(); //differenceBetweenTimes
    }

    public ArrayList<Alarm> getAlarms(){
        return alarms;
    }

    public void update(int i, Alarm alarm, boolean changedTime){
        alarms.set(i, alarm);
        DatabaseManager.getInstance().updateDataBaseItem(alarm);
        if(alarm.getIntId() >=0 && changedTime)
            updateAlarm(i);
        this.setChanged();
        this.notifyObservers();
    }

    public void update(Alarm alarm){
        alarms.set(alarms.indexOf(alarm), alarm);
        DatabaseManager.getInstance().updateDataBaseItem(alarm);
        if(alarm.getId() >= 0)
            this.updateAlarm(alarms.indexOf(alarm));
        this.setChanged();
        this.notifyObservers();
    }

    public void remove(int i){
        Alarm alarm = alarms.remove(i);
        this.unscheduleAlarm(alarm);
        DatabaseManager.getInstance().removeDatabaseItem(alarm);
        this.setChanged();
        this.notifyObservers();
    }

    public void remove(Alarm alarm){
        if(alarm.getId() >=0)
            this.unscheduleAlarm(alarm);
        alarms.remove(alarm);
        DatabaseManager.getInstance().removeDatabaseItem(alarm);
        this.setChanged();
        this.notifyObservers();
    }

    public void add(Alarm alarm){
        alarms.add(alarm);
        if(alarm.getId() >=0)
            this.updateAlarm(alarms.indexOf(alarm));
        DatabaseManager.getInstance().addDatabaseItem(alarm);
        this.setChanged();
        this.notifyObservers();
    }

    private void updateAlarm(int i){

        if(alarms.get(i).isEnabled())
            scheduleAlarm(alarms.get(i));
        else
            unscheduleAlarm(alarms.get(i));
    }

    public void unscheduleAlarm(Alarm alarm){
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(Global.getInstance().getBaseContext(), alarm.getIntId(), this.generateIntent(alarm),PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = this.generateIntent(alarm);

        alarmManager.cancel(pendingIntent);

        Log.i("AlarmsManager", "u" + alarm.getId() + "" + pendingIntent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void scheduleAlarm(Alarm alarm){
        Calendar calendar = Calendar.getInstance();

        long differenceBetweenCurrentAndAlarmTimes = calculateAlarmDay(calendar, alarm);

        PendingIntent pendingIntent = this.generateIntent(alarm);

        alarmManager.cancel(pendingIntent);
        //if(alarm.isRepeating())
            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
            }
        //else
            //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

/*
        int days = (int) differenceBetweenCurrentAndAlarmTimes / (24*60*60*1000) % 7;
        int hours = (int) differenceBetweenCurrentAndAlarmTimes / (60*60*1000) % 24;
        int minutes =  (int) differenceBetweenCurrentAndAlarmTimes / (60*1000) % 60;

        if(days >= 1)
            Toast.makeText(Global.getInstance(), "Next Alarm in " + days + " days " + hours + " hours ", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(Global.getInstance(), "Next Alarm in "+ hours + " hours " + minutes + " minutes ", Toast.LENGTH_SHORT).show();
*/
        Log.i("AlarmsManager", "s" + alarm.getId() + ":" + pendingIntent.toString());

    }

    //always generate the same intent using this function, enables easy cancel
    private PendingIntent generateIntent(Alarm alarm){
        Intent intent = new Intent(Global.getInstance().getBaseContext(), AlarmReceiver.class);
        intent.putExtra("alarmId", alarm.getId());
        intent.setAction(GlobalStrings.START_ALARM.toString());

        return PendingIntent.getBroadcast(Global.getInstance().getBaseContext(), alarm.getIntId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Alarm getAlarm(Long id){
        for(int i = 0; i < alarms.size(); i++)
            if(alarms.get(i).getId() == id) return alarms.get(i);

        return new StandardAlarm();
    }

    /**
     * used primarily to restart all alarms after the phone has been restarted
     */
    public void scheduleAllAlarms(){
        //if already set remove
        for(int i = 0; i < alarms.size();i++){
            if(alarms.get(i).isEnabled())
                this.unscheduleAlarm(alarms.get(i));
        }
        for(int i = 0; i < alarms.size();i++){
            if(alarms.get(i).isEnabled())
                this.scheduleAlarm(alarms.get(i));
        }
    }

    /**
     * used calendar add to alarm 5 minutes from function call, normally right after user presses snooze
     * @param alarm orginal alarm, to work like previous alarm but 5 minutes later
     */
    public void setSnoozeAlarm(Alarm alarm){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 5);

        Intent intent = new Intent(Global.getInstance().getBaseContext(), AlarmReceiver.class);
        //Log.e(this.getClass().getSimpleName(), "context:" + Global.getInstance().getApplicationContext());
        intent.putExtra("alarmId", alarm.getId());
        intent.setAction(GlobalStrings.START_SNOOZE_ALARM.toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Global.getInstance().getBaseContext(), 0,intent,PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    /**
     * Ensure that the alarms are set at the right time.
     */
    public void start_alarm_verification_service(){
        //always generate the same intent using this function, enables easy cancel
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

            Intent intent = new Intent(Global.getInstance().getBaseContext(), ScheduledCheckReceiver.class);
            intent.setAction(GlobalStrings.SCHEDULED_ALARM_CHECK.toString());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(Global.getInstance().getBaseContext(), (Integer.MAX_VALUE - 123456789) ,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}