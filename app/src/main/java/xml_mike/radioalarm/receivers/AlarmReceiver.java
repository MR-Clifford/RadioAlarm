package xml_mike.radioalarm.receivers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.GlobalStrings;
import xml_mike.radioalarm.StaticWakeLock;
import xml_mike.radioalarm.controllers.AlarmActivity;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.models.Alarm;

/**
 * Created by MClifford on 11/04/15.
 *
 * Receives all messages for the alarm, including starting and stopping the alarm service.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    //TODO extract logic from the method and move it to private methods.
    @Override
    public void onReceive(Context context, Intent intent) {

        StaticWakeLock.lockOn(context);
        if(intent.getAction() == null)
            Log.d(this.getClass().getSimpleName(),"the intent action was null");
        else if (intent.getAction().equals(GlobalStrings.START_ALARM.toString())) {

            Long alarmId = intent.getLongExtra("alarmId", -1L);
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);

            if(alarm.getId() >= 0L) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                //if alarm is repeating set again, else set enabled to false & update alarm on database
                if (alarm.isRepeating()) {
                    AlarmsManager.getInstance().scheduleAlarm(alarm);
                } else {
                    alarm.setEnabled(false);
                    AlarmsManager.getInstance().update(alarm);
                }

                if (alarm.getRepeatingDay(day-1) || !alarm.isRepeating()) {
                    this.startAlarmActivity(context, intent);
                }

                Global.writeToLogFile("onReceive:"+day+":"+hour+":"+minute , true);
            }

        } else if(intent.getAction().equals(GlobalStrings.START_SNOOZE_ALARM.toString())){
            Long alarmId = 0L;
            alarmId = intent.getLongExtra("alarmId", alarmId);
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);

            if(alarm.getId() >= 0L)
                startAlarmActivity(context, intent);
            Toast.makeText(Global.getInstance(), "test", Toast.LENGTH_SHORT).show();
        } /* else if(intent.getAction().equals(GlobalStrings.STOP_ALARM.toString())){ //catch stop alarm intent then handle it.
            Long alarmId = intent.getLongExtra("alarmId", -1L);
            Intent broadCastIntent = new Intent(GlobalStrings.STOP_ALARM_FOR_NEXT.toString());
            broadCastIntent.putExtra("alarmId", alarmId);
            context.sendBroadcast(broadCastIntent);
            Toast.makeText(Global.getInstance(), "test", Toast.LENGTH_SHORT).show();
        } else if(intent.getAction().equals(GlobalStrings.STOP_SNOOZE_ALARM)){ //stop alarm then arrange a snooze alarm to go off
            Toast.makeText(Global.getInstance(), "test", Toast.LENGTH_SHORT).show();
        } */
    }

    private void startAlarmActivity(Context context, Intent intent){
        Long alarmId = 0L;
        alarmId = intent.getLongExtra("alarmId", alarmId);

        if(isRunning(AlarmActivity.class, context) || AlarmActivity.isRunning) {
            Intent broadCastIntent = new Intent(GlobalStrings.STOP_ALARM_FOR_NEXT.toString());
            broadCastIntent.putExtra("alarmId", alarmId);
            context.sendBroadcast(broadCastIntent);
        } else {
            Intent newIntent = new Intent(Global.getInstance().getBaseContext(), AlarmActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            newIntent.setAction("com.example.action.PLAY");
            newIntent.putExtra("alarmId", alarmId);

            context.startActivity(newIntent);
        }
   }

    /**
     * Currently dummy function.
     * @param serviceClass
     * @param context
     * @return if activity is running then return true.
     */
    private boolean isRunning(Class<?> serviceClass, Context context)  {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();
/*
        for (ActivityManager.RunningAppProcessInfo activity : tasks ) {
            if (serviceClass.getPackage().getName().equalsIgnoreCase(activity.baseActivity.getPackageName())) {

                Log.e("isRunning",serviceClass.getPackage().getName());
                Log.e("isRunning",activity.baseActivity.getPackageName());

                return true;
            }
        }
*/
        return false;
    }
}
