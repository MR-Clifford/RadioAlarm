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
 */
public class AlarmsManager extends Observable {

    static private AlarmsManager instance;


    private ArrayList<Alarm> alarms;
    private AlarmManager alarmManager;

    private AlarmsManager(){
        super();
        alarms = new ArrayList<>();
        alarms.addAll(DatabaseManager.getInstance().getAlarmDatabaseItems());
        alarmManager = (AlarmManager) Global.getInstance().getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * singleton pattern with lazy initialisation
     * @return
     */
    static public AlarmsManager getInstance(){
        if(instance == null)
            instance = new AlarmsManager();
        return instance;
    }

    public ArrayList<Alarm> getAlarms(){
        //this.setChanged();
        return alarms;
    }

    public void update(int i, Alarm alarm){
        alarms.set(i, alarm);
        DatabaseManager.getInstance().updateDataBaseItem(alarm);
        if(alarm.getId() >= 0)
            this.updateAlarm(i);
        this.setChanged();
        this.notifyObservers();
    }


    public void update(Alarm alarm){
        //alarms.remove(alarm);
        //alarms.add(alarm);
        alarms.set(alarms.indexOf(alarm), alarm);
        DatabaseManager.getInstance().updateDataBaseItem(alarm);
        if(alarm.getId() >= 0)
            this.updateAlarm(alarms.indexOf(alarm));
        this.setChanged();
        this.notifyObservers();
    }

    public void remove(int i){
        Alarm alarm = alarms.remove(i);
        DatabaseManager.getInstance().removeDatabaseItem(alarm);
        if(alarm.getId() >=0)
            this.unscheduleAlarm(alarm);
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
        //Log.e("AlarmsManager.updateAlarm",""+alarms.get(i).getId());
    }

    private void unscheduleAlarm(Alarm alarm){
        alarmManager.cancel(this.generatePendingIntent(alarm));
        Log.e("AlarmsManager.unscheduleAlarm", "" + alarm.getId());

    }

    private void scheduleAlarm(Alarm alarm){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getTimeHour());
        calendar.set(Calendar.MINUTE,alarm.getTimeMinute() );
        calendar.set(Calendar.SECOND,0 );

        //alarmManager.cancel(this.generatePendingIntent(alarm)); //ensure that
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, this.generatePendingIntent(alarm));
        //Toast.makeText(Global.getInstance().getBaseContext(),"Will go off on:"+calendar.getTime().toString(), Toast.LENGTH_SHORT ).show();
        Log.e("AlarmsManager.scheduleAlarm", "" + alarm.getId());
    }

    private PendingIntent generatePendingIntent(Alarm alarm){

        Intent intent = new Intent(Global.getInstance(), AlarmReceiver.class);
        intent.putExtra("alarmId", alarm.getId());
        intent.setAction("Start.Alarm");
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getBroadcast(Global.getInstance().getBaseContext(), safeLongToInt(alarm.getId()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public Alarm getAlarm(Long id){
        for(int i = 0; i < alarms.size(); i++)
            if(alarms.get(i).getId() == id) return alarms.get(i);

        return new StandardAlarm();
    }

    private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
