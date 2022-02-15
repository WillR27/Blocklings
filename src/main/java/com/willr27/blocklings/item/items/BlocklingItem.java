package com.willr27.blocklings.item.items;

import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.EntityTypes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.DeferredWorkQueue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlocklingItem extends Item
{
    public BlocklingItem(Properties properties)
    {
        super(properties);
    }

    public static ItemStack create(BlocklingEntity blockling)
    {
        ItemStack stack = new ItemStack(Items.BLOCKLING.get(), 1);
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

//            BlocklingEntity blockling = (BlocklingEntity) EntityTypes.BLOCKLING_ENTITY.get().spawn((ServerWorld) world, stack, context.getPlayer(), blockpos, SpawnReason.SPAWN_EGG, true, true);

            BlocklingEntity blockling = new BlocklingEntity(EntityTypes.BLOCKLING_ENTITY.get(), world);

            if (stack.getTag() != null)
            {
                blockling.readAdditionalSaveData((CompoundNBT) stack.getTag().get("entity"));
            }
            else
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
        super.appendHoverText(stack, world, tooltip, flag);

        CompoundNBT stackTag = stack.getTag();

        if (stackTag != null && stackTag.contains("entity"))
        {
            tooltip.add(new StringTextComponent(TextFormatting.GREEN + new BlocklingsTranslationTextComponent("attribute.health.name").getString() + ": " + stackTag.getInt("health") + "/" + stackTag.getInt("max_health")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.combat_level.name").getString() + ": " + stackTag.getInt("combat_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.mining_level.name").getString() + ": " + stackTag.getInt("mining_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.woodcutting_level.name").getString() + ": " + stackTag.getInt("woodcutting_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.farming_level.name").getString() + ": " + stackTag.getInt("farming_level")));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + new BlocklingsTranslationTextComponent("attribute.total_level.name").getString() + ": " + stackTag.getInt("total_level")));
        }
    }

    public static void registerItemModelsProperties()
    {
        DeferredWorkQueue.runLater(() ->
        {
            ItemModelsProperties.register(Items.BLOCKLING.get(), new BlocklingsResourceLocation("type"), (stack, world, entity) ->
            {
                CompoundNBT c = stack.getTag();

                if (c == null)
                {
                    return 0;
                }

                return ((CompoundNBT) ((CompoundNBT) c.get("entity")).get("blockling")).getInt("type");
            });
        });
    }
}
