package xml_mike.radioalarm.models;

/**
 * Created by MClifford on 18/05/15.
 */
public class RadioCategory{

    public long id;
    public String title;
    public String description;
    public String slug;

    public RadioCategory(){
        this.id = 0L;
        this.title = "";
        this.description = "";
        this.slug = "";
    } //default constructor

    public RadioCategory(long id, String title, String description, String slug) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.slug = slug;
    }
}