package xml_mike.radioalarm.managers;

import android.widget.TextView;

/**
 * Created by MClifford on 02/07/15.
 *
 * To replace a redundant class
 */
public interface AudioService {

    void stopAudio();
    void startAudio(String audio_path, TextView view);
}
