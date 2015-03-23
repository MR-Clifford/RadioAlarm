package xml_mike.radioalarm.managers;

/**
 * Created by MClifford on 23/03/15.
 */
public class FileManager {
    private static FileManager ourInstance = new FileManager();

    public static FileManager getInstance() {
        return ourInstance;
    }

    private FileManager() {
    }
}
