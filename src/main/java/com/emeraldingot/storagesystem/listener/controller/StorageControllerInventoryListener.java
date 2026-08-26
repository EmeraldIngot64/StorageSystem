package com.emeraldingot.storagesystem.listener.controller;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.block.StorageControllerBlock;
import com.emeraldingot.storagesystem.impl.ControllerManager;
import com.emeraldingot.storagesystem.item.StorageCell;
import com.emeraldingot.storagesystem.langauge.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class StorageControllerInventoryListener implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

//        if(!event.getAction().equals(InventoryAction.PLACE_ALL) && !event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
//            return;
//        }
//        if (event.getInventory() instanceof CraftInventoryCrafting) {
//            return;
//        }


        String title = event.getView().getTitle();
        if (!title.startsWith(Language.STORAGE_CONTROLLER_ITEM)) {
            return;
        }

        if (event.getView().getTopInventory().getItem(4) == null) {
            StorageControllerBlock.setOffline(event.getView().getTopInventory());
        } else {
            StorageControllerBlock.setOnline(event.getView().getTopInventory());
        }

        if (event.getAction().equals(InventoryAction.HOTBAR_SWAP) || event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)) {
            event.setCancelled(true);
            return;
        }


        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        if (currentItem == null) {
            return;
        }

        if (StorageControllerBlock.isStatusPane(currentItem) || StorageControllerBlock.isStatusPane(currentItem)) {
            event.setCancelled(true);
            return;
        }
        // this doesn't work for hotbar swap since of the two swapped items one is valid
        if (StorageCell.isStorageCell(currentItem) || StorageCell.isStorageCell(cursorItem)) {
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
            return;
        }


        event.setCancelled(false);

        Bukkit.getScheduler().runTaskLater(StorageSystem.getInstance(), new Runnable() {
            @Override
            public void run() {
                Location location = event.getView().getTopInventory().getLocation();
                ControllerManager.getInstance().updateState(location);
            }
        }, 5L);


    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

//        if(!event.getAction().equals(InventoryAction.PLACE_ALL) && !event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
//            return;
//        }
//        if (event.getInventory() instanceof CraftInventoryCrafting) {
//            return;
//        }


        String title = event.getView().getTitle();
        if (!title.startsWith(Language.STORAGE_CONTROLLER_ITEM)) {
            return;
        }

        event.setCancelled(true);
        return;
    }

}
