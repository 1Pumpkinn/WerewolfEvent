package net.pumpkin.werewolfevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CupidCommand implements CommandExecutor, Listener {

    private final RoleCommand rollCommand;
    private final Map<String, String> linkedPlayers = new HashMap<>(); // Tracks linked players
    private final Set<String> cupidUsed = new HashSet<>(); // To track if the Cupid has already used the match ability
    private final Set<String> damagedPlayers = new HashSet<>(); // Track players that have been damaged in the current event

    // Constructor that takes RollCommand as a parameter to access roles
    public CupidCommand(RoleCommand rollCommand) {
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

        // Check if the player is a Cupid
        if (!rollCommand.hasRole(player) || !rollCommand.getRole(player).equals("Cupid")) {
            player.sendMessage(ChatColor.RED + "You must be a Cupid to use this command!");
            return false;
        }

        // Check if the Cupid has already used their ability
        if (cupidUsed.contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You have already used your ability to link players!");
            return false;
        }

        // Ensure that two player names are provided
        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Usage: /match <playername1> <playername2>");
            return false;
        }

        String playerName1 = args[0];
        String playerName2 = args[1];
        Player targetPlayer1 = player.getServer().getPlayer(playerName1);
        Player targetPlayer2 = player.getServer().getPlayer(playerName2);

        // Ensure both players are online
        if (targetPlayer1 == null || targetPlayer2 == null) {
            player.sendMessage(ChatColor.RED + "One or both players are not online!");
            return false;
        }

        // Link the players' lives
        linkedPlayers.put(targetPlayer1.getName(), targetPlayer2.getName());
        linkedPlayers.put(targetPlayer2.getName(), targetPlayer1.getName());

        // Inform the Cupid and the players that their lives are linked
        player.sendMessage(ChatColor.GREEN + "You have successfully linked " + targetPlayer1.getName() + " and " + targetPlayer2.getName() + "'s lives!");
        targetPlayer1.sendMessage(ChatColor.GREEN + "You have been linked with " + targetPlayer2.getName() + "'s life by Cupid.");
        targetPlayer2.sendMessage(ChatColor.GREEN + "You have been linked with " + targetPlayer1.getName() + "'s life by Cupid.");

        // Mark that the Cupid has used their ability (one-time use)
        cupidUsed.add(player.getName());

        return true;
    }

    // Event listener to handle the death of linked players
    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player deadPlayer = (Player) event.getEntity();
        String deadPlayerName = deadPlayer.getName();

        // Check if the dead player is part of a linked pair
        if (linkedPlayers.containsKey(deadPlayerName)) {
            String linkedPlayerName = linkedPlayers.get(deadPlayerName);
            Player linkedPlayer = deadPlayer.getServer().getPlayer(linkedPlayerName);

            // Unlink the players after the death
            linkedPlayers.remove(deadPlayerName);
            linkedPlayers.remove(linkedPlayerName);

            // Notify the linked player that the link has been broken
            if (linkedPlayer != null) {
                linkedPlayer.sendMessage(ChatColor.RED + "Your life link with " + deadPlayerName + " has been broken because they died.");

                // Kill the linked player after their link is broken
                linkedPlayer.setHealth(0);
                linkedPlayer.sendMessage(ChatColor.RED + "You have died because your link was broken.");
            }

            // Remove link info from the dead player's side
            deadPlayer.sendMessage(ChatColor.RED + "Your life link with " + linkedPlayerName + " has been broken because you died.");
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player damagedPlayer = (Player) entity;
        String damagedPlayerName = damagedPlayer.getName();

        // Prevent recursive damage handling by checking if the player is already in the damaged set
        if (damagedPlayers.contains(damagedPlayerName)) {
            return;
        }

        // Add the player to the set to prevent recursion
        damagedPlayers.add(damagedPlayerName);

        // Check if the damaged player has a linked partner (Cupid)
        if (linkedPlayers.containsKey(damagedPlayerName)) {
            String linkedPlayerName = linkedPlayers.get(damagedPlayerName);
            Player linkedPlayer = damagedPlayer.getServer().getPlayer(linkedPlayerName);

            // Check if both the damaged and linked players are Cupids
            if (linkedPlayer != null && linkedPlayer.isOnline()) {
                // Ensure the event is caused by a player (EntityDamageByEntityEvent)
                if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent entityDamageEvent = (EntityDamageByEntityEvent) event;

                    // Get the damager (attacker)
                    Entity damager = entityDamageEvent.getDamager();

                    if (damager instanceof Player) {
                        Player attacker = (Player) damager;
                        // Get the role of the player causing the damage
                        String attackerRole = rollCommand.getRole(attacker);

                        // Allow damage only if the attacker has the "Wolf" role
                        if (!"Wolf".equals(attackerRole)) {
                            event.setCancelled(true);  // Cancel damage if the attacker is not a "Wolf"
                            attacker.sendMessage(ChatColor.RED + "You can only damage Cupids if you are a Wolf!");
                        } else {
                            // If the attacker is a "Wolf", synchronize health between the two linked players
                            double newHealth = damagedPlayer.getHealth() - event.getDamage();

                            // Clamp health to a minimum of 0 and a maximum of the player's max health
                            newHealth = Math.max(0, Math.min(newHealth, linkedPlayer.getMaxHealth()));

                            // Set health for both players
                            linkedPlayer.setHealth(newHealth);
                            damagedPlayer.setHealth(newHealth);
                        }
                    }
                }
            }
        }

        // Remove the player from the damaged set after the event is processed
        damagedPlayers.remove(damagedPlayerName);
    }



    public void reloadCupid() {
        linkedPlayers.clear(); // Reset linked players
        cupidUsed.clear(); // Reset Cupid's usage
    }
}
