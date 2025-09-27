package com.polarite.buddingpolar;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.polarite.buddingpolar.config.BuddingPolarConfig;
import com.polarite.buddingpolar.worldgen.MeteoriteWorldHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = BuddingPolar.MODID,
    version = BuddingPolar.VERSION,
    name = BuddingPolar.NAME,
    guiFactory = "com.polarite.buddingpolar.gui.GuiFactoryBuddingPolar")
public class BuddingPolar {

    public static final String MODID = "buddingpolar";
    public static final String VERSION = "0.1.0";
    public static final String NAME = "Budding Polar";

    @SidedProxy(
        clientSide = "com.polarite.buddingpolar.ClientProxy",
        serverSide = "com.polarite.buddingpolar.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static BuddingPolar instance;

    public static BuddingPolarConfig config;

    public static final CreativeTabs creativeTabs = new CreativeTabs("buddingpolar") {

        @Override
        public Item getTabIconItem() {
            return BuddingPolarItems.certus_quartz_crystal != null ? BuddingPolarItems.certus_quartz_crystal
                : Items.diamond;
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "buddingpolar.cfg");
        Configuration configuration = new Configuration(configFile);
        config = new BuddingPolarConfig(configuration);

        BuddingPolarBlocks.init();
        BuddingPolarItems.init();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        proxy.registerRenderers();

        // Register meteorite world handler for placing budding certus quartz blocks
        MinecraftForge.EVENT_BUS.register(new MeteoriteWorldHandler());
        FMLCommonHandler.instance()
            .bus()
            .register(new MeteoriteWorldHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);

        // Initialize AE2 integration if AE2 is present
        try {
            Class.forName("appeng.api.AEApi");
            com.polarite.buddingpolar.integration.AE2Integration.init();

            // Set up AE2 item references after AE2 integration is initialized
            BuddingPolarItems.postInit();
        } catch (ClassNotFoundException e) {
            // AE2 not present, Budding Polar will not function properly
            System.err
                .println("[Budding Polar] Applied Energistics 2 not found! Budding Polar will not function properly.");
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // Register debug command
        event.registerServerCommand(new com.polarite.buddingpolar.commands.CommandCertusQuartzDebug());
    }

    public static String find(String name) {
        return MODID + ":" + name;
    }
}
