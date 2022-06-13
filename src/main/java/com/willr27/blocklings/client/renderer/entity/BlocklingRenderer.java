package com.willr27.blocklings.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.willr27.blocklings.client.renderer.entity.layer.BlocklingHeldItemLayer;
import com.willr27.blocklings.client.renderer.entity.model.BlocklingModel;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * The renderer for the blockling.
 */
public class BlocklingRenderer extends MobRenderer<BlocklingEntity, BlocklingModel>
{
    /**
     * @param rendererManager the entity render manager.
     */
    public BlocklingRenderer(@Nonnull EntityRendererManager rendererManager)
    {
        super(rendererManager, new BlocklingModel(), 1.0f);

        addLayer(new BlocklingHeldItemLayer(this));
    }

    @Override
    public void render(@Nonnull BlocklingEntity blockling, float p_225623_2_, float p_225623_3_, @Nonnull MatrixStack p_225623_4_, @Nonnull IRenderTypeBuffer p_225623_5_, int p_225623_6_)
    {
        shadowRadius = blockling.getScale() * 0.5f;

        super.render(blockling, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }

    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(@Nonnull BlocklingEntity blockling)
    {
        return blockling.getBlocklingType() == blockling.getNaturalBlocklingType() ? blockling.getBlocklingType().entityTexture : blockling.getNaturalBlocklingType().getCombinedTexture(blockling.getBlocklingType(), blockling.getBlocklingTypeVariant());
    }
}
