package uk.co.gencoreoperative.btw.ui.workers;

import javax.swing.*;

import uk.co.gencoreoperative.btw.version.Version;

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
public class InitialiseWorker extends SwingWorker<Version, Void> {
    @Override
    protected Version doInBackground() throws Exception {
        // TODO Create the folder if missing

        // TODO Read previous version

        // TODO Identify previous artefacts to remove
        return null;
    }
}
