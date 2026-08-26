package com.emeraldingot.storagesystem.gui;

import com.emeraldingot.storagesystem.impl.StorageCellData;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class StorageSystemHolder implements InventoryHolder {

    private final StorageCellData storageCellData;
    private final int pageNumber;

    public StorageSystemHolder (StorageCellData storageCellData, int pageNumber) {
        this.storageCellData = storageCellData;
        this.pageNumber = pageNumber;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    public StorageCellData getStorageCellData() {
        return storageCellData;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
