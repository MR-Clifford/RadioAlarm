package xml_mike.radioalarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import xml_mike.radioalarm.managers.AlarmsManager;

/**
 when a pending intent is loaded, the Alarm Receiver will upon activation create the appropriate alarm activity with correct alarm object.
 */
public class AlarmBootReceiver extends BroadcastReceiver {
    public AlarmBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        //if the device has just booted restart all alarms so that they will run on time.
        //this is becuase android resets all alarms on restart/shutdown.
        if(intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
                AlarmsManager.getInstance().scheduleAllAlarms(); //schedule all active alarms

            Log.d("AlarmBookReceiver", intent.getAction());
        }
    }
}
