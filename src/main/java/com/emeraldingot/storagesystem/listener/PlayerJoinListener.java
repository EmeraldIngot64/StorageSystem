package com.emeraldingot.storagesystem.listener;


import com.emeraldingot.storagesystem.StorageSystem;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class PlayerJoinListener implements Listener {
    private static final Set<NamespacedKey> RECIPE_KEYS = Set.of(
            new NamespacedKey(StorageSystem.getInstance(), "storage_cell_1k"),
            new NamespacedKey(StorageSystem.getInstance(), "storage_cell_4k"),
            new NamespacedKey(StorageSystem.getInstance(), "storage_cell_16k"),
            new NamespacedKey(StorageSystem.getInstance(), "storage_cell_64k"),
            new NamespacedKey(StorageSystem.getInstance(), "storage_cell_256k"),
            new NamespacedKey(StorageSystem.getInstance(), "storage_controller"),
            new NamespacedKey(StorageSystem.getInstance(), "storage_core"),
            new NamespacedKey(StorageSystem.getInstance(), "storage_terminal")
    );

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (event.getPlayer().hasPermission("storagesystem.use")) {
            event.getPlayer().discoverRecipes(RECIPE_KEYS);
        } else {
            event.getPlayer().undiscoverRecipes(RECIPE_KEYS);
        }


    }


}
