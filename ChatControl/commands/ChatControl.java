package ChatControl.commands;

import java.util.Arrays;
import java.util.List;

import ChatControl.Config;
import ChatControl.Main;
import SpoutSDK.ChatColor;
import SpoutSDK.CraftCommand;
import SpoutSDK.CraftPlayer;
import SpoutSDK.SpoutHelper;

public class ChatControl implements CraftCommand {

	@Override
	public List<String> getAliases() {
		return Arrays.asList("chatcontrol");
	}

	@Override
	public String getCommandName() {
		return "cc";
	}

	@Override
	public String getHelpLine(CraftPlayer plr) {
		return ChatColor.GOLD + "/cc " + ChatColor.WHITE + "--- Chat Control command";
	}

	@Override
	public List<String> getTabCompletionList(CraftPlayer plr, String[] args) {
		return null;
	}

	@Override
	public void handleCommand(CraftPlayer plr, String[] args) {
		if(hasPermissionToUse(plr)) {
			if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
				sendHelpMessage(plr);
				return;
			} else if(args[0].equalsIgnoreCase("check")) {
				if(!(plr.hasPermission("chatcontrol.check"))) {
					plr.sendMessage(ChatColor.RED + "You do not have permission for that command.");
					return;
				}
				if(!(args[1]).isEmpty()) {
					plr.sendMessage(ChatColor.GOLD + "Checking data on " + args[1]);
					String uuid = SpoutHelper.getServer().getPlayerUUIDFromName(args[1]).toString();
					ensureDataOfPlayer(uuid);
					plr.sendMessage(ChatColor.GRAY + "Censor Violations: " + ChatColor.GOLD + Main.censorViolations.get(uuid));
					plr.sendMessage(ChatColor.GRAY + "Spam Violations: " + ChatColor.GOLD + Main.censorViolations.get(uuid));
					plr.sendMessage(ChatColor.GRAY + "Advert Violations: " + ChatColor.GOLD + Main.censorViolations.get(uuid));
					if(Main.suspensions.containsKey(uuid)) {
						plr.sendMessage(ChatColor.RED + args[1] + " is suspended: " + ChatColor.GRAY + Main.suspensions.get(uuid));
					}
				} else {
					plr.sendMessage(ChatColor.RED + "Usage: /cc check (username)");
				}
			} else if(args[0].equalsIgnoreCase("pardon")) {
				if(!(plr.hasPermission("chatcontrol.pardon"))) {
					plr.sendMessage(ChatColor.RED + "You do not have permission for that command.");
					return;
				} if(!(args[1]).isEmpty()) {
					String uuid = SpoutHelper.getServer().getPlayerUUIDFromName(args[1]).toString();
					if(Main.suspensions.containsKey(uuid)) {
						plr.sendMessage(ChatColor.GOLD + "[ChatControl] Pardoned " + args[1]);
						Main.suspensions.remove(uuid);
					} else {
						plr.sendMessage(ChatColor.RED + "That player is not suspended.");
					}
				} else {
					plr.sendMessage(ChatColor.RED + "Usage: /cc pardon (username)");
				}
			} else if(args[0].equalsIgnoreCase("reload")) {
				if(!(plr.hasPermission("chatcontrol.reload"))) {
					plr.sendMessage(ChatColor.RED + "You do not have permission for that command.");
					return;
				}
				Config.readConfig();
				plr.sendMessage(ChatColor.GOLD + "[ChatControl] Configuration reloaded.");
			}
		} else {
			plr.sendMessage(ChatColor.RED + "You do not have permission for that command.");
		}
		
	}
	
	public void sendHelpMessage(CraftPlayer plr) {
		plr.sendMessage(" ");
		plr.sendMessage(ChatColor.GOLD + "ChatControl version " + Main.version);
		plr.sendMessage(ChatColor.GRAY + "/cc help --- Display command list");
		plr.sendMessage(ChatColor.GRAY + "/cc check --- Check a player's infractions");
		plr.sendMessage(ChatColor.GRAY + "/cc pardon --- Pardon a suspension");
		plr.sendMessage(ChatColor.GRAY + "/cc reload --- Reload the config");
		plr.sendMessage(" ");
	}
	
	public static void ensureDataOfPlayer(String uuid) {
		if(!(Main.censorViolations.containsKey(uuid))) {
			Main.censorViolations.put(uuid, 0);
		} if(!(Main.spamViolations.containsKey(uuid))) {
			Main.spamViolations.put(uuid, 0);
		} if(!(Main.advertViolations.containsKey(uuid))) {
			Main.advertViolations.put(uuid, 0);
		}
	}

	@Override
	public boolean hasPermissionToUse(CraftPlayer plr) {
		return plr.hasPermission("chatcontrol.command");
	}

}
