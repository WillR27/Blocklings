package com.willr27.blocklings.entity.entities.blockling;

import com.google.common.collect.Iterables;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.action.BlocklingActions;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.gui.BlocklingGuiHandler;
import com.willr27.blocklings.inventory.inventories.EquipmentInventory;
import com.willr27.blocklings.util.IReadWriteNBT;
import com.willr27.blocklings.util.ObjectUtil;
import com.willr27.blocklings.util.ToolUtil;
import com.willr27.blocklings.item.items.BlocklingItem;
import com.willr27.blocklings.item.items.BlocklingWhistleItem;
import com.willr27.blocklings.network.messages.BlocklingAttackTargetMessage;
import com.willr27.blocklings.network.messages.BlocklingNameMessage;
import com.willr27.blocklings.network.messages.BlocklingScaleMessage;
import com.willr27.blocklings.network.messages.BlocklingTypeMessage;
import com.willr27.blocklings.skill.BlocklingSkills;
import com.willr27.blocklings.skill.skills.*;
import com.willr27.blocklings.task.BlocklingTasks;
import com.willr27.blocklings.task.Task;
import com.willr27.blocklings.util.Version;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiPredicate;

import static com.willr27.blocklings.item.items.BlocklingsItems.BLOCKLING_WHISTLE;

/**
 * The blockling entity.
 */
public class BlocklingEntity extends TameableEntity implements IEntityAdditionalSpawnData, IReadWriteNBT
{
    /**
     * The blockling type the blockling spawned as.
     */
    @Nonnull
    private BlocklingType originalBlocklingType = BlocklingType.GRASS;

    /**
     * The blockling type the blockling has been changed to.
     */
    @Nonnull
    private BlocklingType blocklingType = BlocklingType.GRASS;

    /**
     * The variant used to determine how a blockling blends with its original blockling type.
     */
    private int blocklingTypeVariant = 0;

    /**
     * The blockling's attribute manager (called stats because attributes is already thing in vanilla).
     */
    @Nonnull
    private final BlocklingAttributes stats = new BlocklingAttributes(this);

    /**
     * The blockling's skills manager.
     */
    @Nonnull
    private final BlocklingSkills skills = new BlocklingSkills(this);

    /**
     * The blockling's task manager.
     */
    @Nonnull
    private final BlocklingTasks tasks = new BlocklingTasks(this);

    /**
     * The blockling's action manager.
     */
    @Nonnull
    private final BlocklingActions actions = new BlocklingActions(this);

    /**
     * The blockling's equipment inventory.
     */
    @Nonnull
    private final EquipmentInventory equipmentInv = new EquipmentInventory(this);

    /**
     * Handles opening screens and containers.
     */
    @Nonnull
    public final BlocklingGuiHandler guiHandler = new BlocklingGuiHandler(this);

    /**
     * The blockling's scale (size).
     */
    private float scale;

    /**
     * Tracks how many attacks have occurred within 100 ticks of each other.
     * Used by the momentum skill.
     */
    private int attacksRecently = 0;

    /**
     * Tracks how many ores have been mined within 100 ticks of each other.
     * Used by the momentum skill.
     */
    private int oresMinedRecently = 0;

    /**
     * Tracks how many logs have been chopped within 100 ticks of each other.
     * Used by the momentum skill.
     */
    private int logsChoppedRecently = 0;

    /**
     * Tracks how many crops have been harvested within 100 ticks of each other.
     * Used by the momentum skill.
     */
    private int cropsHarvestedRecently = 0;

    /**
     * Whether the last attack the blockling performed was via a hunt task.
     * NOT synced to the client/server.
     */
    public boolean wasLastAttackHunt = false;

    /**
     * Used to track whether the player has released crouch after interacting (changing blockling type).
     * This stops a player picking up a blockling immediately after changing its type by accident.
     * Should be replaced with a capability on the player to tell when they have stopped using an item.
     */
    private boolean hasPlayerResetCrouchBetweenInteractions = true;

    /**
     * @param type the blockling entity type.
     * @param world the world the blockling is in.
     */
    public BlocklingEntity(@Nonnull EntityType<? extends BlocklingEntity> type, @Nonnull World world)
    {
        super(type, world);

        stats.initUpdateCallbacks();

        // Set up any values that are determined randomly here
        // So that we can sync them up using read/writeSpawnData
        if (!level.isClientSide())
        {
            blocklingTypeVariant = getRandom().nextInt(3);
            originalBlocklingType = BlocklingType.TYPES.get(getRandom().nextInt(BlocklingType.TYPES.size()));
            setBlocklingType(originalBlocklingType, false);

            setScale(getRandom().nextFloat() * 0.5f + 0.45f, false);

            stats.init();
        }

        equipmentInv.updateToolAttributes();

        setHealth(getMaxHealth());
    }

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeModifierMap.MutableAttribute createAttributes()
    {
        return MobEntity.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 0.0).add(Attributes.ATTACK_SPEED, 0.0);
    }

    @Override
    @Nullable
    public ILivingEntityData finalizeSpawn(@Nonnull IServerWorld world, @Nonnull DifficultyInstance difficultyInstance, @Nonnull SpawnReason spawnReason, @Nullable ILivingEntityData entityData, @Nullable CompoundNBT tag)
    {
        if (spawnReason == SpawnReason.SPAWN_EGG && tag != null)
        {
            CompoundNBT blocklingTag = (CompoundNBT) tag.get("blockling");

            if (blocklingTag != null)
            {
                readFromNBT(blocklingTag, ObjectUtil.coalesce(new Version(blocklingTag.getString("blocklings_version")), Blocklings.VERSION));
            }
        }

        return super.finalizeSpawn(world, difficultyInstance, spawnReason, entityData, tag);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT tag)
    {
        super.addAdditionalSaveData(tag);

        CompoundNBT blocklingTag = new CompoundNBT();

        blocklingTag.putString("blocklings_version", Blocklings.VERSION.toString());

        writeToNBT(blocklingTag);

        tag.put("blockling", blocklingTag);
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT blocklingTag)
    {
        blocklingTag.putString("original_type", originalBlocklingType.key);
        blocklingTag.putString("type", blocklingType.key);
        blocklingTag.putInt("variant", blocklingTypeVariant);
        blocklingTag.putFloat("scale", scale);

        blocklingTag.put("equipment_inv", equipmentInv.writeToNBT());
        blocklingTag.put("attributes", stats.writeToNBT());
        blocklingTag.put("tasks", tasks.writeToNBT());
        blocklingTag.put("skills", skills.writeToNBT());

        return blocklingTag;
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT tag)
    {
        super.readAdditionalSaveData(tag);

        CompoundNBT blocklingTag = tag.getCompound("blockling");

        if (blocklingTag != null)
        {
            readFromNBT(blocklingTag, ObjectUtil.coalesce(new Version(blocklingTag.getString("blocklings_version")), Blocklings.VERSION));
        }
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT blocklingTag, @Nonnull Version tagVersion)
    {
        blocklingTypeVariant = blocklingTag.getInt("variant");
        originalBlocklingType = BlocklingType.find(blocklingTag.getString("original_type"), tagVersion);
        blocklingType = BlocklingType.find(blocklingTag.getString("type"), tagVersion);
        setScale(blocklingTag.getFloat("scale"), false);

        // Health can be overwritten when loading max health modifiers.
        float health = getHealth();

        CompoundNBT equipmentInvTag = blocklingTag.getCompound("equipment_inv");

        if (equipmentInvTag != null)
        {
            equipmentInv.readFromNBT(equipmentInvTag, tagVersion);
        }

        CompoundNBT statsTag = blocklingTag.getCompound("attributes");

        if (statsTag != null)
        {
            stats.readFromNBT(statsTag, tagVersion);
        }

        CompoundNBT tasksTag = blocklingTag.getCompound("tasks");

        if (tasksTag != null)
        {
            tasks.readFromNBT(tasksTag, tagVersion);
        }

        CompoundNBT skillsTag = blocklingTag.getCompound("skills");

        if (skillsTag != null)
        {
            skills.readFromNBT(skillsTag, tagVersion);
        }

        equipmentInv.updateToolAttributes();
        stats.updateTypeBonuses(false);

        // Set back to the saved health as this should be correct.
        setHealth(health);
    }

    @Override
    public void writeSpawnData(@Nonnull PacketBuffer buf)
    {
        buf.writeInt(BlocklingType.TYPES.indexOf(originalBlocklingType));
        buf.writeInt(BlocklingType.TYPES.indexOf(blocklingType));
        buf.writeInt(blocklingTypeVariant);
        buf.writeFloat(scale);

        equipmentInv.encode(buf);
        stats.encode(buf);
        tasks.encode(buf);
        skills.encode(buf);
    }

    @Override
    public void readSpawnData(@Nonnull PacketBuffer buf)
    {
        originalBlocklingType = BlocklingType.TYPES.get(buf.readInt());
        blocklingType = BlocklingType.TYPES.get(buf.readInt());
        blocklingTypeVariant = buf.readInt();
        setScale(buf.readFloat(), false);

        equipmentInv.decode(buf);
        stats.decode(buf);
        tasks.decode(buf);
        skills.decode(buf);

        equipmentInv.updateToolAttributes();
        stats.updateTypeBonuses(false);
    }

    @Override
    public @Nonnull IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!level.isClientSide && !hasPlayerResetCrouchBetweenInteractions)
        {
            hasPlayerResetCrouchBetweenInteractions = !isTame() || (getOwner() != null && !getOwner().isCrouching());
        }

        skills.tick();
        actions.tick();

        checkAndUpdateCooldowns();
        
        equipmentInv.detectAndSendChanges();
    }

    @Override
    public void customServerAiStep()
    {
        super.customServerAiStep();

        // Tick the tasks just after the goal and target selectors have ticked
        tasks.tick();
    }

    /**
     * Checks and updates any cooldowns if required.
     */
    private void checkAndUpdateCooldowns()
    {
        actions.regenerationCooldown.tryStart();

        if (actions.regenerationCooldown.isFinished())
        {
            if (skills.getSkill(CombatSkills.REGENERATION_3).isBought())
            {
                heal(5.0f);
            }
            else if (skills.getSkill(CombatSkills.REGENERATION_2).isBought())
            {
                heal(3.0f);
            }
            else if (skills.getSkill(CombatSkills.REGENERATION_1).isBought())
            {
                heal(1.0f);
            }
        }

        if (actions.attacksCooldown.isFinished())
        {
            attacksRecently = 0;
            stats.attackSpeedSkillMomentumModifier.setValue(0.0f);
        }

        if (actions.oresMinedCooldown.isFinished())
        {
            oresMinedRecently = 0;
            stats.miningSpeedSkillMomentumModifier.setValue(0.0f);
        }

        if (actions.logsChoppedCooldown.isFinished())
        {
            logsChoppedRecently = 0;
            stats.woodcuttingSpeedSkillMomentumModifier.setValue(0.0f);
        }

        if (actions.cropsHarvestedCooldown.isFinished())
        {
            cropsHarvestedRecently = 0;
            stats.farmingSpeedSkillMomentumModifier.setValue(0.0f);
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity target)
    {
        BlocklingHand attackingHand = actions.attack.getRecentHand();
        ItemStack mainStack = getMainHandItem();
        ItemStack offStack = getOffhandItem();

        float damage = 0.0f;
        float knockback = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        int fireAspect = 0;

        if (target instanceof LivingEntity)
        {
            if (attackingHand == BlocklingHand.MAIN || attackingHand == BlocklingHand.BOTH)
            {
                damage += stats.mainHandAttackDamage.getValue();
                damage += ToolUtil.getToolEnchantmentDamage(mainStack, ((LivingEntity) target).getMobType());
                knockback += ToolUtil.getToolKnockback(mainStack);
                fireAspect += ToolUtil.getToolFireAspect(mainStack);
            }

            if (attackingHand == BlocklingHand.OFF || attackingHand == BlocklingHand.BOTH)
            {
                damage += stats.offHandAttackDamage.getValue();
                damage += ToolUtil.getToolEnchantmentDamage(offStack, ((LivingEntity) target).getMobType());
                knockback += ToolUtil.getToolKnockback(offStack);
                fireAspect += ToolUtil.getToolFireAspect(offStack);
            }
        }

        if (fireAspect > 0)
        {
            target.setSecondsOnFire(fireAspect * 4);
        }

        if (target instanceof LivingEntity)
        {
            LivingEntity livingTarget = (LivingEntity) target;

            if (skills.getSkill(CombatSkills.POISON_ATTACKS).isBought())
            {
                livingTarget.addEffect(new EffectInstance(Effects.POISON, 100));
            }
            else if (skills.getSkill(CombatSkills.WITHER_ATTACKS).isBought())
            {
                livingTarget.addEffect(new EffectInstance(Effects.WITHER, 60));
            }
        }

        int invulnerableTime = target.invulnerableTime;
        boolean hasHurt = target.hurt(DamageSource.mobAttack(this), damage);
        target.invulnerableTime = invulnerableTime;

        if (hasHurt)
        {
            stats.combatXp.incrementValue((int) damage + 1);

            if (knockback > 0.0f && target instanceof LivingEntity)
            {
                ((LivingEntity) target).knockback(knockback * 0.5f, (double) MathHelper.sin(this.yRot * ((float) Math.PI / 180.0f)), (-MathHelper.cos(this.yRot * ((float) Math.PI / 180.0f))));
                setDeltaMovement(getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity) target;
                maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            doEnchantDamageEffects(this, target);
            setLastHurtMob(target);
        }

        return hasHurt;
    }

    /**
     * Copied from MobEntity as we need to run custom hurt target code but still need this functionality.
     */
    private void maybeDisableShield(PlayerEntity p_233655_1_, ItemStack p_233655_2_, ItemStack p_233655_3_) {
        if (!p_233655_2_.isEmpty() && !p_233655_3_.isEmpty() && p_233655_2_.getItem() instanceof AxeItem && p_233655_3_.getItem() == Items.SHIELD) {
            float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                p_233655_1_.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent(p_233655_1_, (byte)30);
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float damage)
    {
        boolean hurt = super.hurt(damageSource, damage);

        if (isDeadOrDying())
        {
            BlocklingWhistleItem.onBlocklingDestroyed(this);
        }

        if (!level.isClientSide)
        {
            if (skills.getSkill(GeneralSkills.ARMADILLO).isBought())
            {
                if (isDeadOrDying())
                {
                    setHealth(1.0f);

                    dropItemStack(BlocklingItem.create(this));

                    remove();
                }
            }
        }

        return hurt;
    }

    @Override
    @Nonnull
    public ActionResultType mobInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand)
    {
        ActionResultType result;

        if (hand == Hand.MAIN_HAND)
        {
            result = mobInteractMainHand(player);
        }
        else
        {
            result = mobInteractOffHand(player);
        }

        if (result != ActionResultType.PASS)
        {
            return result;
        }

        return super.mobInteract(player, hand);
    }

    /**
     * Handles the player interacting with their main hand.
     *
     * @param player the interacting player.
     * @return the result of the interaction.
     */
    @Nonnull
    private ActionResultType mobInteractMainHand(@Nonnull PlayerEntity player)
    {
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        Item item = stack.getItem();

        if (item == BLOCKLING_WHISTLE.get())
        {
            if (player == getOwner())
            {
                BlocklingWhistleItem.setBlockling(stack, this);

                return ActionResultType.SUCCESS;
            }
        }
        if (blocklingType.isFoodForType(item))
        {
            if (!level.isClientSide())
            {
                if (!isTame())
                {
                    tryTame((ServerPlayerEntity) player, stack);

                    if (!player.abilities.instabuild)
                    {
                        stack.shrink(1);
                    }

                    return ActionResultType.SUCCESS;
                }
                else
                {
                    if (hasPlayerResetCrouchBetweenInteractions && skills.getSkill(GeneralSkills.PACKLING).isBought())
                    {
                        if (player == getOwner())
                        {
                            if (player.isCrouching())
                            {
                                ItemStack blocklingStack = BlocklingItem.create(this);

                                if (!player.inventory.add(blocklingStack))
                                {
                                    dropItemStack(blocklingStack);
                                }

                                BlocklingWhistleItem.onBlocklingDestroyed(this);

                                remove();

                                if (!player.abilities.instabuild)
                                {
                                    stack.shrink(1);
                                }

                                return ActionResultType.SUCCESS;
                            }
                        }
                    }

                    if (hasPlayerResetCrouchBetweenInteractions && skills.getSkill(GeneralSkills.HEAL).isBought())
                    {
                        if (getHealth() < getMaxHealth())
                        {
                            heal(random.nextInt(3) + 3);

                            level.broadcastEntityEvent(this, (byte) 7);

                            if (!player.abilities.instabuild)
                            {
                                stack.shrink(1);
                            }

                            return ActionResultType.SUCCESS;
                        }
                    }
                }
            }
        }
        else if (BlocklingType.isFood(item))
        {
            if (player == getOwner() && player.isCrouching())
            {
                if (!level.isClientSide())
                {
                    hasPlayerResetCrouchBetweenInteractions = false;

                    if (random.nextInt(4) == 0)
                    {
                        setBlocklingType(BlocklingType.findTypeForFood(item));
                    }
                    else
                    {
                        level.broadcastEntityEvent(this, (byte) 6);
                    }

                    if (!player.abilities.instabuild)
                    {
                        stack.shrink(1);
                    }
                }

                return ActionResultType.SUCCESS;
            }
        }
        else if (item == Items.EXPERIENCE_BOTTLE)
        {
            if (player == getOwner() && player.abilities.instabuild)
            {
                if (!level.isClientSide())
                {
                    if (player.isCrouching())
                    {
                        stats.combatLevel.setValue(BlocklingAttributes.Level.MAX);
                        stats.miningLevel.setValue(BlocklingAttributes.Level.MAX);
                        stats.woodcuttingLevel.setValue(BlocklingAttributes.Level.MAX);
                        stats.farmingLevel.setValue(BlocklingAttributes.Level.MAX);

                    }

                    stats.combatXp.setValue(BlocklingAttributes.getXpForLevel(stats.combatLevel.getValue()));
                    stats.miningXp.setValue(BlocklingAttributes.getXpForLevel(stats.miningLevel.getValue()));
                    stats.woodcuttingXp.setValue(BlocklingAttributes.getXpForLevel(stats.woodcuttingLevel.getValue()));
                    stats.farmingXp.setValue(BlocklingAttributes.getXpForLevel(stats.farmingLevel.getValue()));

                    heal(Float.MAX_VALUE);
                }

                return ActionResultType.SUCCESS;
            }
        }

        if (isTame() && player == getOwner())
        {
//            if (item != Items.EXPERIENCE_BOTTLE && (!BlocklingType.isFood(item) || !player.isCrouching()) && (!blocklingType.isFoodForType(item) || getHealth() >= getMaxHealth()))
//            {
                if (!level.isClientSide())
                {
                    if (hasPlayerResetCrouchBetweenInteractions)
                    {
                        guiHandler.openGui(player);
                    }
                }

                return ActionResultType.CONSUME;
//            }
        }

        return ActionResultType.PASS;
    }

    /**
     * Handles the player interacting with their off hand.
     *
     * @param player the interacting player.
     * @return the result of the interaction.
     */
    @Nonnull
    private ActionResultType mobInteractOffHand(@Nonnull PlayerEntity player)
    {
        ItemStack stack = player.getItemInHand(Hand.OFF_HAND);
        Item item = stack.getItem();

        return ActionResultType.PASS;
    }

    /**
     * Attempts to tame the blockling with a 1 in 3 chance.
     *
     * @param player the player interacting with the blockling.
     * @param stack the stack involved in the interaction.
     * @return true if the blockling is successfully tamed.
     */
    private boolean tryTame(@Nonnull ServerPlayerEntity player, @Nonnull ItemStack stack)
    {
        if (random.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player))
        {
            tame(player);

            for (Task task : getTasks().getPrioritisedTasks())
            {
                if (task.isConfigured() && task.getType() == BlocklingTasks.WANDER)
                {
                    task.setType(BlocklingTasks.FOLLOW);
                }
            }

            navigation.stop();
            level.broadcastEntityEvent(this, (byte) 7);

            return true;
        }
        else
        {
            level.broadcastEntityEvent(this, (byte) 6);
        }

        return false;
    }

    @Override
    public void tame(@Nonnull PlayerEntity player)
    {
        super.tame(player);

        if (!hasCustomName())
        {
            setCustomName(new StringTextComponent("Blockling"), true);
        }
    }

    @Override
    protected void dropCustomDeathLoot(@Nonnull DamageSource damageSource, int something, boolean something2)
    {
        super.dropCustomDeathLoot(damageSource, something, something2);

        for (int i = 0; i < equipmentInv.getContainerSize(); i++)
        {
            ItemStack stack = equipmentInv.getItem(i);

            if (!stack.isEmpty())
            {
                spawnAtLocation(stack);
            }
        }
    }

    @Override
    protected void dropAllDeathLoot(@Nonnull DamageSource damageSource)
    {
        if (!skills.getSkill(GeneralSkills.ARMADILLO).isBought())
        {
            super.dropAllDeathLoot(damageSource);
        }
    }

    @Override
    @Nonnull
    public ItemStack getMainHandItem()
    {
        return getItemInHand(Hand.MAIN_HAND);
    }

    @Override
    @Nonnull
    public ItemStack getOffhandItem()
    {
        return getItemInHand(Hand.OFF_HAND);
    }

    @Override
    @Nonnull
    public ItemStack getItemInHand(@Nonnull Hand hand)
    {
        return equipmentInv.getHandStack(hand);
    }

    @Override
    public void setItemInHand(@Nonnull Hand hand, @Nonnull ItemStack stack)
    {
        equipmentInv.setHandStack(hand, stack);
    }

    @Override
    @Nonnull
    public Iterable<ItemStack> getHandSlots()
    {
        BlocklingHand attackingHand = actions.attack.getRecentHand();

        if (attackingHand == BlocklingHand.MAIN)
        {
            return Collections.singletonList(getMainHandItem());
        }
        else if (attackingHand == BlocklingHand.OFF)
        {
            return Collections.singletonList(getOffhandItem());
        }

        return Arrays.asList(getMainHandItem(), getOffhandItem());
    }

    @Override
    @Nonnull
    public Iterable<ItemStack> getArmorSlots()
    {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public Iterable<ItemStack> getAllSlots()
    {
        return Iterables.concat(getHandSlots(), getArmorSlots());
    }

    @Override
    public boolean hasItemInSlot(@Nonnull EquipmentSlotType slotType)
    {
        if (slotType == EquipmentSlotType.MAINHAND)
        {
            return !getMainHandItem().isEmpty();
        }
        else if (slotType == EquipmentSlotType.OFFHAND)
        {
            return !getOffhandItem().isEmpty();
        }

        return false;
    }

    @Override
    @Nonnull
    public ItemStack getItemBySlot(@Nonnull EquipmentSlotType slotType)
    {
        if (slotType == EquipmentSlotType.MAINHAND)
        {
            return getMainHandItem();
        }
        else if (slotType == EquipmentSlotType.OFFHAND)
        {
            return getOffhandItem();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@Nonnull EquipmentSlotType slotType, @Nonnull ItemStack stack)
    {
        if (slotType == EquipmentSlotType.MAINHAND)
        {
            setItemInHand(Hand.MAIN_HAND, stack);
        }
        else if (slotType == EquipmentSlotType.OFFHAND)
        {
            setItemInHand(Hand.OFF_HAND, stack);
        }
    }

    @Override
    public boolean checkSpawnRules(@Nonnull IWorld world, @Nonnull SpawnReason reason)
    {
        if (reason == SpawnReason.NATURAL || reason == SpawnReason.CHUNK_GENERATION)
        {
            if (random.nextInt(blocklingType.spawnRateReduction) != 0)
            {
                return false;
            }

            if (!world.getBlockState(getOnPos()).getMaterial().isSolid())
            {
                return false;
            }

            for (BiPredicate<BlocklingEntity, IWorld> predicate : getBlocklingType().spawnPredicates)
            {
                if (!predicate.test(this, world))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public float getEyeHeightAccess(@Nonnull Pose pose, @Nonnull EntitySize size)
    {
        return size.height * 0.45f;
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(@Nonnull ServerWorld world, @Nonnull AgeableEntity entity)
    {
        return null;
    }

    /**
     * Drops the given stack at the blockling's location.
     *
     * @param stack the stack to drop.
     */
    public void dropItemStack(@Nonnull ItemStack stack)
    {
        level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.2f, getZ(), stack));
    }

    @Override
    public boolean fireImmune()
    {
        return blocklingType == BlocklingType.NETHERITE || blocklingType == BlocklingType.OBSIDIAN;
    }

    /**
     * Sets the name of the blockling.
     * Does NOT sync to client/server.
     *
     * @param name the new name.
     */
    @Override
    public void setCustomName(@Nullable ITextComponent name)
    {
        if (name != null)
        {
            name = new StringTextComponent(name.getString());
        }

        setCustomName((StringTextComponent) name, false);
    }

    /**
     * Sets the name of the blockling.
     * Syncs to the server if set from the client and sync is true.
     *
     * @param name the new name.
     * @param sync whether to sync to the server from the client.
     */
    public void setCustomName(@Nullable StringTextComponent name, boolean sync)
    {
        super.setCustomName(name);

        if (level.isClientSide && sync)
        {
            new BlocklingNameMessage(this, name).sync();
        }
    }

    /**
     * Sets the current target to the given entity.
     * Syncs to the client/server.
     *
     * @param target the new target.
     */
    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        setTarget(target, true);
    }

    /**
     * Sets the current target to the given entity.
     * Syncs to the client/server if sync is true.
     *
     * @param target the new target.
     * @param sync whether to sync to the client/server.
     */
    public void setTarget(@Nullable LivingEntity target, boolean sync)
    {
        super.setTarget(target);

        if (sync)
        {
            new BlocklingAttackTargetMessage(this, target).sync();
        }
    }

    /**
     * @return the original blockling type.
     */
    @Nonnull
    public BlocklingType getOriginalBlocklingType()
    {
        return originalBlocklingType;
    }

    /**
     * @return the current blockling type.
     */
    @Nonnull
    public BlocklingType getBlocklingType()
    {
        return blocklingType;
    }

    /**
     * Sets the current blockling type to the given blockling type.
     * Syncs to the client/server.
     *
     * @param blocklingType the new blockling type.
     */
    public void setBlocklingType(@Nonnull BlocklingType blocklingType)
    {
        setBlocklingType(blocklingType, true);
    }

    /**
     * Sets the current blockling type to the given blockling type.
     * Syncs to the client/server if sync is true.
     *
     * @param blocklingType the new blockling type.
     * @param sync whether to sync to the client/server.
     */
    public void setBlocklingType(@Nonnull BlocklingType blocklingType, boolean sync)
    {
        this.blocklingType = blocklingType;

        stats.updateTypeBonuses(sync);

        if (sync)
        {
            new BlocklingTypeMessage(this, blocklingType).sync();
        }
    }

    /**
     * @return the current variant.
     */
    public int getBlocklingTypeVariant()
    {
        return blocklingTypeVariant;
    }

    /**
     * @return the blockling's attribute manager.
     */
    @Nonnull
    public BlocklingAttributes getStats()
    {
        return stats;
    }

    /**
     * @return the blockling's skill manager.
     */
    @Nonnull
    public BlocklingSkills getSkills()
    {
        return skills;
    }

    /**
     * @return the blockling's task manager.
     */
    @Nonnull
    public BlocklingTasks getTasks()
    {
        return tasks;
    }

    /**
     * @return the blockling's action manager.
     */
    @Nonnull
    public BlocklingActions getActions()
    {
        return actions;
    }

    /**
     * @return the blockling's equipment inventory.
     */
    public EquipmentInventory getEquipment()
    {
        return equipmentInv;
    }

    /**
     * @return the blockling's scale.
     */
    @Override
    public float getScale()
    {
        return scale;
    }

    /**
     * Sets the blockling's scale.
     * Syncs to the client/server.
     *
     * @param scale the new scale.
     */
    public void setScale(float scale)
    {
        setScale(scale, true);
    }

    /**
     * Sets the blockling's scale.
     * Syncs to the client/server if sync is true.
     *
     * @param scale the new scale.
     * @param sync whether to sync to the client/server.
     */
    public void setScale(float scale, boolean sync)
    {
        this.scale = scale;

        refreshDimensions();

        if (sync)
        {
            new BlocklingScaleMessage(this, scale).sync();
        }
    }

    /**
     * Increments the count of attacks recently and resets the cooldown.
     */
    public void incAttacksRecently()
    {
        attacksRecently++;
        actions.attacksCooldown.start();

        if (skills.getSkill(CombatSkills.MOMENTUM).isBought())
        {
            int cappedCount = Math.min(attacksRecently, 20);
            stats.attackSpeedSkillMomentumModifier.setValue((float) cappedCount);
        }
    }

    /**
     * Increments the count of ores mined recently and resets the cooldown.
     */
    public void incOresMinedRecently()
    {
        oresMinedRecently++;
        actions.oresMinedCooldown.start();

        if (skills.getSkill(MiningSkills.MOMENTUM).isBought())
        {
            int cappedCount = Math.min(oresMinedRecently, 20);
            stats.miningSpeedSkillMomentumModifier.setValue((float) cappedCount);
        }
    }

    /**
     * Increments the count of logs chopped recently and resets the cooldown.
     */
    public void incLogsChoppedRecently()
    {
        logsChoppedRecently++;
        actions.logsChoppedCooldown.start();

        if (skills.getSkill(WoodcuttingSkills.MOMENTUM).isBought())
        {
            int cappedCount = Math.min(logsChoppedRecently, 20);
            stats.woodcuttingSpeedSkillMomentumModifier.setValue((float) cappedCount);
        }
    }

    /**
     * Increments the count of crops harvested recently and resets the cooldown.
     */
    public void incCropsHarvestedRecently()
    {
        cropsHarvestedRecently++;
        actions.cropsHarvestedCooldown.start();

        if (skills.getSkill(FarmingSkills.MOMENTUM).isBought())
        {
            int cappedCount = Math.min(cropsHarvestedRecently, 20);
            stats.farmingSpeedSkillMomentumModifier.setValue((float) cappedCount);
        }
    }
}
