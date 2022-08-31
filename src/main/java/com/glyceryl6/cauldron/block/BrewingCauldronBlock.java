package com.glyceryl6.cauldron.block;

import com.glyceryl6.cauldron.RegistryContents;
import com.glyceryl6.cauldron.potion.PotionHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked"})
public class BrewingCauldronBlock extends BaseEntityBlock {

    private static final HashMap<Object, Object> b = new HashMap<>();
    public static final BooleanProperty POTION = BooleanProperty.create("potion");
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
    private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(
            box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D),
            box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D),
            box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE),
            BooleanOp.ONLY_FIRST);

    public BrewingCauldronBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LEVEL, 1)
                .setValue(POTION, false));
    }

    public List b_(BrewingCauldronEntity brewingCauldron) {
        int i = brewingCauldron.getLiquidData();
        List list = (List) b.get(i);
        if (list == null) {
            list = PotionHelper.getPotionEffects(i);
            b.put(i, list);
        }
        return list;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrewingCauldronEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide && oldState.getBlock() != newState.getBlock()) {
            level.removeBlockEntity(pos);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return INSIDE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        int i = state.getValue(LEVEL);
        float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
        BlockEntity tileEntity = level.getBlockEntity(pos);
        BrewingCauldronEntity brewingCauldronEntity = (BrewingCauldronEntity)tileEntity;
        if (entity instanceof ItemEntity entityItem) {
            ItemStack itemStack = entityItem.getItem();
            if (brewingCauldronEntity != null && brewingCauldronEntity.applyIngredient(itemStack)) {
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                this.updateBlockState(level, pos, state, state.getValue(LEVEL), true);
                level.sendBlockUpdated(pos, state, state, 3);
                itemStack.shrink(1);
            }
        }
        if (!level.isClientSide && i > 0 && entity.getBoundingBox().minY <= (double)f) {
            if (entity.isOnFire()) {
                entity.clearFire();
            }
            if (brewingCauldronEntity != null && !brewingCauldronEntity.isCauldronDataZero()) {
                if (entity instanceof LivingEntity livingEntity) {
                    List<MobEffectInstance> potionEffects = this.b_(brewingCauldronEntity);
                    for (MobEffectInstance instance : potionEffects) {
                        livingEntity.addEffect(new MobEffectInstance(instance));
                    }
                }
            }
        }
    }

    public void updateBlockState(Level world, BlockPos pos, BlockState state, int level, boolean hasPotion) {
        BlockState newState = state.setValue(LEVEL, Mth.clamp(level, 0, 3)).setValue(POTION, hasPotion);
        world.setBlock(pos, newState, 3);
        world.sendBlockUpdated(pos, state, state, 3);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) level.sendBlockUpdated(pos, state, state, 3);
        ItemStack heldItemStack = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BrewingCauldronEntity brewingCauldronEntity)) {
            return InteractionResult.CONSUME;
        }
        int cauldronMetadata = brewingCauldronEntity.getLiquidData();
        if (heldItemStack.getItem() == Items.WATER_BUCKET) {
            if (brewingCauldronEntity.fillCauldronWithWaterBucket()) {
                if (!player.getAbilities().instabuild) {
                    heldItemStack.shrink(1);
                    player.getInventory().add(new ItemStack(Items.BUCKET));
                }
                if (!brewingCauldronEntity.isCauldronDataZero() && cauldronMetadata != brewingCauldronEntity.getLiquidData()) {
                    level.sendBlockUpdated(pos, state, state, 3);
                }
                this.updateBlockState(level, pos, state, 3, brewingCauldronEntity.getLiquidData() != 0);
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }
        if (heldItemStack.getItem() == Items.GLASS_BOTTLE ||
                heldItemStack.getItem() == RegistryContents.SPLASH_GLASS_BOTTLE.get() ||
                heldItemStack.getItem() == RegistryContents.LINGERING_GLASS_BOTTLE.get()) {
            if (!brewingCauldronEntity.isCauldronEmpty()) {
                Item potionItem;
                if (heldItemStack.getItem() == Items.GLASS_BOTTLE) {
                    potionItem = RegistryContents.POTION_ITEM.get();
                } else if (heldItemStack.getItem() == RegistryContents.SPLASH_GLASS_BOTTLE.get()) {
                    potionItem = RegistryContents.SPLASH_POTION_ITEM.get();
                } else {
                    potionItem = RegistryContents.LINGERING_POTION_ITEM.get();
                }
                ItemStack potionItemStack = new ItemStack(potionItem, 1);
                potionItemStack.setDamageValue(brewingCauldronEntity.getLiquidData());
                CompoundTag compoundTag = potionItemStack.getOrCreateTag();
                compoundTag.putBoolean("Unbreakable", true);
                compoundTag.putInt("HideFlags", 4);
                compoundTag.putInt("Color", Mth.abs(brewingCauldronEntity.getLiquidColor()));
                potionItemStack.setTag(compoundTag);
                if (!player.getInventory().add(potionItemStack)) {
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, potionItemStack));
                }
                if (!player.getAbilities().instabuild) {
                    heldItemStack.shrink(1);
                    brewingCauldronEntity.decrementLiquidLevel();
                    this.updateBlockState(level, pos, state, state.getValue(LEVEL) - 1, brewingCauldronEntity.getLiquidData() != 0);
                }
                if (heldItemStack.getCount() <= 0) {
                    player.getInventory().add(player.getInventory().selected, ItemStack.EMPTY);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else if (heldItemStack.getItem() == Items.ARROW) {
            int arrowCount;
            ItemStack potionArrowStack = new ItemStack(RegistryContents.TIPPED_ARROW_ITEM.get());
            potionArrowStack.setDamageValue(brewingCauldronEntity.getLiquidData());
            if (heldItemStack.getCount() >= 64) {
                arrowCount = switch (brewingCauldronEntity.getLiquidLevel()) {
                    case 3 -> 64;
                    case 2 -> 32;
                    default -> 16;
                };
            } else if (heldItemStack.getCount() >= 32) {
                arrowCount = switch (brewingCauldronEntity.getLiquidLevel()) {
                    case 3 -> heldItemStack.getCount();
                    case 2 -> 32;
                    default -> 16;
                };
            } else if (heldItemStack.getCount() >= 16) {
                arrowCount = switch (brewingCauldronEntity.getLiquidLevel()) {
                    case 3, 2 -> heldItemStack.getCount();
                    default -> 16;
                };
            } else {
                arrowCount = 16;
            }
            potionArrowStack.setCount(arrowCount);
            CompoundTag compoundTag = potionArrowStack.getOrCreateTag();
            compoundTag.putBoolean("Unbreakable", true);
            compoundTag.putInt("HideFlags", 4);
            compoundTag.putInt("Color", Mth.abs(brewingCauldronEntity.getLiquidColor()));
            potionArrowStack.setTag(compoundTag);
            if (!player.getInventory().add(potionArrowStack)) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, potionArrowStack));
            }
            if (!player.getAbilities().instabuild) {
                heldItemStack.shrink(arrowCount);
                brewingCauldronEntity.setLiquidData(0);
                brewingCauldronEntity.setLiquidLevel(0);
                this.updateBlockState(level, pos, state, 0, false);
            }
            if (heldItemStack.getCount() <= 0) {
                player.getInventory().add(player.getInventory().selected, ItemStack.EMPTY);
            }
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else if (brewingCauldronEntity.applyIngredient(heldItemStack)) {
            this.updateBlockState(level, pos, state, state.getValue(LEVEL), true);
            if (heldItemStack.getItem() == RegistryContents.POTION_ITEM.get()) {
                if (!player.getAbilities().instabuild) {
                    heldItemStack.shrink(1);
                }
                if (heldItemStack.getCount() <= 0) {
                    player.getInventory().add(player.getInventory().selected, ItemStack.EMPTY);
                }
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            } else {
                if (!player.getAbilities().instabuild) {
                    heldItemStack.shrink(1);
                }
                if (heldItemStack.getCount() <= 0) {
                    player.getInventory().add(player.getInventory().selected, ItemStack.EMPTY);
                }
            }
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.sendBlockUpdated(pos, state, state, 3);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(RegistryContents.BREWING_CAULDRON.get());
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, POTION);
    }

}