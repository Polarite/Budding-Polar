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
    }

    /**
     * Called after AE2Integration.init() to set up the AE2 item references
     */
    public static void postInit() {
        certus_quartz_crystal = AE2Integration.ae2MultiMaterial;
        certus_quartz_dust = AE2Integration.ae2MultiMaterial;
        if (AE2Integration.hasAE2Items()) {
            // AE2 items successfully found
        } else {
            System.err.println("[Budding Polar] Failed to find AE2! Budding Polar will not function properly.");
        }
    }

    /**
     * Creates an ItemStack for the appropriate AE2 item based on cluster type
     */
    public static ItemStack createItemStackForCluster(int clusterType, int count) {
        return AE2Integration.getCertusQuartzItemStackForStage(clusterType, count);
    }
}
