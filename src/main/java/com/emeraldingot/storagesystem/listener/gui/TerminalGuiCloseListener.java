package com.emeraldingot.storagesystem.listener.gui;


import com.emeraldingot.storagesystem.gui.StorageSystemHolder;
import com.emeraldingot.storagesystem.impl.ControllerManager;
import com.emeraldingot.storagesystem.impl.StorageCellData;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.sql.SQLException;

public class TerminalGuiCloseListener implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) throws SQLException {

        InventoryHolder inventoryHolder = event.getView().getTopInventory().getHolder();

        if (!(inventoryHolder instanceof StorageSystemHolder storageSystemHolder)) {
            return;
        }

        StorageCellData storageCellData = storageSystemHolder.getStorageCellData();

        Location controllerLocation = storageCellData.getControllerLocation();

        ControllerManager.getInstance().recalculateUsed(controllerLocation);
        ControllerManager.getInstance().closeController(controllerLocation);


    }


}
