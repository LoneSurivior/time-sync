package me.JaxonLeake.TimeSync;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	String prefix = "&7[&aTime-Sync&7]&r";
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
		sync();
		
		//AutoSync Process
		int minute = (int) 1200L;
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				sync();
				tellConsole(prefix + "Time just AutoSynced");
			}
		}, 0L, minute * this.getConfig().getInt("AutoSync"));
		
		//startup
		//reloads
		//plugin reloads 
	}
	
	@Override
	public void onDisable() {
		this.saveDefaultConfig();
		sync();
		//shutdown
		//reloads
		//plugin reloads
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("timesync") || label.equalsIgnoreCase("ts")) {
			if(args.length == 0) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					if(player.hasPermission("timesync.help")) {
						player.sendMessage(ChatColor.GRAY + "------------------");
						player.sendMessage(ChatColor.GREEN + "Time-Sync Commands");
						player.sendMessage(ChatColor.GRAY + "----------------");
						if(player.hasPermission("timesync.gettime")) {
							player.sendMessage(ChatColor.GREEN + "/ts gettime");
						}
						if(player.hasPermission("timesync.reload")) {
							player.sendMessage(ChatColor.GREEN + "/ts reload");
						}
						if(player.hasPermission("timesync.settime")) {
							player.sendMessage(ChatColor.GREEN + "/ts settime");
						}
						if(player.hasPermission("timesync.sync")) {
							player.sendMessage(ChatColor.GREEN + "/ts sync");
						}
						return true;
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
								" &4You do not have permission!"));
					}
				} else {
					tellConsole(ChatColor.GRAY + "------------------");
					tellConsole(ChatColor.GREEN + "Time-Sync Commands");
					tellConsole(ChatColor.GRAY + "------------------");
					tellConsole(ChatColor.GREEN + "/ts gettime");
					tellConsole(ChatColor.GREEN + "/ts reload");
					tellConsole(ChatColor.GREEN + "/ts settime");
					tellConsole(ChatColor.GREEN + "/ts sync");
				}
			} else {
		 		
				if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					if(sender instanceof Player) {
							Player player = (Player) sender;
							if(player.hasPermission("timesync.reload")) {
							this.saveDefaultConfig();
							this.reloadConfig();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
									" &aTime-sync config reloaded!"));
							return true; 
						} else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
									" &4You do not have permission!"));
						}
					} else {
						this.saveDefaultConfig();
						this.reloadConfig();
						tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
								" &aTimeSync config reloaded!"));
					}
				} 
		 		
		 		else if(args[0].equalsIgnoreCase("sync")) {
					if(sender instanceof Player) {
						Player player = (Player) sender;
						if(player.hasPermission("timesync.sync")) {
							//Actual Logic
							sync();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
									" &aAll world groups have been synced!"));
							//Done
							return true; 
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
									" &4You do not have permission!"));
						}
					} else {
						//Actual Logic
						sync();
						tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
								" &aAll world groups have been synced!"));
						//Done
						return true; 
					}
				} 
				
				else if (args[0].equalsIgnoreCase("gettime") || args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("gt") || args[0].equalsIgnoreCase("time")) {
					if(sender instanceof Player) {
						Player player = (Player) sender;
						if(player.hasPermission("timesync.gettime")) {
							//Actual Logic
							player.sendMessage(ChatColor.GRAY + "----------");
							player.sendMessage(ChatColor.GREEN + "World Times");
							player.sendMessage(ChatColor.GRAY + "----------");
							List<World> worlds = getServer().getWorlds();
							for(World world : worlds) {
								player.sendMessage(ChatColor.GREEN + world.getName() + ": " + world.getTime());
							}
							//Done
							return true;
						} else {
							player.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
						}
					} else {
						//Actual Logic
						List<World> worlds = getServer().getWorlds();
						tellConsole(ChatColor.GRAY + "----------");
						tellConsole(ChatColor.GREEN + "World Times");
						tellConsole(ChatColor.GRAY + "----------");
						for(World world : worlds) {
							tellConsole(ChatColor.GREEN + world.getName() + ": " + world.getTime());
						}
						//Done
					}
				} 
				
				else if (args[0].equalsIgnoreCase("settime") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("st")) {
					if(sender instanceof Player) {
						Player player = (Player) sender;
						if(player.hasPermission("timesync.settime")) {
							if(args.length == 2) {
								if(isNum(args[1]) == true) {
									if(Integer.parseInt(args[1])>=0 && Integer.parseInt(args[1])<=24000) {
										//Auth Complete
										setTime(Integer.parseInt(args[1]));
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
												" &aTime of all groups set to: " + Integer.parseInt(args[1])));
										return true;
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
												" &cTime must be a number between 0 & 24000!"));
									}
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
											" &cUsage: /timesync settime <time>"));
								}
							} else if(args.length >= 3) {
								if(isNum(args[1]) == true && isNum(args[2]) == true) {
									if(Integer.parseInt(args[2])>=0 && Integer.parseInt(args[2])<=24000) {
										if(Integer.parseInt(args[1]) >= 1 && Integer.parseInt(args[1]) <= this.getConfig().getConfigurationSection("Groups").getKeys(false).size()) {
											//Auth Complete
											setGroupTime(numToGroup(Integer.parseInt(args[1])), Integer.parseInt(args[2]));
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
													" &aTime of group " + Integer.parseInt(args[1]) + " set to: " + Integer.parseInt(args[2])));
											return true;
										} else {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
													" &cInvalid Group!"));
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
												" &cTime must be a number between 0 & 24000!"));
									}
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
											" &cUsage: /timesync settime <time>"));
								}
							} else {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
										" &cUsage: /timesync settime <time> OR /timesync settime <group> <time>"));
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
									" &4You do not have permission!"));
						}
					} else { //If Console
						if(args.length == 2) {
							if(isNum(args[1]) == true) {
								if(Integer.parseInt(args[1])>=0 && Integer.parseInt(args[1])<=24000) {
									//Auth Complete
									setTime(Integer.parseInt(args[1]));
									tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
											" &aTime of all groups set to: " + Integer.parseInt(args[1])));
									return true;
								} else {
									tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
											" &cTime must be a number between 0 & 24000!"));
								}
							} else {
								tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
										" &cUsage: /timesync settime <time>"));
							}
						} else if(args.length >= 3) {
							if(isNum(args[1]) == true && isNum(args[2]) == true) {
								if(Integer.parseInt(args[2])>=0 && Integer.parseInt(args[2])<=24000) {
									if(Integer.parseInt(args[1]) >= 1 && Integer.parseInt(args[1]) <= this.getConfig().getConfigurationSection("Groups").getKeys(false).size()) {
										//Auth Complete
										setGroupTime(numToGroup(Integer.parseInt(args[1])), Integer.parseInt(args[2]));
										tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
												" &aTime of group " + Integer.parseInt(args[1]) + " set to: " + Integer.parseInt(args[2])));
										return true;
									} else {
										tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
												" &cInvalid Group!"));
									}
								} else {
									tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
											" &cTime must be a number between 0 & 24000!"));
								}
							} else {
								tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
										" &cUsage: /timesync settime <time>"));
							}
						} else {
							tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
									" &cUsage: /timesync settime <time> OR /timesync settime <group> <time>"));
						}
					}
				}
				
				else {
					if(sender instanceof Player) {
						Player player = (Player) sender;
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + 
								" &cUnknown command! Type /timesync to get a list of available commands"));
					} else {
						tellConsole(ChatColor.translateAlternateColorCodes('&', prefix + 
								" &cUnknown command! Type /timesync to get a list of available commands"));
					}
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onSkipNight(PlayerBedLeaveEvent event) {
		if(event.getPlayer().getWorld().getTime() == 0) {
			String group = worldToGroup(event.getPlayer().getWorld());
			setGroupTime(group, 0);
		}
	}
	
	@EventHandler
	public void onTime() {
		
	}
	
	public void tellConsole(String message) {
	      System.out.println(message);
	}

	public void sync() {
		for(String group : this.getConfig().getConfigurationSection("Groups").getKeys(false)) {
			World leader = getServer().getWorld(this.getConfig().getString("Groups." + group + ".leader"));
			List<String> followerStrings = this.getConfig().getStringList("Groups." + group + ".followers");
			List<World> followerWorlds = new ArrayList<>();
			for(String followerString : followerStrings) {
				followerWorlds.add(getServer().getWorld(followerString));
			}
			
			for(World follower : followerWorlds) {
				follower.setTime(leader.getTime());
		    }
		}
	}
	
	public void setTime(int input) {
		List<World> worlds = getServer().getWorlds();
		for(World world : worlds) {
			world.setTime(input);
		}
	}
	
	public void setGroupTime(String group, int input) {
		//gets leader and sets time
		World leader = getServer().getWorld(this.getConfig().getString("Groups." + group + ".leader"));
		leader.setTime(input);
		//Converts followers from a list of Strings to a list of Worlds
		List<String> worldStrings = this.getConfig().getStringList("Groups." + group + ".followers");
		List<World> followers = new ArrayList<>();
		for(String worldString : worldStrings) {
			followers.add(getServer().getWorld(worldString));
		}
		//Sets time of followers
		for(World follower : followers) {
			follower.setTime(input);
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
	
	public String worldToGroup(World world) {
		for(String group : this.getConfig().getConfigurationSection("Groups").getKeys(false)) {
			World leader = getServer().getWorld(this.getConfig().getString("Groups." + group + ".leader"));
			List<String> followerStrings = this.getConfig().getStringList("Groups." + group + ".followers");
			List<World> followerWorlds = new ArrayList<>();
			for(String followerString : followerStrings) {
				followerWorlds.add(getServer().getWorld(followerString));
			}
			if(leader == world) {
				return group;
			}
			for(World follower : followerWorlds) {
				if(follower == world) {
					return group;
				}
		    }
		}
		return null;
	}
	
	public String numToGroup(int group) {	
		ArrayList<String>groups = new ArrayList<>(this.getConfig().getConfigurationSection("Groups").getKeys(false));
		return groups.get(group-1);
	}
}