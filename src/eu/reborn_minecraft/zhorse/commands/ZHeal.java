package eu.reborn_minecraft.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;

import eu.reborn_minecraft.zhorse.ZHorse;

public class ZHeal extends Command {

	public ZHeal(ZHorse zh, CommandSender s, String[] a) {
		super(zh, a, s);
		idAllow = true;
		targetAllow = false;
		if (isPlayer()) {
			if (analyseArguments()) {
				if (hasPermission()) {
					if (isWorldEnabled()) {
						if (!(idMode || targetMode)) {
							if (isOnHorse()) {
								horse = (Horse)p.getVehicle();
								execute();
							}
						}
						else {
							if (idMode) {
								if (zh.getUM().isRegistered(targetUUID, userID)) {
									horse = zh.getUM().getHorse(targetUUID, userID);
									if (horse != null) {
										execute();
									}
									else if (displayConsole) {
										s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseNotFound), zh.getUM().getHorseName(horse)));
									}
								}
								else if (displayConsole) {
									sendUnknownHorseMessage(targetName);
								}
							}
							else if (displayConsole){
								sendCommandUsage();
							}
						}
					}
				}
			}
		}
	}

	private void execute() {
		if (isRegistered()) {
			if (isOwner()) {
				if (zh.getEM().isReadyToPay(p, command)) {
					Damageable dm = horse;
					dm.setHealth(dm.getMaxHealth());
					if (displayConsole) {
						s.sendMessage(String.format(zh.getLM().getCommandAnswer(zh.getLM().horseHealed), horseName));
					}
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}

}