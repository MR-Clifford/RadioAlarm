package xml_mike.radioalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import xml_mike.radioalarm.managers.AlarmMediaManager;


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
}

    @Override
    protected void onPause() {
        super.onPause();
    }


    private Intent getAlarmService(){

        Intent intent = new Intent(getBaseContext(), AlarmMediaManager.class);
        Long alarmId = this.getIntent().getLongExtra("alarmId", -1L);

        if(alarmId >= 0)
            intent.putExtra("alarmId",alarmId);

        intent.setAction("com.example.action.PLAY");

        return intent;
    }

    public void stopAlarmService(View view){

        Intent intent = new Intent(getBaseContext(), AlarmMediaManager.class);
        intent.setAction("com.example.action.STOP");


        startService(intent);

        finish();
    }
}
