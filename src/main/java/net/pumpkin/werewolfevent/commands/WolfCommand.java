package net.pumpkin.werewolfevent.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WolfCommand implements CommandExecutor, Listener {
    private final RoleCommand rollCommand;

    public WolfCommand(RoleCommand rollCommand) {
        this.rollCommand = rollCommand;
    }

    private final ItemStack wolfSword = new ItemStack(Material.IRON_SWORD);
    private final ItemStack wolfHelmet = new ItemStack(Material.IRON_HELMET);
    private final ItemStack wolfChestplate = new ItemStack(Material.IRON_CHESTPLATE);
    private final ItemStack wolfLeggings = new ItemStack(Material.IRON_LEGGINGS);
    private final ItemStack wolfBoots = new ItemStack(Material.IRON_BOOTS);

    private void applyWolfItemsAndStatus(Player player) {
        PlayerInventory inventory = player.getInventory();

        // Store existing armor or drop if inventory is full
        handleArmorReplacement(player, inventory, inventory.getHelmet(), wolfHelmet, true);
        handleArmorReplacement(player, inventory, inventory.getChestplate(), wolfChestplate, true);
        handleArmorReplacement(player, inventory, inventory.getLeggings(), wolfLeggings, true);
        handleArmorReplacement(player, inventory, inventory.getBoots(), wolfBoots, true);

        // Drop item in the main hand and replace with the wolf sword
        ItemStack currentItem = inventory.getItemInMainHand();
        if (currentItem != null && currentItem.getType() != Material.AIR) {
            player.getWorld().dropItemNaturally(player.getLocation(), currentItem);
        }
        inventory.setItemInMainHand(wolfSword);

        inventory.setHelmet(wolfHelmet);
        inventory.setChestplate(wolfChestplate);
        inventory.setLeggings(wolfLeggings);
        inventory.setBoots(wolfBoots);

        // Apply invisibility effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
    }

    private void handleArmorReplacement(Player player, PlayerInventory inventory, ItemStack currentArmor, ItemStack newArmor, boolean replace) {
        if (currentArmor != null) {
            if (inventory.firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), currentArmor);
            } else {
                inventory.addItem(currentArmor);
            }
        }
        if (replace) {
            if (newArmor.getType() != Material.AIR) {
                inventory.addItem(newArmor);
            }
        }
    }

    private void removeWolfItemsAndStatus(Player player) {
        PlayerInventory inventory = player.getInventory();

        // Clear the wolf items
        if (wolfSword.equals(inventory.getItemInMainHand())) {
            inventory.setItemInMainHand(null);
        }
        if (wolfHelmet.equals(inventory.getHelmet())) {
            inventory.setHelmet(null);
        }
        if (wolfChestplate.equals(inventory.getChestplate())) {
            inventory.setChestplate(null);
        }
        if (wolfLeggings.equals(inventory.getLeggings())) {
            inventory.setLeggings(null);
        }
        if (wolfBoots.equals(inventory.getBoots())) {
            inventory.setBoots(null);
        }

        // Remove invisibility effect
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
            return false;
        }

        if (command.getName().equalsIgnoreCase("night")) {
            Bukkit.getWorlds().get(0).setTime(13000);

            List<Player> onlinePlayers = List.copyOf(sender.getServer().getOnlinePlayers());
            for (Player onlinePlayer : onlinePlayers) {
                String role = rollCommand.getRole(onlinePlayer);

                if ("Wolf".equals(role)) {
                    applyWolfItemsAndStatus(onlinePlayer);
                } else {
                    onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false, false));
                }
            }

            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "The night has fallen. Werewolves grow stronger!");
            return true;

        } else if (command.getName().equalsIgnoreCase("day")) {
            Bukkit.getWorlds().get(0).setTime(1000);

            List<Player> onlinePlayers = List.copyOf(sender.getServer().getOnlinePlayers());
            for (Player onlinePlayer : onlinePlayers) {
                String role = rollCommand.getRole(onlinePlayer);

                if ("Wolf".equals(role)) {
                    removeWolfItemsAndStatus(onlinePlayer);
                }
                // Remove blindness effect from all players
                onlinePlayer.removePotionEffect(PotionEffectType.BLINDNESS);
            }

            Bukkit.broadcastMessage(ChatColor.YELLOW + "The sun has risen. Werewolves grow weaker!");
            return true;
        }

        return false;
    }
}