package com.willr27.blocklings.interop;

import net.minecraft.core.Registry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerTools;

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
        return Registry.ITEM.stream().filter(item -> item.equals(TinkerTools.pickaxe.get())).collect(Collectors.toList());
    }

    @Override
    public boolean isTinkersTool(@Nonnull Item item)
    {
        return item instanceof ModifiableItem;
    }

    @Override
    public boolean isToolBroken(@Nonnull ItemStack stack)
    {
        return ToolStack.from(stack).isBroken();
    }

    @Override
    public boolean canToolHarvest(@Nonnull ItemStack stack, @Nonnull BlockState blockState)
    {
        return ToolHarvestLogic.isEffective(ToolStack.from(stack), blockState);
    }

    @Override
    public float getToolHarvestSpeed(@Nonnull ItemStack stack, @Nonnull BlockState blockState)
    {
        return ToolHarvestLogic.getDestroySpeed(stack, blockState);
    }

    @Override
    public boolean attackEntity(@Nonnull ItemStack stack, @Nonnull LivingEntity attackerLiving, @Nonnull InteractionHand hand, @Nonnull Entity targetEntity, @Nonnull DoubleSupplier cooldownFunction, boolean isExtraAttack)
    {
        return ToolAttackUtil.attackEntity(ToolStack.from(stack), attackerLiving, InteractionHand.MAIN_HAND, targetEntity, () -> 1.0, false);
    }

    @Override
    public boolean damageTool(@Nonnull ItemStack stack, int damage, @Nonnull LivingEntity entity)
    {
        return ToolDamageUtil.damageAnimated(ToolStack.from(stack), damage, entity);
    }
}
