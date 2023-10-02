package com.willr27.blocklings.event;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.blockling.BlocklingType;
import com.willr27.blocklings.entity.blockling.skill.skills.CombatSkills;
import com.willr27.blocklings.item.BlocklingWhistleItem;
import com.willr27.blocklings.util.BlockUtil;
import com.willr27.blocklings.util.EntityUtil;
import com.willr27.blocklings.util.ToolUtil;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

/**
 * Handles any Forge events.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBusEvents
{
    /**
     * Handles any setup that needs to take place when the world loads.
     */
    @SubscribeEvent
    public static void onWorldLoad(@Nonnull WorldEvent.Load event)
    {
        EntityUtil.latestWorld = (World) event.getWorld();
        BlockUtil.latestWorld = (World) event.getWorld();

        BlocklingType.init();
        ToolUtil.init();

        BlocklingWhistleItem.BLOCKLINGS_TO_WHISTLES.clear();
    }

    /**
     * Handles changing the scale/hitbox of a blockling.
     */
    @SubscribeEvent
    public static void onEntitySize(@Nonnull EntityEvent.Size event)
    {
        if (event.getEntity() instanceof BlocklingEntity)
        {
            BlocklingEntity blockling = (BlocklingEntity) event.getEntity();
            float scale = blockling.getScale();

            // If we have just spawned, the scale will not have been set. Assume then any scale less than or equal to 0
            // is a new blockling and set the initial spawn scale. If this still has issues, we could set a "just spawned"
            // flag in the constructor and check that instead. This was originally a fix for the Shrink mod reading the
            // blocklings dimensions as 0.0 x 0.0 and making them appear invisible on the client.
            if (scale <= 0.0f)
            {
                blockling.setScale(blockling.getRandom().nextFloat() * 0.5f + 0.45f, false);
            }

            scale = blockling.getScale();

            event.setNewSize(new EntitySize(scale * 1.0f, scale * 1.0f, true), true);
        }
    }

    @SubscribeEvent
    public static void onLootingLevelEvent(@Nonnull LootingLevelEvent event)
    {
        if (event.getDamageSource() == null)
        {
            return;
        }

        if (event.getDamageSource().getEntity() instanceof BlocklingEntity)
        {
            BlocklingEntity blockling = (BlocklingEntity) event.getDamageSource().getEntity();

            if (blockling != null)
            {
                if (blockling.getNaturalBlocklingType() == BlocklingType.LAPIS || blockling.getBlocklingType() == BlocklingType.LAPIS)
                {
                    event.setLootingLevel(event.getLootingLevel() + 1);
                }
            }
        }
    }

    /**
     * Handles an entity dropping items.
     */
    @SubscribeEvent
    public static void onLivingDropsEvent(@Nonnull LivingDropsEvent event)
    {
        if (event.getSource().getEntity() instanceof BlocklingEntity)
        {
            BlocklingEntity blockling = (BlocklingEntity) event.getSource().getEntity();

            if (blockling.getSkills().getSkill(CombatSkills.HUNTER).isBought() && blockling.wasLastAttackHunt)
            {
                for (ItemEntity itemEntity : event.getDrops())
                {
                    ItemStack itemStack = blockling.getEquipment().addItem(itemEntity.getItem());

                    if (blockling.getSkills().getSkill(CombatSkills.ANIMAL_HUNTER).isBought() && event.getEntity() instanceof AnimalEntity)
                    {
                        itemStack.setCount(itemStack.getCount() * 2);
                    }
                    else if (blockling.getSkills().getSkill(CombatSkills.MONSTER_HUNTER).isBought() && event.getEntity() instanceof MonsterEntity)
                    {
                        itemStack.setCount(itemStack.getCount() * 2);
                    }

                    itemStack = blockling.getEquipment().addItem(itemStack);

                    if (!itemStack.isEmpty())
                    {
                        blockling.dropItemStack(itemStack);
                    }
                }

                event.setCanceled(true);
            }
        }
    }
}
