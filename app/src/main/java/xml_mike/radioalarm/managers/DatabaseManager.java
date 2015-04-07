package xml_mike.radioalarm.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.models.Alarm;
import xml_mike.radioalarm.models.AlarmFactory;

/**
 * Created by MClifford on 23/03/15.
 */
public class DatabaseManager {
    private static DatabaseManager ourInstance = new DatabaseManager();

    public static DatabaseManager getInstance() {

        if(ourInstance == null)
            ourInstance = new DatabaseManager();

        return ourInstance;
    }

    private DatabaseManager() {
    }

    public void addDatabaseItem(Alarm alarm){
        SQLiteDatabase db = new AlarmHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlarmSchema.ALARM_TIME_HOUR,alarm.getTimeHour());
        values.put(AlarmSchema.ALARM_TIME_MINTUE,alarm.getTimeMinute());
        values.put(AlarmSchema.ALARM_REPEATING_DAYS,alarm.getDBRepeatingDays());
        values.put(AlarmSchema.ALARM_REPEAT,alarm.isRepeating() ? 1:0);
        values.put(AlarmSchema.ALARM_IS_VIBRATING,alarm.isVibrate() ? 1:0);
        values.put(AlarmSchema.ALARM_NAME,alarm.getName());
        values.put(AlarmSchema.ALARM_IS_ENABLED,alarm.getName());
        values.put(AlarmSchema.ALARM_TYPE,alarm.getName());
        values.put(AlarmSchema.ALARM_DATA,alarm.getName());

        long id = db.insert(AlarmSchema.TABLE_NAME, "NULL", values);

        alarm.setId(id);

        db.close();

        //Global.getInstance().updateAlarm(alarm);
    }

    public void updateDataBaseItem(Alarm alarm){
        SQLiteDatabase db = new AlarmHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(AlarmSchema.ALARM_TIME_HOUR,alarm.getTimeHour());
        values.put(AlarmSchema.ALARM_TIME_MINTUE,alarm.getTimeMinute());
        values.put(AlarmSchema.ALARM_REPEATING_DAYS,alarm.getDBRepeatingDays());
        values.put(AlarmSchema.ALARM_REPEAT,alarm.isRepeating() ? 1:0);
        values.put(AlarmSchema.ALARM_IS_VIBRATING,alarm.isVibrate() ? 1:0);
        values.put(AlarmSchema.ALARM_NAME,alarm.getName());
        values.put(AlarmSchema.ALARM_IS_ENABLED,alarm.isEnabled() ? 1:0);
        values.put(AlarmSchema.ALARM_TYPE,alarm.getClass().toString());
        values.put(AlarmSchema.ALARM_DATA,alarm.getData());

        String selection = AlarmSchema.ALARM_ID + " LIKE ?";

        String[] selectionArgs = {String.valueOf(alarm.getId())};

        db.update(AlarmSchema.TABLE_NAME,values, selection, selectionArgs);
        db.close();
    }

    public void removeDatabaseItem(Alarm alarm){
        SQLiteDatabase db = new AlarmHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();
        // Define 'where' part of query.
        String selection = AlarmSchema.ALARM_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(alarm.getId())};
        // Issue SQL statement.
        db.delete(AlarmSchema.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public ArrayList<Alarm> getAlarmDatabaseItems(){

        ArrayList<Alarm> returnList = new ArrayList<>();

        SQLiteDatabase db = new AlarmHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();

        String[] projection = {
                AlarmSchema.ALARM_ID,
                AlarmSchema.ALARM_TIME_HOUR,
                AlarmSchema.ALARM_TIME_MINTUE,
                AlarmSchema.ALARM_REPEATING_DAYS,
                AlarmSchema.ALARM_REPEAT,
                AlarmSchema.ALARM_IS_VIBRATING,
                AlarmSchema.ALARM_NAME,
                AlarmSchema.ALARM_IS_ENABLED,
                AlarmSchema.ALARM_TYPE,
                AlarmSchema.ALARM_DATA //changes depending on alarm type
        };

        //initialise variables
        String sortOrder = AlarmSchema.ALARM_NAME + " Desc";
        String[] selectionArgs = {};
        String selection = "";

        Cursor cursor = db.query(AlarmSchema.TABLE_NAME, projection, null, null, null, null, sortOrder);

        cursor.moveToFirst();

        if(cursor.getCount() > 0){
            do{
                //Collect data needed to create objects
                String ALARM_ID = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_ID));
                int ALARM_TIME_HOUR = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_TIME_HOUR));
                int ALARM_TIME_MINTUE = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_TIME_MINTUE));
                String ALARM_REPEATING_DAYS = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_REPEATING_DAYS));
                int ALARM_REPEAT = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_REPEAT));
                String ALARM_NAME = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_NAME));
                int ALARM_IS_ENABLED = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_IS_ENABLED));
                int ALARM_IS_VIBRATING = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_IS_VIBRATING));
                String ALARM_TYPE = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_TYPE));
                String ALARM_DATA = cursor.getString(cursor.getColumnIndexOrThrow(AlarmSchema.ALARM_DATA));

                Alarm alarm = AlarmFactory.createAlarm(Long.parseLong(ALARM_ID, 10),ALARM_TYPE,ALARM_NAME,ALARM_DATA,ALARM_REPEATING_DAYS,ALARM_TIME_HOUR,ALARM_TIME_MINTUE,ALARM_IS_ENABLED,ALARM_REPEAT, ALARM_IS_VIBRATING);

                returnList.add(alarm);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return returnList;
    }

    private class DatabaseSchema implements BaseColumns {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "RadioAlarm";
    }

    /**
     * Inner class to Help store and retrieve Products items from the database
     */
    private class AlarmHelper extends SQLiteOpenHelper {

        AlarmHelper(Context context) {
            super(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //db.execSQL(AlarmSchema.SQL_DELETE_ENTRIES);
            db.execSQL(AlarmSchema.TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL(AlarmSchema.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    private class AlarmSchema implements BaseColumns{
        private static final String TABLE_NAME ="alarm";
        private static final String ALARM_ID ="id";
        private static final String ALARM_TIME_HOUR ="time_hour";
        private static final String ALARM_TIME_MINTUE ="time_minute";
        private static final String ALARM_REPEATING_DAYS ="repeating_days";
        private static final String ALARM_REPEAT ="repeat";
        private static final String ALARM_NAME ="name";
        private static final String ALARM_IS_ENABLED ="is_enabled";
        private static final String ALARM_IS_VIBRATING ="is_vibrating";
        private static final String ALARM_TYPE = "type";
        private static final String ALARM_DATA = "data";

        private static final String TABLE_CREATE = "CREATE TABLE " +
                AlarmSchema.TABLE_NAME           + " ( " +
                AlarmSchema.ALARM_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AlarmSchema.ALARM_TIME_HOUR      + " INTEGER, " +
                AlarmSchema.ALARM_TIME_MINTUE    + " INTEGER, " +
                AlarmSchema.ALARM_REPEATING_DAYS + " TEXT, " +
                AlarmSchema.ALARM_REPEAT         + " INTEGER, " +
                AlarmSchema.ALARM_NAME           + " TEXT, " +
                AlarmSchema.ALARM_IS_ENABLED     + " INTEGER, " +
                AlarmSchema.ALARM_IS_VIBRATING   + " INTEGER, " +
                AlarmSchema.ALARM_TYPE           + " TEXT, " +
                AlarmSchema.ALARM_DATA           + " TEXT " +
                " );" ;

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS" + AlarmSchema.TABLE_NAME;
    }
}
