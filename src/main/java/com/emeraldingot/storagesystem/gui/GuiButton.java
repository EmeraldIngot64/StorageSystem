package com.emeraldingot.storagesystem.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiButton {
    private ItemStack itemStack;
    private final Consumer<InventoryClickEvent> onClick;


    public GuiButton(ItemStack itemStack, Consumer<InventoryClickEvent> onClick) {
        this.itemStack = itemStack;
        this.onClick = onClick;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void click(InventoryClickEvent event) {
        onClick.accept(event);
    }


}
