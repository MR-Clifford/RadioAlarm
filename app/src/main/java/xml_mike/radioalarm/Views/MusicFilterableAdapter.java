package xml_mike.radioalarm.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xml_mike.radioalarm.models.AlarmMedia;

/**
 * Created by MClifford on 17/04/15.
 */
public class MusicFilterableAdapter extends BaseAdapter implements Filterable {

    private List<AlarmMedia>  originalData = null;
    private List<AlarmMedia> filteredData = null;
    private Context context = null;
    private LayoutInflater mInflator = null;
    private ItemFilter mFilter = new ItemFilter();

    public MusicFilterableAdapter(Context context, List<AlarmMedia> data ){
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
        ViewHolder holder;

        if (convertView == null) {
             convertView = mInflator.inflate(android.R.layout.simple_list_item_1, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(android.R.id.text1);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.text.setText(filteredData.get(position).title);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", filteredData.get(position).id);
                ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                ((Activity) context).finish();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<AlarmMedia> list = originalData;

            int count = list.size();
            final ArrayList<AlarmMedia> nlist = new ArrayList<AlarmMedia>(count);

            AlarmMedia filterableAlarm ;

            for (int i = 0; i < count; i++) {
                filterableAlarm = list.get(i);
                if (filterableAlarm.title.toLowerCase().contains(filterString)) {
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
            filteredData = (ArrayList<AlarmMedia>) results.values;
            notifyDataSetChanged();
        }

    }
}
