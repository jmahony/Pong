package common;

/**
 * Created by josh on 30/03/14.
 */
public interface NetObjectWriter {

    boolean put(Object o);

    void put(final Object data, final long delay);

}
