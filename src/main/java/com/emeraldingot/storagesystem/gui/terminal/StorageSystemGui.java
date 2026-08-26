package com.emeraldingot.storagesystem.gui.terminal;

import com.emeraldingot.storagesystem.gui.GuiButton;
import com.emeraldingot.storagesystem.gui.terminal.pages.TerminalItemsPage;
import com.emeraldingot.storagesystem.impl.StorageCellData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class StorageSystemGui {
    private static HashMap<Player, HashMap<ItemStack, GuiButton>> buttonItems = new HashMap<>();

    public static Inventory getInventory(Player player, StorageCellData storageCellData) {

        TerminalItemsPage terminalItemsPage = new TerminalItemsPage(player, storageCellData, 0);

        return terminalItemsPage.build();
    }

    public static HashMap<ItemStack, GuiButton> getButtonItems(Player player) {
        if (!buttonItems.containsKey(player)) {
            buttonItems.put(player, new HashMap<>());
        }

        return buttonItems.get(player);
    }
}
