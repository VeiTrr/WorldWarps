package dev.vt.worldwarps.permissions;

import dev.vt.worldwarps.config.ModConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

public class PermissionsManager {

    public static boolean hasPermission(ServerCommandSource source, PermissionNode<?> permission) {
        if (usePermissionAPI()) {
            if (permission.getType() == PermissionTypes.BOOLEAN) {
                return hasBooleanPermission(source, (PermissionNode<Boolean>) permission);
            } else if (permission.getType() == PermissionTypes.INTEGER) {
                return hasIntegerPermission(source, (PermissionNode<Integer>) permission);
            }
        } else {
            if (permission.getType() == PermissionTypes.BOOLEAN) {
                return defaultBooleanPermissionCheck(source, (PermissionNode<Boolean>) permission);
            } else if (permission.getType() == PermissionTypes.INTEGER) {
                return defaultIntegerPermissionCheck(source, (PermissionNode<Integer>) permission);
            }
        }
        return false;
    }

    private static boolean usePermissionAPI() {
        return ModConfig.usePermissionsApi;
    }

    private static boolean hasBooleanPermission(ServerCommandSource source, PermissionNode<Boolean> permission) {
        if (source.hasPermissionLevel(2)) {
            return true;
        } else {
            ServerPlayerEntity player = source.getPlayer();
            if (player != null) {
                return PermissionAPI.getPermission(player, permission);
            } else {
                return defaultBooleanPermissionCheck(source, permission);
            }
        }
    }

    private static boolean hasIntegerPermission(ServerCommandSource source, PermissionNode<Integer> permission) {
        if (source.hasPermissionLevel(2)) {
            return true;
        } else {
            ServerPlayerEntity player = source.getPlayer();
            if (player != null) {
                int value = PermissionAPI.getPermission(player, permission);
                //todo: check if the player value is matching the permission value
                return true;
            } else {
                return defaultIntegerPermissionCheck(source, permission);
            }
        }
    }

    private static boolean defaultBooleanPermissionCheck(ServerCommandSource source, PermissionNode<Boolean> permission) {
        if (source.hasPermissionLevel(4)) {
            return true;
        }
        if (permission.getDescription() != null && permission.getDescription().getString().contains("requiredPermissionLevel:")) {
            String permissionDescription = permission.getDescription().getString();
            int requiredPermissionLevel = Integer.parseInt(permissionDescription.substring(permissionDescription.indexOf("requiredPermissionLevel: ") + 25));
            ServerPlayerEntity player = source.getPlayer();
            if (player != null) {
                return player.hasPermissionLevel(requiredPermissionLevel);
            } else {
                return source.hasPermissionLevel(requiredPermissionLevel);
            }
        }
        return true;
    }

    private static boolean defaultIntegerPermissionCheck(ServerCommandSource source, PermissionNode<Integer> permission) {
        if (source.hasPermissionLevel(4)) {
            return true;
        }
        if (permission.getDescription() != null && permission.getDescription().getString().contains("requiredPermissionLevel:")) {
            String permissionDescription = permission.getDescription().getString();
            int requiredPermissionLevel = Integer.parseInt(permissionDescription.substring(permissionDescription.indexOf("requiredPermissionLevel: ") + 25));
            ServerPlayerEntity player = source.getPlayer();
            if (player != null) {
                return player.hasPermissionLevel(requiredPermissionLevel);
            } else {
                return source.hasPermissionLevel(requiredPermissionLevel);
            }
        } else {
            return true;
        }
    }
}