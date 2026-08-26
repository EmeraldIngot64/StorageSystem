package com.emeraldingot.storagesystem.impl;

import com.google.gson.JsonObject;

public record SearchData(String query, boolean material, boolean itemName) {
    public static SearchData fromJson(JsonObject jsonObject) {
        return new SearchData(
                jsonObject.get("query").getAsString(),
                jsonObject.get("material").getAsInt() == 1,
                jsonObject.get("item_name").getAsInt() == 1
        );
    }
}
