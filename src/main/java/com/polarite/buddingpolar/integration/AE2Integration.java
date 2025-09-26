package com.polarite.buddingpolar.integration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Integration class for Applied Energistics 2 compatibility.
 * This adds Growth Accelerator support to our budding certus quartz system.
 */
public class AE2Integration {

    // AE2 Block references - using strings to avoid direct class dependencies
    public static Block growthAccelerator;

    // AE2 ItemMultiMaterial base item - used for both pure certus quartz and dust
    public static Item ae2MultiMaterial;

    // AE2 ItemMultiMaterial metadata values
    public static final int PURE_CERTUS_QUARTZ_META = 10; // Pure Certus Quartz Crystal
    public static final int CERTUS_QUARTZ_DUST_META = 2; // Certus Quartz Dust

    public static void init() {
        try {
            // Try to find AE2 Growth Accelerator block by its registered name
            growthAccelerator = GameRegistry.findBlock("appliedenergistics2", "tile.BlockQuartzGrowthAccelerator");

            // Find AE2 ItemMultiMaterial - this handles both pure certus quartz and dust
            ae2MultiMaterial = GameRegistry.findItem("appliedenergistics2", "item.ItemMultiMaterial");

            if (growthAccelerator != null) {
                // Growth accelerator found
            } else {
                // Growth Accelerator not found
            }

            if (ae2MultiMaterial != null) {
                // AE2 ItemMultiMaterial found
            } else {
                // AE2 ItemMultiMaterial not found
            }

        } catch (Exception e) {
            // Failed to initialize AE2 integration
        }
    }

    /**
     * Creates an ItemStack for AE2 Pure Certus Quartz Crystal.
     * Returns null if AE2 ItemMultiMaterial is not available.
     */
    public static ItemStack createPureCertusQuartzCrystal(int count) {
        if (ae2MultiMaterial == null) {
            return null;
        }
        ItemStack result = new ItemStack(ae2MultiMaterial, count, PURE_CERTUS_QUARTZ_META);
        return result;
    }

    /**
     * Creates an ItemStack for AE2 Certus Quartz Dust.
     * Returns null if AE2 ItemMultiMaterial is not available.
     */
    public static ItemStack createCertusQuartzDust(int count) {
        if (ae2MultiMaterial == null) {
            return null;
        }
        ItemStack result = new ItemStack(ae2MultiMaterial, count, CERTUS_QUARTZ_DUST_META);
        return result;
    }

    /**
     * Gets the appropriate AE2 ItemStack for cluster drops based on cluster stage.
     * Returns null if AE2 items aren't available.
     */
    public static ItemStack getCertusQuartzItemStackForStage(int stage, int count) {
        switch (stage) {
            case 3: // Full grown cluster - drops pure certus quartz
                return createPureCertusQuartzCrystal(count);
            case 0: // Small cluster - drops dust
            case 1: // Medium cluster - drops dust
            case 2: // Large cluster - drops dust
                return createCertusQuartzDust(count);
            default:
                return null;
        }
    }

    /**
     * Checks if we have access to AE2 items for drops.
     */
    public static boolean hasAE2Items() {
        return ae2MultiMaterial != null;
    }

    /**
     * Checks if a block is an AE2 growth accelerator
     */
    public static boolean isGrowthAccelerator(Block block) {
        return growthAccelerator != null && block == growthAccelerator;
    }

    /**
     * Counts the number of active (powered) Growth Accelerators adjacent to the given position
     * 
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return Number of adjacent active Growth Accelerators (0-6)
     */
    public static int countAdjacentAccelerators(World world, int x, int y, int z) {
        if (growthAccelerator == null) {
            return 0;
        }

        int count = 0;

        // Check all 6 adjacent faces
        if (isActiveGrowthAccelerator(world, x + 1, y, z)) count++;
        if (isActiveGrowthAccelerator(world, x - 1, y, z)) count++;
        if (isActiveGrowthAccelerator(world, x, y + 1, z)) count++;
        if (isActiveGrowthAccelerator(world, x, y - 1, z)) count++;
        if (isActiveGrowthAccelerator(world, x, y, z + 1)) count++;
        if (isActiveGrowthAccelerator(world, x, y, z - 1)) count++;

        return count;
    }

    /**
     * Checks if the block at the given position is an active (powered) Growth Accelerator
     * Uses reflection to avoid hard dependencies on AE2 classes
     * 
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @return true if it's a powered Growth Accelerator, false otherwise
     */
    private static boolean isActiveGrowthAccelerator(World world, int x, int y, int z) {
        // First check if it's a growth accelerator block
        if (world.getBlock(x, y, z) != growthAccelerator) {
            return false;
        }

        try {
            // Get the tile entity at this position
            net.minecraft.tileentity.TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity == null) {
                return false;
            }

            // Use reflection to check if it implements ICrystalGrowthAccelerator and is powered
            Class<?> tileClass = tileEntity.getClass();

            // Look for the isPowered method
            java.lang.reflect.Method isPoweredMethod = null;
            try {
                isPoweredMethod = tileClass.getMethod("isPowered");
            } catch (NoSuchMethodException e) {
                // If isPowered method doesn't exist, just assume it's active
                // This provides backward compatibility if AE2 changes its API
                return true;
            }

            // Call the isPowered method
            Object result = isPoweredMethod.invoke(tileEntity);
            return result instanceof Boolean && (Boolean) result;

        } catch (Exception e) {
            // If reflection fails, assume the accelerator is not active
            // This is safer than crashing
            return false;
        }
    }

    /**
     * Calculates the number of additional growth attempts from Growth Accelerators
     * In official AE2, each Growth Accelerator adds 1 additional random tick attempt
     * 
     * @param acceleratorCount Number of adjacent powered accelerators
     * @return Number of additional growth attempts (0-6)
     */
    public static int calculateAdditionalGrowthAttempts(int acceleratorCount) {
        return acceleratorCount;
    }
}
