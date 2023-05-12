package org.bungee.sync.synchro.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.database.Database;
import org.bungee.sync.synchro.player.PlayerData;
import org.bungee.sync.synchro.util.InventorySerilization;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorldUpdateListener implements Listener {

    private static boolean backupInProgress;
    private static Database db;
    private final Synchro main;

    public WorldUpdateListener(Synchro main) {
        this.main = main;
        db = main.getDatabase();
    }

    @EventHandler
    public void onWorldUpdate(WorldSaveEvent event) {
        db = main.getDatabase();
        if (event.getWorld().getName().equals(main.getConfig().getString("Backup.World.name"))) {
            backupPlayerInv(main.getPlayerDataManager().getPlayerDataList());
        }
    }

    public void backupPlayerInv(HashMap<UUID, PlayerData> playerDataList) {
        db = main.getDatabase();
        if(main.getConfigHandler().isInDebugmode())
            main.getLogger().severe("[DEBUG][WORLDUPDATELISTENER] -> Backup beginnt ...");
        for (Map.Entry<UUID, PlayerData> playerDataEntry : playerDataList.entrySet()) {
            PlayerData pd = playerDataEntry.getValue();
            Player player = Bukkit.getPlayer(pd.getUuid());
            if (player != null)
                db.backupPlayerData(player.getHealth(), player.getFoodLevel(), player.getExp() + player.getLevel(), player.getUniqueId(), InventorySerilization.saveModdedStacksData(player.getInventory().getContents()),InventorySerilization.saveModdedStacksData(player.getEnderChest().getContents()));

        }
        if(main.getConfigHandler().isInDebugmode())
            main.getLogger().severe("[DEBUG][BACKUPEXECUTOR] -> Backup done.");
    }

}
