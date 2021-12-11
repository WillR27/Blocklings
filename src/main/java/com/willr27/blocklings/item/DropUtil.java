package com.willr27.blocklings.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DropUtil
{
    public static List<ItemStack> getDrops(Entity entity, BlockPos blockPos, ItemStack stack)
    {
        return getDrops(entity, blockPos, stack, stack);
    }

    public static List<ItemStack> getDrops(Entity entity, BlockPos blockPos, ItemStack mainStack, ItemStack offStack)
    {
        List<ItemStack> drops = new ArrayList<>();
        BlockState blockState = entity.level.getBlockState(blockPos);
        Block block = blockState.getBlock();

        ItemStack stack = new Random().nextInt(2) == 0 && !mainStack.isEmpty() ? mainStack : !offStack.isEmpty() ? offStack : mainStack;
        drops.addAll(block.getDrops(blockState, (ServerWorld) entity.level, blockPos, null, entity, stack));
        drops = drops.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());

//        if (entity instanceof BlocklingEntity)
//        {
//            BlocklingEntity blockling = (BlocklingEntity) entity;
//            if (blockling.abilityManager.isBought(Abilities.Woodcutting.CHARCOAL))
//            {
//                int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
//                if (level > 0 || blockling.abilityManager.isBought(Abilities.Woodcutting.CHARCOAL_2))
//                {
//                    List<ItemStack> remove = new ArrayList<>();
//                    List<ItemStack> add = new ArrayList<>();
//                    for (ItemStack drop : drops)
//                    {
//                        Optional<FurnaceRecipe> rec = entity.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(drop), entity.world);
//                        if (rec.isPresent())
//                        {
//                            FurnaceRecipe furnaceRecipe = rec.get();
//                            ItemStack item = furnaceRecipe.getRecipeOutput().copy();
//                            if (item.getItem() == Items.CHARCOAL)
//                            {
//                                remove.add(drop);
//                                add.add(item);
//                            }
//                        }
//                    }
//
//                    drops.removeAll(remove);
//                    drops.addAll(add);
//                }
//            }
//        }

        return drops;
    }
}
