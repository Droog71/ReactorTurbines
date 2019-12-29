package com.droog71.reactor_turbines.init;

import com.droog71.reactor_turbines.ReactorTurbines;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ReactorTurbines.MODID)
public class ReactorTurbineSounds
{
	static ResourceLocation turbineSoundLocation;
	static ResourceLocation steamSoundLocation;
	static ResourceLocation alarmSoundLocation;
	public static SoundEvent turbineSoundEvent;
	public static SoundEvent steamSoundEvent;
	public static SoundEvent alarmSoundEvent;
	
	public static void init() 
	{
		turbineSoundLocation = new ResourceLocation("reactor_turbines", "turbine");
		steamSoundLocation = new ResourceLocation("reactor_turbines", "steam");
		alarmSoundLocation = new ResourceLocation("reactor_turbines", "alarm");
		turbineSoundEvent = new SoundEvent(turbineSoundLocation);
		steamSoundEvent = new SoundEvent(steamSoundLocation);
		alarmSoundEvent = new SoundEvent(alarmSoundLocation);
		registerSoundEvent("turbine",turbineSoundEvent);
		registerSoundEvent("steam",steamSoundEvent);
		registerSoundEvent("alarm",alarmSoundEvent);
	}
	
	public static void registerSoundEvent(String name, SoundEvent event)
	{
		event.setRegistryName(name);
		ForgeRegistries.SOUND_EVENTS.register(event);
	}
}