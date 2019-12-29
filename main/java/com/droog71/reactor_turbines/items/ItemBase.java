package com.droog71.reactor_turbines.items;

import net.minecraft.item.Item;

public class ItemBase extends Item 
{
	public ItemBase(String name) 
	{
		setUnlocalizedName(name);
		setRegistryName(name);
	}
}