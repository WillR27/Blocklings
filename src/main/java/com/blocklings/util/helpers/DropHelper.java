package com.blocklings.util.helpers;

import com.blocklings.abilities.AbilityHelper;
import com.blocklings.entities.EntityBlockling;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class DropHelper
{
    private static final Random rand = new Random();

    public static NonNullList<ItemStack> getDops(EntityBlockling blockling, World world, BlockPos blockPos)
    {
        NonNullList<ItemStack> dropStacks = NonNullList.create();

        ItemStack mainStack = blockling.getHeldItemMainhand();
        ItemStack offStack = blockling.getHeldItemOffhand();

        int fortuneLevel = 0;
        boolean silkTouch = false;

        boolean toolMatchesTaskMain = (blockling.isUsingPickaxeRight() && blockling.getTask() == EntityHelper.Task.MINE) || (blockling.isUsingAxeRight() && blockling.getTask() == EntityHelper.Task.CHOP);
        boolean toolMatchesTaskOff = (blockling.isUsingPickaxeLeft() && blockling.getTask() == EntityHelper.Task.MINE) || (blockling.isUsingAxeLeft() && blockling.getTask() == EntityHelper.Task.CHOP);

        if (toolMatchesTaskMain)
        {
            List<NBTTagCompound> enchantments = ToolHelper.getEnchantmentTagsFromTool(mainStack);
            for (NBTTagCompound enchantmentTag : enchantments)
            {
                ToolHelper.Enchantment enchantment = ToolHelper.Enchantment.getEnchantmentFromTag(enchantmentTag);
                if (enchantment == ToolHelper.Enchantment.FORTUNE)
                {
                    fortuneLevel += enchantmentTag.getInteger("lvl");
                }
                if (enchantment == ToolHelper.Enchantment.SILKTOUCH)
                {
                    silkTouch = true;
                }
            }
        }

        if (toolMatchesTaskOff)
        {
            List<NBTTagCompound> enchantments = ToolHelper.getEnchantmentTagsFromTool(offStack);
            for (NBTTagCompound enchantmentTag : enchantments)
            {
                ToolHelper.Enchantment enchantment = ToolHelper.Enchantment.getEnchantmentFromTag(enchantmentTag);
                if (enchantment == ToolHelper.Enchantment.FORTUNE)
                {
                    fortuneLevel += enchantmentTag.getInteger("lvl");
                }
                if (enchantment == ToolHelper.Enchantment.SILKTOUCH)
                {
                    silkTouch = true;
                }
            }
        }

        if (silkTouch)
        {
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            dropStacks.add(new ItemStack(Item.getItemFromBlock(block)));
        }
        else
        {
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            block.getDrops(dropStacks, world, blockPos, blockState, fortuneLevel);
        }

        return dropStacks;
    }

    public static ItemStack getFurnaceResult(EntityBlockling blockling, ItemStack stack)
    {
        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
        ItemStack copy = result.copy();

        if (rand.nextFloat() < 0.25f)
        {
            if (blockling.miningAbilities.isAbilityAcquired(AbilityHelper.metallurgy2))
            {
                copy.setCount(copy.getCount() * 3);
            }
            else if (blockling.miningAbilities.isAbilityAcquired(AbilityHelper.metallurgy1))
            {
                copy.setCount(copy.getCount() * 2);
            }
        }

        return copy;
    }
}
