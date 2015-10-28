package xml_mike.radioalarm;

/**
 * Created by MClifford on 25/06/15.
 */
public enum GlobalStrings {
    STOP_ALARM_BROADCAST("com.xml_mike.radioalarm.action.STOP_ALARM_BROADCAST"),
    STRING_TWO("TWO")
    ;

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
