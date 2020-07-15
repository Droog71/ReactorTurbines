package com.droog71.reactor_turbines.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ConfigHandler 
{
	public static float getPowerMultiplier()
	{
		File configFile = new File(System.getProperty("user.dir")+"/config/reactor_turbines.cfg");	
        if (configFile.exists())
        {
			Scanner configFileScanner;
			try 
			{
				configFileScanner = new Scanner(configFile);
				String configFileContents = configFileScanner.useDelimiter("\\Z").next();				
				configFileScanner.close();
				String[]splitContents = configFileContents.split(":");
				float configValue = Float.parseFloat(splitContents[1]);
				if (configValue > 1.0f)
				{
					return 1.0f;
				}
				else if (configValue < 0.1f)
				{
					return 0.1f;
				}
				else
				{
					return configValue;
				}
			} 
			catch (FileNotFoundException e) 
			{
				System.out.println("Reactor turbines mod failed to find config file!");
				e.printStackTrace();
			}			
        }
        else
        {
        	try 
        	{
				configFile.createNewFile();
				FileWriter f;    			
			    try 
			    {
			        f = new FileWriter(configFile,false);
			        f.write("turbine_output_modifier:1");
			        f.close();
			    } 
			    catch (IOException ioe) 
			    {
			    	System.out.println("Reactor turbines mod failed to write to config file!");
			        ioe.printStackTrace();
			    } 
			} 
        	catch (IOException e) 
        	{
				e.printStackTrace();
			}
        }
        return 1.0f;
	}
}