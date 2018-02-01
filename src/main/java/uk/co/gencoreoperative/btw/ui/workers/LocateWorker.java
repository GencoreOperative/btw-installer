package uk.co.gencoreoperative.btw.ui.workers;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.actions.Locate;
import uk.co.gencoreoperative.btw.actions.ProgressInputStream;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.utils.FileUtils;
import uk.co.gencoreoperative.btw.utils.Timer;

/**
 * Worker dedicated to locating an 1.5.2 Jar and making it available for subsequent
 * workers to process.
 * <p>
 * This worker will locate and stream the 1.5.2 jar to a temporary location, and in
 * doing so will be able to generate progress information about this operation.
 * <p>
 * If there was an unrecoverable error during this process we will notify the user.
 * Also, if the Jar could not be located then we will also notify the user.
 * <p>
 * <b>Progress</b>: This worker will update its progress property as the client jar
 * is copied from the location it was found to a temporary location.
 */
public class LocateWorker extends SwingWorker<File, Void> {
    private final PathResolver resolver;
    private final Locate locate = new Locate();

    public LocateWorker(PathResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected File doInBackground() throws Exception {
        ProgressInputStream inputStream = Timer.timeAndReturn(
                "Locating 1.5.2",
                () -> locate.locateMinecraftOneFiveTwo(resolver, this::setProgress));

        if (inputStream == null) {
            throw new Exception(Errors.MC_ONE_FIVE_TWO_NOT_FOUND.getReason());
        }

        final File tempFile = File.createTempFile("client", "jar");
        tempFile.deleteOnExit();
        return Timer.timeAndReturn("Copying 1.5.2 locally", () -> {
            FileUtils.copyStream(
                    inputStream,
                    new FileOutputStream(tempFile),
                    true,
                    true);
            return tempFile;
        });
    }

    public static void main(String... args) throws ExecutionException, InterruptedException {
        LocateWorker worker = new LocateWorker(new PathResolver());
        worker.execute();
        System.out.println(worker.get().getPath());
    }
}
