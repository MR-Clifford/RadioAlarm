package xml_mike.radioalarm;

import android.app.Application;

import xml_mike.radioalarm.managers.ResourceManager;

/**
 * Created by MClifford on 18/03/15.
 *
 * allows access to context from any location
 */
public class Global extends Application {

    public final static String test ="";
    // TODO add all static strings to this global class as a single resource of all declared strings
    public static String PREFERENCE_FIRST_RUN = "PREFERENCE_FIRST_RUN";
    //single instance of application
    private static Global singleton;

    /**
     * this is a universal singleton used to get the applications context from any instance, TODO check if this is good practice
     * @return returns instance of application object
     */
    public static Global getInstance(){
        return singleton;
    }

    public final void onCreate(){
        super.onCreate();
        singleton = this;
        ResourceManager.getInstance();
    }
}
