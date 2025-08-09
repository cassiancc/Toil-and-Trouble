package cc.cassian.cauldrons.blocks.entity;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.core.CauldronModTags;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronSoundEvents;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.BREWING;
import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.POTION_QUANTITY;

public class CauldronBlockEntity extends BlockEntity {

    protected PotionContents potion = PotionContents.EMPTY;
    protected boolean splashing = false;
    protected boolean splashParticles = false;
    protected boolean lingering = false;
    protected boolean lingeringParticles = false;
    private int progress;
    private int maxProgress = CauldronMod.CONFIG.brewingTime.value() * 20;
    private int bubbleTimer = 0;
    private boolean pop = false;

    private ItemStack reagent = ItemStack.EMPTY;

    public CauldronBlockEntity(BlockPos pos, BlockState state, Holder<Potion> water) {
        super(CauldronBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), pos, state);
        this.potion = new PotionContents(water);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public CauldronBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CauldronBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), blockPos, blockState);

    }

    @Override
    public void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        Optional<ItemStack> inventory = tag.read("cauldron.inventory", ItemStack.SINGLE_ITEM_CODEC);
        if (inventory.isPresent())
            reagent = inventory.get();
        else reagent = ItemStack.EMPTY;
        progress = tag.getIntOr("cauldron.progress", 0);
        maxProgress = tag.getIntOr("cauldron.max_progress", 0);
        var p = tag.getStringOr("cauldron.potion", "minecraft:air");
        if (!p.equals("minecraft:air")) {
            potion = BuiltInRegistries.POTION.get(ResourceLocation.parse(p)).map(PotionContents::new).orElse(PotionContents.EMPTY);
        } else {
            potion = PotionContents.EMPTY;
        }
        splashing = tag.getBooleanOr("cauldron.splashing", false);
        lingering = tag.getBooleanOr("cauldron.lingering", false);
    }

    @Override
    public void saveAdditional(ValueOutput tag) {
        if (!reagent.isEmpty()) {
            tag.store("cauldron.inventory", ItemStack.SINGLE_ITEM_CODEC, reagent);
        }
        tag.putInt("cauldron.progress", progress);
        tag.putInt("cauldron.max_progress", maxProgress);
        if (this.potion != null && potion.potion().isPresent() && potion.potion().orElseThrow().unwrapKey().isPresent()) {
            tag.putString("cauldron.potion", potion.potion().orElseThrow().unwrapKey().orElseThrow().location().toString());
        } else {
            tag.putString("cauldron.potion", "minecraft:air");
        }
        tag.putBoolean("cauldron.splashing", splashing);
        tag.putBoolean("cauldron.lingering", lingering);
        super.saveAdditional(tag);
    }

    public Pair<InteractionResult, ItemStack> insert(ItemStack itemStack) {
        var potionQuantity = getFillLevel();
        // fill with water bucket
        if (itemStack.is(Items.WATER_BUCKET) && potionQuantity == 0) {
            setFillLevel(3);
            this.potion = new PotionContents(Potions.WATER);
            return new Pair<>(InteractionResult.SUCCESS, Items.BUCKET.getDefaultInstance());
        // fill with potion
        } else if (itemStack.has(DataComponents.POTION_CONTENTS) && potionQuantity < 3 && !itemStack.is(CauldronModTags.CANNOT_FILL_CAULDRON)) {
            PotionContents insertedPotion = itemStack.get(DataComponents.POTION_CONTENTS);
            assert insertedPotion != null;
            Optional<Holder<Potion>> currentPotion = this.potion.potion();
            if (currentPotion.isEmpty()) {
                this.potion = insertedPotion;
                setFillLevel(1);
                return new Pair<>(InteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance());
            }
            else if (insertedPotion.is(currentPotion.get())) {
                setFillLevel(potionQuantity+1);
                return new Pair<>(InteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance());
            }
        // drain with bucket
        } else if (itemStack.is(Items.BUCKET) && potionQuantity>=1) {
            ItemStack returnStack;
            if (isPotionWater()) returnStack = Items.WATER_BUCKET.getDefaultInstance();
            else returnStack = Items.BUCKET.getDefaultInstance();
            setFillLevel(0);
            return new Pair<>(InteractionResult.SUCCESS, returnStack);
        // drain with bottle
        } else if (itemStack.is(Items.GLASS_BOTTLE) && potionQuantity>=1) {
            var potionItem = Items.POTION;
            if (splashing) potionItem = Items.SPLASH_POTION;
            else if (lingering) potionItem = Items.LINGERING_POTION;
            ItemStack stack = createItemStack(potionItem, potion);
            setFillLevel(potionQuantity-1);
            return new Pair<>(InteractionResult.SUCCESS, stack);
        // drain with arrow
        } else if (itemStack.is(Items.ARROW) && potionQuantity>=1) {
            var stack = createItemStack(Items.TIPPED_ARROW, potion);
            setFillLevel(potionQuantity-1);
            return new Pair<>(InteractionResult.SUCCESS, stack);
        }
        // insert as inventory
        else if (reagent.isEmpty()) {
            reagent = itemStack;
            if (getFillLevel()>0 && this.getLevel().isClientSide()) {
                for (int i = 0; i < 20; i++) {
                    Random random = new Random();
                    double d = (random.nextDouble());
                    double e = (random.nextDouble());
                    this.getLevel().addParticle(ParticleTypes.SPLASH, this.getBlockPos().getX() + d, this.getBlockPos().getY() + 1F, this.getBlockPos().getZ() + e, 0.05, 0.25, 0.05);
                }
                progress = 0;
            }
            return new Pair<>(InteractionResult.SUCCESS, ItemStack.EMPTY);
        }
        return new Pair<>(InteractionResult.PASS, ItemStack.EMPTY);
    }

    public void brew() {
        if (potion.potion().isEmpty() || reagent.isEmpty()) return;
        if (this.level instanceof ServerLevel serverLevel) {
            Optional<RecipeHolder<BrewingRecipe>> recipe = serverLevel.recipeAccess().getRecipeFor(CauldronModRecipes.BREWING.get(), new BrewingRecipeInput(reagent, potion), level);
            if (recipe.isPresent()) {
                this.potion = recipe.get().value().getResultPotion(level.registryAccess());
                updateAfterBrewing();
                return;
            }
        }
        if (reagent.is(CauldronModTags.CREATES_SPLASH_POTIONS)) {
            this.splashing = true;
            this.lingering = false;
            updateAfterBrewing();
            this.splashParticles = true;
        }
        else if (reagent.is(CauldronModTags.CREATES_LINGERING_POTIONS)) {
            this.splashing = false;
            this.lingering = true;
            updateAfterBrewing();
            this.lingeringParticles = true;
        }
        else if (CauldronMod.CONFIG.useBrewingStandRecipes.value()) {
            var potionBrewing = this.level.potionBrewing();
            var potionItem = createItemStack(Items.POTION, potion);
            if (potionBrewing.hasMix(potionItem, reagent)) {
                ItemStack mix = potionBrewing.mix(reagent, potionItem);
                this.potion = mix.getComponents().get(DataComponents.POTION_CONTENTS);
                updateAfterBrewing();
            }
        }

    }

    private void updateAfterBrewing() {
        this.reagent = ItemStack.EMPTY;
        //level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, this.getBlockPos(), 0);
        level.playSound(null, getBlockPos(), CauldronSoundEvents.BREWS.get(), SoundSource.BLOCKS);
        level.setBlockAndUpdate(getBlockPos(), this.getBlockState().setValue(BrewingCauldronBlock.BREWING, false));
        bubbleTimer = 20;
        splashParticles = false;
        lingeringParticles = false;
    }

    public ItemStack retrieve() {
        return reagent.copyAndClear();
    }

    public ItemStack getItem() {
        return reagent;
    }

    public Integer getFillLevel() {
        Integer value = this.getBlockState().getValue(POTION_QUANTITY);
        return value;
    }

    public void setFillLevel(int value) {
        BrewingCauldronBlock.setFillLevel(this.getBlockState(), this.getLevel(), this.getBlockPos(), value);
    }

    public int getPotionColour() {
        return potion.getColor();
    }

    @Nullable
    public Holder<Potion> getPotion() {
        if (potion != null && potion.potion().isPresent())
            return potion.potion().get();
        else return null;
    }

    public static ItemStack createItemStack(Item item, PotionContents potion) {
        return PotionContents.createItemStack(item, potion.potion().get());
    }

    public boolean isPotionWater() {
        if (potion.potion().isPresent())
            return Objects.equals(potion.potion().get(), Potions.WATER);
        return false;
    }

    public boolean isPotionSplash() {
        if (potion.potion().isPresent())
            return splashing;
        return false;
    }

    public boolean isPotionLingering() {
        if (potion.potion().isPresent())
            return lingering;
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, BlockEntity blockEntity) {
        if (blockEntity instanceof CauldronBlockEntity cauldronBlockEntity) {
            // particle logic
            if (cauldronBlockEntity.isBubbling()) {
                double d = pos.getX() + level.random.nextDouble();
                double e = pos.getY() + 1;
                double f = pos.getZ() + level.random.nextDouble();
                if (cauldronBlockEntity.getPotion() != null) {
                    var effects = cauldronBlockEntity.getPotion().value().getEffects();
                    if (cauldronBlockEntity.splashParticles) {
                        level.addParticle(ParticleTypes.SMOKE, d, e, f, 0.01, 0.05, 0.01);
                    }
                    else if (cauldronBlockEntity.lingeringParticles) {
                        level.addParticle(ParticleTypes.DRAGON_BREATH, d, e, f, 0.01, 0.05, 0.01);
                    }
                    else if (!effects.isEmpty()) {
                        for (MobEffectInstance effect : effects) {
                            level.addParticle(effect.getParticleOptions(), d, e, f, 0.01, 0.05, 0.01);
                        }
                    } else {
                        if (cauldronBlockEntity.pop) {
                            level.addParticle(ParticleTypes.BUBBLE_POP, d, e, f, 0.01, 0.05, 0.01);
                        } else {
                            level.addParticle(ParticleTypes.BUBBLE, d, e, f, 0.01, 0.05, 0.01);
                        }
                    }
                }
                cauldronBlockEntity.pop = !cauldronBlockEntity.pop;
                cauldronBlockEntity.bubbleTimer--;
            }
            // brewing
            boolean cauldronHeated = level.getBlockState(pos.below()).is(CauldronModTags.HEATS_CAULDRON);
            boolean cauldronCanBrew = cauldronHeated || !CauldronMod.CONFIG.requiresHeat.value();
            if (cauldronCanBrew && !cauldronBlockEntity.reagent.isEmpty()) {
                var maxProgress = cauldronBlockEntity.maxProgress;
                if (cauldronHeated) {
                    maxProgress = (int) (maxProgress*CauldronMod.CONFIG.heatAmplification.value());
                }
                if (cauldronBlockEntity.progress > maxProgress) {
                    cauldronBlockEntity.brew();
                    cauldronBlockEntity.progress = 0;
                } else {
                    cauldronBlockEntity.progress++;
                    if (!blockState.getValue(BREWING))
                        level.setBlockAndUpdate(pos, blockState.setValue(BREWING, true));
                }
            }
            //reset to vanilla
            if (cauldronBlockEntity.reagent.isEmpty()) {
                if (cauldronBlockEntity.getFillLevel().equals(0)) {
                    var newState = Blocks.CAULDRON.defaultBlockState();
                    level.setBlockAndUpdate(pos, newState);
                } else if (blockState.getValue(BREWING)) {
                    level.setBlockAndUpdate(pos, blockState.setValue(BrewingCauldronBlock.BREWING, false));
                }
            }
            if (blockState.getValue(POTION_QUANTITY).equals(0)) {
                cauldronBlockEntity.potion = PotionContents.EMPTY;
                cauldronBlockEntity.splashing = false;
                cauldronBlockEntity.lingering = false;
            }
        }
    }

    private boolean isBubbling() {
        return bubbleTimer > 0;
    }
}
