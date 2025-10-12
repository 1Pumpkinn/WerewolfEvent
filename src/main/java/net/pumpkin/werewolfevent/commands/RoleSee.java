package net.pumpkin.werewolfevent.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RoleSee implements CommandExecutor {

    private final RoleCommand rollCommand; // Reference to RollCommand to check roles

    // Constructor to get an instance of RollCommand
    public RoleSee(RoleCommand rollCommand) {
        this.rollCommand = rollCommand;
    }

    // Map of roles to their corresponding ChatColor
    private final Map<String, ChatColor> roleColors = new HashMap<String, ChatColor>() {{
        put("Hunter", ChatColor.DARK_GREEN);
        put("Seer", ChatColor.BLUE);
        put("Wolf", ChatColor.RED);
        put("Villager", ChatColor.YELLOW);
        put("Cupid", ChatColor.LIGHT_PURPLE);
        put("Sacrifice", ChatColor.DARK_RED);
    }};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        // Check if the player has permission to see other players' roles
        if (!player.hasPermission("rollsee.view")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to see other players' roles.");
            return true;
        }

        // Check if a player was specified in the arguments
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /rollsee <player>");
            return true;
        }

        // Get the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "The specified player is not online.");
            return true;
        }

        // Get the target player's role
        String role = rollCommand.getRole(target);

        // Check if the role exists and apply the color
        if (role == null) {
            player.sendMessage(ChatColor.RED + target.getName() + " does not have a role.");
        } else {
            // Color the role based on the mapping
            ChatColor roleColor = roleColors.getOrDefault(role, ChatColor.WHITE);  // Default to white if no color is found
            player.sendMessage(ChatColor.GREEN + target.getName() + "'s role: " + roleColor + role);
        }

        return true;
    }
}
