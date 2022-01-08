package com.willr27.blocklings.entity.entities.blockling;

import com.google.common.collect.Iterables;
import com.willr27.blocklings.action.BlocklingActions;
import com.willr27.blocklings.attribute.BlocklingAttributes;
import com.willr27.blocklings.goal.Task;
import com.willr27.blocklings.gui.GuiHandler;
import com.willr27.blocklings.inventory.inventories.EquipmentInventory;
import com.willr27.blocklings.item.ToolUtil;
import com.willr27.blocklings.item.items.BlocklingItem;
import com.willr27.blocklings.network.messages.BlocklingScaleMessage;
import com.willr27.blocklings.network.messages.BlocklingAttackTargetMessage;
import com.willr27.blocklings.network.messages.BlocklingTypeMessage;
import com.willr27.blocklings.skills.BlocklingSkills;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
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

public class BlocklingEntity extends TameableEntity implements IEntityAdditionalSpawnData
{
    private BlocklingType originalBlocklingType = BlocklingType.GRASS;
    private BlocklingType blocklingType = BlocklingType.GRASS;
    private int blocklingTypeVariant = 0;

    private final BlocklingAttributes stats = new BlocklingAttributes(this);
    private final BlocklingTasks tasks = new BlocklingTasks(this);
    private final BlocklingSkills skills = new BlocklingSkills(this);
    private final BlocklingActions actions = new BlocklingActions(this);
    private BlocklingGuiInfo guiInfo = new BlocklingGuiInfo(this);

    private final EquipmentInventory equipmentInv = new EquipmentInventory(this);

    private float scale;

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

    public BlocklingEntity(EntityType<? extends BlocklingEntity> type, World world)
    {
        super(type, world);

        setCustomName(new StringTextComponent("Blockling"));

        stats.initUpdateCallbacks();

        // Set up any values that are determined randomly here
        // So that we can sync them up using read/writeSpawnData
        if (!level.isClientSide)
        {
            blocklingTypeVariant = getRandom().nextInt(3);
            originalBlocklingType = BlocklingType.TYPES.get(getRandom().nextInt(BlocklingType.TYPES.size()));
            setBlocklingType(originalBlocklingType, false);

            setScale(getRandom().nextFloat() * 0.5f + 0.49f, false);

            stats.init();
        }

        equipmentInv.updateToolAttributes();

        setHealth(getMaxHealth());
    }

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
                readBlocklingFromNBT(blocklingTag);
            }
        }

        return super.finalizeSpawn(world, difficultyInstance, spawnReason, entityData, tag);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT c)
    {
        super.addAdditionalSaveData(c);

        c.put("blockling", writeBlocklingToNBT());
    }

    public CompoundNBT writeBlocklingToNBT()
    {
        CompoundNBT c = new CompoundNBT();

        c.putInt("original_type", BlocklingType.TYPES.indexOf(originalBlocklingType));
        c.putInt("type", BlocklingType.TYPES.indexOf(blocklingType));
        c.putInt("variant", blocklingTypeVariant);
        c.putFloat("scale", scale);

        equipmentInv.writeToNBT(c);
        stats.writeToNBT(c);
        tasks.writeToNBT(c);
        skills.writeToNBT(c);

        return c;
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT c)
    {
        super.readAdditionalSaveData(c);

        CompoundNBT blocklingTag = (CompoundNBT) c.get("blockling");

        if (blocklingTag != null)
        {
            readBlocklingFromNBT(blocklingTag);
        }
    }

    public void readBlocklingFromNBT(CompoundNBT c)
    {
        if (c == null)
        {
            return;
        }

        blocklingTypeVariant = c.getInt("variant");
        originalBlocklingType = BlocklingType.TYPES.get(c.getInt("original_type"));
        blocklingType = BlocklingType.TYPES.get(c.getInt("type"));
        setScale(c.getFloat("scale"), false);

        equipmentInv.readFromNBT(c);
        stats.readFromNBT(c);
        tasks.readFromNBT(c);
        skills.readFromNBT(c);

        equipmentInv.updateToolAttributes();
        stats.updateTypeBonuses(false);
    }

    @Override
    public void writeSpawnData(PacketBuffer buf)
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
    public void readSpawnData(PacketBuffer buf)
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

        skills.tick();
        actions.tick();

        checkCooldowns();

        equipmentInv.detectAndSendChanges();
    }

    private void checkCooldowns()
    {
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
    public @Nonnull ActionResultType mobInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand)
    {
        ActionResultType result = ActionResultType.PASS;

        if (hand == Hand.MAIN_HAND)
        {
            if (level.isClientSide)
            {
                result = mobInteractMainHandClient((ClientPlayerEntity) player);
            }
            else
            {
                result = mobInteractMainHandServer((ServerPlayerEntity) player);
            }

            if (result != ActionResultType.PASS)
            {
                return result;
            }
        }
        else
        {
            if (level.isClientSide)
            {
                result = mobInteractOffHandClient((ClientPlayerEntity) player);
            }
            else
            {
                result = mobInteractOffHandServer((ServerPlayerEntity) player);
            }

            if (result != ActionResultType.PASS)
            {
                return result;
            }
        }

        return super.mobInteract(player, hand);
    }

    private ActionResultType mobInteractMainHandClient(ClientPlayerEntity player)
    {
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        Item item = stack.getItem();

        if (isTame() && getOwner() == player)
        {
            if (item != Items.EXPERIENCE_BOTTLE && (!BlocklingType.isFood(item) || !player.isCrouching()) && (!blocklingType.isFoodForType(item) || getHealth() >= getMaxHealth()))
            {
                OpenGui(player);
            }
        }

        return ActionResultType.PASS;
    }

    private ActionResultType mobInteractMainHandServer(ServerPlayerEntity player)
    {
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        Item item = stack.getItem();

        if (blocklingType.isFoodForType(item))
        {
            if (!isTame())
            {
                tryTame(player, stack);
            }
            else
            {
                if (player.isCrouching())
                {
                    ItemStack blocklingStack = BlocklingItem.create(this);

                    if (!player.inventory.add(blocklingStack))
                    {
                        dropItemStack(blocklingStack);
                    }

                    remove();
                }
                else
                {
                    if (getHealth() < getMaxHealth())
                    {
                        heal(random.nextInt(3) + 3);

                        level.broadcastEntityEvent(this, (byte) 7);
                    }
                }
            }
        }
        else if (BlocklingType.isFood(item))
        {
            if (player.isCrouching())
            {
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
        }
        else if (item == Items.EXPERIENCE_BOTTLE)
        {
            if (player.abilities.instabuild)
            {
                if (player.isCrouching())
                {
                    stats.combatLevel.setValue(99);
                    stats.miningLevel.setValue(99);
                    stats.woodcuttingLevel.setValue(99);
                    stats.farmingLevel.setValue(99);

                }

                stats.combatXp.setValue(BlocklingAttributes.getXpUntilNextLevel(stats.combatLevel.getValue()));
                stats.miningXp.setValue(BlocklingAttributes.getXpUntilNextLevel(stats.miningLevel.getValue()));
                stats.woodcuttingXp.setValue(BlocklingAttributes.getXpUntilNextLevel(stats.woodcuttingLevel.getValue()));
                stats.farmingXp.setValue(BlocklingAttributes.getXpUntilNextLevel(stats.farmingLevel.getValue()));

                heal(Float.MAX_VALUE);
            }
        }

        return ActionResultType.PASS;
    }

    private ActionResultType mobInteractOffHandClient(ClientPlayerEntity player)
    {
        ItemStack stack = player.getItemInHand(Hand.OFF_HAND);
        Item item = stack.getItem();

        return ActionResultType.PASS;
    }

    private ActionResultType mobInteractOffHandServer(ServerPlayerEntity player)
    {
        ItemStack stack = player.getItemInHand(Hand.OFF_HAND);
        Item item = stack.getItem();

        return ActionResultType.PASS;
    }

    @Override
    protected void dropCustomDeathLoot(@Nonnull DamageSource source, int something, boolean something2)
    {
        super.dropCustomDeathLoot(source, something, something2);

        for (int i = 0; i < equipmentInv.getContainerSize(); i++)
        {
            ItemStack stack = equipmentInv.getItem(i);

            if (!stack.isEmpty())
            {
                spawnAtLocation(stack);
            }
        }
    }

    private void tryTame(ServerPlayerEntity player, ItemStack stack)
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

    @Override
    public @Nonnull ItemStack getMainHandItem()
    {
        return getItemInHand(Hand.MAIN_HAND);
    }

    @Override
    public @Nonnull ItemStack getOffhandItem()
    {
        return getItemInHand(Hand.OFF_HAND);
    }

    @Override
    public @Nonnull ItemStack getItemInHand(@Nonnull Hand hand)
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

            for (BiPredicate<BlocklingEntity, IWorld> predicate : getBlocklingType().predicates)
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
    public float getEyeHeightAccess(Pose pose, EntitySize size)
    {
        return size.height * 0.45f;
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(@Nonnull ServerWorld world, @Nonnull AgeableEntity entity)
    {
        return null;
    }

    public void dropItemStack(ItemStack stack)
    {
        level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.2f, getZ(), stack));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target)
    {
        setTarget(target, true);
    }

    public void setTarget(@Nullable LivingEntity target, boolean sync)
    {
        super.setTarget(target);

        if (sync)
        {
            new BlocklingAttackTargetMessage(this, target).sync();
        }
    }

    private void OpenGui(PlayerEntity player)
    {
        GuiHandler.openGui(guiInfo.getCurrentGuiId(), this, player);
    }

    public BlocklingType getOriginalBlocklingType()
    {
        return originalBlocklingType;
    }

    public BlocklingType getBlocklingType()
    {
        return blocklingType;
    }

    public void setBlocklingType(BlocklingType blocklingType)
    {
        setBlocklingType(blocklingType, true);
    }

    public void setBlocklingType(BlocklingType blocklingType, boolean sync)
    {
        this.blocklingType = blocklingType;

        stats.updateTypeBonuses(sync);

        if (sync)
        {
            new BlocklingTypeMessage(this, blocklingType).sync();
        }
    }

    public int getBlocklingTypeVariant()
    {
        return blocklingTypeVariant;
    }

    public BlocklingAttributes getStats()
    {
        return stats;
    }

    public BlocklingTasks getTasks() { return tasks; }

    public BlocklingSkills getSkills()
    {
        return skills;
    }

    public BlocklingActions getActions ()
    {
        return actions;
    }

    public BlocklingGuiInfo getGuiInfo()
    {
        return guiInfo;
    }

    public void setGuiInfo(BlocklingGuiInfo guiInfo)
    {
        setGuiInfo(guiInfo, true);
    }

    public void setGuiInfo(BlocklingGuiInfo guiInfo, boolean sync)
    {
        this.guiInfo = guiInfo;

        if (sync)
        {
            new BlocklingGuiInfo.Message(this, guiInfo).sync();
        }
    }

    public EquipmentInventory getEquipment()
    {
        return equipmentInv;
    }

    @Override
    public float getScale()
    {
        return scale;
    }

    public void setScale(float scale)
    {
        setScale(scale, true);
    }

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
     * Increments the count of ores mined recently and resets the cooldown.
     */
    public void incOresMinedRecently()
    {
        oresMinedRecently++;
        actions.oresMinedCooldown.start();

        if (skills.getSkill(BlocklingSkills.Mining.MOMENTUM).isBought())
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

        if (skills.getSkill(BlocklingSkills.Woodcutting.MOMENTUM).isBought())
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

        if (skills.getSkill(BlocklingSkills.Farming.MOMENTUM).isBought())
        {
            int cappedCount = Math.min(cropsHarvestedRecently, 20);
            stats.farmingSpeedSkillMomentumModifier.setValue((float) cappedCount);
        }
    }
}
