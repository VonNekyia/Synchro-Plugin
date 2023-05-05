package org.bungee.sync.synchro.player;

import org.bukkit.entity.Player;
import org.bungee.sync.synchro.Synchro;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerDataManager {


    private final Synchro main;



    public PlayerDataManager(Synchro main) {
        this.main = main;
    }

    //private final List<PlayerData> playerDataList = new ArrayList<>();
    HashMap<UUID, PlayerData> playerDataList = new HashMap<UUID, PlayerData>();

    public PlayerData getPlayerData(UUID uuid) { return playerDataList.get(uuid); }
    public HashMap<UUID, PlayerData> getPlayerDataList() { return playerDataList; }

    public void removePlayerData(UUID uuid) {
        playerDataList.remove(uuid);
    }

    public void updatePlayerData(double health,int hunger,int experience, UUID uuid, boolean sync) {

        PlayerData newData = new PlayerData();
        newData.setExperience(experience);
        newData.setHealth(health);
        newData.setHunger(hunger);
        newData.setUuid(uuid);
        newData.setSync(sync);

        playerDataList.computeIfPresent(uuid, (k, v) -> newData);
    }

    public void addPlayerDataRaw(double health,int hunger,int experience, UUID uuid, boolean sync) {
        {
            PlayerData data = new PlayerData();
            data.setExperience(experience);
            data.setHealth(health);
            data.setHunger(hunger);
            data.setUuid(uuid);
            data.setSync(sync);
            playerDataList.put(uuid, data);
        }
    }

    public void convertInv(){

    }

    public void addPlayerData(PlayerData playerData) { playerDataList.put(playerData.getUuid(),playerData); }

}
