package com.emeraldingot.storagesystem;



import com.emeraldingot.storagesystem.command.StorageSystemCommand;
import com.emeraldingot.storagesystem.command.tabcompleter.StorageSystemTabCompleter;
import com.emeraldingot.storagesystem.listener.*;
import com.emeraldingot.storagesystem.impl.DatabaseManager;
import com.emeraldingot.storagesystem.listener.controller.*;
import com.emeraldingot.storagesystem.listener.crafting.CraftItemListener;
import com.emeraldingot.storagesystem.listener.crafting.PrepareCraftItemsListener;
import com.emeraldingot.storagesystem.listener.gui.ItemsGuiClickListener;
import com.emeraldingot.storagesystem.listener.gui.SearchDialogListener;
import com.emeraldingot.storagesystem.listener.gui.TerminalGuiClickListener;
import com.emeraldingot.storagesystem.listener.gui.TerminalGuiCloseListener;
import com.emeraldingot.storagesystem.listener.terminal.TerminalInteractListener;
import com.emeraldingot.storagesystem.recipe.*;
import com.emeraldingot.storagesystem.util.ControllerFileManager;
import org.bukkit.*;
import org.bukkit.entity.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class StorageSystem extends JavaPlugin {
    private static StorageSystem instance;

    public static StorageSystem getInstance() {
        return instance;
    }

    public static ArrayList<Player> playingMusic = new ArrayList<>();

    private DatabaseManager databaseManager;

    public UUID cellUUID;
    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("onEnable is called!");
        ControllerFileManager.getInstance().initData(this);

        // Events
        getServer().getPluginManager().registerEvents(new ItemsGuiClickListener(), this);
        getServer().getPluginManager().registerEvents(new PistonExtendListener(), this);
        getServer().getPluginManager().registerEvents(new StorageItemPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new StorageControllerPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new StorageControllerBreakListener(), this);
        getServer().getPluginManager().registerEvents(new StorageControllerExplodeListener(), this);
        getServer().getPluginManager().registerEvents(new StorageControllerEntityChangeListener(), this);
        getServer().getPluginManager().registerEvents(new StorageControllerInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new CraftItemListener(), this);
        getServer().getPluginManager().registerEvents(new PrepareCraftItemsListener(), this);
        getServer().getPluginManager().registerEvents(new BlockDispenseListener(), this);
        getServer().getPluginManager().registerEvents(new TerminalInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        getServer().getPluginManager().registerEvents(new TerminalGuiClickListener(), this);
        getServer().getPluginManager().registerEvents(new TerminalGuiCloseListener(), this);
        getServer().getPluginManager().registerEvents(new StorageControllerOpenListener(), this);


        getServer().getPluginManager().registerEvents(new SearchDialogListener(), this);

        // Commands
        this.getCommand("storagesystem").setExecutor(new StorageSystemCommand());
        this.getCommand("storagesystem").setTabCompleter(new StorageSystemTabCompleter());

        // Recipes
        try {
            new StorageCoreRecipe().registerRecipe(this);
            new StorageCell1KRecipe().registerRecipe(this);
            new StorageCell4KRecipe().registerRecipe(this);
            new StorageCell16KRecipe().registerRecipe(this);
            new StorageCell64KRecipe().registerRecipe(this);
            new StorageCell256KRecipe().registerRecipe(this);
            new StorageControllerRecipe().registerRecipe(this);
            new StorageTerminalRecipe().registerRecipe(this);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }


//        MoneyFileManager.getInstance().initData(this);

        try {
            // Ensure the plugin's data folder exists
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            // Initialize the DatabaseManager with the path to the database file
            // we make it 'databaseManager =' because we will use it when registering events.
            // The file name will be 'database.db' but you can change that here.
            databaseManager = new DatabaseManager(getDataFolder().getAbsolutePath() + "/database.db");
            databaseManager.update();
        } catch (SQLException e) {
            e.printStackTrace();
            // Disable the plugin if the database connection fails, because we don't want enabled plugin with no functionality.
            Bukkit.getPluginManager().disablePlugin(this);
        }


//        this.getCommand("gamble").setExecutor(new GambleCommand(databaseManager));
//        this.getCommand("cashout").setExecutor(new CashoutCommand());



//        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
//            public void run() {
//            }
//        }, 20, 1);



    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
        try {
            // Close the database connection when the plugin is disabled
            if (databaseManager != null) {
                databaseManager.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}