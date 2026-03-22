package cc.cassian.cauldrons.blocks.entity;

import cc.cassian.cauldrons.CauldronMod;
import cc.cassian.cauldrons.Platform;
import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import cc.cassian.cauldrons.core.CauldronContents;
import cc.cassian.cauldrons.core.CauldronModEvents;
import cc.cassian.cauldrons.core.CauldronModRecipes;
import cc.cassian.cauldrons.core.CauldronModTags;
import cc.cassian.cauldrons.recipe.BrewingRecipe;
import cc.cassian.cauldrons.recipe.BrewingRecipeInput;
import cc.cassian.cauldrons.recipe.AlchemyRecipe;
import cc.cassian.cauldrons.registry.CauldronModBlockEntityTypes;
import cc.cassian.cauldrons.registry.CauldronModBlocks;
import cc.cassian.cauldrons.registry.CauldronModSoundEvents;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
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
import org.jspecify.annotations.NullMarked;
import oshi.util.tuples.Pair;

import java.util.*;

import static cc.cassian.cauldrons.blocks.BrewingCauldronBlock.*;

@NullMarked
public class CauldronBlockEntity extends BlockEntity implements WorldlyContainer {

    protected CauldronContents contents = CauldronContents.EMPTY;
    protected boolean splashing = false;
    protected boolean lingering = false;
    private int progress;
    private int maxProgress = CauldronMod.CONFIG.brewingTime.value() * 20;
    private int bubbleTimer = 0;
    private boolean pop = false;

    private final ArrayList<ItemStack> items = new ArrayList<>(9);
    private ParticleOptions particleType = ParticleTypes.BUBBLE;

    public CauldronBlockEntity(BlockPos pos, BlockState state, CauldronContents contents) {
        super(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY, pos, state);
        this.contents = contents;
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
        super(CauldronModBlockEntityTypes.CAULDRON_BLOCK_ENTITY, blockPos, blockState);

    }

    @Override
    public void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        Optional<List<ItemStack>> inventory = tag.read("cauldron.inventory", ItemStack.CODEC.listOf());
        items.clear();
		inventory.ifPresent(items::addAll);
        progress = tag.getIntOr("cauldron.progress", 0);
        maxProgress = tag.getIntOr("cauldron.max_progress", 0);
        contents = tag.read("cauldron.potion", CauldronContents.CODEC).orElse(CauldronContents.EMPTY);
        splashing = tag.getBooleanOr("cauldron.splashing", false);
        lingering = tag.getBooleanOr("cauldron.lingering", false);
        bubbleTimer = tag.getIntOr("cauldron.bubble_timer", 0);
        particleType = tag.read("cauldron.particle_type", ParticleTypes.CODEC).orElse(ParticleTypes.BUBBLE);
    }

    @Override
    public void saveAdditional(ValueOutput tag) {
        if (!items.isEmpty()) {
            items.removeIf(ItemStack::isEmpty);
            tag.store("cauldron.inventory", ItemStack.CODEC.listOf(), items);
        } else {
            tag.store("cauldron.inventory", ItemStack.CODEC.listOf(), List.of());
        }
        tag.putInt("cauldron.progress", progress);
        tag.putInt("cauldron.max_progress", maxProgress);
        tag.store("cauldron.potion", CauldronContents.CODEC, contents);
        tag.putBoolean("cauldron.splashing", splashing);
        tag.putBoolean("cauldron.lingering", lingering);
        tag.putInt("cauldron.bubble_timer", bubbleTimer);
        tag.store("cauldron.particle_type", ParticleTypes.CODEC, particleType);
        super.saveAdditional(tag);
    }

    @Deprecated
    public Pair<InteractionResult, ItemStack> insert(ItemStack itemStack) {
        int potionQuantity = getFillLevel();
        // fill with potion
        if (itemStack.has(DataComponents.POTION_CONTENTS) && (contents.isPotion() || contents == CauldronContents.EMPTY) && potionQuantity < 3 && !itemStack.is(CauldronModTags.CANNOT_FILL_CAULDRON)) {
            PotionContents insertedPotion = itemStack.get(DataComponents.POTION_CONTENTS);
            assert insertedPotion != null;
            Optional<Holder<Potion>> currentPotion = this.contents.potion();
            if (currentPotion.isEmpty()) {
                this.contents = new CauldronContents(insertedPotion);
                setFillLevel(1);
                return new Pair<>(InteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance());
            }
            else if (insertedPotion.is(currentPotion.get())) {
                setFillLevel(potionQuantity+1);
                return new Pair<>(InteractionResult.SUCCESS, Items.GLASS_BOTTLE.getDefaultInstance());
            }
        }
        // insert as inventory
        if (hasSpace()) {
            items.add(itemStack);
            setChanged();
			if (level != null && getFillLevel() > 0 && level.isClientSide()) {
				var particle = ParticleTypes.SPLASH;
				if (this.contents.is("honey")) particle = ParticleTypes.LANDING_HONEY;
				else if (this.contents.is("lava")) particle = ParticleTypes.LANDING_LAVA;
				for (int i = 0; i < 20; i++) {
					Random random = new Random();
					double d = (random.nextDouble());
					double e = (random.nextDouble());
                    BlockPos blockPos = this.getBlockPos();
                    level.addParticle(particle, blockPos.getX() + d, blockPos.getY() + 1F, blockPos.getZ() + e, 0.05, 0.25, 0.05);
				}
				progress = 0;
			}
			return new Pair<>(InteractionResult.SUCCESS, ItemStack.EMPTY);
        }
        return new Pair<>(CauldronModEvents.PASS_TO_EMPTY_HAND, ItemStack.EMPTY);
    }

    public void brew(boolean cauldronHeated) {
        var input = new BrewingRecipeInput(items, contents, cauldronHeated);
        if (level == null) return;
		Optional<RecipeHolder<BrewingRecipe>> brewingRecipe = Platform.getFirstRecipe(CauldronModRecipes.BREWING, input, level);
		if (brewingRecipe.isPresent()) {
			this.contents = brewingRecipe.get().value().getResultPotion();
			updateAfterBrewing(ItemStack.EMPTY, this.contents, brewingRecipe.get().value().getParticleType());
		}
		Optional<RecipeHolder<AlchemyRecipe>> dippingRecipe = Platform.getFirstRecipe(CauldronModRecipes.ALCHEMY, input, level);
		if (dippingRecipe.isPresent()) {
			updateAfterBrewing(dippingRecipe.get().value().getResultItem(), this.contents, dippingRecipe.get().value().getParticleType());
			setFillLevel(0);
		}
		else if (items.size() == 1) {
			if (items.getFirst().is(CauldronModTags.CREATES_SPLASH_POTIONS) && this.contents.isPotion()) {
				this.splashing = true;
				this.lingering = false;
				updateAfterBrewing(ItemStack.EMPTY, this.contents, ParticleTypes.SMOKE);
			}
			else if (items.getFirst().is(CauldronModTags.CREATES_LINGERING_POTIONS) && this.contents.isPotion()) {
				this.splashing = false;
				this.lingering = true;
				updateAfterBrewing(ItemStack.EMPTY, this.contents, PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1));
			}
			else if (CauldronMod.CONFIG.useBrewingStandRecipes.value()) {
				var potionBrewing = this.level.potionBrewing();
				var potionItem = CauldronContents.createItemStack(Items.POTION, contents);
				if (potionBrewing.hasMix(potionItem, items.getFirst())) {
					ItemStack mix = potionBrewing.mix(items.getFirst(), potionItem);
					this.contents = new CauldronContents(Objects.requireNonNullElse(mix.getComponents().get(DataComponents.POTION_CONTENTS), PotionContents.EMPTY));
					updateAfterBrewing(ItemStack.EMPTY, this.contents, ParticleTypes.BUBBLE);
				}
			}
		}

	}

    private void updateAfterBrewing(ItemStack stack, CauldronContents contents, ParticleOptions particleType) {
        updateAfterBrewing(List.of(stack), contents, particleType);
    }

    private void updateAfterBrewing(List<ItemStack> stack, CauldronContents contents, ParticleOptions particleType) {
        this.items.clear();
        this.items.addAll(stack);
        if (level == null) return;
        //level.levelEvent(LevelEvent.SOUND_BREWING_STAND_BREW, this.getBlockPos(), 0);
        this.level.playSound(null, getBlockPos(), CauldronModSoundEvents.BREWS, SoundSource.BLOCKS);
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
        return items.removeFirst();
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public Integer getFillLevel() {
		return this.getBlockState().getValue(POTION_QUANTITY);
    }

    public void setFillLevel(int value) {
        BrewingCauldronBlock.setFillLevel(this.getBlockState(), this.getLevel(), this.getBlockPos(), value);
    }

    public int getPotionColour() {
       return contents.getColor();
    }

    public CauldronContents getContents() {
        return contents;
    }

    public Component getContentsName() {
        if (contents.customName().isPresent()) {
            return Component.literal(contents.customName().get());
        }
        else if (contents.isPotion()) {
            return contents.toPotionContents().getName("item.minecraft.potion.effect.");
        } else {
            return Component.translatable(contents.id().toLanguageKey("cauldron"));
        }
	}

    public void setContents(CauldronContents contents) {
        this.contents = contents;
    }

    public boolean isPotionWater() {
        if (contents.potion().isPresent())
            return Objects.equals(contents.potion().get(), Potions.WATER);
        return false;
    }

    public boolean isPotionSplash() {
        if (contents.isPotion())
            return splashing;
        return false;
    }

    public boolean isPotionLingering() {
        if (contents.isPotion())
            return lingering;
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, BlockEntity blockEntity) {
        if (blockEntity instanceof CauldronBlockEntity cauldronBlockEntity) {
            // particle logic
            if (cauldronBlockEntity.isBubbling()) {
                double d = pos.getX() + level.getRandom().nextDouble();
                double e = pos.getY() + 1;
                double f = pos.getZ() + level.getRandom().nextDouble();
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
            if (cauldronCanBrew && !cauldronBlockEntity.items.isEmpty()) {
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
            if (newState.is(CauldronModBlocks.BREWING_CAULDRON)) {
                if (cauldronBlockEntity.items.isEmpty()) {
                    if (cauldronBlockEntity.getFillLevel().equals(0)) {
                        newState = Blocks.CAULDRON.defaultBlockState();
                    } else if (blockState.getValue(BREWING)) {
                        newState = newState.trySetValue(BrewingCauldronBlock.BREWING, false);
                    }
                }
                if (newState.getOptionalValue(POTION_QUANTITY).orElse(0).equals(0)) {
                    cauldronBlockEntity.contents = CauldronContents.EMPTY;
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
        else if (getContents().is("milk")) return Contents.MILK;
        else if (getContents().is(Identifier.fromNamespaceAndPath("chorus_honey", "chorus_honey"))) return Contents.CHORUS_HONEY;
        else if (getContents().is("lava")) return Contents.LAVA;
        else if (getContents().is(Identifier.withDefaultNamespace("air"))) return Contents.EMPTY;
        return Contents.POTION;
    }

    private boolean isBubbling() {
        return bubbleTimer > 0;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[9];
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
        return 9;
    }

    /**
     * Checks if there are no items in this Cauldron.
     */
    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Checks if there is space to put items in this Cauldron.
     */
    public boolean hasSpace() {
        return items.size() < getContainerSize();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (items.size()>slot)
            return items.get(slot);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return items.remove(slot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return items.remove(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
    }
}
