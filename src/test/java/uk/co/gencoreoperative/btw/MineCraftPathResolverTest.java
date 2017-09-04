package uk.co.gencoreoperative.btw;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;


public class MineCraftPathResolverTest {
    @Test
    public void shouldResolveInstallationFolder() {
        File path = MineCraftPathResolver.getDefaultMinecraftPath();
        assertThat(path.getAbsolutePath()).isNotEmpty();
    }
}