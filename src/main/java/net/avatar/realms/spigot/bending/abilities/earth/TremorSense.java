package net.avatar.realms.spigot.bending.abilities.earth;

import net.avatar.realms.spigot.bending.abilities.*;
import net.avatar.realms.spigot.bending.controller.ConfigurationParameter;
import net.avatar.realms.spigot.bending.utils.BlockTools;
import net.avatar.realms.spigot.bending.utils.EntityTools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Nokorbis on 02/03/2016.
 */
@ABendingAbility(name = TremorSense.NAME, element = BendingElement.EARTH, shift = true)
public class TremorSense extends BendingActiveAbility {

    public static final String NAME = "TremorSense";

    private static final PotionEffect BLIND = new PotionEffect(PotionEffectType.BLINDNESS, 20, 0);
    private static final PotionEffect GLOW = new PotionEffect(PotionEffectType.GLOWING, 10000*20/1000, 2);
    private static final int[][] RELATIVES = {{1,0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    @ConfigurationParameter("Base-Distance")
    private static int BASE_DISTANCE = 5;

    @ConfigurationParameter("Max-Distance")
    private static int MAX_DISTANCE = 50;

    @ConfigurationParameter("Distance-Increment")
    private static int DISTANCE_INC = 5;

    private int currentDistance;

    private long lastIncrementTime;
    private boolean shouldBlind;
    
    private Map<Integer, LivingEntity> entities;

    public TremorSense(RegisteredAbility register, Player player) {
        super(register, player);
        entities = new HashMap<Integer, LivingEntity>();
    }

	@Override
    public boolean sneak() {
        currentDistance = BASE_DISTANCE;
        lastIncrementTime = startedTime;
        shouldBlind = true;

        setState(BendingAbilityState.PROGRESSING);
        return true;
    }

    @Override
    public boolean canTick() {
        if(!super.canTick()) {
        	return false;
        }

        if (!isOnEarth(player)) {
            return false;
        }

        if (bender.isOnCooldown(NAME)) {
            return false;
        }
        return true;
    }

    @Override
    protected long getMaxMillis() {
        return 1000 * 60 * 5; // 5 minutes
    }

    @Override
    public boolean canBeUsedWithTools() {
        return true;
    }

    @Override
    public Object getIdentifier() {
        return player;
    }

    @Override
    public void progress() {
        if (!getState().equals(BendingAbilityState.PROGRESSING)) {
            return;
        }
        if (!player.isSneaking()) {
            remove();
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastIncrementTime >= 1000) {
            if (currentDistance < MAX_DISTANCE) {
                currentDistance += DISTANCE_INC;
                if (currentDistance > MAX_DISTANCE) {
                    currentDistance = MAX_DISTANCE;
                }
            }
            lastIncrementTime = now;
            if (shouldBlind) {
            	player.addPotionEffect(BLIND);
                shouldBlind = false;
            }
            else {
                shouldBlind = true;
            }
        }

        
        for (LivingEntity livingEntity : EntityTools.getLivingEntitiesAroundPoint(player.getLocation(), currentDistance)) {
            if (player.getEntityId() != livingEntity.getEntityId() && isOnEarth(livingEntity) && !entities.containsKey(livingEntity.getEntityId())) {
                livingEntity.addPotionEffect(GLOW);
                entities.put(livingEntity.getEntityId(), livingEntity);
            }
        }
    }

    @Override
    public void stop() {
    	for(LivingEntity entity : entities.values()) {
    		entity.removePotionEffect(PotionEffectType.GLOWING);
    	}
    	entities.clear();
        bender.cooldown(NAME, (System.currentTimeMillis() - startedTime) / 10);
    }

    public boolean isOnEarth(LivingEntity entity) {
        if (!entity.isOnGround()) {
            return false;
        }
        Block block = entity.getLocation().clone().add(0, -1, 0).getBlock();
        if (BlockTools.isEarthbendable(player, block)) {
            return true;
        }
        for (int[] rel : RELATIVES) {
            Block relative = block.getRelative(rel[0], 0, rel[1]);
            if (BlockTools.isEarthbendable(player, relative)) {
                return true;
            }
        }
        return false;
    }

	public Map<Integer, LivingEntity> getEntities() {
		return entities;
	}
    
}