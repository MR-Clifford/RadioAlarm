package xml_mike.radioalarm.models;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MClifford on 23/02/15.
 */
public class AlarmAdapter extends ArrayAdapter<Alarm>{

    ArrayList<Alarm> alarms = new ArrayList<>();

    public AlarmAdapter(Context context, int resource, List<Alarm> objects) {
        super(context, resource, objects);
        alarms.addAll(objects);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public Alarm getItem(int i){
        return alarms.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    private static class ViewHolder{
        public TextView name;
        public TextView time;
        public TextView days;

    }

}
