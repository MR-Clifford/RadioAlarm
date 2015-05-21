package xml_mike.radioalarm.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MClifford on 13/05/15.
 */
public class RadioFactory {

    /**
     *
     * @return RadioStation generated from parameters
     */
    public static RadioStation generateRadionStation(long id, String name, String description, String slug, String country, String website, String updated, ArrayList<RadioStream> streams, ArrayList<RadioCategory> categories){
        RadioStation newRadioStation = new RadioStation();

        newRadioStation.setId(id);
        newRadioStation.setName(name);
        newRadioStation.setDescription(description);
        newRadioStation.setSlug(slug);
        newRadioStation.setCountry(country);
        newRadioStation.setWebsite(website);
        newRadioStation.setUpdated(updated);
        newRadioStation.setStreams(streams);
        newRadioStation.setCategories(categories);

        return newRadioStation;
    }


    /**
     * create a RadioStation Object based on a JSON RESTFul API Drible
     * @param jsonObject return string from
     * @return RadioStation generated from string
     */
    public static RadioStation generateRadionStation(JSONObject jsonObject){

        RadioStation newRadioStation = new RadioStation();
        try {

            newRadioStation.setId(jsonObject.getInt("id")); //all elements have an idea or else it will not be added to database
            if(jsonObject.has("name"))
                newRadioStation.setName(jsonObject.getString("name"));
            if(jsonObject.has("description"))
                newRadioStation.setDescription(jsonObject.getString("description"));
            if(jsonObject.has("slug"))
                newRadioStation.setSlug(jsonObject.getString("slug"));
            if(jsonObject.has("country"))
                newRadioStation.setCountry(jsonObject.getString("country"));
            if(jsonObject.has("website"))
                newRadioStation.setWebsite(jsonObject.getString("website"));
            if(jsonObject.has("updated"))
                newRadioStation.setUpdated(jsonObject.getString("updated"));

            newRadioStation.setStreams(getStreams(jsonObject.getLong("id"), jsonObject.getJSONArray("streams")));
            newRadioStation.setCategories(getCategories(jsonObject.getJSONArray("categories")));

        }catch(JSONException e){
            Log.e("JsonError",e.toString());
        }

        return newRadioStation;

    }

    private static ArrayList<RadioStream> getStreams(long radioid,JSONArray streamJSONObjects){
        ArrayList<RadioStream> streams = new ArrayList<>();

        try {
            for (int i = 0; i < streamJSONObjects.length(); i++) {
                JSONObject tempJsonObject = streamJSONObjects.getJSONObject(i);

                if(tempJsonObject.has("stream")) {
                    if (tempJsonObject.getString("stream").length() > 0) {
                        RadioStream newStream = new RadioStream();
                        newStream.radioId = radioid;
                        newStream.url = tempJsonObject.getString("stream");
                        streams.add(newStream);
                    }
                    else
                        Log.e("Stream is empty", "Station:"+radioid + " has an empty stream" );
                }

            }
        }
        catch(JSONException e){
            Log.e("RadioFactory:","getStreams"+e.toString());
        }

        return streams;
    }

    private static ArrayList<RadioCategory> getCategories(JSONArray streamJSONObjects){
        ArrayList<RadioCategory> categories = new ArrayList<>();

        try {
            for (int i = 0; i < streamJSONObjects.length(); i++) {
                JSONObject tempJsonObject = streamJSONObjects.getJSONObject(i);
                RadioCategory newCategory = new RadioCategory();

                newCategory.id = tempJsonObject.getLong("id");

                if(tempJsonObject.has("title"))
                    newCategory.title = tempJsonObject.getString("title");
                if(tempJsonObject.has("description"))
                    newCategory.description = tempJsonObject.getString("description");
                if(tempJsonObject.has("slug"))
                    newCategory.slug = tempJsonObject.getString("slug");

                categories.add(newCategory);
            }
        }
        catch(JSONException e){
            Log.e("RadioFactory:","getCategories"+e.toString());
        }

        return categories;
    }
}
