package uk.co.gencoreoperative.btw.ui;

import java.util.Observable;

/**
 * Indicates a task to be processed and associates the success/failure status
 * of the item.
 */
public class Item extends Observable {
    private String description;
    private Boolean success;
    private Exception reason;

    public Item(String description) {
        this.description = description;
        success = null;
        reason = null;
    }

    /**
     * @return True if the item has been processed, otherwise false.
     */
    public boolean isProcessed() {
        return success != null;
    }

    /**
     * @return True if the item was processed successfully.
     */
    public boolean isSuccessful() {
        if (success == null) throw new IllegalStateException("Item not processed yet");
        return success;
    }

    /**
     * @return A textual description of the Item.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    /**
     * Mark the item as failed, and if there was a reason capture this.
     *
     * @param reason The optional reason for the failure.
     * @param error The optional stacktrace of the error if known.
     */
    public void failed(String reason, Exception error) {
        this.reason = error;
        failed();
    }

    /**
     * Mark the item as successfully processed.
     */
    public void success() {
        success = true;
        setChanged();
        notifyObservers(this);
    }

    public void failed() {
        success = false;
        setChanged();
        notifyObservers(this);
    }
}
