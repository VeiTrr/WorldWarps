package vt.worldwarps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vt.worldwarps.config.ConfigManager;
import vt.worldwarps.config.Configuration;

import java.io.File;
import java.nio.file.Path;

public class WorldWarps {
    public static final String MODID = "worldwarps";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID.toUpperCase());
    public static final Path CONFIG_DIR = new File("./config/" + MODID).toPath();
    public static Configuration config = ConfigManager.getConfig();


}
