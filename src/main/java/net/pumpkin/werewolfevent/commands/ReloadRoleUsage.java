package net.pumpkin.werewolfevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadRoleUsage implements CommandExecutor {

    // References to RollCommand, SeerCommand, CupidCommand, and HunterCommand to manage role states
    private final RoleCommand rollCommand;
    private final SeerCommand seerCommand;
    private final CupidCommand cupidCommand;
    private final HunterCommand hunterCommand;

    // Constructor to initialize RollCommand, SeerCommand, CupidCommand, and HunterCommand
    public ReloadRoleUsage(RoleCommand rollCommand, SeerCommand seerCommand, CupidCommand cupidCommand, HunterCommand hunterCommand) {
        this.rollCommand = rollCommand;
        this.seerCommand = seerCommand;
        this.cupidCommand = cupidCommand;
        this.hunterCommand = hunterCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure that only OPs can execute this command
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
            return false;
        }


        // Check for the argument
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /reloadroleusage <role|all>");
            return false;
        }

        String role = args[0].toLowerCase();

        // Reload logic for all roles
        if (role.equals("all")) {
            resetAllRoles(player);
            return true;
        }

        // Reload logic for the Seer role
        if (role.equals("seer")) {
            resetSeerUsage(player);
            return true;
        }

        // Reload logic for the Cupid role
        if (role.equals("cupid")) {
            resetCupidUsage(player);
            return true;
        }

        // Reload logic for the Hunter role
        if (role.equals("hunter")) {
            resetHunterUsage(player);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Invalid role specified. Supported roles: 'seer', 'cupid', 'hunter', or 'all'.");
        return false;
    }

    // Helper method to reset all roles
    private void resetAllRoles(Player player) {
        // Reset the Seer's usage ability
        resetSeerUsage(player);

        // Reset the Cupid's linked players and usage ability
        resetCupidUsage(player);

        // Reset the Hunter's last stand target and usage ability
        resetHunterUsage(player);

        player.sendMessage(ChatColor.GREEN + "All roles have been reloaded. Everyone's role usage has been reset.");
    }

    // Helper method to reset the Seer's ability usage for all Seers
    private void resetSeerUsage(Player player) {
        seerCommand.getSeerUsed().clear(); // This clears the usage list for Seers
        player.sendMessage(ChatColor.GREEN + "Seer role usage has been reloaded. Seers can now use their ability again.");
    }

    // Helper method to reset the Cupid's linked players and usage ability
    private void resetCupidUsage(Player player) {
        cupidCommand.reloadCupid(); // This resets the linked players and Cupid usage
        player.sendMessage(ChatColor.GREEN + "Cupid's role usage has been reloaded. Cupid can now link players again.");
    }

    // Helper method to reset the Hunter's last stand target and usage ability
    private void resetHunterUsage(Player player) {
        hunterCommand.reloadHunter(); // This resets the Hunter's last stand targets
        player.sendMessage(ChatColor.GREEN + "Hunter's role usage has been reloaded. Hunters can now choose last stand targets again.");
    }
}
