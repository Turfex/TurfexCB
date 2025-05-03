package de.pixeltyles.turfexcb.listener;

import de.pixeltyles.turfexcb.JobSystem.JobManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class JobInventoryListener implements Listener {

    private final JobManager jobManager;

    public JobInventoryListener(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (e.getView().getTitle().equals("§8Wähle deinen Job")) {
            e.setCancelled(true);

            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()).toLowerCase();

            switch (name) {
                case"platzieren" -> {
                    jobManager.setJob(p, name);
                    p.sendMessage("§aDu hast den Job §e" + name + " §agewählt!");
                    p.closeInventory();
                }
            }
        }
    }
}
