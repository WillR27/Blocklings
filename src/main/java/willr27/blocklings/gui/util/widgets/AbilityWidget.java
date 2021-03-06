package willr27.blocklings.gui.util.widgets;

import net.minecraft.client.gui.FontRenderer;

public class AbilityWidget extends BoundWidget
{
    public enum ConnectionType
    {
        SINGLE_LONGEST_FIRST,
        SINGLE_SHORTEST_FIRST,
        DOUBLE_LONGEST_SPLIT,
        DOUBLE_SHORTEST_SPLIT;
    }

    public AbilityWidget(FontRenderer font, int x, int y, int width, int height, int textureX, int textureY)
    {
        super(font, x, y, width, height, textureX, textureY);
    }

    public void connect(AbilityWidget ability, int width, int colour, int boundLeft, int boundRight, int boundTop, int boundBottom, ConnectionType connectionType)
    {
        drawPath(findPath(ability, connectionType), width, colour, boundLeft, boundRight, boundTop, boundBottom);
    }

    private void drawPath(Vec2i[] path, int width, int colour, int boundLeft, int boundRight, int boundTop, int boundBottom)
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

            int dl = boundLeft - x1;
            int dr = x1 - boundRight;
            int dt = boundTop - y1;
            int db = y1 - boundBottom;
            if (dl > 0) x1 += dl;
            else if (dr > 0) x1 -= dr;
            if (dt > 0) y1 += dt;
            else if (db > 0) y1 -= db;

            dl = boundLeft - x2;
            dr = x2 - boundRight;
            dt = boundTop - y2;
            db = y2 - boundBottom;
            if (dl > 0) x2 += dl;
            else if (dr > 0) x2 -= dr;
            if (dt > 0) y2 += dt;
            else if (db > 0) y2 -= db;

            if (x1 != x2 && y1 != y2)
            {
                fill(x1, y1, x2, y2, colour);
            }
        }
    }

    private Vec2i[] findPath(AbilityWidget ability, ConnectionType connectionType)
    {
        Vec2i[] path = new Vec2i[3];

        int x1 = x + width / 2;
        int y1 = y + height / 2;
        int x2 = ability.x + ability.width / 2;
        int y2 = ability.y + ability.height / 2;

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
