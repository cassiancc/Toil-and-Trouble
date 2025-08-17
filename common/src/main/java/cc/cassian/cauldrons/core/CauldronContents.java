package cc.cassian.cauldrons.core;

import cc.cassian.cauldrons.CauldronMod;
import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Optional;

public record CauldronContents(ResourceLocation id, Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects, Integer amount) {
    public static final CauldronContents EMPTY = new CauldronContents(ResourceLocation.withDefaultNamespace("air"), Optional.empty(), Optional.empty(), List.of(), 0);

    private static final Codec<CauldronContents> FULL_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ResourceLocation.CODEC.fieldOf("id").forGetter(CauldronContents::id),
                            Potion.CODEC.optionalFieldOf("potion").forGetter(CauldronContents::potion),
                            Codec.INT.optionalFieldOf("custom_color").forGetter(CauldronContents::customColor),
                            MobEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(CauldronContents::customEffects),
                            Codec.INT.optionalFieldOf("amount", 3).forGetter(CauldronContents::amount)
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
            ByteBufCodecs.INT,
            CauldronContents::amount,
            CauldronContents::new
    );

    public CauldronContents(Holder<Potion> potion) {
        this(ResourceLocation.withDefaultNamespace("potion"),Optional.of(potion), Optional.empty(), List.of(), 3);
    }

    public CauldronContents(PotionContents potion) {
        this(ResourceLocation.withDefaultNamespace("potion"), potion.potion(), potion.customColor(), potion.customEffects(), 3);
    }

    public CauldronContents(ResourceLocation potion) {
        this(potion, Optional.empty(), Optional.empty(), List.of(), 3);
    }

    public CauldronContents(String potion) {
        this(CauldronMod.of(potion));
    }

    public static ItemStack createItemStack(Item item, CauldronContents potion) {
        if (potion.potion().isPresent()) {
            return PotionContents.createItemStack(item, potion.potion().get());
        }
        var stack = item.getDefaultInstance();
        stack.set(DataComponents.POTION_CONTENTS, potion.toPotionContents());
        return stack;
    }

    public boolean is(Holder<Potion> potion) {
        return this.potion.isPresent() && this.potion.get().is(potion) && this.customEffects.isEmpty();
    }

    public boolean is(ResourceLocation potion) {
        return potion.equals(id());
    }

    public PotionContents toPotionContents() {
        return new PotionContents(potion, customColor, customEffects, Optional.empty());
    }

    public int getColor() {
        return this.customColor.orElseGet(() -> PotionContents.getColorOptional(this.getAllEffects()).orElse(-13083194));
    }

    public Iterable<MobEffectInstance> getAllEffects() {
        return this.potion.map(potionHolder -> this.customEffects.isEmpty()
                ? ((Potion) ((Holder) potionHolder).value()).getEffects()
                : Iterables.concat(((Potion) ((Holder) potionHolder).value()).getEffects(), this.customEffects)).orElse(this.customEffects);
    }

    public boolean test(CauldronContents testedContents) {
        if (this.potion.isPresent() && testedContents.potion.isPresent()) {
            return this.is(testedContents.potion.get());
        }
        return this.is(testedContents.id);
    }

    public boolean isPotion() {
        return this.is(ResourceLocation.withDefaultNamespace("potion"));
    }

    public boolean is(String name) {
        return this.is(CauldronMod.of(name));
    }
}
