package com.droog71.reactor_turbines;

import org.apache.logging.log4j.Logger;
import com.droog71.reactor_turbines.common.CommonProxy;
import com.droog71.reactor_turbines.init.ReactorTurbineBlocks;
import com.droog71.reactor_turbines.init.ReactorTurbineItems;
import com.droog71.reactor_turbines.init.ReactorTurbineSounds;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ReactorTurbines.MODID, name = ReactorTurbines.NAME, version = ReactorTurbines.VERSION, dependencies = "required-after:ic2")
public class ReactorTurbines
{
    public static final String MODID = "reactor_turbines";
    public static final String NAME = "Reactor Turbines";
    public static final String VERSION = "1.1.6";
    private static Logger logger;

    @Instance
	public static ReactorTurbines instance;
    
    @SidedProxy(clientSide = "com.droog71.reactor_turbines.common.ClientProxy", serverSide = "com.droog71.reactor_turbines.common.CommonProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		System.out.println("Reactor turbines mod found ic2 installed, good to go!");
		ReactorTurbineBlocks.init();
    	ReactorTurbineItems.init();
    	ReactorTurbineSounds.init();
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        
    }
    
    public static final CreativeTabs tabReactorTurbines = new CreativeTabs("Reactor Turbines") 
	{
		@Override
		public ItemStack getTabIconItem() 
		{
			return new ItemStack(ReactorTurbineBlocks.reactorTurbine);
		}
	};
}
