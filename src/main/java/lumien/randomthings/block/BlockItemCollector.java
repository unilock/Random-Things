package lumien.randomthings.block;

import lumien.randomthings.tileentity.TileEntityItemCollector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockItemCollector extends BlockContainerBase
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	
	protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.375F, 0.375F, 1.0F - 5 / 16.0F, 0.625F, 0.625F, 1.0F);
	protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 5 / 16.0F);
	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(1.0F - 5 / 16.0F, 0.375F, 0.375F, 1.0F, 0.625F, 0.625F);
	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0F, 0.375F, 0.375F, 5 / 16.0F, 0.625F, 0.625F);
	protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.375F, 0.0F, 0.375F, 0.625F, 0.0F + 5 / 16.0F, 0.625F);
	protected static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.375F, 1.0F - 5 / 16.0F, 0.375F, 0.625F, 1.0F, 0.625F);

	protected BlockItemCollector()
	{
		super("itemCollector", Material.rock);

		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
		this.setHardness(0.3F);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).ordinal();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityItemCollector();
	}

	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
	{
		return func_181088_a(worldIn, pos, side.getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		for (EnumFacing enumfacing : EnumFacing.values())
		{
			if (func_181088_a(worldIn, pos, enumfacing))
			{
				return true;
			}
		}

		return false;
	}

	protected static boolean func_181088_a(World p_181088_0_, BlockPos p_181088_1_, EnumFacing p_181088_2_)
	{
		return p_181088_2_ == EnumFacing.DOWN && isBlockInventory(p_181088_0_, p_181088_1_.down()) ? true : isBlockInventory(p_181088_0_,p_181088_1_.offset(p_181088_2_));
	}

	private static boolean isBlockInventory(World worldObj, BlockPos pos)
	{
		TileEntity te = worldObj.getTileEntity(pos);

		if (te == null)
		{
			return false;
		}
		
		return te instanceof IInventory;
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to
	 * allow for adjustments to the IBlockstate
	 */
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return func_181088_a(worldIn, pos, facing.getOpposite()) ? this.getDefaultState().withProperty(FACING, facing) : this.getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	/**
	 * Called when a neighboring block changes.
	 */
	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		if (this.checkForDrop(worldIn, pos, state) && !func_181088_a(worldIn, pos, state.getValue(FACING).getOpposite()))
		{
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
	{
		if (this.canPlaceBlockAt(worldIn, pos))
		{
			return true;
		}
		else
		{
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			return false;
		}
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		EnumFacing enumfacing = state.getValue(FACING);
		switch (enumfacing)
		{
			case EAST:
				return EAST_AABB;
			case WEST:
				return WEST_AABB;
			case SOUTH:
				return SOUTH_AABB;
			case NORTH:
				return NORTH_AABB;
			case UP:
				return UP_AABB;
			case DOWN:
				return DOWN_AABB;
		}
		
		return UP_AABB;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}
}
