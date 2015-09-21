package net.avatar.realms.spigot.bending.commands.subcommands;

import java.util.List;

import org.bukkit.command.CommandSender;

import net.avatar.realms.spigot.bending.Bending;
import net.avatar.realms.spigot.bending.Messages;
import net.avatar.realms.spigot.bending.abilities.AbilityManager;
import net.avatar.realms.spigot.bending.commands.BendingCommand;
import net.md_5.bungee.api.ChatColor;

public class ReloadExecution extends BendingCommand {

	public ReloadExecution() {
		super();
		this.command = "reload";
	}

	@Override
	public boolean execute(CommandSender sender, List<String> args) {
		if (sender.hasPermission("bending.command.reload")) {
			AbilityManager.getManager().stopAllAbilities();
			AbilityManager.getManager().applyConfiguration(Bending.plugin.getDataFolder());
			sender.sendMessage(ChatColor.GREEN + Messages.RELOADED);
		}
		return true;
	}

	@Override
	public void printUsage(CommandSender sender) {
		if (!sender.hasPermission("bending.command.reload")) {
			sender.sendMessage("/bending reload");
		}
	}
}
