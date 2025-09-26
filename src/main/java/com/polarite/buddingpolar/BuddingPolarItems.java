package com.polarite.buddingpolar;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.polarite.buddingpolar.integration.AE2Integration;

public class BuddingPolarItems {

    // These now reference AE2's base ItemMultiMaterial
    public static Item certus_quartz_crystal;
    public static Item certus_quartz_dust;

    public static void init() {
        // Items will be set later in postInit when AE2Integration is initialized
        // This ensures AE2 is fully loaded before we try to access its items
    }

    /**
     * Called after AE2Integration.init() to set up the AE2 item references
     */
    public static void postInit() {
        // Both items now reference the same AE2 ItemMultiMaterial base item
        // The actual item type is determined by metadata when creating ItemStacks
        certus_quartz_crystal = AE2Integration.ae2MultiMaterial;
        certus_quartz_dust = AE2Integration.ae2MultiMaterial;

        // Initialize AE2 item references
        if (AE2Integration.hasAE2Items()) {
            // AE2 ItemMultiMaterial found
        } else {
            // AE2 ItemMultiMaterial not found
        }
    }

    /**
     * Creates an ItemStack for the appropriate AE2 item based on cluster type
     */
    public static ItemStack createItemStackForCluster(int clusterType, int count) {
        return AE2Integration.getCertusQuartzItemStackForStage(clusterType, count);
    }
}
