package com.blocklings.render;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.models.ModelBlockling;
import com.blocklings.util.helpers.EntityHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public class RenderBlockling extends RenderLiving<EntityBlockling>
{
    public static final Factory FACTORY = new Factory();

    public RenderBlockling(RenderManager rendermanagerIn)
    {
        super(rendermanagerIn, new ModelBlockling(), 0.3F);
        addLayer(new LayerHeldItem(this));
    }

    @Override
    protected void preRenderCallback(EntityBlockling blockling, float partialTicks)
    {
        float val = EntityHelper.BASE_SCALE * blockling.getBlocklingScale();
        GlStateManager.scale(val, val, val);
    }

    @Override
    protected void renderLivingLabel(EntityBlockling blockling, String label, double x, double y, double z, int maxDistance)
    {
        if (!blockling.isInGui) super.renderLivingLabel(blockling, label, x, y, z, maxDistance);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityBlockling entity)
    {
        return entity.blocklingType.entityTexture;
    }

    public static class Factory implements IRenderFactory<EntityBlockling>
    {
        @Override
        public Render<? super EntityBlockling> createRenderFor(RenderManager manager)
        {
            return new RenderBlockling(manager);
        }

    }
}