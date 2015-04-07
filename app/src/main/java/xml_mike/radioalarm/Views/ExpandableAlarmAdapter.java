package xml_mike.radioalarm.Views;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.managers.DatabaseManager;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.AlarmFactory;
import xml_mike.radioalarm.models.MusicAlarm;
import xml_mike.radioalarm.models.RadioAlarm;
import xml_mike.radioalarm.models.StandardAlarm;

/**
 * Created by MClifford on 23/03/15.
 */
public class ExpandableAlarmAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Alarm> alarms;
    private int currentAlarm = -1;

    public ExpandableAlarmAdapter(Context context, LayoutInflater layout, ArrayList<Alarm> alarms) {
        //super();
        this.context = context;
        this.inflater = layout;
        this.alarms = alarms;
    }

    @Override
    public int getGroupCount() {
        return alarms.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.alarm_item, parent, false);
        }

        final ExpandableAlarmAdapter callBack = this;

        TextView alarm_name = (TextView) convertView.findViewById(R.id.alarm_name);
        TextView alarm_time = (TextView) convertView.findViewById(R.id.alarm_time);
        CheckBox isEnabled = (CheckBox) convertView.findViewById(R.id.is_enabled);
        LinearLayout view = (LinearLayout) convertView.findViewById(R.id.alarm_item);

        view.setVisibility((isExpanded) ? View.INVISIBLE : View.VISIBLE);

        isEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarms.get(groupPosition).setEnabled(isChecked);
                // Global.getInstance().setAlarms(alarms);
                AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition));
                callBack.notifyDataSetChanged();
            }
        });

        String timeHour = (alarms.get(groupPosition).getTimeHour() < 10) ? "0" + alarms.get(groupPosition).getTimeHour() : "" + alarms.get(groupPosition).getTimeHour();
        String timeMinute = (alarms.get(groupPosition).getTimeMinute() < 10) ? "0" + alarms.get(groupPosition).getTimeMinute() : "" + alarms.get(groupPosition).getTimeMinute();

        alarm_name.setText(alarms.get(groupPosition).getName());
        alarm_time.setText(timeHour + ":" + timeMinute);
        isEnabled.setChecked(alarms.get(groupPosition).isEnabled());
        isEnabled.setFocusable(false);

        return convertView;
    }

    /*
        warning this is GUI code with the creation of new onclick listeners, very large method.
     */
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.alarm_item_config, parent, false);
        }

        final Resources thisResource = context.getResources();

        TextView alarm_name = (TextView) convertView.findViewById(R.id.alarm_name);
        TextView alarm_time = (TextView) convertView.findViewById(R.id.alarm_time);
        CheckBox alarm_isEnabled = (CheckBox) convertView.findViewById(R.id.is_enabled);
        CheckBox alarm_isRepeating = (CheckBox) convertView.findViewById(R.id.repeating);
        CheckBox alarm_isVibrating = (CheckBox) convertView.findViewById(R.id.vibrating);
        LinearLayout daysLayout = (LinearLayout) convertView.findViewById(R.id.days);
        Spinner alarm_type = (Spinner) convertView.findViewById(R.id.alarm_type);
        Button deleteButton = (Button) convertView.findViewById(R.id.alarm_delete);

        final ExpandableAlarmAdapter callBack = this;

        alarm_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setMessage("Set Alarm Name");
                alertDialog.setView(input);
                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alarms.get(groupPosition).setName(input.getText().toString());
                        AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition));
                        //Global.getInstance().setAlarms(alarms);
                        callBack.notifyDataSetChanged();
                    }
                })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                alertDialog.create().show();
            }
        });

        alarm_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAlarm = groupPosition;
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if (currentAlarm >= 0) {
                            alarms.get(currentAlarm).setTimeMinute(minute);
                            alarms.get(currentAlarm).setTimeHour(hourOfDay);

                            //Global.getInstance().setAlarms(alarms);
                            AlarmsManager.getInstance().update(currentAlarm, alarms.get(currentAlarm));
                            callBack.notifyDataSetChanged();
                        }

                        currentAlarm = -1;
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        alarm_isEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarms.get(groupPosition).setEnabled(isChecked);
                AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition));
                //Global.getInstance().updateAlarm(alarms.get(groupPosition));
                callBack.notifyDataSetChanged();
            }
        });

        alarm_isRepeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarms.get(groupPosition).setRepeating(isChecked);
                AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition));
                //Global.getInstance().updateAlarm(alarms.get(groupPosition));
                callBack.notifyDataSetChanged();
            }
        });

        alarm_isVibrating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarms.get(groupPosition).setVibrate(isChecked);
                AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition));
                //Global.getInstance().updateAlarm(alarms.get(groupPosition));
                callBack.notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // callBack.alarms.remove(groupPosition);
                DatabaseManager.getInstance().removeDatabaseItem(alarms.get(groupPosition));
                AlarmsManager.getInstance().remove(groupPosition);
                //Global.getInstance().removeAlarm(groupPosition);
            }
        });

        alarm_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String className = "";

                switch (position) {
                    case 0:
                        className = StandardAlarm.class.toString();
                        break;
                    case 1:
                        className = MusicAlarm.class.toString();
                        break;
                    case 2:
                        className = RadioAlarm.class.toString();
                        break;
                }

                AlarmsManager.getInstance().update(groupPosition, AlarmFactory.convertAlarm(className, alarms.get(groupPosition)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String timeHour = (alarms.get(groupPosition).getTimeHour() < 10) ? "0" + alarms.get(groupPosition).getTimeHour() : "" + alarms.get(groupPosition).getTimeHour();
        String timeMinute = (alarms.get(groupPosition).getTimeMinute() < 10) ? "0" + alarms.get(groupPosition).getTimeMinute() : "" + alarms.get(groupPosition).getTimeMinute();

        alarm_time.setText(timeHour + ":" + timeMinute);
        alarm_name.setText(alarms.get(groupPosition).getName());
        alarm_isEnabled.setChecked(alarms.get(groupPosition).isEnabled());
        alarm_isRepeating.setChecked(alarms.get(groupPosition).isRepeating());
        alarm_isVibrating.setChecked(alarms.get(groupPosition).isVibrate());

        int alarm_type_position = 0;

        if (alarms.get(groupPosition).getClass().toString().equalsIgnoreCase(MusicAlarm.class.toString()))
            alarm_type_position = 1;
        if (alarms.get(groupPosition).getClass().toString().equalsIgnoreCase(RadioAlarm.class.toString()))
            alarm_type_position = 2;

        alarm_type.setSelection(alarm_type_position);

        if (!alarms.get(groupPosition).isRepeating())
            daysLayout.setAlpha(0.5f);
        else
            daysLayout.setAlpha(1f);

        for (int i = 0; i < daysLayout.getChildCount(); i++) {
            final int currentday = i;
            daysLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarms.get(groupPosition).setRepeatingDay(currentday, (!alarms.get(groupPosition).getRepeatingDay(currentday)));
                    //Global.getInstance().setAlarms(alarms);
                    AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition));
                    callBack.notifyDataSetChanged();
                }
            });

            if (alarms.get(groupPosition).getRepeatingDay(i)) {
                daysLayout.getChildAt(i).setBackgroundResource(R.color.text_background_selected);
                ((TextView) daysLayout.getChildAt(i)).setTextColor(thisResource.getColor(R.color.text_selected));
            } else {
                daysLayout.getChildAt(i).setBackgroundResource(R.color.text_background_unselected);
                ((TextView) daysLayout.getChildAt(i)).setTextColor(thisResource.getColor(R.color.text_unselected));
            }
            daysLayout.getChildAt(i).setEnabled(alarms.get(groupPosition).isRepeating());

        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
