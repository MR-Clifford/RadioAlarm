package xml_mike.radioalarm.models;

import java.util.ArrayList;

/**
 * Created by MClifford on 13/05/15.
 */
public class RadioStation {

    long id;
    String name;
    String description;
    String slug;
    String country;
    String website;
    String updated;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
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
}

