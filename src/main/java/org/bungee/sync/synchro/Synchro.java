package org.bungee.sync.synchro;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bungee.sync.synchro.commands.syncCommand;
import org.bungee.sync.synchro.commands.syncTabCompleter;
import org.bungee.sync.synchro.database.Database;
import org.bungee.sync.synchro.events.ConnectionListener;
import org.bungee.sync.synchro.events.InventoryInteractionListener;
import org.bungee.sync.synchro.player.PlayerDataManager;
import org.bungee.sync.synchro.util.BackupExecutor;
import org.bungee.sync.synchro.util.ConfigHandler;
import org.bungee.sync.synchro.util.DelayedTask;
import org.bungee.sync.synchro.util.InventorySerilization;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public final class Synchro extends JavaPlugin implements Listener {

    private static Database database;
    private static PlayerDataManager playerDataManager;
    private static Logger log;
    private static ConfigHandler configHandler;
    private static BackupExecutor backupExecutor;
    private ProtocolManager protocolManager;
    private InventorySerilization invSer;


    //TODO KINDA SEVERE Async Backup
    //TODO KINDA SEVERE Convert old inventories

    //TODO NOT SEVERE Zeit in der Config die der Server versucht das Inventar herzustellen bevor er das Backup lädt
    //TODO NOT SEVERE command /sync (debug, invsee)
    //TODO NOT SEVERE warum Hashmappe ich pd und UUID wenn die pd die UUID enthält PLAYERDATAMANAGER
    //TODO NOT SEVERE Database auto reconnect
    //TODO NOT SEVERE TODO options to toggle sync factors in config (health hunger exp)

    @Override
    public void onEnable() {

        invSer = new InventorySerilization(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        configHandler = new ConfigHandler(this);
        //Command Registry
        Objects.requireNonNull(getCommand("sync")).setExecutor( new syncCommand(this));
        Objects.requireNonNull(getCommand("sync")).setTabCompleter( new syncTabCompleter());
        //Event Registry
        Bukkit.getPluginManager().registerEvents( new ConnectionListener(this),this);
        Bukkit.getPluginManager().registerEvents( new InventoryInteractionListener(this),this);

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        //Database connection
        database = new Database(this);
        try {
            database.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(getConfigHandler().isInDebugmode()) {
            getLogger().severe(String.format("[DEBUG][SYNC] -> Datenbankconnection: %s ", database.isConnected()));
        }

        //Initialisiert Speicher für Spielerdaten
        playerDataManager = new PlayerDataManager(this);
        backupExecutor = new BackupExecutor(this);
        log = getLogger();


    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

    //Getter
    public Database getDatabase() {return database; }
    public PlayerDataManager getPlayerDataManager() {return playerDataManager; }
    public ConfigHandler getConfigHandler() {return configHandler; }
}
