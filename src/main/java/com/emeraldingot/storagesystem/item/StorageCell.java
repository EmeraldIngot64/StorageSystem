package com.emeraldingot.storagesystem.item;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.impl.StorageCellType;
import com.emeraldingot.storagesystem.langauge.Language;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public abstract class StorageCell {
    public static final NamespacedKey CELL_UUID_KEY = new NamespacedKey(StorageSystem.getInstance(), "cell_uuid");
    public static final NamespacedKey CELL_TYPE_KEY = new NamespacedKey(StorageSystem.getInstance(), "cell_type");
    public static final NamespacedKey BYTES_USED_KEY = new NamespacedKey(StorageSystem.getInstance(), "bytes_used");
    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static boolean isStorageCell(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getItemMeta() == null) {
            return false;
        }

        if (itemStack == null) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta.getPersistentDataContainer().has(CELL_UUID_KEY)) {
            return true;
        }

        if (isLegacy(itemStack)) {
            migrateLegacy(itemStack);
            return true;
        }

        return false;

    }

    public static boolean isLegacy(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta.getLore() == null) {
            return false;
        }

        List<String> lore = itemMeta.getLore();
        if (!lore.contains(Language.STORAGE_SYSTEM_LORE_TAG)) {
            return false;
        }

        if (!itemMeta.getItemName().endsWith("Storage Cell")) {
            return false;
        }

        return true;

    }

    public static void migrateLegacy(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        try {
            List<String> lore = itemMeta.getLore();
            String uuidString = lore.get(1).split("§fCell ID: ")[1];


            UUID cellUUID;
            if (uuidString.equals("unset")) {
                cellUUID = EMPTY_UUID;
            } else {
                cellUUID = UUID.fromString(uuidString);
            }


            String[] cellDataString = lore.get(0).split("Stored: ")[1].split("/");

            int bytesUsed = Integer.parseInt(cellDataString[0]);
            int capacity = Integer.parseInt(cellDataString[1].split(" ")[0]);

            StorageCellType storageCellType;

            switch (capacity) {
                case 1024 -> storageCellType = StorageCellType.CELL_1K;
                case 4096 -> storageCellType = StorageCellType.CELL_4K;
                case 16384 -> storageCellType = StorageCellType.CELL_16K;
                case 65536 -> storageCellType = StorageCellType.CELL_64K;
                case 262144 -> storageCellType = StorageCellType.CELL_256K;
                default -> storageCellType = null;
            }

            if (storageCellType == null) {
                throw new RuntimeException("Failed to determine Storage Cell Type");
            }

            itemMeta.getPersistentDataContainer().set(CELL_UUID_KEY, PersistentDataType.STRING, cellUUID.toString());
            itemMeta.getPersistentDataContainer().set(CELL_TYPE_KEY, PersistentDataType.STRING, storageCellType.toString());
            itemMeta.getPersistentDataContainer().set(BYTES_USED_KEY, PersistentDataType.INTEGER, bytesUsed);

            itemMeta.setMaxStackSize(1);

            itemStack.setItemMeta(itemMeta);
        } catch (RuntimeException e) {
            StorageSystem.getInstance().getLogger().log(Level.WARNING, "Failed to migrate cell data! ItemStack of: " + itemStack);
            throw new RuntimeException(e);
        }


    }


    public static int getBytesUsed(ItemStack itemStack) {
        int bytesUsed = itemStack.getItemMeta().getPersistentDataContainer().get(BYTES_USED_KEY, PersistentDataType.INTEGER);
        return bytesUsed;
    }


    public ItemStack getStack() {
        return getStack(0, null);
    }

    ;

    public ItemStack getStack(int filledBytes, UUID uuid) {
        return null;
    }

    public int getCapacity() {
        return 0;
    }

    // Assumes that itemStack is a StorageCell
    public static UUID getUuid(ItemStack itemStack) {

        return UUID.fromString(itemStack.getItemMeta().getPersistentDataContainer().get(CELL_UUID_KEY, PersistentDataType.STRING));
    }

    public static int getCapacity(ItemStack itemStack) {
        StorageCellType storageCellType = StorageCellType.valueOf(itemStack.getItemMeta().getPersistentDataContainer().get(CELL_TYPE_KEY, PersistentDataType.STRING));
        return storageCellType.getCell().getCapacity();
    }
}
