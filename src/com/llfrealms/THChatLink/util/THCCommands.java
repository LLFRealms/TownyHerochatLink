package com.llfrealms.THChatLink.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
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
	private String create = "ch create entity nick"; //string holding the command for creating a new channel
	private String color = "ch set entity color words"; //string holding the command for changing the color of the channel where "words" is the color from the config
	private String format = "ch set entity format words"; //command for changing the format of the channel
	private String createGroup = "perm group entity create"; // command to create a group for the town/nation where words is the town/nation name
	private String addPlayer = "perm group entity add player";
	private String addGroupPerm = "perm group entity set words";
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(cmd.getName().equalsIgnoreCase("thcsave") && sender.hasPermission("tllf.save"))
	    {
			plugin.saveConfig();
        	Utilities.sendMessage(sender, "Config saved");
        	return true;
	    }
		if(cmd.getName().equalsIgnoreCase("thcload")  && sender.hasPermission("tllf.load"))
	    {
			plugin.reloadConfig();
			Utilities.sendMessage(sender, "Config reloaded");
        	return true;
	    }
		if(cmd.getName().equalsIgnoreCase("thcrefresh") && sender.hasPermission("thcl.refresh"))
		{
			checkChannels();
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("thcgroups"))
		{
			checkGroups(sender);
			return true;
		}
		return false;
	}
	public void checkGroups(CommandSender sender)
	{
		String[] groups = plugin.permission.getGroups();
		ArrayList<String> entityGroups = new ArrayList<>();
		for(String s: groups)
		{
			if(s.contains("n:") || s.contains("t:"))
			{
				entityGroups.add(s);
			}
		}
		Utilities.sendMessage(sender, "&aGroups List:");
		for(int i = 0; i < entityGroups.size(); i++)
		{
			Utilities.sendMessage(sender, "&e" + entityGroups.get(i));
		}
	}
	public void checkChannels()
    {
    	List<Town> towns =  TownyUniverse.getDataSource().getTowns();
    	ArrayList<String> townsList = new ArrayList<>();
		List<Nation> nations = TownyUniverse.getDataSource().getNations();
		ArrayList<String> nationsList = new ArrayList<>();
		Set<String> groups = THCLink.service.getAllGroups();
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
					Utilities.sendMessage(plugin.consoleMessage, "I derp: " + i);
					tempTown = townsList.get(i);
					for(int j = 0; j < groupsList.size(); j++)
					{
						Utilities.sendMessage(plugin.consoleMessage, "J derp: " + j);
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
						townCreation(towns.get(i), towns.get(i).getMayor().toString());
						Utilities.sendMessage(plugin.consoleMessage, "&7It should have just created a channel for: " + tempTown);
					}
				}
				entityCheck.clear();
			}
			if(plugin.createN)
			{
				for(int i = 0; i < nations.size(); i++)
				{
					Utilities.sendMessage(plugin.consoleMessage, "I derp: " + i);
					tempNation = nationsList.get(i);
					for(int j = 0; j < groupsList.size(); j++)
					{
						Utilities.sendMessage(plugin.consoleMessage, "J derp: " + j);
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
						nationCreation(nations.get(i), nations.get(i).getCapital());
						Utilities.sendMessage(plugin.consoleMessage, "&7It should have just created a channel for: " + tempNation);
					}
					
				}
				entityCheck.clear();
			}
			
		}
		else if(groupsList.isEmpty())
		{
			for(int i = 0; i < nationsList.size(); i++)
			{
				nationCreation(nations.get(i), nations.get(i).getCapital());
			}
			for(int i = 0; i < townsList.size(); i++)
			{
				townCreation(towns.get(i), towns.get(i).getMayor().toString());
			}
		}
    }
    public void townCreation(Town event, String mayor) 
	{
		Town createdTown = event;
		String town = createdTown.toString();
		town.replaceAll("_", "");
		
		String nick = town.substring(0, 4).toUpperCase();
		int nickSuffix = 0;
		String nick2 = nick;
		while(plugin.isNickTaken(nick))
		{
			nickSuffix++;
			nick = nick2 + nickSuffix;
		}
		plugin.addRecord("INSERT INTO " + plugin.pluginname + " VALUES(\'"+nick+"\', \'"+town+"\');");
		String command = create.replace("entity", town); //replace "channel" with the town name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("towns.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("entity", town);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("towns.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("entity", town);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("entity", "t:"+town); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		command = addPlayer.replace("entity", "t:" + town);
		command = command.replace("player", mayor);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		String permToAdd, addPerm;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			addPerm = addGroupPerm.replace("entity", "t:"+town);
			addPerm = addPerm.replace("words", permToAdd);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), addPerm); //add perms to town group
		}
		Player commandPlayer = plugin.getServer().getPlayer(mayor);
		commandPlayer.performCommand("ch join " + town); //add mayor to town chat
		
		
	}
    public void nationCreation(Nation createdNation, Town creatingTown)
	{
		String nation = createdNation.toString();
		nation.replaceAll("_", "");
		
		String nick = nation.substring(0, 4).toUpperCase();
		int nickSuffix = 0;
		String nick2 = nick;
		while(plugin.isNickTaken(nick))
		{
			nickSuffix++;
			nick = nick2 + nickSuffix;
		}
		plugin.addRecord("INSERT INTO " + plugin.pluginname + " VALUES(\'"+nick+"\', \'"+nation+"\');");
		String command = create.replace("entity", nation); //replace "channel" with the nation name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the nation name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("nations.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("entity", nation);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("nations.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("entity", nation);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("entity", "n:" + nation); //change the command to creating a group for the nation
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the n
		
		List<Resident> capital = creatingTown.getResidents();
		
		for(Resident s: capital)
		{
			String player = s.toString();
			command = addPlayer.replace("entity", "n:" + nation);
			command = command.replace("player", player);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			Player commandPlayer = plugin.getServer().getPlayer(s.toString());
			commandPlayer.performCommand("ch join " + nation); //add residents to nation chat
		}
		String permToAdd, addPerm;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + nation; //add the channel name to the end of each permission to be added
			addPerm = addGroupPerm.replace("entity", "n:" + nation);
			addPerm = addPerm.replace("words", permToAdd);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), addPerm); //add the channel name to the end of each permission to be added
		}
	}
}
