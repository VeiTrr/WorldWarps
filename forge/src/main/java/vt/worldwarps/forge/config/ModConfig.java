package vt.worldwarps.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import vt.worldwarps.WorldWarps;

@Mod.EventBusSubscriber(modid = WorldWarps.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    public static final ForgeConfigSpec CONFIG;
    private static final ForgeConfigSpec.BooleanValue USE_PERMISSIONS_API;
    private static final ForgeConfigSpec.IntValue WARP_LIMIT;
    private static final ForgeConfigSpec.IntValue CAN_WARP_PLEVEL;
    private static final ForgeConfigSpec.IntValue CAN_CHANGE_TYPE_PLEVEL;
    private static final ForgeConfigSpec.IntValue CAN_CREATE_WARP_PLEVEL;
    private static final ForgeConfigSpec.IntValue WARP_ADMIN_PLEVEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("General settings")
                .push("general");

        USE_PERMISSIONS_API = builder
                .comment("Use Permissions API")
                .define("usePermissionsApi", false);

        WARP_LIMIT = builder
                .comment("The maximum number of warps a player can have(WIP)")
                .defineInRange("warpLimit", 5, 0, Integer.MAX_VALUE);

        builder.comment("Permission levels if NOT using permissions API")
                .push("permissions");

        CAN_WARP_PLEVEL = builder
                .comment("The permission level required to warp")
                .defineInRange("canWarpPermissionLevel", 0, 0, 4);

        CAN_CHANGE_TYPE_PLEVEL = builder
                .comment("The permission level required to change warp type")
                .defineInRange("canChangeTypePermissionLevel", 0, 0, 4);

        CAN_CREATE_WARP_PLEVEL = builder
                .comment("The permission level required to create a warp")
                .defineInRange("canCreateWarpPermissionLevel", 0, 0, 4);

        WARP_ADMIN_PLEVEL = builder
                .comment("The permission level required to be a warp admin")
                .defineInRange("warpAdminPermissionLevel", 4, 0, 4);

        builder.pop();

        CONFIG = builder.build();
    }

    public static boolean usePermissionsApi;
    public static int warpLimit;
    public static int canWarpPermissionLevel;
    public static int canChangeTypePermissionLevel;
    public static int canCreateWarpPermissionLevel;
    public static int warpAdminPermissionLevel;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        usePermissionsApi = USE_PERMISSIONS_API.get();
        warpLimit = WARP_LIMIT.get();
        canWarpPermissionLevel = CAN_WARP_PLEVEL.get();
        canChangeTypePermissionLevel = CAN_CHANGE_TYPE_PLEVEL.get();
        canCreateWarpPermissionLevel = CAN_CREATE_WARP_PLEVEL.get();
        warpAdminPermissionLevel = WARP_ADMIN_PLEVEL.get();
    }
}