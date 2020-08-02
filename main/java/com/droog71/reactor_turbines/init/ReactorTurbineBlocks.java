package com.droog71.reactor_turbines.init;

import com.droog71.reactor_turbines.ReactorTurbines;
import com.droog71.reactor_turbines.blocks.energy.ReactorTurbine;
import com.droog71.reactor_turbines.tile_entity.ReactorTurbineTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = ReactorTurbines.MODID)
public class ReactorTurbineBlocks 
{
	public static Block reactorTurbine;
	static ReactorTurbineTileEntity turbineTileEntity;
	
	public static void init() 
	{
		reactorTurbine = new ReactorTurbine("reactor_turbine",Material.IRON).setCreativeTab(ReactorTurbines.tabReactorTurbines);
		turbineTileEntity = new ReactorTurbineTileEntity();
		GameRegistry.registerTileEntity(turbineTileEntity.getClass(), "reactor_turbines:turbineTileEntity");
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) 
	{
		event.getRegistry().registerAll(reactorTurbine);
	}
	
	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) 
	{
		event.getRegistry().registerAll(new ItemBlock(reactorTurbine).setRegistryName(reactorTurbine.getRegistryName()));
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) 
	{
		registerRender(Item.getItemFromBlock(reactorTurbine));
	}
	
	public static void registerRender(Item item) 
	{
		ReactorTurbines.proxy.registerRenderInformation(item);
	}
}
