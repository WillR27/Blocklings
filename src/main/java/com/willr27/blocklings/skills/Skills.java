package com.willr27.blocklings.skills;

public class Skills
{
//    public static class Mining
//    {
//        public static final List<Ability> ABILITIES = new ArrayList<>();
//
//        public static final Ability NOVICE_MINER = create();
//        public static final Ability MINING_WHITELIST = create();
//        public static final Ability FASTER_MINING = create();
//        public static final Ability FASTER_MINING_FOR_HEALTH = create();
//        public static final Ability FASTER_MINING_FOR_ORES = create();
//        public static final Ability FASTER_MINING_FOR_DURABILITY = create();
//        public static final Ability FASTER_MINING_IN_DARK = create();
//        public static final Ability AUTOSMELT = create();
//        public static final Ability AUTOSMELT_WHITELIST = create();
//        public static final Ability FUEL_EFFICIENT = create();
//        public static final Ability AUTOSMELT_XP = create();
//        public static final Ability TORCH_PLACER = create();
//        public static final Ability TORCH_PLACER_IN_LIGHTER_AREAS = create();
//
//        static
//        {
//            NOVICE_MINER.setGeneralInfo("dcbf7cc1-8bef-49aa-a5a0-cd70cb40cbac", AbilityType.AI, "Novice Miner", "Unlocks the \"Mine Nearby Ores\" task.");
//            MINING_WHITELIST.setGeneralInfo("8963cddd-06dd-4b5a-8c1e-b1e38a99b25f", AbilityType.OTHER, "Mining Whitelist", "Unlocks the ore whitelist for mining.");
//            FASTER_MINING.setGeneralInfo("19253148-ff6e-4395-9464-289e081b442b", AbilityType.STAT, "Efficiency", "Bonus 10% mining speed.");
//            FASTER_MINING_FOR_HEALTH.setGeneralInfo("0f0039b2-99e3-4063-b532-f8434ba090f6", AbilityType.STAT, "Adrenaline", "The lower your blockling's health, the faster they will mine.");
//            FASTER_MINING_FOR_ORES.setGeneralInfo("bf736400-051a-41bd-8c08-210ce629d2dc", AbilityType.STAT, "Momentum", "Your blockling mines faster for each consecutive ore mined.");
//            FASTER_MINING_FOR_DURABILITY.setGeneralInfo("7e32822a-1d82-49bc-88e6-2d0544096d9a", AbilityType.STAT, "Hasty", "Your blockling mines 25% faster but their pickaxes use 2x the durability.");
//            FASTER_MINING_IN_DARK.setGeneralInfo("73140713-23ef-4bf0-bf25-d86f74d2974a", AbilityType.STAT, "Night Owl", "Your blockling mines faster the lower the light level.");
//            AUTOSMELT.setGeneralInfo("404a976a-6811-43a1-a0a1-6d2be0eec4c4", AbilityType.AI, "Autosmelt", "Your blockling will smelt ores in their inventory. Requires furnace utility.");
//            AUTOSMELT_WHITELIST.setGeneralInfo("62235fc9-43d6-4d2a-8787-95ee44a2e28f", AbilityType.OTHER, "Smelting Whitelist", "Unlocks the ore whitelist for smelting.");
//            FUEL_EFFICIENT.setGeneralInfo("6415ba87-dccd-4ed9-98fe-42557636a0cd", AbilityType.OTHER, "Fuel Efficient", "50% Chance to not use fuel when smelting.");
//            AUTOSMELT_XP.setGeneralInfo("3e29a328-e088-4fe9-bee5-d53bae2d4691", AbilityType.OTHER, "Bonus XP", "Smelting can sometimes yield bonus xp.");
//            TORCH_PLACER.setGeneralInfo("8a870aa8-2388-4fe9-868c-c41cf97c83a5", AbilityType.AI, "Let There Be Light", "Your blockling can place down torches in dark areas.");
//            TORCH_PLACER_IN_LIGHTER_AREAS.setGeneralInfo("0aab9dec-728a-4380-ac21-8611e92e209c", AbilityType.OTHER, "Let There Be More Light", "Your blockling will place down torches whilst moving and more effectively.");
//
//            NOVICE_MINER.setGuiInfo(0, 0, 0, 0, 0xdddddd);
//            MINING_WHITELIST.setGuiInfo(0, 70, 2, 1, 0xe0f1ff);
//            FASTER_MINING.setGuiInfo(70, 0, 1, 0, 0xffd56d);
//            FASTER_MINING_FOR_HEALTH.setGuiInfo(140, -50, 2, 0, 0xb72626);
//            FASTER_MINING_FOR_ORES.setGuiInfo(140, 50, 3, 0, 0xad79b5);
//            FASTER_MINING_FOR_DURABILITY.setGuiInfo(210, -50, 4, 0, 0x4eb2aa);
//            FASTER_MINING_IN_DARK.setGuiInfo(210, 50, 5, 0, 0x2b2a3d);
//            AUTOSMELT.setGuiInfo(-70, -100, 6, 0, 0xffde00);
//            AUTOSMELT_WHITELIST.setGuiInfo(-70, -190, 7, 0, 0xff9c00);
//            FUEL_EFFICIENT.setGuiInfo(-110, -240, 8, 0, 0xffa800);
//            AUTOSMELT_XP.setGuiInfo(-30, -240, 9, 0, 0xdcff00);
//            TORCH_PLACER.setGuiInfo(70, -100, 0, 1, 0xffe700);
//            TORCH_PLACER_IN_LIGHTER_AREAS.setGuiInfo(70, -240, 1, 1, 0xffa800);
//
//            FASTER_MINING.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            MINING_WHITELIST.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_MINING_FOR_HEALTH.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_MINING_FOR_ORES.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_MINING_FOR_DURABILITY.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_MINING_IN_DARK.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            AUTOSMELT.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            AUTOSMELT_WHITELIST.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_SHORTEST_SPLIT);
//            FUEL_EFFICIENT.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_LONGEST_SPLIT);
//            AUTOSMELT_XP.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_LONGEST_SPLIT);
//            TORCH_PLACER.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            TORCH_PLACER_IN_LIGHTER_AREAS.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_SHORTEST_SPLIT);
//
//            NOVICE_MINER.setSkillPointsRequired(1);
//            MINING_WHITELIST.setSkillPointsRequired(2);
//            FASTER_MINING.setSkillPointsRequired(1);
//            FASTER_MINING_FOR_HEALTH.setSkillPointsRequired(3);
//            FASTER_MINING_FOR_ORES.setSkillPointsRequired(3);
//            FASTER_MINING_FOR_DURABILITY.setSkillPointsRequired(3);
//            FASTER_MINING_IN_DARK.setSkillPointsRequired(3);
//            AUTOSMELT.setSkillPointsRequired(2);
//            AUTOSMELT_WHITELIST.setSkillPointsRequired(2);
//            FUEL_EFFICIENT.setSkillPointsRequired(1);
//            AUTOSMELT_XP.setSkillPointsRequired(1);
//            TORCH_PLACER.setSkillPointsRequired(2);
//            TORCH_PLACER_IN_LIGHTER_AREAS.setSkillPointsRequired(3);
//
//            MINING_WHITELIST.setLevelRequirements(new Pair<>("miningLevel", 10.0f));
//            FASTER_MINING.setLevelRequirements(new Pair<>("miningLevel", 5.0f));
//            FASTER_MINING_FOR_HEALTH.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
//            FASTER_MINING_FOR_ORES.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
//            FASTER_MINING_FOR_DURABILITY.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
//            FASTER_MINING_IN_DARK.setLevelRequirements(new Pair<>("miningLevel", 25.0f));
//            AUTOSMELT.setLevelRequirements(new Pair<>("miningLevel", 15.0f));
//            AUTOSMELT_WHITELIST.setLevelRequirements(new Pair<>("miningLevel", 30.0f));
//            FUEL_EFFICIENT.setLevelRequirements(new Pair<>("miningLevel", 45.0f));
//            AUTOSMELT_XP.setLevelRequirements(new Pair<>("miningLevel", 45.0f));
//            TORCH_PLACER.setLevelRequirements(new Pair<>("miningLevel", 20.0f));
//            TORCH_PLACER_IN_LIGHTER_AREAS.setLevelRequirements(new Pair<>("miningLevel", 40.0f));
//
//            MINING_WHITELIST.setParents(NOVICE_MINER);
//            FASTER_MINING.setParents(NOVICE_MINER);
//            FASTER_MINING_FOR_HEALTH.setParents(FASTER_MINING);
//            FASTER_MINING_FOR_ORES.setParents(FASTER_MINING);
//            FASTER_MINING_FOR_DURABILITY.setParents(FASTER_MINING);
//            FASTER_MINING_IN_DARK.setParents(FASTER_MINING);
//            AUTOSMELT.setParents(NOVICE_MINER);
//            AUTOSMELT_WHITELIST.setParents(AUTOSMELT);
//            FUEL_EFFICIENT.setParents(AUTOSMELT);
//            AUTOSMELT_XP.setParents(AUTOSMELT);
//            TORCH_PLACER.setParents(NOVICE_MINER);
//            TORCH_PLACER_IN_LIGHTER_AREAS.setParents(TORCH_PLACER);
//
//            createConflictGroup(FASTER_MINING_FOR_HEALTH, FASTER_MINING_FOR_ORES, FASTER_MINING_FOR_DURABILITY, FASTER_MINING_IN_DARK);
//            createConflictGroup(FUEL_EFFICIENT, AUTOSMELT_XP);
//        }
//
//        private static Ability create()
//        {
//            Ability ability = new Ability();
//            ABILITIES.add(ability);
//            return ability;
//        }
//    }
//
//    public static class Woodcutting
//    {
//        public static final List<Ability> ABILITIES = new ArrayList<>();
//
//        public static final Ability NOVICE_LUMBERJACK = create();
//        public static final Ability WOODCUTTING_WHITELIST = create();
//        public static final Ability FASTER_CHOPPING = create();
//        public static final Ability FASTER_CHOPPING_FOR_HEALTH = create();
//        public static final Ability FASTER_CHOPPING_FOR_LOGS = create();
//        public static final Ability FASTER_CHOPPING_FOR_DURABILITY = create();
//        public static final Ability FASTER_CHOPPING_IN_DARK = create();
//        public static final Ability BONEMEAL_NEARBY = create();
//        public static final Ability BONEMEAL_EFFICIENT = create();
//        public static final Ability CHARCOAL = create();
//        public static final Ability CHARCOAL_2 = create();
//        public static final Ability REPLANTER = create();
//        public static final Ability REPLANT_WHITELIST = create();
//        public static final Ability LEAF_BREAKER = create();
//        public static final Ability LEAF_DROP_GATHERER = create();
//        public static final Ability MORE_LEAVES_BROKEN = create();
//
//        static
//        {
//            NOVICE_LUMBERJACK.setGeneralInfo("9253ef82-9033-495a-8b68-7ae949d74804", AbilityType.AI, "Novice Lumberjack", "Unlocks the \"Chop Nearby Trees\" task.");
//            WOODCUTTING_WHITELIST.setGeneralInfo("714fd9ed-2641-4ead-8a15-aabed807db35", AbilityType.AI, "Woodcutting Whitelist", "Unlocks the log whitelist for woodcutting.");
//            FASTER_CHOPPING.setGeneralInfo("98a7851a-9ff8-408f-b1b8-14989fc28439", AbilityType.STAT, "Efficiency", "Bonus 10% chopping speed.");
//            FASTER_CHOPPING_FOR_HEALTH.setGeneralInfo("846e4117-37d4-4e0e-a3e7-afb691c3a421", AbilityType.STAT, "Adrenaline", "The lower your blockling's health, the faster they will chop.");
//            FASTER_CHOPPING_FOR_LOGS.setGeneralInfo("1c0d9bac-b4ec-4199-8309-afd75611bf40", AbilityType.STAT, "Momentum", "Your blockling chops faster for each consecutive log mined.");
//            FASTER_CHOPPING_FOR_DURABILITY.setGeneralInfo("cb0097f0-91fb-4f6d-8b43-1a87aa74cee6", AbilityType.STAT, "Hasty", "Your blockling chops 25% faster but their axes use 2x the durability.");
//            FASTER_CHOPPING_IN_DARK.setGeneralInfo("c83e7fd4-bf4d-411b-ad52-79e593f993de", AbilityType.STAT, "Night Owl", "Your blockling chops faster the lower the light level.");
//            BONEMEAL_NEARBY.setGeneralInfo("fc182378-6799-4f63-8b0b-919c1a5019f9", AbilityType.AI, "Fertiliser", "Unlocks the \"Fertilise Saplings\" task.");
//            BONEMEAL_EFFICIENT.setGeneralInfo("6a7f7f2e-0f9d-4ff8-831f-348bbd397ddf", AbilityType.OTHER, "Resourceful Fertiliser", "50% chance to not use bonemeal when fertilising.");
//            CHARCOAL.setGeneralInfo("3e40ae99-5b6d-4bab-9115-faf57a84207b", AbilityType.OTHER, "Ashes To Ashes", "When using an axe with fire aspect, logs will be turned into charcoal.");
//            CHARCOAL_2.setGeneralInfo("88870e8e-afa6-4063-9d09-7c0bea1aaf84", AbilityType.OTHER, "Hot Hands", "Logs will be converted to charcoal without the need for fire aspect.");
//            REPLANTER.setGeneralInfo("46d227bc-260d-4bed-890f-148add65ffa8", AbilityType.OTHER, "Replanter", "Your blockling will try to replant any tree they cut down.");
//            REPLANT_WHITELIST.setGeneralInfo("8f6fe4a9-0df6-4ae5-bd8a-1fe89cbe75de", AbilityType.OTHER, "Sapling Whitelist", "Unlocks the sapling whitelist for woodcutting.");
//            LEAF_BREAKER.setGeneralInfo("478f4a74-fcce-4b25-94bd-025965c94a6b", AbilityType.OTHER, "Leaf Blower", "Your blockling will break any leaves in a 3x3 area around each log.");
//            LEAF_DROP_GATHERER.setGeneralInfo("d24cfc8e-6381-4387-85a4-d79a8cd9c0c3", AbilityType.OTHER, "Tree Surgeon", "Your blockling will collect any drops from broken leaves.");
//            MORE_LEAVES_BROKEN.setGeneralInfo("43641065-c7a7-41e3-9744-6f81012a714f", AbilityType.OTHER, "Leaf Obliterator", "Your blockling will break any leaves in a 5x5 area around each log.");
//
//            NOVICE_LUMBERJACK.setGuiInfo(0, 0, 0, 0, 0xdddddd);
//            WOODCUTTING_WHITELIST.setGuiInfo(0, 70, 6, 0, 0xeeeeee);
//            FASTER_CHOPPING.setGuiInfo(70, 0, 1, 0, 0xffd56d);
//            FASTER_CHOPPING_FOR_HEALTH.setGuiInfo(140, -50, 2, 0, 0xad79b5);
//            FASTER_CHOPPING_FOR_LOGS.setGuiInfo(140, 50, 3, 0, 0xad79b5);
//            FASTER_CHOPPING_FOR_DURABILITY.setGuiInfo(210, -50, 4, 0, 0x4eb2aa);
//            FASTER_CHOPPING_IN_DARK.setGuiInfo(210, 50, 5, 0, 0x2b2a3d);
//            BONEMEAL_NEARBY.setGuiInfo(-70, -100, 7, 0, 0xf5fff2);
//            BONEMEAL_EFFICIENT.setGuiInfo(-70, -190, 8, 0, 0xdbffd1);
//            CHARCOAL.setGuiInfo(0, -130, 9, 0, 0x4c3602);
//            CHARCOAL_2.setGuiInfo(0, -220, 0, 1, 0x281c01);
//            REPLANTER.setGuiInfo(70, -100, 1, 1, 0x74a500);
//            REPLANT_WHITELIST.setGuiInfo(70, -190, 2, 1, 0xfbffd6);
//            LEAF_BREAKER.setGuiInfo(-70, 0, 3, 1, 0xb5ffed);
//            LEAF_DROP_GATHERER.setGuiInfo(-140, -50, 4, 1, 0xff7a7a);
//            MORE_LEAVES_BROKEN.setGuiInfo(-140, 50, 5, 1, 0x6bff8d);
//
//            WOODCUTTING_WHITELIST.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_CHOPPING.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_CHOPPING_FOR_HEALTH.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_CHOPPING_FOR_LOGS.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_CHOPPING_FOR_DURABILITY.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_CHOPPING_IN_DARK.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            BONEMEAL_NEARBY.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            BONEMEAL_EFFICIENT.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            CHARCOAL.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            CHARCOAL_2.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            REPLANTER.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            REPLANT_WHITELIST.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            LEAF_BREAKER.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            LEAF_DROP_GATHERER.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_LONGEST_SPLIT);
//            MORE_LEAVES_BROKEN.setConnectionType(AbilityWidget.ConnectionType.DOUBLE_LONGEST_SPLIT);
//
//            NOVICE_LUMBERJACK.setSkillPointsRequired(1);
//            WOODCUTTING_WHITELIST.setSkillPointsRequired(2);
//            FASTER_CHOPPING.setSkillPointsRequired(1);
//            FASTER_CHOPPING_FOR_HEALTH.setSkillPointsRequired(3);
//            FASTER_CHOPPING_FOR_LOGS.setSkillPointsRequired(3);
//            FASTER_CHOPPING_FOR_DURABILITY.setSkillPointsRequired(3);
//            FASTER_CHOPPING_IN_DARK.setSkillPointsRequired(3);
//            BONEMEAL_NEARBY.setSkillPointsRequired(2);
//            BONEMEAL_EFFICIENT.setSkillPointsRequired(1);
//            CHARCOAL.setSkillPointsRequired(2);
//            CHARCOAL_2.setSkillPointsRequired(2);
//            REPLANTER.setSkillPointsRequired(2);
//            REPLANT_WHITELIST.setSkillPointsRequired(1);
//            LEAF_BREAKER.setSkillPointsRequired(1);
//            LEAF_DROP_GATHERER.setSkillPointsRequired(3);
//            MORE_LEAVES_BROKEN.setSkillPointsRequired(2);
//
//            WOODCUTTING_WHITELIST.setLevelRequirements(new Pair<>("woodcuttingLevel", 10.0f));
//            FASTER_CHOPPING.setLevelRequirements(new Pair<>("woodcuttingLevel", 5.0f));
//            FASTER_CHOPPING_FOR_HEALTH.setLevelRequirements(new Pair<>("woodcuttingLevel", 25.0f));
//            FASTER_CHOPPING_FOR_LOGS.setLevelRequirements(new Pair<>("woodcuttingLevel", 25.0f));
//            FASTER_CHOPPING_FOR_DURABILITY.setLevelRequirements(new Pair<>("woodcuttingLevel", 25.0f));
//            FASTER_CHOPPING_IN_DARK.setLevelRequirements(new Pair<>("woodcuttingLevel", 25.0f));
//            BONEMEAL_NEARBY.setLevelRequirements(new Pair<>("woodcuttingLevel", 20.0f));
//            BONEMEAL_EFFICIENT.setLevelRequirements(new Pair<>("woodcuttingLevel", 30.0f));
//            CHARCOAL.setLevelRequirements(new Pair<>("woodcuttingLevel", 30.0f));
//            CHARCOAL_2.setLevelRequirements(new Pair<>("woodcuttingLevel", 50.0f));
//            REPLANTER.setLevelRequirements(new Pair<>("woodcuttingLevel", 20.0f));
//            REPLANT_WHITELIST.setLevelRequirements(new Pair<>("woodcuttingLevel", 30.0f));
//            LEAF_BREAKER.setLevelRequirements(new Pair<>("woodcuttingLevel", 15.0f));
//            LEAF_DROP_GATHERER.setLevelRequirements(new Pair<>("woodcuttingLevel", 30.0f));
//            MORE_LEAVES_BROKEN.setLevelRequirements(new Pair<>("woodcuttingLevel", 30.0f));
//
//            WOODCUTTING_WHITELIST.setParents(NOVICE_LUMBERJACK);
//            FASTER_CHOPPING.setParents(NOVICE_LUMBERJACK);
//            FASTER_CHOPPING_FOR_HEALTH.setParents(FASTER_CHOPPING);
//            FASTER_CHOPPING_FOR_LOGS.setParents(FASTER_CHOPPING);
//            FASTER_CHOPPING_FOR_DURABILITY.setParents(FASTER_CHOPPING);
//            FASTER_CHOPPING_IN_DARK.setParents(FASTER_CHOPPING);
//            BONEMEAL_NEARBY.setParents(NOVICE_LUMBERJACK);
//            BONEMEAL_EFFICIENT.setParents(BONEMEAL_NEARBY);
//            CHARCOAL.setParents(NOVICE_LUMBERJACK);
//            CHARCOAL_2.setParents(CHARCOAL);
//            REPLANTER.setParents(NOVICE_LUMBERJACK);
//            REPLANT_WHITELIST.setParents(REPLANTER);
//            LEAF_BREAKER.setParents(NOVICE_LUMBERJACK);
//            LEAF_DROP_GATHERER.setParents(LEAF_BREAKER);
//            MORE_LEAVES_BROKEN.setParents(LEAF_BREAKER);
//
//            createConflictGroup(FASTER_CHOPPING_FOR_HEALTH, FASTER_CHOPPING_FOR_LOGS, FASTER_CHOPPING_FOR_DURABILITY, FASTER_CHOPPING_IN_DARK);
//        }
//
//        private static Ability create()
//        {
//            Ability ability = new Ability();
//            ABILITIES.add(ability);
//            return ability;
//        }
//    }
//
//    public static class Farming
//    {
//        public static final List<Ability> ABILITIES = new ArrayList<>();
//
//        public static final Ability NOVICE_FARMER = create();
//        public static final Ability FARMING_WHITELIST = create();
//        public static final Ability FASTER_FARMING = create();
//        public static final Ability FASTER_FARMING_FOR_HEALTH = create();
//        public static final Ability FASTER_FARMING_FOR_CROPS = create();
//        public static final Ability FASTER_FARMING_FOR_DURABILITY = create();
//        public static final Ability FASTER_FARMING_IN_DARK = create();
//        public static final Ability BONEMEAL_NEARBY = create();
//        public static final Ability BONEMEAL_EFFICIENT = create();
//
//        static
//        {
//            NOVICE_FARMER.setGeneralInfo("395b159e-5162-4285-bf49-546f12a16c25", AbilityType.AI, "Novice Farmer", "Unlocks the \"Farm Nearby Crops\" task.");
//            FARMING_WHITELIST.setGeneralInfo("dad2828d-15dc-4e66-98c2-a7fbb29b5fb6", AbilityType.AI, "Farming Whitelist", "Unlocks the log whitelist for farming.");
//            FASTER_FARMING.setGeneralInfo("ddc11349-e1fa-4e73-af38-25dbbb2ffa1b", AbilityType.STAT, "Efficiency", "Bonus 10% farming speed.");
//            FASTER_FARMING_FOR_HEALTH.setGeneralInfo("c9f8660b-24d4-4a8b-9c47-765afc5ebecb", AbilityType.STAT, "Adrenaline", "The lower your blockling's health, the faster they will farm.");
//            FASTER_FARMING_FOR_CROPS.setGeneralInfo("d8d2cb1c-4323-4ed3-bb9a-d16a86e922c3", AbilityType.STAT, "Momentum", "Your blockling farms faster for each consecutive crop farmed.");
//            FASTER_FARMING_FOR_DURABILITY.setGeneralInfo("4338efeb-47b5-44dd-b02d-751e19ed5d38", AbilityType.STAT, "Hasty", "Your blockling farms 25% faster but their hoes use 2x the durability.");
//            FASTER_FARMING_IN_DARK.setGeneralInfo("bf9150e9-a032-4baa-aa7e-94b246320c2b", AbilityType.STAT, "Night Owl", "Your blockling farms faster the lower the light level.");
//            BONEMEAL_NEARBY.setGeneralInfo("cdd77205-8b09-4e9d-8e8e-393c09e7674f", AbilityType.AI, "Fertiliser", "Unlocks the \"Fertilise Crops\" task.");
//            BONEMEAL_EFFICIENT.setGeneralInfo("d3b5c385-080a-44de-97be-0f7162d55191", AbilityType.OTHER, "Resourceful Fertiliser", "50% chance to not use bonemeal when fertilising.");
//
//            NOVICE_FARMER.setGuiInfo(0, 0, 0, 0, 0xdddddd);
//            FARMING_WHITELIST.setGuiInfo(0, 70, 6, 0, 0xeeeeee);
//            FASTER_FARMING.setGuiInfo(70, 0, 1, 0, 0xffd56d);
//            FASTER_FARMING_FOR_HEALTH.setGuiInfo(140, -50, 2, 0, 0xad79b5);
//            FASTER_FARMING_FOR_CROPS.setGuiInfo(140, 50, 3, 0, 0xad79b5);
//            FASTER_FARMING_FOR_DURABILITY.setGuiInfo(210, -50, 4, 0, 0x4eb2aa);
//            FASTER_FARMING_IN_DARK.setGuiInfo(210, 50, 5, 0, 0x2b2a3d);
//            BONEMEAL_NEARBY.setGuiInfo(-70, -100, 7, 0, 0xf5fff2);
//            BONEMEAL_EFFICIENT.setGuiInfo(-70, -190, 8, 0, 0xdbffd1);
//
//            FARMING_WHITELIST.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_FARMING.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_FARMING_FOR_HEALTH.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_FARMING_FOR_CROPS.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_FARMING_FOR_DURABILITY.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            FASTER_FARMING_IN_DARK.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            BONEMEAL_NEARBY.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//            BONEMEAL_EFFICIENT.setConnectionType(AbilityWidget.ConnectionType.SINGLE_LONGEST_FIRST);
//
//            NOVICE_FARMER.setSkillPointsRequired(1);
//            FARMING_WHITELIST.setSkillPointsRequired(2);
//            FASTER_FARMING.setSkillPointsRequired(1);
//            FASTER_FARMING_FOR_HEALTH.setSkillPointsRequired(3);
//            FASTER_FARMING_FOR_CROPS.setSkillPointsRequired(3);
//            FASTER_FARMING_FOR_DURABILITY.setSkillPointsRequired(3);
//            FASTER_FARMING_IN_DARK.setSkillPointsRequired(3);
//            BONEMEAL_NEARBY.setSkillPointsRequired(2);
//            BONEMEAL_EFFICIENT.setSkillPointsRequired(1);
//
//            FARMING_WHITELIST.setLevelRequirements(new Pair<>("farmingLevel", 10.0f));
//            FASTER_FARMING.setLevelRequirements(new Pair<>("farmingLevel", 5.0f));
//            FASTER_FARMING_FOR_HEALTH.setLevelRequirements(new Pair<>("farmingLevel", 25.0f));
//            FASTER_FARMING_FOR_CROPS.setLevelRequirements(new Pair<>("farmingLevel", 25.0f));
//            FASTER_FARMING_FOR_DURABILITY.setLevelRequirements(new Pair<>("farmingLevel", 25.0f));
//            FASTER_FARMING_IN_DARK.setLevelRequirements(new Pair<>("farmingLevel", 25.0f));
//            BONEMEAL_NEARBY.setLevelRequirements(new Pair<>("farmingLevel", 20.0f));
//            BONEMEAL_EFFICIENT.setLevelRequirements(new Pair<>("farmingLevel", 30.0f));
//
//            FARMING_WHITELIST.setParents(NOVICE_FARMER);
//            FASTER_FARMING.setParents(NOVICE_FARMER);
//            FASTER_FARMING_FOR_HEALTH.setParents(FASTER_FARMING);
//            FASTER_FARMING_FOR_CROPS.setParents(FASTER_FARMING);
//            FASTER_FARMING_FOR_DURABILITY.setParents(FASTER_FARMING);
//            FASTER_FARMING_IN_DARK.setParents(FASTER_FARMING);
//            BONEMEAL_NEARBY.setParents(NOVICE_FARMER);
//            BONEMEAL_EFFICIENT.setParents(BONEMEAL_NEARBY);
//
//            createConflictGroup(FASTER_FARMING_FOR_HEALTH, FASTER_FARMING_FOR_CROPS, FASTER_FARMING_FOR_DURABILITY, FASTER_FARMING_IN_DARK);
//        }
//
//        private static Ability create()
//        {
//            Ability ability = new Ability();
//            ABILITIES.add(ability);
//            return ability;
//        }
//    }
//
//    private static void createConflictGroup(Ability... abilities)
//    {
//        for (int i = 0; i < abilities.length; i++)
//        {
//            Ability ability = abilities[i];
//            List<Ability> conflicts = new LinkedList<>(Arrays.asList(abilities));
//            conflicts.remove(i);
//            ability.setConflicts(Arrays.copyOf(conflicts.toArray(), conflicts.size(), Ability[].class));
//        }
//    }
}
