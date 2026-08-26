package com.emeraldingot.storagesystem.block;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.langauge.Language;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StorageControllerBlock {

    private static final int[] SPACER_SLOTS = {0, 1, 2, 3, 5, 6, 7, 8};

    public static final NamespacedKey CONTROLLER_KEY = new NamespacedKey(StorageSystem.getInstance(), "storage_controller");

    public static ItemStack getStack() {
        ItemStack storageController = new ItemStack(Material.DISPENSER);
        ItemMeta itemMeta = storageController.getItemMeta();
        itemMeta.setItemName(ChatColor.YELLOW + "Storage Controller");
        itemMeta.setDisplayName(ChatColor.YELLOW + "Storage Controller");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "StorageSystem");
        itemMeta.setLore(lore);

        itemMeta.getPersistentDataContainer().set(CONTROLLER_KEY, PersistentDataType.BYTE, (byte) 0);

        storageController.setItemMeta(itemMeta);


        return storageController;
    }

    public static void setOffline(Inventory inventory) {

        ItemStack indicator = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta indicatorMeta = indicator.getItemMeta();
        indicatorMeta.setItemName(ChatColor.RED + "OFFLINE");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Language.INDICATOR_LORE);
        indicatorMeta.setLore(lore);
        indicator.setItemMeta(indicatorMeta);


        for (int slot : SPACER_SLOTS) {
            inventory.setItem(slot, indicator);
        }


    }

    public static void setOnline(Inventory inventory) {

        ItemStack indicator = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta indicatorMeta = indicator.getItemMeta();
        indicatorMeta.setItemName(ChatColor.GREEN + "ONLINE");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "Indicator");
        indicatorMeta.setLore(lore);
        indicator.setItemMeta(indicatorMeta);

        for (int slot : SPACER_SLOTS) {
            inventory.setItem(slot, indicator);
        }

    }

    public static void clearStatusSlots(Inventory inventory) {

        for (int slot : SPACER_SLOTS) {
            inventory.setItem(slot, null);
        }

    }

    public static boolean isStatusPane(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        if (itemMeta.getLore() == null) {
            return false;
        }
        if (!(itemMeta.getLore().size() == 1)) {
            return false;
        }
        if (itemMeta.getLore().get(0).equals(Language.INDICATOR_LORE)) {
            return true;
        }
        return false;
    }

    public static boolean isStorageController(Location location) {
        if (location.getBlock().getType() != Material.DISPENSER) {
            return false;
        }
        Dispenser dispenser = (Dispenser) location.getBlock().getState();

        if (isLegacyBlock(dispenser.getInventory())) {
            dispenser.getPersistentDataContainer().set(CONTROLLER_KEY, PersistentDataType.BYTE, (byte) 0);
        }

        return dispenser.getPersistentDataContainer().has(CONTROLLER_KEY);
    }

    public static boolean isStorageControllerItem(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getPersistentDataContainer().has(CONTROLLER_KEY)) {
            return true;
        }

        if (isLegacy(itemStack)) {
            migrateLegacy(itemStack);
            return true;
        }

        return false;

    }

    public static boolean isLegacyBlock(Inventory inventory) {

        ItemStack spacer = inventory.getItem(0);

        if (spacer == null) {
            return false;
        }

        if (spacer.getItemMeta() == null) {
            return false;
        }

        ItemMeta itemMeta = spacer.getItemMeta();
        if (itemMeta.getLore() == null) {
            return false;
        }

        List<String> lore = itemMeta.getLore();

        if (lore.isEmpty()) {
            return false;
        }

        return lore.get(0).equals(Language.INDICATOR_LORE);
    }

    public static boolean isLegacy(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.getLore() == null) {
            return false;
        }

        List<String> lore = itemMeta.getLore();
        if (!lore.contains(Language.STORAGE_SYSTEM_LORE_TAG)) {
            return false;
        }

        if (!itemMeta.getItemName().equals(Language.STORAGE_CONTROLLER_ITEM)) {
            return false;
        }

        return true;

    }

    public static void migrateLegacy(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(CONTROLLER_KEY, PersistentDataType.BYTE, (byte) 0);
        itemStack.setItemMeta(itemMeta);
    }

}
