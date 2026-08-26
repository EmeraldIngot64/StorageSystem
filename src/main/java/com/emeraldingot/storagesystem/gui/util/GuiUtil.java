package com.emeraldingot.storagesystem.gui.util;

import com.emeraldingot.storagesystem.impl.SearchData;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.dialog.DialogBase;
import net.md_5.bungee.api.dialog.MultiActionDialog;
import net.md_5.bungee.api.dialog.action.ActionButton;
import net.md_5.bungee.api.dialog.action.CustomClickAction;
import net.md_5.bungee.api.dialog.body.PlainMessageBody;
import net.md_5.bungee.api.dialog.input.BooleanInput;
import net.md_5.bungee.api.dialog.input.TextInput;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiUtil {
    private static final Map<UUID, Consumer<SearchData>> searchCallbacks = new HashMap<>();

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
