package com.emeraldingot.storagesystem.recipe;

import com.emeraldingot.storagesystem.item.StorageCell256K;
import com.emeraldingot.storagesystem.item.StorageCell64K;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class StorageCell256KRecipe implements Recipe {
    public void registerRecipe(Plugin plugin) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, "storage_cell_256k");

        RecipeChoice.ExactChoice storageCellChoice = new RecipeChoice.ExactChoice(new StorageCell64K().getStack());

        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, new StorageCell256K().getStack());

        shapedRecipe.shape("#%#", "*&*", "#%#");

        shapedRecipe.setIngredient('#', Material.DRAGON_BREATH);
        shapedRecipe.setIngredient('%', Material.SHULKER_SHELL);
        shapedRecipe.setIngredient('*', Material.NETHERITE_BLOCK);
        shapedRecipe.setIngredient('&', Material.PLAYER_HEAD);

        plugin.getServer().addRecipe(shapedRecipe);
    }

    @Override
    public ItemStack getResult() {
        return new StorageCell256K().getStack();
    }
}
