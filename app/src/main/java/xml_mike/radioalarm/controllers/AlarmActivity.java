package xml_mike.radioalarm.controllers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.GlobalStrings;
import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.AlarmService;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.models.Alarm;


/**
 * @author MClifford
 *
 * This class will be the main one intiated once the alarm goes off,
 * it will be bound to the alarm Service so that a user can easily cancel it
 * TODO refacter this class to include new broadcasts
 */
public class AlarmActivity extends AppCompatActivity {

    public static boolean isRunning = false;
    static AlarmActivity alarmActivity;
    long alarmId;
    AlarmService mService;
    boolean mBound = false;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AlarmService.AlarmServiceBinder binder = (AlarmService.AlarmServiceBinder) service;
            mService = binder.getService();
            mService.startAudio("", null);
            mBound = true;

            Log.e("service","Service connected to Activity");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private BroadcastReceiver broadcastReceiver = new StopAlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        //adRequest.isTestDevice(this);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) { // On admob interstitial failed to load, request new ad
                mAdView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        mAdView.loadAd(adRequest);

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
    protected void onStart() {

        alarmActivity = this;

        alarmId = this.getIntent().getLongExtra("alarmId", -1L);

        if(alarmId >= 0) bindService(getAlarmService(), mConnection, Context.BIND_AUTO_CREATE);
        else Log.e("Wrong ID","no id found");

        IntentFilter intentFilter =  new IntentFilter();

        intentFilter.addAction(GlobalStrings.STOP_ALARM_BROADCAST.toString());
        intentFilter.addAction(GlobalStrings.STOP_ALARM.toString());
        intentFilter.addAction(GlobalStrings.STOP_SNOOZE_ALARM.toString());

        registerReceiver(broadcastReceiver, intentFilter);

        isRunning = true;
        super.onStart();
    }

    private Intent getAlarmService(){

        Intent intent = new Intent(getBaseContext(), AlarmService.class);
        Long alarmId = this.getIntent().getLongExtra("alarmId", -1L);

        if (alarmId >= 0)
            intent.putExtra("alarmId",alarmId);

        intent.setAction("com.example.action.PLAY");

        return intent;
    }

    public void stopAlarmService(View view){

        stopAlarmService();

        alarmActivity = null;
        isRunning = false;
        this.finish();
    }

    public void pauseAlarmService(View view){

        stopAlarmService();

        Long alarmId = this.getIntent().getLongExtra("alarmId", -1L);
        if(alarmId >= 0L) {
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);
            AlarmsManager.getInstance().setSnoozeAlarm(alarm);
        }

        this.finish();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    private void stopAlarmService(){
        if (mBound) {
            mService.stopAudio();
            unbindService(mConnection);
            mBound = false;
        }
        isRunning = false;
    }

    @Override
    public void onBackPressed() {
        stopAlarmService();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        stopAlarmService();
        super.onDestroy();
    }

    public static class StopAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(alarmActivity != null) {
                if (intent.getAction().equals(GlobalStrings.STOP_ALARM_BROADCAST.toString())) {
                    Long alarmId = 0L;
                    alarmId = intent.getLongExtra("alarmId", alarmId);

                    alarmActivity.stopAlarmService(null); //pass null as there is no Gui View passed in.

                    Intent restartIntent = new Intent(Global.getInstance().getBaseContext(), AlarmActivity.class);
                    restartIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    restartIntent.setAction("com.example.action.PLAY");
                    restartIntent.putExtra("alarmId", alarmId);

                    context.startActivity(restartIntent);

                    Global.writeToLogFile("AlarmActivity.BroadcastReceiver.end ", true);
                    Log.e("StopAlarmReceiver", "1");
                } else if (intent.getAction().equals(GlobalStrings.STOP_ALARM.toString())) {
                    alarmActivity.stopAlarmService(null); // replicate button press in activity, pass null View
                    Log.e("StopAlarmReceiver", "2");
                } else if (intent.getAction().equals(GlobalStrings.STOP_SNOOZE_ALARM.toString())) {
                    alarmActivity.pauseAlarmService(null); // replicate button press in activity, pass null View
                    Log.e("StopAlarmReceiver", "3");
                }
            }

            Log.e("StopAlarmReceiver", intent.getAction());
        }
    }
}
