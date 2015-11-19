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
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.AudioService;
import xml_mike.radioalarm.managers.RadioStationsManager;
import xml_mike.radioalarm.managers.TestAudioService;
import xml_mike.radioalarm.models.MediaPlayerView;
import xml_mike.radioalarm.models.RadioStation;
import xml_mike.radioalarm.views.MusicFilterableAdapter;

/**
 * Created by MClifford on 20/05/15.
 */
public class RadioSelectActivity extends ListActivity implements AudioService {

    MusicFilterableAdapter adapter = null;
    private TestAudioService mService;
    private boolean mBound = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        updateViewsFromDatabase();
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

        if(adapter != null)
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

    private void onDatabaseCallback(List<RadioStation> files){

        EditText editText = (EditText) findViewById(R.id.selection_filter);
        ListView listview = (ListView) findViewById(android.R.id.list);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        View emptyView = findViewById(R.id.empty);
        //files =  RadioStationsManager.getInstance().getRadioStations();
        List<MediaPlayerView> convertedList = new ArrayList<>();

        for(RadioStation currentMedia: files)
            convertedList.add(currentMedia);

        adapter = new MusicFilterableAdapter(this, convertedList);

        listview.setAdapter(adapter);
        listview.setEmptyView(emptyView);
        listview.setDividerHeight(2);
        progressBar.setVisibility(View.GONE);

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
                    final List<RadioStation> files = RadioStationsManager.getInstance().getRadioStations();

                    @Override
                    public void run() {
                        RadioSelectActivity.this.onDatabaseCallback(files);
                    }
                });
            }
        }).start();
    }
}
