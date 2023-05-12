package org.bungee.sync.synchro.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bungee.sync.synchro.Synchro;

public class InventoryInteractionListener implements Listener {

    private final Synchro main;

    public InventoryInteractionListener(Synchro main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (main.getPlayerDataManager().getPlayerData(p.getUniqueId()) == null)
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if (main.getPlayerDataManager().getPlayerData(p.getUniqueId()) == null)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player p)) {
            return;
        }
        if (main.getPlayerDataManager().getPlayerData(p.getUniqueId()) == null)
            event.setCancelled(true);
    }

}
