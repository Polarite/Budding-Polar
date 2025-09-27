package com.polarite.buddingpolar;

import net.minecraft.block.Block;

import com.polarite.buddingpolar.blocks.BlockBuddingCertusQuartz;
import com.polarite.buddingpolar.blocks.BlockCertusQuartzCluster;

import cpw.mods.fml.common.registry.GameRegistry;

public class BuddingPolarBlocks {

    public static Block budding_certus_quartz_block;
    public static Block small_certus_quartz_bud;
    public static Block medium_certus_quartz_bud;
    public static Block large_certus_quartz_bud;
    public static Block certus_quartz_cluster;

    public static void init() {
        // Budding certus quartz block
        budding_certus_quartz_block = new BlockBuddingCertusQuartz();
        GameRegistry.registerBlock(budding_certus_quartz_block, "budding_certus_quartz_block");

        // Certus quartz cluster stages
        small_certus_quartz_bud = new BlockCertusQuartzCluster(0); // Small bud
        GameRegistry.registerBlock(small_certus_quartz_bud, "small_certus_quartz_bud");

        medium_certus_quartz_bud = new BlockCertusQuartzCluster(1); // Medium bud
        GameRegistry.registerBlock(medium_certus_quartz_bud, "medium_certus_quartz_bud");

        large_certus_quartz_bud = new BlockCertusQuartzCluster(2); // Large bud
        GameRegistry.registerBlock(large_certus_quartz_bud, "large_certus_quartz_bud");

        certus_quartz_cluster = new BlockCertusQuartzCluster(3); // Full cluster
        GameRegistry.registerBlock(certus_quartz_cluster, "certus_quartz_cluster");
    }
}
