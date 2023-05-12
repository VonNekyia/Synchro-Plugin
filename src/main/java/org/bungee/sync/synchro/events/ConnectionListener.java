package org.bungee.sync.synchro.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.database.Database;
import org.bungee.sync.synchro.player.PlayerData;
import org.bungee.sync.synchro.player.PlayerDataManager;
import org.bungee.sync.synchro.util.InventorySerilization;

public class ConnectionListener implements Listener {

    private final Synchro main;

    public ConnectionListener(Synchro main) {
        this.main = main;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        Database db = main.getDatabase();
        PlayerDataManager pdm = main.getPlayerDataManager();

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            BukkitTask ifPlayerSyncApplyPlayerdata = new BukkitRunnable() {

                int timer = 0;

                @Override
                public void run() {
                    if (!player.isOnline()) {
                        if (main.getConfigHandler().isInDebugmode())
                            main.getLogger().severe(String.format("[DEBUG][CONNECTIONLISTENER] -> Der Spieler %s ist während des suchens der Spielerdata disconnected.", player.getName()));
                        this.cancel();
                        return;
                    }
                    if (timer == 12) {
                        if (main.getConfigHandler().isInDebugmode())
                            main.getLogger().severe(String.format("[DEBUG][CONNECTIONLISTENER] -> Playerdata Backup von %s konnte nicht gefunden werden.", player.getName()));
                        player.kickPlayer("Deine Spielerdata konnte leider nicht gefunden werden, wende dich bitte an einen Developer.");
                        this.cancel();
                        return;
                    }
                    if (timer == 10) {
                        if (main.getConfigHandler().isInDebugmode())
                            main.getLogger().severe(String.format("[DEBUG][CONNECTIONLISTENER] -> Playerdata von %s konnte nach 10 Sekunden nicht Synchronisiert werden, lade Backup.", player.getName()));

                        db.setData("SYNC", true, player.getUniqueId());
                    }

                    main.getDatabase().checkConnection();
                    PlayerData pd = db.getPlayerData(player.getUniqueId());

                    if (main.getConfigHandler().isInDebugmode())
                        main.getLogger().severe(String.format("[DEBUG][CONNECTIONLISTENER] -> Playerdata von %s wird gesucht.", player.getName()));

                    if (pd.isSync()) {
                        pd = db.getPlayerData(player.getUniqueId());
                        if (main.getConfigHandler().isInDebugmode())
                            main.getLogger().severe(String.format("[DEBUG][CONNECTIONLISTENER] -> Playerdata von %s wurde gefunden.", player.getName()));

                        //Spieler wird zur Online-Liste hinzugefügt
                        pdm.addPlayerData(pd);
                        //Spieler erhält seine gespeicherte Playerdata
                        applyPlayerData(player, pd);
                        this.cancel();
                    }
                    timer++;
                }
            }.runTaskTimer(main, 0, 20);
        });
        }

    private void applyPlayerData(Player player, PlayerData pd) {
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
        main.getDatabase().setData("SYNC", false, player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            Player player = event.getPlayer();
            Database db = main.getDatabase();
            PlayerDataManager pdm = main.getPlayerDataManager();
            PlayerData pd = pdm.getPlayerData(event.getPlayer().getUniqueId());


            //Spielers Playerdata wird gespeichert
            if (pd != null)
                db.updatePlayerData(player.getHealth(), player.getFoodLevel(), player.getExp() + player.getLevel(), player.getUniqueId(), true, InventorySerilization.saveModdedStacksData(player.getInventory().getContents()),  player.getInventory().getHeldItemSlot(), InventorySerilization.saveModdedStacksData(player.getEnderChest().getContents()));

            //Spieler wird aus der Liste der Online-Spieler entfernt
            pdm.removePlayerData(player.getUniqueId());
        });

    }

}
