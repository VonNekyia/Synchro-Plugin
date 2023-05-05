package org.bungee.sync.synchro.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bungee.sync.synchro.Synchro;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            if(args.length == 1){

                Player p = (Player) sender;
                double health = p.getHealth();
                int hunger = p.getFoodLevel();
                int experience = p.getTotalExperience();
                UUID uuid = p.getUniqueId();
                boolean sync = true;

                if(Objects.equals(args[0], "set")){

                }
                if(Objects.equals(args[0], "get")){

                }
                if(Objects.equals(args[0], "debug")){
                    if(Objects.equals(args[1], "enable")){
                        main.getConfig().set("Debug", true);
                    }
                    if(Objects.equals(args[1], "disable")){
                        main.getConfig().set("Debug", false);
                    }
                }
            }
        }
        return false;
    }


}

