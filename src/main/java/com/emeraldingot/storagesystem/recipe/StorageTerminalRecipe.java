package com.emeraldingot.storagesystem.recipe;

import com.emeraldingot.storagesystem.item.StorageTerminal;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.net.MalformedURLException;

public class StorageTerminalRecipe {
    public void registerRecipe(Plugin plugin) throws MalformedURLException {

        NamespacedKey namespacedKey = new NamespacedKey(plugin, "storage_terminal");
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, StorageTerminal.getStack());
        shapedRecipe.shape("#$#", "$%$", "^#^");

        shapedRecipe.setIngredient('#', Material.GLASS);
        shapedRecipe.setIngredient('$', Material.REDSTONE);
        shapedRecipe.setIngredient('%', Material.ENDER_PEARL);
        shapedRecipe.setIngredient('^', Material.IRON_INGOT);

        plugin.getServer().addRecipe(shapedRecipe);
    }

}
