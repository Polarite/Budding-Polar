package com.polarite.buddingpolar.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.polarite.buddingpolar.BuddingPolar;
import com.polarite.buddingpolar.BuddingPolarBlocks;
import com.polarite.buddingpolar.sounds.BuddingPolarSounds;

public class BlockBuddingCertusQuartz extends Block {

    public BlockBuddingCertusQuartz() {
        super(Material.ROCK);
        setHardness(1.5F);
        setResistance(1.5F);
        setSoundType(SoundType.GLASS);
        setTranslationKey("budding_certus_quartz_block");
        setRegistryName(BuddingPolar.MODID, "budding_certus_quartz_block");
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(BuddingPolar.creativeTabs);
        setTickRandomly(true);
    }

    @Override
    public boolean canHarvestBlock(net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player) {
        if (player == null) return false;
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty()) return false;

        boolean hasPickaxe = heldItem.getItem().getToolClasses(heldItem).contains("pickaxe");

        if (!hasPickaxe) {
            return false;
        }

        boolean silkTouchRequired = com.polarite.buddingpolar.config.BuddingPolarConfig
            .isSilkTouchRequiredForBuddingBlocks();

        if (silkTouchRequired) {
            boolean hasSilkTouch = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(
                net.minecraft.init.Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) > 0;
            return hasSilkTouch;
        }
        return true;
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 0;
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        return "pickaxe";
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleUpdate(pos, this, 10); // Start ticking every 10 ticks
        BuddingPolarSounds.playSound(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            BuddingPolarSounds.BUDDING_PLACE, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        world.scheduleUpdate(pos, this, 10);
        int acceleratorCount = 0;
        int additionalAttempts = 0;

        try {
            // Only check for accelerators if AE2 integration is available
            Class.forName("com.polarite.buddingpolar.integration.AE2Integration");
            acceleratorCount = com.polarite.buddingpolar.integration.AE2Integration
                .countAdjacentAccelerators(world, pos);
            additionalAttempts = com.polarite.buddingpolar.integration.AE2Integration
                .calculateAdditionalGrowthAttempts(acceleratorCount);
        } catch (ClassNotFoundException e) {}

        int totalAttempts = 1 + additionalAttempts; // Base 1 + 0-6 additional from accelerators

        // Determine growth chance based on accelerator presence and configuration
        int growthChance;
        if (acceleratorCount > 0) {
            // With accelerators: use configured growth chance
            growthChance = com.polarite.buddingpolar.config.BuddingPolarConfig.getGrowthChanceWithAccelerators();
        } else {
            // Without accelerators: use configured slower growth chance
            growthChance = com.polarite.buddingpolar.config.BuddingPolarConfig.getGrowthChanceWithoutAccelerators();
        }

        boolean didGrow = false;
        for (int attempt = 0; attempt < totalAttempts && !didGrow; attempt++) {
            if (rand.nextInt(growthChance) != 0) {
                continue; // Failed this attempt
            }

            didGrow = attemptGrowth(world, pos, rand);
        }

        if (rand.nextInt(8) == 0) { // 1 in 8 chance
            BuddingPolarSounds.playShimmerSound(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
    }

    private boolean attemptGrowth(World world, BlockPos pos, Random rand) {
        int facing = rand.nextInt(6);

        int offsetX = 0, offsetY = 0, offsetZ = 0;
        switch (facing) {
            case 0:
                offsetY = -1;
                break; // Down
            case 1:
                offsetY = 1;
                break; // Up
            case 2:
                offsetZ = -1;
                break; // North
            case 3:
                offsetZ = 1;
                break; // South
            case 4:
                offsetX = -1;
                break; // West
            case 5:
                offsetX = 1;
                break; // East
        }

        BlockPos targetPos = pos.add(offsetX, offsetY, offsetZ);
        IBlockState targetState = world.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();

        if (targetBlock instanceof BlockCertusQuartzCluster) {
            int targetMeta = targetBlock.getMetaFromState(targetState);
            if (targetMeta % 6 == facing) {
                if (targetBlock == BuddingPolarBlocks.small_certus_quartz_bud) {
                    world.setBlockState(targetPos, BuddingPolarBlocks.medium_certus_quartz_bud.getStateFromMeta(facing));
                    return true;
                } else if (targetBlock == BuddingPolarBlocks.medium_certus_quartz_bud) {
                    world.setBlockState(targetPos, BuddingPolarBlocks.large_certus_quartz_bud.getStateFromMeta(facing));
                    return true;
                } else if (targetBlock == BuddingPolarBlocks.large_certus_quartz_bud) {
                    world.setBlockState(targetPos, BuddingPolarBlocks.certus_quartz_cluster.getStateFromMeta(facing));
                    return true;
                }
            }
        } else if (canGrowIn(targetState)) {
            world.setBlockState(targetPos, BuddingPolarBlocks.small_certus_quartz_bud.getStateFromMeta(facing));
            return true;
        }

        return false;
    }

    private boolean canGrowIn(IBlockState state) {
        return state.getMaterial() == Material.AIR || state.getMaterial() == Material.WATER;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        BuddingPolarSounds.playSound(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            BuddingPolarSounds.BUDDING_BREAK, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        BuddingPolarSounds.playSound(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            BuddingPolarSounds.BUDDING_STEP, 0.7f, 0.8f + world.rand.nextFloat() * 0.4f);
    }
}
