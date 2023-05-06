package org.bungee.sync.synchro.util;

import org.bungee.sync.synchro.Synchro;

@Deprecated

public class ConfigHandler {
    private final Synchro main;
    public ConfigHandler(Synchro main) {
        this.main = main;
    }

    public boolean isInDebugmode() {
        return main.getConfig().getBoolean("Debug");
    }



}
