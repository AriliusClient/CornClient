package me.constantindev.ccl.events;	

public interface ICancellable {
    /**
     * Sets if this event is cancelled.
     * @param cancelled Is cancelled
     */
    void setCancelled(boolean cancelled);

    /**
     * Cancels this event.
     */
    default void cancel() { setCancelled(true); }

    /**
     * @return True if this event is cancelled.
     */
    boolean isCancelled();
}