package com.willr27.blocklings.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

public class SkillWidget extends BoundWidget
{
    public enum ConnectionType
    {
        SINGLE_LONGEST_FIRST,
        SINGLE_SHORTEST_FIRST,
        DOUBLE_LONGEST_SPLIT,
        DOUBLE_SHORTEST_SPLIT;
    }

    public SkillWidget(FontRenderer font, int x, int y, int width, int height, int textureX, int textureY)
    {
        super(font, x, y, width, height, textureX, textureY);
    }

    public void connect(MatrixStack matrixStack, SkillWidget ability, int width, int colour, ConnectionType connectionType)
    {
        drawPath(matrixStack, findPath(ability, connectionType), width, colour);
    }

    private void drawPath(MatrixStack matrixStack, Vec2i[] path, int width, int colour)
    {
        for (int i = 0; i < path.length - 1; i++)
        {
            int x1 = path[i].x;
            int y1 = path[i].y;
            int x2 = path[i + 1].x;
            int y2 = path[i + 1].y;

            int dx = x2 - x1;
            int dy = y2 - y1;

            Direction direction = Direction.RIGHT;
            if (dx < 0 ) direction = Direction.LEFT;
            else if (dy < 0) direction = Direction.UP;
            else if (dy > 0) direction = Direction.DOWN;

            int d = (width + 1) / 2;

            if (direction == Direction.RIGHT)
            {
                y1 -= d;
                x2 += d;
                y2 += d;
            }
            else if (direction == Direction.LEFT)
            {
                y1 -= d;
                x2 -= d;
                y2 += d;
            }
            else if (direction == Direction.UP)
            {
                x1 -= d;
                x2 += d;
                y2 -= d;
            }
            else if (direction == Direction.DOWN)
            {
                x1 -= d;
                x2 += d;
                y2 += d;
            }

            if (x1 != x2 && y1 != y2)
            {
                fill(matrixStack, x1, y1, x2, y2, colour);
            }
        }
    }

    private Vec2i[] findPath(SkillWidget ability, ConnectionType connectionType)
    {
        Vec2i[] path = new Vec2i[3];

        int x1 = screenX + width / 2;
        int y1 = screenY + height / 2;
        int x2 = ability.screenX + ability.width / 2;
        int y2 = ability.screenY + ability.height / 2;

        if (connectionType == ConnectionType.SINGLE_LONGEST_FIRST || connectionType == ConnectionType.SINGLE_SHORTEST_FIRST)
        {
            int dx = x2 - x1;
            int dy = y2 - y1;

            int cx = dx;
            int cy = dy;

            if (connectionType == ConnectionType.SINGLE_LONGEST_FIRST)
            {
                cx = dy;
                cy = dx;
            }

            if (Math.abs(cx) > Math.abs(cy))
            {
                path[1] = new Vec2i(x1 + dx, y1);
            }
            else
            {
                path[1] = new Vec2i(x1, y1 + dy);
            }
        }
        else if (connectionType == ConnectionType.DOUBLE_SHORTEST_SPLIT || connectionType == ConnectionType.DOUBLE_LONGEST_SPLIT)
        {
            path = new Vec2i[4];

            int dx = x2 - x1;
            int dy = y2 - y1;

            int cx = dx;
            int cy = dy;

            if (connectionType == ConnectionType.DOUBLE_SHORTEST_SPLIT)
            {
                cx = dy;
                cy = dx;
            }

            if (Math.abs(cx) > Math.abs(cy))
            {
                path[1] = new Vec2i(x1 + dx / 2, y1);
                path[2] = new Vec2i(x1 + dx / 2, y1 + dy);
            }
            else
            {
                path[1] = new Vec2i(x1, y1 + dy / 2);
                path[2] = new Vec2i(x1 + dx, y1 + dy / 2);
            }
        }

        path[0] = new Vec2i(x1, y1);
        path[path.length - 1] = new Vec2i(x2, y2);

        return path;
    }

    private enum Direction
    {
        LEFT, UP, RIGHT, DOWN;
    }

    private class Vec2i
    {
        public final int x, y;

        public Vec2i(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
