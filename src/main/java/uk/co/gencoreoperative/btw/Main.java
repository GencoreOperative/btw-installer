package uk.co.gencoreoperative.btw;

import uk.co.gencoreoperative.btw.ui.Progress;

import java.io.File;
import java.util.Arrays;
import java.util.function.Predicate;

// Based on  http://www.sargunster.com/btwforum/viewtopic.php?f=9&t=8925
public class Main {
    private static final Predicate<File> EXISTS = File::exists;

    private Progress progress = new Progress();
    private TaskFactory factory = new TaskFactory(progress);

    public Main() {
        Arrays.stream(Tasks.values()).forEach(tasks -> progress.addItem(tasks.getTask()));

        // Request the location of the Minecraft installation
        File installationFolder = factory.selectMinecraftHome()
                .thenValidate(Tasks.INSTALLATION_FOLDER, EXISTS);

        // Verify that 1.5.2 version is present.
        PathResolver pathResolver = factory.getPathResolver(installationFolder)
                .thenValidate(Tasks.ONE_FIVE_TWO_EXISTS,
                        resolver -> new File(resolver.oneFiveTwo(), "1.5.2.jar").exists());

        // Remove previous installation
        File targetFolder = pathResolver.betterThanWolves();
        factory.removePreviousInstallation(pathResolver).thenValidate(Tasks.PREVIOUS_REMOVED, EXISTS.negate());

        // Create target folder
        factory.createInstallationFolder(targetFolder).thenValidate(Tasks.CREATED_FOLDER, EXISTS);

        // Copy JSON from resources - we do not expect this to change
        factory.copyJsonToInstallation(targetFolder).thenValidate(Tasks.COPIED_JSON, EXISTS);

        // Request Patch Zip from user.
        File patchFile = factory.selectPatchZip().thenValidate(Tasks.PATCH_WAS_SELECTED, EXISTS);

        // stream the contents of the BTW Patch utils into a map
        factory.mergePatchAndRelease(targetFolder, patchFile, pathResolver).thenValidate(Tasks.COPIED_JAR, EXISTS);

    }

    public static void main(String... args) {
        new Main();
    }
}
