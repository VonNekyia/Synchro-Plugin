package org.bungee.sync.synchro;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bungee.sync.synchro.commands.syncCommand;
import org.bungee.sync.synchro.commands.syncTabCompleter;
import org.bungee.sync.synchro.database.Database;
import org.bungee.sync.synchro.events.ConnectionListener;
import org.bungee.sync.synchro.events.InventoryInteractionListener;
import org.bungee.sync.synchro.events.WorldUpdateListener;
import org.bungee.sync.synchro.player.PlayerDataManager;
import org.bungee.sync.synchro.util.ConfigHandler;
import org.bungee.sync.synchro.util.InventorySerilization;

import java.util.Objects;
import java.util.logging.Logger;

public final class Synchro extends JavaPlugin implements Listener {

    private static Database database;
    private static PlayerDataManager playerDataManager;
    private static Logger log;
    private static ConfigHandler configHandler;
    //private static BackupExecutor backupExecutor;
    private ProtocolManager protocolManager;
    private InventorySerilization invSer;

    //TODO Enderchest remove hp
    //TODO KINDA SEVERE Async Backup
    //TODO NOT SEVERE TODO options to toggle sync factors in config (health hunger exp) Zeit in der Config die der Server versucht das Inventar herzustellen bevor er das Backup lädt
    //TODO tab complete

    public static Database getDatabase() {
        return database;
    }

    public static PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public static ConfigHandler getConfigHandler() {
        return configHandler;
    }

    @Override
    public void onEnable() {

        invSer = new InventorySerilization(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        configHandler = new ConfigHandler(this);
        log = getLogger();
        playerDataManager = new PlayerDataManager();

        commandRegistry();
        eventRegistry();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        database = new Database(this);
        database.connect();
        database.setupDatabase();
        if (getConfigHandler().isInDebugmode()) {
            getLogger().severe(String.format("[DEBUG][SYNC] -> Datenbankconnection: %s ", database.isConnected()));
        }

    }

    @Override
    public void onDisable() {
        database.disconnect();
    }

    public void commandRegistry() {
        Objects.requireNonNull(getCommand("sync")).setExecutor(new syncCommand(this));
        Objects.requireNonNull(getCommand("sync")).setTabCompleter(new syncTabCompleter());
    }

    public void eventRegistry() {
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryInteractionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldUpdateListener(this), this);
    }
}
