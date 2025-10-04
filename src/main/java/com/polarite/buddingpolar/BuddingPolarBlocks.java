package com.polarite.buddingpolar;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.polarite.buddingpolar.blocks.BlockBuddingCertusQuartz;
import com.polarite.buddingpolar.blocks.BlockCertusQuartzCluster;

public class BuddingPolarBlocks {

    public static Block budding_certus_quartz_block;
    public static Block small_certus_quartz_bud;
    public static Block medium_certus_quartz_bud;
    public static Block large_certus_quartz_bud;
    public static Block certus_quartz_cluster;

    public static void init() {
        // Budding certus quartz block
        budding_certus_quartz_block = new BlockBuddingCertusQuartz();
        registerBlock(budding_certus_quartz_block);

        // Certus quartz cluster stages
        small_certus_quartz_bud = new BlockCertusQuartzCluster(0); // Small bud
        registerBlock(small_certus_quartz_bud);

        medium_certus_quartz_bud = new BlockCertusQuartzCluster(1); // Medium bud
        registerBlock(medium_certus_quartz_bud);

        large_certus_quartz_bud = new BlockCertusQuartzCluster(2); // Large bud
        registerBlock(large_certus_quartz_bud);

        certus_quartz_cluster = new BlockCertusQuartzCluster(3); // Full cluster
        registerBlock(certus_quartz_cluster);
    }

    private static void registerBlock(Block block) {
        ForgeRegistries.BLOCKS.register(block);
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        itemBlock.setTranslationKey(block.getTranslationKey());
        ForgeRegistries.ITEMS.register(itemBlock);
    }
}
