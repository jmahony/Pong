package common;

/**
 * Provide a common interface for send messages
 */
public interface NetObjectWriter {

    boolean put(Object o);

    void put(final Object data, final long delay);

}
