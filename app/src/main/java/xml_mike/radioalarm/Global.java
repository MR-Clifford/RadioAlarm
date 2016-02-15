package xml_mike.radioalarm;

import android.app.Application;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import xml_mike.radioalarm.managers.ResourceManager;

/**
 * Created by MClifford on 18/03/15.
 *
 * allows access to context from any location
 */
public class Global extends Application {

    // TODO add all static strings to this global class as a single resource of all declared strings
    public static String PREFERENCE_FIRST_RUN = "PREFERENCE_FIRST_RUN";
    public static String COUNTRY_CODE = "COUNTRY_CODE";
    //single instance of application
    private static Global singleton;

    /**
     * this is a universal singleton used to get the applications context from any instance, TODO check if this is good practice
     * @return returns instance of application object
     */
    public static Global getInstance(){
        return singleton;
    }

    /**
     *
     * @param message Message to add to log file
     * @param append whether or not to append to current log file or to overwrite
     * @return optinal return, not null.
     */
    public static String writeToLogFile(String message,boolean append){

        File logPath = Environment.getExternalStorageDirectory();

        logPath = new File(logPath.getPath() + "/Android/data/com.xml_mike.radioalarm/files");

        if(!logPath.exists())
            logPath.mkdirs();

        try {
            File  LogFile = new File(logPath, "debug.log");
            FileWriter LogWriter = new FileWriter(LogFile, append);
            BufferedWriter out = new BufferedWriter(LogWriter);
            Date date = new Date();
            out.write("L:" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":->" + message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public final void onCreate(){
        super.onCreate();
        singleton = this;
        ResourceManager.getInstance();
    }
}
