package org.bungee.sync.synchro.player;

import java.io.Serializable;
import java.util.UUID;

public class PlayerData implements Serializable {

    //JavaBean for the PlayerData ONLY holds start values

    private UUID uuid;
    private int hunger;
    private double health;
    private float experience;
    private boolean sync;
    private String inventory;
    private int heltItemSlot;
    private String enderchest;

    public String getEnderchest() {
        return enderchest;
    }

    public void setEnderchest(String enderchest) {
        this.enderchest = enderchest;
    }

    public int getHeltItemSlot() {
        return heltItemSlot;
    }

    public void setHeltItemSlot(int heltItemSlot) {
        this.heltItemSlot = heltItemSlot;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }


}
