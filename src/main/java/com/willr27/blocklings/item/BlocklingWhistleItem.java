package com.willr27.blocklings.item;

import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.sound.BlocklingsSounds;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * An item used to return a blockling back to the owner.
 */
public class BlocklingWhistleItem extends Item
{
    /**
     * The key used by a blockling whistle stack to reference a blockling uuid.
     */
    @Nonnull
    public static final String BLOCKLING_UUID_KEY = "blockling_uuid";

    /**
     * The key used by a blockling whistle stack to store the blockling's name.
     */
    @Nonnull
    public static final String BLOCKLING_NAME_KEY = "blockling_name";

    /**
     * Maps the instances of blocklings to their respective blockling whistles.
     */
    @Nonnull
    public static final Map<BlocklingEntity, Set<ItemStack>> BLOCKLINGS_TO_WHISTLES = new HashMap<>();

    /**
     * Default constructor.
     */
    public BlocklingWhistleItem()
    {
        super(new Properties()
                .tab(ItemGroup.TAB_MISC)
                .stacksTo(1)
                .durability(64)
                .setNoRepair());
    }

    /**
     * Sets the blockling the whistle points to.
     *
     * @param stack the stack to set the blockling on.
     * @param blockling the blockling to apply to the whistle.
     */
    public static void setBlockling(@Nonnull ItemStack stack, @Nonnull BlocklingEntity blockling)
    {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putUUID(BLOCKLING_UUID_KEY, blockling.getUUID());

        addStackToMap(blockling, stack);
    }

    /**
     * Adds the given whistle to the set of blockling whistles associated with the given blockling.
     *
     * @param blockling the blockling to associate the whistle with.
     * @param stack the stack to be associated with the given blockling.
     */
    public static void addStackToMap(@Nonnull BlocklingEntity blockling, @Nonnull ItemStack stack)
    {
        Set<ItemStack> stacks = BLOCKLINGS_TO_WHISTLES.getOrDefault(blockling, new HashSet<>());
        stacks.add(stack);
        BLOCKLINGS_TO_WHISTLES.put(blockling, stacks);
    }

    /**
     * Called when the blockling is destroyed from the world.
     * This should not be called every time the blockling is removed.
     * Only when the blockling is permanently removed from the world (e.g. death, packling).
     */
    public static void onBlocklingDestroyed(@Nonnull BlocklingEntity blockling)
    {
        Set<ItemStack> stacks = BLOCKLINGS_TO_WHISTLES.get(blockling);

        if (stacks != null)
        {
            for (ItemStack stack : stacks)
            {
                CompoundNBT stackTag = stack.getTag();

                if (stackTag != null)
                {
                    stackTag.remove(BLOCKLING_UUID_KEY);
                    stackTag.remove(BLOCKLING_NAME_KEY);
                }
            }
        }

        BLOCKLINGS_TO_WHISTLES.remove(blockling);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (world instanceof ServerWorld)
        {
            ServerWorld serverWorld = (ServerWorld) world;

            if (stack.hasTag())
            {
                CompoundNBT stackTag = stack.getTag();

                if (stackTag.hasUUID(BLOCKLING_UUID_KEY))
                {
                    UUID blocklingId = stackTag.getUUID(BLOCKLING_UUID_KEY);
                    Entity entity = serverWorld.getEntity(blocklingId);

                    if (entity instanceof BlocklingEntity)
                    {
                        BlocklingEntity blockling = (BlocklingEntity) entity;

                        if (player == blockling.getOwner())
                        {
                            blockling.teleportTo(player.getX(), player.getY(), player.getZ());

                            stack.hurtAndBreak(1, player, playerEntity -> playerEntity.broadcastBreakEvent(hand));

                            serverWorld.playSound(null, player.blockPosition(), BlocklingsSounds.BLOCKLING_WHISTLE.get(), SoundCategory.PLAYERS, 1.0f, 1.5f);
                        }
                    }
                }
            }
        }

        return ActionResult.fail(stack);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int something, boolean somethingElse)
    {
        super.inventoryTick(stack, world, entity, something, somethingElse);

        if (stack.hasTag() && stack.getTag().contains(BLOCKLING_UUID_KEY))
        {
            if (world.isClientSide)
            {
                BlocklingEntity blockling = findBlockling(stack, (ClientWorld) world);

                if (blockling != null)
                {
                    addStackToMap(blockling, stack);
                }

                String name = findBlocklingName(stack, (ClientWorld) world);

                if (name != null)
                {
                    stack.getTag().putString(BLOCKLING_NAME_KEY, name);
                }
            }
            else
            {
                BlocklingEntity blockling = (BlocklingEntity) ((ServerWorld) world).getEntity(stack.getTag().getUUID(BLOCKLING_UUID_KEY));

                if (blockling != null)
                {
                    addStackToMap(blockling, stack);

                    stack.getTag().putString(BLOCKLING_NAME_KEY, blockling.getCustomName().getString());
                }
            }
        }
    }

    /**
     * Finds the blockling in the world.
     *
     * @param stack the stack to find the blockling for.
     * @param world the client world.
     * @return the blockling.
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    private BlocklingEntity findBlockling(@Nonnull ItemStack stack, @Nonnull ClientWorld world)
    {
        if (stack.hasTag() && stack.getTag().contains(BLOCKLING_UUID_KEY))
        {
            Int2ObjectMap<Entity> entitiesById = ObfuscationReflectionHelper.getPrivateValue(ClientWorld.class, world, "field_217429_b");

            Entity blockling = entitiesById.values().stream().filter(e -> e.getUUID().equals(stack.getTag().getUUID(BLOCKLING_UUID_KEY))).findFirst().orElse(null);

            return (BlocklingEntity) blockling;
        }

        return null;
    }

    /**
     * Finds the blockling's name on the client.
     *
     * @param stack the stack to find the name for.
     * @param world the client world.
     * @return the blockling's name.
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    private String findBlocklingName(@Nonnull ItemStack stack, @Nonnull ClientWorld world)
    {
        BlocklingEntity blockling = findBlockling(stack, world);

        if (blockling != null)
        {
            ITextComponent customName = blockling.getCustomName();

            if (customName != null)
            {
                return customName.getString();
            }
        }

        return null;
    }

    /**
     * Finds the blockling's location on the client.
     *
     * @param stack the stack to find the location for.
     * @param world the client world.
     * @return the blockling's location string.
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    private String findBlocklingLocation(@Nonnull ItemStack stack, @Nonnull ClientWorld world)
    {
        BlocklingEntity blockling = findBlockling(stack, world);

        if (blockling != null)
        {
            return String.format("%d %d %d", (int) blockling.getX(), (int) blockling.getY(), (int) blockling.getZ());
        }

        return null;
    }

    @Nonnull
    @Override
    public ITextComponent getName(@Nonnull ItemStack stack)
    {
        if (stack.hasTag() && stack.getTag().contains(BLOCKLING_NAME_KEY))
        {
            return new StringTextComponent(TextFormatting.LIGHT_PURPLE + super.getName(stack).getString() + " (" + stack.getTag().getString(BLOCKLING_NAME_KEY) + ")");
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag)
    {
        if (world != null)
        {
            String location = findBlocklingLocation(stack, (ClientWorld) world);

            if (location != null)
            {
                tooltip.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent(getDescriptionId() + ".location").getString() + location));
            }
        }

        super.appendHoverText(stack, world, tooltip, flag);
    }
}
