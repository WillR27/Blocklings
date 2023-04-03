package com.willr27.blocklings.entity.blockling;

import com.willr27.blocklings.config.BlocklingsConfig;
import com.willr27.blocklings.util.BlocklingsResourceLocation;
import com.willr27.blocklings.util.BlocklingsTranslationTextComponent;
import com.willr27.blocklings.util.ItemUtil;
import com.willr27.blocklings.util.Version;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * The blockling type contains properties used to determine textures, spawning and stats.
 *
 * NOTE: If the key of anything type changes it must be reflected in the find method using the version.
 */
public class BlocklingType
{
    /**
     * The list of all available blockling types.
     */
    public static final List<BlocklingType> TYPES = new ArrayList<>();

    public static final BlocklingType GRASS = create("grass", 5).addCombatStats(2.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.0f, 1.0f, 2.0f);
    public static final BlocklingType DIRT = create("dirt", 1).addCombatStats(2.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.0f, 1.0f, 2.0f);
    public static final BlocklingType OAK_LOG = create("oak_log", 0).addCombatStats(3.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 2.0f, 1.0f);
    public static final BlocklingType BIRCH_LOG = create("birch_log", 0).addCombatStats(3.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 2.0f, 1.0f);
    public static final BlocklingType SPRUCE_LOG = create("spruce_log", 0).addCombatStats(3.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 2.0f, 1.0f);
    public static final BlocklingType JUNGLE_LOG = create("jungle_log", 0).addCombatStats(3.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 2.0f, 1.0f);
    public static final BlocklingType DARK_OAK_LOG = create("dark_oak_log", 0).addCombatStats(3.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 2.0f, 1.0f);
    public static final BlocklingType ACACIA_LOG = create("acacia_log", 0).addCombatStats(3.0f, 1.0f, 1.5f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(0.5f, 2.0f, 1.0f);
    public static final BlocklingType STONE = create("stone", 0).addCombatStats(5.0f, 1.0f, 1.0f, 2.0f, 1.0f, 0.3f, 2.5f).addGatheringStats(1.5f, 0.5f, 0.5f);
    public static final BlocklingType IRON = create("iron", 0).addCombatStats(6.0f, 2.0f, 1.0f, 2.0f, 1.0f, 0.1f, 3.0f).addGatheringStats(2.0f, 0.5f, 0.5f);
    public static final BlocklingType QUARTZ = create("quartz", 0).addCombatStats(3.0f, 4.0f, 2.0f, 1.0f, 0.5f, 0.1f, 3.5f).addGatheringStats(2.0f, 0.5f, 0.5f);
    public static final BlocklingType LAPIS = create("lapis", 0).addCombatStats(5.0f, 3.0f, 1.5f, 1.0f, 0.5f, 0.1f, 3.0f).addGatheringStats(2.0f, 0.5f, 0.5f);
    public static final BlocklingType GOLD = create("gold", 0).addCombatStats(1.0f, 4.0f, 2.5f, 1.0f, 0.5f, 0.1f, 4.0f).addGatheringStats(2.5f, 0.5f, 0.5f);
    public static final BlocklingType EMERALD = create("emerald", 0).addCombatStats(5.0f, 3.0f, 1.5f, 2.0f, 1.0f, 0.2f, 3.0f).addGatheringStats(2.5f, 0.5f, 0.5f);
    public static final BlocklingType DIAMOND = create("diamond", 0).addCombatStats(8.0f, 6.0f, 2.0f, 3.0f, 1.5f, 0.2f, 3.0f).addGatheringStats(3.0f, 0.5f, 0.5f);
    public static final BlocklingType NETHERITE = create("netherite", 0).addCombatStats(15.0f, 7.0f, 2.0f, 4.0f, 2.0f, 0.3f, 2.5f).addGatheringStats(2.5f, 1.0f, 1.0f);
    public static final BlocklingType OBSIDIAN = create("obsidian", 0).addCombatStats(25.0f, 5.0f, 1.0f, 4.0f, 2.0f, 0.8f, 2.0f).addGatheringStats(1.0f, 0.5f, 0.5f);
    public static final BlocklingType GLOWSTONE = create("glowstone", 0).addCombatStats(2.0f, 2.0f, 2.0f, 0.0f, 0.0f, 0.0f, 3.0f).addGatheringStats(1.0f, 1.0f, 1.0f);

    /**
     * Initialises the additional properties for each blockling type.
     */
    public static void init()
    {
        FOODS.clear();
        TYPES.forEach(blocklingType -> { blocklingType.spawnPredicates.clear(); blocklingType.foods.clear(); });

        GRASS.addFoods(Blocks.GRASS_BLOCK);
        GRASS.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        GRASS.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        GRASS.spawnPredicates.add((blockling, world) -> canSeeSky(blockling, world));

        DIRT.addFoods(Blocks.DIRT);
        DIRT.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        DIRT.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        DIRT.spawnPredicates.add((blockling, world) -> canSeeSky(blockling, world));

        OAK_LOG.addFoods(Blocks.OAK_LOG);
        OAK_LOG.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        OAK_LOG.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        OAK_LOG.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 8, Blocks.OAK_LOG));

        BIRCH_LOG.addFoods(Blocks.BIRCH_LOG);
        BIRCH_LOG.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        BIRCH_LOG.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        BIRCH_LOG.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 8, Blocks.BIRCH_LOG));

        SPRUCE_LOG.addFoods(Blocks.SPRUCE_LOG);
        SPRUCE_LOG.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        SPRUCE_LOG.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        SPRUCE_LOG.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 8, Blocks.SPRUCE_LOG));

        JUNGLE_LOG.addFoods(Blocks.JUNGLE_LOG);
        JUNGLE_LOG.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        JUNGLE_LOG.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        JUNGLE_LOG.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 8, Blocks.JUNGLE_LOG));

        DARK_OAK_LOG.addFoods(Blocks.DARK_OAK_LOG);
        DARK_OAK_LOG.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        DARK_OAK_LOG.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        DARK_OAK_LOG.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 8, Blocks.DARK_OAK_LOG));

        ACACIA_LOG.addFoods(Blocks.ACACIA_LOG);
        ACACIA_LOG.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        ACACIA_LOG.spawnPredicates.add((blockling, world) -> blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        ACACIA_LOG.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 8, Blocks.ACACIA_LOG));

        STONE.addFoods(Blocks.STONE);
        STONE.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        STONE.spawnPredicates.add((blockling, world) -> !blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        STONE.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 4, Blocks.STONE));

        IRON.addFoods(Items.IRON_INGOT);
        IRON.addFoods(Blocks.IRON_ORE, Blocks.IRON_BLOCK);
        IRON.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        IRON.spawnPredicates.add((blockling, world) -> !blockBelowIs(blockling, world, Blocks.GRASS_BLOCK));
        IRON.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 6, Blocks.IRON_ORE));

        QUARTZ.addFoods(Items.QUARTZ);
        QUARTZ.addFoods(Blocks.NETHER_QUARTZ_ORE, Blocks.QUARTZ_BLOCK);
        QUARTZ.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.NETHER));
        QUARTZ.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 8, Blocks.NETHER_QUARTZ_ORE));

        LAPIS.addFoods(Items.LAPIS_LAZULI);
        LAPIS.addFoods(Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK);
        LAPIS.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        LAPIS.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 12, Blocks.LAPIS_ORE));

        GOLD.addFoods(Items.GOLD_INGOT);
        GOLD.addFoods(Blocks.GOLD_ORE, Blocks.GOLD_BLOCK);
        GOLD.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        GOLD.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 12, Blocks.GOLD_ORE));

        EMERALD.addFoods(Items.EMERALD);
        EMERALD.addFoods(Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK);
        EMERALD.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        EMERALD.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 16, Blocks.EMERALD_ORE));

        DIAMOND.addFoods(Items.DIAMOND);
        DIAMOND.addFoods(Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK);
        DIAMOND.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        DIAMOND.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 12, Blocks.DIAMOND_ORE));

        NETHERITE.addFoods(Items.NETHERITE_SCRAP, Items.NETHERITE_INGOT);
        NETHERITE.addFoods(Blocks.ANCIENT_DEBRIS, Blocks.NETHERITE_BLOCK);
        NETHERITE.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.NETHER));
        NETHERITE.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 12, Blocks.ANCIENT_DEBRIS));

        OBSIDIAN.addFoods(Blocks.OBSIDIAN);
        OBSIDIAN.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.OVERWORLD));
        OBSIDIAN.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 16, Blocks.OBSIDIAN));

        GLOWSTONE.addFoods(Items.GLOWSTONE_DUST);
        GLOWSTONE.addFoods(Blocks.GLOWSTONE);
        GLOWSTONE.spawnPredicates.add((blockling, world) -> isInWorld(blockling, world, World.NETHER));
        GLOWSTONE.spawnPredicates.add((blockling, world) -> blockNearbyIs(blockling, world, 16, Blocks.GLOWSTONE));
    }

    /**
     * The list of all items that are treated as foods.
     */
    @Nonnull
    private static final Set<Item> FOODS = new HashSet<>();

    /**
     * The key used to identify the blockling type (for things like translation text components).
     */
    @Nonnull
    public final String key;

    /**
     * The standard texture for the blockling type.
     */
    @Nonnull
    public final ResourceLocation entityTexture;

    /**
     * The translation text component for the blockling type's name.
     */
    @Nonnull
    public final TranslationTextComponent name;

    /**
     * The chance a blockling can spawn with the current blockling type if all other conditions are met.
     */
    public final int spawnRateReduction;

    /**
     * The bonus max health the blockling type gives.
     */
    private float maxHealth = 0.0f;

    /**
     * The bonus attack damage the blockling type gives.
     */
    private float attackDamage = 0.0f;

    /**
     * The bonus attack speed the blockling type gives.
     */
    private float attackSpeed = 3.0f;

    /**
     * The bonus armour the blockling type gives.
     */
    private float armour = 0.0f;

    /**
     * The bonus armour toughness the blockling type gives.
     */
    private float armourToughness = 0.0f;

    /**
     * The bonus knockback resistance the blockling type gives.
     */
    private float knockbackResistance = 0.0f;

    /**
     * The bonus move speed the blockling type gives.
     */
    private float moveSpeed = 0.0f;

    /**
     * The bonus mining speed the blockling type gives.
     */
    private float miningSpeed = 0.0f;

    /**
     * The bonus woodcutting speed the blockling type gives.
     */
    private float woodcuttingSpeed = 0.0f;

    /**
     * The bonus farming speed the blockling type gives.
     */
    private float farmingSpeed = 0.0f;

    /**
     * The foods for the blockling type.
     */
    @Nonnull
    public final Set<Item> foods = new HashSet<>();

    /**
     * The list of predicates that need to be met for a blockling to spawn with for the blockling type.
     */
    @Nonnull
    public final List<BiPredicate<BlocklingEntity, IWorld>> spawnPredicates = new ArrayList<>();

    /**
     * @param key the key used to identify the blockling type (for things like translation text components).
     * @param spawnRateReduction the chance a blockling can spawn with the current blockling type if all other conditions are met.
     */
    public BlocklingType(@Nonnull String key, int spawnRateReduction)
    {
        this.key = key;
        this.entityTexture = new BlocklingsResourceLocation("textures/entity/blockling/blockling_" + key + ".png");
        this.name = new BlocklingsTranslationTextComponent("type." + key);
        this.spawnRateReduction = spawnRateReduction + 1;
    }

    /**
     * Creates and adds a new blockling type to the list of blockling types.
     *
     * @param key the key used to identify the blockling type (for things like translation text components).
     * @param spawnRateReduction the chance a blockling can spawn with the current blockling type if all other conditions are met.
     * @return the instance of the blockling type.
     */
    @Nonnull
    private static BlocklingType create(@Nonnull String key, int spawnRateReduction)
    {
        BlocklingType type = new BlocklingType(key, spawnRateReduction);
        TYPES.add(type);
        return type;
    }

    /**
     * @param key the key used to identify the blockling type.
     * @return the blockling type with the given key, or grass if it does not exist.
     */
    @Nonnull
    public static BlocklingType find(@Nonnull String key)
    {
        return TYPES.stream().filter(type -> type.key.equals(key)).findFirst().orElse(BlocklingType.GRASS);
    }

    /**
     * @param key the key used to identify the blockling type.
     * @param version the version of the mod to use the key for.
     * @return the blockling type with the given key, or grass if it does not exist.
     */
    @Nonnull
    public static BlocklingType find(@Nonnull String key, @Nonnull Version version)
    {
        return find(key);
    }

    /**
     * Sets the combat stats for the blockling type.
     *
     * @param maxHealth the bonus max health the blockling type gives.
     * @param attackDamage the bonus attack damage health the blockling type gives.
     * @param attackSpeed the bonus attack speed health the blockling type gives.
     * @param armour the bonus armour the blockling type gives.
     * @param armourToughness the bonus armour toughness health the blockling type gives.
     * @param knockbackResistance the bonus knockback resistance health the blockling type gives.
     * @param moveSpeed the bonus move speed the blockling type gives.
     */
    @Nonnull
    private BlocklingType addCombatStats(float maxHealth, float attackDamage, float attackSpeed, float armour, float armourToughness, float knockbackResistance, float moveSpeed)
    {
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.armour = armour;
        this.armourToughness = armourToughness;
        this.knockbackResistance = knockbackResistance;
        this.moveSpeed = moveSpeed;

        return this;
    }

    /**
     * Sets the gathering stats for the blockling type.
     *
     * @param miningSpeed the bonus mining speed the blockling type gives.
     * @param woodcuttingSpeed the bonus woodcutting speed the blockling type gives.
     * @param farmingSpeed the bonus farming speed the blockling type gives.
     */
    @Nonnull
    private BlocklingType addGatheringStats(float miningSpeed, float woodcuttingSpeed, float farmingSpeed)
    {
        this.miningSpeed = miningSpeed;
        this.woodcuttingSpeed = woodcuttingSpeed;
        this.farmingSpeed = farmingSpeed;

        return this;
    }

    /**
     * @param blocklingType the blockling type.
     * @param variant the variant.
     * @return the resource location for the combined texture (returns the regular type texture if the option is disabled in the config).
     */
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public ResourceLocation getCombinedTexture(@Nonnull BlocklingType blocklingType, int variant)
    {
        if (BlocklingsConfig.CLIENT.disableDirtyBlocklings.get())
        {
            return blocklingType.entityTexture;
        }

        return new BlocklingsResourceLocation("textures/entity/blockling/blockling_" + key + "_merged_with_" + blocklingType.key + "_" + variant);
    }

    /**
     * @return the bonus max health the blockling type gives.
     */
    public float getMaxHealth()
    {
        return maxHealth;
    }

    /**
     * @return the bonus attack damage the blockling type gives.
     */
    public float getAttackDamage()
    {
        return attackDamage;
    }

    /**
     * @return the bonus armour the blockling type gives.
     */
    public float getArmour()
    {
        return armour;
    }

    /**
     * @return the bonus move speed the blockling type gives.
     */
    public float getMoveSpeed()
    {
        return moveSpeed;
    }

    /**
     * @return the bonus attack speed the blockling type gives.
     */
    public float getAttackSpeed()
    {
        return attackSpeed;
    }

    /**
     * @return the bonus armour toughness the blockling type gives.
     */
    public float getArmourToughness()
    {
        return armourToughness;
    }

    /**
     * @return the bonus knockback resistance the blockling type gives.
     */
    public float getKnockbackResistance()
    {
        return knockbackResistance;
    }

    /**
     * @return the bonus mining speed the blockling type gives.
     */
    public float getMiningSpeed()
    {
        return miningSpeed;
    }

    /**
     * @return the bonus woodcutting speed the blockling type gives.
     */
    public float getWoodcuttingSpeed()
    {
        return woodcuttingSpeed;
    }

    /**
     * @return the bonus farming speed the blockling type gives.
     */
    public float getFarmingSpeed()
    {
        return farmingSpeed;
    }

    /**
     * @param stack the food stack.
     * @return the blockling type that eats the given food.
     */
    @Nonnull
    public static BlocklingType findTypeForFood(@Nonnull ItemStack stack)
    {
        return findTypeForFood(stack.getItem());
    }

    /**
     * @param item the food item.
     * @return the blockling type that eats the given food.
     */
    @Nonnull
    public static BlocklingType findTypeForFood(@Nonnull Item item)
    {
        return Objects.requireNonNull(TYPES.stream().filter(blocklingType -> blocklingType.isFoodForType(item)).findFirst().orElse(null));
    }

    /**
     * @param stack the stack to check.
     * @return true if the stack is food.
     */
    public static boolean isFood(@Nonnull ItemStack stack)
    {
        return isFood(stack.getItem());
    }

    /**
     * @param item the item to check.
     * @return true if the item is food.
     */
    public static boolean isFood(@Nonnull Item item)
    {
        return ItemUtil.isFlower(item) || FOODS.contains(item);
    }

    /**
     * @param stack the stack to check.
     * @return true if the stack is food for this blockling type.
     */
    public boolean isFoodForType(@Nonnull ItemStack stack)
    {
        return isFoodForType(stack.getItem());
    }

    /**
     * @param item the item to check.
     * @return true if the item is food for this blockling type.
     */
    public boolean isFoodForType(Item item)
    {
        return ItemUtil.isFlower(item) || foods.contains(item);
    }

    /**
     * Adds the given items as foods to blockling type.
     *
     * @param items the items to add as foods.
     */
    private void addFoods(@Nonnull Item... items)
    {
        FOODS.addAll(Arrays.asList(items));
        foods.addAll(Arrays.asList(items));
    }

    /**
     * Adds the given blocks as foods to blockling type.
     *
     * @param blocks the blocks to add as foods.
     */
    private void addFoods(@Nonnull Block... blocks)
    {
        FOODS.addAll(Arrays.stream(blocks).map(Item.BY_BLOCK::get).collect(Collectors.toList()));
        foods.addAll(Arrays.stream(blocks).map(Item.BY_BLOCK::get).collect(Collectors.toList()));
    }

    /**
     * @param blockling the blockling.
     * @param world the world the blockling is in.
     * @param worldTypes the dimensions to check for.
     * @return true if the blockling is one of the given dimensions.
     */
    @SafeVarargs
    private static boolean isInWorld(@Nonnull BlocklingEntity blockling, @Nonnull IWorld world, @Nonnull RegistryKey<World>... worldTypes)
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

    /**
     * @param blockling the blockling.
     * @param world the world the blockling is in.
     * @param biomes the biomes to check for.
     * @return true if the blockling is one of the given biomes.
     */
    @SafeVarargs
    private static boolean isInBiome(@Nonnull BlocklingEntity blockling, @Nonnull IWorld world, @Nonnull RegistryKey<Biome>... biomes)
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

    /**
     * @param blockling the blockling.
     * @param world the world the blockling is in.
     * @return true if the blockling can see the sky.
     */
    private static boolean canSeeSky(@Nonnull BlocklingEntity blockling, @Nonnull IWorld world)
    {
        return world.canSeeSky(blockling.blockPosition());
    }

    /**
     * @param blockling the blockling.
     * @param world the world the blockling is in.
     * @param blocks the blocks to check for.
     * @return true if the blockling is on one of the given blocks.
     */
    private static boolean blockBelowIs(@Nonnull BlocklingEntity blockling, @Nonnull IWorld world, @Nonnull Block... blocks)
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

    /**
     * @param blockling the blockling.
     * @param world the world the blockling is in.
     * @param radius the (cube) radius to search in.
     * @param blocks the blocks to check for.
     * @return true if the blockling is near one of the given blocks.
     */
    private static boolean blockNearbyIs(@Nonnull BlocklingEntity blockling, @Nonnull IWorld world, int radius, @Nonnull Block... blocks)
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
