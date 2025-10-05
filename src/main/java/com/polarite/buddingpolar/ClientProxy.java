package com.polarite.buddingpolar;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Client-side pre-initialization (renderers, textures, etc.)
        registerItemRenderers();
    }

    @Override
    public void registerRenderers() {
        // All blocks now use JSON model system
        // Cluster blocks use cross model with directional blockstates
        // Render layer (CUTOUT) is handled by getBlockLayer() in each block class
    }

    private void registerItemRenderers() {
        // Register item model for budding block
        registerItemRenderer(Item.getItemFromBlock(BuddingPolarBlocks.budding_certus_quartz_block));
        
        // Register item models for cluster blocks
        registerItemRenderer(Item.getItemFromBlock(BuddingPolarBlocks.small_certus_quartz_bud));
        registerItemRenderer(Item.getItemFromBlock(BuddingPolarBlocks.medium_certus_quartz_bud));
        registerItemRenderer(Item.getItemFromBlock(BuddingPolarBlocks.large_certus_quartz_bud));
        registerItemRenderer(Item.getItemFromBlock(BuddingPolarBlocks.certus_quartz_cluster));
    }

    private void registerItemRenderer(Item item) {
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(item, 0, 
                new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // Client-side initialization
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        // Client-side post-initialization
    }
}
