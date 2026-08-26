package com.emeraldingot.storagesystem.listener.gui;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.gui.GuiButton;
import com.emeraldingot.storagesystem.gui.StorageSystemHolder;
import com.emeraldingot.storagesystem.gui.terminal.StorageSystemGui;
import com.emeraldingot.storagesystem.gui.terminal.pages.TerminalItemsPage;
import com.emeraldingot.storagesystem.gui.util.InventoryActionUtil;
import com.emeraldingot.storagesystem.impl.ControllerManager;
import com.emeraldingot.storagesystem.impl.DatabaseManager;
import com.emeraldingot.storagesystem.impl.StorageCellData;
import com.emeraldingot.storagesystem.item.StorageCell;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public class TerminalGuiClickListener implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();

        if (!(holder instanceof StorageSystemHolder storageSystemHolder)) {
            return;
        }

        StorageCellData storageCellData = storageSystemHolder.getStorageCellData();
        UUID uuid = storageCellData.getUUID();

        int bytesUsed = StorageCell.getBytesUsed(storageCellData.getStorageCell());
        int capacity = StorageCell.getCapacity(storageCellData.getStorageCell());

        int bytesLeft = capacity - bytesUsed;


        if (event.getClickedInventory() == event.getView().getTopInventory()) {

            HashMap<ItemStack, GuiButton> buttonItems = StorageSystemGui.getButtonItems((Player)event.getWhoClicked());

            if (buttonItems.containsKey(event.getCurrentItem())) {
                // This setup ensures that you cannot right-click regular buttons
                // Left-click button
                if (event.isLeftClick()){
                    playSound(event);
                    buttonItems.get(event.getCurrentItem()).click(event);
                }

                event.setCancelled(true);
                return;
            }

            if (event.getCursor() == null && event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }


            for (int slot : TerminalItemsPage.NAVIGATION_BAR) {
                if (slot == event.getSlot()) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (InventoryActionUtil.isSwapAction(event.getAction())) {
                event.setCancelled(true);
                return;
            }

            if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                event.setCancelled(true);
            }


            // At this point whatever was clicked is an item



            // Single item type remove actions
            if (actionEquals(event, InventoryAction.PICKUP_SOME)) {
                event.setCancelled(true);
                return;
            }

            if (actionEquals(event, InventoryAction.PICKUP_ONE)) {
                ItemStack removedStack = event.getCursor().clone();
                removedStack.setAmount(1);
                removeFromCell(storageCellData, removedStack);
            }

            if (actionEquals(event, InventoryAction.PICKUP_HALF)) {
                ItemStack removedStack = event.getCurrentItem().clone();
                int amount = removedStack.getAmount();

                int newAmount = (int) Math.ceil(amount / 2d);

                removedStack.setAmount(newAmount);
                removeFromCell(storageCellData, removedStack);
            }

            if (actionEquals(event, InventoryAction.PICKUP_ALL)) {
                ItemStack removedStack = event.getCurrentItem();

                removeFromCell(storageCellData, removedStack);
            }



            // Single item type add actions
            if (actionEquals(event, InventoryAction.PLACE_SOME)) {
                event.setCancelled(true);
                return;
            }

            if (actionEquals(event, InventoryAction.PLACE_ONE)) {
                ItemStack addedStack = event.getCursor().clone();
                addedStack.setAmount(1);

                if (getScaledByteValue(addedStack) > bytesLeft) {
                    event.setCancelled(true);
                    return;
                }

                addToCell(storageCellData, addedStack);
            }

            if (actionEquals(event, InventoryAction.PLACE_ALL)) {
                ItemStack addedStack = event.getCursor();

                if (getScaledByteValue(addedStack) > bytesLeft) {
                    event.setCancelled(true);
                    return;
                }
                addToCell(storageCellData, addedStack);
            }

            // Other actions
            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {

                ItemStack removedStack = event.getCurrentItem();
                removeFromCell(storageCellData, removedStack);
            }

            if (InventoryActionUtil.isDropAction(event.getAction())) {
                event.setCancelled(true);
                return;
            }


        }

        if (event.getClickedInventory() == event.getView().getBottomInventory()) {


            if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                event.setCancelled(true);
            }


            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {

                ItemStack addedStack = event.getCurrentItem().clone();

                if (getScaledByteValue(addedStack) > bytesLeft) {
                    event.setCancelled(true);
                    return;
                }

                boolean refreshPage = false;

                // If the inventory is full, a new page needs to be added
                // The item also needs to be manually removed from the client since it has nowhere to go
                if (event.getView().getTopInventory().firstEmpty() == -1) {
                    refreshPage = true;
                    event.getWhoClicked().getInventory().setItem(event.getSlot(), null);
                }

                addToCell(storageCellData, addedStack);

                if (refreshPage) {
                    TerminalItemsPage terminalItemsPage = new TerminalItemsPage((Player)event.getWhoClicked(), storageCellData, storageSystemHolder.getPageNumber());
                    event.getWhoClicked().openInventory(terminalItemsPage.build());
                }

            }
        }

//        event.setCancelled(true);

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!(event.getView().getTopInventory().getHolder() instanceof StorageSystemHolder)) {
            return;
        }
        event.setCancelled(true);
    }

    private static void playSound(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 1f);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof StorageSystemHolder)) {
            return;
        }

        Player player = (Player)event.getPlayer();

        // Clear out the player's button map after they close the inventory
        Bukkit.getScheduler().runTaskLater(StorageSystem.getInstance(), () -> {
            if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof StorageSystemHolder)) {
                StorageSystemGui.getButtonItems(player).clear();
            }

        }, 1L); // Delay 1 tick

    }

    private static boolean actionEquals(InventoryClickEvent event, InventoryAction action) {
        return event.getAction().equals(action);
    }

    public static void addToCell(StorageCellData storageCellData, ItemStack itemStack) {
        UUID uuid = storageCellData.getUUID();

        ItemStack cell = storageCellData.getStorageCell();
        ItemMeta cellMeta = cell.getItemMeta();
        int bytesUsed = StorageCell.getBytesUsed(cell);

        int stackBytes = getScaledByteValue(itemStack);

        cellMeta.getPersistentDataContainer().set(StorageCell.BYTES_USED_KEY, PersistentDataType.INTEGER, bytesUsed + stackBytes);
        cell.setItemMeta(cellMeta);

        DatabaseManager.getInstance().addItemsToCell(uuid, itemStack);
    }

    public static void removeFromCell(StorageCellData storageCellData, ItemStack itemStack) {
        UUID uuid = storageCellData.getUUID();

        ItemStack cell = storageCellData.getStorageCell();
        ItemMeta cellMeta = cell.getItemMeta();
        int bytesUsed = StorageCell.getBytesUsed(cell);

        int stackBytes = getScaledByteValue(itemStack);

        cellMeta.getPersistentDataContainer().set(StorageCell.BYTES_USED_KEY, PersistentDataType.INTEGER, bytesUsed - stackBytes);
        cell.setItemMeta(cellMeta);

        DatabaseManager.getInstance().removeItemsFromCell(uuid, itemStack);
    }

    public static int getScaledByteValue(ItemStack itemStack) {
        int maxStackSize = itemStack.getMaxStackSize();
        int bytesPerItem = 64 / maxStackSize;

        return itemStack.getAmount() * bytesPerItem;
    }

}
