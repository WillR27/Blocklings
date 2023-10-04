package com.willr27.blocklings.entity.blockling.skill.info;

import com.willr27.blocklings.client.gui.texture.Texture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Info regarding how to display a skill in a gui.
 */
public class SkillGuiInfo
{
    /**
     * The x position.
     */
    public final int x;

    /**
     * The y position.
     */
    public final int y;

    /**
     * The connection type used to connect a skill with its parent.
     */
    @Nonnull
    public final ConnectionType connectionType;

    /**
     * The skill's icon texture.
     */
    @Nonnull
    public final SkillIconTexture iconTexture;

    /**
     * The skill's colour.
     */
    @Nonnull
    public final Color colour;

    /**
     * @param x the x position.
     * @param y the y position.
     * @param connectionType the connection type used to connect a skill with its parent.
     * @param colour the skill's icon texture.
     * @param texture the skill's colour.
     */
    public SkillGuiInfo(int x, int y, @Nonnull ConnectionType connectionType, int colour, @Nonnull SkillIconTexture texture)
    {
        this.x = x;
        this.y = y;
        this.connectionType = connectionType;
        this.colour = new Color(colour);
        this.iconTexture = texture;
    }

    /**
     * A skill's icon texture.
     */
    public static class SkillIconTexture extends Texture
    {
        /**
         * The width and height of an icon.
         */
        public static final int ICON_SIZE = 24;

        /**
         * @param texture the texture location.
         * @param x the texture x index.
         * @param y the texture y index.
         */
        public SkillIconTexture(@Nonnull ResourceLocation texture, int x, int y)
        {
            super(texture, x * ICON_SIZE, y * ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }
    }

    public enum ConnectionType
    {
        SINGLE_LONGEST_FIRST,
        SINGLE_SHORTEST_FIRST,
        DOUBLE_LONGEST_SPLIT,
        DOUBLE_SHORTEST_SPLIT;
    }
}
