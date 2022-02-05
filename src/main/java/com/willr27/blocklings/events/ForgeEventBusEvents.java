package com.willr27.blocklings.events;

import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.EntityUtil;
import com.willr27.blocklings.entity.entities.blockling.BlocklingEntity;
import com.willr27.blocklings.entity.entities.blockling.BlocklingType;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.skill.skills.CombatSkills;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
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
        BlocklingType.init();
        ToolUtil.init();
        EntityUtil.init((World) event.getWorld());
    }

    /**
     * Handles changing the scale/hitbox of a blockling.
     */
    @SubscribeEvent
    public static void onEntitySize(@Nonnull EntityEvent.Size event)
    {
        if (event.getEntity() instanceof BlocklingEntity)
        {
            float scale = ((BlocklingEntity) event.getEntity()).getScale();
            event.setNewSize(new EntitySize(scale * 1.0f, scale * 1.0f, true), true);
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
                    else if (blockling.getSkills().getSkill(CombatSkills.MONSTER_HUNTER).isBought() && event.getEntity() instanceof MobEntity)
                    {
                        itemStack.setCount(itemStack.getCount() * 2);
                    }

                    blockling.level.addFreshEntity(new ItemEntity(blockling.level, event.getEntity().getX(), event.getEntity().getY() + 0.2f, event.getEntity().getZ(), itemStack));
                }

                event.setCanceled(true);
            }
        }
    }
}
