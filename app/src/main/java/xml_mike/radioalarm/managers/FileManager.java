package xml_mike.radioalarm.managers;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.models.AlarmMedia;

/**
 * Created by MClifford on 23/03/15.
 */
public class FileManager {
    private static FileManager ourInstance;
    private ArrayList<AlarmMedia> songsList;


    public static FileManager getInstance() {

        if(ourInstance == null)
            ourInstance = new FileManager();

        return ourInstance;
    }

    private FileManager() {
        songsList = getLocalMedia();
    }

    public List<AlarmMedia> getMediaList(){
        return songsList;
    }

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

        Log.e("getMediaList",""+cursor.getCount());

        ArrayList<AlarmMedia> songs = new ArrayList<>();
        while(cursor.moveToNext())
            songs.add(new AlarmMedia(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5) ));

        cursor.close();

        return songs;
    }

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

            Log.e("getMediaList", "" + cursor.getCount());

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
            Log.e("","");
        }


        return alarmMedia;
    }

}
