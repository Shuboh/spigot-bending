package net.bendercraft.spigot.bending.abilities.arts;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.bendercraft.spigot.bending.abilities.ABendingAbility;
import net.bendercraft.spigot.bending.abilities.BendingActiveAbility;
import net.bendercraft.spigot.bending.abilities.BendingElement;
import net.bendercraft.spigot.bending.abilities.RegisteredAbility;
import net.bendercraft.spigot.bending.controller.ConfigurationParameter;
import net.bendercraft.spigot.bending.utils.BlockTools;
import net.bendercraft.spigot.bending.utils.Tools;

@ABendingAbility(name = HighJump.NAME, element = BendingElement.MASTER, shift=false)
public class HighJump extends BendingActiveAbility {
	public final static String NAME = "HighJump";

	@ConfigurationParameter("Height")
	private static int JUMP_HEIGHT = 7;

	@ConfigurationParameter("Cooldown")
	private static long COOLDOWN = 3500;

	public HighJump(RegisteredAbility register, Player player) {
		super(register, player);
	}

	@Override
	public boolean swing() {
		if (makeJump()) {
			this.bender.cooldown(NAME, COOLDOWN);
		}
		return true;
	}

	private boolean makeJump() {
		if (!BlockTools.isSolid(this.player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
			return false;
		}
		int height = JUMP_HEIGHT;
		Vector vec = Tools.getVectorForPoints(this.player.getLocation(), this.player.getLocation().add(this.player.getVelocity()).add(0, height, 0));
		this.player.setVelocity(vec);
		return true;
	}

	@Override
	public Object getIdentifier() {
		return this.player;
	}

	@Override
	public void progress() {
		
	}

	@Override
	public void stop() {
		
	}
}
