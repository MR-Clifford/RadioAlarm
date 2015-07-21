package xml_mike.radioalarm.managers;

import android.media.RingtoneManager;

import xml_mike.radioalarm.Global;

/**
 * Created by MClifford on 23/03/15.
 *
 * stub: see if this is needed later
 */
public class ResourceManager {
    private static ResourceManager ourInstance = new ResourceManager();
    private RingtoneManager ringtoneManager = null;

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        return ourInstance;
    }

    public RingtoneManager getRingtoneManager(){

        if(ringtoneManager == null)
            ringtoneManager = new RingtoneManager(Global.getInstance());

        return ringtoneManager;
    }
}
