package org.stormdev.uuidcorrector.main;

import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.stormdev.UUIDAPI.PlayerIDFinder;
import org.stormdev.UUIDAPI.PlayerIDFinder.MojangID;
import org.stormdev.UUIDAPI.UUIDLoadEvent;

public class Core extends JavaPlugin implements Listener {
	private static Core plugin;
	@Override
	public void onEnable(){
		plugin = this;
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	void join(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable(){

			@Override
			public void run() {
				MojangID id = PlayerIDFinder.getMojangID(player);
				UUID uid;
				try {
					uid = PlayerIDFinder.getAsUUID(id.getID());
				} catch (Exception e) {
					uid = player.getUniqueId();
				}
				if(id == null){
					return;
				}
				
				final UUIDLoadEvent evt = new UUIDLoadEvent(player, id, uid);
				Bukkit.getScheduler().callSyncMethod(plugin, new Callable<Void>(){

					@Override
					public Void call() throws Exception {
						Bukkit.getPluginManager().callEvent(evt);
						return null;
					}});
				return;
			}});
	}
}
