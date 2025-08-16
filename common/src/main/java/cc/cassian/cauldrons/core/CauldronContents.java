package cc.cassian.cauldrons.core;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.alchemy.PotionContents.getColorOptional;

public record CauldronContents(ResourceLocation id, Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects) {
    public static final CauldronContents EMPTY = new CauldronContents(ResourceLocation.withDefaultNamespace("air"), Optional.empty(), Optional.empty(), List.of());

    private static final Codec<CauldronContents> FULL_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ResourceLocation.CODEC.fieldOf("id").forGetter(CauldronContents::id),
                            Potion.CODEC.optionalFieldOf("potion").forGetter(CauldronContents::potion),
                            Codec.INT.optionalFieldOf("custom_color").forGetter(CauldronContents::customColor),
                            MobEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(CauldronContents::customEffects)
                    )
                    .apply(instance, CauldronContents::new)
    );

    public static final Codec<CauldronContents> CODEC = Codec.withAlternative(FULL_CODEC, PotionContents.CODEC, CauldronContents::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, CauldronContents> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            CauldronContents::id,
            Potion.STREAM_CODEC.apply(ByteBufCodecs::optional),
            CauldronContents::potion,
            ByteBufCodecs.INT.apply(ByteBufCodecs::optional),
            CauldronContents::customColor,
            MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            CauldronContents::customEffects,
            CauldronContents::new
    );

    public CauldronContents(Holder<Potion> potion) {
        this(ResourceLocation.withDefaultNamespace("potion"),Optional.of(potion), Optional.empty(), List.of());
    }

    public CauldronContents(PotionContents potion) {
        this(ResourceLocation.withDefaultNamespace("potion"), potion.potion(), potion.customColor(), potion.customEffects());
    }

    public boolean is(Holder<Potion> potion) {
        return this.potion.isPresent() && this.potion.get().is(potion) && this.customEffects.isEmpty();
    }

    public boolean is(ResourceLocation potion) {
        return potion.equals(id());
    }

    public PotionContents toPotionContents() {
        return new PotionContents(potion, customColor, customEffects);
    }

    public int getColor() {
        return this.customColor.orElseGet(() -> getColor(this.getAllEffects()));
    }

    public static int getColor(Holder<Potion> potion) {
        return getColor(potion.value().getEffects());
    }

    public static int getColor(Iterable<MobEffectInstance> effects) {
        return getColorOptional(effects).orElse(-13083194);
    }

    public Iterable<MobEffectInstance> getAllEffects() {
        return this.potion.map(potionHolder -> this.customEffects.isEmpty()
                ? ((Potion) ((Holder) potionHolder).value()).getEffects()
                : Iterables.concat(((Potion) ((Holder) potionHolder).value()).getEffects(), this.customEffects)).orElse(this.customEffects);
    }

}
