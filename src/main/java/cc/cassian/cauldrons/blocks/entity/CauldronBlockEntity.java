package cc.cassian.cauldrons.blocks.entity;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class CauldronBlockEntity extends BlockEntity {

    protected PotionContents potion = PotionContents.EMPTY;
    private int progress;
    private int maxProgress;
    private int bubbleTimer = 0;

    private ItemStack itemHandler = ItemStack.EMPTY;

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
        } else if (itemStack.has(DataComponents.POTION_CONTENTS) && potionQuantity < 3) {
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
            this.potion = null;
            return new Pair<>(ItemInteractionResult.SUCCESS, returnStack);
        // drain with bottle
        } else if (itemStack.is(Items.GLASS_BOTTLE) && potionQuantity>=1) {
            setFillLevel(potionQuantity-1);
            var stack = createItemStack(Items.POTION, potion);
            if (getFillLevel() == 0)
                this.potion = null;
            return new Pair<>(ItemInteractionResult.SUCCESS, stack);
        // drain with arrow
        } else if (itemStack.is(Items.ARROW) && potionQuantity>=1) {
            setFillLevel(potionQuantity-1);
            var stack = createItemStack(Items.TIPPED_ARROW, potion);
            if (getFillLevel() == 0)
                this.potion = null;
            return new Pair<>(ItemInteractionResult.SUCCESS, stack);
        }
        // insert as inventory
        else if (itemHandler.isEmpty()) {
            itemHandler = itemStack;
            if (getFillLevel()>0 && this.getLevel().isClientSide()) {
                for (int i = 0; i < 20.0F; i++) {
                    Random random = new Random();
                    double d = (random.nextDouble() * 2.0 - 0.5);
                    double e = (random.nextDouble() * 2.0 - 0.5);
                    this.getLevel().addParticle(ParticleTypes.SPLASH, this.getBlockPos().getX() + d, this.getBlockPos().getY() + 0.5F, this.getBlockPos().getZ() + e, 0.05, 0.25, 0.05);
                }
            }
            return new Pair<>(ItemInteractionResult.SUCCESS, ItemStack.EMPTY);
        }
        return new Pair<>(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION, ItemStack.EMPTY);
    }

    public void brew() {
        var potionBrewing = this.level.potionBrewing();
        var potionItem = createItemStack(Items.POTION, potion);
        if (!itemHandler.isEmpty() && potionBrewing.hasMix(potionItem, itemHandler)) {
            ItemStack mix = potionBrewing.mix(itemHandler, potionItem);
            this.potion = mix.getComponents().get(DataComponents.POTION_CONTENTS);
            this.itemHandler = ItemStack.EMPTY;
            //level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, this.getBlockPos(), 0);
            level.playSound(null, getBlockPos(), CauldronSoundEvents.BREWS.get(), SoundSource.BLOCKS);
            level.setBlockAndUpdate(getBlockPos(), this.getBlockState().setValue(BrewingCauldronBlock.MAGIC, !this.getBlockState().getValue(BrewingCauldronBlock.MAGIC)));
            bubbleTimer = 20;
        }
    }

    public ItemStack retrieve() {
        return itemHandler.copyAndClear();
    }

    public ItemStack getItem() {
        return itemHandler;
    }

    public Integer getFillLevel() {
        Integer value = this.getBlockState().getValue(BrewingCauldronBlock.POTION_QUANTITY);
        if (value == 0)
            this.potion = PotionContents.EMPTY;
        return value;
    }

    public void setFillLevel(int value) {
        if (value == 0)
            this.potion = PotionContents.EMPTY;
        BrewingCauldronBlock.setFillLevel(this.getBlockState(), this.getLevel(), this.getBlockPos(), value);
    }

    public int getPotionColour() {
        return potion.getColor();
    }

    @Nullable
    public Holder<Potion> getPotion() {
        if (potion.potion().isPresent())
            return potion.potion().get();
        else return null;
    }

    public static ItemStack createItemStack(Item item, PotionContents potion) {
        ItemStack itemStack = new ItemStack(item);
        itemStack.set(DataComponents.POTION_CONTENTS, potion);
        return itemStack;
    }

    public boolean isPotionWater() {
        if (potion.potion().isPresent())
            return Objects.equals(potion.potion().get(), Potions.WATER);
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof CauldronBlockEntity cauldronBlockEntity) {
            if (cauldronBlockEntity.isBubbling()) {
                level.addParticle(ParticleTypes.BUBBLE, pos.getX() + level.random.nextDouble(), pos.getY() + 1, pos.getZ() + level.random.nextDouble(), 0.01, 0.05, 0.01);
                cauldronBlockEntity.bubbleTimer--;
            }
            cauldronBlockEntity.brew();
        }
    }

    private boolean isBubbling() {
        return bubbleTimer > 0;
    }
}
