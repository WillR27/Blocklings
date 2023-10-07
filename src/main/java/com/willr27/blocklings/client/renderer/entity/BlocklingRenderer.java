package com.willr27.blocklings.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.willr27.blocklings.client.renderer.entity.layer.BlocklingHeldItemLayer;
import com.willr27.blocklings.client.renderer.entity.model.BlocklingModel;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * The renderer for the blockling.
 */
public class BlocklingRenderer extends MobRenderer<BlocklingEntity, BlocklingModel>
{
    /**
     * @param context the entity renderer provider context.
     */
    public BlocklingRenderer(EntityRendererProvider.Context context)
    {
        super(context, new BlocklingModel(context.bakeLayer(BlocklingModel.LAYER_LOCATION)), 1.0f);

        addLayer(new BlocklingHeldItemLayer(this));
    }

    @Override
    public void render(@Nonnull BlocklingEntity blockling, float p_225623_2_, float p_225623_3_, @Nonnull PoseStack p_225623_4_, @Nonnull MultiBufferSource p_225623_5_, int p_225623_6_)
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
