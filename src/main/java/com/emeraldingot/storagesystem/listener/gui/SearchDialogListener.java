package com.emeraldingot.storagesystem.listener.gui;

import com.emeraldingot.storagesystem.gui.StorageSystemHolder;
import com.emeraldingot.storagesystem.gui.util.GuiUtil;
import com.emeraldingot.storagesystem.impl.ControllerManager;
import com.emeraldingot.storagesystem.impl.SearchData;
import com.emeraldingot.storagesystem.impl.StorageCellData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCustomClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.sql.SQLException;
import java.util.Map;

public class SearchDialogListener implements Listener {
    @EventHandler
    public void onCustomClick(PlayerCustomClickEvent event) {

        if (!event.getId().toString().equals("storagesystem:submit_search")) {
            return;
        }

        Player player = event.getPlayer();
        JsonElement jsonElement = event.getData();

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SearchData searchData = SearchData.fromJson(jsonObject);

        GuiUtil.fulfillCallback(player.getUniqueId(), searchData);



    }

}
