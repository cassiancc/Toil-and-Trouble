package cc.cassian.cauldrons.registry;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class CauldronModSoundEvents {
    public static final Supplier<SoundEvent> BREWS = CommonRegistry.registerSoundEvent("block.cauldron.brews");

    public static void touch() {

    }
}
