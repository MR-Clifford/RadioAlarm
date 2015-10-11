package xml_mike.radioalarm.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import xml_mike.radioalarm.models.AlarmMedia;

/**
 * Created by MClifford on 11/07/15. dialog
 *
 * issue passing cursor to create
 */
public class AlarmMediaAdapter extends ArrayAdapter<AlarmMedia> {

    public AlarmMediaAdapter(Context context, int resource, AlarmMedia[] objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AlarmMedia alarm = getItem(position);
        //Log.e("alarm name",alarm.getName());
                // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_checked, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(android.R.id.text1);

        // Populate the data into the template view using the data object
        tvName.setText(alarm.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
