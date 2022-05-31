package com.willr27.blocklings;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;
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
         * The default list of additional ores.
         */
        @Nonnull
        public final List<String> defaultAdditionalOres = new ArrayList<>();

        /**
         * The blocks to ensure are excluded from the list of blocks that are regarded as ores.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedOres;

        /**
         * The default list of excluded ores.
         */
        @Nonnull
        public final List<String> defaultExcludedOres = new ArrayList<>();

        /**
         * The blocks to ensure are added to the list of blocks that are regarded as logs.
         * This should only include blocks that are not tagged as logs.
         * The final list will also include any block with the logs tag (disjoint with the excluded logs).
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> additionalLogs;

        /**
         * The default list of additional logs.
         */
        @Nonnull
        public final List<String> defaultAdditionalLogs = new ArrayList<>();

        /**
         * The blocks to ensure are excluded from the list of blocks that are regarded as logs.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedLogs;

        /**
         * The default list of excluded logs.
         */
        @Nonnull
        public final List<String> defaultExcludedLogs = new ArrayList<>();

        /**
         * The blocks to ensure are added to the list of blocks that are regarded as leaves.
         * This should only include blocks that are not tagged as leaves.
         * The final list will also include any block with the leaves tag (disjoint with the excluded leaves).
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> additionalLeaves;

        /**
         * The default list of additional leaves.
         */
        @Nonnull
        public final List<String> defaultAdditionalLeaves = new ArrayList<>();

        /**
         * The blocks to ensure are excluded from the list of blocks that are regarded as leaves.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedLeaves;

        /**
         * The default list of excluded leaves.
         */
        @Nonnull
        public final List<String> defaultExcludedLeaves = new ArrayList<>();

        /**
         * @param builder the builder used to create the config.
         */
        public Common(@Nonnull ForgeConfigSpec.Builder builder)
        {
            Config.setInsertionOrderPreserved(true);

            builder.push("Mining");

            additionalOres = builder
                    .comment("The list of ores (as registry names) to ensure are included in the list of blocks that are regarded as ores.",
                            "Any block with the ores tag will automatically be added, so only include ores without that tag here.",
                            "NOT ALL BLOCKS ARE GUARANTEED TO WORK.",
                            "Example: [\"minecraft:stone\", \"minecraft:obsidian\"]")
                    .worldRestart()
                    .defineList("additionalOres", () -> defaultAdditionalOres, s -> true);

            excludedOres = builder
                    .comment("The list of ores (as registry names) to ensure are excluded from the list of blocks that regarded as ores.",
                            "Any block with the ores tag will automatically be added unless specified here.",
                            "Example: [\"minecraft:coal_ore\", \"minecraft:diamond_ore\"]")
                    .worldRestart()
                    .defineList("excludedOres", () -> defaultExcludedOres, s -> true);

            builder.pop();

            builder.push("Woodcutting");

            additionalLogs = builder
                    .comment("The list of logs (as registry names) to ensure are included in the list of blocks that are regarded as logs.",
                            "Any block with the logs tag will automatically be added, so only include logs without that tag here.",
                            "NOT ALL BLOCKS ARE GUARANTEED TO WORK.",
                            "Example: [\"minecraft:oak_planks\", \"minecraft:spruce_planks\"]")
                    .worldRestart()
                    .defineList("additionalLogs", () -> defaultAdditionalLogs, s -> true);

            excludedLogs = builder
                    .comment("The list of logs (as registry names) to ensure are excluded from the list of blocks that regarded as logs.",
                            "Any block with the logs tag will automatically be added unless specified here.",
                            "Example: [\"minecraft:oak_logs\", \"minecraft:spruce_logs\"]")
                    .worldRestart()
                    .defineList("excludedLogs", () -> defaultExcludedLogs, s -> true);

            additionalLeaves = builder
                    .comment("The list of leaves (as registry names) to ensure are included in the list of blocks that are regarded as leaves.",
                            "Any block with the leaves tag will automatically be added, so only include leaves without that tag here.",
                            "NOT ALL BLOCKS ARE GUARANTEED TO WORK.",
                            "Example: [\"minecraft:grass\", \"minecraft:stone\"]")
                    .worldRestart()
                    .defineList("additionalLeaves", () -> defaultAdditionalLeaves, s -> true);

            excludedLeaves = builder
                    .comment("The list of leaves (as registry names) to ensure are excluded from the list of blocks that regarded as leaves.",
                            "Any block with the leaves tag will automatically be added unless specified here.",
                            "Example: [\"minecraft:oak_leaves\", \"minecraft:spruce_leaves\"]")
                    .worldRestart()
                    .defineList("excludedLeaves", () -> defaultExcludedLeaves, s -> true);

            builder.pop();
        }
    }

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
     * Static constructor to initialize the config.
     */
    static
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
