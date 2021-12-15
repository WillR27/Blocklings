package com.willr27.blocklings.item;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DropUtil
{
    public static List<ItemStack> getDrops(@Nonnull BlocklingEntity blockling, @Nonnull BlockPos blockPos, @Nonnull ItemStack mainStack, @Nonnull ItemStack offStack)
    {
        World world = blockling.level;

        ItemStack mergedStack = mainStack.copy();

        for (Enchantment enchantment : ToolUtil.findToolEnchantments(offStack))
        {
            int mainLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, mainStack);
            int offLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, offStack);
            mergedStack.enchant(enchantment, Math.max(mainLevel, offLevel));
        }

        List<ItemStack> drops = Block.getDrops(world.getBlockState(blockPos), (ServerWorld) world, blockPos, null, null, mergedStack);

        // Do any post loot processing here. Skills etc...

        return drops;
    }
}
