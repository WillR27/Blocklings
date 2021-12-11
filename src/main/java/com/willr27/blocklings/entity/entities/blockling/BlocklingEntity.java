package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.entity.entities.blockling.action.BlocklingActions;
import com.willr27.blocklings.entity.entities.blockling.attribute.BlocklingAttributes;
import com.willr27.blocklings.entity.entities.blockling.goal.BlocklingTasks;
import com.willr27.blocklings.gui.GuiHandler;
import com.willr27.blocklings.inventory.inventories.EquipmentInventory;
import com.willr27.blocklings.item.ItemUtil;
import com.willr27.blocklings.item.items.BlocklingItem;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.BlocklingTypeMessage;
import com.willr27.blocklings.skills.BlocklingSkills;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlocklingEntity extends TameableEntity implements IEntityAdditionalSpawnData
{
    private BlocklingType blocklingType = BlocklingType.GRASS;
    private final BlocklingAttributes stats = new BlocklingAttributes(this);
    private final BlocklingTasks tasks = new BlocklingTasks(this);
    private final BlocklingSkills skills = new BlocklingSkills(this);
    private final BlocklingActions actions = new BlocklingActions(this);
    private BlocklingGuiInfo guiInfo = new BlocklingGuiInfo(this);

    private final EquipmentInventory equipmentInv = new EquipmentInventory(this);

    public BlocklingEntity(EntityType<? extends BlocklingEntity> type, World world)
    {
        super(type, world);

        setCustomName(new StringTextComponent("Blockling"));

        // Set up any values that are determined randomly here
        // So that we can sync them up using read/writeSpawnData
        if (!level.isClientSide)
        {
            setBlocklingType(BlocklingType.TYPES.get(getRandom().nextInt(3)), false);

            stats.init();
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes()
    {
        return MobEntity.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 2.0D);
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

        c.putInt("type", BlocklingType.TYPES.indexOf(blocklingType));

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
        setBlocklingType(BlocklingType.TYPES.get(c.getInt("type")), false);

        equipmentInv.readFromNBT(c);
        stats.readFromNBT(c);
        tasks.readFromNBT(c);
        skills.readFromNBT(c);
    }

    @Override
    public void writeSpawnData(PacketBuffer buf)
    {
        buf.writeInt(BlocklingType.TYPES.indexOf(blocklingType));

        equipmentInv.encode(buf);
        stats.encode(buf);
        tasks.encode(buf);
        skills.encode(buf);
    }

    @Override
    public void readSpawnData(PacketBuffer buf)
    {
        setBlocklingType(BlocklingType.TYPES.get(buf.readInt()), false);

        equipmentInv.decode(buf);
        stats.decode(buf);
        tasks.decode(buf);
        skills.decode(buf);
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

        actions.tick();

        refreshDimensions();

        equipmentInv.detectAndSendChanges();
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

        if (!ItemUtil.isFlower(item))
        {
            OpenGui(player);
        }

        return ActionResultType.PASS;
    }

    private ActionResultType mobInteractMainHandServer(ServerPlayerEntity player)
    {
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        Item item = stack.getItem();

        if (ItemUtil.isFlower(item))
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
                        level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.2f, getZ(), blocklingStack));
                    }

                    remove();
                }
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
            tasks.setIsUnlocked(BlocklingTasks.FOLLOW, true);
            tasks.setIsUnlocked(BlocklingTasks.SIT, true);
            tasks.setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT, true);
            tasks.setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT_BY, true);
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
    public boolean checkSpawnRules(@Nonnull IWorld world, @Nonnull SpawnReason reason)
    {
//        return super.checkSpawnRules(world, reason);

        return reason != SpawnReason.NATURAL;
    }

    @Override
    public @Nonnull EntitySize getDimensions(@Nonnull Pose pose)
    {
//        if (id == 1)
//        {
//            return new EntitySize(2.0f, 1.0f, true);
//        }

        return this.getType().getDimensions();
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(@Nonnull ServerWorld world, @Nonnull AgeableEntity entity)
    {
        return null;
    }

    private void OpenGui(PlayerEntity player)
    {
        GuiHandler.openGui(guiInfo.getCurrentGuiId(), this, player);
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

        if (sync)
        {
            NetworkHandler.sync(level, new BlocklingTypeMessage(blocklingType, getId()));
        }
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
            NetworkHandler.sync(level, new BlocklingGuiInfo.BlocklingGuiInfoMessage(guiInfo, getId()));
        }
    }

    public EquipmentInventory getEquipment()
    {
        return equipmentInv;
    }
}
