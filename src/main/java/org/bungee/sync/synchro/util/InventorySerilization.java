package org.bungee.sync.synchro.util;


import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bungee.sync.synchro.Synchro;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class InventorySerilization {

    private final Synchro main;

    public InventorySerilization(Synchro main) {
        super();
        this.main = main;
    }


    public String encodeItems(ItemStack[] items) {
        return saveModdedStacksData(items);
    }

    public ItemStack[] decodeItems(String data) throws Exception {

            ItemStack[] it = restoreModdedStacks(data);
            if (it == null)
                it = itemStackArrayFromBase64(data);
            return it;
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
        public static ItemStack[] itemStackArrayFromBase64 (String data) throws IOException {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                ItemStack[] items = new ItemStack[dataInput.readInt()];
                for (int i = 0; i < items.length; i++)
                    items[i] = (ItemStack) dataInput.readObject();
                dataInput.close();
                return items;
            } catch (ClassNotFoundException e) {
                throw new IOException("Unable to decode class type.", e);
            }
        }


}