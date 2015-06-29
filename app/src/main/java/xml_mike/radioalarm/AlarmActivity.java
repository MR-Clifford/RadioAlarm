package xml_mike.radioalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import xml_mike.radioalarm.managers.AlarmService;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.models.Alarm;

/**
 * @author MClifford
 *
 * this class will be the main one intiated once the alarm goes off,
 * it will be bound to the alarm Service so that a user can easily cancel it,
 */
public class AlarmActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        long alarmId = this.getIntent().getLongExtra("alarmId", -1L);
        if(alarmId >= 0)
            startService(getAlarmService());
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
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getBaseContext(), AlarmService.class);
        intent.setAction("com.example.action.STOP");
        startService(intent);
}

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(getBaseContext(), AlarmService.class);
        intent.setAction("com.example.action.STOP");
        startService(intent);
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

        Intent intent = new Intent(getBaseContext(), AlarmService.class);
        intent.setAction("com.example.action.STOP");
        startService(intent);

        finish();
    }

    public void pauseAlarmService(View view){
        Intent intent = new Intent(getBaseContext(), AlarmService.class);
        intent.setAction("com.example.action.STOP");
        startService(intent);

        Long alarmId = this.getIntent().getLongExtra("alarmId", -1L);
        if(alarmId >= 0L) {
            Alarm alarm = AlarmsManager.getInstance().getAlarm(alarmId);
            AlarmsManager.getInstance().setSnoozeAlarm(alarm);
        }
        finish();
    }
}