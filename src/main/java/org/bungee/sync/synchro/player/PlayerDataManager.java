package org.bungee.sync.synchro.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.database.Database;
import org.bungee.sync.synchro.util.InventorySerilization;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataManager {


    private static final Plugin main = Bukkit.getPluginManager().getPlugin("Synchro");
    //private final List<PlayerData> playerDataList = new ArrayList<>();
    static HashMap<UUID, PlayerData> playerDataList = new HashMap<UUID, PlayerData>();

    /*
    public PlayerDataManager(Synchro main) {
        this.main = main;
        Bukkit.getPluginManager().getPlugin().;
    }
    */

    public static void addPlayerData(PlayerData playerData) {
        playerDataList.put(playerData.getUuid(), playerData);
    }

    public static void savePlayerData(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            Database db = Synchro.getDatabase();
            PlayerDataManager pdm = Synchro.getPlayerDataManager();
            PlayerData pd = pdm.getPlayerData(player.getUniqueId());
            //Spielers Playerdata wird gespeichert
            if (pd != null)
                db.updatePlayerData(player.getHealth(), player.getFoodLevel(), player.getExp() + player.getLevel(), player.getUniqueId(), true, InventorySerilization.saveModdedStacksData(player.getInventory().getContents()), player.getInventory().getHeldItemSlot(), InventorySerilization.saveModdedStacksData(player.getEnderChest().getContents()));
            //Spieler wird aus der Liste der Online-Spieler entfernt
            pdm.removePlayerData(player.getUniqueId());
        });
    }

    public static void applyPlayerData(Player player, PlayerData pd) {
        player.setHealth(pd.getHealth());
        player.setFoodLevel(pd.getHunger());
        float explevel = pd.getExperience();
        int level = (int) Math.floor(explevel);
        float exp = explevel - level;
        player.setLevel(level);
        player.setExp(exp);
        player.getInventory().setHeldItemSlot(pd.getHeltItemSlot());
        if (pd.getInventory() != null) {
            player.getInventory().setContents(InventorySerilization.restoreModdedStacks(pd.getInventory()));
        }
        if (pd.getEnderchest() != null) {
            player.getEnderChest().setContents(InventorySerilization.restoreModdedStacks(pd.getEnderchest()));
        }
        Synchro.getDatabase().setData("SYNC", false, player.getUniqueId());
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
