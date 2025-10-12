package net.pumpkin.werewolfevent.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StartVoteCommand implements CommandExecutor {

    private final VoteCommand voteCommand;

    public StartVoteCommand(VoteCommand voteCommand) {
        this.voteCommand = voteCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Check if the sender has permission to start the vote
        if (!sender.hasPermission("vote.start")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to start a voting session.");
            return true;
        }

        // Start the voting session
        voteCommand.startVotingSession();
        sender.sendMessage(ChatColor.GREEN + "You have started a new voting session!");
        return true;
    }
}
