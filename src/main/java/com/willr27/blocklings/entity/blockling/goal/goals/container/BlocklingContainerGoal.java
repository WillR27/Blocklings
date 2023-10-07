package com.willr27.blocklings.entity.blockling.goal.goals.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.capabilities.BlockSelectCapability;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.config.ContainerControl;
import com.willr27.blocklings.client.gui.control.controls.config.ItemsConfigurationControl;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.TabbedPanel;
import com.willr27.blocklings.client.gui.control.event.events.*;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.blockling.goal.config.ContainerInfo;
import com.willr27.blocklings.entity.blockling.goal.config.iteminfo.OrderedItemInfoSet;
import com.willr27.blocklings.entity.blockling.skill.skills.GeneralSkills;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.config.ItemConfigurationTypeProperty;
import com.willr27.blocklings.entity.blockling.task.config.range.IntRangeProperty;
import com.willr27.blocklings.network.messages.GoalMessage;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.Version;
import com.willr27.blocklings.util.event.ValueChangedEvent;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * A base class for handling goals that involve moving to and interacting with containers.
 */
public abstract class BlocklingContainerGoal extends BlocklingTargetGoal<ContainerInfo> implements OrderedItemInfoSet.IOrderedItemInfoSetProvider
{
    /**
     * The maximum number of items that can be included in the items list.
     */
    public static final int MAX_ITEMS = 32;

    /**
     * The maximum number of containers that the blockling can interact with.
     */
    public static final int MAX_CONTAINERS = 8;

    /**
     * The list of items to use as a whitelist.
     */
    @Nonnull
    public final OrderedItemInfoSet itemInfoSet;

    /**
     * The list of containers that the blockling can interact with in priority order.
     */
    @Nonnull
    protected final List<ContainerInfo> containerInfos = new ArrayList<>();

    /**
     * The property used to select the type of item configuration to use.
     */
    @Nonnull
    public final ItemConfigurationTypeProperty itemConfigurationTypeProperty;

    /**
     * The property used to select the amount of items to transfer per second.
     */
    @Nonnull
    public final IntRangeProperty itemTransferAmount;

    /**
     * The container control used for the items tab in the configuration screen.
     */
    private BaseControl itemsContainer;

    /**
     * The timer used to determine when to transfer items.
     */
    private int transferTimer = 0;

    /**
     * @param id        the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks     the blockling tasks.
     */
    public BlocklingContainerGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));

        itemInfoSet = new OrderedItemInfoSet(this);

        properties.add(itemConfigurationTypeProperty = new ItemConfigurationTypeProperty(
                "35d1e5a5-dfff-4a06-bb71-de1df8823632", this,
                new BlocklingsTranslationTextComponent("task.property.item_configuration_type.name"),
                new BlocklingsTranslationTextComponent("task.property.item_configuration_type.desc")));
        properties.add(itemTransferAmount = new IntRangeProperty(
                "c858da92-5009-407c-94ae-56b70d91f01a", this,
                new BlocklingsTranslationTextComponent("task.property.item_transfer_amount.name"),
                new BlocklingsTranslationTextComponent("task.property.item_transfer_amount.desc"),
                1, 4, 4));

        itemConfigurationTypeProperty.setEnabled(blockling.getSkills().getSkill(GeneralSkills.ADVANCED_COURIER).isBought());
        itemConfigurationTypeProperty.onTypeChanged.subscribe((this::recreateItemsConfigurationControl));

        itemTransferAmount.setEnabled(blockling.getSkills().getSkill(GeneralSkills.FIRST_CLASS).isBought());
    }

    @Override
    public void writeToNBT(@Nonnull CompoundNBT taskTag)
    {
        super.writeToNBT(taskTag);

        ListNBT containerInfosTag = new ListNBT();

        for (int i = 0; i < containerInfos.size(); i++)
        {
            containerInfosTag.add(containerInfos.get(i).writeToNBT());
        }

        taskTag.put("container_infos", containerInfosTag);
        taskTag.put("item_set", itemInfoSet.writeToNBT());
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT taskTag, @Nonnull Version tagVersion)
    {
        super.readFromNBT(taskTag, tagVersion);

        ListNBT containerInfosTag = taskTag.getList("container_infos", 10);

        for (int i = 0; i < containerInfosTag.size(); i++)
        {
            ContainerInfo containerInfo = new ContainerInfo();
            containerInfo.readFromNBT(containerInfosTag.getCompound(i), tagVersion);

            if (containerInfo.getBlock() != Blocks.AIR)
            {
                containerInfos.add(containerInfo);
            }
        }

        CompoundNBT itemSetTag = taskTag.getCompound("item_set");

        if (taskTag.contains("item_set"))
        {
            itemInfoSet.readFromNBT(itemSetTag, tagVersion);
        }
        else
        {
            Blocklings.LOGGER.warn("Could not find item set for deposit container goal!");
        }
    }

    @Override
    public void encode(@Nonnull PacketBuffer buf)
    {
        super.encode(buf);

        buf.writeInt(containerInfos.size());

        for (int i = 0; i < containerInfos.size(); i++)
        {
            containerInfos.get(i).encode(buf);
        }

        itemInfoSet.encode(buf);
    }

    @Override
    public void decode(@Nonnull PacketBuffer buf)
    {
        super.decode(buf);

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            ContainerInfo containerInfo = new ContainerInfo();
            containerInfo.decode(buf);
            containerInfos.add(containerInfo);
        }

        itemInfoSet.decode(buf);
    }

    @Override
    protected void tickGoal()
    {
        if (transferTimer < 20)
        {
            transferTimer++;

            return;
        }

        if (isInRangeOfPathTargetPos())
        {
            boolean depositedAnItem = tryTransferItems(getTarget(), false);

            // If no items were deposited then try other targets before this one again.
            if (!depositedAnItem)
            {
                markTargetBad();
            }
        }

        transferTimer = 0;
    }

    /**
     * Tries to transfer items to the target container.
     *
     * @param containerInfo the container info.
     * @param simulate whether to simulate the transfer.
     * @return returns true if an item was transferred.
     */
    protected abstract boolean tryTransferItems(@Nonnull ContainerInfo containerInfo, boolean simulate);

    @Override
    public void recalcTarget()
    {
        if (!hasItemsToTransfer())
        {
            setTarget(null);
            trySetPathTarget(null, null);

            return;
        }

//        for (BlockPos testPos : BlockPos.betweenClosed(blockling.blockPosition().offset(-range, -range, -range), blockling.blockPosition().offset(range, range, range)))
//        {
//            TileEntity tileEntity = world.getBlockEntity(testPos);
//
//            if (isValidTarget(tileEntity))
//            {
//                setTarget(tileEntity);
//                setPathTargetPos(null, null);
//
//                return true;
//            }
//        }

        for (ContainerInfo containerInfo : containerInfos)
        {
            if (!isInRange(containerInfo.getBlockPos(), getPathTargetRangeSq()))
            {
                continue;
            }

            if (isValidTarget(containerInfo))
            {
                setTarget(containerInfo);
                recalcPathTargetPosAndPath(true);

                return;
            }
        }

        setTarget(null);
    }

    @Override
    protected void recalcPathTargetPosAndPath(boolean force)
    {
        trySetPathTarget(getTarget().getBlockPos(), null);
    }

    @Override
    protected boolean isValidPathTargetPos(@Nonnull BlockPos blockPos)
    {
        return true;
    }

    @Override
    public float getPathTargetRangeSq()
    {
        return 256.0f;
    }

    @Override
    protected void checkForAndHandleInvalidTargets()
    {
        if (!isTargetValid())
        {
            markTargetBad();
        }
    }

    @Override
    public void markEntireTargetBad()
    {
        if (getTarget() != null)
        {
            markTargetBad();
        }
    }

    @Override
    public boolean isValidTarget(@Nullable ContainerInfo containerInfo)
    {
        if (containerInfo == null)
        {
            return false;
        }

        if (!containerInfo.isConfigured())
        {
            return false;
        }

        if (badTargets.contains(containerInfo))
        {
            return false;
        }

        if (!tryTransferItems(containerInfo, true))
        {
            return false;
        }

        return true;
    }

    /**
     * @return whether there are items available to transfer.
     */
    protected abstract boolean hasItemsToTransfer();

    /**
     * Counts the number of items in the blockling's inventory.
     *
     * @param item the item to count.
     * @return the number of items in the blockling's inventory.
     */
    public int countItemsInInventory(@Nonnull Item item)
    {
        return blockling.getEquipment().count(new ItemStack(item));
    }

    /**
     * @return true if the blockling has the given item in their inventory.
     */
    public boolean hasItemInInventory(@Nonnull Item item)
    {
        return blockling.getEquipment().has(new ItemStack(item));
    }

    /**
     * @param containerItemHandler the container to check.
     * @param item the item to check for.
     * @return true if the container has the given item.
     */
    public boolean hasItemInContainer(@Nonnull IItemHandler containerItemHandler, @Nonnull Item item)
    {
        for (int slot = 0; slot < containerItemHandler.getSlots(); slot++)
        {
            ItemStack stack = containerItemHandler.getStackInSlot(slot);

            if (stack.getItem() == item)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Counts the number of items in the container.
     *
     * @param containerItemHandler the container to count the items in.
     * @param item the item to count.
     * @return the number of items in the container.
     */
    public int countItemsInContainer(@Nonnull IItemHandler containerItemHandler, @Nonnull Item item)
    {
        int count = 0;

        for (int slot = 0; slot < containerItemHandler.getSlots(); slot++)
        {
            ItemStack stack = containerItemHandler.getStackInSlot(slot);

            if (stack.getItem() == item)
            {
                count += stack.getCount();
            }
        }

        return count;
    }

    /**
     * @return returns the target tile entity.
     */
    @Nullable
    public TileEntity targetAsTileEntity()
    {
        if (getTarget() == null)
        {
            return null;
        }

        return containerAsTileEntity(getTarget());
    }

    /**
     * @param containerInfo the container info.
     * @return returns the container info's tile entity.
     */
    @Nullable
    public TileEntity containerAsTileEntity(@Nonnull ContainerInfo containerInfo)
    {
        return world.getBlockEntity(containerInfo.getBlockPos());
    }

    /**
     * Tries to get the item handler for the given tile entity.
     *
     * @param tileEntity the tile entity to get the item handler for.
     * @param direction the direction to get the item handler for.
     * @return the item handler, or null if it doesn't exist.
     */
    @Nullable
    public IItemHandler getItemHandler(@Nonnull TileEntity tileEntity, @Nonnull Direction direction)
    {
        // We have to use orElse(null) here because the lazy optional doesn't seem to provide a way to use the object
        // outside ifPresent().
        return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);
    }

    /**
     * @return whether the container list is full.
     */
    public boolean isContainerListFull()
    {
        return containerInfos.size() >= MAX_CONTAINERS;
    }

    /**
     * Adds the given container info to the list and syncs it to the client/server.
     *
     * @param containerInfo the container info.
     * @param configureInWorld whether to configure the container in the world.
     */
    public void addContainerInfo(@Nonnull ContainerInfo containerInfo, boolean configureInWorld)
    {
        addContainerInfo(containerInfo, configureInWorld, true);
    }

    /**
     * Adds the given container info to the list.
     *
     * @param containerInfo the container info.
     * @param configureInWorld whether to configure the container in the world.
     * @param sync whether to sync the container info to the client.
     */
    public void addContainerInfo(@Nonnull ContainerInfo containerInfo, boolean configureInWorld, boolean sync)
    {
        containerInfos.add(containerInfo);

        if (configureInWorld)
        {
            PlayerEntity player = (PlayerEntity) blockling.getOwner();
            player.getCapability(BlockSelectCapability.CAPABILITY).ifPresent(cap ->
            {
                cap.isSelecting = true;
            });
        }

        if (sync)
        {
            new ContainerGoalContainerAddRemoveMessage(blockling, id, containerInfos.size() - 1, true, configureInWorld).sync();
            new ContainerGoalContainerMessage(blockling, id, containerInfo, containerInfos.size() - 1).sync();
        }
    }

    /**
     * Removes the container info at the given index and syncs it to the client/server.
     *
     * @param index the index.
     */
    public void removeContainerInfo(int index)
    {
        removeContainerInfo(index, true);
    }

    /**
     * Removes the container info at the given index.
     *
     * @param index the index.
     * @param sync whether to sync the container info to the client.
     */
    public void removeContainerInfo(int index, boolean sync)
    {
        containerInfos.remove(index);

        if (sync)
        {
            new ContainerGoalContainerAddRemoveMessage(blockling, id, index, false, false).sync();
        }
    }

    /**
     * Sets the container info at the given index and syncs it to the client/server.
     *
     * @param index the index.
     * @param containerInfo the container info.
     */
    public void setContainerInfo(int index, @Nonnull ContainerInfo containerInfo)
    {
        setContainerInfo(index, containerInfo, true);
    }

    /**
     * Sets the container info at the given index.
     *
     * @param index the index.
     * @param containerInfo the container info.
     * @param sync whether to sync the container info to the client.
     */
    public void setContainerInfo(int index, @Nonnull ContainerInfo containerInfo, boolean sync)
    {
        containerInfos.set(index, containerInfo);

        if (sync)
        {
            new ContainerGoalContainerMessage(blockling, id, containerInfo, index).sync();
        }
    }

    /**
     * Moves the container info at the given index to the given index and syncs it to the client/server.
     *
     * @param fromIndex the index to move from.
     * @param toIndex the index to move to.
     */
    public void moveContainerInfo(int fromIndex, int toIndex)
    {
        moveContainerInfo(fromIndex, toIndex, true);
    }

    /**
     * Moves the container info at the given index to the given index.
     *
     * @param fromIndex the index to move from.
     * @param toIndex the index to move to.
     * @param sync whether to sync the container info to the client.
     */
    public void moveContainerInfo(int fromIndex, int toIndex, boolean sync)
    {
        ContainerInfo containerInfo = containerInfos.get(fromIndex);
        containerInfos.remove(fromIndex);
        containerInfos.add(toIndex - (fromIndex < toIndex ? 1 : 0), containerInfo);

        if (sync)
        {
            new ContainerGoalContainerMoveMessage(blockling, id, fromIndex, toIndex).sync();
        }
    }

    @Nonnull
    @Override
    public OrderedItemInfoSet getItemSet()
    {
        return itemInfoSet;
    }

    /**
     * @return the number of items to transfer every second.
     */
    public int getTransferAmount()
    {
        return itemTransferAmount.isEnabled() ? itemTransferAmount.getValue() : itemTransferAmount.getMin();
    }

    /**
     * @return whether the goal should take items from the container or deposit them.
     */
    public abstract boolean isTakeItems();

    @Nonnull
    @Override
    public void addConfigTabControls(@Nonnull TabbedPanel tabbedPanel)
    {
        super.addConfigTabControls(tabbedPanel);

        itemsContainer = tabbedPanel.addTab(new BlocklingsTranslationTextComponent("config.items"));
        itemsContainer.setCanScrollVertically(true);

        recreateItemsConfigurationControl(itemConfigurationTypeProperty.getType());

        BaseControl containersContainer = tabbedPanel.addTab(new BlocklingsTranslationTextComponent("config.containers"));
        containersContainer.setCanScrollVertically(true);

        StackPanel stackPanel = new StackPanel();
        stackPanel.setParent(containersContainer);
        stackPanel.setWidthPercentage(1.0);
        stackPanel.setFitHeightToContent(true);
        stackPanel.setMargins(5.0, 9.0, 5.0, 5.0);
        stackPanel.setSpacing(4.0);
        stackPanel.setClipContentsToBounds(false);
        stackPanel.eventBus.subscribe((BaseControl c, ReorderEvent e) ->
        {
            int movedIndex = stackPanel.getChildren().indexOf(e.draggedControl);
            int closestIndex = stackPanel.getChildren().indexOf(e.closestControl);

            moveContainerInfo(movedIndex, closestIndex + (e.insertBefore ? 0 : 1));
        });

        Control addContainerContainer = new Control();
        addContainerContainer.setParent(stackPanel);
        addContainerContainer.setWidthPercentage(1.0);
        addContainerContainer.setFitHeightToContent(true);
        addContainerContainer.setReorderable(false);

        // A function used to add a container control to the stack panel.
        Function<ContainerInfo, ContainerControl> addContainerControl = (ContainerInfo containerInfo) ->
        {
            ContainerControl containerControl = new ContainerControl(containerInfo);
            stackPanel.insertChildBefore(containerControl, addContainerContainer);
            containerControl.setWidthPercentage(1.0);
            containerControl.setDraggableY(true);
            containerControl.setScrollFromDragControl(containersContainer);
            containerControl.eventBus.subscribe((BaseControl c, ValueChangedEvent<ContainerInfo> e2) ->
            {
                setContainerInfo(containerInfos.indexOf(e2.newValue), e2.newValue);
            });
            containerControl.eventBus.subscribe((BaseControl c, ParentChangedEvent e2) ->
            {
                // When the container control is removed, remove the container info too.
                if (e2.newParent == null)
                {
                    removeContainerInfo(containerInfos.indexOf(((ContainerControl) c).containerInfo));
                }
            });

            return containerControl;
        };

        // Add the existing container infos.
        for (ContainerInfo containerInfo : containerInfos)
        {
            addContainerControl.apply(containerInfo);
        }

        TexturedControl addContainerButton = new TexturedControl(Textures.Common.PLUS_ICON)
        {
            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (isContainerListFull())
                {
                    renderTextureAsBackground(matrixStack, Textures.Common.PLUS_ICON_DISABLED);
                }
                else
                {
                    super.onRender(matrixStack, scissorStack, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                List<IReorderingProcessor> tooltip = new ArrayList<>();
                tooltip.add(new BlocklingsTranslationTextComponent("config.container.add").withStyle(isContainerListFull() ? TextFormatting.GRAY : TextFormatting.WHITE).getVisualOrderText());
                tooltip.add(new BlocklingsTranslationTextComponent("config.container.amount", containerInfos.size(), MAX_CONTAINERS).withStyle(TextFormatting.GRAY).getVisualOrderText());
                tooltip.add(StringTextComponent.EMPTY.getVisualOrderText());
                tooltip.addAll(GuiUtil.get().split(new BlocklingsTranslationTextComponent("config.container.add.help", new StringTextComponent(Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString()).withStyle(TextFormatting.ITALIC)).withStyle(TextFormatting.GRAY), 200));
                renderTooltip(matrixStack, mouseX, mouseY, tooltip);
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed() && !isContainerListFull())
                {
                    ContainerInfo containerInfo = new ContainerInfo(BlockPos.ZERO, Blocks.AIR, Arrays.asList(Direction.UP, Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.DOWN));
                    addContainerInfo(containerInfo, !GuiUtil.get().isCrouchKeyDown());

                    ContainerControl containerControl = addContainerControl.apply(containerInfo);

                    // If the user is pressing crouch, then show the item search control.
                    if (GuiUtil.get().isCrouchKeyDown())
                    {
                        containerControl.onFirstAdded();
                    }
                    else
                    {
                        ContainerControl.currentlyConfiguredContainerControl = containerControl;
                        ContainerControl.screenToGoBackTo = Minecraft.getInstance().screen;
                        getScreen().setShouldReallyClose(false);
                        ContainerControl.screenToGoBackTo.onClose();
                        getScreen().setShouldReallyClose(true);
                    }

                    e.setIsHandled(true);
                }
            }
        };
        addContainerButton.setParent(addContainerContainer);
        addContainerButton.setHorizontalAlignment(0.5);
        addContainerButton.setMargins(0.0, 1.0, 0.0, 1.0);
    }

    /**
     * Recreates the items configuration control.
     */
    private void recreateItemsConfigurationControl(@Nonnull ItemConfigurationTypeProperty.Type type)
    {
        if (itemsContainer == null)
        {
            return;
        }

        itemsContainer.clearChildren();

        ItemsConfigurationControl itemsConfigurationControl = type.createItemsConfigurationControl(itemInfoSet, isTakeItems());
        itemsConfigurationControl.setParent(itemsContainer);
        itemsConfigurationControl.setMargins(5.0, 9.0, 5.0, 5.0);
        itemsConfigurationControl.setMaxItems(MAX_ITEMS);
        itemsConfigurationControl.setScrollFromDragControl(itemsContainer);
    }

    /**
     * A message used to sync adding and removing container info between a goal on the client/server.
     */
    public static class ContainerGoalContainerAddRemoveMessage extends GoalMessage<ContainerGoalContainerAddRemoveMessage, BlocklingContainerGoal>
    {
        /**
         * Whether to add or remove the container info.
         */
        private boolean add;

        /**
         * The index of the container.
         */
        private int index;

        /**
         * Whether to configure the container in world.
         */
        private boolean configureInWorld;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ContainerGoalContainerAddRemoveMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param taskId the id of the goal.
         * @param index the index of the container.
         * @param add whether to add or remove the container info.
         * @param configureInWorld whether to configure the container in world.
         */
        public ContainerGoalContainerAddRemoveMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, int index, boolean add, boolean configureInWorld)
        {
            super(blockling, taskId);
            this.index = index;
            this.add = add;
            this.configureInWorld = configureInWorld;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeBoolean(add);
            buf.writeInt(index);
            buf.writeBoolean(configureInWorld);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            add = buf.readBoolean();
            index = buf.readInt();
            configureInWorld = buf.readBoolean();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingContainerGoal goal)
        {
            if (add)
            {
                goal.addContainerInfo(new ContainerInfo(), configureInWorld, false);
            }
            else
            {
                goal.removeContainerInfo(index, false);
            }
        }
    }

    /**
 * A message used to sync container info between a goal on the client/server.
 */
    public static class ContainerGoalContainerMessage extends GoalMessage<ContainerGoalContainerMessage, BlocklingContainerGoal>
    {
        /**
         * The container info.
         */
        private ContainerInfo containerInfo;

        /**
         * The index of the container.
         */
        private int index;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ContainerGoalContainerMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param taskId the id of the goal.
         * @param containerInfo the container info.
         * @param index the index of the container.
         */
        public ContainerGoalContainerMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, @Nonnull ContainerInfo containerInfo, int index)
        {
            super(blockling, taskId);
            this.containerInfo = containerInfo;
            this.index = index;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            containerInfo.encode(buf);
            buf.writeInt(index);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            containerInfo = new ContainerInfo();
            containerInfo.decode(buf);
            index = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingContainerGoal goal)
        {
            goal.setContainerInfo(index, containerInfo, false);
        }
    }
    /**
     * A message used to sync the priority of the container info between a goal on the client/server.
     */
    public static class ContainerGoalContainerMoveMessage extends GoalMessage<ContainerGoalContainerMoveMessage, BlocklingContainerGoal>
    {
        /**
         * The index of the container to move.
         */
        private int fromIndex;

        /**
         * The index of the container to move to.
         */
        private int toIndex;

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ContainerGoalContainerMoveMessage()
        {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param taskId the id of the goal.
         * @param fromIndex the index of the container to move.
         * @param toIndex the index of the container to move to.
         */
        public ContainerGoalContainerMoveMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, int fromIndex, int toIndex)
        {
            super(blockling, taskId);
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeInt(fromIndex);
            buf.writeInt(toIndex);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            fromIndex = buf.readInt();
            toIndex = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingContainerGoal goal)
        {
            goal.moveContainerInfo(fromIndex, toIndex, false);
        }
    }
}
