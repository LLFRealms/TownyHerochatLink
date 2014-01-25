package com.llfrealms.THChatLink.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.llfrealms.THChatLink.THCLink;
import com.llfrealms.THChatLink.util.Utilities;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class THCCommands  implements CommandExecutor 
{
	private THCLink plugin;
	public THCCommands(THCLink plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(cmd.getName().equalsIgnoreCase("thcsave") && sender.hasPermission("tllf.save"))
	    {
			plugin.saveConfig();
			plugin.saveYamls();
        	Utilities.sendMessage(sender, "Configs saved");
        	return true;
	    }
		if(cmd.getName().equalsIgnoreCase("thcload")  && sender.hasPermission("tllf.load"))
	    {
			plugin.reloadConfig();
			plugin.loadYamls();
			Utilities.sendMessage(sender, "Configs reloaded");
        	return true;
	    }
		if(cmd.getName().equalsIgnoreCase("thcrefresh") && sender.hasPermission("thcl.refresh"))
		{
			checkChannels();
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("thcgroups"))
		{
			return true;
		}
		return false;
	}
	public void checkChannels()
    {
    	List<Town> towns =  TownyUniverse.getDataSource().getTowns();
    	ArrayList<String> townsList = new ArrayList<>();
		List<Nation> nations = TownyUniverse.getDataSource().getNations();
		ArrayList<String> nationsList = new ArrayList<>();
		String[] groups = plugin.permission.getGroups();
		ArrayList<String> groupsList = new ArrayList<>();
		String tempGroup, tempTown, tempNation;
		ArrayList<Boolean> entityCheck = new ArrayList<>();
		for(String s: groups)
		{
			if(s.contains("n:") || s.contains("t:"))
			{
				groupsList.add(s);
			}
			
		}
		if(towns.size() > 0)
		{
			for(int i = 0; i < towns.size(); i++)
			{
				townsList.add(towns.get(i).toString());
			}
		}
		if(nations.size() > 0)
		{
			for(int i = 0; i < nations.size(); i++)
			{
				nationsList.add(nations.get(i).toString());
			}
		}
		if(!groupsList.isEmpty())
		{
			if(plugin.createT)
			{
				for(int i = 0; i < towns.size(); i++)
				{
					tempTown = townsList.get(i);
					for(int j = 0; j < groupsList.size(); j++)
					{
						tempGroup = groupsList.get(j);
						if(tempGroup.substring(2).equalsIgnoreCase(tempTown))
						{
							entityCheck.add(true);
						}
						else
						{
							entityCheck.add(false);
						}
					}
					if(Utilities.allTheSame(entityCheck))
					{
						townCreation(towns.get(i));
					}
				}
				entityCheck.clear();
			}
			if(plugin.createN)
			{
				for(int i = 0; i < nations.size(); i++)
				{
					tempNation = nationsList.get(i);
					for(int j = 0; j < groupsList.size(); j++)
					{
						tempGroup = groupsList.get(j);
						if(tempGroup.substring(2).equalsIgnoreCase(tempNation))
						{
							entityCheck.add(true);
						}
						else
						{
							entityCheck.add(false);
						}
					}
					if(Utilities.allTheSame(entityCheck))
					{
						nationCreation(nations.get(i));
					}
					
				}
				entityCheck.clear();
			}
			
		}
		else if(groupsList.isEmpty())
		{
			for(int i = 0; i < nationsList.size(); i++)
			{
				nationCreation(nations.get(i));
			}
			for(int i = 0; i < townsList.size(); i++)
			{
				townCreation(towns.get(i));
			}
		}
    }
    public void townCreation(Town event) 
	{
    	String entityType = "town";
		Town createdTown = event;
		
		String town = createdTown.toString();
		town.replaceAll("_", "");
		town.replaceAll(".", "");
		
		String nick = town.substring(0, 4).toUpperCase();
		int nickSuffix = 0;
		String nick2 = nick;
		while(plugin.isNickTaken(nick))
		{
			nickSuffix++;
			nick = nick2 + nickSuffix;
		}
		plugin.addToNicks(nick, town); //add the new nickname to the list in the YML file
		
		plugin.createChannel(entityType, town, nick); // create the new channel
		
		plugin.createGroup(entityType, town);
		
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			plugin.permission.groupAdd(plugin.world, "t:"+town, permToAdd);//add each permission to the group
		}
		for(Resident s: createdTown.getResidents())
		{
	    	Player player = plugin.getServer().getPlayer(s.toString());
	    	plugin.permission.playerAddGroup(player, "t:"+town);
			player.performCommand("ch join " + town); //add resident to town chat
		}
		
		
	}
    public void nationCreation(Nation createdNation)
	{
		String nation = createdNation.toString();
		nation.replaceAll("_", "");
		nation.replaceAll(".", "");
		
		String nick = nation.substring(0, 4).toUpperCase();
		int nickSuffix = 0;
		String nick2 = nick;
		while(plugin.isNickTaken(nick))
		{
			nickSuffix++;
			nick = nick2 + nickSuffix;
		}
		plugin.addToNicks(nick, nation);
		
		plugin.createChannel("nation", nation, nick);
		
		plugin.createGroup("nation", nation);
		
		
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + nation; //add the channel name to the end of each permission to be added
			plugin.permission.groupAdd(plugin.world, "n:" + nation, permToAdd);
		}
		List<Town> towns = createdNation.getTowns();
		
		for(Town s: towns)
		{
			for(Resident r: s.getResidents())
			{
				Player player = plugin.getServer().getPlayer(r.toString());
				plugin.permission.playerAddGroup(player, "n:"+nation); //add the player to the group
				player.performCommand("ch join " + nation); //add residents to nation chat
			}
			
		}
	}
}
