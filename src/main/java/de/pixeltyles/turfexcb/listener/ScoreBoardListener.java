package de.pixeltyles.turfexcb.listener;

import de.pixeltyles.turfexcb.Database.DataBaseManager;
import de.pixeltyles.turfexcb.TurfexCB;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class ScoreBoardListener implements Listener {

    private final TurfexCB plugin;
    private final DataBaseManager db;
    private final HashMap<UUID, Integer> playtimeMap = new HashMap<>();
    private final DecimalFormat formatter = new DecimalFormat("#,###");

    public ScoreBoardListener(TurfexCB plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabaseManager();
        startScoreboardUpdater();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playtimeMap.put(event.getPlayer().getUniqueId(), 0);
    }

    private void startScoreboardUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                    incrementPlaytime(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20 * 8L);
    }

    private void incrementPlaytime(Player player) {
        UUID uuid = player.getUniqueId();
        playtimeMap.put(uuid, playtimeMap.getOrDefault(uuid, 0) + 1);
    }

    private void updateScoreboard(Player player) {
        ScoreboardManager smanager = Bukkit.getScoreboardManager();
        if (smanager == null) return;
        Scoreboard board = smanager.getNewScoreboard();

        Objective obj = board.registerNewObjective("sidebar", "dummy", "§aTurfex.net");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        String uuid = player.getUniqueId().toString();
        int coins = db.getCoins(uuid);
        int superCoins = db.getSuperCoins(uuid);
        int online = Bukkit.getOnlinePlayers().size();
        int hours = playtimeMap.getOrDefault(player.getUniqueId(), 0) / 3600;

        String formattedCoins = formatCoins(coins);
        String formattedSuperCoins = formatCoins(superCoins);

        obj.getScore("§7").setScore(12);
        obj.getScore("§7» §c§lServer").setScore(11);
        obj.getScore("CB").setScore(10);
        obj.getScore("").setScore(9);
        obj.getScore("§7» §c§lSpieler").setScore(8);
        obj.getScore("§f" + online + "/125").setScore(7);
        obj.getScore("§4     §a").setScore(6);
        obj.getScore("§7» §c§lSupercoins").setScore(5);
        obj.getScore("§f" + formattedSuperCoins + "✪").setScore(4);
        obj.getScore("§f    §a").setScore(3);
        obj.getScore("§7» §c§lKonto").setScore(2);
        obj.getScore("§f" + formattedCoins + "$").setScore(1);
        obj.getScore("§f").setScore(0);

        player.setScoreboard(board);
    }

    private String formatCoins(int coins) {
        return formatter.format(coins).replace(",", ".");
    }
}
