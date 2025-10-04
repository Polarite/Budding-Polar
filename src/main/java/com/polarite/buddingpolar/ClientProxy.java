package com.polarite.buddingpolar;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// Reflection imports
import java.lang.reflect.Method;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import com.polarite.buddingpolar.client.renderer.TileEntityCertusQuartzClusterRenderer;
import com.polarite.buddingpolar.tileentity.TileEntityCertusQuartzCluster;
/**
 * Client proxy: register client-only render layers in a forward-compatible way.
 */

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Client-side pre-initialization (renderers, textures, etc.)
        registerItemModels();
    }

    @Override
    public void registerRenderers() {
        // Register custom block renderers / render layers for transparent blocks.
        registerRenderLayers();
    }

    private void registerItemModels() {
        // Register item models for block items (so inventory/GUI shows correct textures)
        // This must be called during preInit
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BuddingPolarBlocks.budding_certus_quartz_block), 0,
                new ModelResourceLocation(BuddingPolar.find("budding_certus_quartz_block"), "inventory"));
        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BuddingPolarBlocks.small_certus_quartz_bud), 0,
                new ModelResourceLocation(BuddingPolar.find("small_certus_quartz_bud"), "inventory"));
        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BuddingPolarBlocks.medium_certus_quartz_bud), 0,
                new ModelResourceLocation(BuddingPolar.find("medium_certus_quartz_bud"), "inventory"));
        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BuddingPolarBlocks.large_certus_quartz_bud), 0,
                new ModelResourceLocation(BuddingPolar.find("large_certus_quartz_bud"), "inventory"));
        
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BuddingPolarBlocks.certus_quartz_cluster), 0,
                new ModelResourceLocation(BuddingPolar.find("certus_quartz_cluster"), "inventory"));
    }

    private void registerRenderLayers() {
        // Prefer modern RenderTypeLookup.setRenderLayer if available (newer Forge),
        // otherwise fall back to MinecraftForgeClient.setRenderLayer used in 1.12.
        try {
            // Try to locate RenderTypeLookup (1.14+) and set render layer via reflection
            Class<?> rtl = Class.forName("net.minecraft.client.renderer.RenderTypeLookup");
            Method setMethod = rtl.getMethod("setRenderLayer", Block.class, Class.forName("net.minecraft.client.renderer.RenderType"));
            // If found, we need to get the RenderType.CUTOUT field
            Class<?> renderTypeClass = Class.forName("net.minecraft.client.renderer.RenderType");
            java.lang.reflect.Field cutoutField = renderTypeClass.getField("cutout");
            Object cutout = cutoutField.get(null);
            // Set our cluster block to cutout
            Block cluster = (Block) Class.forName("com.polarite.buddingpolar.BuddingPolarBlocks").getField("certus_quartz_cluster").get(null);
            setMethod.invoke(null, cluster, cutout);
        } catch (Throwable t) {
            // Fallback for 1.12: MinecraftForgeClient.setRenderLayer(Block, BlockRenderLayer)
            try {
                Class<?> mfc = Class.forName("net.minecraftforge.client.MinecraftForgeClient");
                Method setMethod = mfc.getMethod("setRenderLayer", Block.class, BlockRenderLayer.class);
                Block cluster = (Block) Class.forName("com.polarite.buddingpolar.BuddingPolarBlocks").getField("certus_quartz_cluster").get(null);
                setMethod.invoke(null, cluster, BlockRenderLayer.CUTOUT);
            } catch (Throwable t2) {
                // As a last resort the block's getBlockLayer override will be used.
                System.err.println("[BuddingPolar] Failed to programmatically set render layer: " + t2.getMessage());
            }
        }
    }    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // Client-side initialization
        
        // Register TESR for cluster rendering
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCertusQuartzCluster.class, 
            new TileEntityCertusQuartzClusterRenderer());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        // Client-side post-initialization
    }
}
