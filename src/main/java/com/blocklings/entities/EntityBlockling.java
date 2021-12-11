package com.blocklings.entities;

import com.blocklings.abilities.AbilityGroup;
import com.blocklings.abilities.AbilityGroupType;
import com.blocklings.abilities.AbilityHelper;
import com.blocklings.inventories.InventoryBlockling;
import com.blocklings.items.ItemBlockling;
import com.blocklings.main.Blocklings;
import com.blocklings.network.*;
import com.blocklings.util.BlocklingType;
import com.blocklings.util.helpers.*;
import com.blocklings.util.helpers.GuiHelper.Tab;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EntityBlockling extends EntityTameable implements IEntityAdditionalSpawnData
{
    public static final Random RANDOM = new Random();

    public enum AnimationState { IDLE, ATTACKING, MINING }

    public static final double BASE_MAX_HEALTH = 10;
    public static final double BASE_MOVEMENT_SPEED = 0.5;
    public static final double BASE_ATTACK_DAMAGE = 1.0;

    public BlocklingType blocklingType = BlocklingType.blocklingTypes.get(0);

    public boolean isInAttackRange;

    public InventoryBlockling inv;
    private int unlockedSlots = 12;

    public AbilityGroup generalAbilities;
    public AbilityGroup combatAbilities;
    public AbilityGroup miningAbilities;
    public AbilityGroup woodcuttingAbilities;
    public AbilityGroup farmingAbilities;

    public boolean isInGui = false;

    private float scale;

    private AnimationState animationState = AnimationState.IDLE;

    private int guiID = 1;

    private EntityHelper.Task task = EntityHelper.Task.IDLE;
    private EntityHelper.Guard guard = EntityHelper.Guard.NOGUARD;
    private EntityHelper.State state = EntityHelper.State.WANDER;

    private int skillPoints = 0;
    private int combatLevel = 1, miningLevel = 1, woodcuttingLevel = 1, farmingLevel = 1;
    private int combatXp = 0, miningXp = 0, woodcuttingXp = 0, farmingXp = 0;
    private int attackInterval = 10, miningInterval = 20, choppingInterval = 20, farmingInterval = 20;
    private int attackTimer = -1, miningTimer = -1;
    private EnumHand attackingHand = EnumHand.MAIN_HAND;

    private byte autoswitchID = 0;

    private BlocklingAIFollowOwner aiFollow;
    private BlocklingAIWanderAvoidWater aiWander;

    private BlocklingAIAttackMelee aiAttackMelee;
    private BlocklingAIOwnerHurtByTarget aiOwnerHurtBy;
    private BlocklingAIOwnerHurtTarget aiOwnerHurt;

    private ItemStack leftHandStack = ItemStack.EMPTY, rightHandStack = ItemStack.EMPTY;

    private BlocklingAIMining aiMining;
    private BlocklingAIWoodcutting aiWoodcutting;
    private BlocklingAIFarming aiFarming;
    private BlocklingAIHunt aiHunt;

    // CLIENT SERVER
    public EntityBlockling(World worldIn)
    {
        super(worldIn);
    }

    // CLIENT SERVER
    @Override
    protected void entityInit()
    {
        super.entityInit();

        setupInventory();

        unlockedSlots = 12;

        if ((generalAbilities == null || generalAbilities.abilities.size() == 0))
        {
            generalAbilities = new AbilityGroup(AbilityGroupType.GENERAL);
            combatAbilities = new AbilityGroup(AbilityGroupType.COMBAT);
            miningAbilities = new AbilityGroup(AbilityGroupType.MINING);
            woodcuttingAbilities = new AbilityGroup(AbilityGroupType.WOODCUTTING);
            farmingAbilities = new AbilityGroup(AbilityGroupType.FARMING);
        }

        if (!world.isRemote)
        {
            do
            {
                scale = 1.0f + ((float) RANDOM.nextGaussian() / 15.0f);
            }
            while (scale < 0.6f || scale > 1.4f);
        }
        setSize(EntityHelper.BASE_SCALE_FOR_HITBOX * scale, EntityHelper.BASE_SCALE_FOR_HITBOX * scale);
    }

    /**
     * Returns new PathNavigateGround instance
     */
    @Override
    protected PathNavigate createNavigator(World worldIn)
    {
        return new PathNavigateGroundBlockling(this, worldIn);
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();

        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(BASE_MAX_HEALTH);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BASE_MOVEMENT_SPEED);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BASE_ATTACK_DAMAGE);
    }

    @Override
    protected void initEntityAI()
    {
        aiSit = new EntityAISit(this);
        aiOwnerHurtBy = new BlocklingAIOwnerHurtByTarget(this);
        aiOwnerHurt = new BlocklingAIOwnerHurtTarget(this);
        aiAttackMelee = new BlocklingAIAttackMelee(this, true);
        aiFollow = new BlocklingAIFollowOwner(this, 2, 8);
        aiWander = new BlocklingAIWanderAvoidWater(this, 1.0F);
        aiMining = new BlocklingAIMining(this);
        aiWoodcutting = new BlocklingAIWoodcutting(this);
        aiFarming = new BlocklingAIFarming(this);
        aiHunt = new BlocklingAIHunt(this);

        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, aiSit);
        this.tasks.addTask(3, aiAttackMelee);
        this.tasks.addTask(4, aiMining);
        this.tasks.addTask(4, aiWoodcutting);
        this.tasks.addTask(4, aiFarming);
        this.tasks.addTask(6, aiFollow);
        this.tasks.addTask(8, aiWander);
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, aiOwnerHurtBy);
        this.targetTasks.addTask(2, aiOwnerHurt);
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(4, aiHunt);
    }

    private void updateAI()
    {
        setSitting(state == EntityHelper.State.STAY);
    }

    @Override
    public void setAttackTarget(EntityLivingBase ent)
    {
        super.setAttackTarget(ent);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn)
    {
        double damage = getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int fireAspect = 0;
        int knockBack = 0;

        if (hasTool())
        {
            List<NBTTagCompound> enchantments = ToolHelper.getEnchantmentTagsFromTool(getHeldItem(calculateAttackingHand()));
            for (NBTTagCompound enchantmentTag : enchantments)
            {
                ToolHelper.Enchantment enchantment = ToolHelper.Enchantment.getEnchantmentFromTag(enchantmentTag);
                if (enchantment == ToolHelper.Enchantment.SHARPNESS)
                {
                    damage += 1 + ((enchantmentTag.getInteger("lvl") - 1) * 0.5);
                }
                if (enchantment == ToolHelper.Enchantment.BANEOFARTHROPODS)
                {
                    if (entityIn instanceof EntitySpider)
                    {
                        damage += enchantmentTag.getInteger("lvl") * 2.5;
                    }
                }
                if (enchantment == ToolHelper.Enchantment.SMITE)
                {
                    if (entityIn instanceof EntityZombie)
                    {
                        damage += enchantmentTag.getInteger("lvl") * 2.5;
                    }
                }
                if (enchantment == ToolHelper.Enchantment.FIREASPECT)
                {
                    fireAspect += enchantmentTag.getInteger("lvl") * 4;
                }
                if (enchantment == ToolHelper.Enchantment.KNOCKBACK)
                {
                    knockBack += enchantmentTag.getInteger("lvl");
                }
            }

            damageItem(calculateAttackingHand());
        }

        if (damage > ((EntityLiving)entityIn).getHealth())
        {
            damage = Math.ceil(((EntityLiving)entityIn).getHealth());
        }

        float angle = 90.0f;
        float blocklingYaw = this.rotationYaw < 180.0f ? this.rotationYaw + 360.0f : this.rotationYaw;
        float entityYaw = entityIn.rotationYaw < 180.0f ? entityIn.rotationYaw + 360.0f : entityIn.rotationYaw;

        if (blocklingYaw - angle / 2.0f < entityYaw && blocklingYaw + angle / 2.0f > entityYaw)
        {
            if (combatAbilities.isAbilityAcquired(AbilityHelper.shinobi2))
            {
                damage *= 3;
            }
            else if (combatAbilities.isAbilityAcquired(AbilityHelper.shinobi1))
            {
                damage *= 2;
            }
        }

        ((EntityLiving)entityIn).knockBack(this, knockBack,1,1);
        entityIn.setFire(fireAspect);
        entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)damage));

        incrementCombatXp((int)(damage / 4.0f) + 1);

        setAttackingHand(attackingHand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        float chanceToBlock = this.combatAbilities.isAbilityAcquired(AbilityHelper.thickSkin3) ? 0.2f :
        this.combatAbilities.isAbilityAcquired(AbilityHelper.thickSkin2) ? 0.1f :
        this.combatAbilities.isAbilityAcquired(AbilityHelper.thickSkin1) ? 0.05f : 0.0f;

        if (chanceToBlock != 0)
        {
            if (rand.nextFloat() < chanceToBlock)
            {
                return false;
            }
        }

        return super.attackEntityFrom(source, amount);
    }

    public void updateIsInAttackRange()
    {
        if (getAttackTarget() != null)
        {
            double sqDist = getDistanceSq(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);
            double dist = (double)(this.width * 2.0F * this.width * 2.0F + getAttackTarget().width);
            this.isInAttackRange = sqDist <= dist * dist;
        }
        else
        {
            this.isInAttackRange = false;
        }

        NetworkHelper.sync(this.world, new IsInAttackRangeMessage(this.isInAttackRange, this.getEntityId()));
    }

    public EnumHand calculateAttackingHand()
    {
        if (hasWeapon(EnumHand.MAIN_HAND) && hasWeapon(EnumHand.OFF_HAND))
        {
            return attackingHand;
        }
        else if (hasWeapon(EnumHand.MAIN_HAND))
        {
            return EnumHand.MAIN_HAND;
        }
        else if (hasWeapon(EnumHand.OFF_HAND))
        {
            return EnumHand.OFF_HAND;
        }
        else if (hasTool(EnumHand.MAIN_HAND) && hasTool(EnumHand.OFF_HAND))
        {
            return attackingHand;
        }

        return hasTool(EnumHand.MAIN_HAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    public boolean hasCorrectToolForJob() { return hasCorrectToolForJob(EnumHand.MAIN_HAND) || hasCorrectToolForJob(EnumHand.OFF_HAND); }
    public boolean hasCorrectToolForJob(EnumHand hand)
    {
        if (task == EntityHelper.Task.MINE)
        {
            return hasPickaxe(hand);
        }
        else if (task == EntityHelper.Task.CHOP)
        {
            return hasAxe(hand);
        }
        else if (task == EntityHelper.Task.FARM)
        {
            return hasHoe(hand);
        }

        return false;
    }

    // Used to save entity data (variables) when the entity is unloaded
    // SERVER
    @Override
    public void writeEntityToNBT(NBTTagCompound c)
    {
        super.writeEntityToNBT(c);

        c.setString("Name", getCustomNameTag());

        c.setString("BlocklingType", blocklingType.textureName);

        c.setInteger("UnlockedSlots", unlockedSlots);
        c.setFloat("Scale", scale);
        c.setInteger("GuiID", guiID);

        c.setByte("AutoswitchID", autoswitchID);

        c.setInteger("TaskID", task.id);
        c.setInteger("GuardID", guard.id);
        c.setInteger("StateID", state.id);

        c.setInteger("CombatLevel", combatLevel);
        c.setInteger("MiningLevel", miningLevel);
        c.setInteger("WoodcuttingLevel", woodcuttingLevel);
        c.setInteger("FarmingLevel", farmingLevel);

        c.setInteger("CombatXp", combatXp);
        c.setInteger("MiningXp", miningXp);
        c.setInteger("WoodcuttingXp", woodcuttingXp);
        c.setInteger("FarmingXp", farmingXp);

        generalAbilities.writeToNBT(c);
        combatAbilities.writeToNBT(c);
        miningAbilities.writeToNBT(c);
        woodcuttingAbilities.writeToNBT(c);
        farmingAbilities.writeToNBT(c);

        c.setInteger("SkillPoints", skillPoints);

        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.inv.getSizeInventory(); i++)
        {
            ItemStack itemstack = this.inv.getStackInSlot(i);
            if (itemstack != null && !itemstack.isEmpty())
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("slot", (byte) i);
                itemstack.writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        c.setTag("items", nbttaglist);
    }

    // Used to load entity data (variables) when the entity is loaded
    // SERVER
    @Override
    public void readEntityFromNBT(NBTTagCompound c)
    {
        super.readEntityFromNBT(c);

        setCustomNameTag(c.getString("Name"));

        blocklingType = BlocklingType.getTypeFromTextureName(c.getString("BlocklingType"));

        unlockedSlots = c.getInteger("UnlockedSlots");
        scale = c.getFloat("Scale");
        guiID = c.getInteger("GuiID");

        autoswitchID = c.getByte("AutoswitchID");

        task = EntityHelper.Task.getFromID(c.getInteger("TaskID"));
        guard = EntityHelper.Guard.getFromID(c.getInteger("GuardID"));
        state = EntityHelper.State.getFromID(c.getInteger("StateID"));

        combatLevel = c.getInteger("CombatLevel");
        miningLevel = c.getInteger("MiningLevel");
        woodcuttingLevel = c.getInteger("WoodcuttingLevel");
        farmingLevel = c.getInteger("FarmingLevel");

        combatXp = c.getInteger("CombatXp");
        miningXp = c.getInteger("MiningXp");
        woodcuttingXp = c.getInteger("WoodcuttingXp");
        farmingXp = c.getInteger("FarmingXp");

        generalAbilities.initFromNBT(c);
        combatAbilities.initFromNBT(c);
        miningAbilities.initFromNBT(c);
        woodcuttingAbilities.initFromNBT(c);
        farmingAbilities.initFromNBT(c);

        skillPoints = c.getInteger("SkillPoints");

        NBTTagList tag = c.getTagList("items", 10);
        for (int i = 0; i < tag.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = tag.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("slot") & 0xFF;
            if ((j >= 0) && (j < this.inv.getSizeInventory())) {
                this.inv.setInventorySlotContents(j, new ItemStack(nbttagcompound1));
            }
        }

        setHeldItem(getHeldItemMainhand(), EnumHand.MAIN_HAND);
        setHeldItem(getHeldItemOffhand(), EnumHand.OFF_HAND);

        setSize(EntityHelper.BASE_SCALE_FOR_HITBOX * scale, EntityHelper.BASE_SCALE_FOR_HITBOX * scale);
        updateAI();
    }

    // Used to save the data (variables) that need to be synced on spawn
    // SERVER
    @Override
    public void writeSpawnData(ByteBuf buf)
    {
        AbilityHelper.writeSpawnData(buf, this);

        ByteBufUtils.writeUTF8String(buf, blocklingType.textureName);

        buf.writeInt(unlockedSlots);
        buf.writeFloat(scale);
        buf.writeInt(animationState.ordinal());
        buf.writeInt(guiID);

        buf.writeByte(autoswitchID);

        buf.writeInt(task.id);
        buf.writeInt(guard.id);
        buf.writeInt(state.id);

        buf.writeInt(combatLevel);
        buf.writeInt(miningLevel);
        buf.writeInt(woodcuttingLevel);
        buf.writeInt(farmingLevel);

        buf.writeInt(combatXp);
        buf.writeInt(miningXp);
        buf.writeInt(woodcuttingXp);
        buf.writeInt(farmingXp);

        buf.writeInt(skillPoints);

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ByteBufUtils.writeItemStack(buf, inv.getStackInSlot(i));
        }
    }

    // Used to sync client with server on spawn
    // CLIENT
    @Override
    public void readSpawnData(ByteBuf buf)
    {
        AbilityHelper.readSpawnData(buf, this);

        blocklingType = BlocklingType.getTypeFromTextureName(ByteBufUtils.readUTF8String(buf));

        unlockedSlots = buf.readInt();
        scale = buf.readFloat();
        animationState = AnimationState.values()[buf.readInt()];
        guiID = buf.readInt();

        autoswitchID = buf.readByte();

        task = EntityHelper.Task.getFromID(buf.readInt());
        guard = EntityHelper.Guard.getFromID(buf.readInt());
        state = EntityHelper.State.getFromID(buf.readInt());

        combatLevel = buf.readInt();
        miningLevel = buf.readInt();
        woodcuttingLevel = buf.readInt();
        farmingLevel = buf.readInt();

        combatXp = buf.readInt();
        miningXp = buf.readInt();
        woodcuttingXp = buf.readInt();
        farmingXp = buf.readInt();

        skillPoints = buf.readInt();

        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            inv.setInventorySlotContents(i, ByteBufUtils.readItemStack(buf));
        }

        setSize(EntityHelper.BASE_SCALE_FOR_HITBOX * scale, EntityHelper.BASE_SCALE_FOR_HITBOX * scale);
        updateAI();
    }

    private int regenTimer = -1;

    // Called once every tick
    // Used by skeles to check if they are in the sun
    // CLIENT SERVER
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        updateAI();

        if (eatTimer >= 0)
        {
            eatTimer--;
        }
        else if (eatTimer == -1)
        {
            if (world.isRemote) eatUpgradeMaterial();
            eatTimer = -2;
        }

        if (!world.isRemote)
        {
            checkTimers();
        }

        if (getAutoswitchLeft())
        {
            switchTool(EnumHand.MAIN_HAND);
        }
        if (getAutoswitchRight())
        {
            switchTool(EnumHand.OFF_HAND);
        }

        int regenLevel = combatAbilities.isAbilityAcquired(AbilityHelper.regen3) ? 4 : combatAbilities.isAbilityAcquired(AbilityHelper.regen2) ? 2 : combatAbilities.isAbilityAcquired(AbilityHelper.regen1) ? 1 : 0;
        if (regenLevel != 0)
        {
            if (regenTimer <= -1)
            {
                heal(regenLevel);
                regenTimer = 200;
            }
            else
            {
                regenTimer--;
            }
        }
        else
        {
            regenTimer = -1;
        }

        double enderBoye = this.generalAbilities.isAbilityAcquired(AbilityHelper.enderBoye2) ? 0.05 : this.generalAbilities.isAbilityAcquired(AbilityHelper.enderBoye1) ? 0.02 : 0;
        if (enderBoye != 0)
        {
            if (getNavigator().getPath() != null && rand.nextFloat() <= enderBoye)
            {
                PathPoint finalPoint = getNavigator().getPath().getFinalPathPoint();
                setLocationAndAngles(finalPoint.x + 0.5, finalPoint.y, finalPoint.z + 0.5, this.rotationYaw, this.rotationPitch);
            }
        }

        if (!this.world.isRemote)
        {
            if (this.farmingAbilities.isAbilityAcquired(AbilityHelper.natureAura))
            {
                for (int x = -1; x < 2; x++)
                {
                    for (int z = -1; z < 2; z++)
                    {
                        BlockPos blockPos = new BlockPos(this.posX + x, this.posY + 0.5, this.posZ + z);
                        if (this.world.getBlockState(blockPos).getBlock() instanceof BlockCrops && rand.nextFloat() < 0.002f)
                        {
                            BlockCrops cropBlock = ((BlockCrops)this.world.getBlockState(blockPos).getBlock());
                            cropBlock.grow(world, rand, blockPos, world.getBlockState(blockPos));
                            world.playEvent(2005, blockPos, 0);
                        }
                    }
                }
            }

//            if (true)
//            {
//                if (rand.nextFloat() < 0.1f)
//                {
//                    List<Entity> entities = this.world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().expand(2, 2, 2).expand(-2, -2, -2), null);
//                    for (Entity entity : entities)
//                    {
//                        if (entity instanceof EntityBlockling || entity instanceof EntityPlayer || entity instanceof EntityVillager)
//                        {
//                            continue;
//                        }
//
//                        if (entity instanceof EntityMob || entity instanceof EntityCreature)
//                        {
//                            entity.setFire(2);
//                        }
//                    }
//                }
//            }
        }

        checkAbilities();
        checkBonusStats();

        if (!this.world.isRemote)
        {
            updateIsInAttackRange();
        }
    }

    // Also called once every tick
    // Not sure what the difference is between the two update methods
    // CLIENT SERVER
    @Override
    public void onUpdate()
    {
        super.onUpdate();
    }

    // Called when a player interacts (right clicks) on entity
    // Is called on both client and server
    // And called for each hand
    // Client and server is useful for taming
    // This is because we want to set tamed on server but also play effects client side etc
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        boolean isMainHand = hand.equals(EnumHand.MAIN_HAND);

        if (isMainHand)
        {
            if (!player.isSneaking())
            {
                if (!isTamed())
                {
                    if (player != getOwner())
                    {
                        if (ItemHelper.isFlower(item))
                        {
                            if (!player.capabilities.isCreativeMode) stack.shrink(1);
                            if (!world.isRemote)
                            {
                                playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.3f, 1.5f);
                                if (rand.nextInt(3) == 0)
                                {
                                    setTamed(player);
                                }
                                else
                                {
                                    playTameEffect(false);
                                    world.setEntityState(this, (byte) 6);
                                }
                            }
                        }
                    }
                }
                else // Is tamed
                {
                    if (player == getOwner())
                    {
                        if (ItemHelper.isFlower(item))
                        {
                            if (this.generalAbilities.isAbilityAcquired(AbilityHelper.botanist))
                            {
                                if (getHealth() < getMaxHealth())
                                {
                                    heal(1.0f);
                                    if (!world.isRemote) playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.3f, 1.5f);
                                    if (!player.capabilities.isCreativeMode) stack.shrink(1);
                                }
                            }
                        }
                        else if (ToolHelper.isTool(item))
                        {
                            setHeldItemFromInteract(stack, EnumHand.MAIN_HAND, player);
                        }
                        else if (item == Items.EXPERIENCE_BOTTLE)
                        {
                            setCombatLevel(99);
                            setMiningLevel(99);
                            setWoodcuttingLevel(99);
                            setFarmingLevel(99);
                            setSkillPoints(99);
                        }
                        else
                        {
                            if (!world.isRemote)
                            {
                                setState(state == EntityHelper.State.STAY ? EntityHelper.State.FOLLOW : EntityHelper.State.STAY);
                            }
                            else
                            {
                                world.playSound(posX, posY, posZ, new SoundEvent(new ResourceLocation("block.lava.pop")), SoundCategory.AMBIENT, 50, 100, false);
                            }
                        }
                    }
                }
            }
            else // Is sneaking
            {
                if (isTamed())
                {
                    if (player == getOwner())
                    {
                        if (ItemHelper.isFlower(item))
                        {
                            if (generalAbilities.isAbilityAcquired(AbilityHelper.packling))
                            {
                                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                                ItemStack blocklingStack = ItemBlockling.createStack(this);
                                if (!player.addItemStackToInventory(blocklingStack))
                                {
                                    entityDropItem(blocklingStack, 0.0f);
                                }
                                setDead();
                            }
                            else
                            {
                                openGui(player);
                            }
                        }
                        else if (ToolHelper.isTool(item))
                        {
                            setHeldItemFromInteract(stack, EnumHand.OFF_HAND, player);
                        }
                        else
                        {
                            openGui(player);
                        }
                    }
                }
            }
        }
        else
        {

        }

        return super.processInteract(player, hand);
    }

    @Override
    public void heal(float healAmount)
    {
        super.heal(healAmount);

        if (getHealth() < getMaxHealth())
        {
            if (world.isRemote)
            {
                playTameEffect(true);
            }

            if (generalAbilities.isAbilityAcquired(AbilityHelper.flowerPower))
            {
                if (!world.isRemote)
                {
                    world.spawnEntity(new EntityXPOrb(world, posX, posY, posZ, 1));
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause)
    {
        if (generalAbilities.isAbilityAcquired(AbilityHelper.armadillo))
        {
            if (!world.isRemote)
            {
                ItemStack blocklingStack = ItemBlockling.createStack(this);
                blocklingStack.getTagCompound().setFloat("Health", 1.0f);
                entityDropItem(blocklingStack, 0.0f);
            }
        }
        super.onDeath(cause);
    }

    private void setHeldItemFromInteract(ItemStack stack, EnumHand hand, EntityPlayer player)
    {
        int index = hand == EnumHand.MAIN_HAND ? GuiHelper.TOOL_SLOT_LEFT : GuiHelper.TOOL_SLOT_RIGHT;
        ItemStack currentStack = inv.getStackInSlot(index);

        if (!stack.isEmpty()) playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.5f, 1.25f);
        player.setHeldItem(EnumHand.MAIN_HAND, currentStack);
        setHeldItem(stack, hand);
    }

    @Override
    public ItemStack getHeldItem(EnumHand hand)
    {
        if (hand == EnumHand.MAIN_HAND)
        {
            return getHeldItemMainhand();
        }
        else if (hand == EnumHand.OFF_HAND)
        {
            return getHeldItemOffhand();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getHeldItemOffhand()
    {
        return inv.getStackInSlot(GuiHelper.TOOL_SLOT_RIGHT);
    }

    @Override
    public ItemStack getHeldItemMainhand()
    {
        return inv.getStackInSlot(GuiHelper.TOOL_SLOT_LEFT);
    }

    private void setHeldItem(ItemStack stack, EnumHand hand)
    {
        if (stack != null && !stack.isEmpty())
        {
            int index = hand == EnumHand.MAIN_HAND ? GuiHelper.TOOL_SLOT_LEFT : GuiHelper.TOOL_SLOT_RIGHT;
            inv.setInventorySlotContents(index, stack);
        }
    }

    private int eatTimer = 0;

    public void startEatingUpgradeMaterial(ItemStack itemStack)
    {
        if (!itemStack.isEmpty())
        {
            eatTimer = 20;
        }
    }

    private void eatUpgradeMaterial()
    {
        ItemStack itemStack = inv.getStackInSlot(GuiHelper.UPGRADE_SLOT);

        if (itemStack == ItemStack.EMPTY || itemStack.getItem() == Items.AIR)
        {
            return;
        }

        BlocklingType blocklingType = BlocklingType.getTypeFromItemStack(itemStack);
        if (blocklingType == null || this.blocklingType == blocklingType)
        {
            return;
        }

        itemStack.setCount(0);
        NetworkHelper.sync(world, new InvItemStackMessage(itemStack, GuiHelper.UPGRADE_SLOT, getEntityId()));
        if (rand.nextFloat() < 0.25f)
        {
            this.blocklingType = blocklingType;
            playSoundEffect("block.anvil.use");
            NetworkHelper.sync(world, new BlocklingTypeMessage(blocklingType, getEntityId()));
        }
        else
        {
            playSoundEffect("block.anvil.hit");
        }
    }

    public void setBlocklingTypeFromPacketOnServer(BlocklingType type)
    {
        blocklingType = type;
        NetworkHelper.sync(world, new BlocklingTypeMessage(blocklingType, getEntityId()));
    }

    public void playSoundEffect(String sound)
    {
        if (world.isRemote) world.playSound(posX, posY, posZ, new SoundEvent(new ResourceLocation(sound)), SoundCategory.AMBIENT, 50, 100, false);
    }

    public void damageItem(EnumHand hand)
    {
        getHeldItem(hand).damageItem(1, this);
        if (getHeldItem(hand).getCount() <= 0 || getHeldItem(hand) == ItemStack.EMPTY || getHeldItem(hand).getItemDamage() > getHeldItem(hand).getMaxDamage())
        {
            NetworkHelper.sync(world, new InvItemStackMessage(getHeldItem(hand), hand == EnumHand.MAIN_HAND ? GuiHelper.TOOL_SLOT_RIGHT : GuiHelper.TOOL_SLOT_LEFT, getEntityId()));
        }
    }

    private boolean switchTool(EnumHand hand)
    {
        float bestValue = -1.0f;
        int slot = -1;
        int handSlot = hand == EnumHand.MAIN_HAND ? GuiHelper.TOOL_SLOT_LEFT : GuiHelper.TOOL_SLOT_RIGHT;
        ItemStack bestStack = getHeldItem(hand);
        boolean hasSwapped = false;

        if (isAttacking())
        {
            bestValue = ToolHelper.getToolAttackPower(bestStack);
        }
        else if (task == EntityHelper.Task.MINE)
        {
            bestValue = ToolHelper.getPickaxeLevel(bestStack.getItem());
        }
        else if (task == EntityHelper.Task.CHOP)
        {
            bestValue = ToolHelper.getAxeLevel(bestStack.getItem());
        }
        else if (task == EntityHelper.Task.FARM)
        {
            bestValue = ToolHelper.getHoeLevel(bestStack.getItem());
        }
        else if (isSetToAttack())
        {
            bestValue = ToolHelper.getToolAttackPower(bestStack);
        }

        for (int i = GuiHelper.TOOL_SLOT_RIGHT + 1; i < inv.getSizeInventory(); i++)
        {
            ItemStack invStack = inv.getStackInSlot(i);
            if (isAttacking())
            {
                float newValue = ToolHelper.getToolAttackPower(invStack);
                if (newValue > bestValue)
                {
                    bestValue = newValue;
                    bestStack = invStack;
                    slot = i;
                }
            }
            else if (task == EntityHelper.Task.MINE)
            {
                int newValue = ToolHelper.getPickaxeLevel(invStack.getItem());
                if (newValue > bestValue)
                {
                    bestValue = newValue;
                    bestStack = invStack;
                    slot = i;
                }
            }
            else if (task == EntityHelper.Task.CHOP)
            {
                int newValue = ToolHelper.getAxeLevel(invStack.getItem());
                if (newValue > bestValue)
                {
                    bestValue = newValue;
                    bestStack = invStack;
                    slot = i;
                }
            }
            else if (task == EntityHelper.Task.FARM)
            {
                int newValue = ToolHelper.getHoeLevel(invStack.getItem());
                if (newValue > bestValue)
                {
                    bestValue = newValue;
                    bestStack = invStack;
                    slot = i;
                }
            }
            else if (isSetToAttack())
            {
                float newValue = ToolHelper.getToolAttackPower(invStack);
                if (newValue > bestValue)
                {
                    bestValue = newValue;
                    bestStack = invStack;
                    slot = i;
                }
            }
        }

        if (slot != -1)
        {
            ItemStack handStackCopy = getHeldItem(hand).copy();
            inv.setInventorySlotContents(handSlot, bestStack);
            inv.setInventorySlotContents(slot, handStackCopy);
            hasSwapped = true;
        }

        return hasSwapped;
    }

    @Override
    protected void updateEquipmentIfNeeded(EntityItem itemEntity)
    {
        ItemStack stack = itemEntity.getItem();
        itemEntity.setItem(inv.addItem(stack));
    }

    @Override
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier)
    {
        if (!generalAbilities.isAbilityAcquired(AbilityHelper.armadillo))
        {
            for (int i = 0; i < inv.getSizeInventory(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    entityDropItem(stack, 0.0f);
                }
            }
        }
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable)
    {
        return null;
    }

    private void checkAbilities()
    {
        setUnlockedSlots(generalAbilities.isAbilityAcquired(AbilityHelper.mule2) ? 36 : generalAbilities.isAbilityAcquired(AbilityHelper.mule1) ? 24 : 12);

        if (generalAbilities.isAbilityAcquired(AbilityHelper.outline))
        {
            addPotionEffect(new PotionEffect(Potion.getPotionById(24), 20, 0, false, false));
        }
    }

    public void openGui(EntityPlayer player)
    {
        if (world.isRemote)
        {
            if (guiID != Tab.INVENTORY.id && guiID != Tab.EQUIPMENT.id)
                player.openGui(Blocklings.instance, guiID, world, getEntityId(), 0, 0);
        }
        else
        {
            if (guiID == Tab.INVENTORY.id)
                player.openGui(Blocklings.instance, guiID, world, getEntityId(), 0, 0);
            else if (guiID == Tab.EQUIPMENT.id)
                player.openGui(Blocklings.instance, guiID, world, getEntityId(), 0, 0);
        }
    }

    /**
     * Updates the guiID in both sides, then opens whatever it needs to on both sides.
     */
    public void openGui(int guiID, EntityPlayer player)
    {
        setGuiID(guiID);
        openGui(player);
        NetworkHelper.sync(world, new OpenGuiMessage(getEntityId()));
    }

    private void setTamed(EntityPlayer player)
    {
        setTamedBy(player);
        navigator.clearPath();
        setAttackTarget(null);
        //aiSit.setSitting(true);
        setState(EntityHelper.State.FOLLOW);
        playTameEffect(true);
        world.setEntityState(this, (byte) 7);
        setName(!getCustomNameTag().equals("") ? getCustomNameTag() : "Blockling");
    }

    private void setupInventory()
    {
        InventoryBlockling invTemp = inv;
        inv = new InventoryBlockling(this, "Inventory", 39);
        inv.setCustomName("Blockling Inventory");

        if (invTemp != null)
        {
            int slotsToCheck = Math.min(invTemp.getSizeInventory(), inv.getSizeInventory());

            for (int i = 0; i < slotsToCheck; i++)
            {
                ItemStack stack = invTemp.getStackInSlot(i);

                if (stack != null)
                {
                    inv.setInventorySlotContents(i, stack);
                }
            }
        }
    }

    public void syncAbilities()
    {
        if (generalAbilities == null || combatAbilities == null || miningAbilities == null || woodcuttingAbilities == null || farmingAbilities == null)
        {
            return;
        }

        NetworkHelper.sync(world, new AbilitiesMessage(generalAbilities, combatAbilities, miningAbilities, woodcuttingAbilities, farmingAbilities, getEntityId()));
    }

    private void checkTimers()
    {
        if (isAttacking())
        {
            setAnimationState(AnimationState.ATTACKING);
        }
        else if (isMining())
        {
            setAnimationState(AnimationState.MINING);
        }
        else
        {
            setAnimationState(AnimationState.IDLE);
        }

        if (attackTimer >= 0)
        {
            incrementAttackTimer();
            if (attackTimer > attackInterval)
            {
                stopAttacking();
            }
        }

        if (miningTimer >= 0)
        {
            incrementMiningTimer();
            if (miningTimer > getCorrectInterval())
            {
                stopMining();
            }
        }
    }

    private int getCorrectInterval()
    {
        if (task == EntityHelper.Task.MINE)
        {
            return miningInterval;
        }
        else if (task == EntityHelper.Task.CHOP)
        {
            return choppingInterval;
        }
        else if (task == EntityHelper.Task.FARM)
        {
            return farmingInterval;
        }

        return 40;
    }

    public void startAttacking()
    {
        setAttackTimer(0);
    }

    public void stopAttacking()
    {
        setAttackTimer(-1);
    }

    public boolean isAttacking()
    {
        return attackTimer != -1;
    }
    
    public void startMining()
    {
        setMiningTimer(0);
    }

    public void stopMining()
    {
        setMiningTimer(-1);
    }

    public boolean isMining()
    {
        return miningTimer != -1;
    }

    public boolean hasTool() { return hasTool(EnumHand.MAIN_HAND) || hasTool(EnumHand.OFF_HAND); }
    public boolean hasTool(EnumHand hand)
    {
        return ToolHelper.isTool(getHeldItem(hand).getItem());
    }

    public boolean hasWeapon()
    {
        return hasWeapon(EnumHand.MAIN_HAND) || hasWeapon(EnumHand.OFF_HAND);
    }
    public boolean hasWeapon(EnumHand hand)
    {
        return ToolHelper.isWeapon(getHeldItem(hand).getItem());
    }

    public boolean hasPickaxe()
    {
        return hasPickaxe(EnumHand.MAIN_HAND) || hasPickaxe(EnumHand.OFF_HAND);
    }
    public boolean hasPickaxe(EnumHand hand)
    {
        return ToolHelper.isPickaxe(getHeldItem(hand).getItem());
    }

    public boolean hasHoe()
    {
        return hasHoe(EnumHand.MAIN_HAND) || hasHoe(EnumHand.OFF_HAND);
    }
    public boolean hasHoe(EnumHand hand)
    {
        return ToolHelper.isHoe(getHeldItem(hand).getItem());
    }

    public boolean hasAxe()
    {
        return hasAxe(EnumHand.MAIN_HAND) || hasAxe(EnumHand.OFF_HAND);
    }
    public boolean hasAxe(EnumHand hand)
    {
        return ToolHelper.isAxe(getHeldItem(hand).getItem());
    }

    public boolean isSetToAttack() { return (task == EntityHelper.Task.HUNT || guard == EntityHelper.Guard.GUARD || getAttackTarget() != null); }

    public boolean isUsingWeaponRight() { return hasWeapon(EnumHand.MAIN_HAND) && (isSetToAttack()); }
    public boolean isUsingWeaponLeft() { return hasWeapon(EnumHand.OFF_HAND) && (isSetToAttack()); }

    public boolean isUsingPickaxeRight() { return hasPickaxe(EnumHand.MAIN_HAND) && (task == EntityHelper.Task.MINE); }
    public boolean isUsingPickaxeLeft() { return hasPickaxe(EnumHand.OFF_HAND) && (task == EntityHelper.Task.MINE); }

    public boolean isUsingAxeRight() { return hasAxe(EnumHand.MAIN_HAND) && (task == EntityHelper.Task.CHOP); }
    public boolean isUsingAxeLeft() { return hasAxe(EnumHand.OFF_HAND) && (task == EntityHelper.Task.CHOP); }

    public boolean isUsingHoeRight() { return hasHoe(EnumHand.MAIN_HAND) && (task == EntityHelper.Task.FARM); }
    public boolean isUsingHoeLeft() { return hasHoe(EnumHand.OFF_HAND) && (task == EntityHelper.Task.FARM); }

    private void tryRemoveAttackTarget()
    {
        if (task != EntityHelper.Task.HUNT && guard != EntityHelper.Guard.GUARD)
        {
            setAttackTarget(null);
        }
    }

    private void checkBonusStats()
    {
        AttributeModifier typeBonusHealth = new AttributeModifier(UUID.fromString("a6107045-134f-4c54-a645-75c3ae5c7a27"), "Type Bonus Health", blocklingType.bonusHealth, 0);
        AttributeModifier typeBonusAttackDamage = new AttributeModifier(UUID.fromString("a6107045-134f-4c54-a645-75c3ae5c7a28"), "Type Bonus Attack Damage", blocklingType.bonusAttackDamage, 0);
        AttributeModifier typeBonusMovementSpeed = new AttributeModifier(UUID.fromString("a6107045-134f-4c54-a645-75c3ae5c7a29"), "Type Bonus Movement Speed", blocklingType.bonusMovementSpeed / 40.0, 0);

        double weaponBonusAttackDamageValue = 0;
        double weaponBonusAttackSpeedValue = 0;
        boolean mainHandEmpty = getHeldItemMainhand().isEmpty();
        boolean offHandEmpty = getHeldItemOffhand().isEmpty();
        boolean bothTools = hasTool(EnumHand.MAIN_HAND) && hasTool(EnumHand.OFF_HAND) && !hasWeapon(EnumHand.MAIN_HAND) && !hasWeapon(EnumHand.OFF_HAND);
        boolean shouldCountMain = bothTools || hasWeapon(EnumHand.MAIN_HAND) || !hasWeapon(EnumHand.OFF_HAND);
        boolean shouldCountOff = bothTools || hasWeapon(EnumHand.OFF_HAND) || !hasWeapon(EnumHand.MAIN_HAND);
        if (!mainHandEmpty && shouldCountMain)
        {
            weaponBonusAttackDamageValue += ToolHelper.getToolAttackDamage(getHeldItemMainhand());
            weaponBonusAttackSpeedValue += ToolHelper.getToolAttackSpeed(getHeldItemMainhand());
        }
        if (!offHandEmpty && shouldCountOff)
        {
            weaponBonusAttackDamageValue += ToolHelper.getToolAttackDamage(getHeldItemOffhand());
            weaponBonusAttackSpeedValue += ToolHelper.getToolAttackSpeed(getHeldItemOffhand());
        }
        if (shouldCountMain && shouldCountOff)
        {
            weaponBonusAttackDamageValue /= 2;
        }

        AttributeModifier weaponBonusAttackDamage = new AttributeModifier(UUID.fromString("a6107045-134f-4c54-a645-75c3ae5c7a30"), "Weapon Bonus Attack Damage", weaponBonusAttackDamageValue, 0);

        AttributeModifier levelBonusHealth = new AttributeModifier(UUID.fromString("a6107045-134f-4c54-a645-75c3ae5c7a31"), "Level Bonus Health", calcBonusHealthFromLevel(), 0);
        AttributeModifier levelBonusAttackDamage = new AttributeModifier(UUID.fromString("a6107045-134f-4c54-a645-75c3ae5c7a32"), "Level Bonus Attack Damage", calcBonusDamageFromLevel(), 0);

        if (getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null)
        {
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(typeBonusHealth);
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(typeBonusHealth);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(typeBonusAttackDamage);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(typeBonusAttackDamage);
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(typeBonusMovementSpeed);
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(typeBonusMovementSpeed);

            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(weaponBonusAttackDamage);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(weaponBonusAttackDamage);

            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).removeModifier(levelBonusHealth);
            getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(levelBonusHealth);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(levelBonusAttackDamage);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(levelBonusAttackDamage);
        }

        if (this.combatAbilities.isAbilityAcquired(AbilityHelper.berserker))
        {
            float multiplier = 0.5f - ((getHealth() / getMaxHealth()) / 2.0f);
            AttributeModifier damageMultiplier = new AttributeModifier(UUID.fromString("a6107045-134f-4c54-a645-75c3ae5c7a33"), "Damage Multiplier", multiplier, 2);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(damageMultiplier);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(damageMultiplier);
        }

        if (weaponBonusAttackSpeedValue != 0)
        {
            setAttackInterval((int)(5.0 / (weaponBonusAttackSpeedValue)));
        }
        else
        {
            setAttackInterval(20);
        }


        if (hasPickaxe()) setMiningInterval(calcBonusMiningSpeedFromLevel(miningLevel) - (miningAbilities.isAbilityAcquired(AbilityHelper.hasteMining) ? 10 : 0));
        else setMiningInterval(0);
        if (hasAxe()) setChoppingInterval(calcBonusMiningSpeedFromLevel(woodcuttingLevel) - (woodcuttingAbilities.isAbilityAcquired(AbilityHelper.hasteWoodcutting) ? 10 : 0));
        else setChoppingInterval(0);
        if (hasHoe()) setFarmingInterval(calcBonusMiningSpeedFromLevel(farmingLevel) - (farmingAbilities.isAbilityAcquired(AbilityHelper.hasteFarming) ? 10 : 0));
        else setFarmingInterval(0);

        if (!mainHandEmpty && !offHandEmpty)
        {
            if (hasPickaxe(EnumHand.MAIN_HAND) && hasPickaxe(EnumHand.OFF_HAND))
            {
                setMiningInterval(miningInterval / 2);
            }
            else if (hasAxe(EnumHand.MAIN_HAND) && hasAxe(EnumHand.OFF_HAND))
            {
                setChoppingInterval(choppingInterval / 2);
            }
            else if (hasHoe(EnumHand.MAIN_HAND) && hasHoe(EnumHand.OFF_HAND))
            {
                setFarmingInterval(farmingInterval / 2);
            }
        }
    }

    private double calcBonusHealthFromLevel()
    {
        return 3.0 * Math.log(combatLevel);
    }

    private double calcBonusDamageFromLevel()
    {
        return 2.0 * Math.log(combatLevel);
    }

    private int calcBonusMiningSpeedFromLevel(int level)
    {
        return (int)(25.0 / Math.log((level / 30.0) + 1.5));
    }

    private void onXpGained()
    {
        if (combatXp >= EntityHelper.getXpUntilNextLevel(combatLevel))
        {
            setCombatXp(0);
            setCombatLevel(combatLevel + 1);
            if (combatLevel % 5 == 0)
            {
                incrementSkillPoints(1);
                sendSkillPointMessage();
            }
        }
        else if (miningXp >= EntityHelper.getXpUntilNextLevel(miningLevel))
        {
            setMiningXp(0);
            setMiningLevel(miningLevel + 1);
            if (miningLevel % 5 == 0)
            {
                incrementSkillPoints(1);
                sendSkillPointMessage();
            }
        }
        else if (woodcuttingXp >= EntityHelper.getXpUntilNextLevel(woodcuttingLevel))
        {
            setWoodcuttingXp(0);
            setWoodcuttingLevel(woodcuttingLevel + 1);
            if (woodcuttingLevel % 5 == 0)
            {
                incrementSkillPoints(1);
                sendSkillPointMessage();
            }
        }
        else if (farmingXp >= EntityHelper.getXpUntilNextLevel(farmingLevel))
        {
            setFarmingXp(0);
            setFarmingLevel(farmingLevel + 1);
            if (farmingLevel % 5 == 0)
            {
                incrementSkillPoints(1);
                sendSkillPointMessage();
            }
        }
    }

    private void sendSkillPointMessage()
    {
        if (getOwner() != null)
        {
            EntityPlayer player = ((EntityPlayer)getOwner());
            if (world.isRemote) player.sendMessage(new TextComponentString(ChatFormatting.GOLD + getCustomNameTag() + ChatFormatting.WHITE + " gained a skill point!"));
        }
    }


    // SPECIAL
    // GETTERS
    // AND
    // SETTERS

    public EntityHelper.Task getTask()
    {
        return task;
    }

    public void setTask(EntityHelper.Task value)
    {
        task = value;
        NetworkHelper.sync(world, new TaskIDMessage(value.id, getEntityId()));
        tryRemoveAttackTarget();
        updateAI();
    }

    public void setTaskFromPacket(int value)
    {
        task = EntityHelper.Task.getFromID(value);
        updateAI();
    }

    public void cycleTask()
    {
        int id = task.id + 1;
        if (id > EntityHelper.Task.values().length)
        {
            id = 1;
        }

        task = EntityHelper.Task.getFromID(id);
    }


    public EntityHelper.Guard getGuard()
    {
        return guard;
    }

    public void setGuard(EntityHelper.Guard value)
    {
        guard = value;
        NetworkHelper.sync(world, new GuardIDMessage(value.id, getEntityId()));
        tryRemoveAttackTarget();
        updateAI();
    }

    public void setGuardFromPacket(int value)
    {
        guard = EntityHelper.Guard.getFromID(value);
        updateAI();
    }

    public void cycleGuard()
    {
        int id = guard.id + 1;
        if (id > EntityHelper.Guard.values().length)
        {
            id = 1;
        }

        setGuard(EntityHelper.Guard.getFromID(id));
    }


    public EntityHelper.State getState()
    {
        return state;
    }

    public void setState(EntityHelper.State value)
    {
        state = value;
        NetworkHelper.sync(world, new StateIDMessage(value.id, getEntityId()));
        updateAI();
    }

    public void setStateFromPacket(int value)
    {
        state = EntityHelper.State.getFromID(value);
        updateAI();
    }

    public void cycleState()
    {
        int id = state.id + 1;
        if (id > EntityHelper.State.values().length)
        {
            id = 1;
        }

        setState(EntityHelper.State.getFromID(id));
    }



    // GETTERS
    // AND
    // SETTERS

    public int getUnlockedSlots()
    {
        return unlockedSlots;
    }

    public void setUnlockedSlots(int value)
    {
        unlockedSlots = value;
        NetworkHelper.sync(world, new UnlockedSlotsMessage(value, getEntityId()));
    }

    public void setUnlockedSlotsFromPacket(int value)
    {
        unlockedSlots = value;
    }

    public float getBlocklingScale()
    {
        return scale;
    }

    public void setBlocklingScale(float value)
    {
        scale = value;
        NetworkHelper.sync(world, new ScaleMessage(value, getEntityId()));
    }

    public void setScaleFromPacket(float value)
    {
        scale = value;
    }



    @SideOnly(Side.CLIENT)
    public void setName(String value)
    {
        if (value == null || value.equals(""))
        {
            setCustomNameTag("Blockling");
        }
        else
        {
            setCustomNameTag(value);
        }
        NetworkHelper.sync(world, new NameMessage(getCustomNameTag(), getEntityId()));
    }

    public void setNameFromPacket(String value)
    {
        setCustomNameTag(value);
    }



    public AnimationState getAnimationState()
    {
        return animationState;
    }

    public void setAnimationState(AnimationState value)
    {
        animationState = value;
        NetworkHelper.sync(world, new AnimationStateMessage(value, getEntityId()));
    }

    public void setAnimationStateFromPacket(AnimationState value)
    {
        animationState = value;
    }



    public int getGuiID()
    {
        return guiID;
    }

    public void setGuiID(int value)
    {
        guiID = value;
        NetworkHelper.sync(world, new GuiIDMessage(value, getEntityId()));
    }

    public void setGuiIDFromPacket(int value)
    {
        guiID = value;
    }



    public EnumHand getAttackingHand()
    {
        return attackingHand;
    }

    public void setAttackingHand(EnumHand value)
    {
        attackingHand = value;
        NetworkHelper.sync(world, new AttackingHandMessage(value, getEntityId()));
    }

    public void setAttackingHandFromPacket(EnumHand value)
    {
        attackingHand = value;
    }



    public int getAttackInterval()
    {
        return attackInterval;
    }

    public void setAttackInterval(int value)
    {
        attackInterval = value;
        NetworkHelper.sync(world, new AttackIntervalMessage(value, getEntityId()));
    }

    public void setAttackIntervalFromPacket(int value)
    {
        attackInterval = value;
    }

    
    
    public int getAttackTimer()
    {
        return attackTimer;
    }

    public void incrementAttackTimer()
    {
        setAttackTimer(attackTimer + 1);
    }

    public void setAttackTimer(int value)
    {
        attackTimer = value;
        NetworkHelper.sync(world, new AttackTimerMessage(value, getEntityId()));
    }

    public void setAttackTimerFromPacket(int value)
    {
        attackTimer = value;
    }
    


    public int getMiningInterval()
    {
        return miningInterval;
    }

    public void setMiningInterval(int value)
    {
        miningInterval = value;
        NetworkHelper.sync(world, new MiningIntervalMessage(value, getEntityId()));
    }

    public void setMiningIntervalFromPacket(int value)
    {
        miningInterval = value;
    }



    public int getChoppingInterval()
    {
        return choppingInterval;
    }

    public void setChoppingInterval(int value)
    {
        choppingInterval = value;
        NetworkHelper.sync(world, new ChoppingIntervalMessage(value, getEntityId()));
    }

    public void setChoppingIntervalFromPacket(int value)
    {
        choppingInterval = value;
    }



    public int getFarmingInterval()
    {
        return farmingInterval;
    }

    public void setFarmingInterval(int value)
    {
        farmingInterval = value;
        NetworkHelper.sync(world, new FarmingIntervalMessage(value, getEntityId()));
    }

    public void setFarmingIntervalFromPacket(int value)
    {
        farmingInterval = value;
    }



    public int getMiningTimer()
    {
        return miningTimer;
    }

    public void incrementMiningTimer()
    {
        setMiningTimer(miningTimer + 1);
    }

    public void setMiningTimer(int value)
    {
        miningTimer = value;
        NetworkHelper.sync(world, new MiningTimerMessage(value, getEntityId()));
    }

    public void setMiningTimerFromPacket(int value)
    {
        miningTimer = value;
    }



    public boolean hasTarget()
    {
        return aiMining.hasTarget() || aiWoodcutting.hasTarget() || aiFarming.hasTarget();
    }


    public int getSkillPoints()
    {
        return skillPoints;
    }

    public void incrementSkillPoints(int value)
    {
        setSkillPoints(skillPoints + value);
    }

    public void setSkillPoints(int value)
    {
        skillPoints = value;
        NetworkHelper.sync(world, new SkillPointsMessage(value, getEntityId()));
    }

    public void setSkillPointsFromPacket(int value)
    {
        skillPoints = value;
    }



    public int getLevel(String levelName)
    {
        switch (levelName)
        {
            case "Combat":
                return combatLevel;
            case "Mining":
                return miningLevel;
            case "Woodcutting":
                return woodcuttingLevel;
            case "Farming":
                return farmingLevel;
        }

        return 0;
    }



    public int getCombatLevel()
    {
        return combatLevel;
    }

    public void setCombatLevel(int value)
    {
        combatLevel = value;
        if (combatLevel > 99) combatLevel = 99;
        NetworkHelper.sync(world, new CombatLevelMessage(value, getEntityId()));
    }

    public void setCombatLevelFromPacket(int value)
    {
        combatLevel = value;
    }



    public int getMiningLevel()
    {
        return miningLevel;
    }

    public void setMiningLevel(int value)
    {
        miningLevel = value;
        if (miningLevel > 99) miningLevel = 99;
        NetworkHelper.sync(world, new MiningLevelMessage(value, getEntityId()));
    }

    public void setMiningLevelFromPacket(int value)
    {
        miningLevel = value;
    }



    public int getWoodcuttingLevel()
    {
        return woodcuttingLevel;
    }

    public void setWoodcuttingLevel(int value)
    {
        woodcuttingLevel = value;
        if (woodcuttingLevel > 99) woodcuttingLevel = 99;
        NetworkHelper.sync(world, new WoodcuttingLevelMessage(value, getEntityId()));
    }

    public void setWoodcuttingLevelFromPacket(int value)
    {
        woodcuttingLevel = value;
    }



    public int getFarmingLevel()
    {
        return farmingLevel;
    }

    public void setFarmingLevel(int value)
    {
        farmingLevel = value;
        if (farmingLevel > 99) farmingLevel = 99;
        NetworkHelper.sync(world, new FarmingLevelMessage(value, getEntityId()));
    }

    public void setFarmingLevelFromPacket(int value)
    {
        farmingLevel = value;
    }



    public int getCombatXp()
    {
        return combatXp;
    }

    public void incrementCombatXp(int value)
    {
        setCombatXp(combatXp + value);
    }

    public void setCombatXp(int value)
    {
        combatXp = value;
        NetworkHelper.sync(world, new CombatXpMessage(value, getEntityId()));
        onXpGained();
    }

    public void setCombatXpFromPacket(int value)
    {
        combatXp = value;
        onXpGained();
    }



    public int getMiningXp()
    {
        return miningXp;
    }

    public void incrementMiningXp(int value)
    {
        setMiningXp(miningXp + value);
    }

    public void setMiningXp(int value)
    {
        miningXp = value;
        NetworkHelper.sync(world, new MiningXpMessage(value, getEntityId()));
        onXpGained();
    }

    public void setMiningXpFromPacket(int value)
    {
        miningXp = value;
        onXpGained();
    }



    public int getWoodcuttingXp()
    {
        return woodcuttingXp;
    }

    public void incrementWoodcuttingXp(int value)
    {
        setWoodcuttingXp(woodcuttingXp + value);
    }

    public void setWoodcuttingXp(int value)
    {
        woodcuttingXp = value;
        NetworkHelper.sync(world, new WoodcuttingXpMessage(value, getEntityId()));
        onXpGained();
    }

    public void setWoodcuttingXpFromPacket(int value)
    {
        woodcuttingXp = value;
        onXpGained();
    }



    public int getFarmingXp()
    {
        return farmingXp;
    }

    public void incrementFarmingXp(int value)
    {
        setFarmingXp(farmingXp + value);
    }

    public void setFarmingXp(int value)
    {
        farmingXp = value;
        NetworkHelper.sync(world, new FarmingXpMessage(value, getEntityId()));
        onXpGained();
    }

    public void setFarmingXpFromPacket(int value)
    {
        farmingXp = value;
        onXpGained();
    }



    public boolean getAutoswitchLeft()
    {
        return (autoswitchID & 2) > 0;
    }

    public boolean getAutoswitchRight()
    {
        return (autoswitchID & 1) > 0;
    }

    public void setAutoswitchLeft(boolean on)
    {
        byte result = autoswitchID;
        if (on)
        {
            result = (byte) (autoswitchID | 2); // 10
        }
        else
        {
            result = (byte) (autoswitchID & 1); // 01
        }

        setAutoswitchID(result);
    }

    public void setAutoswitchRight(boolean on)
    {
        byte result = autoswitchID;
        if (on)
        {
            result = (byte) (autoswitchID | 1); // 01
        }
        else
        {
            result = (byte) (autoswitchID & 2); // 10
        }

        setAutoswitchID(result);
    }

    private void setAutoswitchID(byte value)
    {
        autoswitchID = value;
        NetworkHelper.sync(world, new AutoswitchIDMessage(value, getEntityId()));
    }

    public void setAutoswitchIDFromPacket(byte value)
    {
        autoswitchID = value;
    }
}



/*
 * When first spawned:
 *
 * [SERVER] entityInit
 * [SERVER] constructor
 * [SERVER] writeSpawnData
 * [SERVER] writeEntityToNBT
 * [SERVER] readEntityFromNBT
 * [CLIENT] entityInit
 * [CLIENT] constructor
 * [CLIENT] readSpawnData
 */

/*
 * When spawned from then on:
 *
 * [SERVER] entityInit
 * [SERVER] constructor
 * [SERVER] readEntityFromNBT
 * [SERVER] writeEntityToNBT
 * [SERVER] writeSpawnData
 * [CLIENT] entityInit
 * [CLIENT] constructor
 * [CLIENT] readSpawnData
 */