package de.pixeltyles.turfexcb.listener;

import de.pixeltyles.turfexcb.Database.DataBaseManager;
import de.pixeltyles.turfexcb.JobSystem.JobManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobsListener implements Listener {

    private final JobManager jobManager;
    private final DataBaseManager db;
    private final Map<UUID, Integer> actionCounter = new HashMap<>();

    public JobsListener(JobManager jobManager, DataBaseManager db) {
        this.jobManager = jobManager;
        this.db = db;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (jobManager.getMainJob(p).equalsIgnoreCase("abbauen")) {
            handleAction(p);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (jobManager.getMainJob(p).equalsIgnoreCase("platzieren")) {
            handleAction(p);
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;

        Player p = e.getEntity().getKiller();
        if (jobManager.getMainJob(p).equalsIgnoreCase("jagen")) {
            if (e.getEntity() instanceof Monster || isAnimal(e.getEntityType())) {
                handleAction(p);
            }
        }
    }

    private boolean isAnimal(EntityType type) {
        return switch (type) {
            case COW, SHEEP, PIG, CHICKEN, RABBIT, HORSE -> true;
            default -> false;
        };
    }

    private void handleAction(Player player) {
        UUID uuid = player.getUniqueId();
        String job = jobManager.getMainJob(player);
        int current = actionCounter.getOrDefault(uuid, 0) + 1;
        actionCounter.put(uuid, current);

        double baseReward = 0.5;
        boolean rewardNow = false;

        switch (job) {
            case "jagen" -> {
                if (current % 3 == 0) rewardNow = true;
            }
            case "abbauen", "platzieren" -> {
                if (current % 10 == 0) rewardNow = true;
            }
        }

        int level = jobManager.getLevel(player, job);

        if (rewardNow) {
            giveCoins(player, level, baseReward);
            jobManager.addXP(player, job, 10);
        }

        if (current >= 50) {
            actionCounter.put(uuid, 0);
        }
    }


    private void giveCoins(Player player, int level, double baseCoins) {
        double multiplier = 0.5 + (level * 0.5);
        int total = (int) Math.round(baseCoins * multiplier);

        int currentCoins = db.getCoins(player.getUniqueId().toString());
        db.setCoins(player.getUniqueId().toString(), currentCoins + total);

        player.sendMessage("§a+" + total + " Coins für deine Aktion!");
    }
}
