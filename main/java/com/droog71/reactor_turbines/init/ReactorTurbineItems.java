package com.droog71.reactor_turbines.init;

import com.droog71.reactor_turbines.ReactorTurbines;
import com.droog71.reactor_turbines.items.ItemBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid=ReactorTurbines.MODID)
public class ReactorTurbineItems 
{
	static Item reactorTurbineImpeller;
	
	public static void init() 
	{
		reactorTurbineImpeller = new ItemBase("turbine_impeller").setCreativeTab(ReactorTurbines.tabReactorTurbines).setMaxStackSize(64);
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) 
	{
		event.getRegistry().registerAll(reactorTurbineImpeller);
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) 
	{
		registerRender(reactorTurbineImpeller);
	}
	
	private static void registerRender(Item item) 
	{
		ReactorTurbines.proxy.registerRenderInformation(item);
	}
}