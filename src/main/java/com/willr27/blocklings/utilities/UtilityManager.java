package com.willr27.blocklings.utilities;

public class UtilityManager
{
//    private static final DataParameter<Integer> UTILITY_1 = EntityDataManager.defineId(BlocklingEntity.class, DataSerializers.INT);
//    private static final DataParameter<Integer> UTILITY_2 = EntityDataManager.defineId(BlocklingEntity.class, DataSerializers.INT);
//
//    private BlocklingEntity blockling;
//
//    private UtilityInventory inv1 = null;
//    private UtilityInventory inv2 = null;
//
//    public UtilityManager(BlocklingEntity blockling)
//    {
//        this.blockling = blockling;
//    }
//
//    public void registerUtilities()
//    {
//        blockling.getEntityData().define(UTILITY_1, -1);
//        blockling.getEntityData().define(UTILITY_2, -1);
//    }
//
//    public void update()
//    {
//        if (getInventory1() instanceof FurnaceInventory) ((FurnaceInventory) getInventory1()).tick();
//        if (getInventory2() instanceof FurnaceInventory) ((FurnaceInventory) getInventory2()).tick();
//    }
//
//    public boolean hasUtility(Utility utility)
//    {
//        return hasUtility(utility, 1) || hasUtility(utility, 2);
//    }
//    public boolean hasUtility(Utility utility, int index)
//    {
//        return getUtility(index) == utility;
//    }
//    public boolean hasUtility1(Utility utility)
//    {
//        return hasUtility(utility, 1);
//    }
//    public boolean hasUtility2(Utility utility)
//    {
//        return hasUtility(utility, 2);
//    }
//
//    public Utility getUtility(int index)
//    {
//        return index == 1 ? getUtility1() : getUtility2();
//    }
//    public Utility getUtility1()
//    {
//        int ordinal = blockling.getEntityData().get(UTILITY_1);
//        return ordinal != - 1 ? Utility.values()[ordinal] : null;
//    }
//    public Utility getUtility2()
//    {
//        int ordinal = blockling.getEntityData().get(UTILITY_2);
//        return ordinal != - 1 ? Utility.values()[ordinal] : null;
//    }
//
//    public void setUtility(Utility utility, int index)
//    {
//        if (index == 1) setUtility1(utility);
//        else setUtility2(utility);
//    }
//    public void setUtility1(Utility utility)
//    {
//        setInventory1(utility);
//        if (utility != null) NetworkHandler.sendToAll(blockling.world, new UtilityInventoryMessage(utility, 1, blockling.getEntityId()));
//        blockling.getEntityData().set(UTILITY_1, utility == null ? -1 : utility.ordinal());
//    }
//    public void setUtility2(Utility utility)
//    {
//        setInventory2(utility);
//        if (utility != null) NetworkHandler.sendToAll(blockling.world, new UtilityInventoryMessage(utility, 2, blockling.getEntityId()));
//        blockling.getEntityData().set(UTILITY_2, utility == null ? -1 : utility.ordinal());
//    }
//
//    public AbstractInventory getInventory(int index)
//    {
//        return index == 1 ? getInventory1() : getInventory2();
//    }
//    public AbstractInventory getInventory1()
//    {
//        return inv1;
//    }
//    public AbstractInventory getInventory2()
//    {
//        return inv2;
//    }
//    public void setInventory(Utility utility, int index)
//    {
//        if (index == 1) setInventory1(utility);
//        else setInventory2(utility);
//    }
//    public void setInventory1(Utility utility)
//    {
//        if (utility != getUtility1())
//        {
//            if (inv1 != null) inv1.close();
//            inv1 = utility == null ? null : utility.inventory.apply(blockling, 1);
//        }
//    }
//    public void setInventory2(Utility utility)
//    {
//        if (utility != getUtility2())
//        {
//            if (inv2 != null) inv2.close();
//            inv2 = utility == null ? null : utility.inventory.apply(blockling, 2);
//        }
//    }
}
