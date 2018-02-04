package uk.co.gencoreoperative.btw.version;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import uk.co.gencoreoperative.btw.utils.FileUtils;

public class VersionResolverV2 implements VersionResolver {
    private final File versionFile;
    private final Gson gson = new Gson();

    public VersionResolverV2(File folder) {
        versionFile = new File(folder, "version-v2.json");
    }

    @Override
    public boolean isApplicable() {
        return versionFile.exists();
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public Version readVersion() throws IOException {
        return gson.fromJson(new FileReader(versionFile), Version.class);
    }

    @Override
    public void writeVersion(Version version) throws IOException {
        String json = gson.toJson(version);
        FileUtils.copyStream(
                new ByteArrayInputStream(json.getBytes()),
                new FileOutputStream(versionFile),
                true,
                true);
    }
}
