package ChatControl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ChatControl.commands.ChatControl;
import SpoutSDK.ChatColor;
import SpoutSDK.CraftEventInfo;
import SpoutSDK.CraftPlayer;
import SpoutSDK.CraftServer;
import SpoutSDK.ModBase;
import SpoutSDK.ModInfo;
import SpoutSDK.SpoutHelper;

public class Main extends ModBase {
	public static String modName = "ChatControl";
	public static String version = "1.0.0";
	public static Boolean adminBypass = false;
	public static Long chatCooldown = 2500L;
	public static Boolean doCensor = true;
	public static int censorsToSuspension = 3;
	public static Boolean doSpamCheck = true;
	public static int spamToSuspension = 3;
	public static Boolean doAdvertCheck = true;
	public static int adsToSuspension = 1;
	public static List<String> advertFlags = new ArrayList<>();
	public static List<String> curses = new ArrayList<>();
	public static Map<String, Long> lastChatTime = new HashMap<>();
	public static Map<String, Integer> spamViolations = new HashMap<>();
	public static Map<String, Integer> advertViolations = new HashMap<>();
	public static Map<String, Integer> censorViolations = new HashMap<>();
	public static Map<String, String> suspensions = new HashMap<>();
	
	public void onStartup(CraftServer argServer) {
		System.out.println("[ChatControl/INFO]: ChatControl version " + version + " starting up...");
		Config.readConfig();
		SpoutHelper.getServer().registerCommand(new ChatControl());
	}
	
	public ModInfo getModInfo() {
		ModInfo info = new ModInfo();
		info.description = "A comprehensive plugin to manage chat (" + version + ")";
		info.name = modName;
		info.version = version;
		return info;
	}
	
	public boolean containsCurse(String chat){
		for(String curse : curses){
			if(chat.contains(curse)){
				return true;
			}
	   }
	   return false;
	}
	
	public boolean containsSpam(String chat, String uuid) {
		int totalChars = chat.length();
		int upperCaseLetters = 0;
		int digits = 0;
		int others = 0;
        for (int i = 0; i < chat.length(); i++) {
            char ch = chat.charAt(i);
            if(Character.isUpperCase(ch)){
                upperCaseLetters++;
            }
            else if (Character.isDigit(ch)) {
                digits++;
            }
            else if(!(Character.isLowerCase(ch))) {
                others++;
            }
        }
        int percentUpperCase = (upperCaseLetters * 100) / totalChars;
        int percentDigits = (digits * 100) / totalChars;
        int percentOther = (others * 100) / totalChars;
        if(percentUpperCase > 49) {
        	return true;
        } if(percentDigits > 40) {
        	return true;
        } if(percentOther > 40) {
        	return true;
        } if(System.currentTimeMillis() - lastChatTime.get(uuid) < chatCooldown) {
        	return true;
        }
        return false;
	}
	
	public boolean containsAdvert(String chat) {
		for(String flag : advertFlags){
			if(chat.contains(flag)){
				return true;
			}
	   }
	   return false;
	}
	
	public void onPlayerInput(CraftPlayer plr, String msg, CraftEventInfo ei) {
		if(adminBypass && plr.hasPermission("chatcontrol.bypass")) {
			lastChatTime.put(plr.getUUID().toString(), System.currentTimeMillis());
			return;
		} else {
			if(doCensor && containsCurse(msg)) {
				ei.isCancelled = true;
				censorViolations.put(plr.getUUID().toString(), censorViolations.get(plr.getUUID().toString()) + 1);
				plr.sendMessage(ChatColor.RED + "[ChatControl] Please watch your language.");
				if(censorViolations.get(plr.getUUID().toString()) >= censorsToSuspension) {
					suspensions.put(plr.getUUID().toString(), censorViolations.get(plr.getUUID().toString()) + " Censor Violations");
					notifyOfSuspension(plr, suspensions.get(plr.getUUID().toString()));
					plr.kick(ChatColor.RED + ChatColor.BOLD + "Suspended by ChatControl\n" + ChatColor.GRAY + suspensions.get(plr.getUUID().toString()));
				}
			lastChatTime.put(plr.getUUID().toString(), System.currentTimeMillis());
			return;
			} if(doSpamCheck && containsSpam(msg, plr.getUUID().toString())) {
				ei.isCancelled = true;
				spamViolations.put(plr.getUUID().toString(), spamViolations.get(plr.getUUID().toString()) + 1);
				plr.sendMessage(ChatColor.RED + "[ChatControl] Please watch the spam.");
				if(spamViolations.get(plr.getUUID().toString()) >= spamToSuspension) {
					suspensions.put(plr.getUUID().toString(), spamViolations.get(plr.getUUID().toString()) + " Spam Violations");
					notifyOfSuspension(plr, suspensions.get(plr.getUUID().toString()));
					plr.kick(ChatColor.RED + ChatColor.BOLD + "Suspended by ChatControl\n" + ChatColor.GRAY + suspensions.get(plr.getUUID().toString()));
				}
			lastChatTime.put(plr.getUUID().toString(), System.currentTimeMillis());
			return;
			} if(doAdvertCheck && containsAdvert(msg)) {
				ei.isCancelled = true;
				advertViolations.put(plr.getUUID().toString(), advertViolations.get(plr.getUUID().toString()) + 1);
				plr.sendMessage(ChatColor.RED + "[ChatControl] Do not advertise.");
				if(advertViolations.get(plr.getUUID().toString()) >= adsToSuspension) {
					suspensions.put(plr.getUUID().toString(), advertViolations.get(plr.getUUID().toString()) + " Ad Violations");
					notifyOfSuspension(plr, suspensions.get(plr.getUUID().toString()));
					plr.kick(ChatColor.RED + ChatColor.BOLD + "Suspended by ChatControl\n" + ChatColor.GRAY + suspensions.get(plr.getUUID().toString()));
				}
			lastChatTime.put(plr.getUUID().toString(), System.currentTimeMillis());
			return;
			}
		lastChatTime.put(plr.getUUID().toString(), System.currentTimeMillis());
		}
	}
	
	public void onPlayerLogin(String playerName, UUID uuid, InetAddress address, CraftEventInfo ei) {
		ChatControl.ensureDataOfPlayer(uuid.toString());
		if(suspensions.containsKey(uuid.toString())) {
			ei.isCancelled = true;
			ei.tag = ChatColor.RED + ChatColor.BOLD + "Suspended by ChatControl\n" + ChatColor.GRAY + suspensions.get(uuid.toString());
			System.out.println("[ChatControl/DANGER]: Disconnecting suspended player " + playerName + ": " + suspensions.get(uuid.toString()));
		}
		lastChatTime.put(uuid.toString(), System.currentTimeMillis());
		
	}
	
	public void notifyOfSuspension(CraftPlayer offender, String reason) {
		System.out.println("[ChatControl/DANGER]: Suspended " + offender.getName() + ": " + reason);
		for(CraftPlayer plr : SpoutHelper.getServer().getPlayers()) {
			if(plr.hasPermission("chatcontrol.notify")) {
				plr.sendMessage(ChatColor.RED + "[ChatControl] Suspended " + offender.getName() + ": " + reason);
			}
		}
	}

}
