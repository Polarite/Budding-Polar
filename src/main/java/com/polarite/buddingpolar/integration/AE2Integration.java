package com.polarite.buddingpolar.integration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

// Optional AE2 API imports are used when AE2 is available at compile/runtime
// We will also perform registry-based fallbacks if the API isn't present or fails
// We avoid a hard compile-time dependency on AE2 API methods which may differ in forks.

/**
 * Integration class for Applied Energistics 2 compatibility.
 * This adds Growth Accelerator support to our budding certus quartz system.
 */
public class AE2Integration {

    // AE2 Block references
    public static Block growthAccelerator;

    // AE2 Items
    public static Item ae2Material; // The base AE2 material item
    public static Item certusQuartzCrystal;
    public static Item pureCertusQuartzCrystal;
    public static Item certusQuartzDust;

    public static void init() {
        try {
            // Try to find AE2 Growth Accelerator block
            growthAccelerator = ForgeRegistries.BLOCKS.getValue(
                new net.minecraft.util.ResourceLocation("appliedenergistics2", "quartz_growth_accelerator"));

            // Find the AE2 material item (used with metadata for different materials)
            ae2Material = ForgeRegistries.ITEMS.getValue(
                new net.minecraft.util.ResourceLocation("appliedenergistics2", "material"));

            // We'll use registry lookups for AE2 items/blocks. This avoids hard compile-time
            // dependencies on AE2 API types which may differ in forks.

            // Fallback: attempt to find items/blocks by common resource names used in AE2 forks
            if (growthAccelerator == null) {
                String[] accelNames = new String[] {"quartz_growth_accelerator", "growth_accelerator", "growthaccelerator", "accelerator"};
                for (String name : accelNames) {
                    Block b = ForgeRegistries.BLOCKS.getValue(new net.minecraft.util.ResourceLocation("appliedenergistics2", name));
                    if (b != null) {
                        growthAccelerator = b;
                        break;
                    }
                }
            }

            // If we have the material item, use it for both dust and pure crystal
            if (ae2Material != null) {
                pureCertusQuartzCrystal = ae2Material; // metadata 10
                certusQuartzDust = ae2Material; // metadata 2
            } else {
                // Fallback to trying individual item names if material item not found
                if (pureCertusQuartzCrystal == null) {
                    String[] pureNames = new String[] {"pure_certus_quartz_crystal", "certus_quartz_crystal_pure", "pure_certus_crystal", "pure_certus_quartz" , "pure_certus_quartz_crystal"};
                    for (String name : pureNames) {
                        Item it = ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("appliedenergistics2", name));
                        if (it != null) {
                            pureCertusQuartzCrystal = it;
                            break;
                        }
                    }
                }

                if (certusQuartzDust == null) {
                    String[] dustNames = new String[] {"certus_quartz_dust", "certus_dust", "certusquartzdust"};
                    for (String name : dustNames) {
                        Item it = ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("appliedenergistics2", name));
                        if (it != null) {
                            certusQuartzDust = it;
                            break;
                        }
                    }
                }
            }

            if (certusQuartzCrystal == null) {
                String[] crystalNames = new String[] {"certus_quartz_crystal", "certus_crystal", "certus_quartz"};
                for (String name : crystalNames) {
                    Item it = ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation("appliedenergistics2", name));
                    if (it != null) {
                        certusQuartzCrystal = it;
                        break;
                    }
                }
            }

            if (growthAccelerator != null) {
                // Growth accelerator found
            } else {
                // Growth Accelerator not found
            }

        } catch (Exception e) {
            // Failed to initialize AE2 integration
            e.printStackTrace();
        }
    }

    /**
     * Creates an ItemStack for AE2 Pure Certus Quartz Crystal.
     * Returns empty ItemStack if AE2 item is not available.
     */
    public static ItemStack createPureCertusQuartzCrystal(int count) {
        if (pureCertusQuartzCrystal == null) {
            return ItemStack.EMPTY;
        }
        
        // If using the new metadata-based system, use metadata 10 for pure crystal
        if (pureCertusQuartzCrystal == ae2Material && ae2Material != null) {
            return new ItemStack(ae2Material, count, 10);
        }
        
        return new ItemStack(pureCertusQuartzCrystal, count);
    }

    /**
     * Creates an ItemStack for AE2 Certus Quartz Dust.
     * Returns empty ItemStack if AE2 item is not available.
     */
    public static ItemStack createCertusQuartzDust(int count) {
        if (certusQuartzDust == null) {
            return ItemStack.EMPTY;
        }
        
        // If using the new metadata-based system, use metadata 2 for dust
        if (certusQuartzDust == ae2Material && ae2Material != null) {
            return new ItemStack(ae2Material, count, 2);
        }
        
        return new ItemStack(certusQuartzDust, count);
    }

    /**
     * Gets the appropriate AE2 ItemStack for cluster drops based on cluster stage.
     * Returns empty ItemStack if AE2 items aren't available.
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
                return ItemStack.EMPTY;
        }
    }

    /**
     * Checks if we have access to AE2 items for drops.
     */
    public static boolean hasAE2Items() {
        return certusQuartzDust != null && pureCertusQuartzCrystal != null;
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
     * @param pos   Block position
     * @return Number of adjacent active Growth Accelerators (0-6)
     */
    public static int countAdjacentAccelerators(World world, BlockPos pos) {
        if (growthAccelerator == null) {
            return 0;
        }

        int count = 0;

        // Check all 6 adjacent faces
        if (isActiveGrowthAccelerator(world, pos.east())) count++;
        if (isActiveGrowthAccelerator(world, pos.west())) count++;
        if (isActiveGrowthAccelerator(world, pos.up())) count++;
        if (isActiveGrowthAccelerator(world, pos.down())) count++;
        if (isActiveGrowthAccelerator(world, pos.north())) count++;
        if (isActiveGrowthAccelerator(world, pos.south())) count++;

        return count;
    }

    /**
     * Checks if the block at the given position is an active (powered) Growth Accelerator
     * Uses reflection to avoid hard dependencies on AE2 classes
     * 
     * @param world The world
     * @param pos   Block position
     * @return true if it's a powered Growth Accelerator, false otherwise
     */
    private static boolean isActiveGrowthAccelerator(World world, BlockPos pos) {
        // First check if it's a growth accelerator block
        if (world.getBlockState(pos).getBlock() != growthAccelerator) {
            return false;
        }

        try {
            // Get the tile entity at this position
            net.minecraft.tileentity.TileEntity tileEntity = world.getTileEntity(pos);
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
