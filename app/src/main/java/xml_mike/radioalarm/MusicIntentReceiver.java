package xml_mike.radioalarm;

import android.content.Context;
import android.content.Intent;

/**
 * Created by MClifford on 09/04/15.
 */
public class MusicIntentReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(
                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // signal your service to stop playback
            // (via an Intent, for instance)
        }
    }
}
