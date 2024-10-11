package vt.worldwarps.forge.permissions;


import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import vt.worldwarps.WorldWarps;
import vt.worldwarps.forge.config.ModConfig;

import java.util.List;

public class PermissionNodes {
    private static String permissionLevelRequired = "requiredPermissionLevel: ";

    public static final PermissionNode<Boolean> CAN_WARP = new PermissionNode<>(
            new Identifier(WorldWarps.MODID, "can_warp"),
            PermissionTypes.BOOLEAN,
            (player, playerUUID, context) -> true
    );

    public static final PermissionNode<Boolean> CAN_CHANGE_TYPE = new PermissionNode<>(
            new Identifier(WorldWarps.MODID, "can_change_type"),
            PermissionTypes.BOOLEAN,
            (player, playerUUID, context) -> true
    );

    public static final PermissionNode<Boolean> CAN_CREATE_WARP = new PermissionNode<>(
            new Identifier(WorldWarps.MODID, "can_create_warp"),
            PermissionTypes.BOOLEAN,
            (player, playerUUID, context) -> true
    );

    public static final PermissionNode<Boolean> WARP_ADMIN = new PermissionNode<>(
            new Identifier(WorldWarps.MODID, "warp_admin"),
            PermissionTypes.BOOLEAN,
            (player, playerUUID, context) -> false
    );

    public static final PermissionNode<Integer> WARP_LIMIT = new PermissionNode<>(
            new Identifier(WorldWarps.MODID, "warp_limit"),
            PermissionTypes.INTEGER,
            (player, playerUUID, context) -> 5
    );

    public static List<PermissionNode<?>> permissionNodesList = List.of(CAN_WARP, CAN_CHANGE_TYPE, CAN_CREATE_WARP, WARP_ADMIN, WARP_LIMIT);

    public static void init() {
        CAN_WARP.setInformation(Text.of("Can Warp"), Text.of(permissionLevelRequired + ModConfig.canWarpPermissionLevel));
        CAN_CHANGE_TYPE.setInformation(Text.of("Can Change Type"), Text.of(permissionLevelRequired + ModConfig.canChangeTypePermissionLevel));
        CAN_CREATE_WARP.setInformation(Text.of("Can Create Warp"), Text.of(permissionLevelRequired + ModConfig.canCreateWarpPermissionLevel));
        WARP_ADMIN.setInformation(Text.of("Warp Admin"), Text.of(permissionLevelRequired + ModConfig.warpAdminPermissionLevel));
        WARP_LIMIT.setInformation(Text.of("Warp Limit"), Text.of("The maximum number of warps a player can have"));
    }
}