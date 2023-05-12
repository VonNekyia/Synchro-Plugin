package org.bungee.sync.synchro.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class syncTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("invsee","endersee", "debug"), new ArrayList<>());
        }
        if (args.length == 2 && args[1].equals("debug")) {
            return StringUtil.copyPartialMatches(args[1], Arrays.asList("enable", "disable"), new ArrayList<>());
        }
        return new ArrayList<>();
    }
}
