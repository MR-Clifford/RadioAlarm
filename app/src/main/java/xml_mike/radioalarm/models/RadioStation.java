package xml_mike.radioalarm.models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by MClifford on 13/05/15.
 */
public class RadioStation implements MediaPlayerView{

    long id;
    String name;
    String description;
    String slug;
    String country;
    String website;
    String updated;
    boolean playing;

    ArrayList<RadioStream> streams; //one radio station can have many streams but never less than one.
    ArrayList<RadioCategory> categories; //one radio station can have many categories and one category can have many radio stations, as referenced in database

    public RadioStation(){
        id=0L;
        name="";
        description="";
        slug="";
        country="";
        website="";
        updated="";
        playing = false;
    }

    public RadioStation(long id, String name, String description, String slug, String country, String website, String updated, ArrayList<RadioStream> streams, ArrayList<RadioCategory> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.country = country;
        this.website = website;
        this.updated = updated;
        this.streams = streams;
        this.categories = categories;
    }

    @Override
    public String getStringId() {
        return ""+id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        if(description.length() >0)
            return description;
        else {
            String returnString ="";
            for(RadioCategory category : categories)
                returnString = returnString+" "+category.description;
            Log.e("[test]", returnString);
            return returnString;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public ArrayList<RadioStream> getStreams() {
        return streams;
    }

    public void setStreams(ArrayList<RadioStream> streams) {
        this.streams = streams;
    }

    public ArrayList<RadioCategory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<RadioCategory> categories) {
        this.categories = categories;
    }

    @Override
    public String getData() {
        return streams.get(0).url;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public boolean setPlaying(boolean boo) {
        playing = boo;
        return boo;
    }
}

