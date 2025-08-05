package cc.cassian.cauldrons;

import net.fabricmc.loader.api.FabricLoader;

public class PlatformMethods {
    public static boolean isModLoaded(String mod) {
        return FabricLoader.getInstance().isModLoaded(mod);
    }
}
