package xml_mike.radioalarm.managers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.AlarmFactory;
import xml_mike.radioalarm.models.AlarmMedia;
import xml_mike.radioalarm.models.RadioCategory;
import xml_mike.radioalarm.models.RadioFactory;
import xml_mike.radioalarm.models.RadioStation;
import xml_mike.radioalarm.models.RadioStream;

/**
 * Created by MClifford on 23/03/15.
 * This class acts as a medium for collection and storing all the relational data, via the broker pattern
 * Every time a database object is called/changed this class will update the fields required.
 */
public class DatabaseManager {
    private static DatabaseManager ourInstance;
    private ArrayList<AlarmMedia> songsList;

    private DatabaseManager() {
        songsList = getLocalMedia();
    }

    public static DatabaseManager getInstance() {

        if(ourInstance == null)
            ourInstance = new DatabaseManager();

        return ourInstance;
    }

    /**
     * add a new alarm object to the database, then update its id field to one stored in the database
     * @param alarm
     */
    public void addDatabaseItem(Alarm alarm){
        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AlarmSchema.TIME_HOUR,alarm.getTimeHour());
        values.put(AlarmSchema.TIME_MINUTE,alarm.getTimeMinute());
        values.put(AlarmSchema.REPEATING_DAYS,alarm.getDBRepeatingDays());
        values.put(AlarmSchema.REPEAT,alarm.isRepeating() ? 1:0);
        values.put(AlarmSchema.IS_VIBRATING,alarm.isVibrate() ? 1:0);
        values.put(AlarmSchema.NAME,alarm.getName());
        values.put(AlarmSchema.IS_ENABLED,alarm.getName());
        values.put(AlarmSchema.TYPE,alarm.getName());
        values.put(AlarmSchema.DATA,alarm.getName());
        values.put(AlarmSchema.DURATION,alarm.getDuration());
        values.put(AlarmSchema.EASING,alarm.getEasing());

        long id = db.insert(AlarmSchema.TABLE_NAME, "NULL", values);

        alarm.setId(id);

        db.close();
        //Global.getInstance().updateAlarm(alarm);
    }

    /**
     * update the database with the object provided
     * @param alarm
     */
    public void updateDataBaseItem(Alarm alarm){
        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlarmSchema.TIME_HOUR,alarm.getTimeHour());
        values.put(AlarmSchema.TIME_MINUTE,alarm.getTimeMinute());
        values.put(AlarmSchema.REPEATING_DAYS,alarm.getDBRepeatingDays());
        values.put(AlarmSchema.REPEAT,alarm.isRepeating() ? 1:0);
        values.put(AlarmSchema.IS_VIBRATING,alarm.isVibrate() ? 1:0);
        values.put(AlarmSchema.NAME,alarm.getName());
        values.put(AlarmSchema.IS_ENABLED,alarm.isEnabled() ? 1:0);
        values.put(AlarmSchema.TYPE,alarm.getClass().toString());
        values.put(AlarmSchema.DATA,alarm.getData());
        values.put(AlarmSchema.DURATION,alarm.getDuration());
        values.put(AlarmSchema.EASING,alarm.getEasing());

        String selection = AlarmSchema.ID + " = "+alarm.getId();

        String[] selectionArgs = {};//{"'"+String.valueOf(alarm.getId())+"'"};

        db.update(AlarmSchema.TABLE_NAME,values, selection, selectionArgs);
        db.close();
    }

    /**
     * remove the provided object from the database,
     * @param alarm
     */
    public void removeDatabaseItem(Alarm alarm){
        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();
        // Define 'where' part of query.
        String selection = AlarmSchema.ID + " = ?";
        // Specify arguments in placeholder order.
        Log.d("Try to delete","id:"+(alarm.getId()));
        String[] selectionArgs = {String.valueOf(alarm.getId())};
        // Issue SQL statement.
        db.delete(AlarmSchema.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    /**
     * get all the alarm items stored in the database in an arrayList
     * @return
     */
    public ArrayList<Alarm> getAlarmDatabaseItems(){

        ArrayList<Alarm> returnList = new ArrayList<>();

        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();

        String[] projection = {
                AlarmSchema.ID,
                AlarmSchema.TIME_HOUR,
                AlarmSchema.TIME_MINUTE,
                AlarmSchema.REPEATING_DAYS,
                AlarmSchema.REPEAT,
                AlarmSchema.IS_VIBRATING,
                AlarmSchema.NAME,
                AlarmSchema.IS_ENABLED,
                AlarmSchema.TYPE,
                AlarmSchema.DATA, //changes depending on alarm type
                AlarmSchema.DURATION,
                AlarmSchema.EASING
        };

        //initialise variables
        String sortOrder = AlarmSchema.NAME + " Desc";
        String[] selectionArgs = {};
        String selection = "";

        Cursor cursor = db.query(AlarmSchema.TABLE_NAME, projection, null, null, null, null, sortOrder);

        cursor.moveToFirst();

        if(cursor.getCount() > 0){
            do{
                //Collect data needed to create objects
                String ALARM_ID = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.ID));
                int ALARM_TIME_HOUR = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.TIME_HOUR));
                int ALARM_TIME_MINTUE = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.TIME_MINUTE));
                String ALARM_REPEATING_DAYS = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.REPEATING_DAYS));
                int ALARM_REPEAT = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.REPEAT));
                String ALARM_NAME = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.NAME));
                int ALARM_IS_ENABLED = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.IS_ENABLED));
                int ALARM_IS_VIBRATING = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.IS_VIBRATING));
                String ALARM_TYPE = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.TYPE));
                String ALARM_DATA = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.DATA));
                int ALARM_DURATION = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.DURATION));
                int ALARM_EASING = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.EASING));

                Alarm alarm = AlarmFactory.createAlarm(
                        Long.parseLong(ALARM_ID, 10),
                        ALARM_TYPE,
                        ALARM_NAME,
                        ALARM_DATA,
                        ALARM_REPEATING_DAYS,
                        ALARM_TIME_HOUR,
                        ALARM_TIME_MINTUE,
                        ALARM_IS_ENABLED,
                        ALARM_REPEAT,
                        ALARM_IS_VIBRATING,
                        ALARM_DURATION,
                        ALARM_EASING
                );

                returnList.add(alarm);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return returnList;
    }

    /**
     * return all media items from database
     * @return
     */
    public List<AlarmMedia> getMediaList(){
        return songsList;
    }

    /**
     * return all local media from phone, this is ringtones and normal alarm sounds
     * @return
     */
    private ArrayList<AlarmMedia> getLocalMedia(){

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        ContentResolver cr = Global.getInstance().getContentResolver();

        Uri uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = cr.query(uri2, projection, selection, null, sortOrder);

        ArrayList<AlarmMedia> songs = new ArrayList<>();

        if(cursor != null) {
            Log.d("DataBaseManager", "Total Media on phone:" + cursor.getCount());

            while(cursor.moveToNext())
                songs.add(new AlarmMedia(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5) ));

            cursor.close();
        }
        else
            Log.e("DatabaseManager","Cursor was null");

        return songs;
    }

    /**
     * this returns all the various details from a given id, this id is known by the android operating system
     * @param id
     * @return
     */
    public AlarmMedia getAlarmMedia(String id){

        AlarmMedia alarmMedia = new AlarmMedia();
        try {
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION
            };

            ContentResolver cr = Global.getInstance().getContentResolver();

            Uri uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media._ID + " = " + id;
            String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
            Cursor cursor = cr.query(uri2, projection, selection, null, sortOrder);

            while (cursor.moveToNext())
                alarmMedia = new AlarmMedia(
                        cursor.getString(0), // MediaStore.Audio.Media._ID,
                        cursor.getString(1), // MediaStore.Audio.Media.ARTIST,
                        cursor.getString(2), // MediaStore.Audio.Media.TITLE,
                        cursor.getString(3), // MediaStore.Audio.Media.DATA,
                        cursor.getString(4), // MediaStore.Audio.Media.DISPLAY_NAME,
                        cursor.getString(5)  // MediaStore.Audio.Media.DURATION
                );
            cursor.close();
        }catch (Exception e){
            Log.d("DatabaseManager",e.toString());
        }

        return alarmMedia;
    }

    /**
     * this adds a radio station to the database
     * @param radioStation
     */
    public void addRadioStation(RadioStation radioStation){
        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();
        try{
            ContentValues values = new ContentValues();

            values.put(RadioStationSchema.ID, radioStation.getId());
            values.put(RadioStationSchema.COUNTRY, radioStation.getCountry());
            values.put(RadioStationSchema.DESCRIPTION, radioStation.getDescription());
            values.put(RadioStationSchema.NAME, radioStation.getName());
            values.put(RadioStationSchema.SLUG, radioStation.getSlug());
            values.put(RadioStationSchema.WEBSITE, radioStation.getWebsite());
            values.put(RadioStationSchema.UPDATED, radioStation.getUpdated());

            Log.d("Test:RadioStation", "Id" + radioStation.getId() + " Name:" + radioStation.getName() + " Slug:" + radioStation.getSlug() + " Country:" + radioStation.getCountry());

            if(!ifRecordExists(RadioStationSchema.TABLE_NAME, "" + radioStation.getId())){
                db.insert(RadioStationSchema.TABLE_NAME, "NULL", values);

                if(radioStation.getStreams().size() >0) {
                    for (int i =0; i < radioStation.getStreams().size(); i++) {
                        ContentValues streamValues = new ContentValues();

                        streamValues.put(RadioStationStreamSchema.RADIO_ID, radioStation.getId());
                        streamValues.put(RadioStationStreamSchema.URL, radioStation.getStreams().get(i).url);

                        radioStation.getStreams().get(i).id = db.insert(RadioStationStreamSchema.TABLE_NAME, "NULL", streamValues);
                    }
                }

                if(radioStation.getCategories().size() >0) {
                    for (RadioCategory category : radioStation.getCategories()) {
                        ContentValues categoryValues = new ContentValues();
                        ContentValues categoryRelationValues = new ContentValues();

                        categoryValues.put(RadioStationCategorySchema.ID, category.id);
                        categoryValues.put(RadioStationCategorySchema.TITLE, category.title);
                        categoryValues.put(RadioStationCategorySchema.SLUG, category.slug);
                        categoryValues.put(RadioStationCategorySchema.DESCRIPTION, category.description);

                        categoryRelationValues.put(RadioStationCategoryRelationSchema.RADIO_ID, radioStation.getId());
                        categoryRelationValues.put(RadioStationCategoryRelationSchema.CATEGORY_ID, category.id);

                        if (this.ifRecordExists(RadioStationCategorySchema.TABLE_NAME, "" + category.id))
                            db.insert(RadioStationCategorySchema.TABLE_NAME, "NULL", categoryValues);

                        db.insert(RadioStationCategoryRelationSchema.TABLE_NAME, "NULL", categoryRelationValues);
                    }
                }
            }
        }catch (Exception e){
            Log.d("Database Issue", radioStation.getId()+" :Exception"+e.toString());
        }
        finally {
            db.close();
        }
    }

    public RadioStation getRadioStation(long id){

        RadioStation returnStation = new RadioStation();

        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getReadableDatabase();

        String[] projection = {
                RadioStationSchema.ID,
                RadioStationSchema.COUNTRY,
                RadioStationSchema.DESCRIPTION,
                RadioStationSchema.NAME,
                RadioStationSchema.SLUG,
                RadioStationSchema.UPDATED,
                RadioStationSchema.WEBSITE
        };

        //initialise variables
        String sortOrder = RadioStationSchema.NAME + " Desc";
        String selection = RadioStationSchema.ID+" = "+id  ;

        Cursor cursor = db.query(RadioStationSchema.TABLE_NAME, projection, selection, null, null, null, sortOrder);

        cursor.moveToFirst();

        if(cursor.getCount() > 0){
            do{
                //Collect data needed to create objects
                String RADIO_ID = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.ID));
                String RADIO_NAME = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.NAME));
                String RADIO_COUNTRY = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.COUNTRY));
                String RADIO_DESCRIPTION = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.DESCRIPTION));
                String RADIO_SLUG = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.SLUG));
                String RADIO_WEBSITE = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.WEBSITE));
                String RADIO_UPDATED = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.UPDATED));

                returnStation = RadioFactory.generateRadionStation(
                        Long.parseLong(RADIO_ID),
                        RADIO_NAME,
                        RADIO_DESCRIPTION,
                        RADIO_SLUG,
                        RADIO_COUNTRY,
                        RADIO_WEBSITE,
                        RADIO_UPDATED,
                        this.getRadioStationStreams(Long.parseLong(RADIO_ID)),
                        this.getRadioStationCategories(Long.parseLong(RADIO_ID))
                );
            } while(cursor.moveToNext());
        }

        Log.d("TESTDATABASE", "Total:"+returnStation.toString()+": cursor:"+cursor.getCount());
        cursor.close();
        db.close();

        return returnStation;
    }


    /**
     * get all the radio stations stored on device
     * @return
     */
    public ArrayList<RadioStation> getAllRadioStations(){
        ArrayList<RadioStation> returnArrayList = new ArrayList<>();

        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getReadableDatabase();

        String[] projection = {
                RadioStationSchema.ID,
                RadioStationSchema.COUNTRY,
                RadioStationSchema.DESCRIPTION,
                RadioStationSchema.NAME,
                RadioStationSchema.SLUG,
                RadioStationSchema.UPDATED,
                RadioStationSchema.WEBSITE
        };

        //initialise variables
        String sortOrder = RadioStationSchema.NAME + " Desc";

        Cursor cursor = db.query(RadioStationSchema.TABLE_NAME, projection, null, null, null, null, sortOrder);

        cursor.moveToFirst();

        if(cursor.getCount() > 0){
            do{
                //Collect data needed to create objects
                String RADIO_ID = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.ID));
                String RADIO_NAME = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.NAME));
                String RADIO_COUNTRY = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.COUNTRY));
                String RADIO_DESCRIPTION = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.DESCRIPTION));
                String RADIO_SLUG = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.SLUG));
                String RADIO_WEBSITE = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.WEBSITE));
                String RADIO_UPDATED = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationSchema.UPDATED));

                RadioStation radioStation = RadioFactory.generateRadionStation(
                        Long.parseLong(RADIO_ID),
                        RADIO_NAME,
                        RADIO_DESCRIPTION,
                        RADIO_SLUG,
                        RADIO_COUNTRY,
                        RADIO_WEBSITE,
                        RADIO_UPDATED,
                        this.getRadioStationStreams(Long.parseLong(RADIO_ID)),
                        this.getRadioStationCategories(Long.parseLong(RADIO_ID))
                );

                returnArrayList.add(radioStation);

            } while(cursor.moveToNext());
        }

        cursor.close(); //close database connection
        db.close(); //close database link

        return returnArrayList;
    }

    /**
     *
     * @param id id of radio station associated with this stream
     * @return return all the streams for a radio station, default used is 0
     */
    public ArrayList<RadioStream> getRadioStationStreams(long id){

        ArrayList<RadioStream> streams = new ArrayList<>();
        if(id >0) {
            SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getReadableDatabase();

            String[] projection = {
                    RadioStationStreamSchema.ID,
                    RadioStationStreamSchema.RADIO_ID,
                    RadioStationStreamSchema.URL,
            };

            //initialise variables
            String sortOrder = null;
            //String[] selectionArgs = null;
            String selection = RadioStationStreamSchema.RADIO_ID + " =  '" +id+"'";

            Cursor cursor = db.query(RadioStationStreamSchema.TABLE_NAME, projection, selection, null, null, null, sortOrder);

            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                do {
                    Log.d("Paramount SQL:", "" + cursor.getCount());
                    String ID = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationStreamSchema.ID));
                    String RADIO_ID = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationStreamSchema.RADIO_ID));
                    String URL = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationStreamSchema.URL));

                    streams.add(new RadioStream(
                            Long.parseLong(ID),
                            Long.parseLong(RADIO_ID),
                            URL
                    ));
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        }
        return streams;
    }

    /**
     * get all the categories that a radio station has
     * @param id id of radio station
     * @return list of categories
     */
    public ArrayList<RadioCategory> getRadioStationCategories(long id){
        ArrayList<RadioCategory> categories = new ArrayList<>();

        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();
        String relationJoinQuery = " SELECT a.* FROM "+RadioStationCategorySchema.TABLE_NAME+" a INNER JOIN "+RadioStationCategoryRelationSchema.TABLE_NAME+" b ON a."+RadioStationCategorySchema.ID+" = b."+RadioStationCategoryRelationSchema.CATEGORY_ID+" WHERE b."+RadioStationCategoryRelationSchema.RADIO_ID+" = '"+id+"';";

        Cursor cursor = db.rawQuery(relationJoinQuery, null);
        try{
            //inner join to find all games
            cursor.moveToFirst();

            if(cursor.getCount() > 0){
                do {
                    Log.d("Paramount SQL:",""+cursor.getCount());

                    String ID = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationCategorySchema.ID));
                    String TITLE = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationCategorySchema.TITLE));
                    String SLUG = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationCategorySchema.SLUG));
                    String DESCRIPTION = cursor.getString(cursor.getColumnIndexOrThrow(RadioStationCategorySchema.DESCRIPTION));

                    categories.add(new RadioCategory(
                            Long.parseLong(ID),
                            TITLE,
                            DESCRIPTION,
                            SLUG
                    ));
                } while(cursor.moveToNext());
            }
        } catch(Exception e){
            Log.d("error",e.toString());
        } finally {
            if(cursor != null) cursor.close();
            db.close();
        }

        return categories;
    }

    /**
     * Based on table structure where table id is "_id"
     * @param tableName name of the table
     * @param id to see if already in table
     * @return if ID is in table return true;
     */
    public boolean ifRecordExists(String tableName, String id){

        Cursor cursor = null;
        SQLiteDatabase db = new DataBaseManagerHelper(Global.getInstance()).getReadableDatabase();

        boolean returnFlag = false;
        try{
            cursor=  db.rawQuery(" select * from "+tableName+" a where a._id = " +id, new String[] {} );

            if(cursor.getCount() >0)
                returnFlag = true;
        }
        catch(Exception e){
            //empty
        }
        finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return returnFlag;
    }


    /**
     * Located below are all static references of database objects found in database
     */
    private class DatabaseSchema implements BaseColumns {
        private static final int DATABASE_VERSION = 2;
        private static final String DATABASE_NAME = "RadioAlarm";
    }

    /**
     * Inner class to Help store and retrieve Products items from the database
     */
    private class DataBaseManagerHelper extends SQLiteOpenHelper {

        DataBaseManagerHelper(Context context) {
            super(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //db.execSQL(AlarmSchema.SQL_DELETE_ENTRIES);
            db.execSQL(AlarmSchema.TABLE_CREATE);
            db.execSQL(RadioStationSchema.TABLE_CREATE);
            db.execSQL(RadioStationStreamSchema.TABLE_CREATE);
            db.execSQL(RadioStationCategorySchema.TABLE_CREATE);
            db.execSQL(RadioStationCategoryRelationSchema.TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL(AlarmSchema.SQL_DELETE_ENTRIES);
            db.execSQL(RadioStationSchema.SQL_DELETE_ENTRIES);
            db.execSQL(RadioStationStreamSchema.SQL_DELETE_ENTRIES);
            db.execSQL(RadioStationCategorySchema.SQL_DELETE_ENTRIES);
            db.execSQL(RadioStationCategoryRelationSchema.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    private class AlarmSchema implements BaseColumns{
        private static final String TABLE_NAME ="alarm";
        private static final String ID ="_id";
        private static final String TIME_HOUR ="time_hour";
        private static final String TIME_MINUTE ="time_minute";
        private static final String REPEATING_DAYS ="repeating_days";
        private static final String REPEAT ="repeat";
        private static final String NAME ="name";
        private static final String IS_ENABLED ="is_enabled";
        private static final String IS_VIBRATING ="is_vibrating";
        private static final String TYPE = "type";
        private static final String DATA = "data";
        private static final String DURATION = "duration";
        private static final String EASING = "easing";

        private static final String TABLE_CREATE = "CREATE TABLE " +
                AlarmSchema.TABLE_NAME           + " ( " +
                AlarmSchema.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AlarmSchema.TIME_HOUR + " INTEGER, " +
                AlarmSchema.TIME_MINUTE + " INTEGER, " +
                AlarmSchema.REPEATING_DAYS + " TEXT, " +
                AlarmSchema.REPEAT + " INTEGER, " +
                AlarmSchema.NAME + " TEXT, " +
                AlarmSchema.IS_ENABLED + " INTEGER, " +
                AlarmSchema.IS_VIBRATING + " INTEGER, " +
                AlarmSchema.TYPE + " TEXT, " +
                AlarmSchema.DATA + " TEXT, " +
                AlarmSchema.DURATION + " INTEGER, " +
                AlarmSchema.EASING + " INTEGER " +
                " );" ;

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + AlarmSchema.TABLE_NAME;
    }

    /**
     * based off the drible API,
     */
    private class RadioStationSchema implements BaseColumns{
        private static final String TABLE_NAME = "radio_station";
        private static final String ID = "_id";
        private static final String NAME = "name";
        private static final String DESCRIPTION = "description";
        private static final String SLUG = "slug";
        private static final String COUNTRY = "country";
        private static final String WEBSITE = "website";
        private static final String UPDATED = "updated";

        private static final String TABLE_CREATE = "CREATE TABLE " +
                RadioStationSchema.TABLE_NAME           + " ( " +
                RadioStationSchema.ID + " INTEGER PRIMARY KEY, " +
                RadioStationSchema.NAME + " TEXT, " +
                RadioStationSchema.DESCRIPTION + " TEXT, " +
                RadioStationSchema.SLUG + " TEXT, " +
                RadioStationSchema.COUNTRY + " TEXT, " +
                RadioStationSchema.WEBSITE + " TEXT, " +
                RadioStationSchema.UPDATED + " TEXT " +
                " );";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + RadioStationSchema.TABLE_NAME;
    }

    private class RadioStationStreamSchema implements BaseColumns{
        private static final String TABLE_NAME = "radio_station_stream";
        private static final String ID = "_id";
        private static final String RADIO_ID = "radio_id";
        private static final String URL = "url";

        private static final String TABLE_CREATE = "CREATE TABLE " +
                RadioStationStreamSchema.TABLE_NAME + " ( " +
                RadioStationStreamSchema.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RadioStationStreamSchema.RADIO_ID + " INTEGER, " +
                RadioStationStreamSchema.URL + " TEXT " +
                " );";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + RadioStationStreamSchema.TABLE_NAME;
    }

    private class RadioStationCategorySchema implements BaseColumns{
        private static final String TABLE_NAME = "radio_station_category";
        private static final String ID = "_id";
        private static final String TITLE = "title";
        private static final String DESCRIPTION = "desciption";
        private static final String SLUG = "slug";

        private static final String TABLE_CREATE = "CREATE TABLE " +
                RadioStationCategorySchema.TABLE_NAME           + " ( " +
                RadioStationCategorySchema.ID + " INTEGER PRIMARY KEY, " +
                RadioStationCategorySchema.TITLE + " TEXT, " +
                RadioStationCategorySchema.DESCRIPTION + " TEXT, " +
                RadioStationCategorySchema.SLUG + " SLUG " +
                " );";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + RadioStationCategorySchema.TABLE_NAME;
    }

    private class RadioStationCategoryRelationSchema implements BaseColumns{
        private static final String TABLE_NAME = "radio_station_category_relation";
        private static final String RADIO_ID = "radio_id";
        private static final String CATEGORY_ID = "category_id";

        private static final String TABLE_CREATE = "CREATE TABLE " +
                RadioStationCategoryRelationSchema.TABLE_NAME           + " ( " +
                RadioStationCategoryRelationSchema.RADIO_ID + " INTEGER, " +
                RadioStationCategoryRelationSchema.CATEGORY_ID + " INTEGER " +
                " );";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + RadioStationCategoryRelationSchema.TABLE_NAME;
    }
}