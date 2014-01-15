package com.llfrealms.THChatLink;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.llfrealms.THChatLink.util.THCListeners;
import com.llfrealms.THChatLink.util.Utilities;


public final class THCLink extends JavaPlugin 
{
	public ConsoleCommandSender consoleMessage = Bukkit.getConsoleSender();
	public String pluginname = "TownyHerochatLink";
	public ArrayList<String> perms = new ArrayList<String>(); //list of permissions from the config
	
	@Override
	public void onEnable()
    {
		this.saveDefaultConfig();
    	this.getConfig();
		new THCListeners(this);
		Utilities.sendMessage(consoleMessage, "&6" + pluginname + " enabled!");
    }

    @Override
    public void onDisable() 
    {
        getLogger().info("Closing "+pluginname);
    }
    
    public void THCSetup()
    {
    	List<String> permissions = getConfig().getStringList("permissions");
    	for(String s : permissions)
    	{
    		perms.add(s);
    	}
    }
    
}