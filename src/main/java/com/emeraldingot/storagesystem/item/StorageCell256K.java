package com.emeraldingot.storagesystem.item;

import com.emeraldingot.storagesystem.impl.StorageCellType;
import com.emeraldingot.storagesystem.util.SkullUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

public class StorageCell256K extends StorageCell {

    private static final int SIZE = 256 * 1024;

    @Override
    public ItemStack getStack(int filledBytes, UUID uuid) {
        try {
            ItemStack storageCore = SkullUtil.createPlayerHead("http://textures.minecraft.net/texture/c4bb880535638d45f16f7fc657452d14bcb9f3f0c598d53876355df8919d0898");
            ItemMeta itemMeta = storageCore.getItemMeta();
            itemMeta.setItemName(ChatColor.YELLOW + "256k Storage Cell");
            itemMeta.setMaxStackSize(1);

            ArrayList<String> lore = new ArrayList<>();

            lore.add(ChatColor.WHITE + "Stored: " + filledBytes + "/" + SIZE + " bytes");

            if (uuid.equals(StorageCell.EMPTY_UUID)) {
                lore.add(ChatColor.WHITE + "Cell ID: unset");
            } else {
                lore.add(ChatColor.WHITE + "Cell ID: " + uuid.toString());
            }

            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "StorageSystem");
            itemMeta.setLore(lore);

            itemMeta.getPersistentDataContainer().set(StorageCell.CELL_UUID_KEY, PersistentDataType.STRING, uuid.toString());
            itemMeta.getPersistentDataContainer().set(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING, StorageCellType.CELL_256K.toString());
            itemMeta.getPersistentDataContainer().set(StorageCell.BYTES_USED_KEY, PersistentDataType.INTEGER, 0);

            storageCore.setItemMeta(itemMeta);
            return storageCore;
        } catch (Exception e) {
            return new ItemStack(Material.PAPER);
        }

    }

    @Override
    public ItemStack getStack() {
        return getStack(0, StorageCell.EMPTY_UUID);
    }

    @Override
    public int getCapacity() {
        return SIZE;
    }

}
