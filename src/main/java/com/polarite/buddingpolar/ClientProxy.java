package com.polarite.buddingpolar;

import com.polarite.buddingpolar.renderer.BlockCertusQuartzClusterRenderer;
import com.polarite.buddingpolar.renderer.RenderIDs;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Client-side pre-initialization (renderers, textures, etc.)
    }

    @Override
    public void registerRenderers() {
        // Register custom block renderers
        RenderingRegistry.registerBlockHandler(RenderIDs.CERTUS_QUARTZ_CLUSTER, new BlockCertusQuartzClusterRenderer());
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
