package uk.co.gencoreoperative.btw.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A dedicated InputStream which allows the caller to observe progress as the
 * stream is read.
 *
 * The caller will need to provide an implementation of {@link ProgressListener}
 * in order to receive notification of % progress.
 */
public class ProgressInputStream extends InputStream {
    private final InputStream stream;
    private final long total;
    private final AtomicLong readCounter = new AtomicLong(0);
    private final ProgressListener listener;

    private int previousProgress;

    public ProgressInputStream(InputStream stream, long total, ProgressListener listener) {
        this.stream = stream;
        this.total = total;
        this.listener = listener;
        this.previousProgress = 0;
    }

    @Override
    public int read() throws IOException {
        readCounter.addAndGet(1);
        int progress = getProgressPercentage();
        if (progress != previousProgress) {
            previousProgress = progress;
            if (listener != null) {
                listener.setProgress(progress);
            }
        }
        return stream.read();
    }

    public int getProgressPercentage() {
        return (int)((readCounter.get() * 100.0f) / total);
    }

    public interface ProgressListener {
        void setProgress(int progress);
    }
}
