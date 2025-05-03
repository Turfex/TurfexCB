package de.pixeltyles.turfexcb.JobSystem;

import de.pixeltyles.turfexcb.Database.DataBaseManager;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobManager {

    private final DataBaseManager db;


    private final Map<UUID, String> mainJobs = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> levels = new HashMap<>();

    public JobManager(DataBaseManager db) {
        this.db = db;
    }

    public int getLevel(Player player, String job) {
        UUID uuid = player.getUniqueId();
        levels.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> playerLevels = levels.get(uuid);

        if (!playerLevels.containsKey(job)) {
            loadJobData(player, job);
        }

        return playerLevels.getOrDefault(job, 0);
    }

    public void setJob(Player player, String job) {
        UUID uuid = player.getUniqueId();
        mainJobs.put(uuid, job);

        try {
            PreparedStatement st = db.getConnection().prepareStatement(
                    "UPDATE player_data SET main_job = ? WHERE uuid = ?"
            );
            st.setString(1, job);
            st.setString(2, uuid.toString());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLevel(Player player, String job, int level) {
        UUID uuid = player.getUniqueId();
        levels.computeIfAbsent(uuid, k -> new HashMap<>()).put(job, level);
        saveJobData(player, job);
    }

    public double getMultiplier(Player player, String job) {
        int level = getLevel(player, job);
        return 0.5 + (level * 0.5);
    }

    public void addXP(Player player, String job, int xpAmount) {
        UUID uuid = player.getUniqueId();

        int currentXP = getXP(player, job);
        int newXP = currentXP + xpAmount;

        int currentLevel = getLevel(player, job);
        int requiredXP = (currentLevel + 1) * 100;

        if (newXP >= requiredXP) {
            setLevel(player, job, currentLevel + 1);
            newXP -= requiredXP;
        }

        try {
            PreparedStatement st = db.getConnection().prepareStatement(
                    "REPLACE INTO player_jobs (uuid, job, level, xp) VALUES (?, ?, ?, ?)"
            );
            st.setString(1, uuid.toString());
            st.setString(2, job);
            st.setInt(3, getLevel(player, job));
            st.setInt(4, newXP);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getXP(Player player, String job) {
        UUID uuid = player.getUniqueId();

        try {
            PreparedStatement st = db.getConnection().prepareStatement(
                    "SELECT xp FROM player_jobs WHERE uuid = ? AND job = ?"
            );
            st.setString(1, uuid.toString());
            st.setString(2, job);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("xp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void loadJobData(Player player, String job) {
        UUID uuid = player.getUniqueId();

        try {
            PreparedStatement st = db.getConnection().prepareStatement(
                    "SELECT level FROM player_jobs WHERE uuid = ? AND job = ?"
            );
            st.setString(1, uuid.toString());
            st.setString(2, job);
            ResultSet rs = st.executeQuery();

            int level = 0;
            if (rs.next()) {
                level = rs.getInt("level");
            }

            levels.computeIfAbsent(uuid, k -> new HashMap<>()).put(job, level);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveJobData(Player player, String job) {
        UUID uuid = player.getUniqueId();
        int level = getLevel(player, job);
        int xp = getXP(player, job);

        try {
            PreparedStatement st = db.getConnection().prepareStatement(
                    "REPLACE INTO player_jobs (uuid, job, level, xp) VALUES (?, ?, ?, ?)"
            );
            st.setString(1, uuid.toString());
            st.setString(2, job);
            st.setInt(3, level);
            st.setInt(4, xp);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getAllLevels(Player player) {
        UUID uuid = player.getUniqueId();

        if (!levels.containsKey(uuid)) {
            loadAllJobs(player);
        }

        return levels.getOrDefault(uuid, new HashMap<>());
    }

    private void loadAllJobs(Player player) {
        UUID uuid = player.getUniqueId();

        try {
            PreparedStatement st = db.getConnection().prepareStatement(
                    "SELECT job, level FROM player_jobs WHERE uuid = ?"
            );
            st.setString(1, uuid.toString());
            ResultSet rs = st.executeQuery();

            Map<String, Integer> playerLevels = new HashMap<>();
            while (rs.next()) {
                String job = rs.getString("job");
                int level = rs.getInt("level");
                playerLevels.put(job, level);
            }

            levels.put(uuid, playerLevels);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getHighestLevel(Player player) {
        return getAllLevels(player).values().stream().max(Integer::compare).orElse(0);
    }

    public String getMainJob(Player player) {
        return getAllLevels(player)
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("none");
    }
}
