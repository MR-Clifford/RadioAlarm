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

import xml_mike.radioalarm.models.RadioStation;

/**
 * Created by MClifford on 20/05/15.
 *
 * deprecated for common functionality
 */
public class RadioFilterableAdapter extends BaseAdapter implements Filterable, FilterableType {
    private String type = null;
    private List<RadioStation> originalData = null;
    private List<RadioStation> filteredData = null;
    private Context context = null;
    private LayoutInflater mInflator = null;
    private ItemFilter mFilter = new ItemFilter();

    public RadioFilterableAdapter(Context context, List<RadioStation> data){

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
        String name = filteredData.get(position).getName();
        String url =" \n NO URL FOUND";
        if(filteredData.get(position).getStreams().size() >0)
            url = " \n "+filteredData.get(position).getStreams().get(0).url;
        holder.text.setText(name+":"+url);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "" + filteredData.get(position).getId());
                returnIntent.putExtra("alarm_type", filteredData.get(position).getClass().toString());
                ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                ((Activity) context).finish();
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
        TextView text;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<RadioStation> list = originalData;

            int count = list.size();
            final ArrayList<RadioStation> nlist = new ArrayList<>(count);

            RadioStation filterableAlarm ;

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
            filteredData = (ArrayList<RadioStation>) results.values;
            notifyDataSetChanged();
        }

    }
}
