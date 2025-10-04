package com.polarite.buddingpolar;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.polarite.buddingpolar.config.BuddingPolarConfig;
import com.polarite.buddingpolar.sounds.BuddingPolarSounds;
import com.polarite.buddingpolar.worldgen.MeteoriteWorldHandler;
import com.polarite.buddingpolar.tileentity.TileEntityCertusQuartzCluster;

@Mod(
    modid = BuddingPolar.MODID,
    version = BuddingPolar.VERSION,
    name = BuddingPolar.NAME,
    dependencies = "required-after:appliedenergistics2")
public class BuddingPolar {

    public static final String MODID = "buddingpolar";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "Budding Polar";

    @SidedProxy(
        clientSide = "com.polarite.buddingpolar.ClientProxy",
        serverSide = "com.polarite.buddingpolar.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
    public static BuddingPolar instance;

    public static BuddingPolarConfig config;

    public static final CreativeTabs creativeTabs = new CreativeTabs(MODID) {

        @Override
        @SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
        public ItemStack createIcon() {
            return BuddingPolarItems.certus_quartz_crystal != null ? new ItemStack(BuddingPolarItems.certus_quartz_crystal)
                : new ItemStack(Items.DIAMOND);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "buddingpolar.cfg");
        Configuration configuration = new Configuration(configFile);
        config = new BuddingPolarConfig(configuration);

        // Register sound events
        MinecraftForge.EVENT_BUS.register(BuddingPolarSounds.class);

        BuddingPolarBlocks.init();
        BuddingPolarItems.init();
        
        // Register tile entities
        GameRegistry.registerTileEntity(TileEntityCertusQuartzCluster.class, 
            new net.minecraft.util.ResourceLocation(MODID, "certus_quartz_cluster"));
        
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        proxy.registerRenderers();

        // Register meteorite world handler for placing budding certus quartz blocks
        MinecraftForge.EVENT_BUS.register(new MeteoriteWorldHandler());
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
