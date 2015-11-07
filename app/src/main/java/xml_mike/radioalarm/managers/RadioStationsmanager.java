package xml_mike.radioalarm.managers;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import xml_mike.radioalarm.Global;
import xml_mike.radioalarm.R;
import xml_mike.radioalarm.models.RadioFactory;
import xml_mike.radioalarm.models.RadioStation;

/**
 * Created by MClifford on 13/05/15.
 *
 * all streams located in database interface, gets all database objects and adds them to a list.
 */
public class RadioStationsManager {
    private static final String baseURl = "http://api.dirble.com/v2/countries/"; //params[0]
    private static final String type = "stations"; //params[2] //
    private static final String per_page = "30";  //params[3] //30 is max page size //paginate to stop memory issues.
    private static final String token = "b3b1e7e015ac9cb7104006f1e0"; //params[4]
    private static String country =  "";// = "GB/"; //params[1] //TODO load Country code based on region
    private static RadioStationsManager ourInstance;
    private ArrayList<RadioStation> radioStations; //potentially convert to hash map for quicker retrieval

    private RadioStationsManager() {
        radioStations = DatabaseManager.getInstance().getAllRadioStations();
    }

    public static RadioStationsManager getInstance() {
        if(ourInstance == null)
            ourInstance = new RadioStationsManager();



        return ourInstance;
    }

    public static RadioStation retrieveRadioStation(long ID){
        return DatabaseManager.getInstance().getRadioStation(ID);
    }

    public ArrayList<RadioStation> getRadioStations() {
        return radioStations;
    }

    public void setRadioStations(ArrayList<RadioStation> radioStations) {
        this.radioStations = radioStations;
    }

    public void downloadStations(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Global.getInstance());
        country = sharedPreferences.getString(Global.getInstance().getString(R.string.pref_current_country_key), "US");

        new StationDownloader().execute(baseURl, country, type, per_page, token);
    }

    public void addRadioStation(RadioStation radioStation){
        radioStations.add(radioStation);
        DatabaseManager.getInstance().addRadioStation(radioStation);
    }

    public RadioStation getRadioStation(int position){
        return radioStations.get(position);
    }

    public RadioStation searchRadioStation(Long ID){

        for(RadioStation radioStation: radioStations) {
            if(radioStation.getId() == ID){
                return radioStation;
            }
        }

        return new RadioStation();
    }

    public void setRadioStation(int i,RadioStation radioStation){
        radioStations.set(i, radioStation);
    }

    public void reDownloadRadioStations(){
        DatabaseManager.getInstance().deleteAllRadioStationEntries();
        this.downloadStations();

        Log.d("country",""+country);
    }

    /**
     * Download all radio stations in region.
     *
     * it is possible to download all radio stations at once but due to memory limitations on most android devices this is loaded incrementally and with pagination
     */
    private class StationDownloader extends AsyncTask<String , String, String> {

        @Override
        protected String doInBackground(String... params) {

            //initialise all variables needed for download
            InputStream inputStream = null;
            HttpURLConnection connection = null;
            int page=1;
            boolean responseEmpty = false;
            try {
               do{
                    inputStream = null;
                    connection = null;
                    URL url = new URL(baseURl+country+"/"+type+"/?per_page="+per_page+"&page="+page+"&token="+token); //Complied URL from static Strings
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    Log.e("Begin", "(" + page+"){" ); //log the beginning & end of each json page

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        break;
                    }

                    // download the file
                    inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader( inputStream, "utf-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    for(String line = reader.readLine(); line != null; line = reader.readLine()){
                        sb.append(line);
                    }

                    JSONArray returnArray = new JSONArray( sb.toString());

                    if(returnArray.length() > 0) {
                        for (int i = 0; i < returnArray.length(); i++) {
                            RadioStation radio = RadioFactory.generateRadionStation(returnArray.getJSONObject(i));
                            if(radio.getStreams().size() >0)
                                addRadioStation(radio) ;
                        }
                    }

                   if(returnArray.length() < Integer.parseInt(per_page))
                        responseEmpty = true;

                    Log.e("End","}(Page:"+page+") (total:"+radioStations.size()+")");
                    page++;
                } while(!responseEmpty);

            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException ignored) {
                    Log.e("DOM", ignored.toString());
                }

                if (connection != null)
                    connection.disconnect();
            }
            return "Complete";
        }
    }
}