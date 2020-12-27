package uk.co.gencoreoperative.btw.ui.workers;

import static java.text.MessageFormat.format;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.utils.Logger;
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
        if (resolver.get().isFile()) {
            String reason = format("{0}\n{1}",
                    Errors.FAILED_FILE_IN_THE_WAY.getReason(),
                    folder.getPath());
            return PatchWorker.Status.failed(reason, Errors.FAILED_FILE_IN_THE_WAY);
        }

        // Create the folder if it doesn't exist
        if (!folder.exists() && !folder.mkdirs()) {
            String reason = format("{0}\n{1}",
                    Errors.FAILED_TO_CREATE_FOLDER.getReason(),
                    folder.getPath());
            return PatchWorker.Status.failed(reason, Errors.FAILED_TO_CREATE_FOLDER);
        }

        // Clean the version file
        VersionManager manager = VersionManager.getVersionManager(resolver);
        manager.cleanVersionInformation();
        setProgress(Percentage.getProgress(1, 3));

        // Clean JSON
        File json = resolver.betterThanWolvesJson();
        Optional<PatchWorker.Status> jsonResult = delete(json);
        if (jsonResult.isPresent()) {
            Logger.info("Remove previous JSON {0}", json.getPath());
            if (!jsonResult.get().isSuccess()) return jsonResult.get();
        }
        setProgress(Percentage.getProgress(2, 3));

        // Clean Jar
        File jar = resolver.betterThanWolvesJar();
        Optional<PatchWorker.Status> jarResult = delete(jar);
        if (jarResult.isPresent()) {
            Logger.info("Remove previous Jar {0}", json.getPath());
            if (!jarResult.get().isSuccess()) return jarResult.get();
        }
        setProgress(Percentage.getProgress(3, 3));

        return PatchWorker.Status.success();
    }

    /**
     * Delete a file if it exists.
     * @param file Non null, possibly non-existing file to attempt deletion on.
     * @return {@link Optional#empty()} if the file did not exist. Otherwise a {@link PatchWorker.Status}
     * indicating if the operation was successful or not.
     */
    private Optional<PatchWorker.Status> delete(File file) {
        if (!file.exists()) return Optional.empty();
        try {
            Files.delete(file.toPath());
            return Optional.of(PatchWorker.Status.success());
        } catch (IOException e) {
            String reason = format("{0}\n  File: {1}\n  Cause: {2}\n  Type: {3}",
                    Errors.FAILED_TO_DELETE_FILE.getReason(),
                    file.getPath(),
                    e.getMessage(),
                    e.getClass().getSimpleName());
            return Optional.of(PatchWorker.Status.failed(reason, Errors.FAILED_TO_DELETE_FILE));
        }
    }
}
