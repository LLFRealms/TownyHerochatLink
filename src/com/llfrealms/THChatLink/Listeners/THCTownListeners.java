package com.llfrealms.THChatLink.Listeners;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.llfrealms.THChatLink.util.PermissionsPlugin;
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
	private String permToAdd, entityType = "town";
	private PermissionsPlugin permPlug = plugin.permPluginCheck();
	
	
	public void townCreation(Town event, String mayor) 
	{
		Player player = plugin.getServer().getPlayer(mayor);
		
		Town createdTown = event;
		
		String town = createdTown.toString();
		
		town.replaceAll("_", ""); //get rid of invalid characters for channel creation
		town.replaceAll(".", ""); //get rid of invalid characters for channel creation
				
		String group = "t:" + town;
		
		//nickname stuff
		String nick = town.substring(0, 4).toUpperCase();
		int nickSuffix = 0;
		String nick2 = nick;
		while(plugin.isNickTaken(nick))
		{
			nickSuffix++;
			nick = nick2 + nickSuffix;
		}
		
		plugin.addToNicks(nick, town); //add the new nickname to the list in the YML file
		
		plugin.createChannel(entityType, town, nick); //create the channel for the town
				
		permPlug.createGroup(entityType, town); // create the group for the town
		
		plugin.permission.playerAddGroup(player, group); // add the player to thr group
		
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			plugin.permission.groupAdd(plugin.world,"t:"+town , permToAdd);//add perm to group
		}
		
		player.performCommand("ch join " + town); //add player to town chat
		
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownDeletion(DeleteTownEvent event) 
	{
		String town = event.getTownName();
		town.replaceAll("_", ""); //get rid of invalid characters for channel creation
		town.replaceAll(".", ""); //get rid of invalid characters for channel creation
		
		plugin.deleteChannel(town); //delete the channel
		
		plugin.removeFromNicks(town);
		
		permPlug.deleteGroup(entityType, town);
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
					
					Player commandPlayer = plugin.getServer().getPlayer(player);
					
					plugin.permission.playerAddGroup(commandPlayer, "t:"+town); //add player to town group
					
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
		String group = "t:"+town;
		String removedPlayer = event.getResident().toString(); //string to hold the creators name
		
		Player commandPlayer = plugin.getServer().getPlayer(removedPlayer); //Player for making the player leave the channel
		
		commandPlayer.performCommand("ch leave " + town); //remove player from town chat
		plugin.permission.playerRemoveGroup(plugin.world, removedPlayer, group); //remove player from the group
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownRename(RenameTownEvent event) 
	{
		Town newName = event.getTown();
		String oldName = event.getOldName();
		List<Resident> players = newName.getResidents();
		
		String town = newName.toString();
		town.replaceAll("_", "");
		town.replaceAll(".", "");
		oldName.replaceAll("_", "");
		oldName.replaceAll(".", "");
		
		plugin.removeFromNicks(oldName);
		String nick = town.substring(0, 4).toUpperCase();
		//nickname stuff
		int nickSuffix = 0;
		String nick2 = nick;
		while(plugin.isNickTaken(nick))
		{
			nickSuffix++;
			nick = nick2 + nickSuffix;
		}
		
		plugin.addToNicks(nick, town); //add the new nickname to the list in the YML file
		
		plugin.createChannel(entityType, town, nick); //create the new channel for the town
		
		plugin.deleteChannel(oldName);//delete the old channel
		permPlug.deleteGroup("town", oldName); //delete the old group
		
		permPlug.createGroup(entityType, town); // create the new group for the town
		

		
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			plugin.permission.groupAdd(plugin.world,"t:"+town , permToAdd);//add perm to group
		}
		
		for(Resident s: players)
		{
			Player player = plugin.getServer().getPlayer(s.toString());
			
			plugin.permission.playerAddGroup(player, "t:" + town); // add the player to thr group
			player.performCommand("ch join " + town); //add residents to nation chat
		}
		
	}
	

}
