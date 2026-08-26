package com.emeraldingot.storagesystem.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocaleUtil {
    public static void getLocalizedName(String translationKey, String locale) {

    }

    public static Path getLangFile(String locale) throws IOException, InterruptedException {
        Path path = Paths.get(ControllerFileManager.getInstance().getLangFolder().toString(), locale + ".json");
        if (!path.toFile().exists()) {
            String url = String.format("https://raw.githubusercontent.com/InventivetalentDev/minecraft-assets/refs/heads/26.2/assets/minecraft/lang/%s.json", locale);
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFile(path));
        }
        return path;
    }
}
