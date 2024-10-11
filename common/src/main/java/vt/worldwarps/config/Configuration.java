package vt.worldwarps.config;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Configuration {
    public boolean UsePermissionsApi;
    public int WarpLimit;
    public int CanWarpPermissionLevel;
    public int CanChangeTypePermissionLevel;
    public int CanCreateWarpPermissionLevel;
    public int WarpAdminPermissionLevel;

    public Configuration() {
        this.UsePermissionsApi = false;
        this.WarpLimit = 5;
        this.CanWarpPermissionLevel = 0;
        this.CanChangeTypePermissionLevel = 0;
        this.CanCreateWarpPermissionLevel = 0;
        this.WarpAdminPermissionLevel = 4;
    }

    public void save() {
        ConfigManager.setConfig(this);
    }

    public HashMap<String, Type> get() {
        HashMap<String, Type> map = new HashMap<>();
        map.put("UsePermissionsApi", boolean.class);
        map.put("WarpLimit", int.class);
        map.put("CanWarpPermissionLevel", int.class);
        map.put("CanChangeTypePermissionLevel", int.class);
        map.put("CanCreateWarpPermissionLevel", int.class);
        map.put("WarpAdminPermissionLevel", int.class);
        return map;
    }

    public void set(Configuration configuration) {
        this.UsePermissionsApi = configuration.UsePermissionsApi;
        this.WarpLimit = configuration.WarpLimit;
        this.CanWarpPermissionLevel = configuration.CanWarpPermissionLevel;
        this.CanChangeTypePermissionLevel = configuration.CanChangeTypePermissionLevel;
        this.CanCreateWarpPermissionLevel = configuration.CanCreateWarpPermissionLevel;
        this.WarpAdminPermissionLevel = configuration.WarpAdminPermissionLevel;
    }

    public void set(String key, String value) {
        switch (key) {
            case "UsePermissionsApi":
                this.UsePermissionsApi = Boolean.parseBoolean(value);
                break;
            case "WarpLimit":
                this.WarpLimit = Integer.parseInt(value);
                break;
            case "CanWarpPermissionLevel":
                this.CanWarpPermissionLevel = Integer.parseInt(value);
                break;
            case "CanChangeTypePermissionLevel":
                this.CanChangeTypePermissionLevel = Integer.parseInt(value);
                break;
            case "CanCreateWarpPermissionLevel":
                this.CanCreateWarpPermissionLevel = Integer.parseInt(value);
                break;
            case "WarpAdminPermissionLevel":
                this.WarpAdminPermissionLevel = Integer.parseInt(value);
                break;
        }
        save();
    }

    public String get(String key) {
        return switch (key) {
            case "UsePermissionsApi" -> String.valueOf(this.UsePermissionsApi);
            case "WarpLimit" -> String.valueOf(this.WarpLimit);
            case "CanWarpPermissionLevel" -> String.valueOf(this.CanWarpPermissionLevel);
            case "CanChangeTypePermissionLevel" -> String.valueOf(this.CanChangeTypePermissionLevel);
            case "CanCreateWarpPermissionLevel" -> String.valueOf(this.CanCreateWarpPermissionLevel);
            case "WarpAdminPermissionLevel" -> String.valueOf(this.WarpAdminPermissionLevel);
            default -> null;
        };
    }
}
