package cc.cassian.cauldrons;

//? if fabric
import net.fabricmc.loader.api.FabricLoader;
//? if neoforge
/*import net.neoforged.fml.loading.FMLPaths;*/

import java.nio.file.Path;

public class Platform {
    public static Path getConfigDir() {
        //? if fabric {
        return FabricLoader.getInstance().getConfigDir();
        //?} else {
        /*return FMLPaths.CONFIGDIR.get();
        *///?}
    }

	public static boolean isModLoaded(String mod) {
		return FabricLoader.getInstance().isModLoaded(mod);
	}

	public static boolean isDev() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}
