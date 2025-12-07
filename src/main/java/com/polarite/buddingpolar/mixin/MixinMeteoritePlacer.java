package com.polarite.buddingpolar.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.worldgen.MeteoritePlacer;
import appeng.worldgen.meteorite.IMeteoriteWorld;

import com.polarite.buddingpolar.BuddingPolarBlocks;

/**
 * Mixin to inject into MeteoritePlacer.placeMeteorite() to place a budding certus quartz block
 * under the sky chest when a meteorite is generated.
 */
@Mixin(value = MeteoritePlacer.class, remap = false)
public class MixinMeteoritePlacer {

    /**
     * Inject at the end of placeMeteorite to place a block under the sky chest.
     * The sky chest is placed at (x, y, z), so we place our block at (x, y-1, z).
     */
    @Inject(method = "placeMeteorite", at = @At("TAIL"))
    private void onPlaceMeteoriteEnd(IMeteoriteWorld w, int x, int y, int z, CallbackInfo ci) {
        // Place a budding certus quartz block under the sky stone chest position
        // The sky chest is at (x, y, z), so we replace the block at (x, y-1, z)
        w.setBlock(x, y - 1, z, BuddingPolarBlocks.budding_certus_quartz_block);
    }
}
