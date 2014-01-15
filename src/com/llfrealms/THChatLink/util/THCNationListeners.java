package com.llfrealms.THChatLink.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
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
	private String create = "ch create town nick"; //string holding the command for creating a new channel
	private String delete = "ch remove town"; //string holding the command for deleting a channel
	private String color = "ch set town color words"; //string holding the command for changing the color of the channel where "words" is the color from the config
	private String format = "ch set town format words"; //command for changing the format of the channel
	private String createGroup = "perm group n:town create"; // command to create a group for the town/nation where words is the town/nation name
	private String deleteGroup = "perm group n:town purge";
	private String world = plugin.getConfig().getString("createIn.world");

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
		
		command = color.replace("words", plugin.getConfig().getString("nations.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("channel", nation);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("nations.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("channel", nation);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("words", nation); //change the command to creating a group for the nation
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the n
		
		List<Resident> creatingTown = createdNation.getCapital().getResidents();
		
		for(Resident s: creatingTown)
		{
			THCLink.permset.playerAddGroup((Player) s, "n:" + nation);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + (Player) s + " ch join " + nation); //add residents to nation chat
		}
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + nation; //add the channel name to the end of each permission to be added
			THCLink.permset.groupAdd(world,"n:"+nation , permToAdd);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationDeletion(DeleteNationEvent event)
	{
		String nation = event.getNationName();
		nation.replaceAll("_", "");
		
		String command = delete.replace("channel", nation); //replace "channel" with the nation name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //delete the channel

		command = deleteGroup.replace("words", nation); //change the command to deleting a group for the nation
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the nation
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationAddTown(NationAddTownEvent event)
	{
		Town addedTown = event.getTown();
		Nation nation = event.getNation();
		List<Resident> addedTownRes = addedTown.getResidents();
		
		for(Resident s: addedTownRes)
		{
			THCLink.permset.playerAddGroup((Player) s, "n:" + nation);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + (Player) s + " ch join " + nation); //add residents to nation chat
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onNationRemoveTown(NationRemoveTownEvent event)
	{
		Town removedTown = event.getTown();
		Nation nation = event.getNation();
		List<Resident> removedTownRes = removedTown.getResidents();
		
		for(Resident s: removedTownRes)
		{
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + (Player) s + " ch leave " + nation); //remove residents from the nation chat
			THCLink.permset.playerRemoveGroup((Player) s, "n:" + nation);
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
