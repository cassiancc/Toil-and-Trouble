package cc.cassian.cauldrons.blocks.entity;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.core.CauldronModTags;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import cc.cassian.cauldrons.recipe.DippingRecipe;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import cc.cassian.cauldrons.registry.CauldronModSoundEvents;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.*;

import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.*;

public class CauldronBlockEntity extends BlockEntity implements WorldlyContainer {

    protected CauldronContents potion = CauldronContents.EMPTY;
    protected boolean splashing = false;
    protected boolean lingering = false;
    private int progress;
    private int maxProgress = CauldronMod.CONFIG.brewingTime.value() * 20;
    private int bubbleTimer = 0;
    private boolean pop = false;

    private ItemStack reagent = ItemStack.EMPTY;
    private ParticleOptions particleType = ParticleTypes.BUBBLE;

    public CauldronBlockEntity(BlockPos pos, BlockState state, Holder<Potion> water) {
        super(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), pos, state);
        this.potion = new CauldronContents(water);
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
        super(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), blockPos, blockState);

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
        potion = tag.read("cauldron.potion", CauldronContents.CODEC).orElse(CauldronContents.EMPTY);
        splashing = tag.getBooleanOr("cauldron.splashing", false);
        lingering = tag.getBooleanOr("cauldron.lingering", false);
        bubbleTimer = tag.getIntOr("cauldron.bubble_timer", 0);
        particleType = tag.read("cauldron.particle_type", ParticleTypes.CODEC).orElse(ParticleTypes.BUBBLE);
    }

    @Override
    public void saveAdditional(ValueOutput tag) {
        if (!reagent.isEmpty()) {
            tag.store("cauldron.inventory", ItemStack.SINGLE_ITEM_CODEC, reagent);
        }
        tag.putInt("cauldron.progress", progress);
        tag.putInt("cauldron.max_progress", maxProgress);
        tag.store("cauldron.potion", CauldronContents.CODEC, potion);
        tag.putBoolean("cauldron.splashing", splashing);
        tag.putBoolean("cauldron.lingering", lingering);
        tag.putInt("cauldron.bubble_timer", bubbleTimer);
        tag.store("cauldron.particle_type", ParticleTypes.CODEC, particleType);
        super.saveAdditional(tag);
    }

    public Pair<InteractionResult, ItemStack> insert(ItemStack itemStack) {
        var potionQuantity = getFillLevel();
        // fill with water bucket
        if (itemStack.is(Items.WATER_BUCKET) && potionQuantity == 0) {
            setFillLevel(3);
            this.potion = new CauldronContents(Potions.WATER);
            return new Pair<>(InteractionResult.SUCCESS, Items.BUCKET.getDefaultInstance());
        // fill with lava bucket
        } else if (itemStack.is(Items.LAVA_BUCKET) && potionQuantity == 0) {
            setFillLevel(3);
            this.potion = new CauldronContents("lava");
            return new Pair<>(InteractionResult.SUCCESS, Items.BUCKET.getDefaultInstance());
        // fill with potion
        } else if (itemStack.has(DataComponents.POTION_CONTENTS) && potion.isPotion() && potionQuantity < 3 && !itemStack.is(CauldronModTags.CANNOT_FILL_CAULDRON)) {
            PotionContents insertedPotion = itemStack.get(DataComponents.POTION_CONTENTS);
            assert insertedPotion != null;
            Optional<Holder<Potion>> currentPotion = this.potion.potion();
            if (currentPotion.isEmpty()) {
                this.potion = new CauldronContents(insertedPotion);
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
            if (potionQuantity==3) {
                if (isPotionWater()) returnStack = Items.WATER_BUCKET.getDefaultInstance();
                else if (getContents().is("lava")) returnStack = Items.LAVA_BUCKET.getDefaultInstance();
                else returnStack = Items.BUCKET.getDefaultInstance();
            }
            else returnStack = Items.BUCKET.getDefaultInstance();
            setFillLevel(0);
            return new Pair<>(InteractionResult.SUCCESS, returnStack);
        // drain with bottle - moved to event
        // drain with arrow
        } else if (itemStack.is(Items.ARROW) && potionQuantity>=1) {
            var stack = CauldronContents.createItemStack(Items.TIPPED_ARROW, potion);
            setFillLevel(potionQuantity-1);
            return new Pair<>(InteractionResult.SUCCESS, stack);
        } else if (itemStack.is(Items.HONEY_BOTTLE) && potionQuantity<4) {
            this.potion = new CauldronContents("honey");
            setFillLevel(getFillLevel()+1);
            return new Pair<>(InteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance());
        }
        // insert as inventory
        else if (reagent.isEmpty()) {
            reagent = itemStack;
            if (getFillLevel()>0 && this.getLevel().isClientSide()) {
                var particle = ParticleTypes.SPLASH;
                if (this.potion.is("honey")) particle = ParticleTypes.LANDING_HONEY;
                else if (this.potion.is("lava")) particle = ParticleTypes.LANDING_LAVA;
                for (int i = 0; i < 20; i++) {
                    Random random = new Random();
                    double d = (random.nextDouble());
                    double e = (random.nextDouble());
                    this.getLevel().addParticle(particle, this.getBlockPos().getX() + d, this.getBlockPos().getY() + 1F, this.getBlockPos().getZ() + e, 0.05, 0.25, 0.05);
                }
                progress = 0;
            }
            return new Pair<>(InteractionResult.SUCCESS, ItemStack.EMPTY);
        }
        return new Pair<>(InteractionResult.TRY_WITH_EMPTY_HAND, ItemStack.EMPTY);
    }

    public void brew(boolean cauldronHeated) {
        var input = new BrewingRecipeInput(reagent, potion, cauldronHeated);
        if ((level instanceof ServerLevel serverLevel)) {
            Optional<RecipeHolder<BrewingRecipe>> brewingRecipe = serverLevel.recipeAccess().getRecipeFor(CauldronModRecipes.BREWING.get(), input, level);
            if (brewingRecipe.isPresent()) {
                this.potion = brewingRecipe.get().value().getResultPotion();
                updateAfterBrewing(ItemStack.EMPTY, this.potion, brewingRecipe.get().value().getParticleType());
            }
            Optional<RecipeHolder<DippingRecipe>> dippingRecipe = serverLevel.recipeAccess().getRecipeFor(CauldronModRecipes.DIPPING.get(), input, level);
            if (dippingRecipe.isPresent()) {
                updateAfterBrewing(dippingRecipe.get().value().getResultItem(), this.potion, dippingRecipe.get().value().getParticleType());
                setFillLevel(0);
            }
            else if (reagent.is(CauldronModTags.CREATES_SPLASH_POTIONS)) {
                this.splashing = true;
                this.lingering = false;
                updateAfterBrewing(ItemStack.EMPTY, this.potion, ParticleTypes.SMOKE);
            }
            else if (reagent.is(CauldronModTags.CREATES_LINGERING_POTIONS)) {
                this.splashing = false;
                this.lingering = true;
                updateAfterBrewing(ItemStack.EMPTY, this.potion, ParticleTypes.DRAGON_BREATH);
            }
            else if (CauldronMod.CONFIG.useBrewingStandRecipes.value()) {
                var potionBrewing = this.level.potionBrewing();
                var potionItem = CauldronContents.createItemStack(Items.POTION, potion);
                if (potionBrewing.hasMix(potionItem, reagent)) {
                    ItemStack mix = potionBrewing.mix(reagent, potionItem);
                    this.potion = new CauldronContents(Objects.requireNonNullElse(mix.getComponents().get(DataComponents.POTION_CONTENTS), PotionContents.EMPTY));
                    updateAfterBrewing(ItemStack.EMPTY, this.potion, ParticleTypes.BUBBLE);
                }
            }
        }

    }

    private void updateAfterBrewing(ItemStack stack, CauldronContents contents, ParticleOptions particleType) {
        this.reagent = stack;
        //level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, this.getBlockPos(), 0);
        this.level.playSound(null, getBlockPos(), CauldronModSoundEvents.BREWS.get(), SoundSource.BLOCKS);
        var state = this.getBlockState();
        // check for a potion quantity
        var potionQuantity = state.getOptionalValue(LayeredCauldronBlock.LEVEL).orElse(0)+ state.getOptionalValue(POTION_QUANTITY).orElse(0)+contents.amount();
        // check for a blockstate
        if (contents.isPotion()) {
            Optional<Block> optional = BuiltInRegistries.BLOCK.getOptional(contents.id());
            if (optional.isPresent()) {
                state = optional.get().defaultBlockState().trySetValue(LayeredCauldronBlock.LEVEL, potionQuantity).trySetValue(POTION_QUANTITY, potionQuantity);
            }
        }
        //setblock
        if (potionQuantity>3) potionQuantity = 3;
        this.level.setBlockAndUpdate(getBlockPos(), state.trySetValue(BrewingCauldronBlock.BREWING, false).trySetValue(POTION_QUANTITY, potionQuantity).trySetValue(CONTENTS, getContentsProperty()));
        this.bubbleTimer = 20;
        this.particleType = particleType;
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
        if (potion.isPotion())
            return potion.getColor();
        else return -1;
    }

    public List<Component> getForWaila(BlockState state) {
        List<Component> iTooltip = new ArrayList<>();
        if (getContents() != CauldronContents.EMPTY) {
            iTooltip.add(Component.translatable("gui.toil_and_trouble.doses", state.getValue(BrewingCauldronBlock.POTION_QUANTITY)).withStyle(ChatFormatting.DARK_PURPLE));
            if (getContents().isPotion()) {
                var item = Items.POTION;
                if (isPotionSplash())
                    item = Items.SPLASH_POTION;
                else if (isPotionLingering())
                    item = Items.LINGERING_POTION;
                iTooltip.add(CauldronContents.createItemStack(item, getContents()).getHoverName());
                if (Screen.hasShiftDown())
                    PotionContents.addPotionTooltip(getContents().getAllEffects(), iTooltip::add, 0, 0);
            } else {
                iTooltip.add(Component.translatableWithFallback(getContents().id().toLanguageKey("cauldron"), ""));
            }
            if (!getItem().isEmpty()) {
                iTooltip.add(Component.empty());
            }
        }
        return iTooltip;
    }

    public CauldronContents getContents() {
        return potion;
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
                if (cauldronBlockEntity.getContents() != CauldronContents.EMPTY) {
                    ArrayList<MobEffectInstance> effects = new ArrayList<>();
                    cauldronBlockEntity.getContents().getAllEffects().forEach(effects::add);
                    if (cauldronBlockEntity.particleType != ParticleTypes.BUBBLE) {
                        level.addParticle(cauldronBlockEntity.particleType, d,e,f,0.01, 0.05, 0.01);
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
            boolean cauldronHeated = level.getBlockState(pos.below()).is(CauldronModTags.HEATS_CAULDRONS);
            boolean cauldronCanBrew = cauldronHeated || !CauldronMod.CONFIG.requiresHeat.value();
            if (cauldronCanBrew && !cauldronBlockEntity.reagent.isEmpty()) {
                var maxProgress = cauldronBlockEntity.maxProgress;
                if (cauldronHeated) {
                    maxProgress = (int) (maxProgress*CauldronMod.CONFIG.heatAmplification.value());
                }
                if (cauldronBlockEntity.progress > maxProgress) {
                    cauldronBlockEntity.brew(cauldronHeated);
                    cauldronBlockEntity.progress = 0;
                } else {
                    cauldronBlockEntity.progress++;
                    if (!blockState.getValue(BREWING))
                        level.setBlockAndUpdate(pos, blockState.setValue(BREWING, true));
                }
            }
            //reset to vanilla
            var newState = level.getBlockState(pos);
            if (newState.is(CauldronModBlocks.BREWING_CAULDRON.get())) {
                if (cauldronBlockEntity.reagent.isEmpty()) {
                    if (cauldronBlockEntity.getFillLevel().equals(0)) {
                        newState = Blocks.CAULDRON.defaultBlockState();
                    } else if (blockState.getValue(BREWING)) {
                        newState = newState.trySetValue(BrewingCauldronBlock.BREWING, false);
                    }
                }
                if (newState.getOptionalValue(POTION_QUANTITY).orElse(0).equals(0)) {
                    cauldronBlockEntity.potion = CauldronContents.EMPTY;
                    cauldronBlockEntity.splashing = false;
                    cauldronBlockEntity.lingering = false;
                }
                newState = newState.trySetValue(HEATED, cauldronHeated).trySetValue(CONTENTS, cauldronBlockEntity.getContentsProperty());
            }
            if (newState != blockState) {
                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    private Contents getContentsProperty() {
        if (getContents().is(Potions.WATER)) return Contents.WATER;
        else if (getContents().potion().isPresent()) return Contents.POTION;
        else if (getContents().is("honey")) return Contents.HONEY;
        else if (getContents().is("lava")) return Contents.LAVA;
        return Contents.EMPTY;
    }

    private boolean isBubbling() {
        return bubbleTimer > 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[1];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return reagent.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return reagent;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return reagent.copyAndClear();
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return reagent.copyAndClear();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        reagent = stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        reagent.copyAndClear();
    }
}
