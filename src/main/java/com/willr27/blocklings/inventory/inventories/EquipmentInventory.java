package com.willr27.blocklings.inventory.inventories;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.network.messages.EquipmentInventoryMessage;
import javafx.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jline.utils.Log;

import javax.annotation.Nonnull;

public class EquipmentInventory extends AbstractInventory
{
    public static final int TOOL_MAIN_HAND = 0;
    public static final int TOOL_OFF_HAND = 1;

    public EquipmentInventory(BlocklingEntity blockling)
    {
        super(blockling, 20);
    }

    public BlocklingHand findHandToolEquipped(ToolType toolType)
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

    public boolean hasToolsEquipped(ToolType toolType1, ToolType toolType2, boolean handsDoNotMatter)
    {
        if (handsDoNotMatter)
        {
            return hasToolEquipped(toolType1) && hasToolEquipped(toolType2);
        }
        else
        {
            return hasToolEquipped(Hand.MAIN_HAND, toolType1) && hasToolEquipped(Hand.OFF_HAND, toolType2);
        }
    }

    public boolean hasToolEquipped(Hand hand)
    {
        return ToolUtil.isTool(getHandStack(hand));
    }

    public boolean hasToolEquipped(ToolType toolType)
    {
        return hasToolEquipped(Hand.MAIN_HAND, toolType) || hasToolEquipped(Hand.OFF_HAND, toolType);
    }

    public boolean hasToolEquipped(Hand hand, ToolType toolType)
    {
        return toolType.is(getHandStack(hand));
    }

    public ItemStack getHandStack(Hand hand)
    {
        return hand == Hand.MAIN_HAND ? getItem(TOOL_MAIN_HAND) : getItem(TOOL_OFF_HAND);
    }

    public void setHandStack(Hand hand, ItemStack stack)
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

    public boolean isAttackingWith(BlocklingHand hand)
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

    public BlocklingHand findAttackingHand()
    {
        if (hasToolEquipped(Hand.MAIN_HAND, ToolType.WEAPON) && !hasToolEquipped(Hand.OFF_HAND, ToolType.WEAPON))
        {
            return BlocklingHand.MAIN;
        }
        else if (!hasToolEquipped(Hand.MAIN_HAND, ToolType.WEAPON) && hasToolEquipped(Hand.OFF_HAND, ToolType.WEAPON))
        {
            return BlocklingHand.OFF;
        }
        else if (!getHandStack(Hand.MAIN_HAND).isEmpty() && getHandStack(Hand.OFF_HAND).isEmpty())
        {
            return BlocklingHand.MAIN;
        }
        else if (getHandStack(Hand.MAIN_HAND).isEmpty() && !getHandStack(Hand.OFF_HAND).isEmpty())
        {
            return BlocklingHand.OFF;
        }

        return BlocklingHand.BOTH;
    }

    public boolean canHarvestBlockWithEquippedTools(BlockState blockState)
    {
        return canHarvestBlockWithEquippedTool(Hand.MAIN_HAND, blockState) || canHarvestBlockWithEquippedTool(Hand.OFF_HAND, blockState);
    }

    public boolean canHarvestBlockWithEquippedTool(Hand hand, BlockState blockState)
    {
        return ToolUtil.canToolHarvestBlock(getHandStack(hand), blockState);
    }

    public boolean trySwitchToBestTool(BlocklingHand hand, ToolType toolType)
    {
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestToolSlots = findBestToolSlotsToSwitchTo(hand, toolType);

        int mainHandSlot = bestToolSlots.getKey().getKey();
        int mainBestSlot = bestToolSlots.getKey().getValue();
        int offHandSlot = bestToolSlots.getValue().getKey();
        int offBestSlot = bestToolSlots.getValue().getValue();

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

    public Pair<ItemStack, ItemStack> findBestToolsToSwitchTo(BlocklingHand hand, ToolType toolType)
    {
        Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> bestTools = findBestToolSlotsToSwitchTo(hand, toolType);

        return new Pair<>(getItem(bestTools.getKey().getValue()), getItem(bestTools.getValue().getValue()));
    }

    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> findBestToolSlotsToSwitchTo(BlocklingHand hand, ToolType toolType)
    {
        if (hand == BlocklingHand.MAIN)
        {
            return new Pair<>(findBestToolSlotToSwitchTo(BlocklingHand.MAIN, toolType), new Pair<>(TOOL_OFF_HAND, TOOL_OFF_HAND));
        }
        else if (hand == BlocklingHand.OFF)
        {
            return new Pair<>(new Pair<>(TOOL_MAIN_HAND, TOOL_MAIN_HAND), findBestToolSlotToSwitchTo(BlocklingHand.OFF, toolType));
        }
        else if (hand == BlocklingHand.BOTH)
        {
            Pair<Integer, Integer> toolSlotsMain = findBestToolSlotToSwitchTo(BlocklingHand.MAIN, toolType);

            if (toolSlotsMain.getValue() != TOOL_MAIN_HAND)
            {
                swapItems(toolSlotsMain.getKey(), toolSlotsMain.getValue());
            }

            Pair<Integer, Integer> toolSlotsOff = findBestToolSlotToSwitchTo(BlocklingHand.OFF, toolType);

            if (toolSlotsMain.getValue() != TOOL_MAIN_HAND)
            {
                swapItems(toolSlotsMain.getKey(), toolSlotsMain.getValue());
            }

            return new Pair<>(toolSlotsMain, toolSlotsOff);
        }

        return new Pair<>(new Pair<>(TOOL_MAIN_HAND, TOOL_MAIN_HAND), new Pair<>(TOOL_OFF_HAND, TOOL_OFF_HAND));
    }

    /**
     * @param hand the hand to find the tool for, should be either MAIN or OFF.
     * @param toolType the tool type to find the tools for.
     * @return a pair containing the slots to swap with the best items, hand slot first then the other slot to swap with.
     */
    @Nonnull
    public Pair<Integer, Integer> findBestToolSlotToSwitchTo(@Nonnull BlocklingHand hand, @Nonnull ToolType toolType)
    {
        if (!(hand == BlocklingHand.MAIN || hand == BlocklingHand.OFF))
        {
            Log.warn("Tried to find the best tool to switch to with a hand that wasn't MAIN or OFF!");

            return new Pair<>(TOOL_OFF_HAND, TOOL_OFF_HAND);
        }

        int bestSlot = hand == BlocklingHand.MAIN ? TOOL_MAIN_HAND : TOOL_OFF_HAND;
        int handSlot = bestSlot;
        ItemStack handStack = getItem(handSlot);

        if (toolType == ToolType.WEAPON)
        {
            float bestAttackPower = ToolUtil.getToolAttackSpeed(handStack) * ToolUtil.getToolBaseDamage(handStack);

            for (int i = TOOL_OFF_HAND + 1; i < getContainerSize(); i++)
            {
                ItemStack stack = stacks[i];

                float attackPower = ToolUtil.getToolAttackSpeed(stack) * ToolUtil.getToolBaseDamage(stack);

                if (attackPower > bestAttackPower)
                {
                    bestSlot = i;
                    bestAttackPower = attackPower;
                }
            }
        }
        else
        {
            float bestSpeed = ToolUtil.getToolSpeed(handStack, toolType);

            for (int i = TOOL_OFF_HAND + 1; i < getContainerSize(); i++)
            {
                ItemStack stack = stacks[i];

                float speed = ToolUtil.getToolSpeed(stack, toolType);

                if (speed > bestSpeed)
                {
                    bestSlot = i;
                    bestSpeed = speed;
                }
            }
        }

        return new Pair<>(handSlot, bestSlot);
    }

    @Override
    public ItemStack addItem(ItemStack stack)
    {
        int maxStackSize = stack.getMaxStackSize();

        for (int i = 0; i < invSize && !stack.isEmpty(); i++)
        {
            if (i >= TOOL_MAIN_HAND && i <= TOOL_OFF_HAND)
            {
                if (!ToolUtil.isTool(stack))
                {
                    continue;
                }
            }

            ItemStack slotStack = getItem(i);

            if (ItemStack.isSame(stack, slotStack))
            {
                int amountToAdd = stack.getCount();
                amountToAdd = Math.min(amountToAdd, maxStackSize - slotStack.getCount());
                stack.shrink(amountToAdd);
                slotStack.grow(amountToAdd);
                setItem(i, slotStack);
            }
        }

        for (int i = 0; i < invSize && !stack.isEmpty(); i++)
        {
            if (i >= TOOL_MAIN_HAND && i <= TOOL_OFF_HAND)
            {
                if (!ToolUtil.isTool(stack))
                {
                    continue;
                }
            }

            ItemStack slotStack = getItem(i);

            if (slotStack.isEmpty())
            {
                setItem(i, stack.copy());
                stack.setCount(0);

                break;
            }
        }

        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        super.setItem(index, stack);

        if (index == TOOL_MAIN_HAND || index == TOOL_OFF_HAND)
        {
            updateToolAttributes();
        }
    }

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
            stats.mainHandAttackDamageToolModifier.setValue(ToolUtil.getToolBaseDamage(blockling.getMainHandItem()), false);
            stats.attackSpeedMainHandModifier.setValue(ToolUtil.getToolAttackSpeed(blockling.getMainHandItem()), false);
        }

        if (isAttackingWith(BlocklingHand.OFF) && hasToolEquipped(Hand.OFF_HAND))
        {
            stats.offHandAttackDamageToolModifier.setValue(ToolUtil.getToolBaseDamage(blockling.getOffhandItem()), false);
            stats.attackSpeedOffHandModifier.setValue(ToolUtil.getToolAttackSpeed(blockling.getOffhandItem()), false);
        }

        if (hasToolEquipped(Hand.MAIN_HAND, ToolType.PICKAXE))
        {
            stats.miningSpeedMainHandModifier.setValue(ToolUtil.getToolMiningSpeed(blockling.getMainHandItem()), false);
        }
        else if (hasToolEquipped(Hand.MAIN_HAND, ToolType.AXE))
        {
            stats.woodcuttingSpeedMainHandModifier.setValue(ToolUtil.getToolWoodcuttingSpeed(blockling.getMainHandItem()), false);
        }
        else if (hasToolEquipped(Hand.MAIN_HAND, ToolType.HOE))
        {
            stats.farmingSpeedMainHandModifier.setValue(ToolUtil.getToolFarmingSpeed(blockling.getMainHandItem()), false);
        }

        if (hasToolEquipped(Hand.OFF_HAND, ToolType.PICKAXE))
        {
            stats.miningSpeedOffHandModifier.setValue(ToolUtil.getToolMiningSpeed(blockling.getOffhandItem()), false);
        }
        else if (hasToolEquipped(Hand.OFF_HAND, ToolType.AXE))
        {
            stats.woodcuttingSpeedOffHandModifier.setValue(ToolUtil.getToolWoodcuttingSpeed(blockling.getOffhandItem()), false);
        }
        else if (hasToolEquipped(Hand.OFF_HAND, ToolType.HOE))
        {
            stats.farmingSpeedOffHandModifier.setValue(ToolUtil.getToolFarmingSpeed(blockling.getOffhandItem()), false);
        }
    }

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
}
