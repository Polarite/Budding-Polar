package com.polarite.buddingpolar.mixin.buddingpolar;

import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.polarite.buddingpolar.BuddingPolarBlocks;

import appeng.worldgen.MeteoritePlacer;
import appeng.worldgen.meteorite.IMeteoriteWorld;

/**
 * Mixin for GTMEGA Applied Energistics 2 Unofficial fork.
 * This fork has a different implementation where the sky chest is placed directly
 * in placeMeteorite() method instead of having a separate spawnMeteoriteCenter() method.
 *
 * In GTMEGA AE2, the sky chest is placed at (x, y, z) inside placeMeteorite().
 * We inject at the end of the method to place a budding certus quartz block under the chest.
 */
@Mixin(value = MeteoritePlacer.class, remap = false, priority = 1001)
public abstract class MixinMeteoritePlacerGTMEGA {

    /**
     * Injects at the end (TAIL) of placeMeteorite() method to place budding certus quartz
     * under the sky chest. The sky chest is placed at (x, y, z) so we place our block at (x, y-1, z).
     *
     * placeMeteorite is only called when a meteorite successfully spawns, so this
     * will only run when a meteorite is actually being placed.
     */
    @Inject(method = "placeMeteorite", at = @At("TAIL"), require = 0)
    private void onPlaceMeteoriteEnd(IMeteoriteWorld w, int x, int y, int z, CallbackInfo ci) {
        // Place budding certus quartz block under the sky chest
        w.setBlock(x, y - 1, z, BuddingPolarBlocks.budding_certus_quartz_block);
    }

    /**
     * Injects at the end of spawnMeteorite(IMeteoriteWorld, NBTTagCompound) to handle
     * meteorites that are spawned from stored NBT data (when chunks are re-loaded).
     * This method extracts coordinates from the NBT and places the budding block.
     */
    @Inject(
        method = "spawnMeteorite(Lappeng/worldgen/meteorite/IMeteoriteWorld;Lnet/minecraft/nbt/NBTTagCompound;)Z",
        at = @At("TAIL"),
        require = 0)
    private void onSpawnMeteoriteFromNBT(IMeteoriteWorld w, NBTTagCompound meteoriteBlob,
        CallbackInfoReturnable<Boolean> cir) {
        int x = meteoriteBlob.getInteger("x");
        int y = meteoriteBlob.getInteger("y");
        int z = meteoriteBlob.getInteger("z");
        w.setBlock(x, y - 1, z, BuddingPolarBlocks.budding_certus_quartz_block);
    }

    /**
     * Injects at the end of spawnMeteorite(IMeteoriteWorld, int, int, int) to handle
     * newly generated meteorites (fresh worldgen).
     */
    @Inject(method = "spawnMeteorite(Lappeng/worldgen/meteorite/IMeteoriteWorld;III)Z", at = @At("TAIL"), require = 0)
    private void onSpawnMeteoriteNew(IMeteoriteWorld w, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        w.setBlock(x, y - 1, z, BuddingPolarBlocks.budding_certus_quartz_block);
    }
}
