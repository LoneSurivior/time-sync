package me.JaxonLeake.TimeSync;

import java.util.List;

import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.JaxonLeake.TimeSync.Files.DataManager;

public class Main extends JavaPlugin implements Listener {
	
	public DataManager data;
	
	@Override
	public void onEnable() {
		if(!this.data.getConfig().contains("mainworld")) {
			data.getConfig().set("MainWorld", 0);
			data.saveConfig();
		}
		this.data = new DataManager(this);
		this.getServer().getPluginManager().registerEvents(this, this);
		syncTo(0);
		//startup
		//reloads
		//plugin reloads 
	}
	
	@Override
	public void onDisable() {
		syncTo(0);
		//shutdown
		//reloads
		//plugin reloads
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equalsIgnoreCase("sync")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.hasPermission("timesync.sync")) {
					if(!this.data.getConfig().contains("mainworld")) {
						syncTo(this.data.getConfig().getInt("mainworld"));
					} else {
						syncTo(0);
					}
					player.sendMessage(ChatColor.GREEN + "Operation Completed");
					return true; 
				} else {
					player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
				}
			} else {
				if(!this.data.getConfig().contains("mainworld")) {
					syncTo(this.data.getConfig().getInt("mainworld"));
				} else {
					syncTo(0);
				}
				tellConsole(ChatColor.GREEN + "Operation Completed");
			}
		}
		
		if (label.equalsIgnoreCase("gettime")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.hasPermission("timesync.gettime")) {
					List<World> worlds = getServer().getWorlds();
					player.sendMessage(ChatColor.GREEN + "World Times:");
					for(int i=0; i<worlds.size(); i++) {
						World world = worlds.get(i);
						player.sendMessage(ChatColor.GREEN + "World: " + world + " Time: " + world.getTime());
					}
					return true;
				} else {
					player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
				}
			} else {
				List<World> worlds = getServer().getWorlds();
				tellConsole(ChatColor.GREEN + "World Times:");
				for(int i=0; i<worlds.size(); i++) {
					World world = worlds.get(i);
					tellConsole(ChatColor.GREEN + "World: " + world + " Time: " + world.getTime());
				}
			}
		}

		if (label.equalsIgnoreCase("settime")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.hasPermission("timesync.settime")) {
					if(args.length == 1 && isNum(args[0]) == true) {
						setTime(Integer.parseInt(args[0]));
						player.sendMessage(ChatColor.GREEN + "Time set to: " + Integer.parseInt(args[0]));
						return true; 
					} else {
						player.sendMessage(ChatColor.RED + "Incorrect Format! Please use /settime <time>");
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
				}
			} else {
				if(args.length == 1 && isNum(args[0]) == true) {
					setTime(Integer.parseInt(args[0]));
					tellConsole(ChatColor.GREEN + "Time set to: " + Integer.parseInt(args[0]));
					return true; 
				} else {
					tellConsole(ChatColor.RED + "Incorrect Format! Please use /settime <time>");
				}
			}
		}
		
		if (label.equalsIgnoreCase("setmainworld")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				if(player.hasPermission("timesync.setmainworld")) {
					if(args.length == 1 && isNum(args[0]) == true) {
						data.getConfig().set("MainWorld", (Integer.parseInt(args[0])));
						data.saveConfig();
						player.sendMessage(ChatColor.GREEN + "Main World set to: " + Integer.parseInt(args[0]));
						return true; 
					} else {
						player.sendMessage(ChatColor.RED + "Incorrect Format! Please use /setmainworld <number 0-" + (getServer().getWorlds().size()-1) + ">");
					}
				} else {
					player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
				}
			} else {
				if(args.length == 1 && isNum(args[0]) == true) {
					data.getConfig().set("MainWorld", (Integer.parseInt(args[0])));
					data.saveConfig();
					tellConsole(ChatColor.GREEN + "Time set to: " + Integer.parseInt(args[0]));
					return true; 
				} else {
					tellConsole(ChatColor.RED + "Incorrect Format! Please use /setmainworld <number 0-" + (getServer().getWorlds().size()-1) + ">");
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onSkipNight(PlayerBedLeaveEvent event) {
		if(event.getPlayer().getWorld().getTime()==0) {
			if(!this.data.getConfig().contains("mainworld")) {
				syncTo(this.data.getConfig().getInt("mainworld"));
			} else {
				syncTo(0);
			}
		}
	}
	@EventHandler
	public void onWorldJoin(PlayerChangedWorldEvent event) {
		World oldworld = event.getFrom();
		if(getServer().getOnlinePlayers().size()==1) {
			//syncTo(oldworld);
			setTime((int)oldworld.getTime());
		} else {
			if(!this.data.getConfig().contains("mainworld")) {
				syncTo(this.data.getConfig().getInt("mainworld"));
			} else {
				syncTo(0);
			}
		}
	}
	
	//Repeatable Commands
	
	public void tellConsole(String message) {
	      System.out.println(message);
	}

	public void syncTo(int input) {
		List<World> worlds = getServer().getWorlds();
		World mainworld = getServer().getWorlds().get(input);
		for(int i=0; i<worlds.size(); i++) {
			World sideworld = worlds.get(i);
			sideworld.setTime(mainworld.getTime());
	    }
	}
	
	public void setTime(int input) {
		if(input<=0 && input>=24000) {
			List<World> worlds = getServer().getWorlds();
			for(int i=0; i<worlds.size(); i++) {
				World world = getServer().getWorlds().get(i);
				world.setTime(input);
		    }
		}
	}
	
	public boolean isNum(String num) {
		try {
			Integer.parseInt(num);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}