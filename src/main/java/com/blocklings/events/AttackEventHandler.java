package com.blocklings.events;

import com.blocklings.entities.EntityBlockling;
import com.blocklings.util.helpers.EntityHelper;
import com.blocklings.util.helpers.ToolHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttackEventHandler
{
    @SubscribeEvent
    public void e(LivingDropsEvent e)
    {
        EntityLivingBase damagee = e.getEntityLiving();
        if ((e.getSource().getTrueSource() instanceof EntityBlockling))
        {
            e.setCanceled(true);

            EntityBlockling blockling = (EntityBlockling)e.getSource().getTrueSource();
            int looting = 0;
            if (blockling.hasTool())
            {
                EnumHand hand = blockling.calculateAttackingHand();
                ItemStack weapon = blockling.getHeldItem(hand);
                List<NBTTagCompound> enchantmenTags = ToolHelper.getEnchantmentTagsFromTool(weapon);
                for (NBTTagCompound enchantmentTag : enchantmenTags)
                {
                    ToolHelper.Enchantment enchantment = ToolHelper.Enchantment.getEnchantmentFromTag(enchantmentTag);
                    if (enchantment == ToolHelper.Enchantment.LOOTING)
                    {
                        looting += enchantmentTag.getInteger("lvl");
                    }
                }
            }

            Random rand = new Random();
            int  lootingMultiplier = (int) ((float)looting * rand.nextFloat()) + 1;
            List<ItemStack> drops = new ArrayList();
            for (EntityItem item : e.getDrops())
            {
                ItemStack stack = item.getItem();
                stack.setCount(stack.getCount() * lootingMultiplier);
                drops.add(stack);
            }

            if (blockling.getTask() == EntityHelper.Task.HUNT)
            {
                List<ItemStack> toDrop = new ArrayList();
                for (ItemStack stack: drops)
                {
                    if (!blockling.inv.addItem(stack).isEmpty())
                    {
                        toDrop.add(stack);
                    }
                }
                for (ItemStack stack : toDrop)
                {
                    damagee.entityDropItem(stack, 0.0f);
                }
            }
            else
            {
                for (ItemStack stack : drops)
                {
                    damagee.entityDropItem(stack, 0.0f);
                }
            }
        }
    }
//
//    @SubscribeEvent
//    public void e(LivingAttackEvent e)
//    {
//
//    }
}
