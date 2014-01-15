package com.llfrealms.THChatLink;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.llfrealms.THChatLink.util.THCListeners;
import com.llfrealms.THChatLink.util.Utilities;


public final class THCLink extends JavaPlugin 
{
	public ConsoleCommandSender consoleMessage = Bukkit.getConsoleSender();
	public String pluginname = "TownyHerochatLink";
	public ArrayList<String> perms = new ArrayList<String>(); //list of permissions from the config
	public boolean createN; //boolean for whether or not to create new channels for nations
	public boolean createT; //boolean for whether or not to create new channels for towns
	public static Permission permset = null;
	
	@Override
	public void onEnable()
    {
		this.saveDefaultConfig();
    	this.getConfig();
		new THCListeners(this);
		setupPermissions();
		THCSetup();
		Utilities.sendMessage(consoleMessage, "&6" + pluginname + " enabled!");
    }
    @Override
    public void onDisable() 
    {
        getLogger().info("Closing "+pluginname);
    }
    
    private boolean setupPermissions() {
    	getLogger().info("Setting up the config the permissions hook");
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permset = rsp.getProvider();
        return perms != null;
    }
    
    public void THCSetup()
    {
    	List<String> permissions = getConfig().getStringList("permissions");
    	for(String s : permissions)
    	{
    		perms.add(s);
    	}
    	if(getConfig().isBoolean("createFor.nations"))
    	{
    	createN = getConfig().getBoolean("createFor.nations");
    	Utilities.sendMessage(consoleMessage, "&6Nations entry is " + createN);
    	}
    	else
    	{
    		Utilities.sendMessage(consoleMessage, "&6Nations entry is not a boolean value");
    	}
    	if(getConfig().isBoolean("createFor.towns"))
    	{
    		createT = getConfig().getBoolean("createFor.towns");
    		Utilities.sendMessage(consoleMessage, "&6Towns entry is " + createT);
    	}
    	else
    	{
    		Utilities.sendMessage(consoleMessage, "&6Towns entry is not a boolean value");
    	}
    }
    
}