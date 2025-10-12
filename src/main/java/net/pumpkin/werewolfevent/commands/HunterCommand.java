package net.pumpkin.werewolfevent.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HunterCommand implements CommandExecutor, Listener {

    private final RoleCommand rollCommand;
    private final Map<String, String> hunterLastStandTargets = new HashMap<>(); // Tracks hunter's last stand target
    private final Set<String> huntersWhoUsedCommand = new HashSet<>(); // Tracks Hunters who have already used the command

    // Constructor that takes RollCommand as a parameter to access roles
    public HunterCommand(RoleCommand rollCommand) {
        this.rollCommand = rollCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return false;
        }

        Player player = (Player) sender;

        // Check if the player is a Hunter
        if (!rollCommand.hasRole(player) || !rollCommand.getRole(player).equals("Hunter")) {
            player.sendMessage(ChatColor.RED + "You must be a Hunter to use this command!");
            return false;
        }

        // Check if the Hunter has already used the command
        if (huntersWhoUsedCommand.contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can only use the /laststand command once!");
            return false;
        }

        // Ensure that a target player is provided
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /laststand <playername>");
            return false;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        // Ensure the target player is online
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "The player " + targetPlayerName + " is not online!");
            return false;
        }

        // Set the target for the Hunter's last stand
        hunterLastStandTargets.put(player.getName(), targetPlayer.getName());

        // Mark the Hunter as having used the command
        huntersWhoUsedCommand.add(player.getName());

        // Inform the Hunter and the target player
        player.sendMessage(ChatColor.GREEN + "You have chosen " + targetPlayerName + " for your last stand.");

        return true;
    }

    // Event listener to handle the Hunter's death and the death of the selected target
    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player deadPlayer = (Player) event.getEntity();
        String deadPlayerName = deadPlayer.getName();

        // Check if the dead player is a Hunter
        if (hunterLastStandTargets.containsKey(deadPlayerName)) {
            // Get the target player
            String targetPlayerName = hunterLastStandTargets.get(deadPlayerName);
            Player targetPlayer = deadPlayer.getServer().getPlayer(targetPlayerName);

            // Remove the last stand target for this Hunter
            hunterLastStandTargets.remove(deadPlayerName);

            // Kill the target player as well
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.setHealth(0);
                targetPlayer.sendMessage(ChatColor.RED + "You have died because your Hunter has died in the last stand!");
            }

            // Broadcast to everyone that both the Hunter and the target have died
            Bukkit.broadcastMessage(ChatColor.RED + deadPlayerName + " has died in a last stand with " + targetPlayerName + ", who also perished!");
        }
    }

    // Method to reset the Hunter's last stand targets and usage state
    public void reloadHunter() {
        hunterLastStandTargets.clear(); // Reset Hunter's last stand targets
        huntersWhoUsedCommand.clear(); // Allow all Hunters to use the command again
    }
}
