package net.avatarrealms.minecraft.bending.abilities.chi;

import net.avatarrealms.minecraft.bending.controller.ConfigManager;
import net.avatarrealms.minecraft.bending.model.Abilities;
import net.avatarrealms.minecraft.bending.model.BendingPlayer;
import net.avatarrealms.minecraft.bending.utils.Tools;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HighJump {

	private double jumpheight = ConfigManager.jumpHeight;

	// private Map<String, Long> cooldowns = new HashMap<String, Long>();

	public HighJump(Player p) {
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(p);

		if (bPlayer.isOnCooldown(Abilities.HighJump))
			return;
		// if (cooldowns.containsKey(p.getName())
		// && cooldowns.get(p.getName()) + cooldown >= System
		// .currentTimeMillis())
		// return;
		jump(p);
	}

	private void jump(Player p) {
		if (!Tools.isSolid(p.getLocation().getBlock()
				.getRelative(BlockFace.DOWN)))
			return;
		Vector vec = p.getVelocity();
		vec.setY(jumpheight);
		p.setVelocity(vec);
		// cooldowns.put(p.getName(), System.currentTimeMillis());
		BendingPlayer.getBendingPlayer(p).cooldown(Abilities.HighJump);
		
		return;
	}

	public static String getDescription() {
		return "To use this ability, simply click. You will jump quite high. This ability has a short cooldown.";
	}
}
