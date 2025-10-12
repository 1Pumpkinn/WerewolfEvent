package net.pumpkin.werewolfevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class SeerCommand implements CommandExecutor, Listener {

    private final RoleCommand rollCommand;
    private final Set<String> seerUsed = new HashSet<>(); // Tracks Seers who have used their ability

    public SeerCommand(RoleCommand rollCommand) {
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

        // Check if the player is a Seer
        if (!rollCommand.hasRole(player) || !rollCommand.getRole(player).equals("Seer")) {
            player.sendMessage(ChatColor.RED + "You must be a Seer to use this command!");
            return false;
        }

        // Check if the Seer has already used their ability
        if (seerUsed.contains(player.getName())) {
            player.sendMessage(ChatColor.RED + "You have already used your ability to see another player's role.");
            return false;
        }

        // Check if player provided a target name
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /see <playername>");
            return false;
        }

        String targetName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetName);

        // Check if the target player is online
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Player " + targetName + " is not online!");
            return false;
        }

        // Get the role of the target player
        String targetRole = rollCommand.getRole(targetPlayer);

        // Inform the Seer of the target player's role
        player.sendMessage(ChatColor.GOLD + "Player " + targetName + " is a " + targetRole + ".");

        // Mark the Seer as having used their ability
        seerUsed.add(player.getName());

        return true;
    }

    public Set<String> getSeerUsed() {
        return seerUsed;
    }
}
