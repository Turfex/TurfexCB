package de.pixeltyles.turfexcb.Database;

import java.sql.*;

public class DataBaseManager {

    private Connection connection;

    public DataBaseManager(String host, String database, String user, String password, int port) {
        connect(host, database, user, password, port);
    }

    public void connect(String host, String database, String user, String password, int port) {
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("[MySQL] Verbindung erfolgreich!");

            // Tabelle erstellen, falls sie nicht existiert
            connection.prepareStatement("""
                CREATE TABLE IF NOT EXISTS player_data (
                    uuid VARCHAR(36) PRIMARY KEY,
                    coins INT DEFAULT 0,
                    supercoins INT DEFAULT 0,
                    job VARCHAR(32) DEFAULT '',
                    level INT DEFAULT 0,
                    xp INT DEFAULT 0,
                    crate_type VARCHAR(32) DEFAULT '',
                    crate_amount INT DEFAULT 0
                );
            """).executeUpdate();

        } catch (SQLException e) {
            System.out.println("[MySQL] Fehler bei Verbindung:");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[MySQL] Verbindung geschlossen.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Coins -----------------

    public void addCoins(String uuid, int amountToAdd) {
        int currentCoins = getCoins(uuid);
        setCoins(uuid, currentCoins + amountToAdd);
    }

    public void removeCoins(String uuid, int amountToRemove) {
        int currentCoins = getCoins(uuid);
        int updatedCoins = Math.max(0, currentCoins - amountToRemove); // Sicherstellen, dass Coins nicht negativ werden
        setCoins(uuid, updatedCoins);
    }

// ----------------- Supercoins -----------------

    public void addSuperCoins(String uuid, int amountToAdd) {
        int currentSuperCoins = getSuperCoins(uuid);
        setSuperCoins(uuid, currentSuperCoins + amountToAdd);
    }

    public void removeSuperCoins(String uuid, int amountToRemove) {
        int currentSuperCoins = getSuperCoins(uuid);
        int updatedSuperCoins = Math.max(0, currentSuperCoins - amountToRemove); // Sicherstellen, dass Supercoins nicht negativ werden
        setSuperCoins(uuid, updatedSuperCoins);
    }

    public void setCoins(String uuid, int coins) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "REPLACE INTO player_data (uuid, coins) VALUES (?, ?);"
            );
            st.setString(1, uuid);
            st.setInt(2, coins);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCoins(String uuid) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT coins FROM player_data WHERE uuid = ?;"
            );
            st.setString(1, uuid);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt("coins");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ----------------- Supercoins -----------------

    public void setSuperCoins(String uuid, int supercoins) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "REPLACE INTO player_data (uuid, supercoins) VALUES (?, ?);"
            );
            st.setString(1, uuid);
            st.setInt(2, supercoins);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSuperCoins(String uuid) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT supercoins FROM player_data WHERE uuid = ?;"
            );
            st.setString(1, uuid);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt("supercoins");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ----------------- Jobs -----------------

    public void setXP(String uuid, String job, int xp) {
        try {
            int level = getLevel(uuid, job);  // Level bleibt unverändert
            PreparedStatement st = connection.prepareStatement(
                    "REPLACE INTO player_data (uuid, job, level, xp) VALUES (?, ?, ?, ?);"
            );
            st.setString(1, uuid);
            st.setString(2, job);
            st.setInt(3, level);
            st.setInt(4, xp);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getXP(String uuid) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT xp FROM player_data WHERE uuid = ?;"
            );
            st.setString(1, uuid);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt("xp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setLevel(String uuid, String job, int level) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "REPLACE INTO player_data (uuid, job, level) VALUES (?, ?, ?);"
            );
            st.setString(1, uuid);
            st.setString(2, job);
            st.setInt(3, level);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLevel(String uuid, String job) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT level FROM player_data WHERE uuid = ? AND job = ?;"
            );
            st.setString(1, uuid);
            st.setString(2, job);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt("level");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setJob(String uuid, String job) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "REPLACE INTO player_data (uuid, job) VALUES (?, ?);"
            );
            st.setString(1, uuid);
            st.setString(2, job);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasJob(String uuid, String job) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT job FROM player_data WHERE uuid = ? AND job = ?;"
            );
            st.setString(1, uuid);
            st.setString(2, job);
            ResultSet rs = st.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ----------------- Crates -----------------

    public int getCrateCount(String uuid, String crateType) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "SELECT crate_amount FROM player_data WHERE uuid = ? AND crate_type = ?;"
            );
            st.setString(1, uuid);
            st.setString(2, crateType);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return rs.getInt("crate_amount");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setCrateCount(String uuid, String crateType, int amount) {
        try {
            PreparedStatement st = connection.prepareStatement(
                    "REPLACE INTO player_data (uuid, crate_type, crate_amount) VALUES (?, ?, ?);"
            );
            st.setString(1, uuid);
            st.setString(2, crateType);
            st.setInt(3, amount);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCrates(String uuid, String crateType, int amountToAdd) {
        int current = getCrateCount(uuid, crateType);
        setCrateCount(uuid, crateType, current + amountToAdd);
    }

    public void removeCrates(String uuid, String crateType, int amountToRemove) {
        int current = getCrateCount(uuid, crateType);
        int updated = Math.max(0, current - amountToRemove);
        setCrateCount(uuid, crateType, updated);
    }

    public Connection getConnection() {
        return connection;
    }
}
