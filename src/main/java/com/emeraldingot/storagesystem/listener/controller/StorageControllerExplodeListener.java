package com.emeraldingot.storagesystem.listener.controller;


import com.emeraldingot.storagesystem.block.StorageControllerBlock;
import com.emeraldingot.storagesystem.impl.ControllerManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.Inventory;

public class StorageControllerExplodeListener implements Listener {
    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {

        for (int i = 0; i < event.blockList().size(); i++) {
            Block block = event.blockList().get(i);
            if (block.getType() != Material.DISPENSER) {
                continue;
            }

            if (!StorageControllerBlock.isStorageController(block.getLocation())) {
                continue;
            }


            Inventory inventory = ((Dispenser) block.getState()).getInventory();
            StorageControllerBlock.clearStatusSlots(inventory);

            if (ControllerManager.getInstance().isInUse(block.getLocation())) {
                Player player = ControllerManager.getInstance().getPlayerUsing(block.getLocation());
                player.closeInventory();
                ControllerManager.getInstance().closeController(block.getLocation());
            }

            ControllerManager.getInstance().removeController(block.getLocation());
            if (inventory.getItem(4) != null) {
                block.getWorld().dropItemNaturally(block.getLocation(), inventory.getItem(4));
            }
            block.getWorld().dropItemNaturally(block.getLocation(), StorageControllerBlock.getStack());
            block.setType(Material.AIR);
            event.blockList().remove(i);
            i--;
        }


//

//        event.setCancelled(true);
//        block.getWorld().dropItemNaturally(block.getLocation(), StorageControllerBlock.getStack());


    }


}
