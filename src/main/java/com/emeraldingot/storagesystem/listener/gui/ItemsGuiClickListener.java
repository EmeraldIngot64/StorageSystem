package com.emeraldingot.storagesystem.listener.gui;

import com.emeraldingot.storagesystem.gui.util.InventoryActionUtil;
import com.emeraldingot.storagesystem.langauge.Language;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.sql.SQLException;

public class ItemsGuiClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) throws SQLException {

//        if(!event.getAction().equals(InventoryAction.PLACE_ALL) && !event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
//            return;
//        }

        String title = event.getView().getTitle();


        // don't have to return here since the next if statement will
        if (!title.equals(Language.STORAGE_SYSTEM_ITEMS_TITLE)) {
            return;
        }

        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            if (event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                event.setCancelled(true);
                event.getWhoClicked().setItemOnCursor(event.getCurrentItem().clone());
            } else if (event.getAction().equals(InventoryAction.CLONE_STACK)) {
                event.setCancelled(true);
                ItemStack cloneStack = event.getCurrentItem().clone();
                cloneStack.setAmount(cloneStack.getMaxStackSize());
                event.getWhoClicked().setItemOnCursor(cloneStack);
            } else {
                event.setCancelled(true);
            }

        } else {
            if (InventoryActionUtil.isCrossInventory(event.getAction())) {
                event.setCancelled(true);
            }
        }


//        event.setCancelled(false);

    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) throws SQLException {

//        if(!event.getAction().equals(InventoryAction.PLACE_ALL) && !event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
//            return;
//        }

        String title = event.getView().getTitle();

        if (title.equals(Language.STORAGE_SYSTEM_ITEMS_TITLE)) {
            event.setCancelled(true);
            return;
        }


    }


}