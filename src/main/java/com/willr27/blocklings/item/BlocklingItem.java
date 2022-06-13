package com.willr27.blocklings.item;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.BlocklingsEntityTypes;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingType;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.ObjectUtil;
import com.willr27.blocklings.util.Version;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.DeferredWorkQueue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * An item used to spawn blocklings with data preserved.
 */
public class BlocklingItem extends Item
{
    /**
     * The default constructor.
     */
    public BlocklingItem()
    {
        super(new Item.Properties()
                .tab(ItemGroup.TAB_MISC)
                .stacksTo(1));
    }

    /**
     * Creates a blockling item from a blockling.
     *
     * @param blockling the blockling to create the item from.
     * @return the blockling item.
     */
    @Nonnull
    public static ItemStack create(@Nonnull BlocklingEntity blockling)
    {
        ItemStack stack = new ItemStack(BlocklingsItems.BLOCKLING.get(), 1);
        stack.setHoverName(new StringTextComponent(TextFormatting.GOLD + blockling.getCustomName().getString()));

        CompoundNBT stackTag = stack.getOrCreateTag();

        CompoundNBT entityTag = new CompoundNBT();
        blockling.addAdditionalSaveData(entityTag);
        stackTag.put("entity", entityTag);

        stackTag.putString("custom_name", blockling.getCustomName().getString());
        stackTag.putInt("health", blockling.getStats().getHealth());
        stackTag.putInt("max_health", blockling.getStats().getMaxHealth());
        stackTag.putInt("combat_level", blockling.getStats().getLevelAttribute(BlocklingAttributes.Level.COMBAT).getValue());
        stackTag.putInt("mining_level", blockling.getStats().getLevelAttribute(BlocklingAttributes.Level.MINING).getValue());
        stackTag.putInt("woodcutting_level", blockling.getStats().getLevelAttribute(BlocklingAttributes.Level.WOODCUTTING).getValue());
        stackTag.putInt("farming_level", blockling.getStats().getLevelAttribute(BlocklingAttributes.Level.FARMING).getValue());
        stackTag.putInt("total_level", blockling.getStats().getLevelAttribute(BlocklingAttributes.Level.TOTAL).getValue());

        return stack;
    }

    @Nonnull
    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();

        if (!world.isClientSide)
        {
            ItemStack stack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);

            if (!blockstate.getCollisionShape(world, blockpos).isEmpty())
            {
                blockpos = blockpos.relative(direction);
            }

            BlocklingEntity blockling = new BlocklingEntity(BlocklingsEntityTypes.BLOCKLING.get(), world);

            CompoundNBT stackTag = stack.getTag();
            CompoundNBT entityTag = null;

            if (stackTag != null && stackTag.contains("entity"))
            {
                entityTag = stackTag.getCompound("entity");
            }

            blockling.finalizeSpawn((IServerWorld) world, world.getCurrentDifficultyAt(blockpos), SpawnReason.SPAWN_EGG, null, entityTag);

            if (entityTag == null || !entityTag.contains("blockling"))
            {
                for (Task task : blockling.getTasks().getPrioritisedTasks())
                {
                    if (task.isConfigured() && task.getType() == BlocklingTasks.WANDER)
                    {
                        task.setType(BlocklingTasks.FOLLOW, false);
                    }
                }
            }

            blockling.setPos(blockpos.getX() + 0.5, blockpos.getY(), blockpos.getZ() + 0.5);
            blockling.tame(context.getPlayer());

            if (stack.getTag() != null)
            {
                if (stack.getTag().contains("custom_name"))
                {
                    blockling.setCustomName(new StringTextComponent(stack.getTag().getString("custom_name")));
                }
            }

            world.addFreshEntity(blockling);

            if (!context.getPlayer().abilities.instabuild)
            {
                stack.shrink(1);
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag)
    {
        CompoundNBT stackTag = stack.getTag();

        if (stackTag != null && stackTag.contains("entity"))
        {
            tooltip.add(new StringTextComponent(TextFormatting.GREEN + new BlocklingsTranslationTextComponent("attribute.health.name").getString() + ": " + stackTag.getInt("health") + "/" + stackTag.getInt("max_health")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.combat_level.name").getString() + ": " + stackTag.getInt("combat_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.mining_level.name").getString() + ": " + stackTag.getInt("mining_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.woodcutting_level.name").getString() + ": " + stackTag.getInt("woodcutting_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.farming_level.name").getString() + ": " + stackTag.getInt("farming_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.total_level.name").getString() + ": " + stackTag.getInt("total_level")));
            tooltip.add(new StringTextComponent(""));
        }

        super.appendHoverText(stack, world, tooltip, flag);
    }

    public static void registerItemModelsProperties()
    {
        DeferredWorkQueue.runLater(() ->
        {
            ItemModelsProperties.register(BlocklingsItems.BLOCKLING.get(), new BlocklingsResourceLocation("type"), (stack, world, entity) ->
            {
                CompoundNBT stackTag = stack.getTag();

                if (stackTag != null)
                {
                    CompoundNBT entityTag = stackTag.getCompound("entity");

                    if (entityTag != null)
                    {
                        CompoundNBT blocklingTag = entityTag.getCompound("blockling");

                        if (blocklingTag != null)
                        {
                            return BlocklingType.TYPES.indexOf(BlocklingType.find(blocklingTag.getString("type"), ObjectUtil.coalesce(new Version(blocklingTag.getString("blocklings_version")), Blocklings.VERSION)));
                        }
                    }
                }

                return 0;
            });
        });
    }
}
