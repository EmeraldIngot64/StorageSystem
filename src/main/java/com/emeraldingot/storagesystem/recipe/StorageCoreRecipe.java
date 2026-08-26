package com.emeraldingot.storagesystem.recipe;

import com.emeraldingot.storagesystem.item.StorageCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.net.MalformedURLException;

public class StorageCoreRecipe {
    public void registerRecipe(Plugin plugin) throws MalformedURLException {

        NamespacedKey namespacedKey = new NamespacedKey(plugin, "storage_core");
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, StorageCore.getStack());
        shapedRecipe.shape("#%#", "*&*", "#%#");

        shapedRecipe.setIngredient('#', Material.GOLD_INGOT);
        shapedRecipe.setIngredient('%', Material.QUARTZ_BLOCK);
        shapedRecipe.setIngredient('*', Material.DIAMOND);
        shapedRecipe.setIngredient('&', Material.REDSTONE);

        plugin.getServer().addRecipe(shapedRecipe);
    }

}
