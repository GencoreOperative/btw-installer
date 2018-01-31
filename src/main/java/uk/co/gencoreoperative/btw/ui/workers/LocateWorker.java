package uk.co.gencoreoperative.btw.ui.workers;

import static java.text.MessageFormat.format;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.actions.Locate;
import uk.co.gencoreoperative.btw.actions.ProgressInputStream;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.signals.ClientJar;
import uk.co.gencoreoperative.btw.utils.FileUtils;

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
    private Context context;
    private final PathResolver resolver;
    private final DialogFactory factory;

    public LocateWorker(Context context, PathResolver resolver, DialogFactory factory) {
        this.context = context;
        this.resolver = resolver;
        this.factory = factory;
    }
    @Override
    protected File doInBackground() throws Exception {
        Locate locate = new Locate();
        ProgressInputStream inputStream = locate.locateMinecraftOneFiveTwo(resolver, this::setProgress);
        File tempFile = File.createTempFile("client", "jar");
        FileUtils.copyStream(
                inputStream,
                new FileOutputStream(tempFile),
                true,
                true);
        tempFile.deleteOnExit();
        return tempFile;
    }

    @Override
    protected void done() {
        super.done();
        try {
            context.add(new ClientJar(get()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            factory.failed(format(
                    "<b>Failed to locate the 1.5.2 client jar</b><br>{0}",
                    e.getMessage()));
        }
    }
}
