package xml_mike.radioalarm.controllers;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.NavigationDrawerFragment;
import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.managers.RadioStationsManager;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.RadioAlarm;
import xml_mike.radioalarm.models.StandardAlarm;
import xml_mike.radioalarm.views.ExpandableAlarmAdapter;


/**
 * Main view of the mobile alarm app
 */
public class ManageActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ExpandableListView.OnGroupCollapseListener, Observer {

    ExpandableListView mExpandableList;
    View emptyView;
    private int currentAlarm = -1;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        //this.deleteDatabase("RadioAlarm.db");

/*
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
*/


        emptyView  = findViewById(R.id.empty);//getLayoutInflater().inflate(R.layout.no_alarms, null);
        mExpandableList = (ExpandableListView) findViewById(R.id.managedAlarms);
        ExpandableAlarmAdapter expandableAlarmAdapter = new ExpandableAlarmAdapter(this, LayoutInflater.from(this), AlarmsManager.getInstance().getAlarms() );
        //mExpandableList.setEmptyView(emptyView);
        mExpandableList.setAdapter(expandableAlarmAdapter);
        mExpandableList.setGroupIndicator(null);


        AlarmsManager.getInstance().addObserver(this);
        AlarmsManager.getInstance().notifyObservers();

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);


        boolean firstRun = p.getBoolean(Global.PREFERENCE_FIRST_RUN, true); //sets behaviour for first use.

        if(firstRun && isNetworkAvailable()){
            String countryCodeValue = Locale.getDefault().getCountry();

            p.edit().putString(getString(R.string.pref_current_country_key), countryCodeValue).apply();

            if(isNetworkAvailable()) {
                RadioStationsManager.getInstance().downloadStations();
                p.edit().putBoolean(Global.PREFERENCE_FIRST_RUN, false).apply();
            }
        }

        if(expandableAlarmAdapter.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
        }


        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        //adRequest.isTestDevice(this);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) { // On admob interstitial failed to load, request new ad
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        mAdView.loadAd(adRequest);
    }

    @Override
    public void update(Observable observable, Object data) {
        ((ExpandableAlarmAdapter) mExpandableList.getExpandableListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onGroupCollapse(int groupPosition) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
//        }
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_reload_stations : RadioStationsManager.getInstance().reDownloadRadioStations();
                Toast.makeText(this, "Reloading RadioStations, please wait", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings : startActivity(new Intent(this, SettingsActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    
    public void createNewAlarm(View v){

        currentAlarm = -1;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Alarm nAlarm = new StandardAlarm() ;

                nAlarm.setTimeHour(hourOfDay);
                nAlarm.setTimeMinute(minute);

                if(currentAlarm < 0 && view.isShown())
                    AlarmsManager.getInstance().add(nAlarm);

                currentAlarm = -1;
            }


        }, 0,0,true);
        timePickerDialog.show();
        emptyView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //in this instance the requestCode is associated with the alarm ID
        if(requestCode >= 0) {
            if(resultCode == Activity.RESULT_OK) { //only if the user selects a media will this be Activity.RESULT_OK

                Log.i("OnActivityResult", "" + requestCode);
                Alarm alarm = AlarmsManager.getInstance().getAlarms().get(requestCode);
                String result = data.getStringExtra("result");
                String alarmType = data.getStringExtra("alarm_type");

                if(alarmType != null){
                    if(alarmType.equals(RadioAlarm.class.toString())) {
                        if (result != null) {
                            alarm.setData(result);
                            AlarmsManager.getInstance().update(requestCode, alarm, false);
                            this.update(null, null);
                        }
                    } else {
                        if (result != null) {
                            alarm.setData(""+result);
                            AlarmsManager.getInstance().update(requestCode, alarm, false);
                            this.update(null, null);
                        }
                    }
                }
                else
                    Log.d("onActivityResult", "F:" + requestCode + ":" + alarm.getData()); //somehow the wrong result was made, if case please analyse
            }
            else
                Log.d("onActivityResult", "F:" + requestCode); //somehow the wrong result was made, if case please analyse
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     *
     */
    private void reload_radio_stations(){

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = p.getBoolean(Global.PREFERENCE_FIRST_RUN, true); //sets behaviour for first use.

        if(firstRun && isNetworkAvailable()){

            TelephonyManager tm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
            String countryCodeValue = tm.getNetworkCountryIso();

            p.edit().putString(Global.COUNTRY_CODE, countryCodeValue);

            RadioStationsManager.getInstance().downloadStations();
            p.edit().putBoolean(Global.PREFERENCE_FIRST_RUN, false).apply();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ManageActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
