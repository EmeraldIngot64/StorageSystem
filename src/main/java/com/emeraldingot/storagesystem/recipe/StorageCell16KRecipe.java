package com.emeraldingot.storagesystem.recipe;

import com.emeraldingot.storagesystem.item.StorageCell16K;
import com.emeraldingot.storagesystem.item.StorageCell4K;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class StorageCell16KRecipe implements Recipe {
    public void registerRecipe(Plugin plugin) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, "storage_cell_16k");

        RecipeChoice.ExactChoice storageCellChoice = new RecipeChoice.ExactChoice(new StorageCell4K().getStack());

        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, new StorageCell16K().getStack());

        shapedRecipe.shape("#%#", "*&*", "#%#");

        shapedRecipe.setIngredient('#', Material.EMERALD_BLOCK);
        shapedRecipe.setIngredient('%', Material.GOLD_BLOCK);
        shapedRecipe.setIngredient('*', Material.REDSTONE_BLOCK);
        shapedRecipe.setIngredient('&', Material.PLAYER_HEAD);

        plugin.getServer().addRecipe(shapedRecipe);
    }

    @Override
    public ItemStack getResult() {
        return new StorageCell16K().getStack();
    }
}
