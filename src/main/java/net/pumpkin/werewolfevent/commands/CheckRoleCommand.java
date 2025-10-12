package net.pumpkin.werewolfevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckRoleCommand implements CommandExecutor {

    private final RoleCommand rollCommand;

    // Constructor to inject the RollCommand (where roles are tracked)
    public CheckRoleCommand(RoleCommand rollCommand) {
        this.rollCommand = rollCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has a role
        if (rollCommand.hasRole(player)) {
            // Get the player's role from the RollCommand
            String role = rollCommand.getRole(player);
            player.sendMessage(ChatColor.GREEN + "Your role is: " + ChatColor.YELLOW + role);
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a role yet. Use /roll to get one!");
        }

        return true;
    }
}
