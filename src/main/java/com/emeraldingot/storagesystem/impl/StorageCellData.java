package com.emeraldingot.storagesystem.impl;

import com.emeraldingot.storagesystem.item.StorageCell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class StorageCellData {
    private StorageCellType storageCellType;
    private UUID uuid;
    private ItemStack storageCell;
    private Location controllerLocation;

    public StorageCellData(StorageCellType storageCellType, UUID uuid, ItemStack storageCell, Location controllerLocation) {
        this.storageCellType = storageCellType;
        this.uuid = uuid;
        this.controllerLocation = controllerLocation;
        this.storageCell = storageCell;
    }

    // TODO: Move this to StorageCell and check persistent data instead of lore


    public static StorageCellData fromGUILore(List<String> lore) {
        UUID cellUUID = UUID.fromString(lore.get(0).split("§8")[1]);
        double x = Double.parseDouble(lore.get(1).split("X: ")[1].split(" Y:")[0]);
        double y = Double.parseDouble(lore.get(1).split(" Y: ")[1].split(" Z: ")[0]);
        double z = Double.parseDouble(lore.get(1).split(" Z: ")[1].split(" W: ")[0]);
        World world = Bukkit.getWorld(lore.get(1).split(" W: ")[1]);
        Location blockLocation = new Location(world, x, y, z);
        return new StorageCellData(null, cellUUID, null, blockLocation);
    }

    public static StorageCellData fromItemLore(List<String> lore) {
        UUID cellUUID = UUID.fromString(lore.get(1).split("§fCell ID: ")[1]);
        Location blockLocation = null;
        return new StorageCellData(null, cellUUID, null, blockLocation);
    }

    public static StorageCellData fromItemStack(ItemStack itemStack, Location newLocation) {
        UUID newUuid = StorageCell.getUuid(itemStack);
        StorageCellType newType = StorageCellType.valueOf(itemStack.getItemMeta().getPersistentDataContainer().get(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING));


        return new StorageCellData(newType, newUuid, itemStack, newLocation);
    }

    public UUID getUUID() {
        return uuid;
    }

    public Location getControllerLocation() {
        return controllerLocation;
    }

    public StorageCellType getStorageCellType() {
        return storageCellType;
    }

    public ItemStack getStorageCell() {
        return storageCell;
    }
}
