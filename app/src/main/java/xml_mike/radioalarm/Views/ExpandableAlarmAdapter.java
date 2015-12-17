package xml_mike.radioalarm.views;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.AlarmsManager;
import xml_mike.radioalarm.managers.DatabaseManager;
import xml_mike.radioalarm.managers.RadioStationsManager;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.AlarmFactory;
import xml_mike.radioalarm.models.AlarmMedia;
import xml_mike.radioalarm.models.MusicAlarm;
import xml_mike.radioalarm.models.RadioAlarm;
import xml_mike.radioalarm.models.StandardAlarm;

/**
 * Created by MClifford on 23/03/15.
 *
 * Subclassed from BaseExpandableListAdapter, will format views based on Alarms provided
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
        TextView isEnabled = (TextView) convertView.findViewById(R.id.is_enabled);
        RelativeLayout view = (RelativeLayout) convertView.findViewById(R.id.alarm_item);

        //view.setVisibility((isExpanded) ? View.INVISIBLE : View.VISIBLE);
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
                        AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), false);
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

                        if(view.isShown()) {
                            alarms.get(currentAlarm).setTimeMinute(minute);
                            alarms.get(currentAlarm).setTimeHour(hourOfDay);

                            //Global.getInstance().setAlarms(alarms);
                            AlarmsManager.getInstance().update(currentAlarm, alarms.get(currentAlarm), true);
                            callBack.notifyDataSetChanged();
                        }
                    }
                }, 0, 0, true);

                timePickerDialog.show();
            }
        });

        isEnabled.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             alarms.get(groupPosition).setEnabled(!alarms.get(groupPosition).isEnabled());
                                             // Global.getInstance().setAlarms(alarms);
                                             AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), true);
                                             callBack.notifyDataSetChanged();
                                         }
                                     }
        );


        if (alarms.get(groupPosition).isEnabled()) {
            isEnabled.setBackgroundResource(R.drawable.button_selected);
            isEnabled.setTextColor(context.getResources().getColor(R.color.background_color));
            isEnabled.setText("On");
        } else {
            isEnabled.setBackgroundResource(R.drawable.button_unselected);
            isEnabled.setTextColor(context.getResources().getColor(R.color.text_unselected));
            isEnabled.setText("Off");
        }

        String timeHour = (alarms.get(groupPosition).getTimeHour() < 10) ? "0" + alarms.get(groupPosition).getTimeHour() : "" + alarms.get(groupPosition).getTimeHour();
        String timeMinute = (alarms.get(groupPosition).getTimeMinute() < 10) ? "0" + alarms.get(groupPosition).getTimeMinute() : "" + alarms.get(groupPosition).getTimeMinute();

        alarm_name.setText(alarms.get(groupPosition).getName());
        alarm_time.setText(timeHour + ":" + timeMinute);
        //isEnabled.setChecked(alarms.get(groupPosition).isEnabled());
        isEnabled.setFocusable(false);

        TextView alarm_details  = (TextView) convertView.findViewById(R.id.alarm_details);
        ImageView   alarm_details_icon = (ImageView) convertView.findViewById(R.id.alarm_details_icon);
        if(!isExpanded) {
            alarm_details.setText(this.get_alarm_details_string(alarms.get(groupPosition)));
            //alarm_details_icon.setVisibility(View.VISIBLE);
            alarm_details_icon.setImageResource(android.R.drawable.arrow_down_float);
        } else {
            alarm_details.setText("");
            //alarm_details_icon.setVisibility(View.INVISIBLE);
            alarm_details_icon.setImageResource(android.R.drawable.arrow_up_float);
        }


     return convertView;
    }

    /*
        warning this is GUI code with the creation of new onclick listeners, very large method.
     */
    //TODO add 2.3.3 functionality as this will only work on honeycomb & up
    //TODO divide this method up for each sub section.
    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.alarm_item_config, parent, false);
        }

        final Resources thisResource = context.getResources();

        TextView alarm_name = (TextView) convertView.findViewById(R.id.alarm_name);
        TextView alarm_time = (TextView) convertView.findViewById(R.id.alarm_time);
        TextView alarm_data = (TextView) convertView.findViewById(R.id.data);
        CheckBox alarm_isEnabled = (CheckBox) convertView.findViewById(R.id.is_enabled);
        CheckBox alarm_isRepeating = (CheckBox) convertView.findViewById(R.id.repeating);
        CheckBox alarm_isVibrating = (CheckBox) convertView.findViewById(R.id.vibrating);
        LinearLayout daysLayout = (LinearLayout) convertView.findViewById(R.id.days);
        final Spinner alarm_type = (Spinner) convertView.findViewById(R.id.alarm_type);
        Button deleteButton = (Button) convertView.findViewById(R.id.alarm_delete);
        Button alarm_volume = (Button) convertView.findViewById(R.id.volume);
        //Button alarm_duration = (Button) convertView.findViewById(R.id.duration);
        //Button alarm_easing = (Button) convertView.findViewById(R.id.easing);

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
                        AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), false);
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

                        if(view.isShown()) {
                            alarms.get(currentAlarm).setTimeMinute(minute);
                            alarms.get(currentAlarm).setTimeHour(hourOfDay);

                            //Global.getInstance().setAlarms(alarms);
                            AlarmsManager.getInstance().update(currentAlarm, alarms.get(currentAlarm), true);
                            callBack.notifyDataSetChanged();
                        }
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        alarm_data.setOnClickListener(alarms.get(groupPosition).getDataOnClickListener(context, groupPosition)); //implementation on Strategy/Visitor Pattern

        alarm_isEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarms.get(groupPosition).setEnabled(!alarms.get(groupPosition).isEnabled());
                // Global.getInstance().setAlarms(alarms);
                AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), true);
                callBack.notifyDataSetChanged();
            }
        });

        alarm_isRepeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarms.get(groupPosition).setRepeating(isChecked);
                AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), true);
                //Global.getInstance().updateAlarm(alarms.get(groupPosition));
                callBack.notifyDataSetChanged();
            }
        });

        alarm_isVibrating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarms.get(groupPosition).setVibrate(isChecked);
                AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), false);
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
                callBack.notifyDataSetChanged();
            }
        });

        int alarm_type_position = 0; //default case which is alarm

        if (alarms.get(groupPosition).getClass().toString().equalsIgnoreCase(MusicAlarm.class.toString()))
            alarm_type_position = 1;
        if (alarms.get(groupPosition).getClass().toString().equalsIgnoreCase(RadioAlarm.class.toString()))
            alarm_type_position = 2;

        alarm_type.setSelection(alarm_type_position, false);

        // Terrible Hack to get around finalise, and have variable passed into subclass that can change
        // Reason: this was done as onItemSelected is called when ever view is recycled and recalled,
        // because of this the alarm object is being changed into the standard type and wiping the data field.
        final boolean[] alarm_type_onClick = {false};
        alarm_type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                alarm_type_onClick[0] = true;
                return false;
            }
        });

        alarm_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int selectedPosition, long id) { //TODO still being called during

                if( alarm_type_onClick[0]) {
                    try {
                        String className;
                        switch (selectedPosition) {
                            case 0:
                                className = StandardAlarm.class.toString();
                                AlarmsManager.getInstance().update(groupPosition, AlarmFactory.convertAlarm(className, alarms.get(groupPosition)), false);
                                break;
                            case 1:
                                className = MusicAlarm.class.toString();
                                AlarmsManager.getInstance().update(groupPosition, AlarmFactory.convertAlarm(className, alarms.get(groupPosition)), false);
                                break;
                            case 2:
                                className = RadioAlarm.class.toString();
                                AlarmsManager.getInstance().update(groupPosition, AlarmFactory.convertAlarm(className, alarms.get(groupPosition)), false);
                                break;
                            default:
                                break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        //error occured
                    }

                alarm_type_onClick[0] = false;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no action is taken
                alarm_type_onClick[0] = false;
            }
        });

        alarm_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder d = new AlertDialog.Builder(context);
                d.setTitle("Volume Easing");

                final View volume_alert_view = inflater.inflate(R.layout.volume_alert, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                volume_alert_view.setLayoutParams(lp);

                final SeekBar mv_seekbar = (SeekBar) volume_alert_view.findViewById(R.id.mv_seekBar);
                final SeekBar d_seekbar = (SeekBar) volume_alert_view.findViewById(R.id.d_seekBar);
                final SeekBar c_seekbar = (SeekBar) volume_alert_view.findViewById(R.id.c_seekBar);

                final TextView mv_textview = (TextView) volume_alert_view.findViewById(R.id.mv_textview);
                final TextView d_textview = (TextView) volume_alert_view.findViewById(R.id.d_textview);
                final TextView c_textview = (TextView) volume_alert_view.findViewById(R.id.c_textview);

                mv_seekbar.setProgress(alarms.get(groupPosition).getMaxVolume());
                d_seekbar.setProgress(alarms.get(groupPosition).getDuration());
                c_seekbar.setProgress( alarms.get(groupPosition).getEasing());

                mv_textview.setText("Max Volume : "+mv_seekbar.getProgress()+"%");
                d_textview.setText("Duration : "+d_seekbar.getProgress()+"m");
                c_textview.setText("Crescendo : "+c_seekbar.getProgress()+"m");

                mv_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mv_textview.setText("Max Volume : "+progress+"%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                d_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        d_textview.setText("Duration : "+progress+"m");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                c_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        c_textview.setText("Crescendo : "+progress+"m");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                d.setView(volume_alert_view);
                d.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int AlarmMaxVolume = mv_seekbar.getProgress();
                        int AlarmDuration = d_seekbar.getProgress();
                        int Cresendo = c_seekbar.getProgress();

                        alarms.get(groupPosition).setMaxVolume(AlarmMaxVolume);
                        alarms.get(groupPosition).setDuration(AlarmDuration);
                        alarms.get(groupPosition).setEasing(Cresendo);

                        AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), false);
                        callBack.notifyDataSetChanged();
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                d.show();
            }
        });

        String timeHour = (alarms.get(groupPosition).getTimeHour() < 10) ? "0" + alarms.get(groupPosition).getTimeHour() : "" + alarms.get(groupPosition).getTimeHour();
        String timeMinute = (alarms.get(groupPosition).getTimeMinute() < 10) ? "0" + alarms.get(groupPosition).getTimeMinute() : "" + alarms.get(groupPosition).getTimeMinute();

        alarm_time.setText(timeHour + ":" + timeMinute);
        alarm_name.setText(alarms.get(groupPosition).getName());
        alarm_isEnabled.setChecked(alarms.get(groupPosition).isEnabled());
        alarm_isRepeating.setChecked(alarms.get(groupPosition).isRepeating());
        alarm_isVibrating.setChecked(alarms.get(groupPosition).isVibrate());

        if(!alarms.get(groupPosition).getData().equals("")){
            if(alarms.get(groupPosition) instanceof StandardAlarm) {
                String alarmdata = alarms.get(groupPosition).getData();//alarm_data.setText(alarms.get(groupPosition).getData());
                Uri alarmToneName = Uri.parse(alarmdata);
                alarm_data.setText(RingtoneManager.getRingtone(context, alarmToneName).getTitle(context));

            }
            else if(alarms.get(groupPosition) instanceof MusicAlarm){
                AlarmMedia alarmMedia = DatabaseManager.getInstance().getAlarmMedia(alarms.get(groupPosition).getData());
                alarm_data.setText(alarmMedia.title);
            }
            else
                alarm_data.setText(RadioStationsManager.retrieveRadioStation(Long.parseLong(alarms.get(groupPosition).getData())).getName());//alarms.get(groupPosition).getData());
        }
        else
            alarm_data.setText("Select Audio");

        //compatibility
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (!alarms.get(groupPosition).isRepeating())
                daysLayout.setAlpha(0.2f);
            else
                daysLayout.setAlpha(1f);
        } else {
            if (!alarms.get(groupPosition).isRepeating())
                daysLayout.setVisibility(View.INVISIBLE);
            else
                daysLayout.setVisibility((View.VISIBLE));
        }

        for (int i = 0; i < daysLayout.getChildCount(); i++) {
            final int currentday = i;
            daysLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarms.get(groupPosition).setRepeatingDay(currentday, (!alarms.get(groupPosition).getRepeatingDay(currentday)));
                    //Global.getInstance().setAlarms(alarms);
                    AlarmsManager.getInstance().update(groupPosition, alarms.get(groupPosition), true);
                    //callBack.notifyDataSetChanged();
                }
            });

            if (alarms.get(groupPosition).getRepeatingDay(i)) {
                daysLayout.getChildAt(i).setBackgroundResource(R.drawable.button_selected);
                ((TextView) daysLayout.getChildAt(i)).setTextColor(thisResource.getColor(R.color.background_color));
            } else {
                daysLayout.getChildAt(i).setBackgroundResource(R.drawable.button_unselected);
                ((TextView) daysLayout.getChildAt(i)).setTextColor(thisResource.getColor(R.color.text_unselected));
            }
            daysLayout.getChildAt(i).setEnabled(alarms.get(groupPosition).isRepeating());
        }

        //alarm_duration.setText("Duration:" + alarms.get(groupPosition).getDuration());
        //alarm_easing.setText("Easing:" + alarms.get(groupPosition).getEasing());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

        if(groupPosition != currentAlarm || currentAlarm != -1){
            this.onGroupCollapsed(currentAlarm);
            Log.e("was accessed","ugh");
        }

        Log.e("test this function","this was called z" + groupPosition + ":" + currentAlarm);

        currentAlarm = groupPosition;

        super.onGroupExpanded(groupPosition);
    }

    /**
     *
     * @param alarm alarm to get the details of
     * @return description of the alarm.
     */
    private String get_alarm_details_string(Alarm alarm){

        String returnAlarmDetailsString = "";

        if(alarm.isRepeating()){
            for(int i =0; i < alarm.getRepeatingDays().length; i++){
                if(alarm.getRepeatingDays()[i]){
                    switch (i){
                        case 0: returnAlarmDetailsString += "Sun"; break;
                        case 1: returnAlarmDetailsString += "Mon"; break;
                        case 2: returnAlarmDetailsString += "Tue"; break;
                        case 3: returnAlarmDetailsString += "Wed"; break;
                        case 4: returnAlarmDetailsString += "Thu"; break;
                        case 5: returnAlarmDetailsString += "Fri"; break;
                        case 6: returnAlarmDetailsString += "Sat"; break;
                    }

                    returnAlarmDetailsString += ", ";
                }
            }
        } else {
            Calendar now = Calendar.getInstance();

            if(alarm.getTimeHour() > now.get(Calendar.HOUR_OF_DAY)) {
                returnAlarmDetailsString = "Today";
            } else if(alarm.getTimeHour() == now.get(Calendar.HOUR_OF_DAY)){
                if( alarm.getTimeMinute() > now.get(Calendar.MINUTE) )
                    returnAlarmDetailsString = "Today";
                else
                    returnAlarmDetailsString = "Tomorrow";
            } else
                returnAlarmDetailsString = "Tomorrow";
        }

        return returnAlarmDetailsString;
    }

    private DialogInterface.OnClickListener OnDeleteClickListener(){
        return null;
    }

    private DialogInterface.OnClickListener OnRepeatingClickListener(){
        return null;
    }

    private DialogInterface.OnClickListener OnVibrateClickListener(){
        return null;
    }

    private DialogInterface.OnClickListener OnDayClickListner(){
        return null;
    }
}