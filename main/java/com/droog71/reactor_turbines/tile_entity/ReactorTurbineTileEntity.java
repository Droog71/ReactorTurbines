package com.droog71.reactor_turbines.tile_entity;

import com.droog71.reactor_turbines.config.ConfigHandler;
import com.droog71.reactor_turbines.forge_energy.EnergyStorage;
import com.droog71.reactor_turbines.init.ReactorTurbineBlocks;
import com.droog71.reactor_turbines.init.ReactorTurbineSounds;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.reactor.IReactor;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ReactorTurbineTileEntity extends TileEntity implements ITickable
{
	public BasicSource ic2EnergySource = new BasicSource(this, 1024, 3);
	private EnergyStorage energyStorage = new EnergyStorage();
	private TurbineBlockPositions positions = new TurbineBlockPositions();
	private ReactorTurbineTileEntity mainTurbine;
	public IReactor reactor;
	private int turbineSoundLoopTimer;
	private int alarmSoundLoopTimer;
	private int coolingTimer;
	private int outOfWaterTimer;
	private int waterEvaporated;
	private int enclosureCheckTimer;
	private float reactorHeatGoal;
	private float currentHeatPercentage;
	private boolean built;
	private boolean located;
	private boolean outOfWater;	
	public double currentGeneration;
	public int reactorHeat;
	public int numTurbines;
	public boolean isMainTurbine;
	public boolean mainTurbineLoaded;
	public boolean adjacentTurbinesLoaded;
	public boolean enclosureIntact;
	
	@Override
	public void onLoad() 
	{
		ic2EnergySource.onLoad();
		energyStorage.capacity = 4096;
	}
	
	@Override
	public void invalidate() 
	{
		super.invalidate();
		ic2EnergySource.invalidate();
	}
	
	@Override
	public void onChunkUnload() 
	{
		ic2EnergySource.onChunkUnload();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) 
	{
		super.readFromNBT(tag);
		ic2EnergySource.readFromNBT(tag);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		return ic2EnergySource.writeToNBT(tag);
	}

	@Override
	public void update() 
	{
		if (world != null)
		{
			if (world.isRemote == false)
			{
				// Get the x,y,z position of relevant blocks.
				if (located == false)
				{
					positions.getPositions(pos);
					located = true;
				}
								
				if (aboveReactor()) // This is the primary turbine.
				{
					if (adjacentTurbineCount() == 5) // System is not active until all 5 turbines are in place.
					{
						if (mainTurbineLoaded == true && adjacentTurbinesLoaded == false) 
						{		
							// Refresh the tile entities on load.
							reloadAdjacentTurbines();
						}
						if (built == false) 
						{
							// Build the initial enclosure.
							buildEnclosure();
						}
						else
						{
							enclosureCheckTimer++;
							if (enclosureCheckTimer >= 100) 
							{
								// Check for missing blocks in the enclosure positions.
								checkEnclosure();
							}
							if (enclosureIntact == true)
							{	
								// Main turbine generates power.
								generateAsMain();
							}
						}
					}
				}
				else
				{		
					// Get all info needed as an adjacent turbine.
					if (getMainTurbine() != null)
					{
						if (mainTurbine.mainTurbineLoaded == false)
						{							
							reloadMainTurbine();
						}
						else
						{							
							generateAsAdjacent();
						}
					}
				}
			}
		}
	}

	// Check for rector below the turbine.
	private boolean aboveReactor()
	{
		if (world.getBlockState(positions.reactorPos) != null)
		{
			if (world.getBlockState(positions.reactorPos).getBlock() != null)
			{
				reactor = (IReactor) world.getTileEntity(positions.reactorPos);
				if (reactor != null)
				{					
					isMainTurbine = true; // This is the main turbine, directly over the reactor.						
					return true;
				}
			}
		}
		if (isMainTurbine == true)
		{
			mainTurbineLoaded = false; // The reactor was there but no longer exists.
		}
		isMainTurbine = false;
		return false;
	}

	// Get all info needed as an adjacent turbine.
	private ReactorTurbineTileEntity getMainTurbine()
	{
		for (BlockPos p : positions.adjacentTurbinePositions)
		{
			if (world.getBlockState(p) != null)
			{
				if (world.getBlockState(p).getBlock() != null)
				{
					TileEntity tileEntityAtPosition = world.getTileEntity(p);
					if (tileEntityAtPosition != null)
					{						
						if (tileEntityAtPosition instanceof ReactorTurbineTileEntity)
						{
							if (((ReactorTurbineTileEntity) tileEntityAtPosition).isMainTurbine == true) // This turbine is adjacent to the main turbine.
							{
								positions.mainTurbinePos = p;	
								mainTurbine = (ReactorTurbineTileEntity) tileEntityAtPosition;
								return mainTurbine;
							}
						}
					}
				}
			}			
		}
		return null;
	}
	
	// Counts the number of adjacent turbines around this block.
	private int adjacentTurbineCount()
	{
		numTurbines = 1;
		for (BlockPos p : positions.adjacentTurbinePositions)
		{
			if (world.getBlockState(p) != null)
			{
				if (world.getBlockState(p).getBlock() != null)
				{
					TileEntity adjacentTileEntity = world.getTileEntity(p);
					if (adjacentTileEntity != null)
					{
						if (adjacentTileEntity instanceof ReactorTurbineTileEntity)
						{
							if (((ReactorTurbineTileEntity) adjacentTileEntity).isMainTurbine == false)
							{
								numTurbines++;
							}
						}
					}
				}
			}
		}
		return numTurbines;
	}
	
	// Replaces adjacent turbines when the world is loaded so IC2 cables will connect to them.
	private void reloadAdjacentTurbines()
	{
		for (BlockPos p : positions.adjacentTurbinePositions)
		{
			world.setBlockToAir(p);
			world.removeTileEntity(p);
			world.setBlockState(p, ReactorTurbineBlocks.reactorTurbine.getDefaultState());
		}
		adjacentTurbinesLoaded = true;
	}
	
	// Initial assembly of the enclosure.
	private void buildEnclosure()
	{
		for (BlockPos p : positions.enclosurePositions)
		{
			Block b = world.getBlockState(p).getBlock();
			if (b == Blocks.AIR || b == Blocks.WATER || b == Blocks.FLOWING_WATER)
			{
				world.setBlockState(p,Blocks.GLASS.getDefaultState()); // Fill in any missing block with glass.
			}
		}
		built = true;
		enclosureIntact = true;
	}
	
	// Checks for missing blocks in enclosure positions.
	private boolean checkEnclosure()
	{
		for (BlockPos p : positions.enclosurePositions)
		{
			Block b = world.getBlockState(p).getBlock();
			if (b == Blocks.AIR || b == Blocks.WATER || b == Blocks.FLOWING_WATER)
			{
				enclosureIntact = false;
				return enclosureIntact;
			}
		}
		enclosureIntact = true;
		return enclosureIntact;
	}
	
	// Generate power as the main turbine.
	private void generateAsMain()
	{
		if (reactor != null)
		{	
			checkForDisallowedConnections();	
			
			negateReactorOutput(reactor);
			
			reactorHeat = reactor.getHeat(); // Amount of heat in the reactor this tick.
			
			if (reactorHeat >= 1) // The reactor is hot enough to operate the turbine.
			{
				reactorHeatGoal = reactor.getMaxHeat() * 0.25f; // Heat required for optimal output is 25% of max reactor heat.
				currentHeatPercentage = reactor.getHeat() / reactorHeatGoal; // How close the reactor is to 25% max heat.			
				
				if (currentHeatPercentage >= 1.5f) // Reactor is too hot.
				{
					soundAlarm();
				}
				
				checkForWater();
				
				if (outOfWater == false)
				{
					currentGeneration = 512 * currentHeatPercentage * ConfigHandler.getPowerMultiplier(); // Energy added to buffer each tick.
					
					if (currentGeneration > 512)
					{
						currentGeneration = 512 * ConfigHandler.getPowerMultiplier(); // Maximum energy added to buffer each tick.
					}
					
					addEnergy(currentGeneration); 
					
					// ic2 experimental reactor GUI will display power generated by turbines.
					reactor.addOutput((float)currentGeneration); 
					
					coolingTimer++;
					if (coolingTimer >= 20)
					{
						coolReactor(reactor);
						coolingTimer = 0;
					}
					
					waterEvaporated++;
					if (waterEvaporated >= 1200 / currentHeatPercentage)
					{
						evaporateWater();
					}
					
					turbineSoundLoopTimer++;
					if (turbineSoundLoopTimer > 25)
					{
						world.playSound(null, pos, ReactorTurbineSounds.turbineSoundEvent,  SoundCategory.BLOCKS, 0.5f, 1);
						turbineSoundLoopTimer = 0;
					}
				}
			}		
		}
	}
	
	// Replaces the main turbine so it will connect to the reactor.
	private void reloadMainTurbine()
	{
		world.setBlockToAir(positions.mainTurbinePos);
		world.removeTileEntity(positions.mainTurbinePos);
		world.setBlockState(positions.mainTurbinePos, ReactorTurbineBlocks.reactorTurbine.getDefaultState());
		((ReactorTurbineTileEntity) world.getTileEntity(positions.mainTurbinePos)).mainTurbineLoaded = true;
	}
	
	// Generate power as an adjacent turbine.
	private void generateAsAdjacent()
	{
		if (mainTurbine.reactor != null)
		{
			if (mainTurbine.numTurbines == 5 && mainTurbine.enclosureIntact == true)
			{
				if (mainTurbine.reactor != null)
				{		
					checkForWater();
					if (outOfWater == false)
					{
						reactorHeat = mainTurbine.reactorHeat; // Amount of heat in the reactor this tick.
						if (reactorHeat >= 1)
						{
							reactorHeatGoal = mainTurbine.reactor.getMaxHeat() * 0.25f; // Heat required for optimal output is 25% of max reactor heat.
							currentHeatPercentage = mainTurbine.reactor.getHeat() / reactorHeatGoal; // How close the reactor is to 25% max heat.
							currentGeneration = 512 * currentHeatPercentage * ConfigHandler.getPowerMultiplier(); // EU added to buffer each tick.
							if (currentGeneration > 512)
							{
								currentGeneration = 512 * ConfigHandler.getPowerMultiplier(); // Maximum amount added to buffer each tick.
							}
							addEnergy(currentGeneration); //  Add the calculated amount to the energy source buffer.
						}
					}
				}
			}
		}
	}
	
	// Prevents traditional use of the reactor when turbines are in place.
	private void checkForDisallowedConnections()
	{
		for (BlockPos p : positions.reactorAdjacentPositions)
		{
			if (world.getBlockState(p) != null)
			{
				if (world.getBlockState(p).getBlock() != null)
				{
					TileEntity disallowedConnection = world.getTileEntity(p);
					if (disallowedConnection != null)
					{
						if (disallowedConnection instanceof IEnergyTile || disallowedConnection instanceof ic2.core.block.wiring.TileEntityTransformer || disallowedConnection instanceof ic2.core.block.wiring.TileEntityElectricBlock || disallowedConnection instanceof ic2.core.block.machine.tileentity.TileEntityStandardMachine || disallowedConnection instanceof ic2.core.block.machine.tileentity.TileEntityElectricMachine || disallowedConnection instanceof ic2.core.block.wiring.TileEntityCable)
						{
							world.setBlockToAir(p); 
						}
					}
				}
			}
		}
	}
	
	// Removes heat from the reactor.
	private void coolReactor(IReactor reactor)
	{
		if (currentHeatPercentage < 0.2f)
		{
			if (reactor.getHeat() >= 24)
			{
				reactor.addHeat(-24); // Turbines cool the reactor.
			}
			else
			{
				reactor.addHeat(reactor.getHeat()*-1); // Heat has reached zero.
			}
		}
		else if (currentHeatPercentage >= 0.2f && currentHeatPercentage < 0.4f)
		{
			if (reactor.getHeat() >= 48)
			{
				reactor.addHeat(-48); // Turbines cool the reactor.
			}
		}
		else if (currentHeatPercentage >= 0.4f && currentHeatPercentage < 0.6f)
		{
			if (reactor.getHeat() >= 72)
			{
				reactor.addHeat(-72); // Turbines cool the reactor.
			}
		}
		else if (currentHeatPercentage >= 0.6f && currentHeatPercentage < 0.8f)
		{
			if (reactor.getHeat() >= 96)
			{
				reactor.addHeat(-96); // Turbines cool the reactor.
			}
		}
		else if (currentHeatPercentage >= 0.8f && currentHeatPercentage <  1.0f)
		{
			if (reactor.getHeat() >= 120)
			{
				reactor.addHeat(-120); // Turbines cool the reactor.
			}
		}
		else if (currentHeatPercentage >= 1.0f)
		{
			if (reactor.getHeat() >= 144)
			{
				reactor.addHeat(-144); // Turbines cool the reactor.
			}
		}
	}
	
	// Checks for water below the turbine.
	private void checkForWater()
	{
		Block waterBlock = world.getBlockState(positions.thisTurbineWaterPos).getBlock();
		if (waterBlock != Blocks.WATER) // There is no water below the turbine.
		{
			if (outOfWater == false) // The turbine has gone without water for less than ~10 seconds.
			{
				outOfWaterTimer++;
				if (outOfWaterTimer >= 200)
				{
					outOfWaterTimer = 0;
					outOfWater = true; // The turbine has gone without water for ~10 seconds or greater.
				}
			}
		}
		else
		{
			outOfWaterTimer = 0;
			outOfWater = false;
		}		
	}
	
	// Removes water blocks above the reactor.
	private void evaporateWater()
	{
		world.playSound(null, pos, ReactorTurbineSounds.steamSoundEvent,  SoundCategory.BLOCKS, 1, 1);
		for (BlockPos p : positions.waterPositions)
		{
			if (world.getBlockState(p).getBlock() == Blocks.WATER || world.getBlockState(p).getBlock() == Blocks.FLOWING_WATER)
			{
				world.setBlockToAir(p);
			}
		}
		waterEvaporated = 0;
	}
	
	// Turbines take over reactor output for ic2 GUI display.
	private void negateReactorOutput(IReactor reactor)
	{		
		if (reactor.getReactorEUEnergyOutput() > 0)
		{
			reactor.addOutput(reactor.getReactorEnergyOutput() * -1);
		}
	}
	
	private void soundAlarm()
	{
		alarmSoundLoopTimer++;
		if (alarmSoundLoopTimer >= 200)
		{
			world.playSound(null, pos, ReactorTurbineSounds.alarmSoundEvent,  SoundCategory.BLOCKS, 1, 1);
			alarmSoundLoopTimer = 0;
		}
	}
	
	// Add energy to the buffer
	private void addEnergy(double amount)
	{
		if (energyStorage.receivers(world, pos).size() > 0)
		{		
			energyStorage.generateEnergy((int) amount * 4);
			for (IEnergyStorage sink : energyStorage.receivers(world, pos))
			{
				energyStorage.giveEnergy(energyStorage, sink, (int) amount * 4);
			}
		}
		else
		{
			ic2EnergySource.addEnergy(amount);
		}
	}
	
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing)
    {
        if (capability == CapabilityEnergy.ENERGY)
        {
        	return (T) energyStorage;
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
    	if (capability == CapabilityEnergy.ENERGY)
    	{
    		return true;
    	}
    	return super.hasCapability(capability, facing);
    }
}
