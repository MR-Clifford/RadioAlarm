package xml_mike.radioalarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import xml_mike.radioalarm.managers.AlarmsManager;

/**
 * Created by MClifford on 02/02/16.
 *
 * Simple implementation to ensure that alarms are always set. Service is set to run by both first run & on boot.
 */
public class ScheduledCheckReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null)
            Log.d(this.getClass().getSimpleName(), "the intent action was null");
        else if (intent.getAction().equals("xml_mike.radioalarm.intent.SCHEDULED_ALARM_CHECK")) {
            AlarmsManager.getInstance().scheduleAllAlarms();
        }
    }
}
