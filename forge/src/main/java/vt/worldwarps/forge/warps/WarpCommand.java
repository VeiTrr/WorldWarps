package vt.worldwarps.forge.warps;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import vt.worldwarps.forge.WorldWarpsForge;
import vt.worldwarps.forge.permissions.PermissionNodes;
import vt.worldwarps.forge.permissions.PermissionsManager;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WarpCommand {
    UUID CONSOLEID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("warp")
                .then(argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            WorldWarpsForge.getWarpManager().getPublicWarps().forEach(w -> builder.suggest(w.getName()));
                            WorldWarpsForge.getWarpManager().getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid() : CONSOLEID).forEach(w -> builder.suggest(w.getName()));
                            return builder.buildFuture();
                        })
                        .requires(source -> hasPermission(source, PermissionNodes.CAN_WARP))
                        .executes(ctx -> tpwarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"))))
                .then(literal("list")
                        .executes(ctx -> listWarps(ctx.getSource()))

                        .then(literal("public")
                                .executes(ctx -> listWarps(ctx.getSource())))

                        .then(literal("personal")
                                .executes(ctx -> listOwnWarps(ctx.getSource()))))
                .then(literal("info")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    WorldWarpsForge.getWarpManager().getPublicWarps().forEach(w -> builder.suggest(w.getName()));
                                    WorldWarpsForge.getWarpManager().getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid() : CONSOLEID).forEach(w -> builder.suggest(w.getName()));
                                    if (hasPermission(ctx.getSource(), PermissionNodes.WARP_ADMIN)) {
                                        WorldWarpsForge.getWarpManager().getAllWarps().forEach(w -> builder.suggest(w.getName()));
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    if (hasPermission(ctx.getSource(), PermissionNodes.WARP_ADMIN)) {
                                        return warpInfo(ctx.getSource(), StringArgumentType.getString(ctx, "name"));
                                    } else {
                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                        if (warp != null && (warp.isPublic() || (ctx.getSource().getPlayer() != null && warp.getOwner().equals(ctx.getSource().getPlayer().getUuid())))) {
                                            return warpInfo(ctx.getSource(), StringArgumentType.getString(ctx, "name"));
                                        } else {
                                            return 1;
                                        }
                                    }
                                }))));

        dispatcher.register(literal("warpmgr")
                .then(literal("create")
                        .then(argument("name", StringArgumentType.word())
                                .requires(source -> hasPermission(source, PermissionNodes.CAN_CREATE_WARP))
                                .executes(ctx -> createWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"))))

                        .then(argument("name", StringArgumentType.word())
                                .then(argument("x", DoubleArgumentType.doubleArg())
                                        .requires(source -> hasPermission(source, PermissionNodes.CAN_CREATE_WARP) && hasPermission(source, PermissionNodes.WARP_ADMIN))
                                        .then(argument("y", DoubleArgumentType.doubleArg())
                                                .then(argument("z", DoubleArgumentType.doubleArg())
                                                        .then(argument("yaw", FloatArgumentType.floatArg())
                                                                .then(argument("pitch", FloatArgumentType.floatArg())
                                                                        .then(argument("world", DimensionArgumentType.dimension())
                                                                                .suggests((ctx, builder) -> DimensionArgumentType.dimension().listSuggestions(ctx, builder)))
                                                                        .executes(ctx -> createWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                                DoubleArgumentType.getDouble(ctx, "x"), DoubleArgumentType.getDouble(ctx, "y"),
                                                                                DoubleArgumentType.getDouble(ctx, "z"), FloatArgumentType.getFloat(ctx, "yaw"),
                                                                                FloatArgumentType.getFloat(ctx, "pitch"), DimensionArgumentType.getDimensionArgument(ctx, "world").toString()))))))))

                        .then(argument("name", StringArgumentType.word())
                                .then(argument("owner", EntityArgumentType.entity())
                                        .requires(source -> hasPermission(source, PermissionNodes.CAN_CREATE_WARP) && hasPermission(source, PermissionNodes.WARP_ADMIN))
                                        .suggests((ctx, builder) -> EntityArgumentType.entity().listSuggestions(ctx, builder))
                                        .then(argument("x", DoubleArgumentType.doubleArg())
                                                .then(argument("y", DoubleArgumentType.doubleArg())
                                                        .then(argument("z", DoubleArgumentType.doubleArg())
                                                                .then(argument("yaw", FloatArgumentType.floatArg())
                                                                        .then(argument("pitch", FloatArgumentType.floatArg())
                                                                                .then(argument("world", DimensionArgumentType.dimension())
                                                                                        .suggests((ctx, builder) -> DimensionArgumentType.dimension().listSuggestions(ctx, builder)))
                                                                                .executes(ctx -> createWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                                        EntityArgumentType.getEntity(ctx, "owner").getUuid(),
                                                                                        DoubleArgumentType.getDouble(ctx, "x"), DoubleArgumentType.getDouble(ctx, "y"),
                                                                                        DoubleArgumentType.getDouble(ctx, "z"), FloatArgumentType.getFloat(ctx, "yaw"),
                                                                                        FloatArgumentType.getFloat(ctx, "pitch"), DimensionArgumentType.getDimensionArgument(ctx, "world").toString()))))))))))


                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    WorldWarpsForge.getWarpManager().getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid() : CONSOLEID).forEach(w -> builder.suggest(w.getName()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    if (hasPermission(ctx.getSource(), PermissionNodes.WARP_ADMIN)) {
                                        return removeWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"));
                                    } else {
                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));

                                        if (warp != null && ctx.getSource().getPlayer() != null && warp.getOwner().equals(ctx.getSource().getPlayer().getUuid())) {
                                            return removeWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"));
                                        } else {
                                            return 1;
                                        }
                                    }
                                })))


                .then(literal("list")
                        .executes(ctx -> listWarps(ctx.getSource()))
                        .then(literal("public")
                                .executes(ctx -> listWarps(ctx.getSource())))

                        .then(literal("personal")
                                .executes(ctx -> listOwnWarps(ctx.getSource()))
                                .then(argument("entity", EntityArgumentType.entity())
                                        .requires(source -> hasPermission(source, PermissionNodes.WARP_ADMIN))
                                        .suggests((ctx, builder) -> EntityArgumentType.entity().listSuggestions(ctx, builder))
                                        .executes(ctx -> {
                                            if (EntityArgumentType.getEntity(ctx, "entity").getUuid() != null) {
                                                return listPersonalWarps(ctx.getSource(), EntityArgumentType.getEntity(ctx, "entity").getUuid());
                                            } else {
                                                return listPublicWarpsbyOwner(ctx.getSource(), EntityArgumentType.getEntity(ctx, "entity").getUuid());
                                            }
                                        })))

                        .then(literal("all")
                                .requires(source -> hasPermission(source, PermissionNodes.WARP_ADMIN))
                                .executes(ctx -> listAllWarps(ctx.getSource())))
                )


                .then(literal("visibility")
                        .then(argument("name", StringArgumentType.word())
                                .requires(source -> hasPermission(source, PermissionNodes.CAN_CHANGE_TYPE))
                                .suggests((ctx, builder) -> {
                                    WorldWarpsForge.getWarpManager().getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid() : CONSOLEID).forEach(w -> builder.suggest(w.getName()));
                                    if (hasPermission(ctx.getSource(), PermissionNodes.WARP_ADMIN)) {
                                        WorldWarpsForge.getWarpManager().getAllWarps().forEach(w -> builder.suggest(w.getName()));
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    if (hasPermission(ctx.getSource(), PermissionNodes.WARP_ADMIN)) {
                                        return toggleWarpType(ctx.getSource(), StringArgumentType.getString(ctx, "name"));
                                    } else {
                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                        if (warp != null && ctx.getSource().getPlayer() != null && warp.getOwner().equals(ctx.getSource().getPlayer().getUuid())) {
                                            return toggleWarpType(ctx.getSource(), StringArgumentType.getString(ctx, "name"));
                                        } else {
                                            return 1;
                                        }
                                    }
                                })))


                .then(literal("update")
                        .then(argument("name", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    WorldWarpsForge.getWarpManager().getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid() : CONSOLEID).forEach(w -> builder.suggest(w.getName()));
                                    return builder.buildFuture();
                                })
                                .then(argument("x", DoubleArgumentType.doubleArg())
                                        .then(argument("y", DoubleArgumentType.doubleArg())
                                                .then(argument("z", DoubleArgumentType.doubleArg())
                                                        .then(argument("yaw", FloatArgumentType.floatArg())
                                                                .then(argument("pitch", FloatArgumentType.floatArg())
                                                                        .executes(ctx -> {
                                                                            if (hasPermission(ctx.getSource(), PermissionNodes.WARP_ADMIN)) {
                                                                                return updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                                        DoubleArgumentType.getDouble(ctx, "x"), DoubleArgumentType.getDouble(ctx, "y"),
                                                                                        DoubleArgumentType.getDouble(ctx, "z"), FloatArgumentType.getFloat(ctx, "yaw"),
                                                                                        FloatArgumentType.getFloat(ctx, "pitch"));
                                                                            } else {
                                                                                Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                                                if (warp != null && ctx.getSource().getPlayer() != null && warp.getOwner().equals(ctx.getSource().getPlayer().getUuid())) {
                                                                                    return updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                                            DoubleArgumentType.getDouble(ctx, "x"), DoubleArgumentType.getDouble(ctx, "y"),
                                                                                            DoubleArgumentType.getDouble(ctx, "z"), FloatArgumentType.getFloat(ctx, "yaw"),
                                                                                            FloatArgumentType.getFloat(ctx, "pitch"));
                                                                                } else {
                                                                                    return 1;
                                                                                }
                                                                            }
                                                                        }))))))

                                .then(argument("param", StringArgumentType.word())
                                        .requires(source -> hasPermission(source, PermissionNodes.WARP_ADMIN))
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("x");
                                            builder.suggest("y");
                                            builder.suggest("z");
                                            builder.suggest("yaw");
                                            builder.suggest("pitch");
                                            return builder.buildFuture();
                                        })
                                        .then(argument("value", StringArgumentType.word())
                                                .suggests((ctx, builder) -> {
                                                    switch (StringArgumentType.getString(ctx, "param")) {
                                                        case "x" -> {
                                                            Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                            if (warp != null) {
                                                                builder.suggest(Double.toString(warp.getPos().x));
                                                            }
                                                            if (ctx.getSource().getPlayer() != null) {
                                                                builder.suggest(Double.toString(ctx.getSource().getPlayer().getPos().x));
                                                            }
                                                        }
                                                        case "y" -> {
                                                            Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                            if (warp != null) {
                                                                builder.suggest(Double.toString(warp.getPos().y));
                                                            }
                                                            if (ctx.getSource().getPlayer() != null) {
                                                                builder.suggest(Double.toString(ctx.getSource().getPlayer().getPos().y));
                                                            }
                                                        }
                                                        case "z" -> {
                                                            Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                            if (warp != null) {
                                                                builder.suggest(Double.toString(warp.getPos().z));
                                                            }
                                                            if (ctx.getSource().getPlayer() != null) {
                                                                builder.suggest(Double.toString(ctx.getSource().getPlayer().getPos().z));
                                                            }
                                                        }
                                                        case "yaw" -> {
                                                            Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                            if (warp != null) {
                                                                builder.suggest(Float.toString(warp.getYaw()));
                                                            }
                                                            if (ctx.getSource().getPlayer() != null) {
                                                                builder.suggest(Float.toString(ctx.getSource().getPlayer().getYaw()));
                                                            }
                                                        }
                                                        case "pitch" -> {
                                                            Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                            if (warp != null) {
                                                                builder.suggest(Float.toString(warp.getPitch()));
                                                            }
                                                            if (ctx.getSource().getPlayer() != null) {
                                                                builder.suggest(Float.toString(ctx.getSource().getPlayer().getPitch()));
                                                            }
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(ctx -> switch (StringArgumentType.getString(ctx, "param")) {
                                                    case "x" -> {
                                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                        if (warp != null) {
                                                            yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                    Double.parseDouble(StringArgumentType.getString(ctx, "value")), warp.getPos().y, warp.getPos().z, warp.getYaw(), warp.getPitch());
                                                        } else {
                                                            ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                            yield 1;
                                                        }
                                                    }
                                                    case "y" -> {
                                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                        if (warp != null) {
                                                            yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                    warp.getPos().x, Double.parseDouble(StringArgumentType.getString(ctx, "value")), warp.getPos().z, warp.getYaw(), warp.getPitch());
                                                        } else {
                                                            ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                            yield 1;
                                                        }
                                                    }
                                                    case "z" -> {
                                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                        if (warp != null) {
                                                            yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                    warp.getPos().x, warp.getPos().y, Double.parseDouble(StringArgumentType.getString(ctx, "value")), warp.getYaw(), warp.getPitch());
                                                        } else {
                                                            ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                            yield 1;
                                                        }
                                                    }
                                                    case "yaw" -> {
                                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                        if (warp != null) {
                                                            yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                    warp.getPos().x, warp.getPos().y, warp.getPos().z, Float.parseFloat(StringArgumentType.getString(ctx, "value")), warp.getPitch());
                                                        } else {
                                                            ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                            yield 1;
                                                        }
                                                    }
                                                    case "pitch" -> {
                                                        Warp warp = WorldWarpsForge.getWarpManager().getWarp(StringArgumentType.getString(ctx, "name"));
                                                        if (warp != null) {
                                                            yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                    warp.getPos().x, warp.getPos().y, warp.getPos().z, warp.getYaw(), Float.parseFloat(StringArgumentType.getString(ctx, "value")));
                                                        } else {
                                                            ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                            yield 1;
                                                        }
                                                    }
                                                    default -> 1;
                                                })))

                        )
                        .executes(ctx -> {
                            if (ctx.getSource().getPlayer() == null) {
                                return 1;
                            }
                            updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                    ctx.getSource().getPlayer().getPos().x, ctx.getSource().getPlayer().getPos().y, ctx.getSource().getPlayer().getPos().z,
                                    ctx.getSource().getPlayer().getYaw(), ctx.getSource().getPlayer().getPitch());
                            return 1;
                        })
                ));
    }

    private static boolean hasPermission(ServerCommandSource source, PermissionNode<Boolean> permissionNode) {
        return PermissionsManager.hasPermission(source, permissionNode);
    }

    private int tpwarp(ServerCommandSource source, String name) {
        Warp warp = WorldWarpsForge.getWarpManager().getWarp(name);
        if (source.getPlayer() != null) {
            if (warp != null) {
                if (warp.isPublic() || warp.getOwner().equals(source.getPlayer().getUuid())) {
                    RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(warp.getWorld()));
                    ServerWorld world = source.getServer().getWorld(worldKey);
                    if (world != null) {
                        source.getPlayer().teleport(world, warp.getPos().x, warp.getPos().y, warp.getPos().z, warp.getYaw(), warp.getPitch());
                        source.getPlayer().sendMessage(Text.of("Teleported to warp " + name), false);
                    } else {
                        source.getPlayer().sendMessage(Text.of("World not found"), false);
                    }
                }
            } else {
                source.getPlayer().sendMessage(Text.of("Warp not found"), false);
            }
        }

        return 0;
    }

    private int createWarp(ServerCommandSource source, String name) {
        List<String> BANNEDNAMES = List.of("list", "info", "remove", "visibility", "update", "public", "personal", "all");
        if (source.getPlayer() == null) {
            return 1;
        }
        if (BANNEDNAMES.contains(name)) {
            source.sendMessage(Text.of("Name " + name + " is not allowed"));
            return 1;
        }
        String world = source.getWorld().getRegistryKey().getValue().toString();
        WorldWarpsForge.getWarpManager().addWarp(new Warp(name, source.getPlayer().getUuid(), false, source.getPlayer().getPos(), source.getPlayer().getYaw(), source.getPlayer().getPitch(), world));
        source.sendMessage(Text.of("Warp " + name + " created"));
        return 0;
    }

    private int createWarp(ServerCommandSource source, String name, double x, double y, double z, float yaw, float pitch, String world) {
        if (source.getPlayer() == null) {
            return 1;
        }
        WorldWarpsForge.getWarpManager().addWarp(new Warp(name, source.getPlayer().getUuid(), false, new Vec3d(x, y, z), yaw, pitch, world));
        source.sendMessage(Text.of("Warp " + name + " created"));
        return 0;
    }

    private int createWarp(ServerCommandSource source, String name, UUID owner, double x, double y, double z, float yaw, float pitch, String world) {
        WorldWarpsForge.getWarpManager().addWarp(new Warp(name, owner, false, new Vec3d(x, y, z), yaw, pitch, world));
        source.sendMessage(Text.of("Warp " + name + " created"));
        return 0;
    }

    private int removeWarp(ServerCommandSource source, String name) {
        WorldWarpsForge.getWarpManager().removeWarp(name, source.getPlayer() != null ? source.getPlayer().getUuid() : CONSOLEID);
        source.sendMessage(Text.of("Warp " + name + " removed"));
        return 0;
    }

    private int listWarps(ServerCommandSource source) {
        source.sendMessage(Text.of("Public warps:"));
        WorldWarpsForge.getWarpManager().getPublicWarps().forEach(w -> source.sendMessage(Text.of(w.getName())));
        return 0;
    }

    private int listPublicWarpsbyOwner(ServerCommandSource source, UUID owner) {
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(owner);
        if (player != null) {
            source.sendMessage(Text.of("Public warps of " + player.getName().getString() + ":"));
        } else {
            source.sendMessage(Text.of("Public warps of unknown:"));
        }
        WorldWarpsForge.getWarpManager().getWarpsByOwner(owner).forEach(w -> {
            if (w.isPublic()) source.sendMessage(Text.of(w.getName()));
        });
        return 0;
    }

    private int listOwnWarps(ServerCommandSource source) {
        source.sendMessage(Text.of("Your warps:"));
        WorldWarpsForge.getWarpManager().getWarpsByOwner(source.getPlayer() != null ? source.getPlayer().getUuid() : CONSOLEID).forEach(w -> source.sendMessage(Text.of(w.getName())));
        return 0;
    }

    private int listPersonalWarps(ServerCommandSource source, UUID owner) {
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(owner);
        if (player != null) {
            source.sendMessage(Text.of("Warps of " + player.getName().getString() + ":"));
        } else {
            source.sendMessage(Text.of("Warps of unknown:"));
        }
        WorldWarpsForge.getWarpManager().getWarpsByOwner(owner).forEach(w -> source.sendMessage(Text.of(w.getName())));
        return 0;
    }

    private int listAllWarps(ServerCommandSource source) {
        List<Warp> warps = WorldWarpsForge.getWarpManager().getAllWarps();
        warps.sort(Comparator.comparing(Warp::getName));
        source.sendMessage(Text.of("All warps:"));
        warps.forEach(w -> source.sendMessage(Text.of(w.getName())));
        return 0;
    }

    private int warpInfo(ServerCommandSource source, String name) {
        Warp warp = WorldWarpsForge.getWarpManager().getWarp(name);
        if (warp != null) {
            source.sendMessage(Text.of("Warp " + name + " info:"));
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(warp.getOwner());
            if (player != null) {
                source.sendMessage(Text.of("Owner: " + player.getName().getString()));
            } else {
                source.sendMessage(Text.of("Owner: unknown"));
            }
            source.sendMessage(Text.of("Public: " + (warp.isPublic() ? "yes" : "no")));
            source.sendMessage(Text.of("Position: " + warp.getPos().x + " " + warp.getPos().y + " " + warp.getPos().z));
            source.sendMessage(Text.of("Yaw: " + warp.getYaw()));
            source.sendMessage(Text.of("Pitch: " + warp.getPitch()));
            source.sendMessage(Text.of("World: " + warp.getWorld()));
        } else {
            source.sendMessage(Text.of("Warp not found"));
        }
        return 0;
    }

    private int toggleWarpType(ServerCommandSource source, String name) {
        WorldWarpsForge.getWarpManager().toggleWarpType(name, source.getPlayer() != null ? source.getPlayer().getUuid() : CONSOLEID);
        source.sendMessage(Text.of("Warp " + name + " is now " + (WorldWarpsForge.getWarpManager().getWarp(name).isPublic() ? "public" : "private")));
        return 0;
    }

    private int updateWarp(ServerCommandSource source, String name, double x, double y, double z, float yaw, float pitch) {
        String world = source.getWorld().getRegistryKey().getValue().toString();
        WorldWarpsForge.getWarpManager().updateWarp(name, source.getPlayer() != null ? source.getPlayer().getUuid() : CONSOLEID, new Vec3d(x, y, z), yaw, pitch, world);
        source.sendMessage(Text.of("Warp " + name + " updated"));
        return 0;
    }
}
