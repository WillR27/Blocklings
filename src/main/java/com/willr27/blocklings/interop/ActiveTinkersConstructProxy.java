package com.willr27.blocklings.interop;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.IModifiableWeapon;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.item.small.SwordTool;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;

/**
 * This class contains the actual implementations for the proxy methods.
 */
public class ActiveTinkersConstructProxy extends TinkersConstructProxy
{
    @Nonnull
    @Override
    public List<Item> findAllWeapons()
    {
        return Registry.ITEM.stream().filter(item -> item instanceof SwordTool).collect(Collectors.toList());
    }

    @Override
    public boolean isTinkersTool(@Nonnull Item item)
    {
        return item instanceof slimeknights.tconstruct.library.tools.item.ToolItem;
    }

    @Override
    public boolean isToolBroken(@Nonnull ItemStack stack)
    {
        return ToolStack.from(stack).isBroken();
    }

    @Override
    public boolean canToolHarvest(@Nonnull ItemStack stack, @Nonnull BlockState blockState)
    {
        return ToolHarvestLogic.DEFAULT.isEffective(ToolStack.from(stack), stack, blockState);
    }

    @Override
    public float getToolHarvestSpeed(@Nonnull ItemStack stack, @Nonnull BlockState blockState)
    {
        return ToolHarvestLogic.DEFAULT.getDestroySpeed(stack, blockState);
    }

    @Override
    public boolean attackEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity attackerLiving, @Nonnull Hand hand, @Nonnull Entity targetEntity, @Nonnull DoubleSupplier cooldownFunction, boolean isExtraAttack)
    {
        return ToolAttackUtil.attackEntity((IModifiableWeapon) stack.getItem(), ToolStack.from(stack), attackerLiving, Hand.MAIN_HAND, targetEntity, () -> 1.0, false);
    }

    @Override
    public boolean damageTool(@Nonnull ItemStack stack, int damage, @Nonnull LivingEntity entity)
    {
        return ToolDamageUtil.damageAnimated(ToolStack.from(stack), damage, entity);
    }
}
