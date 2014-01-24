package com.llfrealms.THChatLink.Listeners;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.llfrealms.THChatLink.util.Utilities;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class THCTownListeners implements Listener {
	
	private THCLink plugin;
	public THCTownListeners(THCLink plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	private String create = "ch create town nick"; //string holding the command for creating a new channel
	private String delete = "ch remove town"; //string holding the command for deleting a channel
	private String color = "ch set town color words"; //string holding the command for changing the color of the channel where "words" is the color from the config
	private String format = "ch set town format words"; //command for changing the format of the channel
	private String createGroup = "perm group t:town create"; // command to create a group for the town/nation where words is the town/nation name
	private String deleteGroup = "perm group t:town purge";
	private String addPlayer = "perm group t:town add player";
	private String removePlayer = "perm group t:town remove player";
	private String addGroupPerm = "perm group t:town set words";
	
	
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
		String command = create.replace("town", town); //replace "channel" with the town name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("towns.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("town", town);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("towns.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("town", town);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("town", town); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		command = addPlayer.replace("town", town);
		command = command.replace("player", mayor);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		String permToAdd, addPerm;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			addPerm = addGroupPerm.replace("town", town);
			addPerm = addPerm.replace("words", permToAdd);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), addPerm); //add perms to town group
			//THCLink.permset.groupAdd("world","t:"+town , permToAdd);//Vault add perm to group
		}
		Player commandPlayer = plugin.getServer().getPlayer(mayor);
		commandPlayer.performCommand("ch join " + town); //add mayor to town chat
		
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownDeletion(DeleteTownEvent event) 
	{
		String town = event.getTownName();
		town.replaceAll("_", "");
		
		String command = delete.replace("town", town); //replace "channel" with the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		plugin.deleteRecord("DELETE FROM " + plugin.pluginname + " WHERE entity = \'"+town+"\';");
		command = deleteGroup.replace("town", town); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownAddResident(TownAddResidentEvent event)
	{
		ArrayList<Boolean> isNewTown = new ArrayList<>();
		Resident resident = event.getResident();
		Town newTown = event.getTown();
		String town = newTown.toString();
		String[] groups = plugin.permission.getGroups();
		ArrayList<String> groupsList = new ArrayList<>();
		ArrayList<String> townList = new ArrayList<>();
		for(String s: groups)
		{
			groupsList.add(s);
		}
		for(String s: groupsList)
		{
			if(s.contains("t:"))
			{
				townList.add(s);
			}
		}
		if(!townList.isEmpty())
		{
			for(int i = 0; i<townList.size(); i++)
			{
				if(townList.get(i).equals("t:"+town))
				{
					isNewTown.add(false);
//					String command;
//					
					String player = resident.toString();
//					command = addPlayer.replace("town", town);
//					command = command.replace("player", player);
//					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
					Player commandPlayer = plugin.getServer().getPlayer(player);
					plugin.permission.playerAddGroup(commandPlayer, "t:"+town);
					commandPlayer.performCommand("ch join " + town); //add residents to town chat
				}
				else
				{
					isNewTown.add(true);
				}
			}
			if(Utilities.allTheSame(isNewTown) && isNewTown.size() > 1)
			{
				townCreation(newTown, resident.toString());
			}
		}
		else
		{
			townCreation(newTown, resident.toString());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownRemoveResident(TownRemoveResidentEvent event) 
	{
		String town = event.getTown().toString();

		String removedPlayer = event.getResident().toString(); //string to hold the creators name
		String command = removePlayer.replace("town", town);
		command = command.replace("player", removedPlayer);
		Player commandPlayer = plugin.getServer().getPlayer(removedPlayer);
		commandPlayer.performCommand("ch leave " + town); //remove player from town chat
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //remove player from group
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownRename(RenameTownEvent event) 
	{
		Town newName = event.getTown();
		String oldName = event.getOldName();
		List<Resident> players = newName.getResidents();
		
		String command = "perm group t:" + oldName + " rename t:" + newName; 
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //rename a group for the new town name
		
		String town = newName.toString();
		town.replaceAll("_", "");
		
		String nick = town.substring(0, 4).toUpperCase();
		
		command = create.replace("town", town); //replace "channel" with the new town name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("towns.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("town", town);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("towns.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("town", town);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = delete.replace("town", town); //replace "channel" with the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //delete the old channel
		for(Resident s: players)
		{
			String player = s.toString();
			command = addPlayer.replace("town", town);
			command = command.replace("player", player);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			Player commandPlayer = plugin.getServer().getPlayer(s.toString());
			commandPlayer.performCommand("ch join " + town); //add residents to nation chat
		}
		
	}
	

}
