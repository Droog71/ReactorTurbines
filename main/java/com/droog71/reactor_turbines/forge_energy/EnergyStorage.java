package com.droog71.reactor_turbines.forge_energy;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage
{
	protected int energy;    
    public int capacity;

    // Load the amount of energy stored.
    public void readFromNBT(NBTTagCompound compound)
    {
    	energy = compound.getInteger("energy");
    }

    // Save the amount of energy stored.
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
    	compound.setInteger("energy",energy);
    	return compound;
    }

    // Add energy to the buffer.
    public int generateEnergy(int amount)
    {
    	int energyAdded = Math.min(capacity - energy, amount);
        energy += energyAdded;
        return energyAdded;     
    }
    
    // Remove energy from the buffer.
    public int useEnergy(int energyToUse)
    {
        int energyUsed = Math.min(energy, energyToUse);
        energy -= energyUsed;
        return energyUsed;
    }
 
    // A list of all adjacent blocks capable of receiving energy.
    public List<IEnergyStorage> receivers(World world, BlockPos pos)
	{		
    	List<IEnergyStorage> receiversFound = new ArrayList<IEnergyStorage>();
		BlockPos[] sides = {pos.add(0,1,0),pos.add(1,0,0),pos.add(0,0,1),pos.add(0,-1,0),pos.add(-1,0,0),pos.add(0,0,-1)};
		for (BlockPos p : sides)
		{
			TileEntity otherTile = world.getTileEntity(p);
			if (otherTile != null)
			{				
				EnumFacing direction = null;
				for (EnumFacing facing : EnumFacing.VALUES)
				{
					IEnergyStorage otherStorage = otherTile.getCapability(CapabilityEnergy.ENERGY,facing);
					if (otherStorage != null)
					{
						if (direction == null)
						{
							direction = facing;
						}						
					}
				}
				IEnergyStorage otherStorage = otherTile.getCapability(CapabilityEnergy.ENERGY,direction);
				if (otherStorage != null)
				{			
					if (otherStorage.canReceive())
					{
						receiversFound.add(otherStorage);
					}								
				}
			}						
		}
		return receiversFound;
	}
    
    // Give energy to receivers
    public void giveEnergy(EnergyStorage source, IEnergyStorage sink, int rating)
    {
    	if (sink != null)
		{								
			if (sink.canReceive()) // The adjacent block can receive the energy.
			{	
				int demand = sink.getMaxEnergyStored() - sink.getEnergyStored(); // The energy required by the receiver.
				int potential = Math.min(demand, rating); // Limit output to the maximum output of the energy storage.						
				int output = Math.min(potential, source.getEnergyStored()); // Limit output to the amount of energy stored.
				source.useEnergy(output); // Remove energy from the source.
				sink.receiveEnergy(output,false); // Add the energy to the adjacent block.
			}
		}
    }

    // Get the amount of energy in the buffer.
    @Override
    public int getEnergyStored()
    {
        return energy;
    }

    // Get the maximum capacity of the buffer.
    @Override
    public int getMaxEnergyStored()
    {
        return capacity;
    }

    // Not used.
    @Override
    public boolean canExtract()
    {
        return false;
    }

    // Not used.
    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return 0;
    }
    
    // Not used.
    @Override
    public boolean canReceive()
    {
        return false;
    }
    
    // Not used.
    @Override
    public int receiveEnergy(int amount, boolean simulate)
    {
		return 0; 	
    }
}
