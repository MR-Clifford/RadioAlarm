package xml_mike.radioalarm;

import android.app.Application;

import java.util.ArrayList;

import xml_mike.radioalarm.models.Alarm;

/**
 * Created by MClifford on 18/03/15.
 */
public class Global extends Application {

    //single instance of application
    private static Global singleton;

    private ArrayList<Alarm> Alarms = new ArrayList<>();

    public final void onCreate(){
        super.onCreate();
    }

    /**
     * @returns current instance of global object
     */
    public static Global getInstance(){
        return singleton;
    }

    public void addAlarm(){

    }

    public ArrayList<Alarm> getAlarms(){

        return null;
    }


}
