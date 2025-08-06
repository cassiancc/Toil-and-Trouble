package cc.cassian.cauldrons.blocks.entity;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.registry.CauldronBlockEntityTypes;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.Objects;

public class CauldronBlockEntity extends BlockEntity {

    protected PotionContents potion = PotionContents.EMPTY;
    private int progress;
    private int maxProgress;

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
        if (potion.potion().isPresent() && potion.potion().orElseThrow().unwrapKey().isPresent()) {
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
            return new Pair<>(ItemInteractionResult.CONSUME, Items.BUCKET.getDefaultInstance());
        // fill with potion
        } else if (itemStack.has(DataComponents.POTION_CONTENTS) && potionQuantity < 3) {
            PotionContents potionContents = itemStack.get(DataComponents.POTION_CONTENTS);
            assert potionContents != null;
            if (potionContents == this.potion) {
                setFillLevel(potionQuantity+1);
                return new Pair<>(ItemInteractionResult.CONSUME, Items.GLASS_BOTTLE.getDefaultInstance());
            } else if (this.potion == null) {
                this.potion = potionContents;
                setFillLevel(1);
                return new Pair<>(ItemInteractionResult.CONSUME, Items.GLASS_BOTTLE.getDefaultInstance());
            }
        // drain with bucket
        } else if (itemStack.is(Items.BUCKET) && potionQuantity>=1) {
            ItemStack returnStack;
            if (isPotionWater()) returnStack = Items.WATER_BUCKET.getDefaultInstance();
            else returnStack = Items.BUCKET.getDefaultInstance();
            setFillLevel(0);
            this.potion = null;
            return new Pair<>(ItemInteractionResult.CONSUME, returnStack);
        // drain with bottle
        } else if (itemStack.is(Items.GLASS_BOTTLE) && potionQuantity>=1) {
            setFillLevel(potionQuantity-1);
            var stack = createItemStack(Items.POTION, potion);
            if (getFillLevel() == 0)
                this.potion = null;
            return new Pair<>(ItemInteractionResult.CONSUME, stack);
        // drain with arrow
        } else if (itemStack.is(Items.ARROW) && potionQuantity>=1) {
            setFillLevel(potionQuantity-1);
            var stack = createItemStack(Items.TIPPED_ARROW, potion);
            if (getFillLevel() == 0)
                this.potion = null;
            return new Pair<>(ItemInteractionResult.CONSUME, stack);
        }
        // insert as inventory
        else if (itemHandler.isEmpty()) {
            itemHandler = itemStack;
            return new Pair<>(ItemInteractionResult.CONSUME, ItemStack.EMPTY);
        }
        return new Pair<>(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION, ItemStack.EMPTY);
    }

    public ItemStack retrieve() {
        return itemHandler.copyAndClear();
    }

    public ItemStack getItem() {
        return itemHandler;
    }

    public Integer getFillLevel() {
        return this.getBlockState().getValue(BrewingCauldronBlock.POTION_QUANTITY);
    }

    public void setFillLevel(int i) {
        BrewingCauldronBlock.setFillLevel(this.getBlockState(), this.getLevel(), this.getBlockPos(), i);
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
}
