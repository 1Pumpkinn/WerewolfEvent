package net.pumpkin.werewolfevent.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteCommand implements CommandExecutor, Listener {


    private final Map<UUID, String> votes = new HashMap<>();
    private boolean votingInProgress = false;
    private final RoleCommand rollCommand; // Reference to RollCommand to check roles

    // Constructor to get an instance of RollCommand
    public VoteCommand(RoleCommand rollCommand) {
        this.rollCommand = rollCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Start a new voting session if an OP runs '/vote start'
        if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
            if (!sender.hasPermission("vote.start")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to start a voting session.");
                return true;
            }

            // Start the voting session
            startVotingSession();
            sender.sendMessage(ChatColor.GREEN + "You have started a new voting session!");
            return true;
        }

        // Only players can vote
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can participate in voting.");
            return true;
        }

        // Check if a voting session is active
        if (!votingInProgress) {
            player.sendMessage(ChatColor.RED + "No voting session is currently active.");
            return true;
        }

        // Check if the player has already voted
        if (votes.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have already voted.");
            return true;
        }

        // Check if the player is in spectator mode (voted out)
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(ChatColor.RED + "You cannot vote because you are in spectator mode.");
            return true;
        }

        // Check if the player is an operator
        if (player.isOp()) {
            player.sendMessage(ChatColor.RED + "Operators cannot participate in voting.");
            return true;
        }

        // Validate the target player
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /vote <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "The specified player is not online.");
            return true;
        }

        // Check if the target player is in spectator mode (cannot vote for voted out players)
        if (target.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(ChatColor.RED + "You cannot vote for " + target.getName() + " because they are in spectator mode.");
            return true;
        }

        // Check if the target player is an operator
        if (target.isOp()) {
            player.sendMessage(ChatColor.RED + "You cannot vote for an operator.");
            return true;
        }

        // Register the vote
        votes.put(player.getUniqueId(), target.getName());
        player.sendMessage(ChatColor.GREEN + "You voted to eliminate " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + ".");

        // Calculate eligible players (non-operators and non-spectators)
        long eligiblePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.isOp() && p.getGameMode() != GameMode.SPECTATOR)
                .count();

        // Check if all eligible players have voted
        if (votes.size() >= eligiblePlayers) {
            endVotingSession();
        }
        return true;
    }

    /**
     * Starts a new voting session.
     */

    public void startVotingSession() {
        if (votingInProgress) {
            Bukkit.broadcastMessage(ChatColor.RED + "A voting session is already in progress.");
            return;
        }

        votingInProgress = true;
        votes.clear();

        // Broadcast the voting start message
        Bukkit.broadcastMessage(ChatColor.GOLD + "A new voting session has started! Use " + ChatColor.YELLOW + "/vote <player>" + ChatColor.GOLD + " to cast your vote.");
    }

    /**
     * Ends the current voting session and processes the results.
     */
    private void endVotingSession() {
        votingInProgress = false;

        // Tally votes
        Map<String, Integer> voteCount = new HashMap<>();
        for (String playerName : votes.values()) {
            voteCount.put(playerName, voteCount.getOrDefault(playerName, 0) + 1);
        }

        // Determine the player(s) with the most votes
        String eliminatedPlayer = null;
        int maxVotes = 0;
        boolean isTie = false;

        // Loop to find the player with the most votes
        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > maxVotes) {
                eliminatedPlayer = entry.getKey();
                maxVotes = entry.getValue();
                isTie = false;  // Reset isTie because a new highest vote is found
            } else if (entry.getValue() == maxVotes) {
                isTie = true;  // There's a tie
            }
        }

        // If there's a tie, broadcast a tie message
        if (isTie) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "It's a tie! No one will be eliminated.");
        } else if (eliminatedPlayer != null) {
            Player player = Bukkit.getPlayer(eliminatedPlayer);
            if (player != null) {
                Bukkit.broadcastMessage(ChatColor.RED + eliminatedPlayer + " has been eliminated with " + maxVotes + " votes!");
                player.setGameMode(GameMode.SPECTATOR);

                // Check if the eliminated player is one of the special roles
                String role = rollCommand.getRole(player); // Get the player's role
                if (role != null && isSpecialRole(role)) {
                    // Notify an operator if the eliminated player has a special role
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("vote.notify")) {
                            onlinePlayer.sendMessage(ChatColor.RED + "ALERT: " + eliminatedPlayer + " with the role of " + role + " has been eliminated!");
                        }
                    }
                }
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "Could not eliminate " + eliminatedPlayer + " because they are offline.");
            }
        }

        // Reset votes for the next round
        votes.clear();
    }

    /**
     * Check if the role is one of the special roles.
     */
    private boolean isSpecialRole(String role) {
        return role.equalsIgnoreCase("sacrifice") || role.equalsIgnoreCase("hunter") ||
                role.equalsIgnoreCase("wolf") || role.equalsIgnoreCase("cupid") || role.equalsIgnoreCase("seer");
    }
}
