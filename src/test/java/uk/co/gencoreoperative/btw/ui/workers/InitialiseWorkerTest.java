package uk.co.gencoreoperative.btw.ui.workers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.cuppa.Cuppa.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.assertj.core.api.Condition;
import org.assertj.core.util.Files;
import org.forgerock.cuppa.Cuppa;
import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;
import uk.co.gencoreoperative.btw.PathResolver;
import uk.co.gencoreoperative.btw.ui.Errors;
import uk.co.gencoreoperative.btw.utils.FileUtils;

/**
 * Demonstrates the functionality of the {@link InitialiseWorker}.
 */
@RunWith(CuppaRunner.class)
@Test
public class InitialiseWorkerTest {

    private PathResolver resolver;

    {
        describe(InitialiseWorkerTest.class.getSimpleName(), () -> {

            beforeEach(() -> {
                // Setup a PathResolver and ensure it is a valid folder for each test
                resolver = new PathResolver(temporaryFile());
                assertThat(resolver.get().mkdirs()).isTrue();
            });
            afterEach(() -> FileUtils.recursiveDelete(resolver.get()));

            when("initialise into an empty folder", () -> {

                InitialiseWorker worker = new InitialiseWorker(resolver);

                it("creates the BTW folder", () -> {
                    PatchWorker.Status status = worker.doInBackground();
                    assertThat(status.isSuccess()).isTrue();
                    assertThat(resolver.get()).exists();
                });
            });

            when("initialise incorrectly on top of a file", () -> {
                File tempFile = getFile();
                assertThat(tempFile).isFile();
                after(() -> assertThat(tempFile.delete()).isTrue());

                InitialiseWorker worker = new InitialiseWorker(new PathResolver(tempFile));

                it("fails with error", () -> {
                    PatchWorker.Status status = worker.doInBackground();
                    assertThat(status.isSuccess()).isFalse();
                    assertThat(status.getCode()).isEqualTo(Errors.FAILED_FILE_IN_THE_WAY);
                });
            });

            when("initialise attempts to delete the version.json which cannot be deleted", () -> {
                it("fails with error", () -> {
                    // TODO - make an unmovable file in the right location
                    // TODO - verify that initialise cannot delete it.
                });
            });

            // Verify that we have cleaned up
            after(() -> assertThat(resolver.get().exists()).isFalse());
        });
    }

    private File getFile() {
        try {
            return File.createTempFile("abd", "def");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static File temporaryFile() {
        return new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
    }

    /**
     * A hacky way to prevent a file from being deleted, is to create the file as a
     * folder that contains another file. This way a file delete request will fail.
     */
    private static void makeUndeletableFile() {

    }
}
