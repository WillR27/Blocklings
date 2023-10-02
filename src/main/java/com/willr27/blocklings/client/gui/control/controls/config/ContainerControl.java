package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.capabilities.BlockSelectCapability;
import com.willr27.blocklings.client.gui.BlocklingGuiHandler;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.*;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.*;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.goal.config.ContainerInfo;
import com.willr27.blocklings.util.BlockUtil;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.event.ValueChangedEvent;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A control used to configure a container.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ContainerControl extends GridPanel
{
    /**
     * The container control being configured in the world.
     */
    @Nullable
    public static ContainerControl currentlyConfiguredContainerControl = null;

    /**
     * The screen to go back to once the container is configured from the world.
     */
    @Nullable
    public static Screen screenToGoBackTo = null;

    /**
     * The grid used to display the dropdown.
     */
    @Nonnull
    private final GridPanel dropdownGrid;

    /**
     * The dropdown up arrow.
     */
    @Nonnull
    private final TexturedControl upArrow;

    /**
     * The dropdown down arrow.
     */
    @Nonnull
    private final TexturedControl downArrow;

    /**
     * The block icon.
     */
    @Nonnull
    private final BlockControl blockIcon;

    /**
     * The block side priority selector.
     */
    @Nonnull
    private final BlockSideSelectionControl sidePriority;

    /**
     * The block name.
     */
    @Nonnull
    private final TextBlockControl name;

    /**
     * The block search control.
     */
    @Nonnull
    private final ItemSearchControl itemSearch;

    /**
     * The container info.
     */
    @Nonnull
    public final ContainerInfo containerInfo;

    /**
     * The x location.
     */
    @Nonnull
    private final NullableIntFieldControl xLocation;

    /**
     * The y location.
     */
    @Nonnull
    private final NullableIntFieldControl yLocation;

    /**
     * The z location.
     */
    @Nonnull
    private final NullableIntFieldControl zLocation;

    /**
     */
    public ContainerControl(@Nonnull ContainerInfo containerInfo)
    {
        super();
        this.containerInfo = containerInfo;

        setFitHeightToContent(true);
        addRowDefinition(GridDefinition.AUTO, 1.0);
        addRowDefinition(GridDefinition.AUTO, 1.0);
        addColumnDefinition(GridDefinition.AUTO, 1.0);

        GridPanel mainGrid = new GridPanel();
        addChild(mainGrid, 0, 0);
        mainGrid.setWidthPercentage(1.0);
        mainGrid.setFitHeightToContent(true);
        mainGrid.addRowDefinition(GridDefinition.RATIO, 1.0);
        mainGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
        mainGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

        Control crossBackground = new Control();

        TexturedControl iconBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED)
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("config.container.remove", new ItemStack(containerInfo.getBlock()).getHoverName().getString()));
            }

            @Override
            public void onHoverEnter()
            {
                crossBackground.setVisibility(Visibility.VISIBLE);
            }

            @Override
            public void onHoverExit()
            {
                crossBackground.setVisibility(Visibility.COLLAPSED);
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed())
                {
                    ContainerControl.this.setParent(null);

                    e.setIsHandled(true);
                }
            }
        };
        mainGrid.addChild(iconBackground, 0, 0);
        iconBackground.setChildrenInteractive(false);

        blockIcon = new BlockControl();
        iconBackground.addChild(blockIcon);
        blockIcon.setWidthPercentage(1.0);
        blockIcon.setHeightPercentage(1.0);
        blockIcon.setBlockScale(0.5f);
        blockIcon.setBlock(containerInfo.getBlock());

        iconBackground.addChild(crossBackground);
        crossBackground.setBackgroundColour(0x55000000);
        crossBackground.setWidthPercentage(1.0);
        crossBackground.setHeightPercentage(1.0);
        crossBackground.setVisibility(Visibility.COLLAPSED);

        TexturedControl crossIcon = new TexturedControl(Textures.Common.CROSS_ICON);
        crossBackground.addChild(crossIcon);
        crossIcon.setVerticalAlignment(0.5);
        crossIcon.setHorizontalAlignment(0.5);
        crossIcon.setRenderZ(18.0);

        TexturedControl nameBackground = new TexturedControl(Textures.Common.BAR_RAISED)
        {
            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                if (isHovered() && getDraggedControl() == null)
                {
                    RenderSystem.color3f(0.7f, 0.9f, 1.0f);
                }

                Texture texture = getBackgroundTexture();

                renderTextureAsBackground(matrixStack, texture.dx(1).width((int) (getWidth() - 2)));
                renderTextureAsBackground(matrixStack, texture.x(texture.width - 2).width(2), getWidth() - 2, 0);
            }

            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, name.getText());
            }

            @Override
            public void forwardTryDrag(@Nonnull TryDragEvent e)
            {
                super.forwardTryDrag(e);
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed())
                {
                    setExpanded(!isExpanded());
                }
            }
        };
        mainGrid.addChild(nameBackground, 0, 1);
        nameBackground.setWidthPercentage(1.0);

        GridPanel nameGrid = new GridPanel();
        mainGrid.addChild(nameGrid, 0, 1);
        nameGrid.setWidthPercentage(1.0);
        nameGrid.setFitHeightToContent(true);
        nameGrid.setVerticalAlignment(0.5);
        nameGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        nameGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
        nameGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
        nameGrid.setInteractive(false);

        name = new TextBlockControl();
        nameGrid.addChild(name, 0, 0);
        name.setText(new BlocklingsTranslationTextComponent("config.container.blank"));
        name.setWidthPercentage(1.0);
        name.setMarginLeft(4.0);

        upArrow = new TexturedControl(Textures.Common.ComboBox.UP_ARROW);
        nameGrid.addChild(upArrow, 0, 1);
        upArrow.setVerticalAlignment(0.5);
        upArrow.setMargins(4.0, 0.0, 5.0, 0.0);

        downArrow = new TexturedControl(Textures.Common.ComboBox.DOWN_ARROW);
        nameGrid.addChild(downArrow, 0, 1);
        downArrow.setVerticalAlignment(0.5);
        downArrow.setMargins(4.0, 0.0, 5.0, 0.0);

        dropdownGrid = new GridPanel()
        {
            @Override
            protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
            {
                Texture texture = Textures.Common.BAR_FLAT.dy(1).dHeight(-2).width((int) getWidth());
                Texture endTexture = Textures.Common.BAR_FLAT.dy(1).dHeight(-2).width(2).x(Textures.Common.BAR_FLAT.width - 2);

                for (int i = 0; i < getHeight(); i += texture.height)
                {
                    renderTextureAsBackground(matrixStack, texture, 0, i);
                    renderTextureAsBackground(matrixStack, endTexture, getWidth() - 2, i);
                }

                renderTextureAsBackground(matrixStack, texture.dy(18).height(1), 0, getHeight() - 1);
                renderRectangleAsBackground(matrixStack, 0x33000000, 1.0, 0.0, (int) (getWidth() - 2), (int) (getHeight() - 1));
            }
        };
        addChild(dropdownGrid, 1, 0);
        dropdownGrid.setWidthPercentage(1.0);
        dropdownGrid.setFitHeightToContent(true);
        dropdownGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        dropdownGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
        dropdownGrid.addColumnDefinition(GridDefinition.RATIO, 1.2);
        dropdownGrid.setDebugName("Dropdown Grid");
        dropdownGrid.setShouldPropagateDrag(false);

        GridPanel locationGrid = new GridPanel();
        dropdownGrid.addChild(locationGrid, 0, 0);
        locationGrid.setWidthPercentage(1.0);
        locationGrid.setFitHeightToContent(true);
        locationGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        locationGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        locationGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        locationGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
        locationGrid.setDebugName("Location Grid");

        GridPanel xGrid = new GridPanel();
        locationGrid.addChild(xGrid, 0, 0);
        xGrid.setWidthPercentage(1.0);
        xGrid.setFitHeightToContent(true);
        xGrid.setMarginTop(3.0);
        xGrid.setMarginBottom(1.0);
        xGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        xGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
        xGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

        TextBlockControl xLabel = new TextBlockControl();
        xGrid.addChild(xLabel, 0, 0);
        xLabel.setFitWidthToContent(true);
        xLabel.setText(new StringTextComponent("X"));
        xLabel.setMarginLeft(5.0);
        xLabel.setMarginRight(4.0);
        xLabel.setVerticalAlignment(0.5);

        xLocation = new IntFieldControl();
        xGrid.addChild(xLocation, 0, 1);
        xLocation.setWidthPercentage(1.0);
        xLocation.setText(new StringTextComponent("1000"));
        xLocation.setHorizontalContentAlignment(0.5);
        xLocation.setHeight(16.0);
        xLocation.setValue(containerInfo.getX());
        xLocation.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
        {
            ContainerInfo oldContainerInfo = new ContainerInfo(containerInfo);
            containerInfo.setX(e.newValue);
            eventBus.post(this, new ValueChangedEvent<>(oldContainerInfo, containerInfo));
        });

        GridPanel yGrid = new GridPanel();
        locationGrid.addChild(yGrid, 1, 0);
        yGrid.setWidthPercentage(1.0);
        yGrid.setFitHeightToContent(true);
        yGrid.setMarginBottom(1.0);
        yGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        yGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
        yGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

        TextBlockControl yLabel = new TextBlockControl();
        yGrid.addChild(yLabel, 0, 0);
        yLabel.setFitWidthToContent(true);
        yLabel.setText(new StringTextComponent("Y"));
        yLabel.setMarginLeft(5.0);
        yLabel.setMarginRight(4.0);
        yLabel.setVerticalAlignment(0.5);

        yLocation = new IntFieldControl();
        yGrid.addChild(yLocation, 0, 1);
        yLocation.setWidthPercentage(1.0);
        yLocation.setText(new StringTextComponent("1000"));
        yLocation.setHorizontalContentAlignment(0.5);
        yLocation.setHeight(16.0);
        yLocation.setValue(containerInfo.getY());
        yLocation.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
        {
            ContainerInfo oldContainerInfo = new ContainerInfo(containerInfo);
            containerInfo.setY(e.newValue);
            eventBus.post(this, new ValueChangedEvent<>(oldContainerInfo, containerInfo));
        });

        GridPanel zGrid = new GridPanel();
        locationGrid.addChild(zGrid, 2, 0);
        zGrid.setWidthPercentage(1.0);
        zGrid.setFitHeightToContent(true);
        zGrid.setMarginBottom(4.0);
        zGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        zGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
        zGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

        TextBlockControl zLabel = new TextBlockControl();
        zGrid.addChild(zLabel, 0, 0);
        zLabel.setFitWidthToContent(true);
        zLabel.setText(new StringTextComponent("Z"));
        zLabel.setMarginLeft(5.0);
        zLabel.setMarginRight(4.0);
        zLabel.setVerticalAlignment(0.5);

        zLocation = new IntFieldControl();
        zGrid.addChild(zLocation, 0, 1);
        zLocation.setWidthPercentage(1.0);
        zLocation.setText(new StringTextComponent("1000"));
        zLocation.setHorizontalContentAlignment(0.5);
        zLocation.setHeight(16.0);
        zLocation.setValue(containerInfo.getZ());
        zLocation.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
        {
            ContainerInfo oldContainerInfo = new ContainerInfo(containerInfo);
            containerInfo.setZ(e.newValue);
            eventBus.post(this, new ValueChangedEvent<>(oldContainerInfo, containerInfo));
        });

        sidePriority = new BlockSideSelectionControl()
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                Direction mouseOverDirection = getDirectionMouseIsOver();
                BlocklingsTranslationTextComponent name = new BlocklingsTranslationTextComponent("config.container.side_priority.name");

                if (mouseOverDirection != null)
                {
                    switch (mouseOverDirection)
                    {
                        case NORTH:
                            name.append(new BlocklingsTranslationTextComponent("config.container.side_priority.side", new BlocklingsTranslationTextComponent("direction.front")));
                            break;
                        case SOUTH:
                            name.append(new BlocklingsTranslationTextComponent("config.container.side_priority.side", new BlocklingsTranslationTextComponent("direction.back")));
                            break;
                        case WEST:
                            name.append(new BlocklingsTranslationTextComponent("config.container.side_priority.side", new BlocklingsTranslationTextComponent("direction.left")));
                            break;
                        case EAST:
                            name.append(new BlocklingsTranslationTextComponent("config.container.side_priority.side", new BlocklingsTranslationTextComponent("direction.right")));
                            break;
                        case UP:
                            name.append(new BlocklingsTranslationTextComponent("config.container.side_priority.side", new BlocklingsTranslationTextComponent("direction.top")));
                            break;
                        case DOWN:
                            name.append(new BlocklingsTranslationTextComponent("config.container.side_priority.side", new BlocklingsTranslationTextComponent("direction.bottom")));
                            break;
                    }
                }

                List<IReorderingProcessor> tooltip = new ArrayList<>();
                tooltip.add(name.withStyle(TextFormatting.WHITE).getVisualOrderText());
                tooltip.addAll(GuiUtil.get().split(new BlocklingsTranslationTextComponent("config.container.side_priority.desc").withStyle(TextFormatting.GRAY), 200));

                renderTooltip(matrixStack, mouseX, mouseY, tooltip);
            }
        };
        dropdownGrid.addChild(sidePriority, 0, 1);
        sidePriority.setWidthPercentage(1.0);
        sidePriority.setHeightPercentage(1.0);
        sidePriority.setDebugName("Side Priority");
        sidePriority.setBlockScale(0.58f);
        sidePriority.setRenderZ(20.0);
        sidePriority.setMarginBottom(1.0);
        sidePriority.setCanMouseRotate(true);
        sidePriority.setBlock(containerInfo.getBlock());
        sidePriority.setSelectedDirections(containerInfo.getSides());
        Quaternion rotationQuat = Quaternion.ONE.copy();
        rotationQuat.mul(Vector3f.XP.rotationDegrees( 30.0f));
        rotationQuat.mul(Vector3f.YP.rotationDegrees(-45.0f));
        sidePriority.setRotationQuat(rotationQuat);
        sidePriority.eventBus.subscribe((BaseControl c, BlockSideSelectionControl.DirectionListChangedEvent e) ->
        {
            ContainerInfo oldContainerInfo = new ContainerInfo(containerInfo);
            containerInfo.setSides(e.newDirections);
            eventBus.post(this, new ValueChangedEvent<>(oldContainerInfo, containerInfo));
        });

        itemSearch = new ItemSearchControl();
        addChild(itemSearch, 0, 0);
        itemSearch.setWidthPercentage(1.0);
        itemSearch.setShouldPropagateDrag(false);
        itemSearch.setRenderZ(0.1);
        itemSearch.setVisibility(Visibility.COLLAPSED);
        itemSearch.setSearchableItems(BlockUtil.CONTAINERS.get().stream().map(Block::asItem).collect(Collectors.toList()));
        itemSearch.eventBus.subscribe((BaseControl c, ItemAddedEvent e) ->
        {
            setBlock(Block.byItem(e.item));
        });

        setExpanded(false);
        setBlock(containerInfo.getBlock());
    }

    @Override
    public void onClose(boolean isRealClose)
    {
        super.onClose(isRealClose);

        if (isRealClose && containerInfo.getBlock() == Blocks.AIR)
        {
            setParent(null);
        }
    }

    @Override
    public void onDragStart(double mouseX, double mouseY)
    {
        setExpanded(false);
    }

    /**
     * Called when the container info is first added.
     */
    public void onFirstAdded()
    {
        itemSearch.setVisibility(Visibility.VISIBLE);
        itemSearch.setFocused(true);

        screenEventBus.subscribe((BaseControl c2, FocusChangedEvent e2) ->
        {
            if (itemSearch.isThisOrDescendant(c2) && !e2.newFocus && !isThisOrDescendant(getFocusedControl()))
            {
                setParent(null);
            }
        });
    }

    /**
     * Called when the container is selected from the world.
     *
     * @param block the block.
     * @param blockPos the block position.
     */
    public void onContainerSelectFromWorld(@Nonnull Block block, BlockPos blockPos)
    {
        xLocation.setValue(blockPos.getX());
        yLocation.setValue(blockPos.getY());
        zLocation.setValue(blockPos.getZ());
        setBlock(block);
    }

    /**
     * @return whether the dropdown is expanded.
     */
    public boolean isExpanded()
    {
        return dropdownGrid.getVisibility() != Visibility.COLLAPSED;
    }

    /**
     * Sets the dropdown to be expanded or collapsed.
     * @param expanded whether the dropdown should be expanded.
     */
    public void setExpanded(boolean expanded)
    {
        dropdownGrid.setVisibility(expanded ? Visibility.VISIBLE : Visibility.COLLAPSED);
        downArrow.setVisibility(expanded ? Visibility.COLLAPSED : Visibility.VISIBLE);
        upArrow.setVisibility(expanded ? Visibility.VISIBLE : Visibility.COLLAPSED);
    }

    /**
     * @return the block.
     */
    public Block getBlock()
    {
        return blockIcon.getBlock();
    }

    /**
     * Sets the block.
     *
     * @param block the block.
     */
    public void setBlock(@Nonnull Block block)
    {
        if (block == Blocks.AIR)
        {
            return;
        }

        blockIcon.setBlock(block);
        sidePriority.setBlock(block);
        name.setText(new ItemStack(block).getHoverName());
        itemSearch.setVisibility(Visibility.COLLAPSED);

        ContainerInfo oldContainerInfo = new ContainerInfo(containerInfo);
        containerInfo.setBlock(block);
        eventBus.post(this, new ValueChangedEvent<>(oldContainerInfo, containerInfo));
    }

    /**
     * Handles the container select event.
     *
     * @param player the player.
     * @param isFinal whether this is the final call from the original event.
     * @param blockPos the block position.
     * @return whether the event should be cancelled.
     */
    private static boolean handleContainerSelect(@Nonnull PlayerEntity player, boolean isFinal, @Nullable BlockPos blockPos)
    {
        if (!player.level.isClientSide())
        {
            BlockSelectCapability cap = player.getCapability(BlockSelectCapability.CAPABILITY).orElse(null);

            if (cap != null)
            {
                boolean wereConfiguring = cap.isSelecting;

                // This can be called for both hands, so we need to make sure we only stop configuring if we are actually done.
                if (isFinal)
                {
                    cap.isSelecting = false;
                }

                if (wereConfiguring)
                {
                    return true;
                }
            }

            return false;
        }
        else if (currentlyConfiguredContainerControl != null)
        {
            if (blockPos != null)
            {
                Block block = player.level.getBlockState(blockPos).getBlock();

                if (BlockUtil.isContainer(block))
                {
                    currentlyConfiguredContainerControl.onContainerSelectFromWorld(block, blockPos);
                }
                else
                {
                    currentlyConfiguredContainerControl.setParent(null);
                }
            }
            else
            {
                currentlyConfiguredContainerControl.setParent(null);
            }

            BlocklingGuiHandler.openScreen(screenToGoBackTo);
            currentlyConfiguredContainerControl = null;
            screenToGoBackTo = null;

            return true;
        }

        return false;
    }

    /**
     * Handles a player selecting a container when configuring from the UI.
     */
    @SubscribeEvent
    public static void onPlayerContainerSelect(@Nonnull PlayerInteractEvent.RightClickBlock event)
    {
        event.setCanceled(handleContainerSelect(event.getPlayer(), event.getHand() == Hand.OFF_HAND, event.getPos()));
    }

    /**
     * Handles a player cancelling container selection when configuring from the UI.
     */
    @SubscribeEvent
    public static void onPlayerContainerSelectCancel(@Nonnull PlayerInteractEvent.LeftClickBlock event)
    {
        event.setCanceled(handleContainerSelect(event.getPlayer(), true, null));
    }

    /**
     * Handles a player cancelling container selection when configuring from the UI.
     */
    @SubscribeEvent
    public static void onPlayerContainerSelectCancel(@Nonnull PlayerInteractEvent.EntityInteract event)
    {
        handleContainerSelect(event.getPlayer(), true, null);
    }
}
