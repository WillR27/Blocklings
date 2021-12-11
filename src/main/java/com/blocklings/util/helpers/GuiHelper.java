package com.blocklings.util.helpers;

public class GuiHelper
{
    /**
     * Y offset for all GUI drawing/containers
     */
    public static final int YOFFSET = -10;

    public static final int UPGRADE_SLOT = 0;
    public static final int TOOL_SLOT_LEFT = 1;
    public static final int TOOL_SLOT_RIGHT = 2;

    public enum Tab
    {
        STATS("Stats", 1, -111, -81, 22, 22),
        EQUIPMENT("Equipment", 8, -111, -52, 22, 22),
        GENERAL("General", 3, 87, -81, 22, 22),
        COMBAT("Combat", 4, 87, -52, 22, 22),
        MINING("Mining", 5, 87, -23, 22, 22),
        WOODCUTTING("Woodcutting", 6, 87, 6, 22, 22),
        FARMING("Farming", 7, 87, 35, 22, 22),
        INVENTORY("Inventory", 2, -111, -23, 22, 22);

        public String name;
        public int id, x, y, width, height;

        Tab(String name, int id, int guiX, int guiY, int width, int height)
        {
            this.name = name;
            this.id = id;
            this.x = guiX;
            this.y = guiY;
            this.width = width;
            this.height = height;
        }
    }

    public static Tab getTabAt(int mouseX, int mouseY, int width, int height)
    {
        Tab tab = null;

        for (Tab t : Tab.values())
        {
            if (mouseX > (width / 2) + t.x && mouseX <= (width / 2) + t.x + t.width && mouseY > (height / 2) + t.y + YOFFSET && mouseY <= (height / 2) + t.y + t.height + YOFFSET)
            {
                return t;
            }
        }

        return tab;
    }
}
