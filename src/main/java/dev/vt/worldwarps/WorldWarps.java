package dev.vt.worldwarps;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WorldWarps.MODID)
public class WorldWarps {
    public static final String MODID = "worldwarps";
    protected static final Logger LOGGER = LogUtils.getLogger();
    protected static WarpManager warpManager;

    public WorldWarps() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        warpManager = WarpManager.getWarpManager(event.getServer().getOverworld());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        warpManager.markDirty();
    }

    @SubscribeEvent
    public void onCommanRegister(RegisterCommandsEvent event) {
        new WarpCommand().register(event.getDispatcher());
    }
}
