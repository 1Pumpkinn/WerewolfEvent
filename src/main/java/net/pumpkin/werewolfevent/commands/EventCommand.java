package net.pumpkin.werewolfevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EventCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Display role information
        sender.sendMessage(ChatColor.GOLD + "=== Role Information ===");

        sender.sendMessage(ChatColor.RED + "Wolf: " + ChatColor.WHITE +
                "\n  - A dangerous role that attacks others during the night." +
                "\n  - Gains invisibility and special items at night.");

        sender.sendMessage(ChatColor.BLUE + "Seer: " + ChatColor.WHITE +
                "\n  - The Seer can use their powers to reveal the role of a player." +
                "\n  - This ability can be used once per round.");

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Cupid: " + ChatColor.WHITE +
                "\n  - Cupid can pair two players together as lovers." +
                "\n  - If one dies, the other dies as well.");

        sender.sendMessage(ChatColor.DARK_GREEN + "Hunter: " + ChatColor.WHITE +
                "\n  - The Hunter can choose to take one player down with them when they die.");

        sender.sendMessage(ChatColor.YELLOW + "Villager: " + ChatColor.WHITE +
                "\n  - The most basic role." +
                "\n  - Their goal is to survive and vote out the wolf.");

        sender.sendMessage(ChatColor.DARK_RED + "Sacrifice: " + ChatColor.WHITE +
                "\n  - A unique role that wins only if they are voted out.");

        return true;
    }
}
