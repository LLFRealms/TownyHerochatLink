package com.llfrealms.THChatLink.util;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
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
	private String world = plugin.getConfig().getString("createIn.world");
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownCreation(NewTownEvent event) 
	{
		Town createdTown = event.getTown();
		String town = createdTown.toString();
		town.replaceAll("_", "");
		
		String nick = town.substring(0, 4).toUpperCase();
		
		String command = create.replace("channel", town); //replace "channel" with the town name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("towns.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("channel", town);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("towns.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("channel", town);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("words", town); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		Player mayor = ((Player) createdTown.getMayor()); //string to hold the creators name
		THCLink.permset.playerAddGroup(mayor, "t:"+town); //vault add player to group
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + mayor + " ch join " + town); //add mayor to town chat
		
		//command = addPerms.replace("words", town);
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			//Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command2); //add perms to town group
			THCLink.permset.groupAdd(world,"t:"+town , permToAdd);//Vault add perm to group
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownDeletion(DeleteTownEvent event) 
	{
		String town = event.getTownName();
		town.replaceAll("_", "");
		
		String command = delete.replace("channel", town); //replace "channel" with the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel

		command = deleteGroup.replace("words", town); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownAddResident(TownAddResidentEvent event) 
	{
		String town = event.getTown().toString();
		Player addedPlayer = (Player) event.getResident();
		String group = "t:" + town;
		
		THCLink.permset.playerAddGroup(addedPlayer, group); //add new resident to town group
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + addedPlayer + " ch join " + town); //add mayor to town chat
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownRemoveResident(TownRemoveResidentEvent event) 
	{
		String town = event.getTown().toString();
		Player removedPlayer = (Player) event.getResident();
		String group = "t:" + town;
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + removedPlayer + " ch join " + town); //add mayor to town chat
		THCLink.permset.playerRemoveGroup(removedPlayer, group); //remove removed resident from town group
		
	}
	

}
