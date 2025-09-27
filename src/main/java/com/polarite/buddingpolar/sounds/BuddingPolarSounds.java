package com.polarite.buddingpolar.sounds;

import net.minecraft.world.World;

public class BuddingPolarSounds {

    // Sound event names
    public static final String BUDDING_BREAK = "buddingpolar:budding_certus_break";
    public static final String BUDDING_PLACE = "buddingpolar:budding_certus_place";
    public static final String BUDDING_STEP = "buddingpolar:budding_certus_step";
    public static final String BUDDING_SHIMMER = "buddingpolar:budding_certus_shimmer";

    public static final String CLUSTER_BREAK = "buddingpolar:cluster_certus_break";
    public static final String CLUSTER_PLACE = "buddingpolar:cluster_certus_place";

    /**
     * Plays a sound at the specified position
     */
    public static void playSound(World world, int x, int y, int z, String soundName, float volume, float pitch) {
        if (!world.isRemote) {
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, soundName, volume, pitch);
        }
    }

    /**
     * Plays a random budding shimmer sound
     */
    public static void playShimmerSound(World world, int x, int y, int z) {
        playSound(world, x, y, z, BUDDING_SHIMMER, 0.5f, 0.8f + world.rand.nextFloat() * 0.4f);
    }
}
