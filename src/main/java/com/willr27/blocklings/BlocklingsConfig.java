package com.willr27.blocklings;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BlocklingsConfig
{
    public static class Common
    {
        private static final int defaultInt1 = 37;
        private static final boolean defaultBool1 = true;

        public final ForgeConfigSpec.ConfigValue<Integer> Int1;
        public final ForgeConfigSpec.ConfigValue<Boolean> Bool1;


        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("category1");
            this.Int1 = builder.comment("This is a nice description of your option. Make it a lot longer than this. Max is 60, default is 37. Enjoy...")
                    .worldRestart()
                    .defineInRange("Short but readable name", defaultInt1, 1, 60);
            this.Bool1 = builder.comment("asdasd as asd asd asd asdas aasd as asd asd. asd as asd asd. asdasdad asd.")
                    .define("Short but readable name 2", defaultBool1);
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static //constructor
    {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }
}
