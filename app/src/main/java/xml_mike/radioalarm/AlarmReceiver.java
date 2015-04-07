package xml_mike.radioalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.models.Alarm;

/**
    when a pending intent is loaded, the Alarm Receiver will upon activation create the appropriate alarm activity with correct alarm object.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Long alarmId =0L;
        alarmId = intent.getLongExtra("alarmId",alarmId);

        Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(alarm.getRepeatingDay(day)){
            Toast.makeText(Global.getInstance().getApplicationContext(), "This! was run on"+calendar.get(Calendar.DAY_OF_WEEK), Toast.LENGTH_LONG).show();
        }
    }
}
