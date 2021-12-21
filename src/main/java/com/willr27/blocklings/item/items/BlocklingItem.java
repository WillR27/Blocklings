package com.willr27.blocklings.item.items;

import com.willr27.blocklings.entity.EntityTypes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingTasks;
import com.willr27.blocklings.entity.entities.blockling.goal.Task;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.NBTTagArgument;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.DeferredWorkQueue;

public class BlocklingItem extends Item
{
    public BlocklingItem(Properties properties)
    {
        super(properties);
    }

    public static ItemStack create(BlocklingEntity blockling)
    {
        ItemStack stack = new ItemStack(Items.BLOCKLING.get(), 1);
        stack.setHoverName(blockling.getCustomName());

        CompoundNBT entityTag = new CompoundNBT();
        blockling.addAdditionalSaveData(entityTag);
        stack.getTag().put("entity", entityTag);

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
            blockling.unlockedTamedTasks(false);

            world.addFreshEntity(blockling);

            if (!context.getPlayer().abilities.instabuild)
            {
                stack.shrink(1);
            }
        }

        return ActionResultType.PASS;
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
