package uk.co.gencoreoperative.btw.version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.forgerock.cuppa.Cuppa.after;
import static org.forgerock.cuppa.Cuppa.describe;
import static org.forgerock.cuppa.Cuppa.it;
import static org.forgerock.cuppa.Cuppa.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.co.gencoreoperative.btw.version.VersionResolverTest.getTmpFolder;

import java.io.File;
import java.io.IOException;

import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.junit.CuppaRunner;
import org.junit.runner.RunWith;
import uk.co.gencoreoperative.btw.utils.FileUtils;

@Test
@RunWith(CuppaRunner.class)
public class VersionManagerTest {
    {
        describe("Version Resolver Selection", () -> {
            when("Given no VersionResolvers", () -> {
                it("Throws an error", () -> {
                    assertThatThrownBy(VersionManager::new)
                            .isInstanceOf(IllegalStateException.class);
                });
            });

            when("Given two VersionResolvers", () -> {

                VersionResolver one = deprecatedVersionResolver(nonApplicableResolver());
                VersionResolver two = applicableResolver();
                VersionManager manager = new VersionManager(one, two);

                it("Selects the applicable one", () -> {
                    manager.getVersion();
                    verify(two).readVersion();
                });

                it("does not use the deprecated resolver for writing", () -> {
                    manager.save(new Version("badger"));
                    verify(two, times(1)).writeVersion(any(Version.class));
                    verify(one, times(0)).writeVersion(any(Version.class));
                });
            });

            when("No versions are applicable", () -> {
                final VersionManager manager = new VersionManager(nonApplicableResolver());
                it("does not return a version", () -> {
                    assertThat(manager.getVersion().isPresent()).isFalse();
                });
            });

            when("All versions are deprecated", () -> {

                it("throws an ISE", () -> {
                    assertThatThrownBy(() -> new VersionManager(deprecatedVersionResolver()))
                            .isInstanceOf(IllegalStateException.class);
                });
            });
        });

        describe("VersionResolver errors", () -> {
            VersionResolver mockResolver = mockResolver();
            try {
                given(mockResolver.readVersion()).willThrow(new IOException());
            } catch (IOException e) {
                fail(e.getMessage());
            }
            final VersionManager manager = new VersionManager(mockResolver);

            when("an IOException occurs", () -> {
                it("does not return a value", () -> {
                    assertThat(manager.getVersion().isPresent()).isFalse();
                });
            });
        });

        describe("Upgrade from V1 to V2", () -> {
            final File tmpFolder = getTmpFolder();
            after("Cleanup Folder", () -> {
                FileUtils.recursiveDelete(tmpFolder);
            });

            VersionResolverV1 versionResolverV1 = new VersionResolverV1(tmpFolder);
            VersionResolverV2 versionResolverV2 = new VersionResolverV2(tmpFolder);
            final VersionManager manager = new VersionManager(versionResolverV1, versionResolverV2);

            when("starting with V1", () -> {
                try {
                    VersionResolverV1.writeVersion(tmpFolder, "badger");
                } catch (IOException e) {
                    fail(e.getMessage());
                }

                it("can read Version", () -> {
                    Version version = manager.getVersion().get();
                    assertThat(version.getPatchVersion()).isEqualTo("badger");
                    // Cleanup V1 version so that it does not interfere with next block
                    assertThat(new File(tmpFolder, "version.txt").delete()).isTrue();
                });

                it("writes it in V2 format", () -> {
                    manager.save(new Version("badger"));
                    assertThat(versionResolverV1.isApplicable()).isFalse();
                    assertThat(versionResolverV2.isApplicable()).isTrue();
                });
            });
        });
    }

    private VersionResolver deprecatedVersionResolver(VersionResolver resolver) {
        given(resolver.isDeprecated()).willReturn(true);
        return resolver;
    }

    private static VersionResolver mockResolver() {
        VersionResolver mockResolver = mock(VersionResolver.class);
        given(mockResolver.isDeprecated()).willReturn(false);
        return mockResolver;
    }

    private VersionResolver applicableResolver() {
        VersionResolver mockResolver = mockResolver();
        given(mockResolver.isApplicable()).willReturn(true);
        try {
            given(mockResolver.readVersion()).willReturn(new Version("badger"));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return mockResolver;
    }

    private static VersionResolver nonApplicableResolver() {
        VersionResolver mockResolver = mockResolver();
        given(mockResolver.isApplicable()).willReturn(false);
        return mockResolver;
    }

    private VersionResolver deprecatedVersionResolver() {
        VersionResolver mockResolver = mockResolver();
        given(mockResolver.isDeprecated()).willReturn(true);
        return mockResolver;
    }
}
