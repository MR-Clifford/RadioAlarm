package xml_mike.radioalarm.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

/**
 * Created by MClifford on 23/03/15.
 */
public class ExpandableAlarmAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private ArrayList<Alarm> alarms;



    public ExpandableAlarmAdapter(Context context, LayoutInflater layout, ArrayList<Alarm> alarms ){
        super();
        this.inflater = layout;
        this.alarms = alarms;
    }

    @Override
    public int getGroupCount() {
        return alarms.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return alarms.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return alarms.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return alarms.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {



        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
