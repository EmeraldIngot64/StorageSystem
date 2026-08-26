package com.emeraldingot.storagesystem.recipe;

import com.emeraldingot.storagesystem.item.StorageCell1K;
import com.emeraldingot.storagesystem.item.StorageCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class StorageCell1KRecipe implements Recipe {
    public void registerRecipe(Plugin plugin) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, "storage_cell_1k");

        RecipeChoice.ExactChoice storageCoreChoice = new RecipeChoice.ExactChoice(StorageCore.getStack());

        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, new StorageCell1K().getStack());

        shapedRecipe.shape("#%#", "*&*", "#%#");

        shapedRecipe.setIngredient('#', Material.IRON_BLOCK);
        shapedRecipe.setIngredient('%', Material.GOLD_INGOT);
        shapedRecipe.setIngredient('*', Material.REDSTONE);
        shapedRecipe.setIngredient('&', Material.PLAYER_HEAD);

        plugin.getServer().addRecipe(shapedRecipe);
    }

    @Override
    public ItemStack getResult() {
        return new StorageCell1K().getStack();
    }
}
