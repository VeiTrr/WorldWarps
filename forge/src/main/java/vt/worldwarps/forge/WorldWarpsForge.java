package vt.worldwarps.forge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.slf4j.Logger;
import vt.worldwarps.WorldWarps;
import vt.worldwarps.forge.permissions.PermissionNodes;
import vt.worldwarps.forge.warps.WarpCommand;
import vt.worldwarps.forge.warps.WarpManager;

@Mod(WorldWarps.MODID)
public class WorldWarpsForge {
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected static WarpManager warpManager;

    public WorldWarpsForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, vt.worldwarps.forge.config.ModConfig.CONFIG, "worldwarps-server.toml");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        warpManager = WarpManager.getWarpManager(event.getServer().getOverworld());
    }

    @SubscribeEvent
    public void onPermissionNodesRegister(PermissionGatherEvent.Nodes event) {
        PermissionNodes.init();
        event.addNodes(PermissionNodes.permissionNodesList);
    }

    public static WarpManager getWarpManager() {
        return warpManager;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        warpManager.markDirty();
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        new WarpCommand().register(event.getDispatcher());
    }
}
