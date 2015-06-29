package xml_mike.radioalarm.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xml_mike.radioalarm.R;
import xml_mike.radioalarm.managers.TestAudioService;
import xml_mike.radioalarm.models.MediaPlayerView;

/**
 * Created by MClifford on 17/04/15.
 */
public class MusicFilterableAdapter extends BaseAdapter implements Filterable, FilterableType {

    private String type = null;
    private List<MediaPlayerView>  originalData = null;
    private List<MediaPlayerView> filteredData = null;
    private Context context = null;
    private LayoutInflater mInflator = null;
    private ItemFilter mFilter = new ItemFilter();

    public MusicFilterableAdapter(Context context, List<MediaPlayerView> data ){
        this.originalData = data;
        this.context = context;
        this.mInflator = LayoutInflater.from(context);
        this.filteredData = new ArrayList<>();

        for (int i = 0; i < originalData.size(); i++)
            filteredData.add(originalData.get(i));
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return originalData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
             convertView = mInflator.inflate(R.layout.list_item_select_media, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.text1);
            holder.description = (TextView) convertView.findViewById(R.id.text2);
            holder.testAudio = (Button) convertView.findViewById(R.id.button1);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.title.setText(filteredData.get(position).getName());
        holder.description.setText(filteredData.get(position).getDescription());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", filteredData.get(position).getStringId());
                returnIntent.putExtra("alarm_type", filteredData.get(position).getClass().toString());
                ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                ((Activity) context).finish();
            }
        });

        holder.testAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test1",holder.testAudio.getText().toString());
                if(holder.testAudio.getText().toString().equalsIgnoreCase("play")) {
                    Log.e("test2",holder.testAudio.getText().toString());

                    holder.testAudio.setText("Stop");
                    Toast.makeText(context, "Testing this function", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, TestAudioService.class);

                    intent.putExtra("test", filteredData.get(position).getData());
                    intent.setAction("com.example.action.PLAY");

                    context.startService(intent);
                } else {
                    Log.e("asd","asd");
                    holder.testAudio.setText("Play");
                    Intent intent = new Intent(context, TestAudioService.class);

                    intent.putExtra("test", filteredData.get(position).getData());
                    intent.setAction("com.example.action.STOP");

                    context.startService(intent);
                }
            }
        });

        return convertView;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String t) {
        type = t;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    static class ViewHolder {
        TextView title;
        TextView description;
        Button testAudio;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<MediaPlayerView> list = originalData;

            int count = list.size();
            final ArrayList<MediaPlayerView> nlist = new ArrayList<MediaPlayerView>(count);

            MediaPlayerView filterableAlarm ;

            for (int i = 0; i < count; i++) {
                filterableAlarm = list.get(i);
                if (filterableAlarm.getName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableAlarm);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<MediaPlayerView>) results.values;
            notifyDataSetChanged();
        }

    }
}
