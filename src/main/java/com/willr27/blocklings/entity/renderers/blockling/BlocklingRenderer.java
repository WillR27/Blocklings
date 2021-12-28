package com.willr27.blocklings.entity.renderers.blockling;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.models.blockling.BlocklingModel;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class BlocklingRenderer extends MobRenderer<BlocklingEntity, BlocklingModel>
{
    public BlocklingRenderer(EntityRendererManager rendererManager)
    {
        super(rendererManager, new BlocklingModel(), 1.0f);

        addLayer(new BlocklingHeldItemLayer(this));
    }

    @Override
    public void render(BlocklingEntity blockling, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_)
    {
        shadowRadius = blockling.getScale() * 0.5f;

        super.render(blockling, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }

    @Override
    public ResourceLocation getTextureLocation(BlocklingEntity blockling)
    {
        return blockling.getBlocklingType() == blockling.getOriginalBlocklingType() ? blockling.getBlocklingType().entityTexture : blockling.getOriginalBlocklingType().getCombinedTexture(blockling.getBlocklingType());
    }
}
