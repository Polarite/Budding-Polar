package com.polarite.buddingpolar.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.polarite.buddingpolar.BuddingPolar;
import com.polarite.buddingpolar.BuddingPolarItems;
import com.polarite.buddingpolar.sounds.BuddingPolarSounds;
import com.polarite.buddingpolar.tileentity.TileEntityCertusQuartzCluster;

public class BlockCertusQuartzCluster extends BlockContainer {

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

    private final int type;
    private final String name;

    private static final String[] STAGE_NAMES = { "small_certus_quartz_bud", "medium_certus_quartz_bud",
        "large_certus_quartz_bud", "certus_quartz_cluster" };

    public BlockCertusQuartzCluster(int type) {
        super(Material.GLASS);
        setHardness(1.5F);
        setResistance(1.5F);
        setSoundType(SoundType.GLASS);
        this.name = STAGE_NAMES[type];
        setTranslationKey(this.name);
        setRegistryName(BuddingPolar.MODID, name);
        setHarvestLevel("pickaxe", 0);
        setLightLevel(getLightLevelForType(type));
        // Allow light to pass through transparent parts of the model
        setCreativeTab(BuddingPolar.creativeTabs);
        this.type = type;
        setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { FACING });
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getLightValueForType(type);
    }

    private float getLightLevelForType(int type) {
        switch (type) {
            case 0: // Small bud
                return 0.25F; // Light level 4
            case 1: // Medium bud  
                return 0.4375F; // Light level 7
            case 2: // Large bud
                return 0.625F; // Light level 10
            case 3: // Full cluster
                return 0.875F; // Light level 14
            default:
                return 0.25F;
        }
    }

    private int getLightValueForType(int type) {
        switch (type) {
            case 0: // Small bud
                return 4;
            case 1: // Medium bud
                return 7;
            case 2: // Large bud
                return 10;
            case 3: // Full cluster
                return 14;
            default:
                return 4;
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int quantity = quantityDroppedBase(world instanceof World ? ((World) world).rand : new Random());
        if (type == 3 && fortune > 0) {
            Random rand = world instanceof World ? ((World) world).rand : new Random();
            if (rand.nextInt(2 + fortune) == 0) {
                quantity += fortune;
            }
        }
        
        if (quantity > 0) {
            ItemStack itemStack = BuddingPolarItems.createItemStackForCluster(type, quantity);
            if (!itemStack.isEmpty()) {
                drops.add(itemStack);
            }
        }
    }

    private int quantityDroppedBase(Random random) {
        switch (type) {
            case 3: // Full cluster - drops 4-7 pure certus quartz
                return 4 + random.nextInt(4);
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
    public boolean canHarvestBlock(net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player) {
        if (player == null) return false;
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty()) return false;

        return heldItem.getItem().getToolClasses(heldItem).contains("pickaxe");
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        float height = 0.1875F + (type * 0.125F);
        float xzOffset = 0.375F - (type * 0.0625F);

        switch (facing) {
            case DOWN:
                return new AxisAlignedBB(xzOffset, 1 - height, xzOffset, 1 - xzOffset, 1.0F, 1 - xzOffset);
            case UP:
                return new AxisAlignedBB(xzOffset, 0.0F, xzOffset, 1 - xzOffset, height, 1 - xzOffset);
            case NORTH:
                return new AxisAlignedBB(xzOffset, xzOffset, 1 - height, 1 - xzOffset, 1 - xzOffset, 1.0F);
            case SOUTH:
                return new AxisAlignedBB(xzOffset, xzOffset, 0.0F, 1 - xzOffset, 1 - xzOffset, height);
            case WEST:
                return new AxisAlignedBB(1 - height, xzOffset, xzOffset, 1.0F, 1 - xzOffset, 1 - xzOffset);
            case EAST:
                return new AxisAlignedBB(0.0F, xzOffset, xzOffset, height, 1 - xzOffset, 1 - xzOffset);
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(world, pos, state)) {
            world.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos supportPos = pos.offset(facing.getOpposite());
        IBlockState supportState = world.getBlockState(supportPos);
        return supportState.isSideSolid(world, supportPos, facing);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        BlockPos supportPos = pos.offset(side.getOpposite());
        IBlockState supportState = world.getBlockState(supportPos);
        return supportState.isSideSolid(world, supportPos, side);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    public boolean isNormalCube(IBlockState state) {
        return false;
    }
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        // Use CUTOUT so fully-transparent pixels are discarded and won't render as black.
        // CUTOUT is appropriate for textures that are either fully opaque or fully transparent.
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        BuddingPolarSounds.playSound(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            BuddingPolarSounds.CLUSTER_BREAK, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        BuddingPolarSounds.playSound(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            BuddingPolarSounds.CLUSTER_PLACE, 1.0f, 0.8f + world.rand.nextFloat() * 0.4f);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCertusQuartzCluster();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 
            net.minecraft.init.SoundEvents.BLOCK_STONE_STEP, net.minecraft.util.SoundCategory.BLOCKS, 
            0.3f, 0.8f + world.rand.nextFloat() * 0.4f);
    }
}
