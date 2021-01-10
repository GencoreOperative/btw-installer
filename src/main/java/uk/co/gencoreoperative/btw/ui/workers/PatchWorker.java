package uk.co.gencoreoperative.btw.ui.workers;

import static java.text.MessageFormat.*;
import static uk.co.gencoreoperative.btw.ui.panels.ProgressPanel.State.*;
import static uk.co.gencoreoperative.btw.utils.Timer.*;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.panels.LogPanel;
import uk.co.gencoreoperative.btw.ui.signals.AddonFiles;
import uk.co.gencoreoperative.btw.utils.Logger;
import uk.co.gencoreoperative.btw.version.Version;
import uk.co.gencoreoperative.btw.version.VersionManager;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.DialogFactory;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.ui.panels.ProgressPanel;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

/**
 * {@link PatchWorker} will handle the actual work of performing the patching process.
 * <p>
 * This {@link SwingWorker} signals progress in two ways. The first is the intermediate
 * progress updates which are used for the {@link ProgressPanel}. This panel is expecting
 * {@link uk.co.gencoreoperative.btw.ui.panels.ProgressPanel.State} signals to update the
 * user on the stages of the installation.
 * <p>
 * The second is the {@link Status} used to signal the end state of the patching process.
 * This will either be successful or a failure case with the error and code indicated.
 */
public class PatchWorker extends SwingWorker<PatchWorker.Status, ProgressPanel.State> {
    private final PatchFile patchFile;
    private final ActionFactory factory;
    private final PathResolver pathResolver;
    private final Context context;
    private final ProgressPanel panel;
    private final DialogFactory dialogFactory;
    private VersionManager manager;

    public PatchWorker(MinecraftHome minecraftHome, PatchFile patchFile, ActionFactory factory, Context context, ProgressPanel panel, DialogFactory dialogFactory) {
        this.patchFile = patchFile;
        this.factory = factory;
        pathResolver = new PathResolver(minecraftHome.getFolder());
        this.context = context;
        this.panel = panel;
        this.dialogFactory = dialogFactory;
        manager = VersionManager.getVersionManager(pathResolver);
    }

    @Override
    protected Status doInBackground() throws Exception {
        // Read previous version - before folder is deleted.
        Optional<Version> previousVersion = manager.getVersion();
        previousVersion.ifPresent(v -> Logger.info("Previous Version: " + v.getPatchVersion()));

        // Run the Initialise Worker to setup the installation folder.
        publish(ProgressPanel.State.CLEAN_PREVIOUS_INSTALLATION);
        InitialiseWorker initialiseWorker = new InitialiseWorker(pathResolver);
        initialiseWorker.addPropertyChangeListener(evt -> panel.setProgress(initialiseWorker.getProgress()));
        initialiseWorker.execute();
        Status status = initialiseWorker.get();
        if (!status.isSuccess()) {
            Logger.error(status.getError());
            return status;
        }

        // Locate Client Jar
        publish(ProgressPanel.State.COPY_1_5_2);
        final LocateWorker worker = new LocateWorker(pathResolver);
        // Wire up workers progress to the ProgressPanel
        worker.addPropertyChangeListener(evt -> panel.setProgress(worker.getProgress()));
        worker.execute();
        final File clientJar;
        try {
            clientJar = worker.get();
        } catch (InterruptedException | ExecutionException e) {
            dialogFactory.failed(format(
                    "<html><b>Failed to locate the 1.5.2 client jar</b><br>{0}</html>",
                    e.getCause().getMessage()));
            return new Status();
        }

        // Write JSON
        publish(COPY_JSON);
        File json = factory.copyJsonToInstallation(pathResolver);
        Logger.info("Copy JSON {0}", json.getPath());
        if (!json.exists()) {
            return new Status(format("{0}\n{1}",
                    Errors.FAILED_TO_WRITE_JSON.getReason(),
                    json.getAbsolutePath()), Errors.FAILED_TO_WRITE_JSON);
        }

        // Create the Better Than Wolves Jar
        publish(CREATE_JAR);
        final ActionFactory.MonitoredSet monitoredSet = factory.mergeClientWithPatch(clientJar, patchFile.getFile());
        monitoredSet.addObserver((o, arg) -> setProgress(monitoredSet.getProgress()));
        File jar = timeAndReturn("Creating Jar", () -> factory.writeToTarget(pathResolver, monitoredSet));
        Logger.info("Created patched Jar {0}", jar.getPath());

        // Add addons to Better Than Wolves Jar
        publish(ADD_ADDONS);
        for (int i = 0; i < AddonFiles.getAddons().size(); i++) {
            ActionFactory.MonitoredSet monitoredAddonSet = factory.mergePatchWithAddon(jar, AddonFiles.getAddon(i), AddonFiles.getZipPath(i));
            System.out.println(AddonFiles.getAddon(i).toString() + AddonFiles.getZipPath(i));
            monitoredAddonSet.addObserver((o, arg) -> setProgress(monitoredAddonSet.getProgress()));
            File addonJar = timeAndReturn("Adding addon", () -> factory.writeToTarget(pathResolver, monitoredAddonSet));
            Logger.info("Added addon to Jar {0}", addonJar.getPath());
        }

        // Write the version to the installation folder
        publish(WRITE_VERSION);
        Version version = VersionManager.createVersion(patchFile);
        manager.save(version);
        Logger.info("Write version information for {0}", version.getPatchVersion());

        // Signal to the application that BTW has been installed
        publish(COMPLETE);
        InstalledVersion installedVersion = new InstalledVersion(jar, version);
        context.add(installedVersion);

        return new Status();
    }

    @Override
    protected void process(List<ProgressPanel.State> chunks) {
        chunks.forEach(panel::setState);
    }

    @Override
    protected void done() {
        super.done();
        try {
            Status status = get();
            LogPanel.hide(panel);
            if (status.isSuccess()) {
                dialogFactory.success(Strings.TITLE_VERSION.getText(), Strings.MSG_PATCH_SUCCESS.getText());
            } else {
                dialogFactory.failed(status.getError());
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.error("Error whilst collecting result of PatchWorker", e);
        }

    }

    /**
     * Output status of a UI Worker thread.
     * Captures human and machine readable status.
     */
    public static class Status {
        private final boolean success;
        private final String error;
        private final Errors code;

        private Status(String errorMessage, Errors code) {
            this.code = code;
            success = false;
            this.error = errorMessage;
        }

        private Status() {
            success = true;
            error = null;
            code = null;
        }

        /**
         * @return True if the worker thread completed successfully.
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * @return If {@link #isSuccess()} is false, this will contain the reason.
         */
        public String getError() {
            return error;
        }

        /**
         * @return If {@link #isSuccess()} is false, this will contain the reason code.
         */
        public Errors getCode() {
            return code;
        }

        /**
         * @return A successful status.
         */
        public static Status success() {
            return new Status();
        }

        /**
         * @param reason Human readable error reason.
         * @param code Error code enumeration.
         * @return A failure {@link Status} along with reason.
         */
        public static Status failed(String reason, Errors code) {
            return new Status(reason, code);
        }
    }
}
