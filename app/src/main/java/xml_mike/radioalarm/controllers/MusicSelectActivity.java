package xml_mike.radioalarm.controllers;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.AudioService;
import xml_mike.radioalarm.managers.DatabaseManager;
import xml_mike.radioalarm.managers.TestAudioService;
import xml_mike.radioalarm.models.AlarmMedia;
import xml_mike.radioalarm.models.MediaPlayerView;
import xml_mike.radioalarm.views.MusicFilterableAdapter;

/**
 * Controller for Music Select Activity,
 */
public class MusicSelectActivity extends ListActivity implements AudioService {

    private TestAudioService mService;
    private boolean mBound = false;

    private MusicFilterableAdapter adapter = null;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TestAudioService.LocalBinder binder = (TestAudioService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * Threaded callback using handler to update GUI thread.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setBackgroundColor(getResources().getColor(R.color.accent));

        updateViewsFromDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_select, menu);
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
        super.onStart();

        Intent intent = new Intent(this, TestAudioService.class);
        intent.setAction("com.example.action.PLAY");

        updateViewsFromDatabase();

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            stopAudio();
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        adapter.refreshItems(new ArrayList<MediaPlayerView>());
        stopAudio();
    }

    @Override
    public void startAudio(String path, TextView view) {
        mService.startAudio(path, view);
    }

    @Override
    public void stopAudio() {
        mService.stopAudio();
    }


    private void onDatabaseCallback(List<AlarmMedia> files){

        EditText editText = (EditText) findViewById(R.id.selection_filter);
        ListView listview = (ListView) findViewById(android.R.id.list);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        View emptyView = findViewById(R.id.empty);
        //files =  RadioStationsManager.getInstance().getRadioStations();
        List<MediaPlayerView> convertedList = new ArrayList<>();

        for(AlarmMedia currentMedia: files)
            convertedList.add(currentMedia);

        adapter = new MusicFilterableAdapter(this, convertedList);

        listview.setAdapter(adapter);
        listview.setEmptyView(emptyView);
        listview.setDividerHeight(2);

        //listview.on
        progressBar.setVisibility(View.INVISIBLE);


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void updateViewsFromDatabase(){
        final Handler handler = new Handler();
        //TODO move to another thread with everything after this being a callback
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    final  List<AlarmMedia> files = DatabaseManager.getInstance().getMediaList();
                    @Override
                    public void run() {
                        MusicSelectActivity.this.onDatabaseCallback(files);
                    }
                });
            }
        }).start();
    }
}
