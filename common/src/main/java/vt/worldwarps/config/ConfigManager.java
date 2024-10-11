package vt.worldwarps.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import vt.worldwarps.WorldWarps;

import java.io.File;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = WorldWarps.MODID.toUpperCase() + ".json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Configuration config;

    public static void loadConfig() {
        File configFile = WorldWarps.CONFIG_DIR.resolve(CONFIG_FILE_NAME).toFile();
        if (configFile.exists()) {
            try {
                config = GSON.fromJson(FileUtils.readFileToString(configFile, "UTF-8"), Configuration.class);
                for (String key : new Configuration().get().keySet()) {
                    if (!config.get().containsKey(key)) {
                        config.get().put(key, new Configuration().get().get(key));
                    }
                }
            } catch (Exception e) {
                WorldWarps.LOGGER.info("Failed to load config file " + e.getMessage());
                config = new Configuration();
            }
        } else {
            config = new Configuration();
        }
        saveConfig();
    }

    public static Configuration getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public static void setConfig(Configuration config) {
        ConfigManager.config = config;
        saveConfig();
    }

    public static void saveConfig() {
        File configFile = WorldWarps.CONFIG_DIR.resolve(CONFIG_FILE_NAME).toFile();
        try {
            FileUtils.writeStringToFile(configFile, GSON.toJson(config), "UTF-8");
        } catch (Exception e) {
            WorldWarps.LOGGER.info("Failed to save config file " + e.getMessage());
        }
    }
}
