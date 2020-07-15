package com.droog71.reactor_turbines.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.droog71.reactor_turbines.config.ConfigHandler;
import com.droog71.reactor_turbines.init.ReactorTurbineBlocks;
import com.droog71.reactor_turbines.init.ReactorTurbineSounds;
import ic2.api.energy.prefab.BasicSource;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReactorTurbineTileEntity extends TileEntity implements ITickable
{
	public BasicSource ic2EnergySource = new BasicSource(this, 1024, 3);
	private int turbineSoundLoopTimer;
	private int alarmSoundLoopTimer;
	private int coolingTimer;
	private int overHeatTimer;
	private int waterEvaporated;
	private int enclosureCheckTimer;
	private float reactorHeatGoal;
	private float currentHeatPercentage;
	private boolean noReactor;
	private boolean built;
	private boolean located;
	private boolean hasWater;
	private boolean overHeating;
	private BlockPos thisTurbineWaterPos;
	private BlockPos mainTurbinePos;
	private TileEntity mainTurbine;
	public TileEntity reactorTileEntity;
	public BlockPos reactorPos;
	public double currentGeneration;
	public int reactorHeat;
	public int numTurbines;
	public boolean hasReactor;
	public boolean isAdjacent;
	public boolean mainTurbineReset;
	public boolean enclosureIntact;
	private List<BlockPos> enclosurePositions = new ArrayList<BlockPos>();
	private List<BlockPos> reactorAdjacentPositions = new ArrayList<BlockPos>();
	private List<BlockPos> adjacentTurbinePositions = new ArrayList<BlockPos>();
	private List<BlockPos> waterPositions = new ArrayList<BlockPos>();
	
	@Override
	public void onLoad() 
	{
		ic2EnergySource.onLoad();
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
			if (!world.isRemote) //Everything is done on the server.
			{
				if (located == false)
				{
					locate(); //Gets the x,y,z position of relevant blocks.
				}
				hasReactor = false; //Reset each tick to check for changes in the world.
				isAdjacent = false; //Reset each tick to check for changes in the world.
				noReactor = false; //Reset each tick to check for changes in the world.
				if (world.getBlockState(reactorPos) != null)
				{
					if (world.getBlockState(reactorPos).getBlock() != null)
					{
						reactorTileEntity = world.getTileEntity(reactorPos);
						if (reactorTileEntity != null)
						{
							if (reactorTileEntity instanceof ic2.api.reactor.IReactor) //There is a reactor below the turbine.
							{
								hasReactor = true; //This is the main turbine, directly over the reactor.
								noReactor = false; //This is not an adjacent turbine.
								reactorHeat = ((ic2.api.reactor.IReactor) reactorTileEntity).getHeat(); //Amount of heat in the reactor this tick.
							}
							else //There was no reactor found below this turbine.
							{
								isAdjacent = false;
								noReactor = true;
								hasReactor = false;
								reactorHeat = 0;
							}
						}
						else //There was no tile entity found below this turbine.
						{
							isAdjacent = false;
							noReactor = true;
							hasReactor = false;
							reactorHeat = 0;
						}
					}
				}
				for (BlockPos p : adjacentTurbinePositions)
				{
					if (noReactor == true) //No reactor below this turbine, so we will determine if it is adjacent to the primary turbine.
					{
						if (world.getBlockState(p) != null)
						{
							if (world.getBlockState(p).getBlock() != null)
							{
								mainTurbine = world.getTileEntity(p);
								if (mainTurbine != null)
								{
									if (mainTurbine instanceof ReactorTurbineTileEntity)
									{
										if (((ReactorTurbineTileEntity) mainTurbine).hasReactor == true) //This turbine is adjacent to the main turbine.
										{
											isAdjacent = true;
											hasReactor = false;
											noReactor = false;
											mainTurbinePos = p;
											reactorHeat = ((ReactorTurbineTileEntity) mainTurbine).reactorHeat; //Amount of heat in the reactor this tick.
										}
									}
									else //Adjacent block is not a turbine.
									{
										isAdjacent = false;
										noReactor = true;
										hasReactor = false;
										reactorHeat = 0;
									}
								}
								else //No tile entity found.
								{
									isAdjacent = false;
									noReactor = true;
									hasReactor = false;
									reactorHeat = 0;
								}
							}
						}
					}
				}
				if (hasReactor == true) //This is the primary turbine.
				{
					numTurbines = 1; //System is not active until all 5 turbines are in place.
					for (BlockPos p : adjacentTurbinePositions)
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
										if (((ReactorTurbineTileEntity) adjacentTileEntity).hasReactor == false)
										{
											numTurbines++;
										}
									}
								}
							}
						}
					}
					if (numTurbines == 5)
					{
						if (built == false) //Initial assembly of the enclosure.
						{
							for (BlockPos p : enclosurePositions)
							{
								Block b = world.getBlockState(p).getBlock();
								if (b == Blocks.AIR || b == Blocks.WATER || b == Blocks.FLOWING_WATER)
								{
									world.setBlockState(p,Blocks.GLASS.getDefaultState()); //Fill in any missing block with glass.
								}
							}
							built = true;
							enclosureIntact = true;
						}
						else
						{
							enclosureCheckTimer++;
							if (enclosureCheckTimer >= 100) //Check for missing blocks in the enclosure positions.
							{
								boolean foundMissingBlock = false;
								for (BlockPos p : enclosurePositions)
								{
									Block b = world.getBlockState(p).getBlock();
									if (b == Blocks.AIR || b == Blocks.WATER || b == Blocks.FLOWING_WATER)
									{
										foundMissingBlock = true;
									}
								}
								if (foundMissingBlock == true)
								{
									enclosureIntact = false;
								}
								else
								{
									enclosureIntact = true;
								}
							}
							if (enclosureIntact == true) //The enclosure is not missing any blocks.
							{
								for (BlockPos p : reactorAdjacentPositions)
								{
									if (world.getBlockState(p) != null)
									{
										if (world.getBlockState(p).getBlock() != null)
										{
											TileEntity disallowedConnection = world.getTileEntity(p);
											if (disallowedConnection != null)
											{
												if (disallowedConnection instanceof ic2.api.energy.tile.IEnergyTile || disallowedConnection instanceof ic2.core.block.wiring.TileEntityTransformer || disallowedConnection instanceof ic2.core.block.wiring.TileEntityElectricBlock || disallowedConnection instanceof ic2.core.block.machine.tileentity.TileEntityStandardMachine || disallowedConnection instanceof ic2.core.block.machine.tileentity.TileEntityElectricMachine || disallowedConnection instanceof ic2.core.block.wiring.TileEntityCable)
												{
													world.setBlockToAir(p); //Prevents traditional use of the reactor when turbines are in place.
												}
											}
										}
									}
								}
								ic2.api.reactor.IReactor reactor = (ic2.api.reactor.IReactor) reactorTileEntity;
								if (reactor != null)
								{	
									reactorHeatGoal = reactor.getMaxHeat() * 0.25f; //Heat required for optimal output is 25% of max reactor heat.
									currentHeatPercentage = reactor.getHeat() / reactorHeatGoal; //How close the reactor is to 25% max heat.
									if (overHeating == false)
									{
										if (reactor.getReactorEUEnergyOutput() > 0)
										{
											reactor.addOutput(reactor.getReactorEnergyOutput() * -1); //Set reactor output to zero.
										}
										if (reactorHeat >= 1) //The reactor is hot enough to operate the turbine.
										{
											currentGeneration = 512 * currentHeatPercentage * ConfigHandler.getPowerMultiplier(); //EU added to buffer each tick.
											if (currentGeneration > 512)
											{
												currentGeneration = 512 * ConfigHandler.getPowerMultiplier(); //Maximum eu added to buffer each tick.
											}
											ic2EnergySource.addEnergy(currentGeneration); // Add the calculated eu to the ic2 energy source buffer.
											reactor.addOutput((float)currentGeneration); //Set reactor output to the output of the turbines.
											coolingTimer++;
											if (coolingTimer >= 20)
											{
												if (currentHeatPercentage < 0.2f)
												{
													if (reactor.getHeat() >= 24)
													{
														reactor.addHeat(-24); //Turbines cool the reactor.
													}
													else
													{
														reactor.addHeat(reactor.getHeat()*-1); //Heat has reached zero.
													}
												}
												else if (currentHeatPercentage >= 0.2f && currentHeatPercentage < 0.4f)
												{
													if (reactor.getHeat() >= 48)
													{
														reactor.addHeat(-48); //Turbines cool the reactor.
													}
												}
												else if (currentHeatPercentage >= 0.4f && currentHeatPercentage < 0.6f)
												{
													if (reactor.getHeat() >= 72)
													{
														reactor.addHeat(-72); //Turbines cool the reactor.
													}
												}
												else if (currentHeatPercentage >= 0.6f && currentHeatPercentage < 0.8f)
												{
													if (reactor.getHeat() >= 96)
													{
														reactor.addHeat(-96); //Turbines cool the reactor.
													}
												}
												else if (currentHeatPercentage >= 0.8f && currentHeatPercentage <  1.0f)
												{
													if (reactor.getHeat() >= 120)
													{
														reactor.addHeat(-120); //Turbines cool the reactor.
													}
												}
												else if (currentHeatPercentage >= 1.0f)
												{
													if (reactor.getHeat() >= 144)
													{
														reactor.addHeat(-144); //Turbines cool the reactor.
													}
												}
												coolingTimer = 0;
											}
											waterEvaporated++;
											if (waterEvaporated >= 1200/currentHeatPercentage) //Water in a 3x3 shape centered above the reactor will evaporate completely at this time.
											{
												world.playSound(null, pos, ReactorTurbineSounds.steamSoundEvent,  SoundCategory.BLOCKS, 1, 1);
												for (BlockPos p : waterPositions)
												{
													if (world.getBlockState(p).getBlock() == Blocks.WATER || world.getBlockState(p).getBlock() == Blocks.FLOWING_WATER)
													{
														world.setBlockToAir(p);
													}
												}
												waterEvaporated = 0;
											}
											turbineSoundLoopTimer++;
											if (turbineSoundLoopTimer > 55) //Sound effect repeats once every 55 ticks (~3 seconds).
											{
												world.playSound(null, pos, ReactorTurbineSounds.turbineSoundEvent,  SoundCategory.BLOCKS, 0.5f, 1);
												turbineSoundLoopTimer = 0;
											}
										}
									}
									Block waterBlock = world.getBlockState(thisTurbineWaterPos).getBlock();
									if (waterBlock != Blocks.WATER) //There is no water below the turbine.
									{
										if (overHeating == false) //The reactor has gone without water for less than ~30 seconds.
										{
											overHeatTimer++;
											if (overHeatTimer >= 600)
											{
												overHeatTimer = 0;
												overHeating = true; //The reactor has gone without water for ~30 seconds or greater.
											}
										}
										else
										{
											if (reactor.getReactorEUEnergyOutput() > 0)
											{
												reactor.addOutput(reactor.getReactorEnergyOutput() * -1); //Set reactor output to zero.
											}
										}
									}
									else
									{
										overHeatTimer = 0;
										overHeating = false;
									}
									if (currentHeatPercentage >= 1.5f) //Reactor is too hot.
									{
										alarmSoundLoopTimer++;
										if (alarmSoundLoopTimer >= 200)
										{
											world.playSound(null, pos, ReactorTurbineSounds.alarmSoundEvent,  SoundCategory.BLOCKS, 1, 1);
											alarmSoundLoopTimer = 0;
										}
									}
								}
							}
						}
					}
				}
				else if (isAdjacent == true)
				{
					ReactorTurbineTileEntity turbine = ((ReactorTurbineTileEntity) mainTurbine); //The main turbine.
					if (turbine != null)
					{
						if (turbine.mainTurbineReset == false) //Sometimes turbines don't interact with the reactor correctly when the world is loaded. This is a workaround.
						{
							world.setBlockToAir(mainTurbinePos);
							world.removeTileEntity(mainTurbinePos);
							world.setBlockState(mainTurbinePos, ReactorTurbineBlocks.reactorTurbine.getDefaultState());
							((ReactorTurbineTileEntity) world.getTileEntity(mainTurbinePos)).mainTurbineReset = true;
						}
						else
						{
							if (turbine.reactorTileEntity != null)
							{
								if (turbine.numTurbines == 5 && turbine.enclosureIntact == true)
								{
									ic2.api.reactor.IReactor reactor = (ic2.api.reactor.IReactor) turbine.reactorTileEntity; //The reactor below the main turbine.
									if (reactor != null)
									{
										reactorHeatGoal = reactor.getMaxHeat() * 0.25f; //Heat required for optimal output is 25% of max reactor heat.
										currentHeatPercentage = reactor.getHeat() / reactorHeatGoal; //How close the reactor is to 25% max heat.
										if (overHeating == false)
										{
											if (reactorHeat >= 1)
											{
												currentGeneration = 512 * currentHeatPercentage * ConfigHandler.getPowerMultiplier(); //EU added to buffer each tick.
												if (currentGeneration > 512)
												{
													currentGeneration = 512 * ConfigHandler.getPowerMultiplier(); //Maximum eu added to buffer each tick.
												}
												ic2EnergySource.addEnergy(currentGeneration); // Add the calculated eu to the ic2 energy source buffer.
												turbineSoundLoopTimer++;
												if (turbineSoundLoopTimer > 55) //Sound effect repeats once every 55 ticks (~3 seconds).
												{
													world.playSound(null, pos, ReactorTurbineSounds.turbineSoundEvent,  SoundCategory.BLOCKS, 0.5f, 1);
													turbineSoundLoopTimer = 0;
												}
											}
										}
										Block waterBlock = world.getBlockState(thisTurbineWaterPos).getBlock();
										if (waterBlock != Blocks.WATER) //There is no water below the turbine.
										{
											if (overHeating == false) //The reactor has gone without water for less than ~30 seconds.
											{
												overHeatTimer++;
												if (overHeatTimer >= 600)
												{
													overHeatTimer = 0;
													overHeating = true; //The reactor has gone without water for ~30 seconds or greater.
												}
											}
										}
										else
										{
											overHeatTimer = 0;
											overHeating = false;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	void locate()
	{
		//Reactor//
		reactorPos = new BlockPos(this.pos.getX(),this.pos.getY()-2,this.pos.getZ()); //Reactor must be 2 blocks below the main turbine.
		
		//Water below this turbine//
		thisTurbineWaterPos = new BlockPos(this.pos.getX(),this.pos.getY()-1,this.pos.getZ()); //Directly below this turbine.
		
		//Reactor adjacent//
		reactorAdjacentPositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-1,this.pos.getZ())); //Above the reactor.
		reactorAdjacentPositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-3,this.pos.getZ())); //Below the reactor.
		reactorAdjacentPositions.add(new BlockPos(this.pos.getX()-1,this.pos.getY()-2,this.pos.getZ())); //Adjacent to the reactor.
		reactorAdjacentPositions.add(new BlockPos(this.pos.getX()+1,this.pos.getY()-2,this.pos.getZ())); //Adjacent to the reactor.
		reactorAdjacentPositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-2,this.pos.getZ()+1)); //Adjacent to the reactor.
		reactorAdjacentPositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-2,this.pos.getZ()-1)); //Adjacent to the reactor.
		
		//Turbines//
		adjacentTurbinePositions.add(new BlockPos(this.pos.getX()-1,this.pos.getY(),this.pos.getZ())); //Adjacent turbine position.
		adjacentTurbinePositions.add(new BlockPos(this.pos.getX()+1,this.pos.getY(),this.pos.getZ())); //Adjacent turbine position.
		adjacentTurbinePositions.add(new BlockPos(this.pos.getX(),this.pos.getY(),this.pos.getZ()+1)); //Adjacent turbine position.
		adjacentTurbinePositions.add(new BlockPos(this.pos.getX(),this.pos.getY(),this.pos.getZ()-1)); //Adjacent turbine position.
		
		//Water//
		waterPositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-1,this.pos.getZ())); //This turbine's water position.
		waterPositions.add(new BlockPos(this.pos.getX()-1,this.pos.getY()-1,this.pos.getZ())); //For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(this.pos.getX()+1,this.pos.getY()-1,this.pos.getZ())); //For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-1,this.pos.getZ()+1)); //For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-1,this.pos.getZ()-1)); //For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(this.pos.getX()+1,this.pos.getY()-1,this.pos.getZ()+1)); //For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(this.pos.getX()+1,this.pos.getY()-1,this.pos.getZ()-1)); //For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(this.pos.getX()-1,this.pos.getY()-1,this.pos.getZ()+1)); //For 3x3 water above the reactor.
		waterPositions.add(new BlockPos(this.pos.getX()-1,this.pos.getY()-1,this.pos.getZ()-1)); //For 3x3 water above the reactor.
		
		/////Enclosure/////
		
		//Left side of water.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()-1));
		
		//Right side of water.
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()-1));
		
		//Front side of water.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ()+2));
		
		//Back side of water.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-1,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-1,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-1,pos.getZ()-2));
		
		//Corners around the water.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-1,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-1,pos.getZ()-2));
		
		//Left side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()-1));
		
		//Right side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()-1));
		
		//Front side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()+2));
		
		//Back side of turbines.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY(),pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()-2));
		
		//Around the turbines.
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()-1));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY(),pos.getZ()-1));
		
		//Corners around the turbines.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY(),pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY(),pos.getZ()-2));
		
		//Left side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()-1));
		
		//Right side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()+1));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()-1));
		
		//Front side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-2,pos.getZ()+2));
		
		//Back side of reactor.
		enclosurePositions.add(new BlockPos(pos.getX(),pos.getY()-2,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()-1,pos.getY()-2,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+1,pos.getY()-2,pos.getZ()-2));
		
		//Corners around the reactor.
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()-2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()-2,pos.getY()-2,pos.getZ()+2));
		enclosurePositions.add(new BlockPos(pos.getX()+2,pos.getY()-2,pos.getZ()-2));
		
		//Reactor adjacent.
		enclosurePositions.add(new BlockPos(this.pos.getX()-1,this.pos.getY()-2,this.pos.getZ()));
		enclosurePositions.add(new BlockPos(this.pos.getX()+1,this.pos.getY()-2,this.pos.getZ()));
		enclosurePositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-2,this.pos.getZ()+1));
		enclosurePositions.add(new BlockPos(this.pos.getX(),this.pos.getY()-2,this.pos.getZ()-1));
		
		//Around the reactor adjacent blocks.
		enclosurePositions.add(new BlockPos(reactorPos.getX()+1,reactorPos.getY(),reactorPos.getZ()+1));
		enclosurePositions.add(new BlockPos(reactorPos.getX()-1,reactorPos.getY(),reactorPos.getZ()-1));
		enclosurePositions.add(new BlockPos(reactorPos.getX()+1,reactorPos.getY(),reactorPos.getZ()-1));
		enclosurePositions.add(new BlockPos(reactorPos.getX()-1,reactorPos.getY(),reactorPos.getZ()+1));
		
		located = true;
	}
}