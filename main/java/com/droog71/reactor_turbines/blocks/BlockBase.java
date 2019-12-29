package com.droog71.reactor_turbines.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBase extends Block 
{
	public BlockBase(String name, Material material) 
	{
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
	}
}