package xml_mike.radioalarm;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import xml_mike.radioalarm.managers.DatabaseManager;
import xml_mike.radioalarm.models.AObservable;
import xml_mike.radioalarm.models.AObserver;
import xml_mike.radioalarm.models.Alarm;

/**
 * Created by MClifford on 18/03/15.
 */
public class Global extends Application implements AObservable {

    //single instance of application
    private static Global singleton;

    private List<AObserver> observers;
    private final Object MUTEX= new Object();

    private ArrayList<Alarm> alarms;

    public final void onCreate(){
        super.onCreate();
        singleton = this;
        this.observers = new ArrayList<>();
        this.alarms = DatabaseManager.getInstance().getAlarmDatabaseItems();//new ArrayList<>();
    }

    /**
     * @returns current instance of global object
     */
    public static Global getInstance(){
        return singleton;
    }

    public ArrayList<Alarm> getAlarms(){
        return alarms;
    }

    public void addAlarm(Alarm alarm){
        DatabaseManager.getInstance().addDatabaseItem(alarm);
        //this.alarms.add(alarm);
        this.notifyObservers();
    }

    public void setAlarms(ArrayList<Alarm> alarms){
        this.alarms = alarms;
        this.notifyObservers();
    }

    public void removeAlarm(int removeIndex){
        alarms.remove(removeIndex);
        this.notifyObservers();
    }

    public void updateAlarm(Alarm alarm){
        alarms.remove(alarm);
        alarms.add(alarm);
        this.notifyObservers();
    }

    @Override
    public void register(AObserver aObserver) {
        if(aObserver == null) throw new NullPointerException("Null Observer");
        synchronized (MUTEX) {
            if(!observers.contains(aObserver)) observers.add(aObserver);
        }
    }

    @Override
    public void unregister(AObserver aObserver) {
        if(aObserver == null) throw new NullPointerException("Null Observer");
        synchronized (MUTEX) {
            if(observers.contains(aObserver)) observers.remove(aObserver);
        }
    }

    @Override
    public void notifyObservers() {
        //synchronization is used to make sure any observer registered after message is received is not notified
        for (AObserver obj : observers) {
            obj.update();
        }
    }

    @Override
    public Object getUpdate(AObserver aObserver) {
        return null;
    }
}
