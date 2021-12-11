package com.blocklings.abilities;

import com.blocklings.entities.EntityBlockling;
import io.netty.buffer.ByteBuf;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AbilityHelper
{
    // GENERAL

    public static Ability mule1 = new Ability()
        .initInfo("46e7311b-0be5-427a-9491-a3e88849b279", "Mule I", new String[] { "Increases inventory", "slots by 12" }, null, 1)
        .initGui(0, 0, 0, 0, 0, 0, new Color(0xFFBF79));
    public static Ability mule2 = new Ability()
        .initInfo("3f2d8efd-170a-4724-9c24-7077e6ed5b85", "Mule II", new String[] { "Increases inventory", "slots by 12" }, mule1, 2)
        .initGui(0, 50, 24, 0, 0, 0, new Color(0xFFBF79));

    public static Ability botanist = new Ability()
        .initInfo("fc665125-5966-48d7-8f49-b03be78e2be1", "Botanist", new String[] { "Blockling can be healed", "using flowers for 1", "health each" }, null, 1)
        .initGui(50, -50, 24 * 3, 24 * 3, 0, 0, new Color(0xCC0E26));

    public static Ability flowerPower = new Ability()
        .initInfo("b4e3433e-5bdd-47fc-a6dd-cc4ba0947678", "Flower Power", new String[] { "Drop XP when healed" }, botanist, 2)
        .initGui(50, 0, 24 * 2, 0, 0, 0, new Color(0xAFFF48));

    public static Ability packling = new Ability()
        .initInfo("0016bab0-ed6a-427c-a915-b59638355efb", "Packling", new String[] { "Pick blockling up", "by using a flower" }, botanist, 2)
        .initGui(100, 0, 24 * 8, 0, 0, 0, new Color(0xBEA60C));
    public static Ability armadillo = new Ability()
        .initInfo("d286385b-a7f9-40de-b910-fd5dbc0c80ff", "Armadillo", new String[] { "Blockling drops as", "item instead of dying" }, packling, 3)
        .initGui(100, 100, 24 * 3, 0, 0, 0, new Color(0x945A51));

    public static Ability enderBoye1 = new Ability()
        .initInfo("776f5824-1785-4082-bedc-44649cbeff3e", "Ender Boye I", new String[] { "Chance to teleport", "to destination" }, null, 1)
        .initGui(150, 0, 24 * 7, 0, 0, 0, new Color(0x1A154A));
    public static Ability enderBoye2 = new Ability()
        .initInfo("42e99f40-5a79-4d48-b540-deaaf76130f3", "Ender Boye II", new String[] { "Higher chance to teleport", "to destination" }, enderBoye1, 2)
        .initGui(150, 50, 24 * 4, 24 * 3, 0, 0, new Color(0x0A0539));

    public static Ability outline = new Ability()
        .initInfo("9a262f73-78b0-4775-aa83-1414077e5c3e", "Outline", new String[] { "Outlines the blockling", "with glowing effect" }, null, 2)
        .initGui(-50, 0, 24 * 5, 24 * 1, 0, 0, new Color(0xFFE857));


    // COMBAT

    public static Ability regen1 = new Ability()
        .initInfo("1b89d93b-91ef-4b94-9e4a-241807773a7e", "Regen I", new String[] { "Heals 1 health every", "10 seconds" }, null, 1)
        .initGui(0, 0, 24 * 4, 0, 0, 0, new Color(0x7AE621));
    public static Ability regen2 = new Ability()
        .initInfo("a46c352e-9ac1-44fb-9959-48282f71b87d", "Regen II", new String[] { "Heals 2 health every", "10 seconds" }, regen1, 1)
        .initGui(0, 50, 24 * 5, 0, 0, 0, new Color(0x7AE621));
    public static Ability regen3 = new Ability()
        .initInfo("6509ac8b-c578-4b01-b8ca-01c9d62cb8b8", "Regen III", new String[] { "Heals 4 health every", "10 seconds" }, regen2, 2)
        .initGui(0, 100, 24 * 6, 0, 0, 0, new Color(0x7AE621));

    public static Ability thickSkin1 = new Ability()
        .initInfo("3a025a49-f885-4ce2-8541-f331e34796e9", "Thick Skin", new String[] { "5% chance to not take", "damage on hit" }, null, 1)
        .initGui(100, 0, 24 * 5, 24 * 3, 0, 0, new Color(0x31AB2C));
    public static Ability thickSkin2 = new Ability()
        .initInfo("251c7256-e740-41a9-9836-cf59a6f7b5c8", "Thicker Skin", new String[] { "10% chance to not take", "damage on hit" }, thickSkin1, 1)
        .initGui(100, 50, 24 * 6, 24 * 3, 0, 0, new Color(0x157988));
    public static Ability thickSkin3 = new Ability()
        .initInfo("a2b548c7-5693-440d-a4c4-633cdc00aaf5", "Thickest Skin", new String[] { "20% chance to not take", "damage on hit" }, thickSkin2, 2)
        .initGui(100, 100, 24 * 7, 24 * 3, 0, 0, new Color(0x533957));

    public static Ability shinobi1 = new Ability()
        .initInfo("21aafdda-9e15-4629-b350-a1bf990a0f66", "Shinobi I", new String[] { "Double damage from", "backstabs" }, null, 2)
        .initGui(-50, 50, 24 * 4, 24 * 1, 0, 0, new Color(0xBC1A2F));
    public static Ability shinobi2 = new Ability()
        .initInfo("bd8c2aa3-cc9e-46ab-b2e5-55bf2ce95731", "Shinobi II", new String[] { "Triple damage from", "backstabs" }, shinobi1, 3)
        .initGui(-50, 150, 24 * 0, 24 * 3, 0, 0, new Color(0x8B001A));

    public static Ability berserker = new Ability()
        .initInfo("49c8d290-7500-4b91-abe0-22a5da5ff295", "Berserker", new String[] { "Higher damage the lower", "the hp" }, null, 3)
        .initGui(50, 50, 24 * 8, 24 * 3, 0, 0, new Color(0xEA470A));


    // MINING

    public static Ability hasteMining = new Ability()
        .initInfo("101dce40-2565-4204-a3e1-89891dbb1bcc", "Haste", new String[] { "Decreases mining interval", "by 10" }, null, 1)
        .initGui(50, 0, 24 * 3, 24 * 1, 0, 0, new Color(0xE5D600));
    public static Ability brittleBlock = new Ability()
    .initInfo("557530d7-c00e-4ffb-94e0-5b64deef8937", "Brittle Block", new String[] { "10% chance to instantly", "mine a block" }, hasteMining, 1)
    .initGui(50, 50, 24 * 1, 24 * 2, 0, 0, new Color(0x828F7F));

    public static Ability blocksmith = new Ability()
        .initInfo("6e73dd2a-4782-4e6a-bec9-9511a451af6b", "Blocksmith", new String[] { "Automatically smelts", "ores mined" }, hasteMining, 2)
        .initGui(0, 50, 24 * 8, 24 * 1, 0, 0, new Color(0xFF8200));
    public static Ability metallurgy1 = new Ability()
        .initInfo("229422d6-4ce3-4748-a666-9309077fecf1", "Metallurgy I", new String[] { "25% chance for", "double smelt" }, blocksmith, 1)
        .initGui(0, 100, 24 * 9, 24 * 1, 0, 0, new Color(0xDCDACE));
    public static Ability metallurgy2 = new Ability()
        .initInfo("ab887f5d-08b2-4b97-ad9c-05f05ed32bcc", "Metallurgy II", new String[] { "25% chance for", "triple smelt" }, metallurgy1, 2)
        .initGui(0, 150, 24 * 0, 24 * 2, 0, 0, new Color(0xFFBE0E));

    public static Ability dwarvenSenses1 = new Ability()
        .initInfo("e3e414bb-58ab-44c6-b834-3aca63603439", "Dwarven Senses I", new String[] { "Bigger search radius" }, null, 1)
        .initGui(100, 50, 24 * 2, 24 * 2, 0, 0, new Color(0xB2FFEB));
    public static Ability dwarvenSenses2 = new Ability()
        .initInfo("380aef41-6f46-4587-bc1b-43070f13b8bc", "Dwarven Senses II", new String[] { "Blockling can path to", "blocks they can't even", "see" }, dwarvenSenses1, 2)
        .initGui(100, 100, 24 * 3, 24 * 2, 0, 0, new Color(0x41FFFA));


    // WOODCUTTING

    public static Ability hasteWoodcutting = new Ability()
        .initInfo("8454598a-2159-4be9-83cd-ce059b9adc2c", "Haste", new String[] { "Decreases chopping interval", "by 10" }, null, 1)
        .initGui(50, 0, 24 * 3, 24 * 1, 0, 0, new Color(0xE5D600));
    public static Ability sawmill = new Ability()
        .initInfo("2348217c-d7c6-4708-8d9b-43831abe3dae", "Sawmill", new String[] { "10% chance to cut an", "extra log from the tree" }, hasteWoodcutting, 1)
        .initGui(50, 50, 24 * 4, 24 * 2, 0, 0, new Color(0x853D25));

    public static Ability forestFire = new Ability()
        .initInfo("30c88bbe-2e74-493b-8197-58ffa63faa7e", "Forest Fire", new String[] { "Convert all logs", "chopped to charcoal" }, null, 2)
        .initGui(100, 50, 24 * 1, 24 * 1, 0, 0, new Color(0x1A0C05));

    public static Ability leafBlower = new Ability()
        .initInfo("adc3b36e-4fea-4159-8e78-d7b63b7012d3", "Leaf Blower", new String[] { "Break the leaves on", "adjacent to logs" }, hasteWoodcutting, 1)
        .initGui(0, 50, 24 * 5, 24 * 2, 0, 0, new Color(0x397129));
    public static Ability treeSurgeon = new Ability()
        .initInfo("4e3dcd06-1531-480d-b6c2-da840371d05d", "Tree Surgeon", new String[] { "Collect the drops from", "leaves" }, leafBlower, 2)
        .initGui(0, 100, 24 * 6, 24 * 2, 0, 0, new Color(0x30A502));

    public static Ability treeHugger = new Ability()
        .initInfo("1fb90b2c-ff54-4805-8730-e7c3b0129836", "Tree Hugger", new String[] { "Plant a sapling after", "chopping a tree" }, null, 1)
        .initGui(150, 50, 24 * 1, 24 * 3, 0, 0, new Color(0xA7662C));
    public static Ability fertilisationWoodcutting = new Ability()
        .initInfo("5753199f-71bb-4ded-a22d-0956963e3ecd", "Fertilisation", new String[] { "Fertilise any sapling", "planted using bonemeal", "in inventory" }, treeHugger, 2)
        .initGui(150, 100, 24 * 9, 24 * 2, 0, 0, new Color(0xEBEBEB));


    // FARMING

    public static Ability hasteFarming = new Ability()
        .initInfo("63a25da9-72ed-4a51-99fb-b92fc3256622", "Haste", new String[] { "Decreases farming interval", "by 10" }, null, 1)
        .initGui(0, 0, 24 * 3, 24 * 1, 0, 0, new Color(0xE5D600));
    public static Ability scythe = new Ability()
        .initInfo("218d498d-8559-4c34-94ad-262f1a4b9ff4", "Scythe", new String[] { "10% chance to harvest", "crops in a 3x3 area" }, hasteFarming, 2)
        .initGui(0, 100, 24 * 2, 24 * 1, 0, 0, new Color(0xC7A600));

    public static Ability plentifulHarvest = new Ability()
        .initInfo("7d47b774-23ac-47f2-8863-0f62bfc84728", "Plentiful Harvest", new String[] { "50% chance to drop", "double crops" }, null, 1)
        .initGui(100, 0, 24 * 0, 24 * 1, 0, 0, new Color(0xD5DA45));

    public static Ability replanter = new Ability()
        .initInfo("890a9964-2f16-4924-bfce-31f320178df9", "Replanter", new String[] { "Can replant seeds after", "harvest" }, null, 1)
        .initGui(50, -50, 24 * 2, 24 * 3, 0, 0, new Color(0x92C62F));
    public static Ability clinicalDibber = new Ability()
        .initInfo("a7775b66-28b0-444e-8d2f-c94f7f9594a9", "Clinical Dibber", new String[] { "50% chance not to use", "seed on plant" }, replanter, 1)
        .initGui(50, 0, 24 * 7, 24 * 2, 0, 0, new Color(0xD6BF97));
    public static Ability fertilisationFarming = new Ability()
        .initInfo("afef939a-26b9-4289-bc3a-ce26446e3161", "Fertilisation", new String[] { "Fertilise any crop", "planted using bonemeal", "in inventory" }, clinicalDibber, 2)
        .initGui(50, 50, 24 * 9, 24 * 2, 0, 0, new Color(0xEBEBEB));
    public static Ability natureAura = new Ability()
        .initInfo("d16be56d-10e4-43e7-86b8-0e7d33b01928", "Nature Aura", new String[] { "Chance to fertilise nearby", "crops" }, fertilisationFarming, 4)
        .initGui(50, 100, 24 * 8, 24 * 2, 0, 0, new Color(0x0B9F00));

    public static List<Ability> generalAbilities = new ArrayList<Ability>();
    public static List<Ability> combatAbilities = new ArrayList<Ability>();
    public static List<Ability> miningAbilities = new ArrayList<Ability>();
    public static List<Ability> woodcuttingAbilities = new ArrayList<Ability>();
    public static List<Ability> farmingAbilities = new ArrayList<Ability>();

    static
    {
        mule1.addLevelRequirement("Combat", 5);
        mule1.addLevelRequirement("Mining", 5);
        mule1.addLevelRequirement("Woodcutting", 5);
        mule1.addLevelRequirement("Farming", 5);
        mule2.addLevelRequirement("Combat", 15);
        mule2.addLevelRequirement("Mining", 15);
        mule2.addLevelRequirement("Woodcutting", 15);
        mule2.addLevelRequirement("Farming", 15);

        enderBoye1.addLevelRequirement("Combat", 5);
        enderBoye1.addLevelRequirement("Mining", 5);
        enderBoye1.addLevelRequirement("Woodcutting", 5);
        enderBoye1.addLevelRequirement("Farming", 5);
        enderBoye2.addLevelRequirement("Combat", 15);
        enderBoye2.addLevelRequirement("Mining", 15);
        enderBoye2.addLevelRequirement("Woodcutting", 15);
        enderBoye2.addLevelRequirement("Farming", 15);

        generalAbilities.add(botanist);
        generalAbilities.add(mule1);
        generalAbilities.add(mule2);
        generalAbilities.add(flowerPower);
        generalAbilities.add(packling);
        generalAbilities.add(armadillo);
        generalAbilities.add(enderBoye1);
        generalAbilities.add(enderBoye2);
        generalAbilities.add(outline);


        regen1.addLevelRequirement("Combat", 5);
        regen2.addLevelRequirement("Combat", 10);
        regen3.addLevelRequirement("Combat", 15);

        shinobi1.addLevelRequirement("Combat", 20);
        shinobi2.addLevelRequirement("Combat", 35);

        thickSkin1.addLevelRequirement("Combat", 5);
        thickSkin2.addLevelRequirement("Combat", 10);
        thickSkin3.addLevelRequirement("Combat", 20);

        berserker.addLevelRequirement("Combat", 25);

        combatAbilities.add(regen1);
        combatAbilities.add(regen2);
        combatAbilities.add(regen3);
        combatAbilities.add(shinobi1);
        combatAbilities.add(shinobi2);
        combatAbilities.add(thickSkin1);
        combatAbilities.add(thickSkin2);
        combatAbilities.add(thickSkin3);
        combatAbilities.add(berserker);


        blocksmith.addLevelRequirement("Mining", 10);
        metallurgy1.addLevelRequirement("Mining", 20);
        metallurgy2.addLevelRequirement("Mining", 30);

        dwarvenSenses1.addLevelRequirement("Mining", 10);
        dwarvenSenses2.addLevelRequirement("Mining", 20);

        miningAbilities.add(hasteMining);
        miningAbilities.add(brittleBlock);
        miningAbilities.add(blocksmith);
        miningAbilities.add(metallurgy1);
        miningAbilities.add(metallurgy2);
        miningAbilities.add(dwarvenSenses1);
        miningAbilities.add(dwarvenSenses2);


        sawmill.addLevelRequirement("Woodcutting", 10);

        forestFire.addLevelRequirement("Woodcutting", 25);

        leafBlower.addLevelRequirement("Woodcutting", 10);
        treeSurgeon.addLevelRequirement("Woodcutting", 20);

        treeHugger.addLevelRequirement("Woodcutting", 20);
        fertilisationWoodcutting.addLevelRequirement("Woodcutting", 25);

        woodcuttingAbilities.add(hasteWoodcutting);
        woodcuttingAbilities.add(sawmill);
        woodcuttingAbilities.add(forestFire);
        woodcuttingAbilities.add(leafBlower);
        woodcuttingAbilities.add(treeSurgeon);
        woodcuttingAbilities.add(treeHugger);
        woodcuttingAbilities.add(fertilisationWoodcutting);


        plentifulHarvest.addLevelRequirement("Farming", 10);

        scythe.addLevelRequirement("Farming", 20);

        replanter.addLevelRequirement("Farming", 10);
        clinicalDibber.addLevelRequirement("Farming", 15);

        fertilisationFarming.addLevelRequirement("Farming", 20);
        natureAura.addLevelRequirement("Farming", 40);

        farmingAbilities.add(hasteFarming);
        farmingAbilities.add(plentifulHarvest);
        farmingAbilities.add(scythe);
        farmingAbilities.add(replanter);
        farmingAbilities.add(clinicalDibber);
        farmingAbilities.add(fertilisationFarming);
        farmingAbilities.add(natureAura);
    }

    public static void readSpawnData(ByteBuf buf, EntityBlockling blockling)
    {
        blockling.generalAbilities.readFromBuf(buf);
        blockling.combatAbilities.readFromBuf(buf);
        blockling.miningAbilities.readFromBuf(buf);
        blockling.woodcuttingAbilities.readFromBuf(buf);
        blockling.farmingAbilities.readFromBuf(buf);
    }

    public static void writeSpawnData(ByteBuf buf, EntityBlockling blockling)
    {
        blockling.generalAbilities.writeToBuf(buf);
        blockling.combatAbilities.writeToBuf(buf);
        blockling.miningAbilities.writeToBuf(buf);
        blockling.woodcuttingAbilities.writeToBuf(buf);
        blockling.farmingAbilities.writeToBuf(buf);
    }
}
