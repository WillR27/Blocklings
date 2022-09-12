package com.willr27.blocklings.config;

import com.electronwill.nightconfig.core.Config;
import com.willr27.blocklings.entity.blockling.goal.goals.gather.BlocklingWoodcutGoal;
import com.willr27.blocklings.util.WorldUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The class used to handle the Blocklings' config.
 */
public class BlocklingsConfig
{
    /**
     * The instance of the common config to access values from.
     */
    @Nonnull
    public static final Common COMMON;

    /**
     * The common config spec.
     */
    @Nonnull
    public static final ForgeConfigSpec COMMON_SPEC;

    /**
     * The instance of the common config to access values from.
     */
    @Nonnull
    public static final Client CLIENT;

    /**
     * The common config spec.
     */
    @Nonnull
    public static final ForgeConfigSpec CLIENT_SPEC;

    /**
     * Static constructor to initialize the config.
     */
    static
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();

        Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
    }

    /**
     * Initialises the configs.
     */
    public static void init()
    {
        ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();

        activeContainer.addConfig(new ModConfig(ModConfig.Type.COMMON, BlocklingsConfig.COMMON_SPEC, activeContainer));
        activeContainer.addConfig(new ModConfig(ModConfig.Type.CLIENT, BlocklingsConfig.CLIENT_SPEC, activeContainer));
    }

    /**
     * Config options shared by both the client and server.
     */
    public static class Common
    {
        /**
         * The blocks to ensure are added to the list of blocks that are regarded as ores.
         * This should only include blocks that are not tagged as ores.
         * The final list will also include any block with the ores tag (disjoint with the excluded ores).
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> additionalOres;

        /**
         * The blocks to ensure are excluded from the list of blocks that are regarded as ores.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedOres;

        /**
         * The minimum number of leaves blocks for each log block to classify a tree as valid.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<Double> defaultMinLeavesToLogRatio;

        /**
         * The list of tuples of blocks that the user wants to add as trees.
         * Format: ["[x:y; a:b; j:k]", "[x:y; a:b; j:k]"] (log, leaf, sapling).
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> customTrees;

        /**
         * The blocks to ensure are added to the list of blocks that are regarded as crops.
         * This should only include blocks that are not added by default.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> additionalCrops;

        /**
         * The blocks to ensure are excluded from the list of blocks that are regarded as crops.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedCrops;

        /**
         * @param builder the builder used to create the config.
         */
        public Common(@Nonnull ForgeConfigSpec.Builder builder)
        {
            Config.setInsertionOrderPreserved(true);

            builder.push("Mining");

            additionalOres = builder
                    .comment("The list of blocks (as registry names) to ensure are included in the list of blocks that are regarded as ores.",
                            "Any block with an ores tag will automatically be added, so only include ores without that tag here.",
                            "NOT ALL BLOCKS ARE GUARANTEED TO WORK.",
                            "Example: [\"minecraft:stone\", \"minecraft:obsidian\"]")
                    .worldRestart()
                    .defineList("additionalOres", () -> new ArrayList<>(), s -> true);

            excludedOres = builder
                    .comment("The list of blocks (as registry names) to ensure are excluded from the list of blocks that regarded as ores.",
                            "Any block with an ores tag will automatically be added unless specified here.",
                            "This is useful if you notice modded blocks that are tagged as ores that you don't want/think should be.",
                            "Example: [\"minecraft:coal_ore\", \"minecraft:diamond_ore\"]")
                    .worldRestart()
                    .defineList("excludedOres", () -> new ArrayList<>(), s -> true);

            builder.pop();

            builder.push("Woodcutting");

            defaultMinLeavesToLogRatio = builder
                    .comment("The default minimum number of leaves blocks for each log block to classify a tree as valid.",
                             "E.g. a ratio of 2.0 means that 5 connected logs and more than 10 connected leaves would classify as a tree.",
                             "This can be changed on a per task basis using the slider when configuring a woodcutting task.",
                             "This is also used as the ratio to find trees for log blocklings' passive abilities.")
                    .worldRestart()
                    .defineInRange("defaultMinLeavesToLogRatio", WorldUtil.DEFAULT_MIN_LEAVES_TO_LOGS_RATIO, BlocklingWoodcutGoal.MIN_MIN_LEAVES_TO_LOGS_RATIO, BlocklingWoodcutGoal.MAX_MIN_LEAVES_TO_LOGS_RATIO);

            customTrees = builder
                    .comment("The list of tuples of blocks that you want to additionally add as trees",
                            "This is useful for modded trees that don't already have support.",
                            "NOT ALL BLOCKS ARE GUARANTEED TO WORK.",
                            "Example: [\"[minecraft:oak_log; minecraft:oak_leaves; minecraft:oak_sapling]\", \"[...]\"]",
                            "This would add oak trees as a custom tree (but they already have support so you don't need to add them here).")
                    .worldRestart()
                    .defineList("customTrees", () -> new ArrayList<>(), s -> true);

            builder.pop();

            builder.push("Farming");

            additionalCrops = builder
                    .comment("The list of blocks (as registry names) to ensure are included in the list of blocks that are regarded as crops.",
                            "NOT ALL BLOCKS ARE GUARANTEED TO WORK.",
                            "Example: [\"minecraft:wheat\", \"minecraft:melon\"]")
                    .worldRestart()
                    .defineList("additionalCrops", () -> new ArrayList<>(), s -> true);

            excludedCrops = builder
                    .comment("The list of blocks (as registry names) to ensure are excluded from the list of blocks that regarded as crops.",
                            "This is useful if you notice modded blocks that have been added as crops that you don't want/think should be.",
                            "Example: [\"minecraft:wheat\", \"minecraft:melon\"]")
                    .worldRestart()
                    .defineList("excludedCrops", () -> new ArrayList<>(), s -> true);

            builder.pop();
        }
    }

    /**
     * Config options only available to each client.
     */
    public static class Client
    {
        /**
         * Whether the mixed blockling type textures are disabled.
         */
        public final ForgeConfigSpec.ConfigValue<Boolean> disableDirtyBlocklings;

        /**
         * @param builder the builder used to create the config.
         */
        public Client(@Nonnull ForgeConfigSpec.Builder builder)
        {
            Config.setInsertionOrderPreserved(true);

            builder.push("Misc");

            disableDirtyBlocklings = builder
                    .comment("Set this to true to disable the mixed blockling type textures.")
                    .define("disableDirtyBlocklings", false);

            builder.pop();
        }
    }
}
