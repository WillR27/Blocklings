package com.willr27.blocklings.inventory.inventories;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingHand;
import com.willr27.blocklings.item.ToolType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.EquipmentInventoryMessage;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class EquipmentInventory extends AbstractInventory
{
    public static final int UTILITY_1 = 0;
    public static final int UTILITY_2 = 1;
    public static final int TOOL_MAIN_HAND = 2;
    public static final int TOOL_OFF_HAND = 3;

    public EquipmentInventory(BlocklingEntity blockling)
    {
        super(blockling, 22);
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

    @Override
    public void setItem(int index, ItemStack stack)
    {
        super.setItem(index, stack);

        if (index == TOOL_MAIN_HAND || index == TOOL_OFF_HAND)
        {
            blockling.getStats().updateToolModifiers(false);
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
                        NetworkHandler.sync(blockling.level, new EquipmentInventoryMessage(i, newStack, blockling.getId()));
                        stacksCopy[i] = newStack.copy();
                    }
                }
            }
        }
    }
}
