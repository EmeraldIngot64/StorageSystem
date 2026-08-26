package com.emeraldingot.storagesystem.item;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.langauge.Language;
import com.emeraldingot.storagesystem.util.SkullUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StorageCore {

    private static final NamespacedKey STORAGE_CORE_KEY = new NamespacedKey(StorageSystem.getInstance(), "storage_core");

    public static ItemStack getStack() {
        try {
            ItemStack storageCore = SkullUtil.createPlayerHead("http://textures.minecraft.net/texture/3db9e7f6ab3a8c7686cf9d8ac244dd47890ca79494e9ce3951824f03deb87b3d");
            ItemMeta itemMeta = storageCore.getItemMeta();
            itemMeta.setItemName(ChatColor.YELLOW + "Storage Core");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("Infinitely dense cubical structure");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "StorageSystem");
            itemMeta.setLore(lore);
            itemMeta.getPersistentDataContainer().set(STORAGE_CORE_KEY, PersistentDataType.BYTE, (byte) 0);

            storageCore.setItemMeta(itemMeta);
            return storageCore;
        } catch (Exception e) {
            return new ItemStack(Material.PAPER);
        }

    }

    public static boolean isStorageCore(ItemStack itemStack) {
        ItemStack storageCore = getStack();

        if (itemStack.getType() != storageCore.getType()) {
            return false;
        }


        if (itemStack.getItemMeta().getPersistentDataContainer().has(STORAGE_CORE_KEY)) {
            return true;
        }

        if (isLegacy(itemStack)) {
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

        if (!itemMeta.getItemName().equals(ChatColor.YELLOW + "Storage Core")) {
            return false;
        }

        return true;

    }

    public static void migrateLegacy(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(STORAGE_CORE_KEY, PersistentDataType.BYTE, (byte) 0);
        itemStack.setItemMeta(itemMeta);
    }


}
