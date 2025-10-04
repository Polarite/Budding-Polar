package com.polarite.buddingpolar.sounds;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import com.polarite.buddingpolar.BuddingPolar;

public class BuddingPolarSounds {

    // Sound events
    public static SoundEvent BUDDING_BREAK;
    public static SoundEvent BUDDING_PLACE;
    public static SoundEvent BUDDING_STEP;
    public static SoundEvent BUDDING_SHIMMER;
    public static SoundEvent CLUSTER_BREAK;
    public static SoundEvent CLUSTER_PLACE;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();

        BUDDING_BREAK = registerSound(registry, "budding_certus_break");
        BUDDING_PLACE = registerSound(registry, "budding_certus_place");
        BUDDING_STEP = registerSound(registry, "budding_certus_step");
        BUDDING_SHIMMER = registerSound(registry, "budding_certus_shimmer");
        CLUSTER_BREAK = registerSound(registry, "cluster_certus_break");
        CLUSTER_PLACE = registerSound(registry, "cluster_certus_place");
    }

    private static SoundEvent registerSound(IForgeRegistry<SoundEvent> registry, String name) {
        ResourceLocation location = new ResourceLocation(BuddingPolar.MODID, name);
        SoundEvent event = new SoundEvent(location);
        event.setRegistryName(location);
        registry.register(event);
        return event;
    }

    /**
     * Plays a sound at the specified position
     */
    public static void playSound(World world, double x, double y, double z, SoundEvent sound, float volume, float pitch) {
        if (!world.isRemote) {
            world.playSound(null, x, y, z, sound, net.minecraft.util.SoundCategory.BLOCKS, volume, pitch);
        }
    }

    /**
     * Plays a random budding shimmer sound
     */
    public static void playShimmerSound(World world, double x, double y, double z) {
        playSound(world, x, y, z, BUDDING_SHIMMER, 0.5f, 0.8f + world.rand.nextFloat() * 0.4f);
    }
}
