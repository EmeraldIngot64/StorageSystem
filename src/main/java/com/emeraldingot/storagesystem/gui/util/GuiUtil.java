package com.emeraldingot.storagesystem.gui.util;

import com.emeraldingot.storagesystem.StorageSystem;
import com.emeraldingot.storagesystem.gui.terminal.pages.AbstractTerminalPage;
import com.emeraldingot.storagesystem.gui.terminal.pages.TerminalItemsPage;
import com.emeraldingot.storagesystem.impl.SearchData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.dialog.Dialog;
import net.md_5.bungee.api.dialog.DialogBase;
import net.md_5.bungee.api.dialog.MultiActionDialog;
import net.md_5.bungee.api.dialog.NoticeDialog;
import net.md_5.bungee.api.dialog.action.ActionButton;
import net.md_5.bungee.api.dialog.action.CustomClickAction;
import net.md_5.bungee.api.dialog.body.DialogBody;
import net.md_5.bungee.api.dialog.body.PlainMessageBody;
import net.md_5.bungee.api.dialog.input.BooleanInput;
import net.md_5.bungee.api.dialog.input.DialogInput;
import net.md_5.bungee.api.dialog.input.TextInput;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class GuiUtil {
    private static final Map<UUID, Consumer<SearchData>> searchCallbacks = new HashMap<>();
//    public static void openCustomStringSign(Player player, Function<String, AbstractTerminalPage> confirmPageFactory, AbstractTerminalPage returnPage, String message) {
//        SignGUI gui = null;
//        try {
//            gui = SignGUI.builder()
//                    // set lines
//                    .setLines(null, "^^^^^^", message)
//
//
//                    // set the sign type
//                    .setType(Material.SPRUCE_SIGN)
//
//
//                    // set the handler/listener (called when the player finishes editing)
//                    .setHandler((p, result) -> {
//                        // get a speficic line, starting index is 0
//                        String line0 = result.getLine(0);
//
//                        if (line0.isEmpty()) {
//                            Bukkit.getScheduler().runTask(StorageSystem.getInstance(), () -> {
//                                player.openInventory(returnPage.build());
//                            });
//                        }
//                        else {
//                            Bukkit.getScheduler().runTask(StorageSystem.getInstance(), () -> {
//                                AbstractTerminalPage confirmPage = confirmPageFactory.apply(line0);
//                                player.openInventory(confirmPage.build());
//                            });
//                        }
//
//
//
//
//
//
////                        if (line0.isEmpty())) {
////                            // The user has not entered anything on line 2, so we open the sign again
////
////                        }
//
//
//                        // Just close the sign by not returning any actions
//                        return Collections.emptyList();
//                    })
//
//                    // build the SignGUI
//                    .build();
//
//            gui.open(player);
//        } catch (SignGUIVersionException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void openSearchDialog(Player player, Consumer<SearchData> onReceiveQuery) {

        DialogBase dialogBase = new DialogBase(
                new TextComponent("Storage Search"),
                null,
                List.of(
                        new TextInput("query", new TextComponent("Search Query")),
                        new BooleanInput("item_name", new TextComponent("Item Name"), true, null, null),
                        new BooleanInput("material", new TextComponent("Material (Item ID)"), false, null, null)
//                        new BooleanInput("item_name", new TextComponent("Item Name")),
                ),
                List.of(
                        new PlainMessageBody(new TextComponent("Select query and search types:"))
                ),
                true,
                false,
                DialogBase.AfterAction.CLOSE
        );
        // TODO: put storagesystem:submit_search into some sort of class
        ActionButton searchButton = new ActionButton(new TextComponent("Search"), new CustomClickAction("storagesystem:submit_search"));

        MultiActionDialog dialog = new MultiActionDialog(dialogBase, searchButton);


        searchCallbacks.put(player.getUniqueId(), onReceiveQuery);
        player.showDialog(dialog);
    }

    public static void fulfillCallback(UUID uuid, SearchData searchData) {
        searchCallbacks.get(uuid).accept(searchData);
        searchCallbacks.remove(uuid);
    }
}
