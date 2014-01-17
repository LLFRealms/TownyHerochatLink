package com.llfrealms.THChatLink.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.llfrealms.THChatLink.THCLink;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;


public class Utilities {
	private static THCLink plugin;

	public static ConsoleCommandSender consoleMessage = Bukkit.getConsoleSender();
	
    public static String colorChat(String msg) 
    {
    	return msg.replace('&', (char) 167);
    }
    public static String getFinalArg(final String[] args, final int start)
    {
            final StringBuilder bldr = new StringBuilder();
            for (int i = start; i < args.length; i++)
            {
                    if (i != start)
                    {
                            bldr.append(" ");
                    }
                    bldr.append(args[i]);
            }
            return bldr.toString();
    }
    public static boolean sendMessage(CommandSender p, String message)
    {
        if (message ==null || message.isEmpty()) return true;
        if (message.contains("\n"))
                return sendMultilineMessage(p,message);
        if (p instanceof Player){
                if (((Player) p).isOnline())
                        p.sendMessage(colorChat(message));
        } else {
                p.sendMessage(colorChat(message));
        }
        return true;
    }
    public static boolean allTheSame(ArrayList<Boolean> l)
    {
    	for(int i = 1; i < l.size(); i++)
    	{
    		if(l.get(i) != l.get(0))
    		{
    			return false;
    		}
    	}
    	return true;
    }
    public static void checkChannels()
    {
    	List<Town> towns =  TownyUniverse.getDataSource().getTowns();
    	ArrayList<String> townsList = new ArrayList<>();
		List<Nation> nations = TownyUniverse.getDataSource().getNations();
		ArrayList<String> nationsList = new ArrayList<>();
		Set<String> groups = THCLink.service.getAllGroups();
		ArrayList<String> groupsList = new ArrayList<>();
		@SuppressWarnings("unused")
		String tempGroup, tempTown, tempNation;
		for(String s: groups)
		{
			groupsList.add(s);
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
		for(int i = 0; i < groupsList.size(); i++)
		{
			sendMessage(consoleMessage, "I derp: " + i);
			tempGroup = groupsList.get(i);
			for(int j = 0; j < towns.size(); j++)
			{
				sendMessage(consoleMessage, "J derp: " + j);
				tempTown = townsList.get(j);
				if(tempGroup.substring(2).equalsIgnoreCase(tempTown))
				{
					sendMessage(consoleMessage, "&aGroup "+ tempGroup + " aka '"+tempGroup.substring(2) + "' does not equal " + tempTown);
				}
				else
				{
					createTownChannel(townsList.get(j)); //else create the channel
					sendMessage(consoleMessage, "&7It should have just created a channel for: " + tempTown);
				}
			}
			
		}
    }
    public static void createTownChannel(String channel)
    {
    	Town towns = new Town(channel);
    	String create = "ch create town nick"; //string holding the command for creating a new channel
    	String color = "ch set town color words"; //string holding the command for changing the color of the channel where "words" is the color from the config
    	String format = "ch set town format words"; //command for changing the format of the channel
    	String createGroup = "perm group t:town create"; // command to create a group for the town/nation where words is the town/nation name
    	
		channel.replaceAll("_", "");
		
		String nick = channel.substring(0, 4).toUpperCase();
		
		String command = create.replace("town", channel); //replace "channel" with the town name
		command = command.replace("nick", nick); //replace "nick" with the first four letters of the town name
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
		
		command = color.replace("words", plugin.townsColor); //change the command to changing the color and change "words" to the color in the config
		command = command.replace("town", channel);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the color to the color from the config
		
		command = format.replace("words", plugin.townsFormat); //change the command to setting the format and change "words" to the format in the config
		command = command.replace("town", channel);
		command = command.replaceAll("<", "{");
		command = command.replaceAll(">", "}");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //change the format to the one defined in the config
		
		command = createGroup.replace("town", channel); //change the command to creating a group for the town
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
		
		List<Resident> addedTownRes = towns.getResidents();
		
		for(Resident s: addedTownRes)
		{
			Player player = plugin.getServer().getPlayer(s.toString());
			THCLink.permset.playerAddGroup(player, "t:" + channel);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sudo " + player + " ch join " + channel); //add residents to nation chat
		}
		
		String permToAdd;
		for(int i = 0; i < plugin.perms.size(); i++)
		{
			permToAdd = plugin.perms.get(i) + "." + channel; //add the channel name to the end of each permission to be added
			THCLink.permset.groupAdd("world","t:"+channel , permToAdd);//Vault add perm to group
		}
    }
    public static boolean sendMultilineMessage(CommandSender p, String message)
    {
        if (message ==null || message.isEmpty()) return true;
        String[] msgs = message.split("\n");
        for (String msg: msgs){
                if (p instanceof Player){
                        if (((Player) p).isOnline())
                                p.sendMessage(colorChat(msg));
                } else {
                        p.sendMessage(colorChat(msg));
                }
        }
        return true;
    }
}
