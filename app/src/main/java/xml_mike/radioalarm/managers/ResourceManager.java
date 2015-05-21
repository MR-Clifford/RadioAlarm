package xml_mike.radioalarm.managers;

/**
 * Created by MClifford on 23/03/15.
 *
 * stub: see if this is needed later
 */
public class ResourceManager {
    private static ResourceManager ourInstance = new ResourceManager();

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        return ourInstance;
    }
}
