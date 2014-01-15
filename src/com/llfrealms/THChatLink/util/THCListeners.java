package com.llfrealms.THChatLink.util;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.llfrealms.THChatLink.THCLink;
import com.palmergames.bukkit.towny.event.NewTownEvent;
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
	String addGroup = "perm player words addgroup g:town"; // command to add a group for the town/nation to the player words is the town/nation name
	String parentGroup = "perm groups g:town setparents g:nation";
	String createGroup = "perm group g:words create"; // command to create a group for the town/nation where words is the town/nation name
	String addPerms = "perm group g:words set "; //command to set a permission for the town/nation group to true where words is the town/nation name
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onTownCreation(NewTownEvent event) 
	{
		Town createdTown = event.getTown();
		String town = createdTown.toString();
		town.replaceAll("_", "");
		
		String nick = town.substring(0, 4);
		
		String command = create.replace("channel", town); //replace "channel" with the town name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.getConfig().getString("towns.color")); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("channel", town);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.getConfig().getString("towns.format")); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("channel", town);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		Utilities.sendMessage(plugin.consoleMessage, "&f" + command);
		
		command = createGroup.replace("words", town); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		String mayor = createdTown.getMayor().toString(); //string to hold the creators name
		command = addGroup.replace("town", town); //set command to adding mayor to the towns group
		command = command.replace("words", mayor);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //add mayor to town group
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + mayor + " ch join " + town); //add mayor to town chat
		
		command = addPerms.replace("words", town);
		String command2;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			command2 = command + plugin.perms.get(i) + "." + town; //add the channel name to the end of each permission to be added
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command2); //add perms to town group
		}
	}
	
	

}
