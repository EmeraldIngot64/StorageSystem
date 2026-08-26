package com.emeraldingot.storagesystem.util;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


public class ControllerFileManager {
    public static FileConfiguration controllersData;

    private File controllersFile;
    private File langFolder;

    private static ControllerFileManager instance;


    public static ControllerFileManager getInstance()
    {
        if (instance == null) {
            instance = new ControllerFileManager();
        }
        return instance;
    }

    public void initData(Plugin plugin) {
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        controllersFile = new File(plugin.getDataFolder(), "controllers.yml");
        if (!controllersFile.exists()) {
            try {
                controllersFile.createNewFile(); //This needs a try catch
                controllersData = YamlConfiguration.loadConfiguration(controllersFile);
                controllersData.set("controllers", new ArrayList<String>());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        controllersData = YamlConfiguration.loadConfiguration(controllersFile);

        langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdir();
        }

    }

//    public void updateData() {
//        MoneyManager.getInstance().resetTable();
//        for (String playerUUID : lossesData.getKeys(false)) {
//            MoneyManager.getInstance().addPlayer(UUID.fromString(playerUUID), Long.parseLong(lossesData.get(playerUUID).toString()));
//        }
//    }



    public void writeControllers(ArrayList<Location> locations) {
        List<String> stringLocations = new ArrayList<>();
        for (Location location : locations) {
            stringLocations.add(Base64Util.toBase64(location));
        }
        controllersData.set("controllers", stringLocations);

        try {
            controllersData.save(controllersFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<Location> loadControllers() throws IOException {
        List<String> stringLocations = controllersData.getStringList("controllers");
        ArrayList<Location> locations = new ArrayList<>();
        for (String string : stringLocations) {
            locations.add((Location) Base64Util.fromBase64(string));
        }

        return locations;

    }

    public void writeBalance(UUID playerUUID, long amount) {
        controllersData.set(String.valueOf(playerUUID), amount);
        try {
            controllersData.save(controllersFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public long getBalance(UUID playerUUID) {
        return (long) controllersData.get(String.valueOf(playerUUID));
    }

    public Path getLangFolder() {
        return langFolder.toPath();
    }

//    public boolean isNewPlayer(Player player) {
//        return !moneyData.contains(player.getDisplayName());
//    }
}
