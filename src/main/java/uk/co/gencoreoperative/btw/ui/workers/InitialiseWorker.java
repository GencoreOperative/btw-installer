package uk.co.gencoreoperative.btw.ui.workers;

import static java.text.MessageFormat.format;

import javax.swing.*;
import java.io.File;
import java.text.MessageFormat;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.utils.Percentage;
import uk.co.gencoreoperative.btw.version.Version;
import uk.co.gencoreoperative.btw.version.VersionManager;

/**
 * The {@link InitialiseWorker} will prepare the installation folder, and if
 * necessary clean up any previous installation artefacts that are present in
 * the folder.
 * <p>
 * If there was a {@link Version} present in the previous installation, this
 * will be returned as part of this worker.
 * <p>
 * <b>Progress</b>: This worker will signal progress as part of identifying
 * previous files to delete and the number deleted.
 */
public class InitialiseWorker extends SwingWorker<PatchWorker.Status, Void> {

    private final PathResolver resolver;

    public InitialiseWorker(PathResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected PatchWorker.Status doInBackground() throws Exception {
        File folder = resolver.betterThanWolves();

        // Check there isn't a file in the way
        if (folder.isFile()) {
            String reason = format("{0}\n{1}",
                    Errors.FAILED_FILE_IN_THE_WAY.getReason(),
                    folder.getPath());
            return new PatchWorker.Status(reason);
        }

        // Create the folder if it doesn't exist
        if (!folder.exists() && !folder.mkdirs()) {
            String reason = MessageFormat.format("{0}\n{1}",
                    Errors.FAILED_TC_CREATE_FOLDER.getReason(),
                    folder.getPath());
            return new PatchWorker.Status(reason);
        }

        // Clean the version file
        VersionManager manager = VersionManager.getVersionManager(resolver);
        manager.cleanVersionInformation();
        setProgress(Percentage.getProgress(1, 3));

        // Clean JSON
        File json = resolver.betterThanWolvesJson();
        if (json.exists() && !json.delete()) {
            String reason = MessageFormat.format("{0}\n{1}",
                    Errors.FAILED_TO_DELETE_FILE.getReason(),
                    json.getPath());
            return new PatchWorker.Status(reason);
        }
        setProgress(Percentage.getProgress(2, 3));

        // Clean Jar
        File jar = resolver.betterThanWolvesJar();
        if (jar.exists() && !jar.delete()) {
            String reason = MessageFormat.format("{0}\n{1}",
                    Errors.FAILED_TO_DELETE_FILE.getReason(),
                    jar.getPath());
            return new PatchWorker.Status(reason);
        }
        setProgress(Percentage.getProgress(3, 3));

        return new PatchWorker.Status();
    }
}
