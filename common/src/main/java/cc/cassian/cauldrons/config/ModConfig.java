package cc.cassian.cauldrons.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class ModConfig extends ReflectiveConfig {
    @Comment("Whether to only allow brewing if the Cauldron is heated.")
    public final TrackedValue<Boolean> requiresHeat = this.value(false);
    @Comment("How much to speed up the brewing process if the Cauldron is heated.")
    @Comment("A value of 0.5 would halve the time it takes to brew a potion.")
    public final TrackedValue<Float> heatAmplification = this.value(1f);
    @Comment("How long should it take to brew a potion (in seconds).")
    public final TrackedValue<Integer> brewingTime = this.value(3);
    @Comment("Whether standing in a Cauldron should cause the entity to absorb the potion inside.")
    public final TrackedValue<Boolean> cauldronsApplyEffects = this.value(true);
    @Comment("Allow Cauldrons to craft all recipes a Brewing Stand can.")
    public final TrackedValue<Boolean> useBrewingStandRecipes = this.value(true);
    @Comment("Whether an item entity in a vanilla cauldron should change it to a Toil and Trouble Cauldron and insert the item into the Cauldron's inventory.")
    public boolean itemEntitiesConvertCauldrons = true;
}
