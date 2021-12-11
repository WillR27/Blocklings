package com.willr27.blocklings.gui.screens.guis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.gui.GuiUtil;
import com.willr27.blocklings.whitelist.GoalWhitelist;
import com.willr27.blocklings.whitelist.Whitelist;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.lwjgl.opengl.GL11;

public class WhitelistGui2 extends AbstractGui
{
    public final BlocklingEntity blockling;
    public final PlayerEntity player;
    public final BlocklingGoal goal;
    public final FontRenderer font;
    public final Screen screen;
    public final ItemRenderer itemRenderer;
    private int centerX, centerY;
    private int left, top;
    private int contentLeft, contentTop;

    private GoalWhitelist whitelist;
    private int whitelistPage;

    public WhitelistGui2(BlocklingEntity blockling, PlayerEntity player, BlocklingGoal goal, FontRenderer font, Screen screen, ItemRenderer itemRenderer, int width, int height)
    {
        this.blockling = blockling;
        this.player = player;
        this.goal = goal;
        this.font = font;
        this.screen = screen;
        this.itemRenderer = itemRenderer;

        centerX = width / 2;
        centerY = height / 2 + TabbedGui.OFFSET_Y;

        left = centerX - TabbedGui.UI_WIDTH / 2;
        top = centerY - TabbedGui.UI_HEIGHT / 2;

        contentLeft = centerX - TabbedGui.CONTENT_WIDTH / 2;
        contentTop = top;

        whitelistPage = 0;
        whitelist = goal.whitelists.get(0);

        maxPages = (int) Math.ceil(whitelist.size() / (float) ENTRIES_PER_PAGE);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtil.bindTexture(GuiUtil.WHITELIST);
        blit(matrixStack, contentLeft, contentTop, 0, 0, TabbedGui.CONTENT_WIDTH, TabbedGui.CONTENT_HEIGHT);

        font.draw(matrixStack, whitelist.name, contentLeft + 47, contentTop + 6, 0xffffff);

        drawButtons(matrixStack, mouseX, mouseY);
        drawScroll(matrixStack, mouseX, mouseY);
        drawEntries(matrixStack, mouseX, mouseY);
        drawTooltips(matrixStack, mouseX, mouseY);
    }

    private static final int ENTRIES_PER_PAGE = 16;

    private static final int ENTRY_BUTTON_TEXTURE_Y = 166;
    private static final int ENTRY_BUTTON_SIZE = 30;
    private static final int ENTRY_BUTTON_GAP = 4;
    private static final int ENTRY_BUTTON_START_X = 13;
    private static final int ENTRY_BUTTON_START_Y = 21;

    private static final int SCROLL_WIDTH = 12;
    private static final int SCROLL_HEIGHT = 15;
    private static final int SCROLL_TEXTURE_Y = 196;
    private static final int SCROLL_X = 155;
    private static final int SCROLL_START_Y = 17;
    private static final int SCROLL_LENGTH = 141 - SCROLL_HEIGHT;

    private static final int BUTTON_SIZE = 11;
    private static final int BUTTON_Y = 4;
    private static final int BUTTON_TEXTURE_Y = 211;

    private static final int ON_BUTTON_X = 8;
    private static final int OFF_BUTTON_X = 20;
    private static final int SWAP_BUTTON_X = 32;

    private static final int ARROW_BUTTON_TEXTURE_Y = 222;
    private static final int LEFT_BUTTON_X = 127;
    private static final int RIGHT_BUTTON_x = 139;

    private int index;
    private int page;
    private int maxPages;
    private int maxForPage;
    private float scroll;
    private boolean scrollPressed;
    private boolean onPressed;
    private boolean offPressed;
    private boolean swapPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    private void drawButtons(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        GuiUtil.bindTexture(GuiUtil.WHITELIST);

        int onTextureX = onPressed ? BUTTON_SIZE : 0;
        RenderSystem.color3f(0.0f, 1.0f, 0.0f);
        blit(matrixStack, getOnX(), getOnY(), onTextureX, BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);

        int offTextureX = offPressed ? BUTTON_SIZE : 0;
        RenderSystem.color3f(1.0f, 0.0f, 0.0f);
        blit(matrixStack, getOffX(), getOffY(), offTextureX, BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);

        int swapTextureX = swapPressed ? BUTTON_SIZE : 0;
        RenderSystem.color3f(1.0f, 1.0f, 0.0f);
        blit(matrixStack, getSwapX(), getSwapY(), swapTextureX, BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);

        if (goal.whitelists.size() > 1)
        {
            int leftTextureX = leftPressed ? BUTTON_SIZE : 0;
            if (leftPressed) RenderSystem.color3f(0.6f, 0.6f, 0.6f);
            else RenderSystem.color3f(0.9f, 0.9f, 0.9f);
            blit(matrixStack, getLeftX(), getLeftY(), leftTextureX, BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);
            blit(matrixStack, getLeftX(), getLeftY(), 0, ARROW_BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);

            int rightTextureX = rightPressed ? BUTTON_SIZE : 0;
            if (rightPressed) RenderSystem.color3f(0.6f, 0.6f, 0.6f);
            else RenderSystem.color3f(0.9f, 0.9f, 0.9f);
            blit(matrixStack, getRightX(), getRightY(), rightTextureX, BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);
            blit(matrixStack, getRightX(), getRightY(), BUTTON_SIZE, ARROW_BUTTON_TEXTURE_Y, BUTTON_SIZE, BUTTON_SIZE);
        }

        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
    }

    private int getOnX()
    {
        return contentLeft + ON_BUTTON_X;
    }
    private int getOnY()
    {
        return contentTop + BUTTON_Y;
    }
    private int getOffX()
    {
        return contentLeft + OFF_BUTTON_X;
    }
    private int getOffY()
    {
        return contentTop + BUTTON_Y;
    }
    private int getSwapX()
    {
        return contentLeft + SWAP_BUTTON_X;
    }
    private int getSwapY()
    {
        return contentTop + BUTTON_Y;
    }

    private int getLeftX()
    {
        return contentLeft + LEFT_BUTTON_X;
    }
    private int getLeftY()
    {
        return contentTop + BUTTON_Y;
    }
    private int getRightX()
    {
        return contentLeft + RIGHT_BUTTON_x;
    }
    private int getRightY()
    {
        return contentTop + BUTTON_Y;
    }

    public void drawTooltips(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        int i = getHoveredEntry(mouseX, mouseY);
        if (i != -1)
        {
            ResourceLocation entry = (ResourceLocation) whitelist.keySet().toArray()[i];

            if (whitelist.type == Whitelist.Type.BLOCK)
            {
                Block block = Registry.BLOCK.get(entry);
                screen.renderTooltip(matrixStack, block.getName(), mouseX, mouseY);
            }
            else if (whitelist.type == Whitelist.Type.ITEM)
            {
                Item item = Registry.ITEM.get(entry);
                screen.renderTooltip(matrixStack, item.getName(item.getDefaultInstance()), mouseX, mouseY);
            }
            else if (whitelist.type == Whitelist.Type.ENTITY)
            {
                Entity ent = EntityUtil.create(entry, blockling.level);
                screen.renderTooltip(matrixStack, ent.getName(), mouseX, mouseY);
            }
        }
    }

    private void drawScroll(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (scrollPressed)
        {
            int dy = mouseY - contentTop - SCROLL_START_Y - SCROLL_HEIGHT / 2;
            dy = Math.min(dy, SCROLL_LENGTH);
            dy = Math.max(dy, 0);
            float percent = (float) dy / (float) SCROLL_LENGTH;
            page = (int)((percent * whitelist.size()) / ENTRIES_PER_PAGE);
            scroll = dy / (float) SCROLL_LENGTH;
        }

        page = Math.max(page, 0);
        page = Math.min(page, maxPages - 1);
        index = page * ENTRIES_PER_PAGE;
        maxForPage = Math.min(ENTRIES_PER_PAGE, whitelist.size() - page * ENTRIES_PER_PAGE);
        scroll = ((page - 1) * ENTRIES_PER_PAGE + maxForPage) / (float) (whitelist.size() - ENTRIES_PER_PAGE);
        if (!scrollPressed) scroll = page / (float) (maxPages - 1);

        int scrollTextureX = scrollPressed ? SCROLL_WIDTH : 0;
        blit(matrixStack, getScrollX(), getScrollY(), scrollTextureX, SCROLL_TEXTURE_Y, SCROLL_WIDTH, SCROLL_HEIGHT);
    }

    private int getScrollX()
    {
        return contentLeft + SCROLL_X;
    }
    private int getScrollY()
    {
        return contentTop + SCROLL_START_Y + ((int) (SCROLL_LENGTH * scroll));
    }

    private void drawEntries(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        for (int i = 0; i < maxForPage; i++)
        {
            ResourceLocation entry = (ResourceLocation) whitelist.keySet().toArray()[i + index];
            boolean isInWhitelist = whitelist.isEntryWhitelisted(entry);

            int buttonX = getButtonX(i);
            int buttonY = getButtonY(i);

            RenderSystem.color3f(1.0f, 1.0f, 1.0f);
            GuiUtil.bindTexture(GuiUtil.WHITELIST);
            int entryButtonTextureX = isInWhitelist ? 0 : ENTRY_BUTTON_SIZE;
            blit(matrixStack, buttonX, buttonY, entryButtonTextureX, ENTRY_BUTTON_TEXTURE_Y, ENTRY_BUTTON_SIZE, ENTRY_BUTTON_SIZE);

            int offset = 2;
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GuiUtil.scissor(buttonX + offset, buttonY + offset, ENTRY_BUTTON_SIZE - offset * 2, ENTRY_BUTTON_SIZE - offset * 2);

            if (whitelist.type == Whitelist.Type.BLOCK)
            {
                Block block = Registry.BLOCK.get(entry);
                ItemStack stack = new ItemStack(block);
                drawItemStack(matrixStack, stack, getItemX(i), getItemY(i), i);
            }
            else if (whitelist.type == Whitelist.Type.ITEM)
            {
                Item item = Registry.ITEM.get(entry);
                ItemStack stack = new ItemStack(item);
                drawItemStack(matrixStack, stack, getItemX(i), getItemY(i), i);
            }
            else if (whitelist.type == Whitelist.Type.ENTITY)
            {
                RenderSystem.enableLighting();
                LivingEntity entity = (LivingEntity) EntityUtil.VALID_ATTACK_TARGETS.get(entry);
                GuiUtil.renderEntityOnScreen(getButtonX(i) + ENTRY_BUTTON_SIZE / 2, getButtonY(i) + ENTRY_BUTTON_SIZE / 2 + 11, 20, 25, -10, entity);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            if (!isInWhitelist)
            {
                matrixStack.translate(0.0F, 0.0F, 200.0F);
                fill(matrixStack, buttonX, buttonY, buttonX + ENTRY_BUTTON_SIZE, buttonY + ENTRY_BUTTON_SIZE, 0x55000000);
                matrixStack.translate(0.0F, 0.0F, -200.0F);
            }
        }
    }

    private int getHoveredEntry(int mouseX, int mouseY)
    {
        for (int i = 0; i < maxForPage; i++)
        {
            if (GuiUtil.isMouseOver(mouseX, mouseY, getButtonX(i), getButtonY(i), ENTRY_BUTTON_SIZE, ENTRY_BUTTON_SIZE))
            {
                return i + index;
            }
        }
        return -1;
    }

    private int getItemX(int i)
    {
        return getButtonX(i) + 6;
    }
    private int getItemY(int i)
    {
        return getButtonY(i)+ 6;
    }

    private int getButtonX(int i)
    {
        return contentLeft + ENTRY_BUTTON_START_X + ((ENTRY_BUTTON_SIZE + ENTRY_BUTTON_GAP) * (i % 4));
    }
    private int getButtonY(int i)
    {
        return top + ENTRY_BUTTON_START_Y + ((ENTRY_BUTTON_SIZE + ENTRY_BUTTON_GAP) * (i / 4));
    }

    private void drawItemStack(MatrixStack matrixStack, ItemStack stack, int x, int y, int i)
    {
//        matrixStack.pushPose();
//        RenderSystem.disableDepthTest();
//        RenderSystem.enableLighting();
//        if (i % 4 == 1) matrixStack.translate(0.2F, 0.0F, 0.0F);
//        else if (i % 4 == 2) matrixStack.translate(-0.23F, 0.0F, 0.0F);
//        float scale = 1.2f;
//        matrixStack.scale(scale, scale, scale);
//        itemRenderer.renderAndDecorateItem(stack, (int)(x / scale), (int)(y / scale));
//        itemRenderer.renderGuiItemDecorations(font, stack, (int)(x / scale), (int)(y / scale));
//        if (i % 4 == 1) matrixStack.translate(-0.2F, 0.0F, 0.0F);
//        else if (i % 4 == 2) matrixStack.translate(0.23F, 0.0F, 0.0F);
//        matrixStack.scale(1 / scale, 1 / scale, 1 / scale);
//        matrixStack.popPose();
//        RenderSystem.enableDepthTest();

        RenderSystem.pushMatrix();
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
        Minecraft.getInstance().textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float)x, (float)y, 100.0F);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, 1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !itemRenderer.getModel(stack, null, null).usesBlockLight();
        if (flag) {
            RenderHelper.setupForFlatItems();
        }

        itemRenderer.render(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null));
        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            RenderHelper.setupFor3DItems();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int state)
    {
        if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getScrollX(), contentTop + SCROLL_START_Y, SCROLL_WIDTH, SCROLL_HEIGHT + SCROLL_LENGTH))
        {
            scrollPressed = true;
            int dy = (int) mouseY - contentTop - SCROLL_START_Y - SCROLL_HEIGHT / 2;
            dy = Math.min(dy, SCROLL_LENGTH);
            dy = Math.max(dy, 0);
            float percent = (float) dy / (float) SCROLL_LENGTH;
            page = (int)((percent * whitelist.size()) / ENTRIES_PER_PAGE);
            scroll = dy / (float) SCROLL_LENGTH;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOnX(), getOnY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            onPressed = true;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOffX(), getOffY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            offPressed = true;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getSwapX(), getSwapY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            swapPressed = true;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getLeftX(), getLeftY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            leftPressed = true;
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getRightX(), getRightY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            rightPressed = true;
        }
        else
        {
            return false;
        }

        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        int i = getHoveredEntry((int) mouseX, (int) mouseY);
        if (i != -1 && !scrollPressed && !onPressed && !offPressed && !swapPressed)
        {
            ResourceLocation entry = (ResourceLocation) whitelist.keySet().toArray()[i];
            whitelist.toggleEntry(entry);
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOnX(), getOnY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            whitelist.setAll(true);
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getOffX(), getOffY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            whitelist.setAll(false);
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getSwapX(), getSwapY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            whitelist.toggleAll();
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getLeftX(), getLeftY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            whitelistPage--;
            if (whitelistPage < 0) whitelistPage = goal.whitelists.size() - 1;
            whitelist = goal.whitelists.get(whitelistPage);
        }
        else if (GuiUtil.isMouseOver((int) mouseX, (int) mouseY, getRightX(), getRightY(), BUTTON_SIZE, BUTTON_SIZE))
        {
            whitelistPage++;
            if (whitelistPage > goal.whitelists.size() - 1) whitelistPage = 0;
            whitelist = goal.whitelists.get(whitelistPage);
        }

        scrollPressed = false;
        onPressed = false;
        offPressed = false;
        swapPressed = false;
        leftPressed = false;
        rightPressed = false;

        return false;
    }

    public boolean keyPressed(int keyCode, int i, int j)
    {
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        page -= scroll;

        return false;
    }
}
