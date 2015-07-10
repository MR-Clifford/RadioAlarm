package xml_mike.radioalarm.models;

/**
 * Created by MClifford on 17/04/15.
 */
public class AlarmMedia implements MediaPlayerView{
    public String id;
    public String artist;
    public String title;
    public String data;
    public String displayName;
    public String duration;


    public AlarmMedia(String id, String artist, String title, String data, String displayName, String duration) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.data = data;
        this.displayName = displayName;
        this.duration = duration;
    }

    public AlarmMedia(){
        this.id = "";
        this.artist = "";
        this.title = "";
        this.data = "";
        this.displayName = "";
        this.duration = "";
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public String getDescription() {
        return artist;
    }

    @Override
    public String getStringId() {
        return id;
    }

    @Override
    public String getData() {
        return data;
    }
}
