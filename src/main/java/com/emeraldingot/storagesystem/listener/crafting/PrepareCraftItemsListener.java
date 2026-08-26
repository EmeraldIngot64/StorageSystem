package com.emeraldingot.storagesystem.listener.crafting;


import com.emeraldingot.storagesystem.block.StorageControllerBlock;
import com.emeraldingot.storagesystem.impl.StorageCellType;
import com.emeraldingot.storagesystem.item.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;

public class PrepareCraftItemsListener implements Listener {
    @EventHandler
    public void onBlockDispense(PrepareItemCraftEvent event) throws SQLException {
        // The exact choice recipe matcher does not work with differing lore so it is set to accept player heads instead
        // this checks to make sure the correct player head is in place

        Recipe recipe = event.getRecipe();

        if (event.getInventory().getResult() == null) {
            return;
        }

        // Recipe: Storage Controller
        if (StorageControllerBlock.isStorageControllerItem(event.getInventory().getResult())) {
            if (StorageCore.isStorageCore(event.getInventory().getItem(5))) {
                return;
            } else {
                event.getInventory().setResult(null);
            }
        }

        // From here on is storage cells only

        ItemMeta itemMeta = event.getInventory().getResult().getItemMeta();

        String cellTypeString = itemMeta.getPersistentDataContainer().get(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING);

        if (cellTypeString == null) {
            return;
        }
        StorageCellType storageCellType = StorageCellType.valueOf(cellTypeString);


        // Recipe: 1k cell
        if (storageCellType.equals(StorageCellType.CELL_1K)) {
            ;
            if (StorageCore.isStorageCore(event.getInventory().getItem(5))) {
                return;
            } else {
                event.getInventory().setResult(null);
            }
        }

        // Recipe: 4k cell
        if (storageCellType.equals(StorageCellType.CELL_4K)) {

            ItemStack centerItem = event.getInventory().getItem(5);
            try {
                ItemMeta centerMeta = centerItem.getItemMeta();
                StorageCellType centerCellType = StorageCellType.valueOf(centerMeta.getPersistentDataContainer().get(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING));

                if (centerCellType.equals(StorageCellType.CELL_1K)) {
                    return;
                }

                event.getInventory().setResult(null);
            } catch (Exception e) {
                if (StorageCell.isLegacy(centerItem)) {
                    StorageCell.migrateLegacy(centerItem);
                } else {
                    event.getInventory().setResult(null);
                }

            }

        }

        // Recipe: 16k cell
        if (isSameName(event.getInventory().getResult(), new StorageCell16K().getStack())) {

            ItemStack centerItem = event.getInventory().getItem(5);
            try {
                ItemMeta centerMeta = event.getInventory().getItem(5).getItemMeta();
                StorageCellType centerCellType = StorageCellType.valueOf(centerMeta.getPersistentDataContainer().get(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING));

                if (centerCellType.equals(StorageCellType.CELL_4K)) {
                    return;
                }

                event.getInventory().setResult(null);
            } catch (Exception e) {
                if (StorageCell.isLegacy(centerItem)) {
                    StorageCell.migrateLegacy(centerItem);
                } else {
                    event.getInventory().setResult(null);
                }
            }
        }

        // Recipe: 64k cell
        if (isSameName(event.getInventory().getResult(), new StorageCell64K().getStack())) {

            ItemStack centerItem = event.getInventory().getItem(5);
            try {
                ItemMeta centerMeta = event.getInventory().getItem(5).getItemMeta();
                StorageCellType centerCellType = StorageCellType.valueOf(centerMeta.getPersistentDataContainer().get(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING));

                if (centerCellType.equals(StorageCellType.CELL_16K)) {
                    return;
                }

                event.getInventory().setResult(null);
            } catch (Exception e) {
                if (StorageCell.isLegacy(centerItem)) {
                    StorageCell.migrateLegacy(centerItem);
                } else {
                    event.getInventory().setResult(null);
                }
            }
        }

        // Recipe: 256k cell
        if (isSameName(event.getInventory().getResult(), new StorageCell256K().getStack())) {

            ItemStack centerItem = event.getInventory().getItem(5);
            try {
                ItemMeta centerMeta = event.getInventory().getItem(5).getItemMeta();
                StorageCellType centerCellType = StorageCellType.valueOf(centerMeta.getPersistentDataContainer().get(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING));

                if (centerCellType.equals(StorageCellType.CELL_64K)) {
                    return;
                }

                event.getInventory().setResult(null);
            } catch (Exception e) {
                if (StorageCell.isLegacy(centerItem)) {
                    StorageCell.migrateLegacy(centerItem);
                } else {
                    event.getInventory().setResult(null);
                }
            }
        }


//            newCell.setItemMeta(itemMeta);

    }
//

    private boolean isSameName(ItemStack firstStack, ItemStack secondStack) {
        try {
            String firstName = firstStack.getItemMeta().getItemName();
            String secondName = secondStack.getItemMeta().getItemName();
            return firstName.equals(secondName);
        } catch (NullPointerException e) {
            return false;
        }

    }


}
