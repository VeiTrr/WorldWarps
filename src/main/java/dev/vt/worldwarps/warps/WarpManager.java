package dev.vt.worldwarps.warps;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpManager extends PersistentState {
    private List<Warp> warps = new ArrayList<>();

    public void addWarp(Warp warp) {
        warps.add(warp);
        this.markDirty();
    }

    public void removeWarp(String name, UUID owner) {
        warps.removeIf(w -> w.getName().equals(name) && w.getOwner().equals(owner));
        this.markDirty();
    }

    public List<Warp> getWarpsByOwner(UUID owner) {
        return warps.stream().filter(w -> w.getOwner().equals(owner)).collect(Collectors.toList());
    }

    public List<Warp> getPublicWarps() {
        return warps.stream().filter(Warp::isPublic).collect(Collectors.toList());
    }

    public Warp getWarp(String name) {
        return warps.stream().filter(w -> w.getName().equals(name)).findFirst().orElse(null);
    }

    protected List<Warp> getAllWarps() {
        return warps;
    }

    public void toggleWarpType(String name, UUID owner) {
        Warp warp = getWarp(name);
        if (warp != null && warp.getOwner().equals(owner)) {
            warp.setPublic(!warp.isPublic());
        }
        this.markDirty();
    }

    public void updateWarp(String name, UUID owner, Vec3d pos, float yaw, float pitch, String world) {
        Warp warp = getWarp(name);
        if (warp != null && warp.getOwner().equals(owner)) {
            warp.setPos(pos);
            warp.setYaw(yaw);
            warp.setPitch(pitch);
            warp.setWorld(world);
        }
        this.markDirty();
    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList warpList = new NbtList();
        warps.forEach(w -> {
            NbtCompound warpNbt = new NbtCompound();
            warpNbt.putString("name", w.getName());
            warpNbt.putUuid("owner", w.getOwner());
            warpNbt.putBoolean("isPublic", w.isPublic());
            warpNbt.putDouble("x", w.getX());
            warpNbt.putDouble("y", w.getY());
            warpNbt.putDouble("z", w.getZ());
            warpNbt.putFloat("yaw", w.getYaw());
            warpNbt.putFloat("pitch", w.getPitch());
            warpNbt.putString("world", w.getWorld());
            warpList.add(warpNbt);
        });
        nbt.put("warps", warpList);
        return nbt;
    }

    private static WarpManager fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        WarpManager manager = new WarpManager();
        NbtList warpList = nbt.getList("warps", 10);
        for (int i = 0; i < warpList.size(); i++) {
            NbtCompound warpNbt = warpList.getCompound(i);
            String name = warpNbt.getString("name");
            UUID owner = warpNbt.getUuid("owner");
            boolean isPublic = warpNbt.getBoolean("isPublic");
            double x = warpNbt.getDouble("x");
            double y = warpNbt.getDouble("y");
            double z = warpNbt.getDouble("z");
            float yaw = warpNbt.getFloat("yaw");
            float pitch = warpNbt.getFloat("pitch");
            String world = warpNbt.getString("world");
            manager.addWarp(new Warp(name, owner, isPublic, new Vec3d(x, y, z), yaw, pitch, world));
        }
        return manager;
    }

    public static WarpManager getWarpManager(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(new PersistentState.Type<>(WarpManager::new, WarpManager::fromNbt, null), "warps");
    }
}