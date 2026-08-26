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
    private final StorageCellType storageCellType;
    private final UUID uuid;
    private final ItemStack storageCell;
    private final Location controllerLocation;

    public StorageCellData(StorageCellType storageCellType, UUID uuid, ItemStack storageCell, Location controllerLocation) {
        this.storageCellType = storageCellType;
        this.uuid = uuid;
        this.controllerLocation = controllerLocation;
        this.storageCell = storageCell;
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
