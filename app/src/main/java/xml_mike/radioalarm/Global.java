package xml_mike.radioalarm;

import android.app.Application;

/**
 * Created by MClifford on 18/03/15.
 */
public class Global extends Application {

    public static String PREFERENCE_FIRST_RUN = "PREFERENCE_FIRST_RUN";
    //single instance of application
    private static Global singleton;

    /**
     * @returns current instance of global object
     */
    public static Global getInstance(){
        return singleton;
    }

    public final void onCreate(){
        super.onCreate();
        singleton = this;
    }

}
