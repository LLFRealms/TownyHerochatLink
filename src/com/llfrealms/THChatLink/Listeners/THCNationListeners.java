package com.llfrealms.THChatLink.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.llfrealms.THChatLink.util.Utilities;
import com.palmergames.bukkit.towny.event.NationAddTownEvent;
import com.palmergames.bukkit.towny.event.NationRemoveTownEvent;
import com.palmergames.bukkit.towny.event.RenameNationEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class THCNationListeners  implements Listener {
	private THCLink plugin;
	public THCNationListeners(THCLink plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	private String entityType = "nation";
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationAddTown(NationAddTownEvent event)
	{
		ArrayList<Boolean> newNation = new ArrayList<>();
		Town addedTown = event.getTown();
		String nation = event.getNation().toString();
		String[] groups = plugin.permission.getGroups();
		ArrayList<String> groupsList = new ArrayList<>();
		ArrayList<String> nationList = new ArrayList<>();
		for(String s: groups)
		{
			groupsList.add(s);
		}
		for(String s: groupsList)
		{
			if(s.contains("n:"))
			{
				nationList.add(s);
			}
		}
		if(!nationList.isEmpty())
		{
			for(int i = 0; i<nationList.size(); i++)
			{
				if(nationList.get(i).equals("n:"+nation))
				{
					newNation.add(false);
					List<Resident> addedTownRes = addedTown.getResidents();
					//String command;
					
					for(Resident s: addedTownRes)
					{
						plugin.permission.playerAddGroup(plugin.world, s.toString(), "n:"+nation);
						plugin.joinChannel(s.toString(), nation);
					}
				}
				else
				{
					newNation.add(true);
				}
			}
			if(Utilities.allTheSame(newNation) && newNation.size() > 1)
			{
				nationCreation(event.getNation(), addedTown);
			}
		}
		else
		{
			nationCreation(event.getNation(), addedTown);
		}
	}

	public void nationCreation(Nation createdNation, Town creatingTown)
	{
		String nation = createdNation.toString();
		nation.replaceAll("_", "");
		
		String nick = nation.substring(0, 4).toUpperCase();
		
		plugin.createChannel(entityType, nation, nick); //create the channel for the town
		
		plugin.createGroup(entityType, nation);
		
		List<Resident> capital = creatingTown.getResidents();
		
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + nation; //add the channel name to the end of each permission to be added
			plugin.permission.groupAdd(plugin.world,"n:"+nation , permToAdd);
		}
		for(Resident s: capital)
		{
			plugin.permission.playerAddGroup(plugin.world, s.toString(), "n:"+nation);
			plugin.joinChannel(s.toString(), nation);
		}
		
	}
	
	
	public void nationDelete(String nation)
	{
		nation.replaceAll("_", "");
		nation.replaceAll(".", "");
		
		plugin.deleteChannel(nation); //delete the channel
		plugin.deleteGroup(entityType, nation); //delete the nation
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationRemoveTown(NationRemoveTownEvent event)
	{
		Town removedTown = event.getTown();
		String nation = event.getNation().toString();
		String capital = event.getNation().getCapital().toString();
		String town = removedTown.toString();
		if(town.equalsIgnoreCase(capital))
		{
			nationDelete(nation);
		}
		else
		{
			List<Resident> removedTownRes = removedTown.getResidents();
			
			for(Resident s: removedTownRes)
			{
				plugin.leaveChannel(s.toString(), nation);
				
				plugin.permission.playerRemoveGroup(plugin.world, s.toString(), "n:"+nation);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationRename(RenameNationEvent event) 
	{
		Nation nation = event.getNation();
		String newName = event.getNation().toString();
		String oldName = event.getOldName();
		
		plugin.renameGroup(entityType, oldName, newName);
		
		String permToAdd;
		for(int i = 0; i<plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + newName; //add the channel name to the end of each permission to be added
			plugin.permission.groupAdd(plugin.world,"n:"+nation , permToAdd);
		}
		List<Town> towns = nation.getTowns();
		for(Town s: towns)
		{
			for(Resident r: s.getResidents())
			{
				plugin.permission.playerAddGroup(plugin.world, r.toString(), "n:"+newName); //add the player to the group
				plugin.joinChannel(r.toString(), newName);
			}
			
		}
		
	}
	
}
