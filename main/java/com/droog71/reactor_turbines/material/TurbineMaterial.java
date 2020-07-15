package com.droog71.reactor_turbines.material;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class TurbineMaterial extends Material
{
	public static final Material TURBINE = new TurbineMaterial(MapColor.IRON).setRequiresTool();
	
	public TurbineMaterial(MapColor color) 
	{
		super(color);
	}
}
