package com.emeraldingot.storagesystem.gui.terminal.pages;

import com.emeraldingot.storagesystem.gui.GuiButton;
import com.emeraldingot.storagesystem.gui.StorageSystemHolder;
import com.emeraldingot.storagesystem.gui.terminal.StorageSystemGui;
import com.emeraldingot.storagesystem.impl.DatabaseManager;
import com.emeraldingot.storagesystem.impl.SearchData;
import com.emeraldingot.storagesystem.impl.StorageCellData;
import com.emeraldingot.storagesystem.langauge.Language;
import com.emeraldingot.storagesystem.util.LocaleUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerminalSearchPage extends AbstractTerminalPage {

    private int pageNumber;
    private SearchData searchData;
    private static final Type localeDataType = new TypeToken<Map<String, String>>() {
    }.getType();
    private static final Map<String, Map<String, String>> localeCache = new HashMap<>();
    private static final Gson gson = new Gson();

    public TerminalSearchPage(Player player, StorageCellData storageCellData, int pageNumber, SearchData searchData) {
        super(player, storageCellData);

        this.pageNumber = pageNumber;
        this.searchData = searchData;
    }

    @Override
    public Inventory build() {

        String searchTerm = searchData.query();
        searchTerm = searchTerm.strip();
        boolean byMaterial = searchData.material();
        boolean byItemName = searchData.itemName();

        List<ItemStack> cellItems = DatabaseManager.getInstance().getItemsByCellUUID(storageCellData.getUUID());
        List<ItemStack> filteredList = filterList(cellItems, player.getLocale(), searchTerm, byMaterial, byItemName);


        int pageCount = TerminalItemsPage.getPageCount(filteredList);

        // first page is index 0, add 1 to look correct in the UI
        String title = ChatColor.YELLOW + "Search: \"" + searchTerm + "\" (" + (pageNumber + 1) + "/" + pageCount + ")";
        Inventory inventory = Bukkit.createInventory(new StorageSystemHolder(storageCellData, pageNumber), 54, title);

        ItemStack spacer = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setHideTooltip(true);
        spacer.setItemMeta(spacerMeta);


        for (int slot : TerminalItemsPage.NAVIGATION_BAR) {
            inventory.setItem(slot, spacer);
        }


        ItemStack backArrow = new ItemStack(Material.ARROW);
        ItemMeta backArrowMeta = backArrow.getItemMeta();
        backArrowMeta.setItemName(Language.BACK_BUTTON);
        backArrow.setItemMeta(backArrowMeta);

        GuiButton backButton = new GuiButton(
                backArrow,
                event -> {
                    TerminalItemsPage terminalItemsPage = new TerminalItemsPage(player, storageCellData, pageNumber);
                    player.openInventory(terminalItemsPage.build());
                }
        );
        StorageSystemGui.getButtonItems(player).put(backArrow, backButton);

        inventory.setItem(49, backArrow);


        TerminalItemsPage.populateItems(inventory, filteredList, pageNumber);

        TerminalItemsPage.createNavigationButtons(inventory, player, storageCellData, pageCount, pageNumber, searchData);

        return inventory;
    }


    private static List<ItemStack> filterList(List<ItemStack> itemStacks, String playerLocale, String searchTerm, boolean byMaterial, boolean byItemName) {
        String normalizedTerm = searchTerm.toLowerCase();

        List<ItemStack> filteredList = new ArrayList<>();
        // List is already alphabetized, so further sorting is not needed
        if (!localeCache.containsKey(playerLocale)) {
            try (Reader reader = Files.newBufferedReader(LocaleUtil.getLangFile(playerLocale), StandardCharsets.UTF_8)) {
                Map<String, String> translations = gson.fromJson(reader, localeDataType);
                localeCache.put(playerLocale, translations);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (ItemStack itemStack : itemStacks) {
            String itemName = "";
            if (byItemName) {
                if (itemStack.getItemMeta().hasDisplayName()) {
                    itemName = itemStack.getItemMeta().getDisplayName();
                } else if (itemStack.getItemMeta().hasItemName()) {
                    itemName = itemStack.getItemMeta().getItemName();
                } else {
                    String translationKey = itemStack.getTranslationKey();
                    itemName = localeCache.get(playerLocale).get(translationKey);
                }

                itemName = itemName.toLowerCase();

                if (itemName.contains(normalizedTerm)) {
                    filteredList.add(itemStack);
                    // Even though byMaterial may be set, we don't want the item to get added to the list twice
                    continue;
                }
            }

            if (byMaterial) {
                itemName = itemStack.getType().toString().replace("_", " ");
                itemName = itemName.toLowerCase();
                if (itemName.contains(normalizedTerm)) {
                    filteredList.add(itemStack);
                    continue;
                }
            }

        }

        return filteredList;
    }


}
