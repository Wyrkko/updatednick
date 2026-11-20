package com.example.nickmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NickManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nickmod_nicks.json");
    private static Map<UUID, String> nicks = new HashMap<>();

    private NickManager() {}

    public static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader r = Files.newBufferedReader(FILE)) {
            Map<UUID, String> loaded = GSON.fromJson(r, new TypeToken<Map<UUID, String>>() {}.getType());
            if (loaded != null) nicks = loaded;
        } catch (Exception e) {
            NickMod.LOGGER.error("Failed to load nicks", e);
        }
    }

    private static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(nicks, w);
        } catch (Exception e) {
            NickMod.LOGGER.error("Failed to save nicks", e);
        }
    }

    public static String get(UUID id) {
        return nicks.get(id);
    }

    public static void set(UUID id, String nick) {
        nicks.put(id, nick);
        save();
    }

    public static void remove(UUID id) {
        nicks.remove(id);
        save();
    }
}
