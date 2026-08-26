package com.emeraldingot.storagesystem.listener;


import com.emeraldingot.storagesystem.item.StorageCell;
import com.emeraldingot.storagesystem.item.StorageCore;
import com.emeraldingot.storagesystem.item.StorageTerminal;
import com.emeraldingot.storagesystem.langauge.Language;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

public class StorageItemPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws SQLException {

        ItemStack itemStack = event.getItemInHand();

        if (itemStack.getType() != Material.PLAYER_HEAD) {
            return;
        }

        if (StorageCell.isStorageCell(itemStack)) {
            event.setCancelled(true);
            return;
        }

        if (StorageTerminal.isStorageTerminal(itemStack)) {
            event.setCancelled(true);
            return;
        }

        if (StorageCore.isStorageCore(itemStack)) {
            event.setCancelled(true);
            return;
        }

        if (itemStack.getItemMeta().getLore() == null) {
            return;
        }


        // For legacy purposes to ensure that items cannot be placed before being migrated
        List<String> lore = itemStack.getItemMeta().getLore();

        if (lore.contains(Language.STORAGE_SYSTEM_LORE_TAG)) {
            event.setCancelled(true);
            return;
        }


    }


}
