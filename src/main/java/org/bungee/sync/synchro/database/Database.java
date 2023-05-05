package org.bungee.sync.synchro.database;

import it.unimi.dsi.fastutil.Pair;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.player.PlayerData;

import java.lang.reflect.GenericDeclaration;
import java.sql.*;
import java.util.UUID;

public class Database {

    private final Synchro main;

    private Connection connection;

    public Database(Synchro main) {
        this.main = main;
    }

    public void connect() throws SQLException {
        String USERNAME = main.getConfig().getString("MySQL.login.username");
        String PASSWORD = main.getConfig().getString("MySQL.login.password");
        String DATABASE = main.getConfig().getString("MySQL.database");
        int PORT = main.getConfig().getInt("MySQL.host.port");
        String IP = main.getConfig().getString("MySQL.host.ip");
        if(main.getConfigHandler().isInDebugmode()) {main.getLogger().severe(String.format("[DEBUG][DATABASE] -> jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?useSSL=false"));}
        if(main.getConfigHandler().isInDebugmode()) {main.getLogger().severe(String.format("[DEBUG][DATABASE] -> Username: %s Password: %s", USERNAME, PASSWORD));}
        connection = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + PORT + "/" + DATABASE + "?useSSL=false", USERNAME, PASSWORD);
    }

    public boolean isConnected() {
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
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(String.format("UPDATE playerdata_sync SET %s = %s WHERE UUID = '%s';",column ,wert ,uuid));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updatePlayerData(double health,int hunger,float experience, UUID uuid, boolean sync, String inv, String armor) {
        try {
            String ps = String.format("UPDATE playerdata_sync SET HEALTH=%s, HUNGER=%s, EXPERIENCE=%s, SYNC=%s, INVENTORY='%s', ARMOR='%s' WHERE UUID = '%s';",health ,hunger,experience ,sync , inv, armor,uuid);
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(ps);
            statement.executeUpdate();
            main.getLogger().severe(String.format("[DEBUG][DATABASE] -> Playerdata von %s wurde geupdated.", uuid));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void backupPlayerData(double health,int hunger,float experience, UUID uuid, String inv, String armor) {
        try {
            String ps = String.format("UPDATE playerdata_sync SET HEALTH=%s, HUNGER=%s, EXPERIENCE=%s, INVENTORY='%s', ARMOR='%s' WHERE UUID = '%s';",health ,hunger,experience, inv, armor,uuid);
            PreparedStatement statement = main.getDatabase().getConnection().prepareStatement(ps);
            statement.executeUpdate();
            main.getLogger().severe(String.format("[DEBUG][DATABASE] -> Playerdata von %s wurde gebackuped.", uuid));
        } catch (SQLException e) { e.printStackTrace(); }
    }


    public PlayerData getPlayerData(UUID uuid) {



        try {
            String ps = "SELECT HEALTH, HUNGER, EXPERIENCE, SYNC, INVENTORY, ARMOR FROM playerdata_sync WHERE UUID = ?;";
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
                return pd;
            } else {
                int hunger = 20;
                double health = 20;
                float experience = 0;
                boolean sync = false;
                String inventory = "";
                String armor = "";
                createPlayerDataEntry(health, hunger, experience, uuid, sync, inventory, armor);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void createPlayerDataEntry(double health,int hunger,float experience, UUID uuid, boolean sync, String armor, String inventory) {
        try {
            PreparedStatement statement1 = main.getDatabase().getConnection().prepareStatement(
                    "INSERT INTO playerdata_sync (ID, HEALTH, HUNGER, EXPERIENCE, UUID, SYNC, ARMOR, INVENTORY) VALUES (" +
                            "default," +
                            health + "," +
                            hunger + "," +
                            experience + "," +
                            "'" + uuid + "'" + "," +
                            sync + "," +
                            "'" + armor + "'" + "," +
                            "'" + inventory + "'" + ");");
            statement1.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }


}