package com.willr27.blocklings.entity.renderers.blockling;

import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.models.blockling.BlocklingModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class BlocklingRenderer extends MobRenderer<BlocklingEntity, BlocklingModel>
{
    public BlocklingRenderer(EntityRendererManager rendererManager)
    {
        super(rendererManager, new BlocklingModel(), 0.7f);

        addLayer(new BlocklingHeldItemLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BlocklingEntity blockling)
    {
        return blockling.getBlocklingType().entityTexture;
    }
}
