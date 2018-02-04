package uk.co.gencoreoperative.btw.version;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.forgerock.cuppa.Cuppa.after;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;

import java.io.File;
import java.io.IOException;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;
import uk.co.gencoreoperative.btw.utils.FileUtils;

/**
 * Upgrade test to demonstrate that upgrading from one VersionResolver to another
 * works correctly.
 */
@Test
@RunWith(CuppaRunner.class)
public class VersionResolverTest {
    {
        describe("Version V1", () -> {
            when("V1 Version is written", () -> {
                final File tmpFolder = getTmpFolder();

                after("Cleanup Folder", () -> {
                    FileUtils.recursiveDelete(tmpFolder);
                });

                try {
                    VersionResolverV1.writeVersion(tmpFolder, "badger");
                } catch (IOException e) {
                    fail(e.getMessage());
                }

                VersionResolverV1 resolver = new VersionResolverV1(tmpFolder);

                it("V1 Version is Applicable", () -> {
                    assertThat(resolver.isApplicable()).isTrue();
                });

                it("V1 Version can read it", () -> {
                    Version version = resolver.readVersion();
                    assertThat(version.getPatchVersion()).isEqualTo("badger");
                });
            });
        });

        describe("Version V2", () -> {
            when("creating a Version", () -> {
                Version version = new Version("badger");
                File tmpFolder = getTmpFolder();
                VersionResolverV2 resolver = new VersionResolverV2(tmpFolder);

                it("writes it to folder", () -> {
                    resolver.writeVersion(version);
                });

                it("#isApplicable", () -> {
                    assertThat(resolver.isApplicable()).isTrue();
                });

                it("reads the version", () -> {
                    assertThat(resolver.readVersion().getPatchVersion()).isEqualTo("badger");
                });
            });
        });
    }

    public static File getTmpFolder() {
        File folder = new File(
                System.getProperty("java.io.tmpdir"),
                Double.toHexString(Math.random()));
        assertThat(folder.mkdirs()).isTrue();
        return folder;
    }
}