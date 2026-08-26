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


public class StorageCell1K extends StorageCell {


    private static final int SIZE = 1 * 1024;

    @Override
    public ItemStack getStack(int filledBytes, UUID uuid) {
        try {
            ItemStack storageCore = SkullUtil.createPlayerHead("http://textures.minecraft.net/texture/5c718e958e31e7fa62de6bd616d1f7eacd4a3ffeedfa27307dde372e3200fb17");
            ItemMeta itemMeta = storageCore.getItemMeta();
            itemMeta.setItemName(ChatColor.YELLOW + "1k Storage Cell");
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
            itemMeta.getPersistentDataContainer().set(StorageCell.CELL_TYPE_KEY, PersistentDataType.STRING, StorageCellType.CELL_1K.toString());
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
