package cc.cassian.cauldrons.blocks.entity;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.core.CauldronModTags;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import cc.cassian.cauldrons.recipe.DippingRecipe;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import cc.cassian.cauldrons.registry.CauldronModSoundEvents;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.*;

import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.*;

public class CauldronBlockEntity extends BlockEntity implements WorldlyContainer {

    protected PotionContents potion = PotionContents.EMPTY;
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
        super(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY.get(), blockPos, blockState);

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Tag inventory = tag.get("cauldron.inventory");
        if (inventory != null)
            reagent = ItemStack.parse(registries, inventory).orElse(ItemStack.EMPTY);
        else reagent = ItemStack.EMPTY;
        progress = tag.getInt("cauldron.progress");
        maxProgress = tag.getInt("cauldron.max_progress");
        potion = PotionContents.CODEC.decode(NbtOps.INSTANCE, tag.get("cauldron.potion")).result().get().getFirst();
        splashing = tag.getBoolean("cauldron.splashing");
        lingering = tag.getBoolean("cauldron.lingering");
        bubbleTimer = tag.getInt("cauldron.bubble_timer");
        if (tag.contains("cauldron.particle_type"))
            particleType = ParticleTypes.CODEC.decode(NbtOps.INSTANCE, tag.get("cauldron.particle_type")).result().get().getFirst();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (!reagent.isEmpty())
            tag.put("cauldron.inventory", reagent.save(registries));
        tag.putInt("cauldron.progress", progress);
        tag.putInt("cauldron.max_progress", maxProgress);
        tag.put("cauldron.potion", PotionContents.CODEC.encodeStart(NbtOps.INSTANCE, potion).result().get());
        tag.putBoolean("cauldron.splashing", splashing);
        tag.putBoolean("cauldron.lingering", lingering);
        tag.putInt("cauldron.bubble_timer", bubbleTimer);
        tag.put("cauldron.particle_type", ParticleTypes.CODEC.encodeStart(NbtOps.INSTANCE, particleType).result().get());
        super.saveAdditional(tag, registries);
    }

    public Pair<ItemInteractionResult, ItemStack> insert(ItemStack itemStack) {
        var potionQuantity = getFillLevel();
        // fill with water bucket
        if (itemStack.is(Items.WATER_BUCKET) && potionQuantity == 0) {
            setFillLevel(3);
            this.potion = new PotionContents(Potions.WATER);
            return new Pair<>(ItemInteractionResult.SUCCESS, Items.BUCKET.getDefaultInstance());
        // fill with potion
        } else if (itemStack.has(DataComponents.POTION_CONTENTS) && potionQuantity < 3 && !itemStack.is(CauldronModTags.CANNOT_FILL_CAULDRON)) {
            PotionContents insertedPotion = itemStack.get(DataComponents.POTION_CONTENTS);
            assert insertedPotion != null;
            Optional<Holder<Potion>> currentPotion = this.potion.potion();
            if (currentPotion.isEmpty()) {
                this.potion = insertedPotion;
                setFillLevel(1);
                return new Pair<>(ItemInteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance());
            }
            else if (insertedPotion.is(currentPotion.get())) {
                setFillLevel(potionQuantity+1);
                return new Pair<>(ItemInteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance());
            }
        // drain with bucket
        } else if (itemStack.is(Items.BUCKET) && potionQuantity>=1) {
            ItemStack returnStack;
            if (isPotionWater()) returnStack = Items.WATER_BUCKET.getDefaultInstance();
            else returnStack = Items.BUCKET.getDefaultInstance();
            setFillLevel(0);
            return new Pair<>(ItemInteractionResult.SUCCESS, returnStack);
        // drain with bottle
        } else if (itemStack.is(Items.GLASS_BOTTLE) && potionQuantity>=1) {
            var potionItem = Items.POTION;
            if (splashing) potionItem = Items.SPLASH_POTION;
            else if (lingering) potionItem = Items.LINGERING_POTION;
            ItemStack stack = createItemStack(potionItem, potion);
            setFillLevel(potionQuantity-1);
            return new Pair<>(ItemInteractionResult.SUCCESS, stack);
        // drain with arrow
        } else if (itemStack.is(Items.ARROW) && potionQuantity>=1) {
            var stack = createItemStack(Items.TIPPED_ARROW, potion);
            setFillLevel(potionQuantity-1);
            return new Pair<>(ItemInteractionResult.SUCCESS, stack);
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
            return new Pair<>(ItemInteractionResult.SUCCESS, ItemStack.EMPTY);
        }
        return new Pair<>(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION, ItemStack.EMPTY);
    }

    public void brew(boolean cauldronHeated) {
        if (potion.potion().isEmpty() || reagent.isEmpty()) return;
        var input = new BrewingRecipeInput(reagent, potion, cauldronHeated);
        if (!level.isClientSide()) {
            Optional<RecipeHolder<BrewingRecipe>> brewingRecipe = level.getRecipeManager().getRecipeFor(CauldronModRecipes.BREWING.get(), input, level);
            if (brewingRecipe.isPresent()) {
                this.potion = brewingRecipe.get().value().getResultPotion(level.registryAccess());
                updateAfterBrewing(ItemStack.EMPTY, brewingRecipe.get().value().getParticleType());
            }
            Optional<RecipeHolder<DippingRecipe>> dippingRecipe = level.getRecipeManager().getRecipeFor(CauldronModRecipes.DIPPING.get(), input, level);
            if (dippingRecipe.isPresent()) {
                updateAfterBrewing(dippingRecipe.get().value().getResultItem(level.registryAccess()), dippingRecipe.get().value().getParticleType());
                setFillLevel(0);
            }
            else if (reagent.is(CauldronModTags.CREATES_SPLASH_POTIONS)) {
                this.splashing = true;
                this.lingering = false;
                updateAfterBrewing(ItemStack.EMPTY, ParticleTypes.SMOKE);
            }
            else if (reagent.is(CauldronModTags.CREATES_LINGERING_POTIONS)) {
                this.splashing = false;
                this.lingering = true;
                updateAfterBrewing(ItemStack.EMPTY, ParticleTypes.DRAGON_BREATH);
            }
            else if (CauldronMod.CONFIG.useBrewingStandRecipes.value()) {
                var potionBrewing = this.level.potionBrewing();
                var potionItem = createItemStack(Items.POTION, potion);
                if (potionBrewing.hasMix(potionItem, reagent)) {
                    ItemStack mix = potionBrewing.mix(reagent, potionItem);
                    this.potion = mix.getComponents().get(DataComponents.POTION_CONTENTS);
                    updateAfterBrewing(ItemStack.EMPTY, ParticleTypes.BUBBLE);
                }
            }
        }
    }

    private void updateAfterBrewing(ItemStack stack, ParticleOptions particleType) {
        this.reagent = stack;
        //level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, this.getBlockPos(), 0);
        level.playSound(null, getBlockPos(), CauldronModSoundEvents.BREWS.get(), SoundSource.BLOCKS);
        level.setBlockAndUpdate(getBlockPos(), this.getBlockState().setValue(BrewingCauldronBlock.BREWING, false));
        bubbleTimer = 20;
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
        return potion.getColor();
    }

    public PotionContents getPotion() {
        return potion;
    }

    public static ItemStack createItemStack(Item item, PotionContents potion) {
        if (potion.potion().isPresent()) {
            return PotionContents.createItemStack(item, potion.potion().get());
        }
        var stack = item.getDefaultInstance();
        stack.set(DataComponents.POTION_CONTENTS, potion);
        return stack;
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
            var newState = blockState;
            // particle logic
            if (cauldronBlockEntity.isBubbling()) {
                double d = pos.getX() + level.random.nextDouble();
                double e = pos.getY() + 1;
                double f = pos.getZ() + level.random.nextDouble();
                if (cauldronBlockEntity.getPotion() != PotionContents.EMPTY) {
                    ArrayList<MobEffectInstance> effects = new ArrayList<>();
                    cauldronBlockEntity.getPotion().getAllEffects().forEach(effects::add);
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
                        newState = newState.setValue(BREWING, true);
                }
            }
            //reset to vanilla
            if (cauldronBlockEntity.reagent.isEmpty()) {
                if (cauldronBlockEntity.getFillLevel().equals(0)) {
                    newState = Blocks.CAULDRON.defaultBlockState();
                } else if (blockState.getValue(BREWING)) {
                    newState = newState.setValue(BrewingCauldronBlock.BREWING, false);
                }
            }
            if (newState.is(CauldronModBlocks.BREWING_CAULDRON.get())) {
                if (newState.getValue(POTION_QUANTITY).equals(0)) {
                    cauldronBlockEntity.potion = PotionContents.EMPTY;
                    cauldronBlockEntity.splashing = false;
                    cauldronBlockEntity.lingering = false;
                }
                newState = newState.trySetValue(HEATED, cauldronHeated).trySetValue(HAS_POTION, cauldronBlockEntity.hasPotion());
            }
            if (newState != blockState) {
                level.setBlockAndUpdate(pos, newState);
            }
        }
    }

    private boolean hasPotion() {
        return !(potion == PotionContents.EMPTY || isPotionWater());
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
