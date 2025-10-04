package com.polarite.buddingpolar.worldgen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.polarite.buddingpolar.BuddingPolarBlocks;

/**
 * Handles meteorite generation modifications to place budding certus quartz blocks
 * under sky chests when meteorites are generated.
 */
public class MeteoriteWorldHandler {

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld().isRemote) {
            return; // Only process on server side
        }

        // Check newly loaded chunks for sky chests (indicating meteorites)
        scanChunkForMeteorites(event.getWorld(), event.getChunk());
    }

    private void scanChunkForMeteorites(World world, Chunk chunk) {
        int chunkX = chunk.x;
        int chunkZ = chunk.z;
        
        // Scan the chunk for sky chests
        for (int x = chunkX * 16; x < (chunkX + 1) * 16; x++) {
            for (int z = chunkZ * 16; z < (chunkZ + 1) * 16; z++) {
                for (int y = 10; y < world.getHeight(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    // Check if this is an AE2 sky chest
                    if (isSkyChest(block)) {
                        processMeteorite(world, pos);
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
        String blockName = block.getClass().getSimpleName();
        String registryName = block.getRegistryName() != null ? block.getRegistryName().toString() : "";

        return blockName.contains("SkyChest") || registryName.contains("sky_chest")
            || registryName.contains("skyChest");
    }

    private void processMeteorite(World world, BlockPos pos) {
        // Check if there's already a budding certus quartz block underneath
        Block belowBlock = world.getBlockState(pos.down()).getBlock();

        if (belowBlock == BuddingPolarBlocks.budding_certus_quartz_block) {
            return; // Already processed this meteorite
        }

        // Verify this looks like a meteorite by checking for sky stone nearby
        if (!isNearSkyStone(world, pos)) {
            return; // Probably not a meteorite
        }

        // Find the lowest solid block under the sky chest to place budding certus quartz
        BlockPos targetPos = findBestPlacementPos(world, pos);

        if (targetPos != null) {
            // Replace the block directly under the sky chest with budding certus quartz
            world.setBlockState(targetPos, BuddingPolarBlocks.budding_certus_quartz_block.getDefaultState());

            System.out.println(
                "[Budding Polar] Placed budding certus quartz block under meteorite sky chest at " + targetPos.getX()
                    + ", "
                    + targetPos.getY()
                    + ", "
                    + targetPos.getZ());
        }
    }

    private boolean isNearSkyStone(World world, BlockPos pos) {
        // Check for sky stone blocks in a small radius to confirm this is a meteorite
        int skyStoneCount = 0;

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos checkPos = pos.add(dx, dy, dz);
                    Block block = world.getBlockState(checkPos).getBlock();
                    String registryName = block.getRegistryName() != null ? block.getRegistryName().toString() : "";

                    if (registryName.contains("skystone") || registryName.contains("sky_stone")) {
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

    private BlockPos findBestPlacementPos(World world, BlockPos pos) {
        // Start from directly under the sky chest and find the first solid block
        for (int checkY = pos.getY() - 1; checkY > 5; checkY--) {
            BlockPos checkPos = new BlockPos(pos.getX(), checkY, pos.getZ());
            Block block = world.getBlockState(checkPos).getBlock();

            // Skip air and liquid blocks
            if (block == Blocks.AIR || block == Blocks.WATER
                || block == Blocks.LAVA
                || block == Blocks.FLOWING_WATER
                || block == Blocks.FLOWING_LAVA) {
                continue;
            }

            // Found a solid block - this is where we'll place the budding certus quartz
            return checkPos;
        }

        return null; // No suitable location found
    }
}
