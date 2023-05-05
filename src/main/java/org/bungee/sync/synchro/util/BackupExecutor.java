package org.bungee.sync.synchro.util;

import com.comphenix.protocol.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.database.Database;
import org.bungee.sync.synchro.player.PlayerData;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class BackupExecutor {

    private static boolean backupInProgress;
    private static Database db;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Synchro main;


    public BackupExecutor(Synchro main) {
        this.main = main;
        this.db = main.getDatabase();
        if(!backupInProgress){
            backupInProgress = true;
            Runnable backup = () -> backupPlayerInv(main.getPlayerDataManager().getPlayerDataList());
                scheduler.scheduleAtFixedRate(backup, 10, 90, SECONDS);
        }
    }

    public void backupPlayerInv(HashMap<UUID, PlayerData> playerDataList) {
        main.getLogger().severe(String.format("[DEBUG][BACKUPEXECUTOR] -> Backup beginnt ..."));
        for (Map.Entry<UUID, PlayerData> playerDataEntry : playerDataList.entrySet()) {
            PlayerData pd = playerDataEntry.getValue();
            Player player = Bukkit.getPlayer(pd.getUuid());
            if(player != null) {
                db.backupPlayerData(player.getHealth(),player.getFoodLevel(), player.getExp() + player.getLevel(), player.getUniqueId(), InventorySerilization.saveModdedStacksData(player.getInventory().getContents()), InventorySerilization.saveModdedStacksData(player.getInventory().getArmorContents()));
            }
        }
        main.getLogger().severe(String.format("[DEBUG][BACKUPEXECUTOR] -> Backup done."));
    }
}
