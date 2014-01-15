package com.llfrealms.THChatLink.util;


import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.palmergames.bukkit.towny.event.DeleteNationEvent;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewNationEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownAddResidentEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class THCListeners implements Listener {
	
	private THCLink plugin;
	public THCListeners(THCLink plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	String create = "ch create channel nick"; //string holding the command for creating a new channel
	String delete = "ch remove channel"; //string holding the command for deleting a channel
	String color = "ch set channel color words"; //string holding the command for changing the color of the channel where "words" is the color from the config
	String format = "ch set channel format words"; //command for changing the format of the channel
	String addGroup = "perm group g:words1 add words2"; // command to add a group for the town/nation to the player words is the town/nation name
	String createGroup = "perm group g:words create"; // command to create a group for the town/nation where words is the town/nation name
	String addPerms = "perm group g:words set "; //command to set a permission for the town/nation group to true where words is the town/nation name
	String deleteGroup = "perm group g:words purge";
	String world = plugin.getConfig().getString("createIn.world");
	
	
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
		THCLink.permset.playerAddGroup(mayor, "g:"+town); //vault add player to group
		
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + mayor + " ch join " + town); //add mayor to town chat
		
		//command = addPerms.replace("words", town);
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			//Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command2); //add perms to town group
			THCLink.permset.groupAdd(world,"g:"+town , permToAdd);//Vault add perm to group
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
		String group = "g:" + town;
		
		THCLink.permset.playerAddGroup(addedPlayer, group); //add new resident to town group
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownRemoveResident(TownRemoveResidentEvent event) 
	{
		String town = event.getTown().toString();
		Player removedPlayer = (Player) event.getResident();
		String group = "g:" + town;
		
		THCLink.permset.playerRemoveGroup(removedPlayer, group); //remove removed resident from town group
		
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationCreation(NewNationEvent event)
	{
		Nation createdNation = event.getNation();
		String nation = createdNation.toString();
		nation.replaceAll("_", "");
		
		String nick = nation.substring(0, 4).toUpperCase();
		
		String command = create.replace("channel", nation); //replace "channel" with the nation name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the nation name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("towns.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("channel", nation);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("towns.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("channel", nation);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("words", nation); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		List<Resident> creatingTown = createdNation.getCapital().getResidents();
		
		for(Resident s: creatingTown)
		{
			THCLink.permset.playerAddGroup((Player) s, "g:" + nation);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + (Player) s + " ch join " + nation); //add mayor to town chat
		}
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + nation; //add the channel name to the end of each permission to be added
			THCLink.permset.groupAdd(world,"g:"+nation , permToAdd);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationDeletion(DeleteNationEvent event)
	{
		String nation = event.getNationName();
		nation.replaceAll("_", "");
		
		String command = delete.replace("channel", nation); //replace "channel" with the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel

		command = deleteGroup.replace("words", nation); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
	}
	

}
