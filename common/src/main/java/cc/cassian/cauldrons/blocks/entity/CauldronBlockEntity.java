package cc.cassian.cauldrons.blocks.entity;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.core.CauldronModTags;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronSoundEvents;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.BREWING;
import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.POTION_QUANTITY;

public class CauldronBlockEntity extends BlockEntity {

    protected PotionContents potion = PotionContents.EMPTY;
    protected boolean splashing = false;
    protected boolean lingering = false;
    private int progress;
    private int maxProgress = CauldronMod.CONFIG.brewingTime;
    private int bubbleTimer = 0;

    private ItemStack itemHandler = ItemStack.EMPTY;

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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Tag inventory = tag.get("cauldron.inventory");
        if (inventory != null)
            itemHandler = ItemStack.parse(registries, inventory).orElse(ItemStack.EMPTY);
        else itemHandler = ItemStack.EMPTY;
        progress = tag.getInt("cauldron.progress");
        maxProgress = tag.getInt("cauldron.max_progress");
        var p = tag.getString("cauldron.potion");
        if (!p.equals("minecraft:air")) {
            potion = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(p)).map(PotionContents::new).orElse(PotionContents.EMPTY);
        } else {
            potion = PotionContents.EMPTY;
        }
        splashing = tag.getBoolean("cauldron.splashing");
        lingering = tag.getBoolean("cauldron.lingering");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (!itemHandler.isEmpty())
            tag.put("cauldron.inventory", itemHandler.save(registries));
        tag.putInt("cauldron.progress", progress);
        tag.putInt("cauldron.max_progress", maxProgress);
        if (this.potion != null && potion.potion().isPresent() && potion.potion().orElseThrow().unwrapKey().isPresent()) {
            tag.putString("cauldron.potion", potion.potion().orElseThrow().unwrapKey().orElseThrow().location().toString());
        } else {
            tag.putString("cauldron.potion", "minecraft:air");
        }
        tag.putBoolean("cauldron.splashing", splashing);
        tag.putBoolean("cauldron.lingering", lingering);
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
        else if (itemHandler.isEmpty()) {
            itemHandler = itemStack;
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

    public void brew() {
        var potionBrewing = this.level.potionBrewing();
        if (potion.potion().isEmpty()) return;
        var potionItem = createItemStack(Items.POTION, potion);
        if (!itemHandler.isEmpty()) {
            if (itemHandler.is(CauldronModTags.CREATES_SPLASH_POTIONS)) {
                this.splashing = true;
                this.lingering = false;
                updateAfterBrewing();
            }
            else if (itemHandler.is(CauldronModTags.CREATES_LINGERING_POTIONS)) {
                this.splashing = false;
                this.lingering = true;
                updateAfterBrewing();
            }
            else if (potionBrewing.hasMix(potionItem, itemHandler)) {
                ItemStack mix = potionBrewing.mix(itemHandler, potionItem);
                this.potion = mix.getComponents().get(DataComponents.POTION_CONTENTS);
                updateAfterBrewing();
            }
        }
    }

    private void updateAfterBrewing() {
        this.itemHandler = ItemStack.EMPTY;
        //level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, this.getBlockPos(), 0);
        level.playSound(null, getBlockPos(), CauldronSoundEvents.BREWS.get(), SoundSource.BLOCKS);
        level.setBlockAndUpdate(getBlockPos(), this.getBlockState().setValue(BrewingCauldronBlock.BREWING, false));
        bubbleTimer = 20;
    }

    public ItemStack retrieve() {
        return itemHandler.copyAndClear();
    }

    public ItemStack getItem() {
        return itemHandler;
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
                if (level.random.nextBoolean()) {
                    level.addParticle(ParticleTypes.BUBBLE_POP, d, e, f, 0.01, 0.05, 0.01);
                } else {
                    level.addParticle(ParticleTypes.BUBBLE, d, e, f, 0.01, 0.1, 0.01);
                }
                cauldronBlockEntity.bubbleTimer--;
            }
            // brewing
            if ((level.getBlockState(pos.below()).is(CauldronModTags.HEATS_CAULDRON) || !CauldronMod.CONFIG.requiresHeat) && !cauldronBlockEntity.itemHandler.isEmpty()) {
                if (cauldronBlockEntity.progress > cauldronBlockEntity.maxProgress) {
                    cauldronBlockEntity.brew();
                    cauldronBlockEntity.progress = 0;
                } else {
                    cauldronBlockEntity.progress++;
                    if (!blockState.getValue(BREWING))
                        level.setBlockAndUpdate(pos, blockState.setValue(BREWING, true));
                }
            }
            //reset to vanilla
            if (cauldronBlockEntity.itemHandler.isEmpty()) {
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
