package com.emeraldingot.storagesystem.gui.terminal.pages;

import com.emeraldingot.storagesystem.gui.GuiButton;
import com.emeraldingot.storagesystem.gui.StorageSystemHolder;
import com.emeraldingot.storagesystem.gui.terminal.StorageSystemGui;
import com.emeraldingot.storagesystem.gui.util.GuiUtil;
import com.emeraldingot.storagesystem.impl.DatabaseManager;
import com.emeraldingot.storagesystem.impl.StorageCellData;
import com.emeraldingot.storagesystem.langauge.Language;
import com.emeraldingot.storagesystem.util.SkullUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TerminalItemsPage extends AbstractTerminalPage {

    public static final int[] NAVIGATION_BAR = {45, 46, 47, 48, 49, 50, 51, 52, 53};
    public static final int FINAL_PAGE_SLOT = 44;

    private static final String PREVIOUS_PAGE_TEXTURE = "http://textures.minecraft.net/texture/528b8cf405eaf606a0210f0303b013179f8f12eaa95824129ebeef9e44b68230";
    private static final String FIRST_PAGE_TEXTURE = "http://textures.minecraft.net/texture/902ac2617482f61bad194f7bcb8b993431040eb40bd1b663ad41e98f3b324d26";
    private static final String NEXT_PAGE_TEXTURE = "http://textures.minecraft.net/texture/5dcda6e3c6dca7e9b8b6ba3febf5cd0917f997b64b2aef18c3f773765e3a579";
    private static final String LAST_PAGE_TEXTURE = "http://textures.minecraft.net/texture/6befd502ee9cce11fcf8c5713fc02b49787bd9ab8b10ed129043f6ca8f4e207d";

    // First item slot is 0, e.g. 0 to 44 is 45 stacks
    private static final int STACKS_PER_PAGE = FINAL_PAGE_SLOT + 1;

    int pageNumber;

    public TerminalItemsPage(Player player, StorageCellData storageCellData, int pageNumber) {
        super(player, storageCellData);

        this.pageNumber = pageNumber;
    }

    @Override
    public Inventory build() {
//        Inventory inventory = Bukkit.createInventory(new StorageSystemHolder(), 54, Language.STORAGE_SYSTEM_TITLE);
        List<ItemStack> cellItems = DatabaseManager.getInstance().getItemsByCellUUID(storageCellData.getUUID());
        int pageCount = getPageCount(cellItems);

        // first page is index 0, add 1 to look correct in the UI
        String title = Language.STORAGE_SYSTEM_TITLE + " (" + (pageNumber + 1) + "/" + pageCount + ")";
        Inventory inventory = Bukkit.createInventory(new StorageSystemHolder(storageCellData, pageNumber), 54, title);

        ItemStack spacer = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setHideTooltip(true);
        spacer.setItemMeta(spacerMeta);


        for (int slot : NAVIGATION_BAR) {
            inventory.setItem(slot, spacer);
        }

        ItemStack searchSign = new ItemStack(Material.SPRUCE_SIGN);
        ItemMeta signMeta = searchSign.getItemMeta();
        signMeta.setItemName(Language.SEARCH_ITEMS);
        searchSign.setItemMeta(signMeta);

        GuiButton searchButton = new GuiButton(
                searchSign,
                event -> {
                    GuiUtil.openSearchDialog(player, searchTerm -> {
                        player.openInventory(new TerminalSearchPage(player, storageCellData, 0, searchTerm).build());
                    });
                }
        );
        StorageSystemGui.getButtonItems(player).put(searchSign, searchButton);

        inventory.setItem(49, searchSign);

        populateItems(inventory, cellItems, pageNumber);

        createNavigationButtons(inventory, player, storageCellData, pageCount, pageNumber, null);





        return inventory;
    }

    public static int getPageCount(List<ItemStack> itemStacks) {

        int totalStackCount = 0;

        for (ItemStack itemStack : itemStacks) {

            int amount = itemStack.getAmount();
            int stackCount = (int) Math.ceil(amount / (double)itemStack.getMaxStackSize());

            totalStackCount += stackCount;

        }

        int pageCount = (int) Math.ceil((double)totalStackCount / STACKS_PER_PAGE);

        pageCount = Math.max(pageCount, 1);

        return pageCount;
    }

    public static List<ItemStack> splitToStacks(List<ItemStack> itemStacks) {
        List<ItemStack> splitStacks = new ArrayList<>();

        for (ItemStack itemStack : itemStacks) {
            int maxStackSize = itemStack.getMaxStackSize();

            if (itemStack.getAmount() <= maxStackSize) {
                splitStacks.add(itemStack);
                continue;
            }

            int amount = itemStack.getAmount();

            while (amount > 0) {
                int stackAmount = Math.min(maxStackSize, amount);
                ItemStack splitStack = itemStack.clone();
                splitStack.setAmount(stackAmount);
                splitStacks.add(splitStack);
                amount -= stackAmount;
            }

        }

        return splitStacks;
    }


    public static void populateItems(Inventory inventory, List<ItemStack> itemStacks, int pageNumber) {

        List<ItemStack> items = splitToStacks(itemStacks);

        // subtract one because first page should start at index 0
        // since last page index is 44, 1 * 45 is onto the next page
        int startIndex = pageNumber * STACKS_PER_PAGE;
        // either the rest of the list or the start + a whole new page
        int endIndex = Math.min(items.size(), startIndex + STACKS_PER_PAGE);



        for (int i = startIndex; i < endIndex; i++) {
            // i will be the absolute position in the list
            // this is done so that it sets items at the top of the inventory
            int distanceFromStart = i - startIndex;
            inventory.setItem(distanceFromStart, items.get(i));
        }

    }

    public static void createPageNavButton(Inventory inventory, Player player, StorageCellData storageCellData, int pageNumber, String url, String name, String searchTerm, int slot) {
        ItemStack navigationItem;
        try {
            navigationItem = SkullUtil.createPlayerHead(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        ItemMeta itemMeta = navigationItem.getItemMeta();
        itemMeta.setDisplayName(name);

        navigationItem.setItemMeta(itemMeta);
        GuiButton navigationButton;

        if (searchTerm == null) {
            navigationButton = new GuiButton(
                    navigationItem,
                    event -> {
                        TerminalItemsPage terminalItemsPage = new TerminalItemsPage(player, storageCellData, pageNumber);
                        player.openInventory(terminalItemsPage.build());
                    }
            );
        }
        else {
            navigationButton = new GuiButton(
                    navigationItem,
                    event -> {
                        TerminalSearchPage terminalSearchPage = new TerminalSearchPage(player, storageCellData, pageNumber, searchTerm);
                        player.openInventory(terminalSearchPage.build());
                    }
            );
        }

        StorageSystemGui.getButtonItems(player).put(navigationItem, navigationButton);

        inventory.setItem(slot, navigationItem);
    }

    public static void createNavigationButtons(Inventory inventory, Player player, StorageCellData storageCellData, int pageCount, int pageNumber, String searchTerm) {

        // Button rules:
        // - Should only show first page button when there is more than 1 page before the current
        // - Should only show last page button when there is more than 1 page after the current
        // - Should only show next page button if there is at least 1 page after the current
        // - Should only show previous page button if there is at least 1 page before the current


        if (pageCount > 1) {

            // these could point to invalid indexes
            int firstPage = 0;
            int previousPage = pageNumber - 1;
            int nextPage = pageNumber + 1;
            int lastPage = pageCount - 1;

            if (pageNumber == lastPage) {
                if (previousPage != firstPage) {
                    createPageNavButton(inventory, player, storageCellData, firstPage, FIRST_PAGE_TEXTURE, Language.FIRST_PAGE, searchTerm, 45);
                }
                createPageNavButton(inventory, player, storageCellData, previousPage, PREVIOUS_PAGE_TEXTURE, Language.PREVIOUS_PAGE, searchTerm, 46);
            }
            else if (pageNumber == firstPage) {
                createPageNavButton(inventory, player, storageCellData, nextPage, NEXT_PAGE_TEXTURE, Language.NEXT_PAGE, searchTerm, 52);
                if (nextPage != lastPage) {
                    createPageNavButton(inventory, player, storageCellData, lastPage, LAST_PAGE_TEXTURE, Language.LAST_PAGE, searchTerm, 53);
                }
            }
            // must be in middle
            else {
                if (previousPage != firstPage) {
                    createPageNavButton(inventory, player, storageCellData, firstPage, FIRST_PAGE_TEXTURE, Language.FIRST_PAGE, searchTerm, 45);
                }
                createPageNavButton(inventory, player, storageCellData, previousPage, PREVIOUS_PAGE_TEXTURE, Language.PREVIOUS_PAGE, searchTerm, 46);
                createPageNavButton(inventory, player, storageCellData, nextPage, NEXT_PAGE_TEXTURE, Language.NEXT_PAGE, searchTerm, 52);
                if (nextPage != lastPage) {
                    createPageNavButton(inventory, player, storageCellData, lastPage, LAST_PAGE_TEXTURE, Language.LAST_PAGE, searchTerm, 53);
                }
            }


        }
    }

}
