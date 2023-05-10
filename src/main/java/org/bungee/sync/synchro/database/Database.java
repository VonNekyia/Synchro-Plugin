package org.bungee.sync.synchro.database;

import com.comphenix.protocol.PacketType;
import it.unimi.dsi.fastutil.Pair;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.player.PlayerData;

import java.lang.reflect.GenericDeclaration;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class Database {

    private final Synchro main;

    private Connection connection;
    String TABLE;
    String USERNAME;
    String PASSWORD;
    String DATABASE;
    int PORT;
    String IP;

    public Database(Synchro main) {
        this.main = main;
    }

    public void connect() {
        try{
            USERNAME = main.getConfig().getString("MySQL.login.username");
            PASSWORD = main.getConfig().getString("MySQL.login.password");
            DATABASE = main.getConfig().getString("MySQL.database");
            TABLE = main.getConfig().getString("MySQL.table");
            PORT = main.getConfig().getInt("MySQL.host.port");
            IP = main.getConfig().getString("MySQL.host.ip");
            if(main.getConfigHandler().isInDebugmode()) {main.getLogger().severe(String.format("[DEBUG][DATABASE] -> jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?useSSL=false"));}
            if(main.getConfigHandler().isInDebugmode()) {main.getLogger().severe(String.format("[DEBUG][DATABASE] -> Username: %s Password: %s", USERNAME, PASSWORD));}
            connection = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?useSSL=false", USERNAME, PASSWORD);
        } catch (SQLException e) {
            main.getLogger().severe(String.format("[DATABASE] Could not connect to mysql database! Error: " + e.getMessage()));
        }

    }

    public void setupDatabase() {
        if (this.connection != null) {
            PreparedStatement query = null;
            try {
                String data = "CREATE TABLE IF NOT EXISTS `" + main.getConfig().getString("MySQL.table") + "` (ID int(10) AUTO_INCREMENT, UUID char(36) NOT NULL UNIQUE, INVENTORY LONGTEXT NOT NULL, ARMOR LONGTEXT NOT NULL, HEALTH DOUBLE NOT NULL, HUNGER int(11) NOT NULL, EXPERIENCE float NOT NULL,  HEADPOINTS int(11) NOT NULL, SYNC tinyint(1) NOT NULL, PRIMARY KEY(id));";
                query = this.connection.prepareStatement(data);
                query.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                main.getLogger().severe(String.format("[DATABASE] Error creating tables! Error: " + e.getMessage()));
            } finally {
                try {
                    if (query != null)
                        query.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void checkConnection() {
        try {
            if (this.connection == null) {
                main.getLogger().severe("[DATABASE] Connection failed. Reconnecting...");
                reConnect();
            }
            if (!this.connection.isValid(3)) {
                main.getLogger().severe("[DATABASE] Connection is idle or terminated. Reconnecting...");
                reConnect();
            }
            if (this.connection.isClosed()) {
                main.getLogger().severe("[DATABASE] Connection is idle or terminated. Reconnecting...");
                reConnect();
            }
        } catch (Exception e) {
            main.getLogger().severe("[DATABASE] Could not reconnect to Database! Error: " + e.getMessage());
        }
    }

    public boolean reConnect() {
        try {
            long start = 0L;
            long end = 0L;
            start = System.currentTimeMillis();
            main.getLogger().severe("[DATABASE] Attempting to establish a connection to the MySQL server!");

            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE, properties);
            end = System.currentTimeMillis();
            main.getLogger().severe("Connection to MySQL server established!");
            main.getLogger().severe("Connection took " + (end - start) + "ms!");
            return true;
        } catch (Exception e) {
            main.getLogger().severe("Error re-connecting to the database! Error: " + e.getMessage());
            return false;
        }
    }


    public boolean isConnected() {
        checkConnection();
        return connection != null;
    }

    public Connection getConnection() { return connection; }


    public void disconnect() {
        if(isConnected()) {
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public <T> void setData(String column,T wert ,UUID uuid) {
        try {
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(String.format("UPDATE %s SET %s = %s WHERE UUID = '%s';",TABLE , column ,wert ,uuid));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData getInvseeData(UUID uuid) {

        PlayerData pd = new PlayerData();

        try {
            String ps = "SELECT INVENTORY, ARMOR FROM " + TABLE + " WHERE UUID = ?;";
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(ps);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();

            //wenn rs.next false ist gibt es den Spieler nicht
            if (rs.next()) {
                pd.setUuid(uuid);
                pd.setInventory(rs.getString("INVENTORY"));
                pd.setArmor(rs.getString("ARMOR"));
                return pd;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void updateHeadPoints(UUID uuid, int headpoints) {
        try {
            String ps = String.format("UPDATE %s SET HEADPOINTS=%s WHERE UUID = '%s';",TABLE , headpoints,uuid);
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(ps);
            statement.executeUpdate();
            main.getLogger().severe(String.format("[DEBUG][DATABASE] -> Headpoints von %s wurden geupdated.", uuid));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updatePlayerData(double health,int hunger,float experience, UUID uuid, boolean sync, String inv, String armor, int heltItemSlot) {
        try {
            String ps = String.format("UPDATE %s SET HEALTH=%s, HUNGER=%s, EXPERIENCE=%s, SYNC=%s, INVENTORY='%s', ARMOR='%s', HELTITEMSLOT=%s WHERE UUID = '%s';",TABLE ,health ,hunger,experience ,sync , inv, armor, heltItemSlot ,uuid);
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(ps);
            statement.executeUpdate();
            main.getLogger().severe(String.format("[DEBUG][DATABASE] -> Playerdata von %s wurde geupdated.", uuid));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void backupPlayerData(double health,int hunger,float experience, UUID uuid, String inv, String armor) {
        try {
            checkConnection();
            String ps = String.format("UPDATE %s SET HEALTH=%s, HUNGER=%s, EXPERIENCE=%s, INVENTORY='%s', ARMOR='%s' WHERE UUID = '%s';",TABLE ,health ,hunger,experience, inv, armor,uuid);
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(ps);
            statement.executeUpdate();
            main.getLogger().severe(String.format("[DEBUG][DATABASE] -> Playerdata von %s wurde gebackuped.", uuid));
        } catch (SQLException e) { e.printStackTrace(); }
    }


    public PlayerData getPlayerData(UUID uuid) {
        try {
            String ps = "SELECT HEALTH, HUNGER, EXPERIENCE, SYNC, INVENTORY, ARMOR, HEADPOINTS, HELTITEMSLOT FROM " + TABLE +  " WHERE UUID = ?;";
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(ps);
            statement.setString(1,uuid.toString());
            ResultSet rs = statement.executeQuery();

            //wenn rs.next false ist gibt es den Spieler nicht
            if(rs.next()) {
                PlayerData pd = new PlayerData();
                pd.setUuid(uuid);
                pd.setHealth(rs.getDouble("HEALTH"));
                pd.setHunger(rs.getInt("HUNGER"));
                pd.setExperience(rs.getFloat("EXPERIENCE"));
                pd.setSync(rs.getBoolean("SYNC"));
                pd.setInventory(rs.getString("INVENTORY"));
                pd.setArmor(rs.getString("ARMOR"));
                pd.setHeltItemSlot(rs.getInt("HELTITEMSLOT"));
                pd.setHeadpoints(rs.getInt("HEADPOINTS"));
                return pd;
            } else {
                int hunger = 20;
                double health = 20;
                float experience = 0;
                boolean sync = true;
                String inventory = "";
                String armor = "";
                int heltItemSlot = 1;
                int headpoints = 0;
                createPlayerDataEntry(health, hunger, experience, uuid, sync, inventory, armor, heltItemSlot, headpoints);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }



    public void createPlayerDataEntry(double health,int hunger,float experience, UUID uuid, boolean sync, String armor, String inventory, int heldItemSlot, int headpoints) {
        try {
            PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement(
                    "INSERT INTO " + TABLE + " (ID, HEALTH, HUNGER, HEADPOINTS, EXPERIENCE, UUID, SYNC, HELTITEMSLOT, ARMOR, INVENTORY) VALUES (" +
                            "default," +
                            health + "," +
                            hunger + "," +
                            headpoints + "," +
                            experience + "," +
                            "'" + uuid + "'" + "," +
                            sync + "," +
                            heldItemSlot + "," +
                            "'" + armor + "'" + "," +
                            "'" + inventory + "'" + ");");
            statement1.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }


}