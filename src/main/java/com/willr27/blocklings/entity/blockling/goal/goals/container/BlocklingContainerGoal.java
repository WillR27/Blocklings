package com.willr27.blocklings.entity.blockling.goal.goals.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.capabilities.ContainerConfigureCapability;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.config.ContainerControl;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.TabbedPanel;
import com.willr27.blocklings.client.gui.control.event.events.ParentChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.ReorderEvent;
import com.willr27.blocklings.client.gui.control.event.events.ValueChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.screen.BlocklingsScreen;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingTargetGoal;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.network.messages.GoalMessage;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.Version;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * A base class for handling goals that involve moving to and interacting with containers.
 */
public abstract class BlocklingContainerGoal extends BlocklingTargetGoal<ContainerInfo>
{
    /**
     * The list of containers that the blockling can interact with in priority order.
     */
    @Nonnull
    protected final List<ContainerInfo> containerInfos = new ArrayList<>();

    /**
     * @param id        the id associated with the goal's task.
     * @param blockling the blockling.
     * @param tasks     the blockling tasks.
     */
    public BlocklingContainerGoal(@Nonnull UUID id, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingTasks tasks)
    {
        super(id, blockling, tasks);

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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
     * @return returns the target tile entity as an inventory, null if the cast fails.
     */
    @Nullable
    public IInventory targetAsInventory()
    {
        return tileEntityAsInventory(targetAsTileEntity());
    }

    /**
     * @return returns the given tile entity as an inventory, null if the cast fails.
     */
    @Nullable
    public IInventory tileEntityAsInventory(@Nullable TileEntity tileEntity)
    {
        return (IInventory) tileEntity;
    }

    /**
     * Adds the given container info to the list and syncs it to the client/server.
     *
     * @param containerInfo the container info.
     */
    public void addContainerInfo(@Nonnull ContainerInfo containerInfo)
    {
        addContainerInfo(containerInfo, true);
    }

    /**
     * Adds the given container info to the list.
     *
     * @param containerInfo the container info.
     * @param sync whether to sync the container info to the client.
     */
    public void addContainerInfo(@Nonnull ContainerInfo containerInfo, boolean sync)
    {
        containerInfos.add(containerInfo);

            PlayerEntity player = (PlayerEntity) blockling.getOwner();
            player.getCapability(ContainerConfigureCapability.CAPABILITY).ifPresent(cap ->
            {
                cap.isConfiguring = true;
            });

        if (sync)
        {
            new ContainerGoalContainerAddRemoveMessage(blockling, id, containerInfos.size() - 1, true).sync();
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
            new ContainerGoalContainerAddRemoveMessage(blockling, id, index, false).sync();
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
    public void addConfigTabControls(@Nonnull TabbedPanel tabbedPanel)
    {
        super.addConfigTabControls(tabbedPanel);

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
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("config.add_container"));
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed())
                {
                    ContainerInfo containerInfo = new ContainerInfo();
                    addContainerInfo(containerInfo);

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
         */
        public ContainerGoalContainerAddRemoveMessage(@Nonnull BlocklingEntity blockling, @Nonnull UUID taskId, int index, boolean add)
        {
            super(blockling, taskId);
            this.index = index;
            this.add = add;
        }

        @Override
        public void encode(@Nonnull PacketBuffer buf)
        {
            super.encode(buf);

            buf.writeBoolean(add);
            buf.writeInt(index);
        }

        @Override
        public void decode(@Nonnull PacketBuffer buf)
        {
            super.decode(buf);

            add = buf.readBoolean();
            index = buf.readInt();
        }

        @Override
        protected void handle(@Nonnull PlayerEntity player, @Nonnull BlocklingEntity blockling, @Nonnull BlocklingContainerGoal goal)
        {
            if (add)
            {
                goal.addContainerInfo(new ContainerInfo(), false);
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
