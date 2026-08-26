package com.emeraldingot.storagesystem.listener.controller;


import com.emeraldingot.storagesystem.block.StorageControllerBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.sql.SQLException;

public class StorageControllerEntityChangeListener implements Listener {
    @EventHandler
    public void onBlockPlace(EntityChangeBlockEvent event) throws SQLException {
        if (StorageControllerBlock.isStorageController(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

    }


}
