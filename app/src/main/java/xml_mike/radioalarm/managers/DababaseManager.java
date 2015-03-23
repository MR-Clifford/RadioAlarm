package xml_mike.radioalarm.managers;

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
public class DababaseManager {
    private static DababaseManager ourInstance = new DababaseManager();

    public static DababaseManager getInstance() {
        return ourInstance;
    }

    private DababaseManager() {
    }

    private void removeDataBaseItem(){

    }

    private void addDatabaseItem(){

    }

    private void updateDataBaseItem(){

    }

    private ArrayList<Alarm> getAlarmDatabaseItems(){

        ArrayList<Alarm> returnList = new ArrayList<>();

        SQLiteDatabase db = new AlarmHelper(Global.getInstance().getApplicationContext()).getWritableDatabase();

        String[] projection = {
                AlarmScema.ALARM_ID,
                AlarmScema.ALARM_TIME_HOUR,
                AlarmScema.ALARM_TIME_MINTUE,
                AlarmScema.ALARM_REPEATING_DAYS,
                AlarmScema.ALARM_REPEAT,
                AlarmScema.ALARM_NAME,
                AlarmScema.ALARM_IS_ENABLED,
                AlarmScema.ALARM_TYPE,
                AlarmScema.ALARM_DATA //changes depending on alarm type
        };

        //initialise variables
        String sortOrder = AlarmScema.ALARM_NAME + " Desc";
        String[] selectionArgs = {};
        String selection = "";

        Cursor cursor = db.query(AlarmScema.TABLE_NAME, projection, null, null, null, null, sortOrder);

        cursor.moveToFirst();

        if(cursor.getCount() > 0){
            do{
                //Collect data needed to create objects

                String ALARM_ID = cursor.getString(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_ID));
                int ALARM_TIME_HOUR = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_TIME_HOUR));
                int ALARM_TIME_MINTUE = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_TIME_MINTUE));
                String ALARM_REPEATING_DAYS = cursor.getString(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_REPEATING_DAYS));
                String ALARM_REPEAT = cursor.getString(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_REPEAT));
                String ALARM_NAME = cursor.getString(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_NAME));
                int ALARM_IS_ENABLED = cursor.getInt(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_IS_ENABLED));
                String ALARM_TYPE = cursor.getString(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_TYPE));
                String ALARM_DATA = cursor.getString(cursor.getColumnIndexOrThrow(AlarmScema.ALARM_DATA));

                Alarm alarm = AlarmFactory.createAlarm(Long.parseLong(ALARM_ID, 10),ALARM_TYPE,ALARM_NAME,ALARM_DATA,ALARM_REPEATING_DAYS,ALARM_TIME_HOUR,ALARM_TIME_MINTUE,ALARM_IS_ENABLED);

                returnList.add(null);

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
            db.execSQL(AlarmScema.TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL(AlarmScema.SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    private class AlarmScema implements BaseColumns{
        private static final String TABLE_NAME ="alarm";
        private static final String ALARM_ID ="id";
        private static final String ALARM_TIME_HOUR ="time_hour";
        private static final String ALARM_TIME_MINTUE ="time_minute";
        private static final String ALARM_REPEATING_DAYS ="repeating_days";
        private static final String ALARM_REPEAT ="repeat";
        private static final String ALARM_NAME ="name";
        private static final String ALARM_IS_ENABLED ="is_enabled";
        private static final String ALARM_TYPE = "type";
        private static final String ALARM_DATA = "data";

        private static final String TABLE_CREATE = "CREATE TABLE " +
                AlarmScema.TABLE_NAME           + " ( " +
                AlarmScema.ALARM_ID             + " TEXT, " +
                AlarmScema.ALARM_TIME_HOUR      + " INT, " +
                AlarmScema.ALARM_TIME_MINTUE    + " INT, " +
                AlarmScema.ALARM_REPEATING_DAYS + " TEXT, " +
                AlarmScema.ALARM_REPEAT         + " INT, " +
                AlarmScema.ALARM_NAME           + " TEXT, " +
                AlarmScema.ALARM_IS_ENABLED     + " INT " +
                AlarmScema.ALARM_TYPE           + " TEXT " +
                AlarmScema.ALARM_DATA           + " TEXT " +
                " );" ;

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS" + AlarmScema.TABLE_NAME;
    }
}
