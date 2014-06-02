package org.stormdev.uuidcorrector.main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.stormdev.UUIDAPI.PlayerIDFinder;
import org.stormdev.UUIDAPI.PlayerIDFinder.MojangID;
import org.stormdev.UUIDAPI.UUIDLoadEvent;

public class Core extends JavaPlugin implements Listener {
	private Map<String, MojangID> handledLogins = new HashMap<String, MojangID>();
	public static Core plugin;
	
	@Override
	public void onEnable(){
		plugin = this;
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler(priority = EventPriority.LOWEST) //Called first
	void prePreJoin(AsyncPlayerPreLoginEvent event){
		final String name = event.getName();
		
		try {
			MojangID mid = PlayerIDFinder.getMojangID(name);
			handledLogins.put(name, mid);
			//Sadly CANNOT correct event.getUniqueID() because it's final...
		}
		catch (Exception e){
			//e.printStackTrace();
			//Oh well
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable(){

			@Override
			public void run() {
				if(handledLogins.containsKey(name)){ //They were kicked or something
					handledLogins.remove(name);
				}
				return;
			}}, 20*20l);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	void login(PlayerLoginEvent event){
		final Player player = event.getPlayer();
		final String pName = player.getName();
		if(handledLogins.containsKey(pName)){
			UUID id = PlayerIDFinder.getAsUUID(handledLogins.get(pName).getID());
			PlayerIDFinder.PlayerReflect.setPlayerUUID(player, id);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	void join(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable(){

			@Override
			public void run() {
				loadUUID(player);
				getLogger().info("Corrected UUID of "+player.getName()+" to "+player.getUniqueId().toString());
			}});
	}
	
	private UUID loadUUID(Player player){
		MojangID id;
		UUID uid;
		
		if(handledLogins.containsKey(player.getName())){
			id = handledLogins.get(player.getName());
			uid = PlayerIDFinder.getAsUUID(id.getID());
			handledLogins.remove(player.getName());
			PlayerIDFinder.PlayerReflect.setPlayerUUID(player, uid);
		}
		else {
			
			id = PlayerIDFinder.getMojangID(player);
			try {
				uid = PlayerIDFinder.getAsUUID(id.getID());
			} catch (Exception e) {
				uid = player.getUniqueId();
			}
			if(id == null){
				return UUID.randomUUID();
			}
		}
		
		final UUIDLoadEvent evt = new UUIDLoadEvent(player, id, uid);
		Bukkit.getScheduler().callSyncMethod(plugin, new Callable<Void>(){

			@Override
			public Void call() throws Exception {
				Bukkit.getPluginManager().callEvent(evt);
				return null;
			}});
		return uid;
	}
}
