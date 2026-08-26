package com.emeraldingot.storagesystem.gui.util;

import org.bukkit.event.inventory.InventoryAction;

import java.util.Arrays;

public class InventoryActionUtil {
    private static final InventoryAction[] CROSS_INVENTORY_ACTIONS = {
            InventoryAction.COLLECT_TO_CURSOR,
            InventoryAction.MOVE_TO_OTHER_INVENTORY
    };

    private static final InventoryAction[] SWAP_ITEM_ACTIONS = {
            InventoryAction.SWAP_WITH_CURSOR,
            InventoryAction.HOTBAR_SWAP
    };


    private static final InventoryAction[] DROP_ACTIONS = {
            InventoryAction.DROP_ALL_SLOT,
            InventoryAction.DROP_ONE_SLOT,
            InventoryAction.DROP_ONE_CURSOR,
            InventoryAction.DROP_ALL_CURSOR
    };

    private static final InventoryAction[] PICK_UP_ACTIONS = {
            InventoryAction.PICKUP_ALL,
            InventoryAction.PICKUP_HALF,
            InventoryAction.PICKUP_ONE,
            InventoryAction.PICKUP_SOME
    };


    public static boolean isCrossInventory(InventoryAction action) {
        return Arrays.stream(CROSS_INVENTORY_ACTIONS).toList().contains(action);
    }

    public static boolean isDropAction(InventoryAction action) {
        return Arrays.stream(DROP_ACTIONS).toList().contains(action);
    }

    public static boolean isPickUpAction(InventoryAction action) {
        return Arrays.stream(PICK_UP_ACTIONS).toList().contains(action);
    }


    public static boolean isSwapAction(InventoryAction action) {
        return Arrays.stream(SWAP_ITEM_ACTIONS).toList().contains(action);
    }
}
