package de.pixeltyles.turfexcb.listener;

import de.pixeltyles.turfexcb.Database.DataBaseManager;
import de.pixeltyles.turfexcb.JobSystem.JobManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobProgressManager {

    private final JobManager jobManager;
    private final DataBaseManager db;

    private final Map<UUID, Integer> actionCounter = new HashMap<>();

    public JobProgressManager(JobManager jobManager, DataBaseManager db) {
        this.jobManager = jobManager;
        this.db = db;
    }

    public void handleAction(Player player) {
        UUID uuid = player.getUniqueId();
        String job = jobManager.getMainJob(player);
        int current = actionCounter.getOrDefault(uuid, 0) + 1;
        actionCounter.put(uuid, current);

        int level = jobManager.getLevel(player, job);
        int baseReward = 10;


        if (job.equals("platzieren") && current % 10 == 0) {
            giveCoins(player, level, baseReward);
        }


        if (current >= 50) {
            if (level < 100) {
                jobManager.setLevel(player, job, level + 1);
                player.sendMessage("§6Level Up! Du bist jetzt Level " + (level + 1));
            }
            actionCounter.put(uuid, 0);
        }
    }

    private void giveCoins(Player player, int level, int baseCoins) {
        double multiplier = 1.0 + (level * 0.5);
        int total = (int) Math.round(baseCoins * multiplier);

        int current = db.getCoins(player.getUniqueId().toString());
        db.setCoins(player.getUniqueId().toString(), current + total);

        player.sendMessage("§a+" + total + " Coins für deine Aktion!");
    }
}
