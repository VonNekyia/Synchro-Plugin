package org.bungee.sync.synchro.player;

import org.bungee.sync.synchro.Synchro;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {


    private final Synchro main;
    //private final List<PlayerData> playerDataList = new ArrayList<>();
    HashMap<UUID, PlayerData> playerDataList = new HashMap<UUID, PlayerData>();

    public PlayerDataManager(Synchro main) {
        this.main = main;
    }



    @Deprecated
    public HashMap<UUID, PlayerData> getPlayerDataList() {
        return playerDataList;
    }

    //zum Boolean machen
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataList.get(uuid);
    }

    public void removePlayerData(UUID uuid) {
        playerDataList.remove(uuid);
    }

    public void addPlayerData(PlayerData playerData) {
        playerDataList.put(playerData.getUuid(), playerData);
    }

    public void updatePlayerData(double health, int hunger, int experience, UUID uuid, boolean sync) {

        PlayerData newData = new PlayerData();
        newData.setExperience(experience);
        newData.setHealth(health);
        newData.setHunger(hunger);
        newData.setUuid(uuid);
        newData.setSync(sync);

        playerDataList.computeIfPresent(uuid, (k, v) -> newData);
    }

    public void addPlayerDataRaw(double health, int hunger, int experience, UUID uuid, boolean sync) {
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





}
