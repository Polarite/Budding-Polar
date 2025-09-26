package com.polarite.buddingpolar.worldgen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkEvent;

import com.polarite.buddingpolar.BuddingPolarBlocks;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles meteorite generation modifications to place budding certus quartz blocks
 * under sky chests when meteorites are generated.
 */
public class MeteoriteWorldHandler {

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.world.isRemote) {
            return; // Only process on server side
        }

        // Check newly loaded chunks for sky chests (indicating meteorites)
        scanChunkForMeteorites(event.world, event.getChunk().xPosition, event.getChunk().zPosition);
    }

    private void scanChunkForMeteorites(World world, int chunkX, int chunkZ) {
        // Scan the chunk for sky chests
        for (int x = chunkX * 16; x < (chunkX + 1) * 16; x++) {
            for (int z = chunkZ * 16; z < (chunkZ + 1) * 16; z++) {
                for (int y = 10; y < world.getActualHeight(); y++) {
                    Block block = world.getBlock(x, y, z);

                    // Check if this is an AE2 sky chest
                    if (isSkyChest(block)) {
                        processMeteorite(world, x, y, z);
                    }
                }
            }
        }
    }

    private boolean isSkyChest(Block block) {
        if (block == null) {
            return false;
        }

        // Check by class name since we can't directly import AE2 classes
        String blockName = block.getClass()
            .getSimpleName();
        String registryName = block.getUnlocalizedName();

        return blockName.contains("SkyChest") || registryName.contains("BlockSkyChest")
            || registryName.contains("skyChest");
    }

    private void processMeteorite(World world, int x, int y, int z) {
        // Check if there's already a budding certus quartz block underneath
        Block belowBlock = world.getBlock(x, y - 1, z);

        if (belowBlock == BuddingPolarBlocks.budding_certus_quartz_block) {
            return; // Already processed this meteorite
        }

        // Verify this looks like a meteorite by checking for sky stone nearby
        if (!isNearSkyStone(world, x, y, z)) {
            return; // Probably not a meteorite
        }

        // Find the lowest solid block under the sky chest to place budding certus quartz
        int targetY = findBestPlacementY(world, x, y, z);

        if (targetY > 0) {
            // Replace the block directly under the sky chest with budding certus quartz
            world.setBlock(x, targetY, z, BuddingPolarBlocks.budding_certus_quartz_block, 0, 3);

            System.out.println(
                "[Budding Polar] Placed budding certus quartz block under meteorite sky chest at " + x
                    + ", "
                    + targetY
                    + ", "
                    + z);
        }
    }

    private boolean isNearSkyStone(World world, int x, int y, int z) {
        // Check for sky stone blocks in a small radius to confirm this is a meteorite
        int skyStoneCount = 0;

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    Block block = world.getBlock(x + dx, y + dy, z + dz);
                    String registryName = block.getUnlocalizedName();

                    if (registryName.contains("skystone") || registryName.contains("SkyStone")) {
                        skyStoneCount++;
                        if (skyStoneCount >= 3) {
                            return true; // Found enough sky stone to confirm meteorite
                        }
                    }
                }
            }
        }

        return false;
    }

    private int findBestPlacementY(World world, int x, int y, int z) {
        // Start from directly under the sky chest and find the first solid block
        for (int checkY = y - 1; checkY > 5; checkY--) {
            Block block = world.getBlock(x, checkY, z);

            // Skip air and liquid blocks
            if (block == Blocks.air || block == Blocks.water
                || block == Blocks.lava
                || block == Blocks.flowing_water
                || block == Blocks.flowing_lava) {
                continue;
            }

            // Found a solid block - this is where we'll place the budding certus quartz
            return checkY;
        }

        return -1; // No suitable location found
    }
}
