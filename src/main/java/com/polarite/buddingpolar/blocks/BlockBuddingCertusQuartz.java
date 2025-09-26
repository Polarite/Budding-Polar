package com.polarite.buddingpolar.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.polarite.buddingpolar.BuddingPolarBlocks;

public class BlockBuddingCertusQuartz extends Block {

    public BlockBuddingCertusQuartz() {
        super(Material.rock);
        setHardness(1.5F);
        setResistance(1.5F);
        setStepSound(Block.soundTypeGlass);
        setBlockName("budding_certus_quartz_block");
        setBlockTextureName("buddingpolar:budding_certus_quartz_block");
        setHarvestLevel("pickaxe", 0); // Requires pickaxe to mine
        setCreativeTab(com.polarite.buddingpolar.BuddingPolar.creativeTabs);
        setTickRandomly(true);
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        // Check if player has valid tool
        if (player == null) return false;
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) return false;

        boolean hasPickaxe = heldItem.getItem()
            .getToolClasses(heldItem)
            .contains("pickaxe");

        // If silk touch is required, check for silk touch enchantment
        if (com.polarite.buddingpolar.config.BuddingPolarConfig.isSilkTouchRequiredForBuddingBlocks()) {
            return hasPickaxe && net.minecraft.enchantment.EnchantmentHelper.getSilkTouchModifier(player);
        }

        // Otherwise just require pickaxe
        return hasPickaxe;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        // Start the self-accelerating tick cycle when block is placed
        world.scheduleBlockUpdate(x, y, z, this, 10); // Start ticking every 10 ticks
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        // Match AE2 behavior: Growth Accelerators tick every 10 ticks by default
        // and budding blocks have a 1 in 5 (20%) chance to grow per random tick

        // Schedule the next tick to maintain self-accelerating behavior
        world.scheduleBlockUpdate(x, y, z, this, 10); // Every 10 ticks = 0.5 seconds (matches AE2 default)

        // Check for adjacent Growth Accelerators and calculate additional attempts
        int acceleratorCount = 0;
        int additionalAttempts = 0;

        try {
            // Only check for accelerators if AE2 integration is available
            Class.forName("com.polarite.buddingpolar.integration.AE2Integration");
            acceleratorCount = com.polarite.buddingpolar.integration.AE2Integration
                .countAdjacentAccelerators(world, x, y, z);
            additionalAttempts = com.polarite.buddingpolar.integration.AE2Integration
                .calculateAdditionalGrowthAttempts(acceleratorCount);
        } catch (ClassNotFoundException e) {
            // AE2 integration not available, use base attempts only
        }

        // AE2 uses GROWTH_CHANCE = 5 (1 in 5 chance per random tick)
        // With accelerators, each adjacent accelerator provides additional random ticks
        // WITHOUT accelerators, we reduce the growth chance to 1/5th (1 in 25)
        // So we apply the growth chance check multiple times based on accelerator count

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
            // Growth chance varies based on accelerator presence
            if (rand.nextInt(growthChance) != 0) {
                continue; // Failed this attempt
            }

            didGrow = attemptGrowth(world, x, y, z, rand);
        }
    }

    private boolean attemptGrowth(World world, int x, int y, int z, Random rand) {
        // Get random direction (0-5 for the 6 faces) - similar to AE2's approach
        int facing = rand.nextInt(6);

        // Calculate offset based on facing direction
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

        // Check if there's already a certus quartz cluster there and upgrade it
        // Use the same metadata checking logic as NovaCraft (meta should match facing direction)
        if (targetBlock instanceof BlockCertusQuartzCluster && targetMeta % 6 == facing) {
            // Upgrade through the 4 tiers: small -> medium -> large -> cluster
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
            // Full clusters don't upgrade further
        } else if (canGrowIn(targetBlock)) {
            // Place new small bud with proper facing metadata (like NovaCraft)
            world.setBlock(targetX, targetY, targetZ, BuddingPolarBlocks.small_certus_quartz_bud, facing, 3);
            return true;
        }

        return false; // Growth attempt failed
    }

    private boolean canGrowIn(Block block) {
        return block.getMaterial() == Material.air || block.getMaterial() == Material.water;
    }

    // ThreadLocal to track who is harvesting this block
    private static final ThreadLocal<EntityPlayer> harvesters = new ThreadLocal<>();

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        harvesters.set(player);
        super.onBlockHarvested(world, x, y, z, meta, player);
        harvesters.remove();
    }

    private boolean harvestingWithPickaxe() {
        EntityPlayer harvester = harvesters.get();
        if (harvester == null) return false;
        ItemStack heldItem = harvester.getCurrentEquippedItem();
        if (heldItem == null) return false;
        return heldItem.getItem()
            .getToolClasses(heldItem)
            .contains("pickaxe");
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
        if (!world.isRemote && !world.restoringBlockSnapshots) {
            // Only drop if harvested with pickaxe
            if (!harvestingWithPickaxe()) {
                return; // No drops without pickaxe
            }
        }
        super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);
    }

    @Override
    public java.util.ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        java.util.ArrayList<ItemStack> drops = new java.util.ArrayList<ItemStack>();

        // getDrops is called by automation tools like ME Annihilation Plane
        // Always clear the ThreadLocal and allow drops for automation
        harvesters.remove();

        // Create the dropped item (the budding certus quartz block itself)
        ItemStack itemStack = new ItemStack(Item.getItemFromBlock(this), 1, metadata);
        if (itemStack != null) {
            drops.add(itemStack);
        }

        return drops;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true; // Allow silk harvesting, but only if harvested with pickaxe
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        // Only drop if harvested with pickaxe
        if (harvestingWithPickaxe()) {
            return Item.getItemFromBlock(this);
        }
        return null; // No drops without pickaxe
    }
}
