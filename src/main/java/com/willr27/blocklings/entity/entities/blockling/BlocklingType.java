package com.willr27.blocklings.entity.entities.blockling;

import com.willr27.blocklings.util.BlocklingsResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class BlocklingType
{
    public static final List<BlocklingType> TYPES = new ArrayList<>();

    public static final BlocklingType GRASS = create("grass", 8).addBonusStats(2.0f, 1.0f, 1.0f, 2.0f);
    public static final BlocklingType OAK_LOG = create("oak_log", 7).addBonusStats(3.0f, 1.0f, 2.0f, 2.0f);
    public static final BlocklingType STONE = create("stone", 2).addBonusStats(5.0f, 1.0f, 3.0f, 1.0f);
    public static final BlocklingType IRON = create("iron", 2).addBonusStats(6.0f, 2.0f, 4.0f, 1.0f);
    public static final BlocklingType QUARTZ = create("quartz", 5).addBonusStats(3.0f, 4.0f, 1.0f, 2.0f);
    public static final BlocklingType LAPIS = create("lapis", 0).addBonusStats(5.0f, 3.0f, 1.0f, 3.0f);
    public static final BlocklingType GOLD = create("gold", 0).addBonusStats(1.0f, 4.0f, 1.0f, 5.0f);
    public static final BlocklingType EMERALD = create("emerald", 0).addBonusStats(5.0f, 3.0f, 3.0f, 1.0f);
    public static final BlocklingType DIAMOND = create("diamond", 0).addBonusStats(8.0f, 6.0f, 4.0f, 1.0f);
    public static final BlocklingType OBSIDIAN = create("obsidian", 0).addBonusStats(25.0f, 5.0f, 8.0f, 0.0f);

    static
    {
//        GRASS.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        GRASS.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
//        GRASS.predicates.add((blockling, world) -> canSeeSky(blockling, world));
//
//        OAK_LOG.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        OAK_LOG.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
//        OAK_LOG.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 3, Blocks.OAK_LOG));
//
//        STONE.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        STONE.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.STONE));
//
//        IRON.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        IRON.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.STONE, Blocks.IRON_ORE));
//        IRON.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 2, Blocks.IRON_ORE));
//
//        QUARTZ.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.THE_NETHER));
//        QUARTZ.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.NETHERRACK, Blocks.NETHER_QUARTZ_ORE));
//        QUARTZ.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 1, Blocks.NETHER_QUARTZ_ORE));
//
//        LAPIS.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        LAPIS.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.STONE, Blocks.LAPIS_ORE));
//        LAPIS.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 3, Blocks.LAPIS_ORE));
//
//        GOLD.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        GOLD.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.STONE, Blocks.GOLD_ORE));
//        GOLD.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 3, Blocks.GOLD_ORE));
//
//        EMERALD.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        EMERALD.predicates.add((blockling, world) -> isInBiome(blockling, world, Biomes.MOUNTAINS, Biomes.MOUNTAIN_EDGE, Biomes.GRAVELLY_MOUNTAINS, Biomes.MODIFIED_GRAVELLY_MOUNTAINS, Biomes.SNOWY_MOUNTAINS, Biomes.SNOWY_TAIGA_MOUNTAINS, Biomes.TAIGA_MOUNTAINS, Biomes.WOODED_MOUNTAINS));
//        EMERALD.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.STONE, Blocks.EMERALD_ORE));
//        EMERALD.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 3, Blocks.EMERALD_ORE));
//
//        DIAMOND.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        DIAMOND.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.STONE, Blocks.DIAMOND_ORE));
//        DIAMOND.predicates.add((blockling, world) -> blockNearbyIs(blockling, world, 3, Blocks.DIAMOND_ORE));
//
//        OBSIDIAN.predicates.add((blockling, world) -> isInDimension(blockling, world, DimensionType.OVERWORLD));
//        OBSIDIAN.predicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.OBSIDIAN));
    }



    public final ResourceLocation entityTexture;
    public final int spawnRateReduction;
    private float bonusHealth;
    private float bonusDamage;
    private float bonusArmour;
    private float bonusSpeed;
    public List<BiPredicate<BlocklingEntity, IWorld>> predicates = new ArrayList<>();

    public BlocklingType(String texture, int spawnRateReduction)
    {
        this.entityTexture = new BlocklingsResourceLocation("textures/entity/blockling/blockling_" + texture + ".png");
        this.spawnRateReduction = spawnRateReduction + 1;
    }

    private static BlocklingType create(String texture, int spawnRateReduction)
    {
        BlocklingType type = new BlocklingType(texture, spawnRateReduction);
        TYPES.add(type);
        return type;
    }

    private BlocklingType addBonusStats(float health, float damage, float armour, float speed)
    {
        this.bonusHealth = health;
        this.bonusDamage = damage;
        this.bonusArmour = armour;
        this.bonusSpeed = speed;
        return this;
    }

    public float getBonusHealth()
    {
        return bonusHealth;
    }

    public float getBonusDamage()
    {
        return bonusDamage;
    }

    public float getBonusArmour()
    {
        return bonusArmour;
    }

    public float getBonusSpeed()
    {
        return bonusSpeed;
    }



    private static boolean isInDimension(BlocklingEntity blockling, IWorld world, DimensionType... dimensionTypes)
    {
        for (DimensionType dimensionType : dimensionTypes)
        {
//            if (world.getDimension().getType() == dimensionType)
//            {
//                return true;
//            }
        }
        return false;
    }

    private static boolean isInBiome(BlocklingEntity blockling, IWorld world, Biome... biomes)
    {
        for (Biome biome : biomes)
        {
            if (world.getBiome(blockling.blockPosition()) == biome)
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
//        int startX = (int) blockling.posX - radius;
//        int startY = (int) blockling.posY - radius;
//        int startZ = (int) blockling.posZ - radius;
//
//        int endX = (int) blockling.posX + radius;
//        int endY = (int) blockling.posY + radius;
//        int endZ = (int) blockling.posZ + radius;
//
//        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
//        for (int x = startX; x <= endX; x++)
//        {
//            for (int y = startY; y <= endY; y++)
//            {
//                for (int z = startZ; z <= endZ; z++)
//                {
//                    blockPos.setPos(x, y, z);
//                    if (!world.isBlockLoaded(blockPos))
//                    {
//                        continue;
//                    }
//
//                    Block block = world.getBlockState(blockPos).getBlock();
//
//                    for (Block block2 : blocks)
//                    {
//                        if (block == block2)
//                        {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }

        return false;
    }
}
