package com.emeraldingot.storagesystem.impl;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.util.Base64Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Connection connection;

    public static DatabaseManager getInstance() {
        return instance;
    }

    public DatabaseManager(String path) throws SQLException {
        instance = this;
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS storage_cells (" +
                    "cell_id TEXT UNIQUE, " +
                    "PRIMARY KEY(cell_id));");
        }
        try (Statement statement = connection.createStatement()) {
//            statement.execute("CREATE TABLE IF NOT EXISTS items (" +
//                    "cell_id TEXT NOT NULL, " +
//                    "item_data TEXT NOT NULL, " +
//                    "PRIMARY KEY(cell_id));");
            String createItemsTable = "CREATE TABLE IF NOT EXISTS items (" +
                    "item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cell_id TEXT, " +
                    "material TEXT, " +
                    "item_meta TEXT, " +
                    "count INTEGER, " +
                    "FOREIGN KEY (cell_id) REFERENCES storage_cells(cell_id) ON DELETE CASCADE" +
                    ");";

            statement.execute(createItemsTable);

        }

        try (Statement statement = connection.createStatement()) {
            String createMetadataTable = "CREATE TABLE IF NOT EXISTS metadata (" +
                    "data_version INTEGER PRIMARY KEY);";

            statement.execute(createMetadataTable);

        }
    }

    public void addStorageCell(UUID uuid) throws SQLException {
        if (!uuidExists(uuid)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO storage_cells (cell_id) VALUES (?)")) {
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.executeUpdate();
            }
        }
    }

    public void addItemsToCell(UUID uuid, ItemStack itemStack) {
        String material = itemStack.getType().toString();  // Get the material type
        String itemMeta = Base64Util.toBase64(itemStack.getItemMeta());  // Get serialized item metadata
        int amountToAdd = itemStack.getAmount();  // Get the amount of items to add

        String selectQuery = "SELECT count FROM items WHERE cell_id = ? AND material = ? AND item_meta = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, uuid.toString());
            selectStmt.setString(2, material);
            selectStmt.setString(3, itemMeta);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    // Item exists, update the count
                    int currentCount = rs.getInt("count");
                    int newCount = currentCount + amountToAdd;

                    // Update the count for the existing item
                    String updateQuery = "UPDATE items SET count = ? WHERE cell_id = ? AND material = ? AND item_meta = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, newCount);  // Set the new count
                        updateStmt.setString(2, uuid.toString());
                        updateStmt.setString(3, material);
                        updateStmt.setString(4, itemMeta);

                        updateStmt.executeUpdate();
//                        System.out.println(amountToAdd + " items added.");
                    }
                } else {
                    // Item does not exist, insert it into the database
                    String insertQuery = "INSERT INTO items (cell_id, material, item_meta, count) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, uuid.toString());
                        insertStmt.setString(2, material);
                        insertStmt.setString(3, itemMeta);
                        insertStmt.setInt(4, amountToAdd);

                        insertStmt.executeUpdate();
//                        System.out.println(amountToAdd + " new items added.");
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeItemsFromCell(UUID uuid, ItemStack itemStack) {
        String material = itemStack.getType().toString();  // Get material (e.g., "OAK_LOG")

        String itemMeta = Base64Util.toBase64(itemStack.getItemMeta());  // Get serialized metadata (can be empty or null)
        int amountToRemove = itemStack.getAmount();  // Get the amount to remove

        String selectQuery = "SELECT count FROM items WHERE cell_id = ? AND material = ? AND item_meta = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, uuid.toString());
            selectStmt.setString(2, material);
            selectStmt.setString(3, itemMeta);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int currentCount = rs.getInt("count");  // Get current count in the database

                    // Step 2: Update the count or delete the item
                    if (currentCount > amountToRemove) {
                        // Item exists and we just need to decrease the count
                        String updateQuery = "UPDATE items SET count = count - ? WHERE cell_id = ? AND material = ? AND item_meta = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, amountToRemove);  // Decrease by the amount to remove
                            updateStmt.setString(2, uuid.toString());
                            updateStmt.setString(3, material);
                            updateStmt.setString(4, itemMeta);

                            updateStmt.executeUpdate();
//                            System.out.println(amountToRemove + " items removed.");
                        }
                    } else if (currentCount == amountToRemove) {
                        // Remove the item completely if the count reaches 0
                        String deleteQuery = "DELETE FROM items WHERE cell_id = ? AND material = ? AND item_meta = ?";
                        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                            deleteStmt.setString(1, uuid.toString());
                            deleteStmt.setString(2, material);
                            deleteStmt.setString(3, itemMeta);

                            deleteStmt.executeUpdate();
//                            System.out.println("Item completely removed.");
                        }
                    } else {
                        // Error: Trying to remove more than what exists in the database
//                        System.out.println("Error: Trying to remove more items than are available.");
                    }
                } else {
                    // this should never happen
//                    System.out.println("Item not found in the database.");
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ItemStack> getItemsByCellUUID(UUID cellUUID) {
        List<ItemStack> itemList = new ArrayList<>();

        String query = "SELECT material, item_meta, count FROM items WHERE cell_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the cell UUID as parameter
            preparedStatement.setString(1, cellUUID.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    Material material = Material.matchMaterial(resultSet.getString("material"));

                    ItemMeta itemMeta = (ItemMeta) Base64Util.fromBase64(resultSet.getString("item_meta"));
                    int count = resultSet.getInt("count");

                    ItemStack itemStack = new ItemStack(material, count);
                    itemStack.setItemMeta(itemMeta);

                    itemList.add(itemStack);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to get data for cell!");
        }

        itemList.sort(Comparator.comparing(ItemStack::getType));
        return itemList;
    }

    public int getVersion() {
        try {
            ResultSet versionResultSet = connection.createStatement().executeQuery("SELECT data_version FROM metadata LIMIT 1;");
            if (versionResultSet.next()) {
                return versionResultSet.getInt("data_version");
            }
            else {
                return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateVersion() {
        // For the time being there's only one row
        // If that changes, so will this code
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM metadata");
            statement.execute("INSERT INTO metadata (data_version) VALUES (" + StorageSystem.getInstance().getConfig().getInt("data-version") + ")");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {

        int version = getVersion();
        int latestVersion = StorageSystem.getInstance().getConfig().getInt("data-version");
        if (version == latestVersion) {
            return;
        }
        StorageSystem.getInstance().getLogger().info("Database is outdated, updating! This may take a while");


        String query = "SELECT item_id, material, item_meta, count FROM items";
        String updateQuery = "UPDATE items SET material = ?, item_meta = ?, count = ? WHERE item_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            PreparedStatement preparedUpdateStatement = connection.prepareStatement(updateQuery);
            int batchCount = 0;

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    String materialName = resultSet.getString("material");
                    Material material = Material.matchMaterial(materialName);
                    if (material == null) {
                        if (materialName.equals("CHAIN")) {
                            material = Material.IRON_CHAIN;
                        }
                        else {
                            StorageSystem.getInstance().getLogger().warning(String.format("Updater: ItemStack of %s failed to decode!", materialName));
                            continue;
                        }
                    }

                    ItemMeta itemMeta = (ItemMeta) Base64Util.fromBase64(resultSet.getString("item_meta"));
                    String newItemMeta = Base64Util.toBase64(itemMeta);

                    int count = resultSet.getInt("count");

                    preparedUpdateStatement.setString(1, material.toString());
                    preparedUpdateStatement.setString(2, newItemMeta);
                    preparedUpdateStatement.setInt(3, count);
                    preparedUpdateStatement.setLong(4, resultSet.getLong("item_id"));

                    preparedUpdateStatement.addBatch();
                    batchCount++;

                    if (batchCount % 500 == 0) {
                        preparedUpdateStatement.executeBatch();
                    }

                }

                preparedUpdateStatement.executeBatch();
                updateVersion();
            } catch (Exception e) {
                StorageSystem.getInstance().getLogger().warning("Failed to update database! There will be problems!");
                throw new RuntimeException(e);
            }
        }
        catch (Exception e) {
            StorageSystem.getInstance().getLogger().warning("Failed to update database! There will be problems!");
            throw new RuntimeException(e);
        }
    }

    public int getCount(UUID cellId, ItemStack itemStack) {
        String query = "SELECT count FROM items WHERE cell_id = ? AND material = ? AND item_meta = ?";
        int count = 0;  // Default to 0 if no items are found

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, cellId.toString());       // Set the cell_id parameter
            preparedStatement.setString(2, itemStack.getType().toString());     // Set the material parameter
            preparedStatement.setString(3, Base64Util.toBase64(itemStack.getItemMeta()));     // Set the item_meta parameter (can be null)

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("count");  // Get the count from the result set
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Handle exceptions appropriately (e.g., logging)
        }

        return count;
    }



    public boolean uuidExists(UUID uuid) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM storage_cells WHERE cell_id = ?")) {
            preparedStatement.setString(1, uuid.toString());
            return preparedStatement.executeQuery().next();
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
