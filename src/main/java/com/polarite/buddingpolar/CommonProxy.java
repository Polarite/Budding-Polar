package com.polarite.buddingpolar;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        // Server and client common pre-initialization
    }

    public void init(FMLInitializationEvent event) {
        // Server and client common initialization
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Server and client common post-initialization
    }

    public void registerRenderers() {
        // Server-side does nothing for renderers
    }
}
