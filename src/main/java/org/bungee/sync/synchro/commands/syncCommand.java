package org.bungee.sync.synchro.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.player.PlayerData;
import org.bungee.sync.synchro.util.InventorySerilization;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class syncCommand implements CommandExecutor {

    private final Synchro main;

    public syncCommand(Synchro main) {
        super();
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(args.length >= 1)) {
            sender.sendMessage("ERROR: Bitte nutze /sync <command> <argumente>.");
            return false;
        }

        if (Objects.equals(args[0], "debug") && sender.hasPermission("sync.debug")) {
            if (!(args.length >= 2)) {
                sender.sendMessage("ERROR: Bitte nutze /sync debug <enable/disable>.");
                return false;
            }
            if (Objects.equals(args[1], "enable")) {
                main.getConfig().set("Debug", true);
            }
            if (Objects.equals(args[1], "disable")) {
                main.getConfig().set("Debug", false);
            }
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage("ERROR: Du musst für diesen Command ein Spieler sein!");
            return false;
        }

        if (Objects.equals(args[0], "invsee") && p.hasPermission("sync.invsee")) {
            if (!(args.length >= 2)) {
                p.sendMessage("ERROR: Bitte nutze /sync invsee <Player>.");
                return false;
            }

            PlayerData pd = main.getDatabase().getInventoryData(Bukkit.getOfflinePlayer(args[1]).getUniqueId());

            if (pd == null) {
                p.sendMessage("ERROR: Der gesuchte Spieler hat nie den Server betreten.");
                return false;
            }
            Inventory inventory = Bukkit.getServer().createInventory(null, 45);
            inventory.setContents(InventorySerilization.restoreModdedStacks(pd.getInventory()));
            p.openInventory(inventory);
        }
        if (Objects.equals(args[0], "endersee") && p.hasPermission("sync.endersee")) {
            if (!(args.length >= 2)) {
                p.sendMessage("ERROR: Bitte nutze /sync endersee <Player>.");
                return false;
            }

            PlayerData pd = main.getDatabase().getEnderchestData(Bukkit.getOfflinePlayer(args[1]).getUniqueId());

            if (pd == null) {
                p.sendMessage("ERROR: Der gesuchte Spieler hat nie den Server betreten.");
                return false;
            }
            Inventory inventory = Bukkit.getServer().createInventory(null, 36);
            inventory.setContents(InventorySerilization.restoreModdedStacks(pd.getEnderchest()));
            p.openInventory(inventory);
        }
        return false;
    }

}