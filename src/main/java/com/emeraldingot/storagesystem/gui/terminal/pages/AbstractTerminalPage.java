package com.emeraldingot.storagesystem.gui.terminal.pages;

import com.emeraldingot.storagesystem.impl.StorageCellData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class AbstractTerminalPage {
    protected Player player;
    protected StorageCellData storageCellData;

    public AbstractTerminalPage(Player player, StorageCellData storageCellData) {
        this.player = player;
        this.storageCellData = storageCellData;
    }

    public abstract Inventory build();
}
