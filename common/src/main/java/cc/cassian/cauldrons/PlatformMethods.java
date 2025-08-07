package cc.cassian.cauldrons;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class PlatformMethods {
    @ExpectPlatform
    public static Path getConfigDir() {
        return null;
    }
}
