package com.willr27.blocklings.client.gui.control.controls.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.willr27.blocklings.client.gui.control.controls.BlockControl;
import com.willr27.blocklings.client.gui.control.event.events.input.MouseReleasedEvent;
import com.willr27.blocklings.client.gui.util.GuiUtil;
import com.willr27.blocklings.client.gui.util.ScissorStack;
import com.willr27.blocklings.util.BlocklingsTranslatableComponent;
import com.willr27.blocklings.util.event.IEvent;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A control used to select the side of a block.
 */
@OnlyIn(Dist.CLIENT)
public class BlockSideSelectionControl extends BlockControl
{
    /**
     * The selected directions in priority order.
     */
    @Nonnull
    private List<Direction> selectedDirections = new ArrayList<>();

    /**
     */
    public BlockSideSelectionControl()
    {
        super();
    }

    @Override
    protected void onRender(@Nonnull PoseStack poseStack, @Nonnull ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks)
    {
        super.onRender(poseStack, scissorStack, mouseX, mouseY, partialTicks);

        x += (getWidth() / 2) * getScaleX();
        y += (getHeight() / 2) * getScaleY();

        for (int i = 0; i < selectedDirections.size(); i++)
        {
            Direction direction = selectedDirections.get(i);
            String s = Integer.toString(i + 1);

            switch (direction)
            {
                case NORTH:
                    renderTextOnSide(s, x, y, z, scale, Quaternion.ONE.copy(), 0xffffffff);
                    break;
                case EAST:
                    renderTextOnSide(s, x, y, z, scale, Vector3f.YP.rotationDegrees(90.0f), 0xffffffff);
                    break;
                case SOUTH:
                    renderTextOnSide(s, x, y, z, scale, Vector3f.YP.rotationDegrees(180.0f), 0xffffffff);
                    break;
                case WEST:
                    renderTextOnSide(s, x, y, z, scale, Vector3f.YP.rotationDegrees(-90.0f), 0xffffffff);
                    break;
                case UP:
                    renderTextOnSide(s ,x, y, z, scale, Vector3f.XP.rotationDegrees(90.0f), 0xffffffff);
                    break;
                case DOWN:
                    renderTextOnSide(s, x, y, z, scale, Vector3f.XP.rotationDegrees(-90.0f), 0xffffffff);
                    break;
            }
        }

        Direction mouseOverDirection = getDirectionMouseIsOver();

        if (mouseOverDirection != null)
        {
            switch (mouseOverDirection)
            {
                case NORTH:
                    renderRectangleOnSide(x, y, z, scale, Quaternion.ONE.copy(), 0x55ffffff);
                    break;
                case SOUTH:
                    renderRectangleOnSide(x, y, z, scale, Vector3f.YP.rotationDegrees(180.0f), 0x55ffffff);
                    break;
                case WEST:
                    renderRectangleOnSide(x, y, z, scale, Vector3f.YP.rotationDegrees(-90.0f), 0x55ffffff);
                    break;
                case EAST:
                    renderRectangleOnSide(x, y, z, scale, Vector3f.YP.rotationDegrees(90.0f), 0x55ffffff);
                    break;
                case UP:
                    renderRectangleOnSide(x, y, z, scale, Vector3f.XP.rotationDegrees(90.0f), 0x55ffffff);
                    break;
                case DOWN:
                    renderRectangleOnSide(x, y, z, scale, Vector3f.XP.rotationDegrees(-90.0f), 0x55ffffff);
                    break;
            }
        }
    }

    /**
     * Calculates the dot product of a segment and a plane.
     *
     * @param segmentPoint the start/end point of the segment.
     * @param planePoint a point on the plane.
     * @param planeNormal the normal of the plane.
     * @return the dot product.
     */
    private float calcSegmentPlaneDotProduct(@Nonnull Vector4f segmentPoint, @Nonnull Vector4f planePoint, @Nonnull Vector4f planeNormal)
    {
        Vector3f segmentMinusPlanePoint = new Vector3f(segmentPoint.x() - planePoint.x(), segmentPoint.y() - planePoint.y(), segmentPoint.z() - planePoint.z());
        Vector3f planeNormal3f = new Vector3f(planeNormal.x(), planeNormal.y(), planeNormal.z());

        return segmentMinusPlanePoint.dot(planeNormal3f);
    }

    private void renderTextOnSide(String text, float x, float y, float z, float scale, @Nonnull Quaternion rotation, int colour)
    {
        RenderSystem.disableCull();
        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, z);
        poseStack.mulPose(new Quaternion(rotationQuat.i(), -rotationQuat.j(), rotationQuat.k(), -rotationQuat.r()));
        poseStack.mulPose(rotation);
        poseStack.scale(scale / 24.0f, scale / 24.0f, 1.0f);
        poseStack.translate(-GuiUtil.get().getTextWidth(text) / 2.0, -GuiUtil.get().getLineHeight() / 2.0, scale / 2.0 + 1.0 -0.03);
        RenderSystem.enableDepthTest();
        renderShadowedText(poseStack, new TextComponent(text).getVisualOrderText(), 0, 0, colour);
        RenderSystem.enableCull();
    }

    private void renderRectangleOnSide(float x, float y, float z, float scale, @Nonnull Quaternion rotation, int colour)
    {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, z);
        poseStack.mulPose(new Quaternion(rotationQuat.i(), -rotationQuat.j(), rotationQuat.k(), -rotationQuat.r()));
        poseStack.mulPose(rotation);
        poseStack.translate(0.0f, 0.0f, scale / 2 + 0.01);
        RenderSystem.enableDepthTest();
        renderCenteredRectangle(poseStack, 0.0, 0.0, scale, scale, colour);
    }

    @Override
    public void onRenderTooltip(@Nonnull PoseStack poseStack, double mouseX, double mouseY, float partialTicks)
    {
        Direction mouseOverDirection = getDirectionMouseIsOver();

        if (mouseOverDirection != null)
        {
            switch (mouseOverDirection)
            {
                case NORTH:
                    renderTooltip(poseStack, mouseX, mouseY, new BlocklingsTranslatableComponent("direction.front"));
                    break;
                case SOUTH:
                    renderTooltip(poseStack, mouseX, mouseY, new BlocklingsTranslatableComponent("direction.back"));
                    break;
                case WEST:
                    renderTooltip(poseStack, mouseX, mouseY, new BlocklingsTranslatableComponent("direction.left"));
                    break;
                case EAST:
                    renderTooltip(poseStack, mouseX, mouseY, new BlocklingsTranslatableComponent("direction.right"));
                    break;
                case UP:
                    renderTooltip(poseStack, mouseX, mouseY, new BlocklingsTranslatableComponent("direction.top"));
                    break;
                case DOWN:
                    renderTooltip(poseStack, mouseX, mouseY, new BlocklingsTranslatableComponent("direction.bottom"));
                    break;
            }
        }
    }

    @Override
    protected void onMouseReleased(@Nonnull MouseReleasedEvent e)
    {
        if (isPressed() && ((dragAmount < 5.0 && e.button == GLFW.GLFW_MOUSE_BUTTON_1) || dragAmount == 0.0))
        {
            Direction mouseOverDirection = getDirectionMouseIsOver();

            if (mouseOverDirection != null)
            {
                List<Direction> oldSelectedDirections = new ArrayList<>(selectedDirections);

                if (selectedDirections.contains(mouseOverDirection))
                {
                    selectedDirections.remove(mouseOverDirection);
                }
                else
                {
                    selectedDirections.add(mouseOverDirection);
                }

                eventBus.post(this, new DirectionListChangedEvent(oldSelectedDirections, new ArrayList<>(selectedDirections)));
            }
        }
    }

    @Nullable
    public Direction getDirectionMouseIsOver()
    {
        float screenMouseX = (float) (GuiUtil.get().getPixelMouseX() / getGuiScale());
        float screenMouseY = (float) (GuiUtil.get().getPixelMouseY() / getGuiScale());

        float scale = (float) (Math.min(getWidth(), getHeight()) * getScaleX()) * getBlockScale();
        float width = scale / 2.0f;
        float widthSquared = width * width;
        double cubeDiagFromCenterToCorner = Math.sqrt(widthSquared + widthSquared + widthSquared);
        int z = (int) (getRenderZ() + cubeDiagFromCenterToCorner);

        PoseStack pointPoseStack = new PoseStack();
        pointPoseStack.translate(x, y, z);
        pointPoseStack.mulPose(new Quaternion(rotationQuat.i(), -rotationQuat.j(), rotationQuat.k(), -rotationQuat.r()));
        PoseStack normalPoseStack = new PoseStack();
        normalPoseStack.mulPose(new Quaternion(rotationQuat.i(), -rotationQuat.j(), rotationQuat.k(), -rotationQuat.r()));

        Vector4f frontPlanePoint = new Vector4f(0, 0, scale/2.0f, 1);
        Vector4f frontPlaneNormal = new Vector4f(0, 0, 1, 1);
        Vector4f backPlanePoint = new Vector4f(0, 0, -scale/2.0f, 1);
        Vector4f backPlaneNormal = new Vector4f(0, 0, -1, 1);
        Vector4f topPlanePoint = new Vector4f(0, -scale/2.0f, 0, 1);
        Vector4f topPlaneNormal = new Vector4f(0, -1, 0, 1);
        Vector4f bottomPlanePoint = new Vector4f(0, scale/2.0f, 0, 1);
        Vector4f bottomPlaneNormal = new Vector4f(0, 1, 0, 1);
        Vector4f leftPlanePoint = new Vector4f(-scale/2.0f, 0, 0, 1);
        Vector4f leftPlaneNormal = new Vector4f(-1, 0, 0, 1);
        Vector4f rightPlanePoint = new Vector4f(scale/2.0f, 0, 0, 1);
        Vector4f rightPlaneNormal = new Vector4f(1, 0, 0, 1);
        frontPlanePoint.transform(pointPoseStack.last().pose());
        frontPlaneNormal.transform(normalPoseStack.last().pose());
        backPlanePoint.transform(pointPoseStack.last().pose());
        backPlaneNormal.transform(normalPoseStack.last().pose());
        topPlanePoint.transform(pointPoseStack.last().pose());
        topPlaneNormal.transform(normalPoseStack.last().pose());
        bottomPlanePoint.transform(pointPoseStack.last().pose());
        bottomPlaneNormal.transform(normalPoseStack.last().pose());
        leftPlanePoint.transform(pointPoseStack.last().pose());
        leftPlaneNormal.transform(normalPoseStack.last().pose());
        rightPlanePoint.transform(pointPoseStack.last().pose());
        rightPlaneNormal.transform(normalPoseStack.last().pose());

        float step = scale / 100.0f;
        float max = (float) cubeDiagFromCenterToCorner + z;

        for (float f = max + step; f > -max - step; f-=step)
        {
            Vector4f segmentStart = new Vector4f(screenMouseX, screenMouseY, f, 1);
            Vector4f segmentEnd = new Vector4f(screenMouseX, screenMouseY, f-step, 1);

            float startFrontPlaneDot = calcSegmentPlaneDotProduct(segmentStart, frontPlanePoint, frontPlaneNormal);
            float endFrontPlaneDot = calcSegmentPlaneDotProduct(segmentEnd, frontPlanePoint, frontPlaneNormal);
            float startBackPlaneDot = calcSegmentPlaneDotProduct(segmentStart, backPlanePoint, backPlaneNormal);
            float endBackPlaneDot = calcSegmentPlaneDotProduct(segmentEnd, backPlanePoint, backPlaneNormal);
            float startTopPlaneDot = calcSegmentPlaneDotProduct(segmentStart, topPlanePoint, topPlaneNormal);
            float endTopPlaneDot = calcSegmentPlaneDotProduct(segmentEnd, topPlanePoint, topPlaneNormal);
            float startBottomPlaneDot = calcSegmentPlaneDotProduct(segmentStart, bottomPlanePoint, bottomPlaneNormal);
            float endBottomPlaneDot = calcSegmentPlaneDotProduct(segmentEnd, bottomPlanePoint, bottomPlaneNormal);
            float startLeftPlaneDot = calcSegmentPlaneDotProduct(segmentStart, leftPlanePoint, leftPlaneNormal);
            float endLeftPlaneDot = calcSegmentPlaneDotProduct(segmentEnd, leftPlanePoint, leftPlaneNormal);
            float startRightPlaneDot = calcSegmentPlaneDotProduct(segmentStart, rightPlanePoint, rightPlaneNormal);
            float endRightPlaneDot = calcSegmentPlaneDotProduct(segmentEnd, rightPlanePoint, rightPlaneNormal);

            if (endFrontPlaneDot < 0 && endBackPlaneDot < 0 && endTopPlaneDot < 0 && endBottomPlaneDot < 0 && endLeftPlaneDot < 0 && endRightPlaneDot < 0)
            {
                if (startFrontPlaneDot * endFrontPlaneDot < 0)
                {
                    return Direction.NORTH;
                }
                else if (startBackPlaneDot * endBackPlaneDot < 0)
                {
                    return Direction.SOUTH;
                }
                else if (startTopPlaneDot * endTopPlaneDot < 0)
                {
                    return Direction.UP;
                }
                else if (startBottomPlaneDot * endBottomPlaneDot < 0)
                {
                    return Direction.DOWN;
                }
                else if (startLeftPlaneDot * endLeftPlaneDot < 0)
                {
                    return Direction.WEST;
                }
                else if (startRightPlaneDot * endRightPlaneDot < 0)
                {
                    return Direction.EAST;
                }

                break;
            }
        }

        return null;
    }

    /**
     * @return the list of directions that are currently selected.
     */
    @Nonnull
    public List<Direction> getSelectedDirections()
    {
        return selectedDirections;
    }

    /**
     * Sets the list of directions that are currently selected.
     *
     * @param selectedDirections the list of directions that are currently selected.
     */
    public void setSelectedDirections(@Nonnull List<Direction> selectedDirections)
    {
        this.selectedDirections = selectedDirections;
    }

    /**
     * An event that is fired when the list of directions is changed.
     */
    public static class DirectionListChangedEvent implements IEvent
    {
        /**
         * The list of directions that were previously selected.
         */
        @Nonnull
        public final List<Direction> oldDirections;

        /**
         * The list of directions that are now selected.
         */
        @Nonnull
        public final List<Direction> newDirections;

        /**
         * @param oldDirections The list of directions that were previously selected.
         * @param newDirections The list of directions that are now selected.
         */
        public DirectionListChangedEvent(@Nonnull List<Direction> oldDirections, @Nonnull List<Direction> newDirections)
        {
            this.oldDirections = oldDirections;
            this.newDirections = newDirections;
        }
    }
}
