package com.willr27.blocklings.item.items;

import com.willr27.blocklings.entity.EntityTypes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.block.BlockState;
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
        stack.getTag().put("blockling", blockling.writeBlocklingToNBT());

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

            BlocklingEntity blockling = (BlocklingEntity) EntityTypes.BLOCKLING_ENTITY.get().spawn((ServerWorld) world, stack, context.getPlayer(), blockpos, SpawnReason.SPAWN_EGG, true, true);

            blockling.tame(context.getPlayer());

            if (stack.getTag() != null)
            {
                blockling.readBlocklingFromNBT((CompoundNBT) stack.getTag().get("blockling"));
            }

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

                return ((CompoundNBT) c.get("blockling")).getInt("type");
            });
        });
    }
}
