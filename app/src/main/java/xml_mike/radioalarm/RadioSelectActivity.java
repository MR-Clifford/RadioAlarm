package xml_mike.radioalarm;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import xml_mike.radioalarm.managers.RadioStationsManager;
import xml_mike.radioalarm.models.RadioStation;
import xml_mike.radioalarm.views.RadioFilterableAdapter;

/**
 * Created by MClifford on 20/05/15.
 */
public class RadioSelectActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        EditText editText = (EditText) findViewById(R.id.selection_filter);

        ListView listview = (ListView) findViewById(R.id.selection_filter_list);

        List<RadioStation> files = RadioStationsManager.getInstance().getRadioStations();
        final RadioFilterableAdapter adapter = new RadioFilterableAdapter(this, files);

        listview.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
