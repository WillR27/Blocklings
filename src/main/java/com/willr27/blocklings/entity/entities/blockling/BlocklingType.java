package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.util.BlocklingsResourceLocation;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class BlocklingType
{
    public static final List<BlocklingType> TYPES = new ArrayList<>();

    public static final BlocklingType GRASS = create("grass", 8).addCombatStats(2.0f, 1.0f, 3.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 1.0f, 2.0f);
    public static final BlocklingType OAK_LOG = create("oak_log", 2).addCombatStats(3.0f, 1.0f, 3.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 2.0f, 1.0f);
    public static final BlocklingType STONE = create("stone", 12).addCombatStats(5.0f, 1.0f, 3.0f, 2.0f, 1.0f, 0.3f, 2.5f).addGatheringStats(1.5f, 0.5f, 0.5f);
    public static final BlocklingType IRON = create("iron", 2).addCombatStats(6.0f, 2.0f, 3.0f, 2.0f, 1.0f, 0.1f, 3.0f).addGatheringStats(2.0f, 0.5f, 0.5f);
    public static final BlocklingType QUARTZ = create("quartz", 0).addCombatStats(3.0f, 4.0f, 3.0f, 1.0f, 0.5f, 0.1f, 3.5f).addGatheringStats(2.0f, 0.5f, 0.5f);
    public static final BlocklingType LAPIS = create("lapis", 0).addCombatStats(5.0f, 3.0f, 3.0f, 1.0f, 0.5f, 0.1f, 3.0f).addGatheringStats(2.0f, 0.5f, 0.5f);
    public static final BlocklingType GOLD = create("gold", 0).addCombatStats(1.0f, 4.0f, 3.0f, 1.0f, 0.5f, 0.1f, 4.0f).addGatheringStats(2.5f, 0.5f, 0.5f);
    public static final BlocklingType EMERALD = create("emerald", 0).addCombatStats(5.0f, 3.0f, 3.0f, 2.0f, 1.0f, 0.2f, 3.0f).addGatheringStats(2.5f, 0.5f, 0.5f);
    public static final BlocklingType DIAMOND = create("diamond", 0).addCombatStats(8.0f, 6.0f, 3.0f, 3.0f, 1.5f, 0.2f, 3.0f).addGatheringStats(3.0f, 0.5f, 0.5f);
    public static final BlocklingType OBSIDIAN = create("obsidian", 0).addCombatStats(25.0f, 5.0f, 2.0f, 4.0f, 2.0f, 0.8f, 2.0f).addGatheringStats(1.0f, 0.5f, 0.5f);

    static
    {
        GRASS.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        GRASS.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        GRASS.predicates.add((blockling, world) -> canSeeSky(blockling, world));

        OAK_LOG.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        OAK_LOG.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        OAK_LOG.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 16, Blocks.OAK_LOG));

        STONE.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        STONE.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.STONE));

        IRON.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        IRON.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 5, Blocks.IRON_ORE));

        QUARTZ.predicates.add((blockling, world) -> isInWorld(blockling, world, World.NETHER));
        QUARTZ.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 5, Blocks.NETHER_QUARTZ_ORE));

        LAPIS.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        LAPIS.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 5, Blocks.LAPIS_ORE));

        GOLD.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        GOLD.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 5, Blocks.GOLD_ORE));

        EMERALD.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        EMERALD.predicates.add((blockling, world) -> isInBiome(blockling, world, Biomes.MOUNTAINS, Biomes.MOUNTAIN_EDGE, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SNOWY_MOUNTAINS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.TAIGA_MOUNTAINS, Biomes.WOODED_MOUNTAINS));
        EMERALD.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 5, Blocks.EMERALD_ORE));

        DIAMOND.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        DIAMOND.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 5, Blocks.DIAMOND_ORE));

        OBSIDIAN.predicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        OBSIDIAN.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 5, Blocks.OBSIDIAN));
    }

    public final ResourceLocation entityTexture;
    public final TranslationTextComponent name;
    public final int spawnRateReduction;
    private float maxHealth = 0.0f;
    private float attackDamage = 0.0f;
    private float attackSpeed = 3.0f;
    private float armour = 0.0f;
    private float armourToughness = 0.0f;
    private float knockbackResistance = 0.0f;
    private float moveSpeed = 0.0f;
    private float miningSpeed = 0.0f;
    private float woodcuttingSpeed = 0.0f;
    private float farmingSpeed = 0.0f;
    public List<BiPredicate<BlocklingEntity, IWorld>> predicates = new ArrayList<>();

    public BlocklingType(String key, int spawnRateReduction)
    {
        this.entityTexture = new BlocklingsResourceLocation("textures/entity/blockling/blockling_" + key + ".png");
        this.name = new BlocklingsTranslationTextComponent("type." + key);
        this.spawnRateReduction = spawnRateReduction + 1;
    }

    private static BlocklingType create(String texture, int spawnRateReduction)
    {
        BlocklingType type = new BlocklingType(texture, spawnRateReduction);
        TYPES.add(type);
        return type;
    }

    private BlocklingType addCombatStats(float health, float attackDamage, float attackSpeed, float armour, float armourToughness, float knockbackResistance, float moveSpeed)
    {
        this.maxHealth = health;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.armour = armour;
        this.armourToughness = armourToughness;
        this.knockbackResistance = knockbackResistance;
        this.moveSpeed = moveSpeed;

        return this;
    }

    private BlocklingType addGatheringStats(float miningSpeed, float woodcuttingSpeed, float farmingSpeed)
    {
        this.miningSpeed = miningSpeed;
        this.woodcuttingSpeed = woodcuttingSpeed;
        this.farmingSpeed = farmingSpeed;

        return this;
    }

    public float getMaxHealth()
    {
        return maxHealth;
    }

    public float getAttackDamage()
    {
        return attackDamage;
    }

    public float getArmour()
    {
        return armour;
    }

    public float getMoveSpeed()
    {
        return moveSpeed;
    }

    public float getAttackSpeed()
    {
        return attackSpeed;
    }

    public float getArmourToughness()
    {
        return armourToughness;
    }

    public float getKnockbackResistance()
    {
        return knockbackResistance;
    }

    public float getMiningSpeed()
    {
        return miningSpeed;
    }

    public float getWoodcuttingSpeed()
    {
        return woodcuttingSpeed;
    }

    public float getFarmingSpeed()
    {
        return farmingSpeed;
    }

    private static boolean isInWorld(BlocklingEntity blockling, IWorld world, RegistryKey<World>... worldTypes)
    {
        for (RegistryKey<World> worldType : worldTypes)
        {
            if (blockling.level.dimension() == worldType)
            {
                return true;
            }
        }
        return false;
    }

    private static boolean isInBiome(BlocklingEntity blockling, IWorld world, RegistryKey<Biome>... biomes)
    {
        for (RegistryKey<Biome> biome : biomes)
        {
            if (world.getBiome(blockling.blockPosition()).getRegistryName() == biome.getRegistryName())
            {
                return true;
            }
        }
        return false;
    }

    private static boolean canSeeSky(BlocklingEntity blockling, IWorld world)
    {
        return world.canSeeSky(blockling.blockPosition());
    }

    private static boolean blockBelowIs(BlocklingEntity blockling, IWorld world, Block... blocks)
    {
        Block testBlock = world.getBlockState(blockling.blockPosition().below()).getBlock();

        for (Block block : blocks)
        {
            if (testBlock == block)
            {
                return true;
            }
        }

        return false;
    }

    private static boolean blockNearbyIs(BlocklingEntity blockling, IWorld world, int radius, Block... blocks)
    {
        int startX = (int) blockling.getX() - radius;
        int startY = (int) blockling.getY() - radius;
        int startZ = (int) blockling.getZ() - radius;

        int endX = (int) blockling.getX() + radius;
        int endY = (int) blockling.getY() + radius;
        int endZ = (int) blockling.getZ() + radius;

        for (int x = startX; x <= endX; x++)
        {
            for (int y = startY; y <= endY; y++)
            {
                for (int z = startZ; z <= endZ; z++)
                {
                    BlockPos blockPos = new BlockPos(x, y, z);

                    if (!world.isAreaLoaded(blockPos, 1))
                    {
                        continue;
                    }

                    Block block = world.getBlockState(blockPos).getBlock();

                    for (Block block2 : blocks)
                    {
                        if (block == block2)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
