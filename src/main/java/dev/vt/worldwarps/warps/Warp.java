package dev.vt.worldwarps.warps;

import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class Warp {
    private String name;
    private UUID owner;
    private boolean isPublic;
    private Vec3d pos;
    private float yaw, pitch;
    private String world;


    public Warp(String name, UUID owner, boolean isPublic, Vec3d pos, float yaw, float pitch, String world) {
        this.name = name;
        this.owner = owner;
        this.isPublic = isPublic;
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public double getX() {
        return pos.x;
    }

    public double getY() {
        return pos.y;
    }

    public double getZ() {
        return pos.z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Vec3d getPos() {
        return pos;
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}