package org.stormdev.UUIDAPI;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.stormdev.UUIDAPI.PlayerIDFinder.MojangID;

/**
 * An event called when a player's UUID is loaded onJoin
 */
public class UUIDLoadEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player = null;
	private MojangID mid;
	private UUID uuid;

	public UUIDLoadEvent(Player player, MojangID mojangID, UUID id) {
		this.player = player;
		this.mid = mojangID;
		this.uuid = id;
	}
	
	/**
	 * Get the UUID of the player as a UUID
	 * @return The UUID
	 */
	public UUID getUUID(){
		return uuid;
	}

	/**
	 * The UUID of the player
	 * @return The UUID object
	 */
	public MojangID getMojangID(){
		return mid;
	}

	/**
	 * The player who's UUID was loaded
	 * @return That player
	 */
	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
