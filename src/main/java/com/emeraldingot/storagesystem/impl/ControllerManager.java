package com.emeraldingot.storagesystem.impl;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.block.StorageControllerBlock;
import com.emeraldingot.storagesystem.item.StorageCell;
import com.emeraldingot.storagesystem.item.StorageCell16K;
import com.emeraldingot.storagesystem.item.StorageCell1K;
import com.emeraldingot.storagesystem.listener.PlayerJoinListener;
import com.emeraldingot.storagesystem.listener.gui.TerminalGuiClickListener;
import com.emeraldingot.storagesystem.util.ControllerFileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.ldap.Control;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ControllerManager {
    private ArrayList<Location> storageControllers = new ArrayList<>();
    private Map<Location, Player> controllersInUse = new HashMap<>();

    public ControllerManager() {
        try {
            this.storageControllers = ControllerFileManager.getInstance().loadControllers();
        }
        catch (Exception e) {
            return;
        }
    }
    private static ControllerManager instance;

    public static ControllerManager getInstance() {
        if (instance == null) {
            instance = new ControllerManager();
        }
        return instance;
    }


    public void addController(Location location) {
        storageControllers.add(location);
        ControllerFileManager.getInstance().writeControllers(ControllerManager.getInstance().getControllerLocations());
    }

    public ArrayList<Location> getControllerLocations() {
        return storageControllers;
    }

    public void removeController(Location location) {
        storageControllers.remove(location);
        ControllerFileManager.getInstance().writeControllers(ControllerManager.getInstance().getControllerLocations());
    }

    public boolean isOnline(Location location) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();
        Inventory inventory =  dispenser.getInventory();

        if (inventory.getItem(4) == null) {
            return false;
        }

        if (!StorageCell.isStorageCell(inventory.getItem(4))) {
            return false;
        }

        return true;

    }

    public UUID getID(Location location) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();

        if (!isOnline(location)) {
            return null;
        }
        ItemStack cell = dispenser.getInventory().getItem(4);

        if (cell == null || cell.getType() == Material.AIR) {
            return null;
        }

        // This should only run if the cell is legacy and needs migrated
        if (!StorageCell.isStorageCell(cell)) {
            return null;
        }

        String uuidString = cell.getItemMeta().getPersistentDataContainer().get(StorageCell.CELL_UUID_KEY, PersistentDataType.STRING);

//        String line = cell.getItemMeta().getLore().get(1);

        UUID uuid = UUID.fromString(uuidString);

        if (uuid.equals(StorageCell.EMPTY_UUID)) {
            return null;
        }
        else {
            return uuid;
        }

    }

    public ItemStack getCell(Location location) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();

        if (!isOnline(location)) {
            return null;
        }
        ItemStack cell = dispenser.getInventory().getItem(4);

        return cell;
    }

    public void updateState(Location location) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();


        if (isOnline(location)) {
            StorageControllerBlock.setOnline(dispenser.getInventory());
            if (getID(location) == null) {
                generateID(location);
            }
        }
        else {
            StorageControllerBlock.setOffline(dispenser.getInventory());
        }

        recalculateUsed(location);
        checkBytesUsed(location);

    }

    private void generateID(Location location) {
        UUID uuid = UUID.randomUUID();
        Dispenser dispenser = (Dispenser) location.getBlock().getState();

        ItemStack cell = dispenser.getInventory().getItem(4);
        ItemMeta itemMeta = cell.getItemMeta();
        List<String> lore = itemMeta.getLore();
        lore.set(1, ChatColor.WHITE + "Cell ID: " + uuid);
        itemMeta.setLore(lore);

        itemMeta.getPersistentDataContainer().set(StorageCell.CELL_UUID_KEY, PersistentDataType.STRING, uuid.toString());

        cell.setItemMeta(itemMeta);
        try {
            DatabaseManager.getInstance().addStorageCell(uuid);
        }
        catch (Exception e) {
            return;
        }


    }

    public Location getNearestController(Location playerLocation) {
        Location closestLocation = playerLocation;
        double closestLocationDistance = Integer.MAX_VALUE;

        for (Location location : storageControllers) {

            if (controllersInUse.keySet().contains(location)) {
                continue;
            }
            // TODO: Use distanceSquared instead
            double distance = location.distance(playerLocation);

            if (distance < closestLocationDistance) {
                closestLocation = location;
                closestLocationDistance = distance;
            }
        }

        if (closestLocationDistance <= 10) {
            return closestLocation;
        }
        else {
            return null;
        }
    }

    public void openController(Location location, Player player) {
        controllersInUse.put(location, player);
    }
    public void closeController(Location location) {
        controllersInUse.remove(location);
    }

    public boolean isInUse(Location location) {
        return controllersInUse.keySet().contains(location);
    }

    public Player getPlayerUsing(Location location) {
        if (!isInUse(location)) {
            return null;
        }

        return controllersInUse.get(location);
    }


    // TODO: refactor this into the database and store the locations, their current cell UUID, and capacity max and current

    // TODO: centralize changing of these fields
    public void changeUsed(Location location, ItemStack itemStack, boolean subtract) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();
        ItemStack cell = dispenser.getInventory().getItem(4);
        ItemMeta itemMeta = cell.getItemMeta();
        List<String> lore = itemMeta.getLore();

        int oldFilledAmount = Integer.parseInt(lore.get(0).split(": ")[1].split("/")[0]);
        String maxAmount = lore.get(0).split(": ")[1].split("/")[1];
        int modifier = subtract ? -1 : 1;
//        System.out.println(getScaledAmount(itemStack) * modifier);
        lore.set(0, ChatColor.WHITE + "Stored: " + (oldFilledAmount + getScaledAmount(itemStack) * modifier) + "/" + maxAmount);
        itemMeta.setLore(lore);
        cell.setItemMeta(itemMeta);
    }

    public boolean canHold(Location location, ItemStack itemStack) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();
        ItemStack cell = dispenser.getInventory().getItem(4);
        ItemMeta itemMeta = cell.getItemMeta();
        List<String> lore = itemMeta.getLore();


        int oldFilledAmount = Integer.parseInt(lore.get(0).split(": ")[1].split("/")[0]);
        int maxAmount = Integer.parseInt(lore.get(0).split(": ")[1].split("/")[1].split(" bytes")[0]);
        if ((oldFilledAmount + getScaledAmount(itemStack)) > maxAmount) {
            return false;
        }
        else {
            return true;
        }
    }

    public void recalculateUsed(Location location) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();
        ItemStack cell = dispenser.getInventory().getItem(4);

        if (cell == null) {
            return;
        }

        int bytesUsed = StorageCell.getBytesUsed(cell);

        ItemMeta itemMeta = cell.getItemMeta();
        List<String> lore = itemMeta.getLore();

        int maxCapacity = StorageCell.getCapacity(cell);
        lore.set(0, ChatColor.WHITE + "Stored: " + (bytesUsed) + "/" + maxCapacity);
        itemMeta.setLore(lore);
        cell.setItemMeta(itemMeta);
    }

    public void checkBytesUsed(Location location) {
        Dispenser dispenser = (Dispenser) location.getBlock().getState();
        ItemStack cell = dispenser.getInventory().getItem(4);

        if (cell == null) {
            return;
        }

        UUID uuid = StorageCell.getUuid(cell);

        int storedBytesUsed = StorageCell.getBytesUsed(cell);

        int realBytesUsed = 0;
        for (ItemStack itemStack : DatabaseManager.getInstance().getItemsByCellUUID(uuid)) {
            realBytesUsed += TerminalGuiClickListener.getScaledByteValue(itemStack);
        }

        if (realBytesUsed != storedBytesUsed) {
//            System.out.println("something awful has happened!");
        }
        else {
//            System.out.println("everything working as expected");
        }
    }



    public int getScaledAmount(ItemStack itemStack) {
        return (64 / itemStack.getMaxStackSize()) * itemStack.getAmount();
    }








}
