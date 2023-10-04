package com.willr27.blocklings.entity.blockling.goal.config.whitelist;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.goal.BlocklingGoal;
import com.willr27.blocklings.network.messages.WhitelistAllMessage;
import com.willr27.blocklings.network.messages.WhitelistIsUnlockedMessage;
import com.willr27.blocklings.network.messages.WhitelistSingleMessage;
import com.willr27.blocklings.util.BlocklingsTranslatableComponent;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.PacketBufferUtils;
import com.willr27.blocklings.util.Version;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class GoalWhitelist extends Whitelist<ResourceLocation> implements IReadWriteNBT
{
    public final UUID id;
    public final String key;
    public final Whitelist.Type type;
    public final BlocklingGoal goal;
    public final BlocklingEntity blockling;
    public final TranslatableComponent name;

    private boolean isUnlocked = true;

    public GoalWhitelist(String id, String key, Whitelist.Type type, BlocklingGoal goal)
    {
        this.id = UUID.fromString(id);
        this.key = key;
        this.type = type;
        this.blockling = goal.blockling;
        this.goal = goal;
        this.name = new BlocklingsTranslatableComponent("whitelist." + key);
    }

    public GoalWhitelist(String id, String key, Whitelist.Type type, BlocklingGoal goal, Map whitelist)
    {
        this(id, key, type, goal);

        clear();
        putAll(whitelist);
    }

    public void setWhitelist(Whitelist<ResourceLocation> whitelist, boolean sync)
    {
        clear();
        putAll(whitelist);

        if (sync)
        {
            new WhitelistAllMessage(blockling, goal.id, goal.whitelists.indexOf(this), this).sync();
        }
    }

    @Nonnull
    public CompoundTag writeToNBT(CompoundTag whitelistTag)
    {
        whitelistTag.putBoolean("is_unlocked", isUnlocked);

        for (Map.Entry<ResourceLocation, Boolean> entry : entrySet())
        {
            whitelistTag.putBoolean(entry.getKey().toString(), entry.getValue());
        }

        return whitelistTag;
    }

    @Nonnull
    public void readFromNBT(@Nonnull CompoundTag whitelistTag, @Nonnull Version tagVersion)
    {
        setIsUnlocked(whitelistTag.getBoolean("is_unlocked"), false);

        for (Map.Entry<ResourceLocation, Boolean> entry : entrySet())
        {
            if (whitelistTag.contains(entry.getKey().toString()))
            {
                put(entry.getKey(), whitelistTag.getBoolean(entry.getKey().toString()));
            }
        }
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBoolean(isUnlocked);
        buf.writeInt(size());

        for (Map.Entry<ResourceLocation, Boolean> entry : entrySet())
        {
            PacketBufferUtils.writeString(buf, entry.getKey().toString());
            buf.writeBoolean(entry.getValue());
        }
    }

    public void decode(FriendlyByteBuf buf)
    {
        setIsUnlocked(buf.readBoolean(), false);

        int size = buf.readInt();

        for (int i = 0; i < size; i++)
        {
            put(new ResourceLocation(PacketBufferUtils.readString(buf)), buf.readBoolean());
        }
    }

    public boolean isEntryWhitelisted(Object entry)
    {
        Boolean result = null;
        if (entry instanceof Block) result = get(((Block)entry).getRegistryName());
        else if (entry instanceof Item) result = get(((Item)entry).getRegistryName());
        else if (entry instanceof Entity) result = get(((Entity)entry).getType().getRegistryName());
        else result = get(entry);

        return result != null ? result : false;
    }

    public boolean isEntryBlacklisted(Object entry)
    {
        return !isEntryWhitelisted(entry);
    }

    @Override
    public void setEntry(ResourceLocation entry, boolean value)
    {
        setEntry(entry, value, true);
    }

    public void setEntry(ResourceLocation entry, boolean value, boolean sync)
    {
        super.setEntry(entry, value);

        if (sync)
        {
            new WhitelistSingleMessage(blockling, goal.id, goal.whitelists.indexOf(this), entry, value).sync();
        }
    }

    @Override
    public void toggleEntry(ResourceLocation entry)
    {
        toggleEntry(entry, true);
    }

    public void toggleEntry(ResourceLocation entry, boolean sync)
    {
        setEntry(entry, !get(entry), sync);
    }

    @Override
    public void setAll(boolean value)
    {
        setAll(value, true);
    }

    public void setAll(boolean value, boolean sync)
    {
        for (ResourceLocation entry : keySet())
        {
            setEntry(entry, value, false);
        }

        if (sync)
        {
            new WhitelistAllMessage(blockling, goal.id, goal.whitelists.indexOf(this), this).sync();
        }
    }

    @Override
    public void toggleAll()
    {
        toggleAll(true);
    }

    public void toggleAll(boolean sync)
    {
        for (ResourceLocation entry : keySet())
        {
            toggleEntry(entry, false);
        }

        if (sync)
        {
            new WhitelistAllMessage(blockling, goal.id, goal.whitelists.indexOf(this), this).sync();
        }
    }

    public boolean isUnlocked()
    {
        return isUnlocked;
    }

    public void setIsUnlocked(boolean isUnlocked)
    {
        setIsUnlocked(isUnlocked, true);
    }

    public void setIsUnlocked(boolean isUnlocked, boolean sync)
    {
        this.isUnlocked = isUnlocked;

        if (sync)
        {
            new WhitelistIsUnlockedMessage(blockling, goal.id, goal.whitelists.indexOf(this), isUnlocked).sync();
        }
    }
}
