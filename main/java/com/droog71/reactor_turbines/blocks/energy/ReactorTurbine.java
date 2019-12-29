package com.droog71.reactor_turbines.blocks.energy;

import com.droog71.reactor_turbines.blocks.BlockBase;
import com.droog71.reactor_turbines.init.ReactorTurbineBlocks;
import com.droog71.reactor_turbines.init.ReactorTurbineSounds;
import com.droog71.reactor_turbines.tileentity.ReactorTurbineTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReactorTurbine extends BlockBase
{
	public ReactorTurbine(String name, Material material) 
	{
		super(name, material);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) 
	{
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new ReactorTurbineTileEntity();
	}
}