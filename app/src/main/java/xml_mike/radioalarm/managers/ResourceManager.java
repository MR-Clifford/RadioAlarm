package xml_mike.radioalarm.managers;

/**
 * Created by MClifford on 23/03/15.
 */
public class ResourceManager {
    private static ResourceManager ourInstance = new ResourceManager();

    public static ResourceManager getInstance() {
        return ourInstance;
    }

    private ResourceManager() {
    }
}
