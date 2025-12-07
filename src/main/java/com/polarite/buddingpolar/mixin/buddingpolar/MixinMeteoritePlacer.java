package com.polarite.buddingpolar.mixin.buddingpolar;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.polarite.buddingpolar.BuddingPolarBlocks;

import appeng.worldgen.MeteoritePlacer;
import appeng.worldgen.meteorite.IMeteoriteWorld;

/**
 * Mixin to modify meteorite generation to place a budding certus quartz block under the sky chest.
 * This injects at the end of spawnMeteoriteCenter() before returning true.
 */
@Mixin(value = MeteoritePlacer.class, remap = false)
public abstract class MixinMeteoritePlacer {

    @Shadow
    @Final
    private IMeteoriteWorld world;

    @Shadow
    @Final
    private int x;

    @Shadow
    @Final
    private int y;

    @Shadow
    @Final
    private int z;

    /**
     * Injects at RETURN of spawnMeteoriteCenter() to place budding certus quartz
     * under the sky chest when meteorite spawns successfully.
     */
    @Inject(method = "spawnMeteoriteCenter", at = @At("RETURN"), require = 1)
    private void onSpawnMeteoriteCenterReturn(CallbackInfoReturnable<Boolean> cir) {
        // Only modify if the meteorite actually spawned (returns true)
        if (cir.getReturnValue()) {
            // Replace the block under the sky chest with budding certus quartz
            world.setBlock(x, y - 1, z, BuddingPolarBlocks.budding_certus_quartz_block);
        }
    }
}
