package com.willr27.blocklings.client.gui.control.controls;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.client.gui.control.Control;
import com.willr27.blocklings.client.gui.control.controls.panels.GridPanel;
import com.willr27.blocklings.client.gui.control.event.events.SelectionChangedEvent;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.properties.GridDefinition;
import com.willr27.blocklings.client.gui.texture.Texture;
import com.willr27.blocklings.client.gui.texture.Textures;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A control that allows the user to select a single option from a set.
 */
@OnlyIn(Dist.CLIENT)
public class SingleSelectorStrip<O> extends Control
{
    /**
     * The options that the user can select from.
     */
    @Nonnull
    private List<O> options = new ArrayList<>();

    /**
     * The index of the currently selected option.
     */
    private int selectedIndex = 0;

    /**
     */
    public SingleSelectorStrip()
    {
        super();

        setFitHeightToContent(true);
    }

    /**
     * Recreates the strip.
     */
    private void recreate()
    {
        clearChildren();

        GridPanel strip = new GridPanel();
        strip.setParent(this);
        strip.setWidthPercentage(1.0);
        strip.setFitHeightToContent(true);
        strip.addRowDefinition(GridDefinition.AUTO, 1.0);

        for (int i = 0; i < getOptions().size(); i++)
        {
            strip.addColumnDefinition(GridDefinition.RATIO, 1.0);

            O option = getOptions().get(i);

            OptionControl optionControl = new OptionControl(option);
            strip.addChild(optionControl, 0, i);
            optionControl.setWidthPercentage(1.0);
        }
    }

    /**
     * @return the options that the user can select from.
     */
    @Nonnull
    public List<O> getOptions()
    {
        return options;
    }

    /**
     * Sets the options that the user can select from.
     *
     * @param options the options that the user can select from.
     */
    public void setOptions(@Nonnull List<O> options)
    {
        this.options = options;

        recreate();
    }

    /**
     * @return the currently selected option.
     */
    public O getSelectedOption()
    {
        return getOptions().get(getSelectedIndex());
    }

    /**
     * Sets the currently selected option.
     *
     * @param option the currently selected option.
     */
    public void setSelectedOption(@Nonnull O option)
    {
        for (int i = 0; i < getOptions().size(); i++)
        {
            if (getOptions().get(i).equals(option))
            {
                setSelectedIndex(i);

                return;
            }
        }
    }

    /**
     * @return the index of the currently selected option.
     */
    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    /**
     * Sets the index of the currently selected option.
     *
     * @param selectedIndex the index of the currently selected option.
     */
    public void setSelectedIndex(int selectedIndex)
    {
        O previousOption = getSelectedOption();

        this.selectedIndex = selectedIndex;

        eventBus.post(this, new SelectionChangedEvent<>(previousOption, getSelectedOption()));
    }

    private class OptionControl extends TexturedControl
    {
        /**
         * The option that this control represents.
         */
        @Nonnull
        public final O option;

        /**
         * @param option the option that this control represents.
         */
        public OptionControl(@Nonnull O option)
        {
            super(Textures.Common.BAR_RAISED, Textures.Common.BAR_FLAT);
            this.option = option;

            TextBlockControl textBlockControl = new TextBlockControl();
            textBlockControl.setParent(this);
            textBlockControl.setText(option.toString());
            textBlockControl.setHorizontalContentAlignment(0.5);
            textBlockControl.setVerticalAlignment(0.5);
            textBlockControl.setWidthPercentage(1.0);
            textBlockControl.setMargins(4.0);
        }

        @Override
        protected void onRender(@Nonnull MatrixStack matrixStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
        {
            Texture texture = isSelected() ? getPressedBackgroundTexture() : getBackgroundTexture();

            if (isHovered())
            {
                RenderSystem.color3f(0.7f, 0.9f, 1.0f);
            }

            renderTextureAsBackground(matrixStack, texture);

            if (isLast())
            {
                renderTextureAsBackground(matrixStack, texture.x(texture.width - 2).width(2), getWidth() - 2, 0);
            }
            else
            {
                renderTextureAsBackground(matrixStack, texture.x(texture.width - 2).width(1), getWidth() - 1, 0);
            }
        }

        @Override
        public void onRenderTooltip(@Nonnull MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks)
        {
            renderTooltip(matrixStack, mouseX, mouseY, new StringTextComponent(option.toString()));
        }

        @Override
        protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
        {
            if (isPressed() && !isSelected())
            {
                select();
            }
        }

        /**
         * @return whether this option is selected.
         */
        public boolean isSelected()
        {
            return getOptions().indexOf(option) == selectedIndex;
        }

        /**
         * Selects this option.
         */
        public void select()
        {
            setSelectedIndex(getOptions().indexOf(option));
        }

        /**
         * @return whether this option is the last in the list.
         */
        public boolean isLast()
        {
            return getOptions().indexOf(option) == getOptions().size() - 1;
        }
    }
}
