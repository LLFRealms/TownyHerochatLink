package com.llfrealms.THChatLink;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;




import com.llfrealms.THChatLink.Listeners.THCNationListeners;
import com.llfrealms.THChatLink.Listeners.THCTownListeners;
import com.llfrealms.THChatLink.util.PermissionsPlugin;
import com.llfrealms.THChatLink.util.THCCommands;
import com.llfrealms.THChatLink.util.Utilities;


public final class THCLink extends JavaPlugin 
{
	public ConsoleCommandSender consoleMessage = Bukkit.getConsoleSender();
	public String townsColor, townsFormat, nationsColor, nationsFormat, database, dbusername, dbpassword, host, permissionsPlugin, world;
	public String pluginname = "TownyHerochatLink";
	private int port;
	public ArrayList<String> perms = new ArrayList<String>(); //list of permissions from the config
	public boolean createN; //boolean for whether or not to create new channels for nations
	public boolean createT; //boolean for whether or not to create new channels for towns
	public Permission permission = null;
	public String logPrefix = "&f[&5"+pluginname+"&f]&e";
	public Statement stmt = null;
	public Connection connection = null;
	public ResultSet result = null;
	
    File nicksFile;
    public FileConfiguration nicks;
	
	@Override
	public void onEnable()
    {
		this.saveDefaultConfig();
    	this.getConfig();
		townsColor = getConfig().getString("towns.color");
		townsFormat = getConfig().getString("towns.format");
		nationsColor = getConfig().getString("nations.color");
		nationsFormat = getConfig().getString("nations.format");
		
		townsFormat = townsFormat.replaceAll("<", "{");
		townsFormat = townsFormat.replaceAll(">", "}");
		nationsFormat = nationsFormat.replaceAll("<", "{");
		nationsFormat = nationsFormat.replaceAll(">", "}");
		
		world = getConfig().getString("createIn.world");
    	getCommand("thcload").setExecutor(new THCCommands(this));
    	getCommand("thcsave").setExecutor(new THCCommands(this));
    	getCommand("thcrefresh").setExecutor(new THCCommands(this));
    	getCommand("thcgroups").setExecutor(new THCCommands(this));

		
        host = getConfig().getString("MySQL.host");
        port = getConfig().getInt("MySQL.port");
        database = getConfig().getString("MySQL.database");
        dbusername = getConfig().getString("MySQL.username");
        dbpassword = getConfig().getString("MySQL.password");
        nicksFile = new File(getDataFolder(), "nicks.yml");
       
        connect(); //connect to database
        tableCheck(); //check to make sure our table exists and if not creates it.
        
		setupPermissions();
		permissionsPlugin = permission.getName();
        try {
            firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
        nicks = new YamlConfiguration();
        loadYamls();
		THCSetup();	
		if(createT){new THCTownListeners(this);}
		if(createN){new THCNationListeners(this);}
		sendLog("Enabled!");
    }
    @Override
    public void onDisable() 
    {
    	saveYamls();
        Utilities.sendMessage(consoleMessage, logPrefix + "Closing");
    }
    
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    public void tableCheck()
    {
    	Utilities.sendMessage(consoleMessage, logPrefix + "Making sure our table exists");
    	String sql = "CREATE TABLE IF NOT EXISTS "+pluginname +
    				 " (nick varchar(255), entity varchar(255));";
    	try {
    		stmt = connection.createStatement();
    		stmt.executeUpdate(sql);
		} catch (SQLException ex) {
            // handle any errors
        	getLogger().info("SQLException: " + ex.getMessage());
        	getLogger().info("SQLState: " + ex.getSQLState());
        	getLogger().info("VendorError: " + ex.getErrorCode());
        }
    	ResultSet checkDefault = query("SELECT nick, entity FROM "+pluginname);
    	try {
			if(!checkDefault.isBeforeFirst())
			{
				Utilities.sendMessage(consoleMessage, logPrefix + "Adding default values to the database");
				addRecord("INSERT INTO " + pluginname + " VALUES(\'Default\', \'Default\');");
			}
		} catch (SQLException ex) {
			getLogger().info("SQLException: " + ex.getMessage());
        	getLogger().info("SQLState: " + ex.getSQLState());
        	getLogger().info("VendorError: " + ex.getErrorCode());
		}
    	if (stmt != null) 
	    {
	        try {
	        	stmt.close();
	        } catch (SQLException sqlEx) { } // ignore

	        stmt = null;
	    }
    }
    public void addRecord(String sql)
    {
    	try {
			stmt = connection.createStatement();
			 stmt.executeUpdate(sql);
		} catch (SQLException ex) {
            // handle any errors
			getLogger().info("SQLException: " + ex.getMessage());
			getLogger().info("SQLState: " + ex.getSQLState());
			getLogger().info("VendorError: " + ex.getErrorCode());
        }
    	if (stmt != null) 
	    {
	        try {
	        	stmt.close();
	        } catch (SQLException sqlEx) { } // ignore

	        stmt = null;
	    }
    }
    public void deleteRecord(String sql)
    {
    	try {
			stmt = connection.createStatement();
			 stmt.executeUpdate(sql);
		} catch (SQLException ex) {
            // handle any errors
			getLogger().info("SQLException: " + ex.getMessage());
			getLogger().info("SQLState: " + ex.getSQLState());
			getLogger().info("VendorError: " + ex.getErrorCode());
        }
    	if (stmt != null) 
	    {
	        try {
	        	stmt.close();
	        } catch (SQLException sqlEx) { } // ignore

	        stmt = null;
	    }
    }
    public void connect() {
        String connectionString = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
        	Utilities.sendMessage(consoleMessage, logPrefix + "Attempting connection to MySQL...");

            // Force driver to load if not yet loaded
            Class.forName("com.mysql.jdbc.Driver");
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", dbusername);
            connectionProperties.put("password", dbpassword);
            connectionProperties.put("autoReconnect", "false");
            connectionProperties.put("maxReconnects", "0");
            connection = DriverManager.getConnection(connectionString, connectionProperties);
            Utilities.sendMessage(consoleMessage, logPrefix + "Connection to MySQL was a &asuccess!");
        }
        catch (SQLException ex) {
            connection = null;
            Utilities.sendMessage(consoleMessage, logPrefix + "&f[&cSEVERE&f]&e Connection to MySQL &cfailed!");
            getLogger().info("SQLException: " + ex.getMessage());
        	getLogger().info("SQLState: " + ex.getSQLState());
        	getLogger().info("VendorError: " + ex.getErrorCode());
        }
        catch (ClassNotFoundException ex) {
            connection = null;
            getLogger().severe("["+pluginname+"] MySQL database driver not found!");
        }
    }
    public boolean isNickTaken(String nick)
    {
    	List<String> nicksList = nicks.getStringList("Nicks");
    	
    	for(String s: nicksList)
    	{
    		sendLog(s);
    		if(s.equalsIgnoreCase(nick))
    		{
    			return true;
    		}
    	}
    	
		return false;
    }
    public void addToNicks(String nick, String entity)
    {
    	nicks.createSection(entity);
    	nicks.set(entity, nick);
    }
    public void removeFromNicks(String entity)
    {
    	nicks.set(entity, null);
    }
    public ResultSet query(String query)
	{
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException ex) {
            // handle any errors
        	getLogger().info("SQLException: " + ex.getMessage());
        	getLogger().info("SQLState: " + ex.getSQLState());
        	getLogger().info("VendorError: " + ex.getErrorCode());
        }
		return rs;
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
	    	if(createN)
    		{
	    		Utilities.sendMessage(consoleMessage, logPrefix + "Towns entry is &a" + createN);
    		}
    		else if(!createN)
    		{
    			Utilities.sendMessage(consoleMessage, logPrefix + "Towns entry is &c" + createN);
    		}
    		
    	}
    	else
    	{
    		Utilities.sendMessage(consoleMessage, logPrefix + "&f[&cERROR&f]&e Nations entry is not a boolean value, please set it to true or false and restart");
    	}
    	if(getConfig().isBoolean("createFor.towns"))
    	{
    		createT = getConfig().getBoolean("createFor.towns");
    		if(createT)
    		{
    			Utilities.sendMessage(consoleMessage, logPrefix + "Towns entry is &a" + createT);
    		}
    		else if(!createT)
    		{
    			Utilities.sendMessage(consoleMessage, logPrefix + "Towns entry is &c" + createT);
    		}
    		
    	}
    	else
    	{
    		Utilities.sendMessage(consoleMessage, logPrefix + "&f[&cERROR&f]&eTowns entry is not a boolean value, please set it to true or false and restart");
    	}
    }
    public void createChannel(String entityType, String entity, String nick)
    {
    	addToNicks(nick, entity);
    	String command = "ch create " + entity + " " + nick;
    	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //create the channel
    	if(entityType.equalsIgnoreCase("town"))
    	{
    	command = "ch set " + entity + " color " + townsColor;
    	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //set the channel color
    	
    	command = "ch set " + entity + " format " + townsFormat;
    	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //set the channel format
    	}
    	else if(entityType.equalsIgnoreCase("nation"))
    	{
    		command = "ch set " + entity + " color " + nationsColor;
        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //set the channel color
        	
        	command = "ch set " + entity + " format " + nationsFormat;
        	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command); //set the channel format
    	}
    }
    public void deleteChannel(String entity)
    {
    	Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ch remove " + entity); //delete the channel
    	removeFromNicks(entity);
    }
    public void createGroup(String entityType, String entity)
    {
    	switch(permissionsPlugin)
    	{
    		case "zPermissions":
    			PermissionsPlugin.ZPERMISSIONS.createGroup(entityType, entity);
    			break;
    		default:
    			Utilities.sendMessage(consoleMessage, "Shit Dude!");
    			break;
    	}
    }
    public void deleteGroup(String entityType, String entity)
    {
    	switch(permissionsPlugin)
    	{
    		case "zPermissions":
    			PermissionsPlugin.ZPERMISSIONS.deleteGroup(entityType, entity);
    			break;
    		default:
    			Utilities.sendMessage(consoleMessage, "Shit Dude!");
    			break;
    	}
    }
    public void renameGroup(String entityType, String oldName, String newName)
    {
    	switch(permissionsPlugin)
    	{
    		case "zPermissions":
    			PermissionsPlugin.ZPERMISSIONS.renameGroup(entityType, oldName, newName);
    			break;
    		default:
    			Utilities.sendMessage(consoleMessage, "Shit Dude!");
    			break;
    	}
    }
    private void firstRun() throws Exception {
        
        if(!nicksFile.exists()){
            nicksFile.getParentFile().mkdirs();
            copy(getResource("history.yml"), nicksFile);
        }
    }
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveYamls() {
        try {
            nicks.save(nicksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadYamls() {
        try {
            nicks.load(nicksFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean sendLog(String message)
    {
    	ConsoleCommandSender p = Bukkit.getConsoleSender();
        if (message ==null || message.isEmpty()) return true;
        p.sendMessage(Utilities.colorChat(logPrefix+message));
        return true;
    }
    public  boolean sendError(String message)
    {
    	ConsoleCommandSender p = Bukkit.getConsoleSender();
        if (message ==null || message.isEmpty()) return true;
        p.sendMessage(Utilities.colorChat(logPrefix+"&c" + message));
        return true;
    }
    
}