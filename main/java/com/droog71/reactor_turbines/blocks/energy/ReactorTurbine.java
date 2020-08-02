package com.droog71.reactor_turbines.blocks.energy;

import com.droog71.reactor_turbines.blocks.BlockBase;
import com.droog71.reactor_turbines.tile_entity.ReactorTurbineTileEntity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
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