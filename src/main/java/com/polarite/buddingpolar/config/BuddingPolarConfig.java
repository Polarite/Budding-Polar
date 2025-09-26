package com.polarite.buddingpolar.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class BuddingPolarConfig {

    public Configuration config;

    public static boolean requireSilkTouchForBuddingBlock = false;
    public static boolean requireSilkTouchForClusters = false;

    public static int growthChanceWithAccelerators = 5;
    public static int growthChanceWithoutAccelerators = 25;

    // Categories
    private static final String CATEGORY_MINING = "mining";
    private static final String CATEGORY_GROWTH = "growth";

    public BuddingPolarConfig(Configuration config) {
        this.config = config;
        loadConfiguration();
    }

    private void loadConfiguration() {
        try {
            config.load();

            requireSilkTouchForBuddingBlock = config.getBoolean(
                "requireSilkTouchForBuddingBlock",
                CATEGORY_MINING,
                false,
                "If true, budding certus quartz blocks can only be mined with silk touch enchantment. "
                    + "If false, they can be mined with any pickaxe.");

            requireSilkTouchForClusters = config.getBoolean(
                "requireSilkTouchForClusters",
                CATEGORY_MINING,
                false,
                "If true, silk touch will drop the cluster block itself instead of crystals. "
                    + "If false, clusters always drop crystals when mined with any pickaxe.");

            Property growthWithAcceleratorsProperty = config.get(
                CATEGORY_GROWTH,
                "growthChanceWithAccelerators",
                5,
                "Growth chance denominator with accelerators (1 in X chance per tick). "
                    + "Lower values = faster growth. Default: 5 (20% chance per tick)");
            growthWithAcceleratorsProperty.setMinValue(1);
            growthWithAcceleratorsProperty.setMaxValue(100);
            growthChanceWithAccelerators = growthWithAcceleratorsProperty.getInt();

            Property growthWithoutAcceleratorsProperty = config.get(
                CATEGORY_GROWTH,
                "growthChanceWithoutAccelerators",
                25,
                "Growth chance denominator without accelerators (1 in X chance per tick). "
                    + "Lower values = faster growth. Default: 25 (4% chance per tick)");
            growthWithoutAcceleratorsProperty.setMinValue(1);
            growthWithoutAcceleratorsProperty.setMaxValue(100);
            growthChanceWithoutAccelerators = growthWithoutAcceleratorsProperty.getInt();

            validateConfiguration();

        } catch (Exception e) {
            System.err.println("[Budding Polar] Error loading configuration: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    private void validateConfiguration() {
        if (growthChanceWithAccelerators < 1) {
            growthChanceWithAccelerators = 1;
        }

        if (growthChanceWithoutAccelerators < 1) {
            growthChanceWithoutAccelerators = 1;
        }
    }

    public void save() {
        if (config.hasChanged()) {
            config.save();
        }
    }

    public void syncConfig() {
        // Re-load configuration values after GUI changes
        requireSilkTouchForBuddingBlock = config.getBoolean(
            "requireSilkTouchForBuddingBlock",
            CATEGORY_MINING,
            false,
            "Require silk touch enchantment to harvest budding certus quartz blocks");

        requireSilkTouchForClusters = config.getBoolean(
            "requireSilkTouchForClusters",
            CATEGORY_MINING,
            false,
            "When enabled, clusters drop the cluster block itself (not crystals) when harvested with silk touch");

        growthChanceWithAccelerators = config.getInt(
            "growthChanceWithAccelerators",
            CATEGORY_GROWTH,
            5,
            1,
            100,
            "Growth chance denominator with accelerators (1 in X chance per tick). Lower values = faster growth");

        growthChanceWithoutAccelerators = config.getInt(
            "growthChanceWithoutAccelerators",
            CATEGORY_GROWTH,
            25,
            1,
            100,
            "Growth chance denominator without accelerators (1 in X chance per tick). Lower values = faster growth");

        validateConfiguration();
        save();
    }

    // Utility methods for easy access
    public static boolean isSilkTouchRequiredForBuddingBlocks() {
        return requireSilkTouchForBuddingBlock;
    }

    public static boolean isSilkTouchRequiredForClusters() {
        return requireSilkTouchForClusters;
    }

    public static int getGrowthChanceWithAccelerators() {
        return growthChanceWithAccelerators;
    }

    public static int getGrowthChanceWithoutAccelerators() {
        return growthChanceWithoutAccelerators;
    }
}
