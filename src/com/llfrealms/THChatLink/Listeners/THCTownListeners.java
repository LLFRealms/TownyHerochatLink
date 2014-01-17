package com.llfrealms.THChatLink.Listeners;


import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.llfrealms.THChatLink.util.Utilities;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
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
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownCreation(NewTownEvent event) 
	{
		Town createdTown = event.getTown();
		String town = createdTown.toString();
		town.replaceAll("_", "");
		
		String nick = town.substring(0, 4).toUpperCase();
		
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
		
		String mayor = createdTown.getMayor().toString(); //string to hold the creators name
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
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + mayor + " ch join " + town); //add mayor to town chat
		
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownDeletion(DeleteTownEvent event) 
	{
		String town = event.getTownName();
		town.replaceAll("_", "");
		
		String command = delete.replace("town", town); //replace "channel" with the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel

		command = deleteGroup.replace("town", town); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownAddResident(TownAddResidentEvent event) 
	{
		String town = event.getTown().toString();

		String addedPlayer = event.getResident().toString(); //string to hold the creators name
		Utilities.sendMessage(plugin.consoleMessage, "&5Adding " + addedPlayer + " to the town group and channel of " + town);
		String command = addPlayer.replace("town", town);
		command = command.replace("player", addedPlayer);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + addedPlayer + " ch join " + town); //add mayor to town chat
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownRemoveResident(TownRemoveResidentEvent event) 
	{
		String town = event.getTown().toString();

		String removedPlayer = event.getResident().toString(); //string to hold the creators name
		Utilities.sendMessage(plugin.consoleMessage, "&5Removing " + removedPlayer + " to the town group and channel of " + town);
		String command = removePlayer.replace("town", town);
		command = command.replace("player", removedPlayer);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + removedPlayer + " ch leave " + town); //remove player from town chat
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
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + player + " ch join " + town); //add residents to nation chat
		}
		
	}
	

}
