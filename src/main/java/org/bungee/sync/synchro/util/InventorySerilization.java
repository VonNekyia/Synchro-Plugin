package org.bungee.sync.synchro.util;


import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bungee.sync.synchro.Synchro;

import java.io.IOException;

public class InventorySerilization {

    private final Synchro main;

    public InventorySerilization(Synchro main) {
        super();
        this.main = main;
    }

    public static ItemStack[] restoreModdedStacks(String string) {
        String[] strings = string.split(";");
        ItemStack[] itemStacks = new ItemStack[strings.length];
        for (int i = 0; i < strings.length; i++) {
            if (!strings[i].equals(""))
                try {
                    itemStacks[i] = StreamSerializer.getDefault().deserializeItemStack(strings[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return itemStacks;
    }

    public static String saveModdedStacksData(ItemStack[] itemStacks) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < itemStacks.length; i++) {
            if (i > 0)
                stringBuilder.append(";");
            if (itemStacks[i] != null && itemStacks[i].getType() != Material.AIR)
                try {
                    stringBuilder.append(StreamSerializer.getDefault().serializeItemStack(itemStacks[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        String string = stringBuilder.toString();
        return string;
    }


}