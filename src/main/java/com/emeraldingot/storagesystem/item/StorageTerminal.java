package com.emeraldingot.storagesystem.item;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.langauge.Language;
import com.emeraldingot.storagesystem.util.SkullUtil;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StorageTerminal {
    private static final NamespacedKey TERMINAL_KEY = new NamespacedKey(StorageSystem.getInstance(), "storage_terminal");

    private static final NamespacedKey PAIRED_CONTROLLER_BLOCK_KEY = new NamespacedKey(StorageSystem.getInstance(), "paired_controller");
    private static final NamespacedKey PAIRED_CONTROLLER_WORLD_KEY = new NamespacedKey(StorageSystem.getInstance(), "paired_controller_world");

    public static final int TERMINAL_RANGE = 10;

    public static ItemStack getStack() {
        try {
            ItemStack storageTerminal = SkullUtil.createPlayerHead("http://textures.minecraft.net/texture/e5c0358fb64eaac6db69a857f8987b5197fcea72609c060a8628da92266fd592");
            ItemMeta itemMeta = storageTerminal.getItemMeta();
            itemMeta.setItemName(ChatColor.YELLOW + "Storage Terminal");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("Wireless item access");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "StorageSystem");
            itemMeta.setLore(lore);

            itemMeta.getPersistentDataContainer().set(TERMINAL_KEY, PersistentDataType.BYTE, (byte) 0);

            storageTerminal.setItemMeta(itemMeta);
            return storageTerminal;
        } catch (Exception e) {
            return new ItemStack(Material.PAPER);
        }

    }

    public static boolean isStorageTerminal(ItemStack itemStack) {
        ItemStack storageTerminal = getStack();
        if (itemStack.getType() != storageTerminal.getType()) {
            return false;
        }

        if (itemStack.getItemMeta() == null) {
            return false;
        }

        // if it has the key, then it's not legacy
        // otherwise check it
        if (itemStack.getItemMeta().getPersistentDataContainer().has(TERMINAL_KEY)) {
            return true;
        }

        if (isLegacy(itemStack)) {
            // Migrate legacy item
            migrateLegacy(itemStack);
            return true;
        }

        return false;


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

        if (!itemMeta.getItemName().equals(Language.STORAGE_TERMINAL_ITEM)) {
            return false;
        }

        return true;

    }

    public static void migrateLegacy(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(TERMINAL_KEY, PersistentDataType.BYTE, (byte) 0);
        itemStack.setItemMeta(itemMeta);
    }

    public static boolean isPaired(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return false;
        }
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        return persistentDataContainer.has(PAIRED_CONTROLLER_WORLD_KEY) && persistentDataContainer.has(PAIRED_CONTROLLER_BLOCK_KEY);
    }

    // Assumes isPaired is already checked
    public static Location getPairedLocation(ItemStack itemStack) {
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();

        World world = Bukkit.getWorld(persistentDataContainer.get(StorageTerminal.PAIRED_CONTROLLER_WORLD_KEY, PersistentDataType.STRING));
        int[] blockLocation = persistentDataContainer.get(StorageTerminal.PAIRED_CONTROLLER_BLOCK_KEY, PersistentDataType.INTEGER_ARRAY);

        Location location = new Location(world, blockLocation[0], blockLocation[1], blockLocation[2]);

        return location;
    }

    // Assumes the item is already known to be a storage terminal
    public static void unpair(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

        persistentDataContainer.remove(PAIRED_CONTROLLER_WORLD_KEY);
        persistentDataContainer.remove(PAIRED_CONTROLLER_BLOCK_KEY);
        itemStack.setItemMeta(itemMeta);


    }

    // Assumes the item is already known to be a storage terminal
    public static void setPairedLocation(ItemStack itemStack, Location location) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

        int[] blockCoordinates = {location.getBlockX(), location.getBlockY(), location.getBlockZ()};

        String world = location.getWorld().getName();

        persistentDataContainer.set(PAIRED_CONTROLLER_WORLD_KEY, PersistentDataType.STRING, world);
        persistentDataContainer.set(PAIRED_CONTROLLER_BLOCK_KEY, PersistentDataType.INTEGER_ARRAY, blockCoordinates);

        itemStack.setItemMeta(itemMeta);

    }
}
