package org.bungee.sync.synchro.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
@Deprecated
public class DelayedTask {

    private static Plugin plugin = null;
    private int id = -1;

    public DelayedTask(Plugin instance) {
        plugin = instance;
    }

    public DelayedTask(Runnable runnable) {
        this(runnable, 0);
    }

    public DelayedTask(Runnable runnable, long delay) {
        if (plugin.isEnabled()){
            id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
        } else {
            runnable.run();
        }

    }



    public DelayedTask(Runnable runnable, long delay, long repeat) {
        if (plugin.isEnabled()){
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delay, repeat);
        } else {
            runnable.run();
        }

    }




    //auch Async möglich https://bukkit.fandom.com/wiki/Scheduler_Programming

    public int getId() {
        return id;
    }
}
