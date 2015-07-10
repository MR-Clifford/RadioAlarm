package xml_mike.radioalarm.models;

/**
 * Created by MClifford on 26/06/15.
 *
 *
 */
public interface MediaPlayerView {

    String getStringId();
    String getName();
    String getDescription();
    String getData();
    boolean isPlaying();
    boolean setPlaying(boolean boo);
}
