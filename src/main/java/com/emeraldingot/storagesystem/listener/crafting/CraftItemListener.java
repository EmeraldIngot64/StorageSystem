package com.emeraldingot.storagesystem.listener.crafting;


import com.emeraldingot.storagesystem.impl.StorageCellType;
import com.emeraldingot.storagesystem.item.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.UUID;

public class CraftItemListener implements Listener {
    @EventHandler
    public void onBlockDispense(CraftItemEvent event) throws SQLException {


        Recipe recipe = event.getRecipe();


        if (
                isSameName(recipe.getResult(), new StorageCell4K().getStack()) ||
                        isSameName(recipe.getResult(), new StorageCell16K().getStack()) ||
                        isSameName(recipe.getResult(), new StorageCell64K().getStack()) ||
                        isSameName(recipe.getResult(), new StorageCell256K().getStack())
        ) {
//            System.out.println("crafted storage cell - copying disk data");
            ItemStack newCell = event.getCurrentItem();

            PersistentDataContainer persistentDataContainer = newCell.getItemMeta().getPersistentDataContainer();
            StorageCellType cellType = StorageCellType.valueOf(persistentDataContainer.get(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING));
            StorageCell storageCell = cellType.getCell();

            ItemStack oldCell = event.getInventory().getItem(5);

            UUID uuid = StorageCell.getUuid(oldCell);

            int oldFilledAmount = StorageCell.getBytesUsed(oldCell);

            ItemStack migratedCell = storageCell.getStack(oldFilledAmount, uuid);

            event.setCurrentItem(migratedCell);

//            newCell.setItemMeta(itemMeta);

        }
//
        event.setCancelled(false);

    }

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
