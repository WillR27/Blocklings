package com.willr27.blocklings;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
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
         * The ores to ensure are added to the ore whitelists for mining tasks.
         * This should only include blocks that are not tagged as ores.
         * The final list will also include any block with the ores tag (disjoint with the ore blacklist).
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> additionalOres;

        /**
         * The default list of additional ores.
         */
        @Nonnull
        public final List<String> defaultAdditionalOres = new ArrayList<>();

        /**
         * The ores to ensure are excluded from the ore whitelists for mining tasks.
         */
        @Nonnull
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> excludedOres;

        /**
         * The default list of excluded ores.
         */
        @Nonnull
        public final List<String> defaultExcludedOres = new ArrayList<>();

        /**
         * @param builder the builder used to create the config.
         */
        public Common(@Nonnull ForgeConfigSpec.Builder builder)
        {
            Config.setInsertionOrderPreserved(true);

            builder.push("Ores");

            additionalOres = builder
                    .comment("The list of ores (as registry names) to ensure are included as part of the ore whitelist for mining tasks.",
                            "Any block with the ores tag will automatically be added, so only include ores without that tag here.",
                            "NOT all blocks are guaranteed to work.",
                            "Example: [\"minecraft:stone\", \"minecraft:obsidian\"]")
                    .worldRestart()
                    .defineList("additionalOres", () -> defaultAdditionalOres, s -> true);

            excludedOres = builder
                    .comment("The list of ores (as registry names) to ensure are excluded as part of the ore whitelist for mining tasks.",
                            "Any block with the ores tag will automatically be added unless specified here.",
                            "Example: [\"minecraft:coal_ore\", \"minecraft:diamond_ore\"]")
                    .worldRestart()
                    .defineList("excludedOres", () -> defaultExcludedOres, s -> true);

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
