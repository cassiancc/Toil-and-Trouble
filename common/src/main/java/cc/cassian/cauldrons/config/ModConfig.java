package cc.cassian.cauldrons.config;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;

public class ModConfig extends WrappedConfig {
    @Comment("Whether to only allow brewing if the Cauldron is heated.")
    public final boolean requiresHeat = false;
    @Comment("How much to speed up the brewing process if the Cauldron is heated.")
    @Comment("A value of 2 would halve the time it takes to brew a potion.")
    public float heatAmplification = 1;
    @Comment("How long should it take to brew a potion.")
    public int brewingTime = 60;

}
