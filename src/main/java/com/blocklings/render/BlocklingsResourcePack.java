package com.blocklings.render;

import com.blocklings.main.Blocklings;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class BlocklingsResourcePack implements IResourcePack
{
    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException
    {
        return new FileInputStream(getResourceLocation(location));
    }

    @Override
    public boolean resourceExists(ResourceLocation location)
    {
        return getResourceLocation(location).exists();
    }

    private File getResourceLocation(ResourceLocation location)
    {
        String path = location.getResourcePath();
        String name = path.substring(path.lastIndexOf('/') + 1);
        return new File("D:/Modding/New Folder/" + name);
    }

    @Override
    public Set<String> getResourceDomains()
    {
        return ImmutableSet.<String>of(Blocklings.MODID);
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
    {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException
    {
        return null;
    }

    @Override
    public String getPackName()
    {
        return "Blocklings Custom Textures";
    }
}
