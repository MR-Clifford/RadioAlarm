package xml_mike.radioalarm.models;

/**
 * Created by MClifford on 26/03/15.
 */
public interface AObservable {

    public void register(AObserver aObserver);
    public void unregister(AObserver aObserver);

    public void notifyObservers();

    public Object getUpdate(AObserver aObserver);

}
