package uk.co.gencoreoperative.btw.ui.workers;

import static java.text.MessageFormat.*;
import static uk.co.gencoreoperative.btw.ui.panels.ProgressPanel.State.*;

import javax.swing.*;
import java.io.File;
import java.util.List;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.VersionResolver;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.ui.panels.ProgressPanel;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

public class PatchWorker extends SwingWorker<PatchWorker.Status, ProgressPanel.State> {
    private final PatchFile patchFile;
    private final ActionFactory factory;
    private final PathResolver pathResolver;
    private final Context context;
    private final ProgressPanel panel;

    public PatchWorker(MinecraftHome minecraftHome, PatchFile patchFile, ActionFactory factory, Context context, ProgressPanel panel) {
        this.patchFile = patchFile;
        this.factory = factory;
        pathResolver = new PathResolver(minecraftHome.getFolder());
        this.context = context;
        this.panel = panel;
    }

    @Override
    protected Status doInBackground() throws Exception {
        // Remove previous installation.
        publish(REMOVE_PREVIOUS);
        factory.removePreviousInstallation(pathResolver);
        if (pathResolver.betterThanWolves().exists()) {
            return new Status(format("{0}\n{1}",
                    Errors.FAILED_TO_DELETE_INSTALLATION.getReason(),
                    pathResolver.betterThanWolves().getAbsolutePath()));
        }

        publish(CREATE_FOLDER);
        File installationFolder = factory.createInstallationFolder(pathResolver);
        if (!installationFolder.exists()) {
            return new Status(format("{0}\n{1}",
                    Errors.FAILED_TC_CREATE_FOLDER.getReason(),
                    installationFolder.getAbsolutePath()));
        }

        // Write JSON
        publish(COPY_JSON);
        File json = factory.copyJsonToInstallation(installationFolder);
        if (!json.exists()) {
            return new Status(format("{0}\n{1}",
                    Errors.FAILED_TO_WRITE_JSON.getReason(),
                    json.getAbsolutePath()));
        }

        // Create the Better Than Wolves Jar
        publish(CREATE_JAR);
        final ActionFactory.MonitoredSet monitoredSet = factory.mergeClientWithPatch(pathResolver, patchFile.getFile());
        monitoredSet.addObserver((o, arg) -> {
            setProgress(monitoredSet.getProgress());
            publish(CREATE_JAR);
        });
        File jar = factory.writeToTarget(pathResolver, monitoredSet);

        // Signal to the application that BTW has been installed
        InstalledVersion installedVersion = new InstalledVersion(jar);
        installedVersion.setVersion(patchFile.getVersion());
        context.add(installedVersion);

        // Write the version to the installation folder
        publish(WRITE_VERSION);
        VersionResolver versionResolver = new VersionResolver();
        versionResolver.writeVersion(pathResolver.betterThanWolves(), installedVersion.getVersion());

        publish(COMPLETE);
        return new Status();
    }

    @Override
    protected void process(List<ProgressPanel.State> chunks) {
        chunks.forEach(s -> {
            panel.setState(s);
            panel.setProgress(this.getProgress());
        });
    }

    public static class Status {
        private final boolean success;
        private final String error;

        public Status(String error) {
            success = false;
            this.error = error;
        }

        public Status() {
            success = true;
            error = null;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }
    }
}
