package com.llfrealms.THChatLink.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.llfrealms.THChatLink.util.Utilities;
import com.palmergames.bukkit.towny.event.DeleteNationEvent;
import com.palmergames.bukkit.towny.event.NationAddTownEvent;
import com.palmergames.bukkit.towny.event.NationRemoveTownEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
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
	private String create = "ch create nation nick"; //string holding the command for creating a new channel
	private String delete = "ch remove nation"; //string holding the command for deleting a channel
	private String color = "ch set nation color words"; //string holding the command for changing the color of the channel where "words" is the color from the config
	private String format = "ch set nation format words"; //command for changing the format of the channel
	private String createGroup = "perm group n:nation create"; // command to create a group for the town/nation where words is the town/nation name
	private String deleteGroup = "perm group n:nation purge";
	private String addGroupPerm = "perm group n:nation set words";
	private String addPlayer = "perm group n:nation add player";
	private String removePlayer = "perm group n:nation remove player";

	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationAddTown(NationAddTownEvent event)
	{
		Town addedTown = event.getTown();
		String nation = event.getNation().toString();
		Set<String> groups = THCLink.service.getAllGroups();
		ArrayList<String> groupsList = new ArrayList<>();
		Utilities.sendMessage(plugin.consoleMessage, "&aTown of "+addedTown.toString()+" added to the Nation of " + nation);
		for(String s: groups)
		{
			groupsList.add(s);
		}
		for(int i = 0; i<groupsList.size(); i++)
		{
			if(groupsList.get(i).equals("n:"+nation))
			{
				List<Resident> addedTownRes = addedTown.getResidents();
				String command;
				
				for(Resident s: addedTownRes)
				{
					String player = s.toString();
					command = addPlayer.replace("nation", nation);
					command = command.replace("player", player);
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + player + " ch join " + nation); //add residents to nation chat
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationCreation(NewNationEvent event)
	{
		Nation createdNation = event.getNation();
		String nation = createdNation.toString();
		Utilities.sendMessage(plugin.consoleMessage, "&aNew Nation of "+nation+"created");
		nation.replaceAll("_", "");
		
		String nick = nation.substring(0, 4).toUpperCase();
		
		String command = create.replace("nation", nation); //replace "channel" with the nation name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the nation name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("nations.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("nation", nation);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("nations.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("nation", nation);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("nation", nation); //change the command to creating a group for the nation
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the n
		
		List<Resident> creatingTown = createdNation.getCapital().getResidents();
		
		for(Resident s: creatingTown)
		{
			String player = s.toString();
			command = addPlayer.replace("nation", nation);
			command = command.replace("player", player);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + player + " ch join " + nation); //add residents to nation chat
		}
		String permToAdd, addPerm;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + nation; //add the channel name to the end of each permission to be added
			addPerm = addGroupPerm.replace("town", nation);
			addPerm = addPerm.replace("words", permToAdd);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), addPerm); //add the channel name to the end of each permission to be added
			THCLink.permset.groupAdd("world","n:"+nation , permToAdd);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationDeletion(DeleteNationEvent event)
	{
		String nation = event.getNationName();
		Utilities.sendMessage(plugin.consoleMessage, "&aNation of "+nation+"deleted");
		nation.replaceAll("_", "");
		
		String command = delete.replace("channel", nation); //replace "channel" with the nation name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //delete the channel

		command = deleteGroup.replace("words", nation); //change the command to deleting a group for the nation
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the nation
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationRemoveTown(NationRemoveTownEvent event)
	{
		Town removedTown = event.getTown();
		String nation = event.getNation().toString();
		Utilities.sendMessage(plugin.consoleMessage, "&aTown of "+removedTown.toString()+" removed from the Nation of " + nation);
		List<Resident> removedTownRes = removedTown.getResidents();
		
		String command;
		
		for(Resident s: removedTownRes)
		{
			String player = s.toString();
			command = removePlayer.replace("nation", nation);
			command = command.replace("player", player);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + player + " ch leave " + nation); //add residents to nation chat
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationRename(RenameNationEvent event) 
	{
		Nation newName = event.getNation();
		String oldName = event.getOldName();
		
		String command = "perm group n:" + oldName + " rename n:" + newName; 
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //rename a group for the new town name
	}
	
}