package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.CommandAdminEnum;
import eu.reborn_minecraft.zhorse.enums.KeyWordEnum;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class ZAdmin extends Command {
	String fullCommand;
	String subCommand;

	public ZAdmin(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {			
			if (!idMode) {
				if (isOnHorse(true)) { // select horse w/ or w/o target
					horse = (Horse) p.getVehicle();
					if (isOwner(targetUUID, true, true)) {
						idMode = true;
						userID = zh.getUM().getUserID(targetUUID, horse);
					}
				}
				execute();
			}
			else if (isRegistered(targetUUID, userID)) {
				execute();
			}
		}
	}

	private void execute() {
		if (zh.getEM().canAffordCommand(p, command)) {
			if (!argument.isEmpty()) {
				subCommand = argument.toLowerCase();;
				if (argument.contains(" ")) {
					subCommand = argument.substring(0, argument.indexOf(" "));
				}
				if (subCommand.equals(CommandAdminEnum.clear.name())) {
					clear();
				}
				else {
					if (displayConsole) {
						zh.getMM().sendMessageValue(s, LocaleEnum.unknownAdminCommand, subCommand);
					}
					sendCommandAdminDescriptionList();
				}
			}
			else {
				sendCommandAdminDescriptionList();
			}
		}
	}

	private void clear() {
		fullCommand = command + KeyWordEnum.dot.getValue() + CommandAdminEnum.clear.getName().toLowerCase(); // TODO vérifier fonctionnement
		if (hasPermission(s, fullCommand , true, false)) {
			if (argument.split(" ").length >= 2) {
				targetMode = true;
				String subArgument = argument.substring(argument.indexOf(" ") + 1);
				if (subArgument.split(" ").length >= 2) {
					idMode = true;
					targetName = subArgument.substring(0, subArgument.indexOf(" "));
					userID = subArgument.substring(subArgument.indexOf(" ") + 1);
				}
				else {
					targetName = subArgument;
				}
				targetUUID = getPlayerUUID(targetName);
				samePlayer = playerCommand && p.getUniqueId().equals(targetUUID);
			}
			if (targetMode) {
				if (!idMode) {
					if (isRegistered(targetUUID)) {
						boolean success = true;
						for (int userID = 1; userID <= zh.getUM().getClaimsAmount(targetUUID); ++userID) {
							Horse horse = zh.getHM().getHorse(targetUUID, Integer.toString(userID));
							if (horse != null) {
								horse.setCustomName(null);
								horse.setCustomNameVisible(false);
							}
							if (!zh.getUM().unRegisterHorse(targetUUID, Integer.toString(userID))) {
								success = false;
							}
						}
						if (success) {
							if (samePlayer) {
								zh.getMM().sendMessage(s, LocaleEnum.playerCleared);
							}
							else {
								zh.getMM().sendMessagePlayer(s, LocaleEnum.playerClearedOther, targetName);
							}
							zh.getEM().payCommand(p, command);
						}
					}
				}
				else if (isRegistered(targetUUID, userID)) {
					Horse horse = zh.getHM().getHorse(targetUUID, userID);
					if (horse != null) {
						horse.setCustomName(null);
						horse.setCustomNameVisible(false);
					}
					if (zh.getUM().unRegisterHorse(targetUUID, userID)) {
						if (samePlayer) {
							zh.getMM().sendMessageHorse(s, LocaleEnum.horseCleared, horseName);
						}
						else {
							zh.getMM().sendMessageHorsePlayer(s, LocaleEnum.horseClearedOther, horseName, targetName);
						}
						zh.getEM().payCommand(p, command);
					}
				}
			}
			else if (displayConsole) {
				zh.getMM().sendMessage(s, LocaleEnum.missingTarget);
				sendCommandUsage(subCommand, true);
			}
		}
	}

}
