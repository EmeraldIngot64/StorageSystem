package com.emeraldingot.storagesystem.util;

import com.emeraldingot.storagesystem.item.StorageCell;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URI;


public class SkullUtil {
    public static ItemStack createPlayerHead(String url) throws MalformedURLException {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        PlayerProfile playerProfile = Bukkit.createPlayerProfile(StorageCell.EMPTY_UUID);
        playerProfile.getTextures().setSkin(URI.create(url).toURL());
        skullMeta.setOwnerProfile(playerProfile);
        itemStack.setItemMeta(skullMeta);


        return itemStack;
    }
}
