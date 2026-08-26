package com.emeraldingot.storagesystem.listener.controller;


import com.emeraldingot.storagesystem.block.StorageControllerBlock;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.sql.SQLException;

public class PistonExtendListener implements Listener {
    @EventHandler
    public void onBlockDispense(BlockPistonExtendEvent event) throws SQLException {

        for (Block block : event.getBlocks()) {
            if (StorageControllerBlock.isStorageController(block.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }


    }


}
