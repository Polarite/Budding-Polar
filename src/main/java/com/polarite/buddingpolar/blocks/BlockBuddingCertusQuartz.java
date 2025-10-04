package com.polarite.buddingpolar.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.polarite.buddingpolar.BuddingPolarBlocks;
import com.polarite.buddingpolar.sounds.BuddingPolarSounds;

public class BlockBuddingCertusQuartz extends Block {

    public BlockBuddingCertusQuartz() {
        super(Material.rock);
        setHardness(1.5F);
        setResistance(1.5F);
        setStepSound(Block.soundTypeGlass);
        setBlockName("budding_certus_quartz_block");
        setBlockTextureName("buddingpolar:budding_certus_quartz_block");
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(com.polarite.buddingpolar.BuddingPolar.creativeTabs);
        setTickRandomly(true);
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        if (player == null) return false;
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) return false;

        boolean hasPickaxe = heldItem.getItem()
            .getToolClasses(heldItem)
            .contains("pickaxe");

        if (!hasPickaxe) {
            return false;
        }

        boolean silkTouchRequired = com.polarite.buddingpolar.config.BuddingPolarConfig
            .isSilkTouchRequiredForBuddingBlocks();

        if (silkTouchRequired) {
            boolean hasSilkTouch = net.minecraft.enchantment.EnchantmentHelper.getSilkTouchModifier(player);
            return hasSilkTouch;
        }
        return true;
    }

    @Override
    public int getHarvestLevel(int metadata) {
        return 0;
    }

    @Override
    public String getHarvestTool(int metadata) {
        return "pickaxe";
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 10); // Start ticking every 10 ticks
        BuddingPolarSounds
            .playSound(world, x, y, z, BuddingPolarSounds.BUDDING_PLACE, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        world.scheduleBlockUpdate(x, y, z, this, 10);
        int acceleratorCount = 0;
        int additionalAttempts = 0;

        try {
            // Only check for accelerators if AE2 integration is available
            Class.forName("com.polarite.buddingpolar.integration.AE2Integration");
            acceleratorCount = com.polarite.buddingpolar.integration.AE2Integration
                .countAdjacentAccelerators(world, x, y, z);
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

            didGrow = attemptGrowth(world, x, y, z, rand);
        }

        if (rand.nextInt(8) == 0) { // 1 in 8 chance
            BuddingPolarSounds.playShimmerSound(world, x, y, z);
        }
    }

    private boolean attemptGrowth(World world, int x, int y, int z, Random rand) {
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

        int targetX = x + offsetX;
        int targetY = y + offsetY;
        int targetZ = z + offsetZ;

        Block targetBlock = world.getBlock(targetX, targetY, targetZ);
        int targetMeta = world.getBlockMetadata(targetX, targetY, targetZ);

        if (targetBlock instanceof BlockCertusQuartzCluster && targetMeta % 6 == facing) {
            if (targetBlock == BuddingPolarBlocks.small_certus_quartz_bud) {
                world.setBlock(targetX, targetY, targetZ, BuddingPolarBlocks.medium_certus_quartz_bud, facing, 3);
                return true;
            } else if (targetBlock == BuddingPolarBlocks.medium_certus_quartz_bud) {
                world.setBlock(targetX, targetY, targetZ, BuddingPolarBlocks.large_certus_quartz_bud, facing, 3);
                return true;
            } else if (targetBlock == BuddingPolarBlocks.large_certus_quartz_bud) {
                world.setBlock(targetX, targetY, targetZ, BuddingPolarBlocks.certus_quartz_cluster, facing, 3);
                return true;
            }
        } else if (canGrowIn(targetBlock)) {
            world.setBlock(targetX, targetY, targetZ, BuddingPolarBlocks.small_certus_quartz_bud, facing, 3);
            return true;
        }

        return false;
    }

    private boolean canGrowIn(Block block) {
        return block.getMaterial() == Material.air || block.getMaterial() == Material.water;
    }

    private static final ThreadLocal<EntityPlayer> harvesters = new ThreadLocal<>();

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        harvesters.set(player);
        super.onBlockHarvested(world, x, y, z, meta, player);
        harvesters.remove();
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);
    }

    @Override
    public java.util.ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        java.util.ArrayList<ItemStack> drops = new java.util.ArrayList<ItemStack>();

        harvesters.remove();

        ItemStack itemStack = new ItemStack(Item.getItemFromBlock(this), 1, metadata);
        if (itemStack != null) {
            drops.add(itemStack);
        }

        return drops;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
        super.onBlockDestroyedByPlayer(world, x, y, z, metadata);
        BuddingPolarSounds
            .playSound(world, x, y, z, BuddingPolarSounds.BUDDING_BREAK, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        BuddingPolarSounds
            .playSound(world, x, y, z, BuddingPolarSounds.BUDDING_STEP, 0.7f, 0.8f + world.rand.nextFloat() * 0.4f);
    }
}
