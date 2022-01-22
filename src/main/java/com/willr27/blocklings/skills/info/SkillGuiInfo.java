package com.willr27.blocklings.skills.info;

import com.willr27.blocklings.gui.GuiTexture;
import com.willr27.blocklings.gui.controls.skills.SkillControl;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public  class SkillGuiInfo
{
    public final int x;
    public final int y;
    public final SkillControl.ConnectionType connectionType;
    public final SkillGuiTexture texture;
    public final Color colour;

    public SkillGuiInfo(int x, int y, SkillControl.ConnectionType connectionType, int colour, SkillGuiTexture texture)
    {
        this.x = x;
        this.y = y;
        this.connectionType = connectionType;
        this.colour = new Color(colour);
        this.texture = texture;
    }

    public static class SkillGuiTexture extends GuiTexture
    {
        public static final int ICON_SIZE = 24;

        public SkillGuiTexture(ResourceLocation texture, int x, int y)
        {
            super(texture, x * ICON_SIZE, y * ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }
    }
}
