package xml_mike.radioalarm;

/**
 * Created by MClifford on 25/06/15.
 */
public enum GlobalStrings {
    STOP_ALARM_BROADCAST("com.xml_mike.radioalarm.action.STOP_ALARM_BROADCAST"), //Used to set runtime broadcast receiver
    STOP_ALARM          ("com.xml_mike.radioalarm.action.STOP_ALARM"),
    START_ALARM         ("xml_mike.radioalarm.intent.START_ALARM"),
    STOP_SNOOZE_ALARM   ("com.xml_mike.radioalarm.action.STOP_SNOOZE_ALARM"),
    START_SNOOZE_ALARM  ("com.xml_mike.radioalarm.action.START_SNOOZE_ALARM"),
    SET_SNOOZE_ALARM    ("xml_mike.radioalarm.intent.SNOOZE"),
    SCHEDULED_ALARM_CHECK("xml_mike.radioalarm.intent.SCHEDULED_ALARM_CHECK"),
    BOOT_COMPLETED      ("android.intent.action.BOOT_COMPLETED"), // Using android intent
    STRING_TWO("TWO");

    private final String text;

    /**
     * @param text
     */
    GlobalStrings(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
