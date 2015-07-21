package xml_mike.radioalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

import xml_mike.radioalarm.controllers.AlarmActivity;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.models.Alarm;

/**
 * Created by MClifford on 11/04/15.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        StaticWakeLock.lockOn(context);
        if(intent.getAction() == null)
            Log.d(this.getClass().getSimpleName(),"the intent action was null");
        else if (intent.getAction().equals("xml_mike.radioalarm.intent.START_ALARM")) {

            Long alarmId = intent.getLongExtra("alarmId", -1L);
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);

            if(alarm.getId() >= 0L) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                if (alarm.getRepeatingDay(day-1) && alarm.isRepeating())
                    this.startService(context, intent);
                else if(!alarm.isRepeating()){
                    alarm.setEnabled(false);
                    AlarmsManager.getInstance().update(alarm);
                }

                Log.i("AlarmReceiver.onReceive", "ID:1: " + alarmId + " CLOCK:" + alarm.getTimeHour() + ":" + alarm.getTimeMinute() );
            }

        } else if(intent.getAction().equals("xml_mike.radioalarm.intent.SNOOZE")){

            Long alarmId = 0L;
            alarmId = intent.getLongExtra("alarmId", alarmId);
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);

            if(alarm.getId() >= 0L)
                startService(context,intent);
        }
    }

    private void startService(Context context, Intent intent){
        Long alarmId = 0L;
        alarmId = intent.getLongExtra("alarmId", alarmId);

        Intent newIntent = new Intent(Global.getInstance().getBaseContext(), AlarmActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.setAction("com.example.action.PLAY");
        newIntent.putExtra("alarmId", alarmId);

        context.startActivity(newIntent);
    }
}

