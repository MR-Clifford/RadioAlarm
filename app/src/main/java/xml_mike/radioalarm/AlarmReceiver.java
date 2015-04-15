package xml_mike.radioalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.models.Alarm;

/**
 * Created by MClifford on 11/04/15.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction() != null) {
            if (intent.getAction().equals("xml_mike.radioalarm.intent.START_ALARM")) {
                Long alarmId = 0L;
                alarmId = intent.getLongExtra("alarmId", alarmId);

                Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);

                if(alarm.getId() >= 0) {

                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);

                    Log.e("","");

                    if (alarm.getRepeatingDay(day)) {
                        Toast.makeText(Global.getInstance().getApplicationContext(), "This! was run on" + calendar.get(Calendar.DAY_OF_WEEK), Toast.LENGTH_LONG).show();
                        Log.e("AlarmReceiver.onReceive", "Alarm went off, right day");
                        Intent newIntent = new Intent(Global.getInstance().getBaseContext(), AlarmActivity.class);
                        newIntent.putExtra("alarmId", alarmId);
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Global.getInstance().startActivity(newIntent);

                    } else
                        Log.e("AlarmReceiver.onReceive", "Alarm did not go off, wrong day");

                     Log.e("AlarmReceiver.onReceive","ID:1: " + alarmId + " CLOCK:"+alarm.getTimeHour()+":"+alarm.getTimeMinute() );
                }
                else Log.e("AlarmReceiver.onReceive","ID:2: " + alarmId + " time:"+alarm.getTimeHour()+":"+alarm.getTimeMinute() );
            }
        }
    }
}
