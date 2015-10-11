package xml_mike.radioalarm.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.AlarmService;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.models.Alarm;

/**
 * @author MClifford
 *
 * This class will be the main one intiated once the alarm goes off,
 * it will be bound to the alarm Service so that a user can easily cancel it
 */
public class AlarmActivity extends AppCompatActivity {

    long alarmId;
    AlarmService mService;
    boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AlarmService.LocalBinder binder = (AlarmService.LocalBinder) service;
            mService = binder.getService();
            mService.startAudio("");
            mBound = true;

            Log.e("service","connected1");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        alarmId = this.getIntent().getLongExtra("alarmId", -1L);

        if(alarmId >= 0) {
            bindService(getAlarmService(), mConnection, Context.BIND_AUTO_CREATE);
        } else {
            Log.e("an Issue happened","wrong id");
        }

        //startService(getAlarmService());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isFinishing() {
        Log.e("Finish","why is this not finished");
        return super.isFinishing();
    }

    private Intent getAlarmService(){

        Intent intent = new Intent(getBaseContext(), AlarmService.class);
        Long alarmId = this.getIntent().getLongExtra("alarmId", -1L);

        if(alarmId >= 0)
            intent.putExtra("alarmId",alarmId);

        intent.setAction("com.example.action.PLAY");

        return intent;
    }

    public void stopAlarmService(View view){

        if (mBound) {
            mService.stopAudio();
            unbindService(mConnection);
            mBound = false;
        }

        this.finish();
        return;
    }

    public void pauseAlarmService(View view){

        if (mBound) {
            mService.stopAudio();
            unbindService(mConnection);
            mBound = false;
        }

        Long alarmId = this.getIntent().getLongExtra("alarmId", -1L);
        if(alarmId >= 0L) {
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);
            AlarmsManager.getInstance().setSnoozeAlarm(alarm);
        }

        this.finish();
    }


}
