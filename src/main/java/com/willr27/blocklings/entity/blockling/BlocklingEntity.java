package com.willr27.blocklings.entity.blockling;

import com.google.common.collect.Iterables;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.block.BlocklingsBlocks;
import com.willr27.blocklings.client.gui.BlocklingGuiHandler;
import com.willr27.blocklings.command.BlocklingsCommands;
import com.willr27.blocklings.config.BlocklingsConfig;
import com.willr27.blocklings.entity.blockling.action.BlocklingActions;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.blockling.skill.BlocklingSkills;
import com.willr27.blocklings.entity.blockling.skill.skills.*;
import com.willr27.blocklings.entity.blockling.task.BlocklingTasks;
import com.willr27.blocklings.entity.blockling.task.Task;
import com.willr27.blocklings.interop.TinkersConstructProxy;
import com.willr27.blocklings.inventory.EquipmentInventory;
import com.willr27.blocklings.item.BlocklingItem;
import com.willr27.blocklings.item.BlocklingWhistleItem;
import com.willr27.blocklings.network.messages.BlocklingAttackTargetMessage;
import com.willr27.blocklings.network.messages.BlocklingNameMessage;
import com.willr27.blocklings.network.messages.BlocklingScaleMessage;
import com.willr27.blocklings.network.messages.BlocklingTypeMessage;
import com.willr27.blocklings.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;

import static com.willr27.blocklings.item.BlocklingsItems.BLOCKLING_WHISTLE;

/**
 * The blockling entity.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlocklingEntity extends TamableAnimal implements IEntityAdditionalSpawnData, IReadWriteNBT
{
    /**
     * The blockling type the blockling spawned as.
     */
    @Nonnull
    private BlocklingType naturalBlocklingType = BlocklingType.GRASS;

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
     * The current position of the blockling's light source block.
     */
    @Nullable
    private BlockPos currentLightPos = null;

    /**
     * @param type the blockling entity type.
     * @param world the world the blockling is in.
     */
    public BlocklingEntity(@Nonnull EntityType<? extends BlocklingEntity> type, @Nonnull Level world)
    {
        super(type, world);

        rotate(Rotation.getRandom(this.getRandom()));

        stats.initUpdateCallbacks();

        // Set up any values that are determined randomly here.
        // So that we can sync them up using read/writeSpawnData.
        if (!level.isClientSide())
        {
            blocklingTypeVariant = getRandom().nextInt(3);
            setNaturalBlocklingType(BlocklingType.TYPES.get(getRandom().nextInt(BlocklingType.TYPES.size())), false);
            setBlocklingType(naturalBlocklingType, false);

            stats.init();
        }

        actions.ticks20.addCallback(this::updatePassiveAbilities);
        actions.logRegenerationCooldown.addCallback(this::updateLogPassiveAbility);

        equipmentInv.updateToolAttributes();

        setHealth(getMaxHealth());
    }

    @Override
    public void remove(RemovalReason removalReason)
    {
        super.remove(removalReason);

        // Make sure any light sources are removed when the blockling is removed.
        updateLightPos(true);
    }

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.ATTACK_SPEED, 0.0)
                .add(Attributes.FOLLOW_RANGE, 48.0); // Follow range determines max pathfinding distance.
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(
            @Nonnull ServerLevelAccessor world,
            @Nonnull DifficultyInstance difficultyInstance,
            @Nonnull MobSpawnType spawnReason,
            @Nullable SpawnGroupData spawnGroupData,
            @Nullable CompoundTag entityTag)
    {
        tasks.initDefaultTasks();

        if (spawnReason == MobSpawnType.SPAWN_EGG && entityTag != null)
        {
            readAdditionalSaveData(entityTag);
        }
        else
        {
            for (UUID playerId : world.players().stream().map(Player::getUUID).toList())
            {
                if (BlocklingsCommands.debugSpawns.getOrDefault(playerId, false))
                {
                    BlockPos blockPos = blockPosition();

                    MutableComponent locationLink = ComponentUtils.wrapInSquareBrackets(
                            new TranslatableComponent("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ()))
                                .withStyle((style) -> style.withColor(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"))));

                    MutableComponent text = new BlocklingsTranslatableComponent("command.debug.spawns.spawn", blocklingType.name.getString()).append(locationLink);

                    world.getPlayerByUUID(playerId).sendMessage(text, Util.NIL_UUID);
                }
            }
        }

        return super.finalizeSpawn(world, difficultyInstance, spawnReason, spawnGroupData, entityTag);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);

        CompoundTag blocklingTag = new CompoundTag();

        blocklingTag.putString("blocklings_version", Blocklings.VERSION.toString());

        writeToNBT(blocklingTag);

        tag.put("blockling", blocklingTag);
    }

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag blocklingTag)
    {
        blocklingTag.putString("original_type", naturalBlocklingType.key);
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
    public void readAdditionalSaveData(@Nonnull CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);

        CompoundTag blocklingTag = tag.getCompound("blockling");

        if (blocklingTag != null)
        {
            readFromNBT(blocklingTag, ObjectUtil.coalesce(new Version(blocklingTag.getString("blocklings_version")), Blocklings.VERSION));
        }

        // Follow range determines max pathfinding distance. Existing blocklings need this value setting here.
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(48.0);
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag blocklingTag, @Nonnull Version tagVersion)
    {
        blocklingTypeVariant = blocklingTag.getInt("variant");
        naturalBlocklingType = BlocklingType.find(blocklingTag.getString("original_type"), tagVersion);
        blocklingType = BlocklingType.find(blocklingTag.getString("type"), tagVersion);
        setScale(blocklingTag.getFloat("scale"), false);

        // Health can be overwritten when loading max health modifiers.
        float health = getHealth();

        CompoundTag equipmentInvTag = blocklingTag.getCompound("equipment_inv");

        if (equipmentInvTag != null)
        {
            equipmentInv.readFromNBT(equipmentInvTag, tagVersion);
        }

        CompoundTag statsTag = blocklingTag.getCompound("attributes");

        if (statsTag != null)
        {
            stats.readFromNBT(statsTag, tagVersion);
        }

        CompoundTag tasksTag = blocklingTag.getCompound("tasks");

        if (tasksTag != null)
        {
            tasks.readFromNBT(tasksTag, tagVersion);
        }

        CompoundTag skillsTag = blocklingTag.getCompound("skills");

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
    public void writeSpawnData(@Nonnull FriendlyByteBuf buf)
    {
        buf.writeInt(BlocklingType.TYPES.indexOf(naturalBlocklingType));
        buf.writeInt(BlocklingType.TYPES.indexOf(blocklingType));
        buf.writeInt(blocklingTypeVariant);
        buf.writeFloat(scale);

        equipmentInv.encode(buf);
        stats.encode(buf);
        tasks.encode(buf);
        skills.encode(buf);
    }

    @Override
    public void readSpawnData(@Nonnull FriendlyByteBuf buf)
    {
        naturalBlocklingType = BlocklingType.TYPES.get(buf.readInt());
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
    public @Nonnull Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!level.isClientSide)
        {
            if (!hasPlayerResetCrouchBetweenInteractions)
            {
                hasPlayerResetCrouchBetweenInteractions = !isTame() || (getOwner() != null && !getOwner().isCrouching());
            }
        }

        skills.tick();
        actions.tick();

        updateLightPos(false);
        checkAndUpdateCooldowns();
        
        equipmentInv.detectAndSendChanges();
    }

    @Override
    public void customServerAiStep()
    {
        super.customServerAiStep();

        // Tick the tasks just after the goal and target selectors have ticked.
        tasks.tick();
    }

    /**
     * Updates the passive abilities of the blockling with a type of log.
     */
    public void updateLogPassiveAbility()
    {
        final int radius = 8;
        final float healAmount = 2.0f;

        Block logBlock = null;
        if (naturalBlocklingType == BlocklingType.OAK_LOG || blocklingType == BlocklingType.OAK_LOG) logBlock = Blocks.OAK_LOG;
        else if (naturalBlocklingType == BlocklingType.BIRCH_LOG || blocklingType == BlocklingType.BIRCH_LOG) logBlock = Blocks.BIRCH_LOG;
        else if (naturalBlocklingType == BlocklingType.SPRUCE_LOG || blocklingType == BlocklingType.SPRUCE_LOG) logBlock = Blocks.SPRUCE_LOG;
        else if (naturalBlocklingType == BlocklingType.JUNGLE_LOG || blocklingType == BlocklingType.JUNGLE_LOG) logBlock = Blocks.JUNGLE_LOG;
        else if (naturalBlocklingType == BlocklingType.DARK_OAK_LOG || blocklingType == BlocklingType.DARK_OAK_LOG) logBlock = Blocks.DARK_OAK_LOG;
        else if (naturalBlocklingType == BlocklingType.ACACIA_LOG || blocklingType == BlocklingType.ACACIA_LOG) logBlock = Blocks.ACACIA_LOG;

        if (logBlock != null)
        {
            for (int i = -radius; i <= radius; i++)
            {
                for (int j = -radius; j <= radius; j++)
                {
                    for (int k = -radius; k <= radius; k++)
                    {
                        BlockPos testPos = blockPosition().offset(i, j, k);
                        Block testBlock = level.getBlockState(testPos).getBlock();

                        if (testBlock == logBlock)
                        {
                            WorldUtil.Tree treeToTest = WorldUtil.findTreeFromPos(level, testPos, 40, (t) -> true, (t) -> true);

                            if (!treeToTest.isValid(BlocklingsConfig.COMMON.defaultMinLeavesToLogRatio.get().floatValue()))
                            {
                                continue;
                            }

                            // Heal themselves.
                            if (getHealth() < getMaxHealth())
                            {
                                if (level.isClientSide)
                                {
                                    level.addParticle(ParticleTypes.HEART, getX(), getY() + getEyeHeight() + 0.75f, getZ(), 0.0f, 0.0f, 0.0f);
                                }
                                else
                                {
                                    heal(healAmount);
                                }
                            }

                            // Heal the owner if they are in range.
                            if (getOwner() != null && getOwner().distanceToSqr(this) < radius * radius)
                            {
                                if (getOwner().getHealth() < getOwner().getMaxHealth())
                                {
                                    if (level.isClientSide)
                                    {
                                        level.addParticle(ParticleTypes.HEART, getOwner().getX(), getOwner().getY() +  getOwner().getEyeHeight() + 0.75f, getOwner().getZ(), 0.0f, 0.0f, 0.0f);
                                    }
                                    else
                                    {
                                        getOwner().heal(healAmount);
                                    }
                                }
                            }

                            // Heal other blocklings in range with the same owner or no owner if this blockling is not tamed.
                            for (BlocklingEntity nearbyBlockling : level.getEntitiesOfClass(BlocklingEntity.class, AABB.ofSize(new Vec3(blockPosition().getX(), blockPosition().getY(), blockPosition().getZ()), radius * 2, radius * 2, radius * 2)))
                            {
                                if (nearbyBlockling != this && (getOwnerUUID() == null || getOwnerUUID().equals(nearbyBlockling.getOwnerUUID())))
                                {
                                    if (nearbyBlockling.getHealth() < nearbyBlockling.getMaxHealth())
                                    {
                                        if (level.isClientSide)
                                        {
                                            level.addParticle(ParticleTypes.HEART, nearbyBlockling.getX(), nearbyBlockling.getY() + nearbyBlockling.getEyeHeight() + 0.75f, nearbyBlockling.getZ(), 0.0f, 0.0f, 0.0f);
                                        }
                                        else
                                        {
                                            nearbyBlockling.heal(healAmount);
                                        }
                                    }
                                }
                            }

                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates any generic passive abilities the blockling has (should be called every 20 ticks).
     */
    private void updatePassiveAbilities()
    {
        if (!level.isClientSide)
        {
           if (naturalBlocklingType == BlocklingType.GRASS || blocklingType == BlocklingType.GRASS)
           {
               if (random.nextInt(3) == 0)
               {
                   BlockPos belowPos = getOnPos();
                   Block belowBlock = level.getBlockState(belowPos).getBlock();

                   if (belowBlock == Blocks.DIRT)
                   {
                       level.setBlock(belowPos, Blocks.GRASS_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
                   }
               }
           }

           if (naturalBlocklingType == BlocklingType.DIRT || blocklingType == BlocklingType.DIRT)
           {
               if (random.nextInt(3) == 0)
               {
                   BlockPos belowPos = getOnPos();
                   Block belowBlock = level.getBlockState(belowPos).getBlock();

                   if (belowBlock == Blocks.GRASS_BLOCK)
                   {
                       level.setBlock(belowPos, Blocks.DIRT.defaultBlockState(), Block.UPDATE_ALL);
                   }
               }
           }

            if (naturalBlocklingType == BlocklingType.STONE || blocklingType == BlocklingType.STONE)
            {
                LivingEntity owner = getOwner();
                final float range = 8.0f;
                final int level = 1;

                addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, level - 1, false, false, true));

                if (owner != null && owner.distanceToSqr(this) < range * range)
                {
                    owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 419, level - 1, false, false, true));
                }

                for (BlocklingEntity nearbyBlockling : this.level.getEntitiesOfClass(BlocklingEntity.class, AABB.ofSize(new Vec3(blockPosition().getX(), blockPosition().getY(), blockPosition().getZ()), range * 2, range * 2, range * 2)))
                {
                    if (getOwnerUUID() == null || getOwnerUUID().equals(nearbyBlockling.getOwnerUUID()))
                    {
                        nearbyBlockling.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, level - 1, false, false, true));
                    }
                }
            }

            if (naturalBlocklingType == BlocklingType.IRON || blocklingType == BlocklingType.IRON)
            {
                LivingEntity owner = getOwner();
                final float range = 8.0f;
                final int level = 1;

                addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, level - 1, false, false, true));

                if (owner != null && owner.distanceToSqr(this) < range * range)
                {
                    owner.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 419, level - 1, false, false, true));
                }

                for (BlocklingEntity nearbyBlockling : this.level.getEntitiesOfClass(BlocklingEntity.class, AABB.ofSize(new Vec3(blockPosition().getX(), blockPosition().getY(), blockPosition().getZ()), range * 2, range * 2, range * 2)))
                {
                    if (getOwnerUUID() == null || getOwnerUUID().equals(nearbyBlockling.getOwnerUUID()))
                    {
                        nearbyBlockling.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, level - 1, false, false, true));
                    }
                }
            }

            if (naturalBlocklingType == BlocklingType.GOLD || blocklingType == BlocklingType.GOLD)
            {
                LivingEntity owner = getOwner();
                final float range = 8.0f;
                final int level = 1;

                addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, level - 1, false, false, true));

                if (owner != null && owner.distanceToSqr(this) < range * range)
                {
                    owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 419, level - 1, false, false, true));
                }

                for (BlocklingEntity nearbyBlockling : this.level.getEntitiesOfClass(BlocklingEntity.class, AABB.ofSize(new Vec3(blockPosition().getX(), blockPosition().getY(), blockPosition().getZ()), range * 2, range * 2, range * 2)))
                {
                    if (getOwnerUUID() == null || getOwnerUUID().equals(nearbyBlockling.getOwnerUUID()))
                    {
                        nearbyBlockling.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, level - 1, false, false, true));
                    }
                }
            }

           if (naturalBlocklingType == BlocklingType.EMERALD || blocklingType == BlocklingType.EMERALD)
           {
               LivingEntity owner = getOwner();
               final float range = 8.0f;
               final int level = 1;

               if (owner != null && owner.distanceToSqr(this) < range * range)
               {
                    owner.addEffect(new MobEffectInstance(MobEffects.LUCK, 419, level - 1, false, false, true));
               }
           }
        }
    }

    /**
     * Updates the position of the blockling's light source.
     *
     * @param removeOnly whether to only remove the light source and not replace it.
     */
    public void updateLightPos(boolean removeOnly)
    {
        if (!level.isClientSide)
        {
            if (currentLightPos != null)
            {
                level.removeBlock(currentLightPos, false);
            }

            if (!removeOnly && (naturalBlocklingType == BlocklingType.GLOWSTONE || blocklingType == BlocklingType.GLOWSTONE))
            {
                BlockPos blockPos = new BlockPos(position().add(0.0, 0.5 * getScale(), 0.0));

                for (BlockPos testPos : Arrays.asList(blockPos, blockPos.above(), blockPos.below(), blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()))
                {
                    BlockState blockState = level.getBlockState(blockPos = testPos);
                    Block block = blockState.getBlock();

                    if (blockState.isAir() || (currentLightPos == null && block == BlocklingsBlocks.LIGHT.get()))
                    {
                        level.setBlock(currentLightPos = blockPos, BlocklingsBlocks.LIGHT.get().defaultBlockState(), Block.UPDATE_ALL);

                        break;
                    }
                }
            }
        }
    }

    /**
     * Checks and updates any cooldowns if required.
     */
    private void checkAndUpdateCooldowns()
    {
        actions.ticks20.tryStart();
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

        actions.logRegenerationCooldown.tryStart();
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity target)
    {
        BlocklingHand attackingHand = actions.attack.getRecentHand();
        ItemStack mainStack = getMainHandItem();
        ItemStack offStack = getOffhandItem();
        Item mainItem = mainStack.getItem();
        Item offItem = offStack.getItem();

        boolean attackingWithMainHand = attackingHand == BlocklingHand.MAIN || attackingHand == BlocklingHand.BOTH;
        boolean attackingWithOffHand = attackingHand == BlocklingHand.OFF || attackingHand == BlocklingHand.BOTH;

        boolean mainHandTinkersTool = ToolUtil.isTinkersTool(mainStack);
        boolean offHandTinkersTool = ToolUtil.isTinkersTool(offStack);

        boolean hasHurt = false;

        float tinkersDamage = 0.0f;
        float damage = 0.0f;
        float knockback = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        int fireAspect = 0;

        if (target instanceof LivingEntity)
        {
            if (attackingWithMainHand)
            {
                if (mainHandTinkersTool && ToolUtil.isUseableTool(mainStack))
                {
                    if (TinkersConstructProxy.instance.attackEntity(mainStack, this, InteractionHand.MAIN_HAND, target, () -> 1.0, false))
                    {
                        tinkersDamage += stats.mainHandAttackDamage.getValue(); // This won't take into account Tinkers' modifiers but is good enough.
                        hasHurt = true;
                    }
                }
                else
                {
                    damage += stats.mainHandAttackDamage.getValue();
                    damage += ToolUtil.getToolEnchantmentDamage(mainStack, ((LivingEntity) target).getMobType());
                    knockback += ToolUtil.getToolKnockbackLevel(mainStack);
                    fireAspect += ToolUtil.getToolFireAspectLevel(mainStack);
                }
            }

            if (attackingWithOffHand)
            {
                if (offHandTinkersTool && ToolUtil.isUseableTool(offStack))
                {
                    if (TinkersConstructProxy.instance.attackEntity(offStack, this, InteractionHand.MAIN_HAND, target, () -> 1.0, false))
                    {
                        tinkersDamage += stats.offHandAttackDamage.getValue(); // This won't take into account Tinkers' modifiers but is good enough.
                        hasHurt = true;
                    }
                }
                else
                {
                    damage += stats.offHandAttackDamage.getValue();
                    damage += ToolUtil.getToolEnchantmentDamage(offStack, ((LivingEntity) target).getMobType());
                    knockback += ToolUtil.getToolKnockbackLevel(offStack);
                    fireAspect += ToolUtil.getToolFireAspectLevel(offStack);
                }
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
                livingTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
            }
            else if (skills.getSkill(CombatSkills.WITHER_ATTACKS).isBought())
            {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 60));
            }
        }

        AttributeInstance damageAttribute = getAttribute(Attributes.ATTACK_DAMAGE);
        Optional<AttributeModifier> strengthModifier = damageAttribute.getModifiers().stream().filter(attributeModifier -> attributeModifier.getId().equals(UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9"))).findFirst();

        if (strengthModifier.isPresent())
        {
            damage += strengthModifier.get().getAmount();
        }

        if (damage > 0)
        {
            int invulnerableTime = target.invulnerableTime;
            target.invulnerableTime = 0;
            hasHurt = target.hurt(DamageSource.mobAttack(this), damage);
            target.invulnerableTime = invulnerableTime;
        }

        if (hasHurt)
        {
            stats.combatXp.incrementValue((int) (damage + tinkersDamage) + 1);

            if (knockback > 0.0f)
            {
                ((LivingEntity) target).knockback(knockback * 0.5f, (double) Mth.sin(getYRot() * ((float) Math.PI / 180.0f)), (-Mth.cos(getYRot() * ((float) Math.PI / 180.0f))));
                setDeltaMovement(getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (target instanceof Player)
            {
                Player player = (Player) target;
                maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            doEnchantDamageEffects(this, target);
            setLastHurtMob(target);

            if (attackingWithMainHand)
            {
                tryDamageToolOnAttack(mainStack);
            }

            if (attackingWithOffHand)
            {
                tryDamageToolOnAttack(offStack);
            }

            incAttacksRecently();
        }

        return hasHurt;
    }

    /**
     * Attempts to damage the given stack in the context of an attack.
     *
     * @param stack the stack to damage.
     */
    public void tryDamageToolOnAttack(@Nonnull ItemStack stack)
    {
        Item item = stack.getItem();

        int damage = getSkills().getSkill(CombatSkills.WRECKLESS).isBought() ? 2 : 1;

        if (ToolUtil.isTinkersTool(item))
        {
            // Tinkers' will already have applied tool damage, but won't take the wreckless skill into account.
            damage--;

            // If the tool is not a weapon then double the damage.
            if (!ToolUtil.isWeapon(item))
            {
                damage *= 2;
            }

            TinkersConstructProxy.instance.damageTool(stack, damage, this);
        }
        else
        {
            if (!ToolUtil.isWeapon(item))
            {
                damage *= 2;
            }

            if (ToolUtil.damageTool(stack, this, damage))
            {
                stack.shrink(1);
            }
        }
    }

    /**
     * Copied from Mob as we need to run custom hurt target code but still need this functionality.
     */
    private void maybeDisableShield(Player p_21425_, ItemStack p_21426_, ItemStack p_21427_) {
        if (!p_21426_.isEmpty() && !p_21427_.isEmpty() && p_21426_.getItem() instanceof AxeItem && p_21427_.is(Items.SHIELD)) {
            float f = 0.25F + (float) EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                p_21425_.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level.broadcastEntityEvent(p_21425_, (byte)30);
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float damage)
    {
        boolean hurt = false;

        if (!((naturalBlocklingType == BlocklingType.NETHERITE || blocklingType == BlocklingType.NETHERITE) && getRandom().nextInt(10) == 0))
        {
            hurt = super.hurt(damageSource, damage);
        }

        if (hurt)
        {
            if (damageSource.getEntity() instanceof LivingEntity)
            {
                LivingEntity attacker = (LivingEntity) damageSource.getEntity();

                if (attacker != null)
                {
                    if (naturalBlocklingType == BlocklingType.QUARTZ || blocklingType == BlocklingType.QUARTZ)
                    {
                        attacker.hurt(DamageSource.mobAttack(this), damage / 15.0f);
                    }

                    if (naturalBlocklingType == BlocklingType.OBSIDIAN || blocklingType == BlocklingType.OBSIDIAN)
                    {
                        attacker.knockback(0.5f, (double) Mth.sin(getYRot() * ((float) Math.PI / 180.0f)), (-Mth.cos(getYRot() * ((float) Math.PI / 180.0f))));
                        setDeltaMovement(getDeltaMovement().multiply(0.6, 1.0, 0.6));
                    }
                }
            }
        }

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

                    setHealth(0.0f);

                    remove(RemovalReason.DISCARDED); // Remove now to avoid a regular death from occurring.
                }
            }
        }

        return hurt;
    }

    @Override
    @Nonnull
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand)
    {
        InteractionResult result;

        if (hand == InteractionHand.MAIN_HAND)
        {
            result = mobInteractMainHand(player);
        }
        else
        {
            result = mobInteractOffHand(player);
        }

        if (result != InteractionResult.PASS)
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
    private InteractionResult mobInteractMainHand(@Nonnull Player player)
    {
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Item item = stack.getItem();

        if (item == BLOCKLING_WHISTLE.get())
        {
            if (player == getOwner())
            {
                BlocklingWhistleItem.setBlockling(stack, this);

                return InteractionResult.SUCCESS;
            }
        }
        if (blocklingType.isFoodForType(item))
        {
            if (!level.isClientSide())
            {
                if (!isTame())
                {
                    tryTame((ServerPlayer) player, stack);

                    if (!player.getAbilities().instabuild)
                    {
                        stack.shrink(1);
                    }

                    return InteractionResult.SUCCESS;
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

                                if (!player.getInventory().add(blocklingStack))
                                {
                                    dropItemStack(blocklingStack);
                                }

                                BlocklingWhistleItem.onBlocklingDestroyed(this);

                                remove(RemovalReason.DISCARDED);

                                if (!player.getAbilities().instabuild)
                                {
                                    stack.shrink(1);
                                }

                                return InteractionResult.SUCCESS;
                            }
                        }
                    }

                    if (hasPlayerResetCrouchBetweenInteractions && skills.getSkill(GeneralSkills.HEAL).isBought())
                    {
                        if (getHealth() < getMaxHealth())
                        {
                            heal(random.nextInt(3) + 3);

                            level.broadcastEntityEvent(this, (byte) 7);

                            if (!player.getAbilities().instabuild)
                            {
                                stack.shrink(1);
                            }

                            return InteractionResult.SUCCESS;
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

                    if (!player.getAbilities().instabuild)
                    {
                        stack.shrink(1);
                    }
                }

                return InteractionResult.SUCCESS;
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

                return InteractionResult.CONSUME;
//            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Handles the player interacting with their off hand.
     *
     * @param player the interacting player.
     * @return the result of the interaction.
     */
    @Nonnull
    private InteractionResult mobInteractOffHand(@Nonnull Player player)
    {
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        Item item = stack.getItem();

        return InteractionResult.PASS;
    }

    /**
     * Attempts to tame the blockling with a 1 in 3 chance.
     *
     * @param player the player interacting with the blockling.
     * @param stack  the stack involved in the interaction.
     */
    private void tryTame(@Nonnull ServerPlayer player, @Nonnull ItemStack stack)
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

    }

    @Override
    public void tame(@Nonnull Player player)
    {
        super.tame(player);

        if (!hasCustomName())
        {
            setCustomName(new TextComponent("Blockling"), true);
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
        return getItemInHand(InteractionHand.MAIN_HAND);
    }

    @Override
    @Nonnull
    public ItemStack getOffhandItem()
    {
        return getItemInHand(InteractionHand.OFF_HAND);
    }

    @Override
    @Nonnull
    public ItemStack getItemInHand(@Nonnull InteractionHand hand)
    {
        return equipmentInv.getHandStack(hand);
    }

    @Override
    public void setItemInHand(@Nonnull InteractionHand hand, @Nonnull ItemStack stack)
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
    public boolean hasItemInSlot(@Nonnull EquipmentSlot slotType)
    {
        if (slotType == EquipmentSlot.MAINHAND)
        {
            return !getMainHandItem().isEmpty();
        }
        else if (slotType == EquipmentSlot.OFFHAND)
        {
            return !getOffhandItem().isEmpty();
        }

        return false;
    }

    @Override
    @Nonnull
    public ItemStack getItemBySlot(@Nonnull EquipmentSlot slotType)
    {
        if (slotType == EquipmentSlot.MAINHAND)
        {
            return getMainHandItem();
        }
        else if (slotType == EquipmentSlot.OFFHAND)
        {
            return getOffhandItem();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@Nonnull EquipmentSlot slotType, @Nonnull ItemStack stack)
    {
        if (slotType == EquipmentSlot.MAINHAND)
        {
            setItemInHand(InteractionHand.MAIN_HAND, stack);
        }
        else if (slotType == EquipmentSlot.OFFHAND)
        {
            setItemInHand(InteractionHand.OFF_HAND, stack);
        }
    }

    /**
     * @return true as the blockling needs to be created before it can decide whether it can spawn.
     */
    public static boolean checkBlocklingSpawnRules(EntityType<? extends Animal> p_223316_0_, LevelAccessor p_223316_1_, MobSpawnType p_223316_2_, BlockPos p_223316_3_, Random p_223316_4_)
    {
        return true;
    }

    @Override
    public boolean checkSpawnRules(@Nonnull LevelAccessor world, @Nonnull MobSpawnType reason)
    {
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION)
        {
            if (random.nextInt(blocklingType.spawnRateReduction) != 0)
            {
                return false;
            }

            if (!world.getBlockState(getOnPos()).getMaterial().isSolid())
            {
                return false;
            }

            final int radius = 64;
            List<BlocklingEntity> nearbyBlocklings = world.getEntitiesOfClass(BlocklingEntity.class, new AABB(getOnPos().getX() - radius, 0, getOnPos().getZ() - radius, getOnPos().getX() + radius, world.getHeight(), getOnPos().getZ() + radius));

            // Only allow 3 blocklings per 128 blocks.
            if (nearbyBlocklings.size() >= 3)
            {
                return false;
            }
            // If there are less than 3 then make sure they are all different types.
            else if (nearbyBlocklings.stream().anyMatch(blockling -> blockling.getBlocklingType() == blocklingType))
            {
                return false;
            }

            for (BiPredicate<BlocklingEntity, Level> predicate : getBlocklingType().spawnPredicates)
            {
                if (!predicate.test(this, (Level) world))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected boolean shouldDespawnInPeaceful()
    {
        return super.shouldDespawnInPeaceful();
    }

    @Override
    public void checkDespawn()
    {
        super.checkDespawn();
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_)
    {
        if (isTame())
        {
            return false;
        }

        return true;
    }

    @Override
    public float getEyeHeightAccess(@Nonnull Pose pose, @Nonnull EntityDimensions size)
    {
        return size.height * 0.45f;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@Nonnull ServerLevel world, @Nonnull AgeableMob entity)
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
        return naturalBlocklingType == BlocklingType.NETHERITE || blocklingType == BlocklingType.NETHERITE || naturalBlocklingType == BlocklingType.OBSIDIAN || blocklingType == BlocklingType.OBSIDIAN;
    }

    /**
     * Sets the name of the blockling.
     * Does NOT sync to client/server.
     *
     * @param name the new name.
     */
    @Override
    public void setCustomName(@Nullable Component name)
    {
        if (name != null)
        {
            name = new TextComponent(name.getString());
        }

        setCustomName((TextComponent) name, false);
    }

    /**
     * Sets the name of the blockling.
     * Syncs to the server if set from the client and sync is true.
     *
     * @param name the new name.
     * @param sync whether to sync to the server from the client.
     */
    public void setCustomName(@Nullable TextComponent name, boolean sync)
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
    public BlocklingType getNaturalBlocklingType()
    {
        return naturalBlocklingType;
    }

    /**
     * Sets the natural blockling type to the given blockling type.
     * Syncs to the client/server.
     *
     * @param blocklingType the new blockling type.
     */
    public void setNaturalBlocklingType(@Nonnull BlocklingType blocklingType)
    {
        setNaturalBlocklingType(blocklingType, true);
    }

    /**
     * Sets the natural blockling type to the given blockling type.
     * Syncs to the client/server if sync is true.
     *
     * @param blocklingType the new blockling type.
     * @param sync whether to sync to the client/server.
     */
    public void setNaturalBlocklingType(@Nonnull BlocklingType blocklingType, boolean sync)
    {
        this.naturalBlocklingType = blocklingType;

        stats.updateTypeBonuses(sync);

        if (sync)
        {
            new BlocklingTypeMessage(this, blocklingType, true).sync();
        }
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
            new BlocklingTypeMessage(this, blocklingType, false).sync();
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
            stats.attackSpeedSkillMomentumModifier.setValue((float) cappedCount / 2.0f);
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
