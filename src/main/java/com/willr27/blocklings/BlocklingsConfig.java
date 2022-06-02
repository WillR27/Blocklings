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
         * The blocks to ensure are excluded from the list of blocks that are regarded as ores.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedOres;

        /**
         * The list of tuples of blocks that the user wants to add as trees.
         * Format: ["[x:y; a:b; j:k]", "[x:y; a:b; j:k]"] (log, leaf, sapling).
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> customTrees;

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
                    .defineList("additionalOres", () -> new ArrayList<>(), s -> true);

            excludedOres = builder
                    .comment("The list of ores (as registry names) to ensure are excluded from the list of blocks that regarded as ores.",
                            "Any block with the ores tag will automatically be added unless specified here.",
                            "Example: [\"minecraft:coal_ore\", \"minecraft:diamond_ore\"]")
                    .worldRestart()
                    .defineList("excludedOres", () -> new ArrayList<>(), s -> true);

            builder.pop();

            builder.push("Woodcutting");

            customTrees = builder
                    .comment("The list of tuples of blocks that you want to additionally add as trees",
                            "This is useful for modded trees that don't already have support.",
                            "NOT ALL BLOCKS ARE GUARANTEED TO WORK.",
                            "Example: [\"[minecraft:oak_log; minecraft:oak_leaves; minecraft:oak_sapling]\", \"[...]\"]",
                            "This would add oak trees as a custom tree (but they already have support so you don't need to add them here).")
                    .worldRestart()
                    .defineList("customTrees", () -> new ArrayList<>(), s -> true);

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
