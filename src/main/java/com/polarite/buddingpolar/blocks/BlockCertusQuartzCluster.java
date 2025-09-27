package com.polarite.buddingpolar.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.polarite.buddingpolar.BuddingPolar;
import com.polarite.buddingpolar.BuddingPolarItems;
import com.polarite.buddingpolar.integration.AE2Integration;
import com.polarite.buddingpolar.sounds.BuddingPolarSounds;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCertusQuartzCluster extends Block {

    private final int type;

    private static final String[] STAGE_NAMES = { "small_certus_quartz_bud", "medium_certus_quartz_bud",
        "large_certus_quartz_bud", "certus_quartz_cluster" };
    private static final String[] TEXTURE_NAMES = { "small_certus_quartz_bud", "medium_certus_quartz_bud",
        "large_certus_quartz_bud", "certus_quartz_cluster" };

    public BlockCertusQuartzCluster(int type) {
        super(Material.glass);
        setHardness(1.5F);
        setResistance(1.5F);
        setStepSound(Block.soundTypeGlass);
        setBlockName(STAGE_NAMES[type]);
        setBlockTextureName("buddingpolar:" + TEXTURE_NAMES[type]);
        setHarvestLevel("pickaxe", 0);
        setLightLevel(0.0625F + (type * 0.125F));
        setCreativeTab(BuddingPolar.creativeTabs);
        this.type = type;
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return 1 + (type * 3);
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        if (!harvestingWithPickaxe()) {
            return null;
        }
        if (AE2Integration.hasAE2Items()) {
            return AE2Integration.ae2MultiMaterial;
        }
        return null;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
        if (!world.isRemote && !world.restoringBlockSnapshots) {
            if (!harvestingWithPickaxe()) {
                harvesters.remove();
                return;
            }

            EntityPlayer harvester = harvesters.get();
            boolean hasSilkTouch = harvester != null
                && net.minecraft.enchantment.EnchantmentHelper.getSilkTouchModifier(harvester);
            if (com.polarite.buddingpolar.config.BuddingPolarConfig.isSilkTouchRequiredForClusters() && hasSilkTouch) {
                ItemStack clusterBlock = new ItemStack(this, 1, metadata);
                float f = 0.7F;
                double dx = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double dy = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double dz = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, clusterBlock);
                entityItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(entityItem);
            } else {
                int quantity = quantityDropped(metadata, fortune, world.rand);
                if (quantity > 0) {
                    ItemStack itemStack = BuddingPolarItems.createItemStackForCluster(type, quantity);
                    if (itemStack != null) {
                        float f = 0.7F;
                        double dx = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double dy = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        double dz = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                        EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, itemStack);
                        entityItem.delayBeforeCanPickup = 10;
                        world.spawnEntityInWorld(entityItem);
                    }
                }
            }
            harvesters.remove();
        }
    }

    protected ItemStack createStackedBlock(int metadata) {
        return new ItemStack(this, 1, metadata);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        harvesters.remove();
        int quantity = quantityDroppedForAutomation(metadata, fortune, world.rand);
        if (quantity > 0) {
            ItemStack itemStack = BuddingPolarItems.createItemStackForCluster(type, quantity);
            if (itemStack != null) {
                drops.add(itemStack);
            }
        }
        return drops;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        super.breakBlock(world, x, y, z, block, metadata);
    }

    private int quantityDroppedForAutomation(int meta, int fortune, Random random) {
        int drop = quantityDroppedBase(random);
        if (type == 3 && fortune > 0 && random.nextInt(2 + fortune) == 0) {
            drop += fortune;
        }
        return drop;
    }

    private int quantityDroppedBase(Random random) {
        switch (type) {
            case 3: // Full cluster - drops 4-7 pure certus quartz
                return 4 + random.nextInt(3);
            case 2: // Large cluster - drops 2-3 dust
                return 2 + random.nextInt(2);
            case 1: // Medium cluster - drops 1-2 dust
                return 1 + random.nextInt(2);
            case 0: // Small cluster - drops 1 dust
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        if (player == null) return false;
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (heldItem == null) return false;

        boolean hasPickaxe = heldItem.getItem()
            .getToolClasses(heldItem)
            .contains("pickaxe");
        return hasPickaxe;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    public int quantityDropped(int meta, int fortune, Random random) {
        int drop = quantityDropped(random);
        if (type == 3 && fortune > 0 && random.nextInt(2 + fortune) == 0) {
            drop += fortune;
        }
        return drop;
    }

    public int quantityDropped(Random random) {
        if (!harvestingWithPickaxe()) {
            return 0;
        }

        switch (type) {
            case 3: // Full cluster - drops 4 pure certus quartz
                return 4 + random.nextInt(3);
            case 2: // Large cluster - drops 2-3 dust
                return 2 + random.nextInt(2);
            case 1: // Medium cluster - drops 1-2 dust
                return 1 + random.nextInt(2);
            case 0: // Small cluster - drops 1 dust
                return 1;
            default:
                return 0;
        }
    }

    private static final ThreadLocal<EntityPlayer> harvesters = new ThreadLocal<>();

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        harvesters.set(player);
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    private boolean harvestingWithPickaxe() {
        EntityPlayer harvester = harvesters.get();
        if (harvester == null) {
            return false;
        }

        ItemStack heldItem = harvester.getCurrentEquippedItem();
        if (heldItem == null) {
            return false;
        }
        Set<String> toolClasses = heldItem.getItem()
            .getToolClasses(heldItem);
        return toolClasses.contains("pickaxe");
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        return side;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        float height = 0.1875F + (type * 0.125F);
        float xzOffset = 0.375F - (type * 0.0625F);

        switch (meta % 6) {
            case 0: // Down
                return AxisAlignedBB.getBoundingBox(
                    x + xzOffset,
                    y + 1 - height,
                    z + xzOffset,
                    x + 1 - xzOffset,
                    y + 1.0F,
                    z + 1 - xzOffset);
            case 1: // Up
                return AxisAlignedBB
                    .getBoundingBox(x + xzOffset, y, z + xzOffset, x + 1 - xzOffset, y + height, z + 1 - xzOffset);
            case 2: // North
                return AxisAlignedBB.getBoundingBox(
                    x + xzOffset,
                    y + xzOffset,
                    z + 1 - height,
                    x + 1 - xzOffset,
                    y + 1 - xzOffset,
                    z + 1.0F);
            case 3: // South
                return AxisAlignedBB
                    .getBoundingBox(x + xzOffset, y + xzOffset, z, x + 1 - xzOffset, y + 1 - xzOffset, z + height);
            case 4: // West
                return AxisAlignedBB.getBoundingBox(
                    x + 1 - height,
                    y + xzOffset,
                    z + xzOffset,
                    x + 1.0F,
                    y + 1 - xzOffset,
                    z + 1 - xzOffset);
            case 5: // East
                return AxisAlignedBB
                    .getBoundingBox(x, y + xzOffset, z + xzOffset, x + height, y + 1 - xzOffset, z + 1 - xzOffset);
        }
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
        int meta = access.getBlockMetadata(x, y, z);
        float height = 0.1875F + (type * 0.125F);
        float xzOffset = 0.375F - (type * 0.0625F);

        switch (meta % 6) {
            case 0: // Down
                this.setBlockBounds(xzOffset, 1 - height, xzOffset, 1 - xzOffset, 1.0F, 1 - xzOffset);
                break;
            case 1: // Up
                this.setBlockBounds(xzOffset, 0.0F, xzOffset, 1 - xzOffset, height, 1 - xzOffset);
                break;
            case 2: // North
                this.setBlockBounds(xzOffset, xzOffset, 1 - height, 1 - xzOffset, 1 - xzOffset, 1.0F);
                break;
            case 3: // South
                this.setBlockBounds(xzOffset, xzOffset, 0.0F, 1 - xzOffset, 1 - xzOffset, height);
                break;
            case 4: // West
                this.setBlockBounds(1 - height, xzOffset, xzOffset, 1.0F, 1 - xzOffset, 1 - xzOffset);
                break;
            case 5: // East
                this.setBlockBounds(0.0F, xzOffset, xzOffset, height, 1 - xzOffset, 1 - xzOffset);
                break;
        }
    }

    protected void checkAndDropBlock(World world, int x, int y, int z) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        this.checkAndDropBlock(world, x, y, z);
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return this.canPlaceBlockOnSide(world, x, y, z, world.getBlockMetadata(x, y, z));
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
        int offsetX = 0, offsetY = 0, offsetZ = 0;
        switch (side % 6) {
            case 0:
                offsetY = 1;
                break; // Down
            case 1:
                offsetY = -1;
                break; // Up
            case 2:
                offsetZ = 1;
                break; // North
            case 3:
                offsetZ = -1;
                break; // South
            case 4:
                offsetX = 1;
                break; // West
            case 5:
                offsetX = -1;
                break; // East
        }
        return world.getBlock(x + offsetX, y + offsetY, z + offsetZ)
            .isOpaqueCube();
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, @SuppressWarnings("rawtypes") List list) {
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return com.polarite.buddingpolar.renderer.RenderIDs.CERTUS_QUARTZ_CLUSTER;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
        super.onBlockDestroyedByPlayer(world, x, y, z, metadata);
        BuddingPolarSounds
            .playSound(world, x, y, z, BuddingPolarSounds.CLUSTER_BREAK, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        BuddingPolarSounds
            .playSound(world, x, y, z, BuddingPolarSounds.CLUSTER_PLACE, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        // Clusters use the default glass step sound for walking
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "step.stone", 0.3f, 0.8f + world.rand.nextFloat() * 0.4f);
    }
}
