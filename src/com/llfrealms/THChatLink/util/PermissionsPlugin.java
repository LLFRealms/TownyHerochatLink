package com.llfrealms.THChatLink.util;

import org.bukkit.Bukkit;

public enum PermissionsPlugin 
{ 
	ZPERMISSIONS("zPermissions"), GROUPMANAGER("GroupManager"), BPERMISSIONS("bPermissions"), BPERMISSIONS2("bPermissions2"), DROXPERMS("DroxPerms"),
	PERMISSIONS3("Permissions3"), PERMISSIONSBUKKIT("PermissionsBukkit"), PERMISSIONSEX("PermissionsEx"), PRIVILEGES("Privileges"), 
	RSCPERMISSIONS("rscPermissions"), SIMPLYPERMS("SimplyPerms"), STARBURST("Starburst"), SUPERPERMS("SuperPerms"), TOTALPERMISSIONS("TotalPermissions"),
	XPERMS("Xperms");
	private final String stringValue;
	private PermissionsPlugin(final String s) { stringValue = s; }
	public String toString() { return stringValue; }
	public void createGroup(String entityType, String entity) //used when creating groups
	{
		switch (this)
		{
			case ZPERMISSIONS:
				if(entityType.equalsIgnoreCase("town"))
				{
					String command = "perm group t:" + entity + " create";
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
				}
				else if(entityType.equalsIgnoreCase("nation"))
				{
					String command = "perm group n:" + entity + " create";
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the nation
				}
				else
				{
					Utilities.sendError("Shit dude, I messed up! Please let me know on my devBukkit page!");
				}
				break;
			case GROUPMANAGER:
				if(entityType.equalsIgnoreCase("town"))
				{
					String command = "mangadd t:" + entity;
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
				}
				else if(entityType.equalsIgnoreCase("nation"))
				{
					String command = "mangadd n:" + entity;
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the nation
				}
				else
				{
					Utilities.sendError("Shit dude, I messed up! Please let me know on my devBukkit page!");
				}
				break;
			case BPERMISSIONS:
				System.out.println("bPermissions");
				break;
			case BPERMISSIONS2:
				System.out.println("bPermissions2");
				break;
			case DROXPERMS:
				System.out.println("DroxPerms");
				break;
			case PERMISSIONS3:
				System.out.println("Permissions3");
				break;
			case PERMISSIONSBUKKIT:
				System.out.println("PermissionsBukkit");
				break;
			case PERMISSIONSEX:
				System.out.println("PermissionsEx");
				break;
			case PRIVILEGES:
				System.out.println("Privileges");
				break;
			case RSCPERMISSIONS:
				System.out.println("rscPermissions");
				break;
			case SIMPLYPERMS:
				System.out.println("SimplyPerms");
				break;
			case STARBURST:
				System.out.println("Starburst");
				break;
			case SUPERPERMS:
				System.out.println("SuperPerms");
				break;
			case TOTALPERMISSIONS:
				System.out.println("TotalPermissions");
				break;
			case XPERMS:
				System.out.println("Xperms");
				break;
		}
	}
	public void deleteGroup(String entityType, String entity) //used when creating groups
	{
		switch (this)
		{
			case ZPERMISSIONS:
				if(entityType.equalsIgnoreCase("town"))
				{
					String command = "perm group t:" + entity + " purge";
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
				}
				else if(entityType.equalsIgnoreCase("nation"))
				{
					String command = "perm group n:" + entity + " purge";
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the nation
				}
				else
				{
					Utilities.sendError("Shit dude, I messed up! Please let me know on my devBukkit page!");
				}
				break;
			case GROUPMANAGER:
				if(entityType.equalsIgnoreCase("town"))
				{
					String command = "mangdel t:" + entity;
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
				}
				else if(entityType.equalsIgnoreCase("nation"))
				{
					String command = "mangdel n:" + entity;
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the nation
				}
				else
				{
					Utilities.sendError("Shit dude, I messed up! Please let me know on my devBukkit page!");
				}
				break;
			case BPERMISSIONS:
				System.out.println("bPermissions");
				break;
			case BPERMISSIONS2:
				System.out.println("bPermissions2");
				break;
			case DROXPERMS:
				System.out.println("DroxPerms");
				break;
			case PERMISSIONS3:
				System.out.println("Permissions3");
				break;
			case PERMISSIONSBUKKIT:
				System.out.println("PermissionsBukkit");
				break;
			case PERMISSIONSEX:
				System.out.println("PermissionsEx");
				break;
			case PRIVILEGES:
				System.out.println("Privileges");
				break;
			case RSCPERMISSIONS:
				System.out.println("rscPermissions");
				break;
			case SIMPLYPERMS:
				System.out.println("SimplyPerms");
				break;
			case STARBURST:
				System.out.println("Starburst");
				break;
			case SUPERPERMS:
				System.out.println("SuperPerms");
				break;
			case TOTALPERMISSIONS:
				System.out.println("TotalPermissions");
				break;
			case XPERMS:
				System.out.println("Xperms");
				break;
		}
	}
	public void renameGroup(String entityType, String oldName, String newName) //used when creating groups
	{
		switch (this)
		{
			case ZPERMISSIONS:
				if(entityType.equalsIgnoreCase("town"))
				{
					String command = "perm group t:" + oldName + " rename t:" + newName;
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the town
				}
				else if(entityType.equalsIgnoreCase("Nation"))
				{
					String command = "perm group n:" + oldName + " rename n:" + newName;
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //make a group for the nation
				}
				else
				{
					Utilities.sendError("Shit dude, I messed up! Please let me know on my devBukkit page!");
				}
				break;
			case GROUPMANAGER:
				if(entityType.equalsIgnoreCase("town"))
				{
					GROUPMANAGER.deleteGroup("town", oldName);
					GROUPMANAGER.createGroup("town", newName);
				}
				else if(entityType.equalsIgnoreCase("Nation"))
				{
					GROUPMANAGER.deleteGroup("nation", oldName);
					GROUPMANAGER.createGroup("nation", newName);
				}
				else
				{
					Utilities.sendError("Shit dude, I messed up! Please let me know on my devBukkit page!");
				}
				break;
			case BPERMISSIONS:
				System.out.println("bPermissions");
				break;
			case BPERMISSIONS2:
				System.out.println("bPermissions2");
				break;
			case DROXPERMS:
				System.out.println("DroxPerms");
				break;
			case PERMISSIONS3:
				System.out.println("Permissions3");
				break;
			case PERMISSIONSBUKKIT:
				System.out.println("PermissionsBukkit");
				break;
			case PERMISSIONSEX:
				System.out.println("PermissionsEx");
				break;
			case PRIVILEGES:
				System.out.println("Privileges");
				break;
			case RSCPERMISSIONS:
				System.out.println("rscPermissions");
				break;
			case SIMPLYPERMS:
				System.out.println("SimplyPerms");
				break;
			case STARBURST:
				System.out.println("Starburst");
				break;
			case SUPERPERMS:
				System.out.println("SuperPerms");
				break;
			case TOTALPERMISSIONS:
				System.out.println("TotalPermissions");
				break;
			case XPERMS:
				System.out.println("Xperms");
				break;
		}
	}
};
