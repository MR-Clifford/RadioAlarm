package xml_mike.radioalarm.managers;

/**
 * Created by MClifford on 17/08/15.
 *
 * Found memory leak in standard Alarm when creating new alarms.
 * To rectify said memory leak will add this class as a singleton to store & retrieve all default alarms stored on phone.
 */
public class DefaultAlarmSoundsManager {
    static DefaultAlarmSoundsManager INSTANCE;



    private DefaultAlarmSoundsManager(){}

    static public DefaultAlarmSoundsManager getInstance(){

        if(INSTANCE == null)
            INSTANCE = new DefaultAlarmSoundsManager();

        return INSTANCE;
    }
}