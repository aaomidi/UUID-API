package com.aaomidi.dev.uuidapi.events;

import com.aaomidi.dev.uuidapi.UUIDAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginEvent implements Listener {
    private final UUIDAPI _plugin;

    public LoginEvent(UUIDAPI plugin) {
        _plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        _plugin.getDatabaseManager().initializePlayer(player);
    }
}
