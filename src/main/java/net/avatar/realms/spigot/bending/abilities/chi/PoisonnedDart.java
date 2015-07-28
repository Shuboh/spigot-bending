package net.avatar.realms.spigot.bending.abilities.chi;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.avatar.realms.spigot.bending.Bending;
import net.avatar.realms.spigot.bending.abilities.Abilities;
import net.avatar.realms.spigot.bending.abilities.Ability;
import net.avatar.realms.spigot.bending.abilities.AbilityManager;
import net.avatar.realms.spigot.bending.utils.BlockTools;
import net.avatar.realms.spigot.bending.utils.EntityTools;
import net.avatar.realms.spigot.bending.utils.ParticleEffect;
import net.avatar.realms.spigot.bending.utils.ProtectionManager;

/**
 * 
 * This ability throws a poisonned dart to straight foward.
 * If the darth hit an entity, this entity gets poisonned.
 * The type of poisonned can change if specifics items are hold in hand.
 *
 */
public class PoisonnedDart extends Ability{
	
	private static final int DAMAGE = Bending.plugin.configuration.getIntAttribute(configPrefix + "Chi.PoisonnedDart.Damage");
	private static final int RANGE = Bending.plugin.configuration.getIntAttribute(configPrefix + "Chi.PoisonnedDart.Range");
	private static final long COOLDOWN = Bending.plugin.configuration.getLongAttribute(configPrefix + "Chi.PoisonnedDart.Cooldown");
	
	private static final ParticleEffect VISUAL = ParticleEffect.VILLAGER_HAPPY;
	
	private Location origin;
	private Location location;
	private Vector direction;
	private List<PotionEffect> effects;
	private boolean started;
	
	public PoisonnedDart(Player player) {
		super(player, null);
		
		if (!canContinue) {
			return;
		}
		started = false;
		AbilityManager.getManager().addInstance(this);
	}
	
	@Override
	public boolean swing() {
		
		if (started || !canContinue) {
			return true;
		}
		
		origin = player.getEyeLocation();
		location = origin.clone();
		direction = origin.getDirection().normalize();
		
		started = true;
		
		ItemStack is = player.getItemInHand();
		effects = new LinkedList<PotionEffect>();
		switch (is.getType()) {
			case MILK_BUCKET:
				effects = null;
				is.setType(Material.BUCKET);
				is.setAmount(1);
				break;
			case POTION:
				Potion potion = Potion.fromItemStack(is);
				effects.addAll(potion.getEffects());
				is.setType(Material.GLASS_BOTTLE);
				is.setAmount(1);
				break;
			case EYE_OF_ENDER:
				effects.add(new PotionEffect(PotionEffectType.BLINDNESS,20*10,1));
				if (is.getAmount() == 1) {
					player.getInventory().remove(is);
				}
				else {
					is.setAmount(is.getAmount() - 1);
				}
				break;
			case MUSHROOM_SOUP:
				effects.add(new PotionEffect(PotionEffectType.CONFUSION,20*12,1));
				is.setType(Material.BOWL);
				is.setAmount(1);
				break;
			case SKULL_ITEM:
				byte data = is.getData().getData();
				// If this is a wither skull
				if (data == 1) {
					effects.add(new PotionEffect(PotionEffectType.WITHER, 20 * 15, 1));
					if (is.getAmount() == 1) {
						player.getInventory().remove(is);
					}
					else {
						is.setAmount(is.getAmount() - 1);
					}
				}
				break;
			default : 
				effects.add(new PotionEffect(PotionEffectType.POISON, 20*1, 0));
				break;
		}

		origin.getWorld().playSound(origin, Sound.SHOOT_ARROW, 10, 1);
		
		bender.cooldown(Abilities.PoisonnedDart, COOLDOWN);
		
		return false;
	}

	public boolean progress() {
		
		if (!started) {
			return true;
		}
		
		if (!super.progress()) {
			return false;
		}
		
		if (!player.getWorld().equals(location.getWorld())) {
			return false;
		}
		if (location.distance(origin) > RANGE) {
			return false;
		}
		
		if (BlockTools.isSolid(location.getBlock())) {
			return false;
		}
		
		advanceLocation();
		if (!affectAround()) {
			return false;
		}
		return true;
	}
	
	private boolean affectAround() {
		if (ProtectionManager.isRegionProtectedFromBending(player, Abilities.PoisonnedDart, location)) {
			return false;
		}		
		int cptEnt = 0;
		boolean health = areHealthEffects();
		for (LivingEntity entity : EntityTools.getLivingEntitiesAroundPoint(location, 2.1)) {
			if (entity.getEntityId() == player.getEntityId()) {
				continue;
			}
			
			if (effects == null) {
				for (PotionEffect effect : entity.getActivePotionEffects()) {
					entity.removePotionEffect(effect.getType());
				}
				entity.getActivePotionEffects().clear();
			}
			else {
				for (PotionEffect effect : effects) {
					entity.addPotionEffect(effect);	
				}
			}
			if (!health && effects != null) {
				EntityTools.damageEntity(player, entity, DAMAGE);
			}
			cptEnt++;
			break;
		}

		if (cptEnt > 0) {
			return false;
		}
		return true;
	}
	
	private boolean areHealthEffects() {
		if (effects == null) {
			return false;
		}
		for (PotionEffect effect : effects) {
			if (effect.getType().equals(PotionEffectType.HEAL)
					|| effect.getType().equals(PotionEffectType.HEALTH_BOOST)
					|| effect.getType().equals(PotionEffectType.REGENERATION)) {
				return true;
			}
		}		
		
		return false;
	}
	private void advanceLocation() {
		VISUAL.display(0, 0, 0, 1, 1, location, 20);
		location = location.add(direction.clone().multiply(1.5));
	}
	
	@Override
	public boolean canBeInitialized() {
		if (!super.canBeInitialized()) {
			return false;
		}
		
		if (AbilityManager.getManager().getPoisonnedDarts().containsKey(player)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public Abilities getAbilityType() {
		return Abilities.PoisonnedDart;
	}
	
	@Override
	public void remove() {
		AbilityManager.getManager().getPoisonnedDarts().remove(player);		
	}
	
}
