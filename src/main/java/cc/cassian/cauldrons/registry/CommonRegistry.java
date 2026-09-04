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

    public static <R, T extends R> T register(String name, T object, Registry<R> reg) {
		Registry.register(reg, CauldronMod.of(name), object);
        return object;
    }

    public static SoundEvent registerSoundEvent(String name, SoundEvent supplier) {
        return register(name, supplier, BuiltInRegistries.SOUND_EVENT);
    }

    public static SoundEvent registerSoundEvent(String name) {
        return registerSoundEvent(name, SoundEvent.createVariableRangeEvent(CauldronMod.of(name)));
    }

    public static <B extends RecipeSerializer<?>> B registerRecipeSerializer(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.RECIPE_SERIALIZER).get();
    }

    public static <B extends RecipeType<?>> B registerRecipe(String name, Supplier<B> supplier) {
        return register(name, supplier, BuiltInRegistries.RECIPE_TYPE).get();
    }
}