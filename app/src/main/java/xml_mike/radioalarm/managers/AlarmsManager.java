package xml_mike.radioalarm.managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;

import xml_mike.radioalarm.AlarmReceiver;
import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.StandardAlarm;

/**
 * Created by MClifford on 01/04/15.
 *
 * this class will store and manage all the alarms on the system.
 * this is to keep track of alarms as android does not allow a means to retrieve alarms that have been set.
 * the main concept is to have this class mimic alarms that are stored on the android device.
 *
 * its main job is to store alarm objects,
 * schedule and unschedule alarms,
 * update the database everytime an alarm is changed
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

        Log.i("AlarmsManager", "u" + alarm.getId()+""+pendingIntent);
    }

    private void scheduleAlarm(Alarm alarm){
        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getTimeHour());
        calendar.set(Calendar.MINUTE, alarm.getTimeMinute());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(calendar.before(now))//if its in the past increment of day // stop the alarm going off immediately
            calendar.add(Calendar.DATE,1);

        PendingIntent pendingIntent = this.generateIntent(alarm);

        alarmManager.cancel(pendingIntent);
        if(alarm.isRepeating())
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.i("AlarmsManager", "s" + alarm.getId() + ":"+pendingIntent.toString());
    }

    //always generate the same intent using this function, enables easy cancel
    private PendingIntent generateIntent(Alarm alarm){
        Intent intent = new Intent(Global.getInstance().getBaseContext(), AlarmReceiver.class);
        intent.putExtra("alarmId", alarm.getId());
        intent.setAction("xml_mike.radioalarm.intent.START_ALARM");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Global.getInstance().getBaseContext(), alarm.getIntId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
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
        calendar.add(Calendar.MINUTE, 1);

        Intent intent = new Intent(Global.getInstance().getBaseContext(), AlarmReceiver.class);
        Log.e(this.getClass().getSimpleName(), "context:" + Global.getInstance().getApplicationContext());
        intent.putExtra("alarmId", alarm.getId());
        intent.setAction("xml_mike.radioalarm.intent.SNOOZE");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Global.getInstance().getBaseContext(), 0,intent,PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}