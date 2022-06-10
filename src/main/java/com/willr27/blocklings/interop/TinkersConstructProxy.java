package com.willr27.blocklings.interop;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

/**
 * This class is used as a proxy for calls to Tinker's Construct.
 * This is required for any optional mod support.
 */
@Proxy(modid = "tconstruct")
public class TinkersConstructProxy extends ModProxy
{
    /**
     * The instance of the proxy.
     */
    public static TinkersConstructProxy instance = new TinkersConstructProxy();

    /**
     * @return the list of all items that are weapons from Tinker's Construct.
     */
    @Nonnull
    public List<Item> findAllWeapons()
    {
        return new ArrayList<>();
    }

    /**
     * @return true if the given item is a Tinker's Construct tool.
     */
    public boolean isTinkersTool(@Nonnull Item item)
    {
        return false;
    }

    /**
     * @return true if the given item is broken.
     */
    public boolean isToolBroken(@Nonnull ItemStack stack)
    {
        return false;
    }

    /**
     * @return true if the given tool can harvest the given block.
     */
    public boolean canToolHarvest(@Nonnull ItemStack stack, @Nonnull BlockState blockState)
    {
        return false;
    }

    /**
     * @return the harvest speed for the given tool.
     */
    public float getToolHarvestSpeed(@Nonnull ItemStack stack, @Nonnull BlockState blockState)
    {
        return 0.0f;
    }

    /**
     * @return true if the attack dealt damage.
     */
    public boolean attackEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity attackerLiving, @Nonnull Hand hand, @Nonnull Entity targetEntity, @Nonnull DoubleSupplier cooldownFunction, boolean isExtraAttack)
    {
        return false;
    }

    /**
     * @return true if the tool was damaged.
     */
    public boolean damageTool(@Nonnull ItemStack stack, int damage, @Nonnull LivingEntity entity)
    {
        return false;
    }
}
