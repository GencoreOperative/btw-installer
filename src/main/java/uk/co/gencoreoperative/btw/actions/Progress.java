package uk.co.gencoreoperative.btw.actions;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

public class Progress extends Observable {

    private int total;
    private AtomicInteger current = new AtomicInteger(0);

    public Progress(int total) {
        this.total = total;
    }

    public int getProgressPercentage() {
        return (int)((current.get() * 100.0f) / total);
    }

    public void addProgress(int delta) {
        current.addAndGet(delta);
        setChanged();
        notifyObservers(this);
    }
}
