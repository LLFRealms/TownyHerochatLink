package com.llfrealms.THChatLink.util;

import org.bukkit.command.*;

import com.llfrealms.THChatLink.THCLink;
import com.llfrealms.THChatLink.util.Utilities;

public class THCCommands  implements CommandExecutor 
{
	private THCLink plugin;
	public THCCommands(THCLink plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(cmd.getName().equalsIgnoreCase("thcsave") && sender.hasPermission("tllf.save"))
	    {
			plugin.saveConfig();
        	Utilities.sendMessage(sender, "Config saved");
        	return true;
	    }
		if(cmd.getName().equalsIgnoreCase("thcload")  && sender.hasPermission("tllf.load"))
	    {
			plugin.reloadConfig();
			Utilities.sendMessage(sender, "Config reloaded");
        	return true;
	    }
		if(cmd.getName().equalsIgnoreCase("THCRefresh") && sender.hasPermission("thcl.refresh"))
		{
			Utilities.checkChannels();
			return true;
		}
		return false;
	}
}
