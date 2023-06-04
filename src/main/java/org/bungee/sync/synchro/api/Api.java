package org.bungee.sync.synchro.api;

import org.bukkit.entity.Player;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.player.PlayerData;
import org.bungee.sync.synchro.player.PlayerDataManager;

public class Api {
    //This Method prevents Backups and Save upon leaving
    public static void disablePlayer(Player player){
        PlayerDataManager.savePlayerData(player);
    }
    //This Method loads the Inventory
    public static void enablePlayer(Player player,PlayerData pd){
        PlayerDataManager.addPlayerData(pd);
        PlayerDataManager.applyPlayerData(player, pd);
    }
}
