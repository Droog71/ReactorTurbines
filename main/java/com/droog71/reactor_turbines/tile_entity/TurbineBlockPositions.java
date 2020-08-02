package com.droog71.reactor_turbines.tile_entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public class TurbineBlockPositions 
{
	public BlockPos reactorPos;
	public BlockPos mainTurbinePos;
	public BlockPos thisTurbineWaterPos;
	public List<BlockPos> enclosurePositions = new ArrayList<BlockPos>();
	public List<BlockPos> reactorAdjacentPositions = new ArrayList<BlockPos>();
	public List<BlockPos> adjacentTurbinePositions = new ArrayList<BlockPos>();
	public List<BlockPos> waterPositions = new ArrayList<BlockPos>();
	
	// Gets the x,y,z position of relevant blocks.
	public void getPositions(BlockPos pos)
	{
		// Reactor // 
		reactorPos = new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()); // Reactor must be 2 blocks below the main turbine.
		
		// Water below this turbine // 
		thisTurbineWaterPos = new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()); // Directly below the turbine.
		
		// Reactor adjacent // 
		reactorAdjacentPositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ())); // Above the reactor.
		reactorAdjacentPositions.add(new BlockPos(pos.getX(),pos.getY()-3,pos.getZ())); // Below the reactor.
		reactorAdjacentPositions.add(new BlockPos(pos.getX()-1,pos.getY()-2,pos.getZ())); // Adjacent to the reactor.
		reactorAdjacentPositions.add(new BlockPos(pos.getX()+1,pos.getY()-2,pos.getZ())); // Adjacent to the reactor.
		reactorAdjacentPositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()+1)); // Adjacent to the reactor.
		reactorAdjacentPositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()-1)); // Adjacent to the reactor.
		
		// Turbines // 
		adjacentTurbinePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ())); // Adjacent turbine position.
		adjacentTurbinePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ())); // Adjacent turbine position.
		adjacentTurbinePositions.add(new BlockPos(pos.getX(),pos.getY(),pos.getZ()+1)); // Adjacent turbine position.
		adjacentTurbinePositions.add(new BlockPos(pos.getX(),pos.getY(),pos.getZ()-1)); // Adjacent turbine position.
		
		// Water // 
		waterPositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ())); // This turbine's water position.
		waterPositions.add(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ())); // For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ())); // For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()+1)); // For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()-1)); // For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ()+1)); // For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ()-1)); // For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ()+1)); // For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ()-1)); // For 3x3 water above the reactor.
		
		// // Enclosure // //
		
		// Left side of water.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()-1));
		
		// Right side of water.
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()-1));
		
		// Front side of water.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ()+2));
		
		// Back side of water.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ()-2));
		
		// Corners around the water.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()-2));
		
		// Left side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()-1));
		
		// Right side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()-1));
		
		// Front side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()+2));
		
		// Back side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY(),pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()-2));
		
		// Around the turbines.
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()-1));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()-1));
		
		// Corners around the turbines.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()-2));
		
		// Left side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()-1));
		
		// Right side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()-1));
		
		// Front side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-2,pos.getZ()+2));
		
		// Back side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-2,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-2,pos.getZ()-2));
		
		// Corners around the reactor.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()-2));
		
		// Reactor adjacent.
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-2,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-2,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()-1));
		
		// Around the reactor adjacent blocks.
		enclosurePositions.add(new BlockPos(reactorPos.getX()+1,reactorPos.getY(),reactorPos.getZ()+1));
		enclosurePositions.add(new BlockPos(reactorPos.getX()-1,reactorPos.getY(),reactorPos.getZ()-1));
		enclosurePositions.add(new BlockPos(reactorPos.getX()+1,reactorPos.getY(),reactorPos.getZ()-1));
		enclosurePositions.add(new BlockPos(reactorPos.getX()-1,reactorPos.getY(),reactorPos.getZ()+1));
	}
}
