package de.pixeltyles.turfexcb.commands;

import de.pixeltyles.turfexcb.JobSystem.JobManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class JobCommand implements CommandExecutor {
    private final JobManager jobManager;

    public JobCommand(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        Inventory inv = Bukkit.createInventory(null, 3 * 9, "§8Wähle deinen Job");

        inv.setItem(13, createJobItem(Material.GRASS_BLOCK, "§aPlatzieren", player, "platzieren"));

        player.openInventory(inv);
        return true;
    }

    private ItemStack createJobItem(Material mat, String name, Player p, String jobKey) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        int level = jobManager.getLevel(p, jobKey);
        double bonus = jobManager.getMultiplier(p, jobKey) - 1;

        meta.setDisplayName(name);
        meta.setLore(List.of(
                "§7Job: " + jobKey,
                "§7Level: §e" + level,
                "§7Bonus: §a+" + bonus + " Coins"
        ));
        item.setItemMeta(meta);
        return item;
    }
}
