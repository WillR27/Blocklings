package com.willr27.blocklings.gui.controls.stats;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.gui.Control;
import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.GuiTextures;
import com.willr27.blocklings.gui.IControl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * A control used to display an xp bar.
 */
@OnlyIn(Dist.CLIENT)
public class XpBarControl extends Control
{
    /**
     * The width of the xp bar.
     */
    public static final int WIDTH = 111;

    /**
     * The height of the xp bar.
     */
    public static final int HEIGHT = 5;

    /**
     * The level.
     */
    @Nonnull
    private final BlocklingAttributes.Level level;

    /**
     * The blockling.
     */
    @Nonnull
    private final BlocklingEntity blockling;

    /**
     * The texture used for the background of the xp bar.
     */
    @Nonnull
    private final GuiTexture backgroundTexture;

    /**
     * @param parent the parent control.
     * @param level the level.
     * @param blockling the blockling.
     */
    public XpBarControl(@Nonnull IControl parent, @Nonnull BlocklingAttributes.Level level, @Nonnull BlocklingEntity blockling, int x, int y)
    {
        super(parent, x, y, WIDTH, HEIGHT);
        this.level = level;
        this.blockling = blockling;
        this.backgroundTexture = new GuiTexture(GuiTextures.STATS, 0, 193 + level.ordinal() * height * 2, width, height);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY)
    {
        float percentage = blockling.getStats().getLevelXpAttribute(level).getValue() / (float) BlocklingAttributes.getXpForLevel(blockling.getStats().getLevelAttribute(level).getValue());
        int middle = (int) (width * percentage);

        renderTexture(matrixStack, backgroundTexture);
        renderTexture(matrixStack, new GuiTexture(GuiTextures.STATS, 0, 188 + level.ordinal() * height * 2, middle, height));
        renderCenteredText(matrixStack, "" + blockling.getStats().getLevelAttribute(level).getValue(), -width / 2 + 1, -1, false, getTextColour());
    }

    /**
     * @return the colour of the text for the level.
     */
    private int getTextColour()
    {
        switch (level)
        {
            case COMBAT: return 0xe03434;
            case MINING: return 0x4870d4;
            case WOODCUTTING: return 0x4db83d;
            case FARMING: return 0xedcf24;
            default: return 0xffffff;
        }
    }
}
