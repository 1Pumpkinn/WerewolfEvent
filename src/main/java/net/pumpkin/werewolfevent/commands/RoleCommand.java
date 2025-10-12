package net.pumpkin.werewolfevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.*;

public class RoleCommand implements CommandExecutor, Listener {

    private final Random random = new Random();
    public final Map<String, String> playerRoles = new HashMap<>();
    public final Map<String, ChatColor> roleColors = Map.of(
            "Hunter", ChatColor.DARK_GREEN,
            "Seer", ChatColor.BLUE,
            "Wolf", ChatColor.RED,
            "Villager", ChatColor.YELLOW,
            "Cupid", ChatColor.LIGHT_PURPLE,
            "Sacrifice", ChatColor.DARK_RED
    );

    public RoleCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return assignRandomRoles(sender);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("forceroll")) {
            return forceAssignRole(sender, args);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid command usage.");
            return false;
        }
    }

    private boolean assignRandomRoles(CommandSender sender) {
        List<Player> onlinePlayers = new ArrayList<>(sender.getServer().getOnlinePlayers());


        Collections.shuffle(onlinePlayers);
        playerRoles.clear();

        // Hardcoded role capacities
        Map<String, Integer> roleCap = new HashMap<>();
        roleCap.put("Hunter", 1);
        roleCap.put("Seer", 1);
        roleCap.put("Wolf", 1); // Ensuring exactly one Wolf
        roleCap.put("Cupid", 1);
        roleCap.put("Sacrifice", 1);
        roleCap.put("Villager", Integer.MAX_VALUE); // Default role

        List<String> availableRoles = new ArrayList<>(roleCap.keySet());

        // Ensure exactly one Wolf
        Player wolfPlayer = onlinePlayers.remove(0);
        playerRoles.put(wolfPlayer.getName(), "Wolf");
        ChatColor wolfColor = roleColors.get("Wolf");
        wolfPlayer.sendTitle(ChatColor.GREEN + "Your Role:", wolfColor + "Wolf", 10, 70, 20);
        wolfPlayer.sendMessage(ChatColor.GOLD + "You have been assigned the role: " + wolfColor + "Wolf");
        roleCap.put("Wolf", 0);

        for (Player player : onlinePlayers) {
            String chosenRole = getRandomAvailableRole(availableRoles, roleCap);
            playerRoles.put(player.getName(), chosenRole);

            ChatColor roleColor = roleColors.getOrDefault(chosenRole, ChatColor.WHITE);
            player.sendTitle(ChatColor.GREEN + "Your Role:", roleColor + chosenRole, 10, 70, 20);
            player.sendMessage(ChatColor.GOLD + "You have been assigned the role: " + roleColor + chosenRole);
        }

        sender.sendMessage(ChatColor.GREEN + "Roles have been assigned to all eligible players.");
        return true;
    }

    private boolean forceAssignRole(CommandSender sender, String[] args) {
        String targetPlayerName = args[1];
        String role = args[2];

        if (!roleColors.containsKey(role)) {
            sender.sendMessage(ChatColor.RED + "Invalid role specified.");
            return false;
        }

        Player targetPlayer = sender.getServer().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player " + targetPlayerName + " is not online.");
            return false;
        }

        playerRoles.put(targetPlayerName, role);
        ChatColor roleColor = roleColors.get(role);
        targetPlayer.sendTitle(ChatColor.GREEN + "Your Role:", roleColor + role, 10, 70, 20);
        targetPlayer.sendMessage(ChatColor.GOLD + "You have been forced into the role: " + roleColor + role);
        sender.sendMessage(ChatColor.GREEN + "You have forced " + targetPlayerName + " into the role: " + roleColor + role);

        return true;
    }

    private String getRandomAvailableRole(List<String> availableRoles, Map<String, Integer> roleCap) {
        List<String> validRoles = new ArrayList<>();

        for (String role : availableRoles) {
            if (roleCap.getOrDefault(role, 0) > 0) {
                validRoles.add(role);
            }
        }

        if (validRoles.isEmpty()) return "Villager"; // Default role

        String chosenRole = validRoles.get(random.nextInt(validRoles.size()));
        roleCap.put(chosenRole, roleCap.get(chosenRole) - 1);

        if (roleCap.get(chosenRole) <= 0) {
            availableRoles.remove(chosenRole);
        }

        return chosenRole;
    }

    public boolean hasRole(Player player) {
        return playerRoles.containsKey(player.getName());
    }

    public String getRole(Player player) {
        return playerRoles.get(player.getName());
    }

    // Event listener for entity damage
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Check if the damager is a player
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            String role = getRole(attacker);

            // Only allow damage if the attacker is a "Wolf"
            if (!"Wolf".equals(role)) {
                // If the attacker is not a Wolf and is damaging another player, cancel the event
                if (event.getEntity() instanceof Player) { // Ensure the entity being damaged is a player
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "Only the Wolf can deal damage to players!");
                }
            }
        }
    }
}

