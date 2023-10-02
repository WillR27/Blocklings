package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.capabilities.BlockSelectCapability;
import com.willr27.blocklings.client.gui.BlocklingGuiHandler;
import com.willr27.blocklings.client.gui.control.BaseControl;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.IntFieldControl;
import com.willr27.blocklings.client.gui.control.controls.NullableIntFieldControl;
import com.willr27.blocklings.client.gui.control.controls.TextBlockControl;
import com.willr27.blocklings.client.gui.control.controls.TexturedControl;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.controls.panels.StackPanel;
import com.willr27.blocklings.client.gui.control.event.events.TryDragEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.properties.Visibility;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.entity.blockling.goal.config.patrol.PatrolPoint;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.event.ValueChangedEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A control for configuring a patrol point.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PatrolPointControl extends Control
{
    /**
     * The patrol point control being configured in the world.
     */
    @Nullable
    public static PatrolPointControl currentlyConfiguredPatrolPointControl = null;

    /**
     * The screen to go back to once the patrol point is configured from the world.
     */
    @Nullable
    public static Screen screenToGoBackTo = null;

    /**
     * The patrol point being configured.
     */
    @Nonnull
    public final PatrolPoint patrolPoint;

    /**
     * The x location field.
     */
    @Nonnull
    private final NullableIntFieldControl xLocation;

    /**
     * The y location field.
     */
    @Nonnull
    private final NullableIntFieldControl yLocation;

    /**
     * The z location field.
     */
    @Nonnull
    private final NullableIntFieldControl zLocation;

    /**
     * @param patrolPoint the patrol point to display.
     */
    public PatrolPointControl(@Nonnull PatrolPoint patrolPoint)
    {
        super();
        this.patrolPoint = patrolPoint;

        setWidthPercentage(1.0);
        setFitHeightToContent(true);
        setDraggableY(true);

        GridPanel grid = new GridPanel();
        addChild(grid);
        grid.setWidthPercentage(1.0);
        grid.setFitHeightToContent(true);
        grid.addRowDefinition(GridDefinition.AUTO, 1.0);
        grid.addRowDefinition(GridDefinition.AUTO, 1.0);
        grid.addColumnDefinition(GridDefinition.AUTO, 1.0);

        GridPanel headerGrid = new GridPanel();
        grid.addChild(headerGrid, 0, 0);
        headerGrid.setWidthPercentage(1.0);
        headerGrid.setFitHeightToContent(true);
        headerGrid.addRowDefinition(GridDefinition.RATIO, 1.0);
        headerGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
        headerGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);

        TexturedControl iconBackground = new TexturedControl(Textures.Tasks.TASK_ICON_BACKGROUND_RAISED, Textures.Tasks.TASK_ICON_BACKGROUND_PRESSED);
        headerGrid.addChild(iconBackground, 0, 0);

        Control crossBackground = new Control()
        {
            @Override
            public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
            {
                renderTooltip(matrixStack, mouseX, mouseY, new BlocklingsTranslationTextComponent("config.patrol.remove"));
            }

            @Override
            public void onHoverEnter()
            {
                setBackgroundColour(0x55000000);
            }

            @Override
            public void onHoverExit()
            {
                setBackgroundColour(0x00000000);
            }

            @Override
            protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
            {
                if (isPressed())
                {
                    PatrolPointControl.this.setParent(null);

                    patrolPoint.getPatrolPointList().remove(patrolPoint.getPatrolPointList().indexOf(patrolPoint));

                    e.setIsHandled(true);
                }
            }
        };
        iconBackground.addChild(crossBackground);
        crossBackground.setBackgroundColour(0x00000000);
        crossBackground.setWidthPercentage(1.0);
        crossBackground.setHeightPercentage(1.0);
        crossBackground.setChildrenInteractive(false);

        TexturedControl crossIcon = new TexturedControl(Textures.Common.CROSS_ICON);
        crossBackground.addChild(crossIcon);
        crossIcon.setVerticalAlignment(0.5);
        crossIcon.setHorizontalAlignment(0.5);
        crossIcon.setRenderZ(18.0);

        GridPanel dropdownGrid = new GridPanel()
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

        TexturedControl upArrow = new TexturedControl(Textures.Common.ComboBox.UP_ARROW);
        TexturedControl downArrow = new TexturedControl(Textures.Common.ComboBox.DOWN_ARROW);
        TextBlockControl name = new TextBlockControl()
        {
            @Override
            public void onTick()
            {
                super.onTick();

                setText(new BlocklingsTranslationTextComponent("config.patrol.point", 1 + PatrolPointControl.this.getParent().getChildren().indexOf(PatrolPointControl.this)));
            }
        };

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
                    dropdownGrid.setVisibility(dropdownGrid.getVisibility() == Visibility.VISIBLE ? Visibility.COLLAPSED : Visibility.VISIBLE);
                    upArrow.setVisibility(dropdownGrid.getVisibility());
                    downArrow.setVisibility(dropdownGrid.getVisibility() == Visibility.VISIBLE ? Visibility.COLLAPSED : Visibility.VISIBLE);
                }
            }
        };
        headerGrid.addChild(nameBackground, 0, 1);
        nameBackground.setWidthPercentage(1.0);

        GridPanel nameGrid = new GridPanel();
        headerGrid.addChild(nameGrid, 0, 1);
        nameGrid.setWidthPercentage(1.0);
        nameGrid.setFitHeightToContent(true);
        nameGrid.setVerticalAlignment(0.5);
        nameGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        nameGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
        nameGrid.addColumnDefinition(GridDefinition.AUTO, 1.0);
        nameGrid.setInteractive(false);

        nameGrid.addChild(name, 0, 0);
        name.setText(new BlocklingsTranslationTextComponent("config.patrol.point", 1));
        name.setWidthPercentage(1.0);
        name.setMarginLeft(4.0);

        nameGrid.addChild(upArrow, 0, 1);
        upArrow.setVerticalAlignment(0.5);
        upArrow.setMargins(4.0, 0.0, 5.0, 0.0);
        upArrow.setVisibility(Visibility.COLLAPSED);

        nameGrid.addChild(downArrow, 0, 1);
        downArrow.setVerticalAlignment(0.5);
        downArrow.setMargins(4.0, 0.0, 5.0, 0.0);

        grid.addChild(dropdownGrid, 1, 0);
        dropdownGrid.setWidthPercentage(1.0);
        dropdownGrid.setFitHeightToContent(true);
        dropdownGrid.addRowDefinition(GridDefinition.AUTO, 1.0);
        dropdownGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
        dropdownGrid.addColumnDefinition(GridDefinition.RATIO, 1.0);
        dropdownGrid.setDebugName("Dropdown Grid");
        dropdownGrid.setShouldPropagateDrag(false);
        dropdownGrid.setVisibility(Visibility.COLLAPSED);

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

        xLocation = new NullableIntFieldControl();
        xGrid.addChild(xLocation, 0, 1);
        xLocation.setWidthPercentage(1.0);
        xLocation.setText(new StringTextComponent("1000"));
        xLocation.setHorizontalContentAlignment(0.5);
        xLocation.setHeight(16.0);
        xLocation.setValue(patrolPoint.getX());
        xLocation.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
        {
            patrolPoint.setX(e.newValue);
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

        yLocation = new NullableIntFieldControl();
        yGrid.addChild(yLocation, 0, 1);
        yLocation.setWidthPercentage(1.0);
        yLocation.setText(new StringTextComponent("1000"));
        yLocation.setHorizontalContentAlignment(0.5);
        yLocation.setHeight(16.0);
        yLocation.setValue(patrolPoint.getY());
        yLocation.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
        {
            patrolPoint.setY(e.newValue);
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

        zLocation = new NullableIntFieldControl();
        zGrid.addChild(zLocation, 0, 1);
        zLocation.setWidthPercentage(1.0);
        zLocation.setText(new StringTextComponent("1000"));
        zLocation.setHorizontalContentAlignment(0.5);
        zLocation.setHeight(16.0);
        zLocation.setValue(patrolPoint.getZ());
        zLocation.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
        {
            patrolPoint.setZ(e.newValue);
        });

        StackPanel waitTimeStackPanel = new StackPanel();
        dropdownGrid.addChild(waitTimeStackPanel, 0, 1);
        waitTimeStackPanel.setWidthPercentage(1.0);
        waitTimeStackPanel.setFitHeightToContent(true);
        waitTimeStackPanel.setVerticalAlignment(0.5);
        waitTimeStackPanel.setHorizontalContentAlignment(0.5);
        waitTimeStackPanel.setMargins(5.0, 0.0, 5.0, 0.0);

        TextBlockControl waitTimeLabel = new TextBlockControl();
        waitTimeStackPanel.addChild(waitTimeLabel);
        waitTimeLabel.setFitWidthToContent(true);
        waitTimeLabel.setText(new BlocklingsTranslationTextComponent("config.patrol.wait_time.name"));
        waitTimeLabel.setMarginBottom(2.0);

        IntFieldControl waitTime = new IntFieldControl();
        waitTimeStackPanel.addChild(waitTime);
        waitTime.setWidthPercentage(1.0);
        waitTime.setValue(patrolPoint.getWaitTime());
        waitTime.setHorizontalContentAlignment(0.5);
        waitTime.setMinVal(0);
        waitTime.setMaxVal(120000);
        waitTime.eventBus.subscribe((BaseControl c, ValueChangedEvent<Integer> e) ->
        {
            patrolPoint.setWaitTime(e.newValue);
        });
    }

    /**
     * Occurs when the patrol point is selected from the world.
     *
     * @param blockPos the block position.
     */
    public void onPatrolPointSelectFromWorld(@Nonnull BlockPos blockPos)
    {
        patrolPoint.setX(blockPos.getX());
        patrolPoint.setY(blockPos.getY());
        patrolPoint.setZ(blockPos.getZ());
        xLocation.setValue(blockPos.getX());
        yLocation.setValue(blockPos.getY());
        zLocation.setValue(blockPos.getZ());
    }

    /**
     * Handles the patrol point select event.
     *
     * @param player the player.
     * @param isFinal whether this is the final call from the original event.
     * @param blockPos the block position.
     * @return whether the event should be cancelled.
     */
    private static boolean handlePatrolPointSelect(@Nonnull PlayerEntity player, boolean isFinal, @Nullable BlockPos blockPos)
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
        else if (currentlyConfiguredPatrolPointControl != null)
        {
            if (blockPos != null)
            {
                currentlyConfiguredPatrolPointControl.onPatrolPointSelectFromWorld(blockPos);
            }
            else
            {
                currentlyConfiguredPatrolPointControl.patrolPoint.getPatrolPointList().remove(currentlyConfiguredPatrolPointControl.patrolPoint.getPatrolPointList().indexOf(currentlyConfiguredPatrolPointControl.patrolPoint));
                currentlyConfiguredPatrolPointControl.setParent(null);
            }

            BlocklingGuiHandler.openScreen(screenToGoBackTo);
            currentlyConfiguredPatrolPointControl = null;
            screenToGoBackTo = null;

            return true;
        }

        return false;
    }

    /**
     * Handles a player selecting a block when configuring from the UI.
     */
    @SubscribeEvent
    public static void onPlayerContainerSelect(@Nonnull PlayerInteractEvent.RightClickBlock event)
    {
        event.setCanceled(handlePatrolPointSelect(event.getPlayer(), event.getHand() == Hand.OFF_HAND, event.getPos()));
    }

    /**
     * Handles a player cancelling block selection when configuring from the UI.
     */
    @SubscribeEvent
    public static void onPlayerBlockSelectCancel(@Nonnull PlayerInteractEvent.LeftClickBlock event)
    {
        event.setCanceled(handlePatrolPointSelect(event.getPlayer(), true, null));
    }

    /**
     * Handles a player cancelling block selection when configuring from the UI.
     */
    @SubscribeEvent
    public static void onPlayerBlockSelectCancel(@Nonnull PlayerInteractEvent.EntityInteract event)
    {
        handlePatrolPointSelect(event.getPlayer(), true, null);
    }
}
