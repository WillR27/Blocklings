package com.willr27.blocklings.util;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingType;
import com.willr27.blocklings.entity.blockling.skill.skills.MiningSkills;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DropUtil
{
    /**
     * Gets and processes the drops from a block.
     * Applies enchantments, skills etc.
     *
     * @param context the type of gathering used to filter applied skills.
     * @param blockling the blockling.
     * @param blockPos the position of the target block.
     * @param mainStack the held item in the blockling's main hand.
     * @param offStack the held item in the blockling's off hand.
     * @return a list of the final drops.
     */
    @Nonnull
    public static List<ItemStack> getDrops(@Nonnull Context context, @Nonnull BlocklingEntity blockling, @Nonnull BlockPos blockPos, @Nonnull ItemStack mainStack, @Nonnull ItemStack offStack)
    {
        Level world = blockling.level;

        ItemStack mergedStack = mainStack.copy();

        for (Enchantment enchantment : ToolUtil.findToolEnchantments(offStack))
        {
            int mainLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, mainStack);
            int offLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, offStack);
            mergedStack.enchant(enchantment, Math.max(mainLevel, offLevel));
        }

        if (blockling.getNaturalBlocklingType() == BlocklingType.LAPIS || blockling.getBlocklingType() == BlocklingType.LAPIS)
        {
            mergedStack.enchant(Enchantments.BLOCK_FORTUNE, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, mergedStack) + 1);
        }

        List<ItemStack> drops = Block.getDrops(world.getBlockState(blockPos), (ServerLevel) world, blockPos, null, null, mergedStack);

        // Do any post drop processing here. Skills etc...

        if (context == Context.MINING)
        {
            if (blockling.getSkills().getSkill(MiningSkills.HOT_HANDS).isBought())
            {
                List<ItemStack> newDrops = new ArrayList<>();

                for (ItemStack stack : drops)
                {
                    Optional<SmeltingRecipe> recipeFor = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), world);

                    if (recipeFor.isPresent())
                    {
                        newDrops.add(recipeFor.get().getResultItem().copy());
                    }
                    else
                    {
                        newDrops.add(stack);
                    }
                }

                drops = newDrops;
            }
        }

        return drops;
    }

    public enum Context
    {
        MINING,
        WOODCUTTING,
        FARMING
    }
}
