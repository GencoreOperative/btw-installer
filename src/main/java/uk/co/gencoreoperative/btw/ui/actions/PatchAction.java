package uk.co.gencoreoperative.btw.ui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import uk.co.gencoreoperative.btw.ActionFactory;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.VersionResolver;
import uk.co.gencoreoperative.btw.ui.Context;
import uk.co.gencoreoperative.btw.ui.Strings;
import uk.co.gencoreoperative.btw.ui.signals.InstalledVersion;
import uk.co.gencoreoperative.btw.ui.signals.MinecraftHome;
import uk.co.gencoreoperative.btw.ui.signals.PatchFile;

public class PatchAction extends AbstractAction implements Observer {
    private final Context context;
    private final ActionFactory factory;

    public PatchAction(Context context, ActionFactory factory) {
        this.context = context;
        this.factory = factory;

        putValue(Action.NAME, Strings.BUTTON_PATCH.getText());

        context.register(MinecraftHome.class, this);
        context.register(PatchFile.class, this);
        update(null, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MinecraftHome minecraftHome = context.get(MinecraftHome.class);
        PatchFile patchFile = context.get(PatchFile.class);

        PathResolver pathResolver = new PathResolver(minecraftHome.getFolder());

        // TODO: Failed to create folder
        File installationFolder = factory.createInstallationFolder(pathResolver);
        // TODO: Failed to write JSON
        File json = factory.copyJsonToInstallation(installationFolder);

        // TODO: Async task
        // Create the Better Than Wolves Jar
        File jar = factory.mergeClientJarWithPatch(pathResolver, patchFile.getFile());
        InstalledVersion installedVersion = new InstalledVersion(jar);

        // Assign version to folder
        installedVersion.setVersion(patchFile.getVersion());
        context.add(installedVersion);

        VersionResolver versionResolver = new VersionResolver();
        versionResolver.writeVersion(pathResolver.betterThanWolves(), installedVersion.getVersion());
    }

    @Override
    public void update(Observable o, Object arg) {
        setEnabled(context.contains(MinecraftHome.class, PatchFile.class));
    }
}
