package net.pumpkin.werewolfevent;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnKillForceSpectator implements Listener {

    private final JavaPlugin plugin;

    public OnKillForceSpectator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Get the player who died
        var player = event.getEntity();

        // Set the player's game mode to spectator
        player.setGameMode(GameMode.SPECTATOR);
    }
}
