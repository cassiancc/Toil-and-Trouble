package cc.cassian.cauldrons.registry;

import cc.cassian.cauldrons.CauldronMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CommonRegistry {
    public static <R, T extends R> Supplier<T> register(String name, Supplier<T> supplier, Registry<R> reg) {
        T object = supplier.get();
        Registry.register(reg, CauldronMod.of(name), object);
        return () -> object;
    }

    public static <T> Supplier<DataComponentType<T>> registerComponentType(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return register(name, () -> (builderOperator.apply(DataComponentType.builder())).build(), BuiltInRegistries.DATA_COMPONENT_TYPE);
    }

    public static <B extends Item> Supplier<B> registerItem(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.ITEM);
    }

    public static <B extends Block> Supplier<B> registerBlock(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.BLOCK);
    }

    public static <T extends EntityType<?>> Supplier<T> registerEntity(String name, Supplier<T> supplier) {
        return register(name, supplier, BuiltInRegistries.ENTITY_TYPE);
    }

    public static Supplier<SoundEvent> registerSoundEvent(String name, Supplier<SoundEvent> supplier) {
        return register(name, supplier, BuiltInRegistries.SOUND_EVENT);
    }

    public static Supplier<SoundEvent> registerSoundEvent(String name) {
        return registerSoundEvent(name, ()->SoundEvent.createVariableRangeEvent(CauldronMod.of(name)));
    }

    public static Holder<MobEffect> registerMobEffect(String name, Supplier<MobEffect> supplier) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, CauldronMod.of(name), supplier.get());
    }

    public static <B extends RecipeSerializer<?>> Supplier<B> registerRecipeSerializer(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.RECIPE_SERIALIZER);
    }

    public static <B extends RecipeType<?>> Supplier<B> registerRecipe(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.RECIPE_TYPE);
    }

    public static <B extends BlockEntityType<?>> Supplier<B> registerBlockEntity(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.BLOCK_ENTITY_TYPE);
    }
}