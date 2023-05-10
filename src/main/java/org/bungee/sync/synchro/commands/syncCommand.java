package org.bungee.sync.synchro.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bungee.sync.synchro.Synchro;
import org.bungee.sync.synchro.player.PlayerData;
import org.bungee.sync.synchro.util.InventorySerilization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class syncCommand implements CommandExecutor {

    private final Synchro main;

    public syncCommand(Synchro main) {
        super();
        this.main = main;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            if(args.length >= 1){
                    Player p = (Player) sender;

                    double health = p.getHealth();
                    int hunger = p.getFoodLevel();
                    int experience = p.getTotalExperience();
                    UUID uuid = p.getUniqueId();
                    boolean sync = true;

                    if(Objects.equals(args[0], "invsee") && p.hasPermission("sync.invsee")){
                        PlayerData pd = main.getDatabase().getInvseeData(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                        p.sendMessage("ARMOR: " + pd.getArmor() + "| INVENTORY: " + pd.getInventory());
                        try {
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(InventorySerilization.itemStackArrayToBase64(InventorySerilization.restoreModdedStacks(pd.getInventory()))));
                            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

                            p.openInventory(inventory);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    if(Objects.equals(args[0], "debug") && p.hasPermission("sync.debug")){
                        if(Objects.equals(args[1], "enable")){
                            main.getConfig().set("Debug", true);
                        }
                        if(Objects.equals(args[1], "disable")){
                            main.getConfig().set("Debug", false);
                        }
                    }
                    if(Objects.equals(args[0], "kp") && p.hasPermission("sync.kp")){
                        System.out.println("kp Command");
                        if(args.length == 1) {
                            p.sendMessage("Deine Köpfepunkte: " +main.getPlayerDataManager().getPlayerData(p.getUniqueId()).getHeadpoints() + " .");
                        } else if(Objects.equals(args[1], "add") && args.length == 4){
                            if(main.getPlayerDataManager().getPlayerData(p.getUniqueId()) != null){
                                try{
                                    int number = Integer.parseInt(args[3]);
                                    Player pp;
                                    for(Player player : Bukkit.getOnlinePlayers()) {
                                        System.out.println(args[2]);
                                        System.out.println(player.getName());
                                        if(player.getName().equals(args[2])){
                                            pp = player;
                                            PlayerData pd = main.getPlayerDataManager().getPlayerData(pp.getUniqueId());
                                            int newheadpoints = pd.getHeadpoints() + number;
                                            if(newheadpoints <= 1000000) {
                                                pd.setHeadpoints(newheadpoints);
                                                main.getDatabase().updateHeadPoints(pp.getUniqueId(),newheadpoints);
                                            } else { p.sendMessage(String.format("Der Spieler %s kann nicht mehr als 10^6 Köpfepunkte haben.", pp.getName())); }
                                        }
                                    }
                                }
                                catch (NumberFormatException ex){
                                    ex.printStackTrace();
                                }
                            }
                        } else if(Objects.equals(args[1], "remove" ) && args.length == 4){
                            if(main.getPlayerDataManager().getPlayerData(p.getUniqueId()) != null){
                                try{
                                    int number = Integer.parseInt(args[3]);
                                    Player pp;
                                    for(Player player : Bukkit.getOnlinePlayers()) {
                                        System.out.println(args[2]);
                                        System.out.println(player.getName());
                                        if(player.getName().equals(args[2])){
                                            pp = player;
                                            PlayerData pd = main.getPlayerDataManager().getPlayerData(pp.getUniqueId());
                                            int newheadpoints = pd.getHeadpoints() - number;
                                            if(newheadpoints >= 0) {
                                                pd.setHeadpoints(newheadpoints);
                                                main.getDatabase().updateHeadPoints(pp.getUniqueId(),newheadpoints);
                                            } else { p.sendMessage(String.format("Der Spieler %s hat nicht genug Köpfepunkte dafür.", pp.getName())); }
                                        }
                                    }
                                }
                                catch (NumberFormatException ex){
                                    ex.printStackTrace();
                                }
                            }

                        }

                    }

            }
        }
        return false;
    }


}

