package com.willr27.blocklings.inventory;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingHand;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.network.messages.EquipmentInventoryMessage;
import com.willr27.blocklings.util.ToolContext;
import com.willr27.blocklings.util.ToolType;
import com.willr27.blocklings.util.ToolUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jline.utils.Log;

import javax.annotation.Nonnull;

/**
 * The blockling's equipment inventory.
 */
public class EquipmentInventory extends AbstractInventory
{
    /**
     * The index of the slot containing the blockling's main hand tool.
     */
    public static final int TOOL_MAIN_HAND = 0;

    /**
     * The index of the slot containing the blockling's off hand tool.
     */
    public static final int TOOL_OFF_HAND = 1;

    /**
     * @param blockling the blockling the inventory is attached to.
     */
    public EquipmentInventory(@Nonnull BlocklingEntity blockling)
    {
        super(blockling, 26);
    }

    /**
     * @return which hand(s) the blockling currently has a tool in.
     */
    @Nonnull
    public BlocklingHand findHandToolEquipped(@Nonnull ToolType toolType)
    {
        boolean hasInMain = hasToolEquipped(Hand.MAIN_HAND);
        boolean hasInOff = hasToolEquipped(Hand.OFF_HAND);

        if (hasInMain && hasInOff)
        {
            return BlocklingHand.BOTH;
        }
        else if (hasInMain)
        {
            return BlocklingHand.MAIN;
        }
        else if (hasInOff)
        {
            return BlocklingHand.OFF;
        }
        else
        {
            return BlocklingHand.NONE;
        }
    }

    /**
     * @return true if the blockling has a tool equipped in the given hand.
     */
    public boolean hasToolEquipped(@Nonnull Hand hand)
    {
        return ToolUtil.isTool(getHandStack(hand));
    }

    /**
     * @return true if the blockling has at least one tool equipped of the given type.
     */
    public boolean hasToolEquipped(@Nonnull ToolType toolType)
    {
        return hasToolEquipped(Hand.MAIN_HAND, toolType) || hasToolEquipped(Hand.OFF_HAND, toolType);
    }

    /**
     * @return true if the blockling has a tool equipped of the given type in the given hand.
     */
    public boolean hasToolEquipped(@Nonnull Hand hand, @Nonnull ToolType toolType)
    {
        return toolType.is(getHandStack(hand));
    }

    /**
     * @return the stack in the given hand.
     */
    @Nonnull
    public ItemStack getHandStack(@Nonnull Hand hand)
    {
        return hand == Hand.MAIN_HAND ? getItem(TOOL_MAIN_HAND) : getItem(TOOL_OFF_HAND);
    }

    /**
     * Sets the stack in the given hand.
     */
    public void setHandStack(@Nonnull Hand hand, @Nonnull ItemStack stack)
    {
        if (hand == Hand.MAIN_HAND)
        {
            setItem(TOOL_MAIN_HAND, stack);
        }
        else if (hand == Hand.OFF_HAND)
        {
            setItem(TOOL_OFF_HAND, stack);
        }
    }

    /**
     * @return true if the blockling is attacking with the given hand.
     */
    public boolean isAttackingWith(@Nonnull BlocklingHand hand)
    {
        BlocklingHand attackingHand = findAttackingHand();

        if (hand == BlocklingHand.BOTH && attackingHand == BlocklingHand.BOTH)
        {
            return true;
        }
        else if (hand == BlocklingHand.MAIN && (attackingHand == BlocklingHand.BOTH || attackingHand == BlocklingHand.MAIN))
        {
            return true;
        }
        else if (hand == BlocklingHand.OFF && (attackingHand == BlocklingHand.BOTH || attackingHand == BlocklingHand.OFF))
        {
            return true;
        }
        else if (hand == BlocklingHand.NONE && attackingHand == BlocklingHand.NONE)
        {
            return true;
        }

        return false;
    }

    /**
     * @return the hand the blockling is attacking with.
     */
    @Nonnull
    public BlocklingHand findAttackingHand()
    {
        if (hasUseableWeapon(Hand.MAIN_HAND) && hasUseableWeapon(Hand.OFF_HAND))
        {
            return BlocklingHand.BOTH;
        }
        else if (hasUseableWeapon(Hand.MAIN_HAND) && !hasUseableWeapon(Hand.OFF_HAND))
        {
            return BlocklingHand.MAIN;
        }
        else if (!hasUseableWeapon(Hand.MAIN_HAND) && hasUseableWeapon(Hand.OFF_HAND))
        {
            return BlocklingHand.OFF;
        }
        else if (hasUseableTool(Hand.MAIN_HAND) && !hasUseableTool(Hand.OFF_HAND))
        {
            return BlocklingHand.MAIN;
        }
        else if (!hasUseableTool(Hand.MAIN_HAND) && hasUseableTool(Hand.OFF_HAND))
        {
            return BlocklingHand.OFF;
        }

        return BlocklingHand.BOTH;
    }

    /**
     * @return true if the given hand has a weapon equipped that can currently be used.
     */
    public boolean hasUseableWeapon(@Nonnull Hand hand)
    {
        return hasToolEquipped(hand, ToolType.WEAPON) && ToolUtil.isUseableTool(getHandStack(hand));
    }

    /**
     * @return true if the given hand has a tool equipped that can currently be used.
     */
    public boolean hasUseableTool(@Nonnull Hand hand)
    {
        return ToolUtil.isUseableTool(getHandStack(hand));
    }

    /**
     * @return true if the blockling can harvest the given block with their current tools.
     */
    public boolean canHarvestBlockWithEquippedTools(@Nonnull BlockState blockState)
    {
        return canHarvestBlockWithEquippedTool(Hand.MAIN_HAND, blockState) || canHarvestBlockWithEquippedTool(Hand.OFF_HAND, blockState);
    }

    /**
     * @return true if the blockling can harvest the given block with the current tool in the given hand.
     */
    public boolean canHarvestBlockWithEquippedTool(@Nonnull Hand hand, @Nonnull BlockState blockState)
    {
        return ToolUtil.canToolHarvest(getHandStack(hand), blockState);
    }

    /**
     * @return true if any slots were switched.
     */
    public boolean trySwitchToBestTool(@Nonnull BlocklingHand hand, @Nonnull ToolContext context)
    {
        Pair<SwitchedTools, SwitchedTools> bestToolSlots = findBestToolSlotsToSwitchTo(hand, context);

        int mainHandSlot = bestToolSlots.getKey().originalSlot;
        int mainBestSlot = bestToolSlots.getKey().bestSlot;
        int offHandSlot = bestToolSlots.getValue().originalSlot;
        int offBestSlot = bestToolSlots.getValue().bestSlot;

        boolean result = false;

        if (mainHandSlot != mainBestSlot)
        {
            swapItems(mainHandSlot, mainBestSlot);

            result = true;
        }

        if (offHandSlot != offBestSlot)
        {
            swapItems(offHandSlot, offBestSlot);

            result = true;
        }

        return result;
    }

    /**
     * @return the best tools for the given hand slots.
     */
    @Nonnull
    public Pair<ItemStack, ItemStack> findBestToolsToSwitchTo(@Nonnull BlocklingHand hand, @Nonnull ToolContext context)
    {
        Pair<SwitchedTools, SwitchedTools> bestTools = findBestToolSlotsToSwitchTo(hand, context);

        return new MutablePair<>(getItem(bestTools.getKey().bestSlot), getItem(bestTools.getValue().bestSlot));
    }

    /**
     * @return the current hand slots and the best hand slots to switch to.
     */
    @Nonnull
    public Pair<SwitchedTools, SwitchedTools> findBestToolSlotsToSwitchTo(@Nonnull BlocklingHand hand, @Nonnull ToolContext context)
    {
        if (hand == BlocklingHand.MAIN)
        {
            return new MutablePair<>(findBestToolSlotToSwitchTo(BlocklingHand.MAIN, context), new SwitchedTools(TOOL_OFF_HAND, TOOL_OFF_HAND));
        }
        else if (hand == BlocklingHand.OFF)
        {
            return new MutablePair<>(new SwitchedTools(TOOL_MAIN_HAND, TOOL_MAIN_HAND), findBestToolSlotToSwitchTo(BlocklingHand.OFF, context));
        }
        else if (hand == BlocklingHand.BOTH)
        {
            SwitchedTools toolSlotsMain = findBestToolSlotToSwitchTo(BlocklingHand.MAIN, context);

            if (toolSlotsMain.bestSlot != TOOL_MAIN_HAND)
            {
                swapItems(toolSlotsMain.originalSlot, toolSlotsMain.bestSlot);
            }

            SwitchedTools toolSlotsOff = findBestToolSlotToSwitchTo(BlocklingHand.OFF, context);

            if (toolSlotsMain.bestSlot != TOOL_MAIN_HAND)
            {
                swapItems(toolSlotsMain.originalSlot, toolSlotsMain.bestSlot);
            }

            return new MutablePair<>(toolSlotsMain, toolSlotsOff);
        }

        return new MutablePair<>(new SwitchedTools(TOOL_MAIN_HAND, TOOL_MAIN_HAND), new SwitchedTools(TOOL_OFF_HAND, TOOL_OFF_HAND));
    }

    /**
     * @param hand the hand to find the tool for, should be either MAIN or OFF.
     * @param context the context to use when finding the best tool.
     * @return a pair containing the slots to swap with the best items, hand slot first then the other slot to swap with.
     */
    @Nonnull
    public SwitchedTools findBestToolSlotToSwitchTo(@Nonnull BlocklingHand hand, @Nonnull ToolContext context)
    {
        if (!(hand == BlocklingHand.MAIN || hand == BlocklingHand.OFF))
        {
            Log.warn("Tried to find the best tool to switch to with a hand that wasn't MAIN or OFF!");

            return new SwitchedTools(TOOL_OFF_HAND, TOOL_OFF_HAND);
        }

        int bestSlot = hand == BlocklingHand.MAIN ? TOOL_MAIN_HAND : TOOL_OFF_HAND;
        int handSlot = bestSlot;
        ItemStack handStack = getItem(handSlot);

        if (context.toolType == ToolType.WEAPON)
        {
            float bestAttackPower = ToolUtil.getToolAttackSpeed(handStack, context.entity) * ToolUtil.getToolBaseDamage(handStack, context.entity);

            for (int i = TOOL_OFF_HAND + 1; i < getContainerSize(); i++)
            {
                ItemStack stack = stacks[i];

                float attackPower = ToolUtil.getToolAttackSpeed(stack, context.entity) * ToolUtil.getToolBaseDamage(stack, context.entity);

                if (attackPower > bestAttackPower)
                {
                    bestSlot = i;
                    bestAttackPower = attackPower;
                }
            }
        }
        else
        {
            float bestSpeed = context.toolType.is(handStack) ? ToolUtil.getToolHarvestSpeedWithEnchantments(handStack, context.blockState) : 0.0f;

            for (int i = TOOL_OFF_HAND + 1; i < getContainerSize(); i++)
            {
                ItemStack stack = stacks[i];

                if (context.toolType.is(stack) && ToolUtil.canToolHarvest(stack, context.blockState))
                {
                    float speed = ToolUtil.getToolHarvestSpeedWithEnchantments(stack, context.blockState);

                    if (speed > bestSpeed)
                    {
                        bestSlot = i;
                        bestSpeed = speed;
                    }
                }
            }
        }

        return new SwitchedTools(handSlot, bestSlot);
    }

    @Nonnull
    @Override
    public ItemStack addItem(@Nonnull ItemStack stackToAdd, int slot, boolean simulate)
    {
        // Only allow tools to be added to the tool slots.
        if (isToolSlot(slot) && !ToolUtil.isTool(stackToAdd))
        {
            return stackToAdd;
        }

        return super.addItem(stackToAdd, slot, simulate);
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack)
    {
        super.setItem(slot, stack);

        if (isToolSlot(slot))
        {
            updateToolAttributes();
        }
    }

    /**
     * Updates the values of the blockling's tool attributes.
     */
    public void updateToolAttributes()
    {
        BlocklingAttributes stats = blockling.getStats();

        stats.mainHandAttackDamageToolModifier.setValue(0.0f, false);
        stats.offHandAttackDamageToolModifier.setValue(0.0f, false);
        stats.attackSpeedMainHandModifier.setValue(0.0f, false);
        stats.attackSpeedOffHandModifier.setValue(0.0f, false);

        stats.miningSpeedMainHandModifier.setValue(0.0f, false);
        stats.miningSpeedOffHandModifier.setValue(0.0f, false);
        stats.woodcuttingSpeedMainHandModifier.setValue(0.0f, false);
        stats.woodcuttingSpeedOffHandModifier.setValue(0.0f, false);
        stats.farmingSpeedMainHandModifier.setValue(0.0f, false);
        stats.farmingSpeedOffHandModifier.setValue(0.0f, false);

        if (isAttackingWith(BlocklingHand.MAIN) && hasToolEquipped(Hand.MAIN_HAND))
        {
            stats.mainHandAttackDamageToolModifier.setValue(ToolUtil.getDefaultToolBaseDamage(blockling.getMainHandItem()), false);
            stats.attackSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolAttackSpeed(blockling.getMainHandItem()), false);
        }

        if (isAttackingWith(BlocklingHand.OFF) && hasToolEquipped(Hand.OFF_HAND))
        {
            stats.offHandAttackDamageToolModifier.setValue(ToolUtil.getDefaultToolBaseDamage(blockling.getOffhandItem()), false);
            stats.attackSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolAttackSpeed(blockling.getOffhandItem()), false);
        }

        if (hasToolEquipped(Hand.MAIN_HAND, ToolType.PICKAXE))
        {
            stats.miningSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolMiningSpeed(blockling.getMainHandItem()), false);
        }
        else if (hasToolEquipped(Hand.MAIN_HAND, ToolType.AXE))
        {
            stats.woodcuttingSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolWoodcuttingSpeed(blockling.getMainHandItem()), false);
        }
        else if (hasToolEquipped(Hand.MAIN_HAND, ToolType.HOE))
        {
            stats.farmingSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolFarmingSpeed(blockling.getMainHandItem()), false);
        }

        if (hasToolEquipped(Hand.OFF_HAND, ToolType.PICKAXE))
        {
            stats.miningSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolMiningSpeed(blockling.getOffhandItem()), false);
        }
        else if (hasToolEquipped(Hand.OFF_HAND, ToolType.AXE))
        {
            stats.woodcuttingSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolWoodcuttingSpeed(blockling.getOffhandItem()), false);
        }
        else if (hasToolEquipped(Hand.OFF_HAND, ToolType.HOE))
        {
            stats.farmingSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolFarmingSpeed(blockling.getOffhandItem()), false);
        }
    }

    /**
     * Detects any changed slots and syncs them to the client.
     */
    public void detectAndSendChanges()
    {
        if (!world.isClientSide)
        {
            for (int i = 0; i < invSize; i++)
            {
                ItemStack oldStack = stacksCopy[i];
                ItemStack newStack = stacks[i];

                if (!ItemStack.isSame(oldStack, newStack))
                {
                    if (newStack.isEmpty() && oldStack.isEmpty())
                    {
                        stacks[i] = ItemStack.EMPTY;
                        stacksCopy[i] = ItemStack.EMPTY;
                    }
                    else
                    {
                        new EquipmentInventoryMessage(blockling, i, newStack).sync();
                        stacksCopy[i] = newStack.copy();
                    }
                }
            }
        }
    }

    /**
     * @param slot the slot to check.
     * @return whether the slot is a tool slot.
     */
    public boolean isToolSlot(int slot)
    {
        return slot >= TOOL_MAIN_HAND && slot <= TOOL_OFF_HAND;
    }

    /**
     * Represents a pair of slots to switch.
     */
    public static class SwitchedTools
    {
        /**
         * The index of the slot that was being switched to.
         */
        public final int originalSlot;

        /**
         * The index of the slot that contained the best tool.
         */
        public final int bestSlot;

        /**
         * @param originalSlot the index of the slot that was being switched to.
         * @param bestSlot the index of the slot that contained the best tool.
         */
        public SwitchedTools(int originalSlot, int bestSlot)
        {
            this.originalSlot = originalSlot;
            this.bestSlot = bestSlot;
        }
    }
}
