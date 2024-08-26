package dev.vt.worldwarps;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ExecutionException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WarpCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("warp")
                .then(argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            WorldWarps.warpManager.getPublicWarps().forEach(w -> builder.suggest(w.getName()));
                            WorldWarps.warpManager.getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid().toString() : "CONSOLE").forEach(w -> builder.suggest(w.getName()));
                            return builder.buildFuture();
                        })
                        .executes(ctx -> tpwarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"))))
                .then(literal("list")
                        .executes(ctx -> listWarps(ctx.getSource()))
                        .then(literal("public")
                                .executes(ctx -> listWarps(ctx.getSource())))
                        .then(literal("personal")
                                .executes(ctx -> listPWarps(ctx.getSource())))));

        dispatcher.register(literal("warpmgr")
                .then(literal("create")
                        .then(argument("name", StringArgumentType.word())
                                .executes(ctx -> createWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"))))
                        .then(argument("name", StringArgumentType.word())
                                .then(argument("x", DoubleArgumentType.doubleArg())
                                        .then(argument("y", DoubleArgumentType.doubleArg())
                                                .then(argument("z", DoubleArgumentType.doubleArg())
                                                        .then(argument("yaw", FloatArgumentType.floatArg())
                                                                .then(argument("pitch", FloatArgumentType.floatArg())
                                                                        .then(argument("world", DimensionArgumentType.dimension())
                                                                                .suggests((ctx, builder) -> DimensionArgumentType.dimension().listSuggestions(ctx, builder)))
                                                                                .requires(s -> s.hasPermissionLevel(2))
                                                                                .executes(ctx -> createWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                                        DoubleArgumentType.getDouble(ctx, "x"), DoubleArgumentType.getDouble(ctx, "y"),
                                                                                        DoubleArgumentType.getDouble(ctx, "z"), FloatArgumentType.getFloat(ctx, "yaw"),
                                                                                        FloatArgumentType.getFloat(ctx, "pitch"), DimensionArgumentType.getDimensionArgument(ctx, "world").toString())))))))))
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            WorldWarps.warpManager.getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid().toString() : "CONSOLE").forEach(w -> builder.suggest(w.getName()));
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> removeWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
                        .then(literal("list")
                                .executes(ctx -> listWarps(ctx.getSource()))
                                .then(literal("public")
                                        .executes(ctx -> listWarps(ctx.getSource())))
                                .then(literal("personal")
                                        .executes(ctx -> listPWarps(ctx.getSource()))))
                        .then(literal("public")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            WorldWarps.warpManager.getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid().toString() : "CONSOLE").forEach(w -> builder.suggest(w.getName()));
                                            return builder.buildFuture();
                                        })
                                        .executes(ctx -> toggleWarpType(ctx.getSource(), StringArgumentType.getString(ctx, "name")))))
                        .then(literal("update")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            WorldWarps.warpManager.getWarpsByOwner(ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().getUuid().toString() : "CONSOLE").forEach(w -> builder.suggest(w.getName()));
                                            return builder.buildFuture();
                                        })
                                        .then(argument("x", DoubleArgumentType.doubleArg())
                                                .then(argument("y", DoubleArgumentType.doubleArg())
                                                        .then(argument("z", DoubleArgumentType.doubleArg())
                                                                .then(argument("yaw", FloatArgumentType.floatArg())
                                                                        .then(argument("pitch", FloatArgumentType.floatArg())
                                                                                .executes(ctx -> updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                                        DoubleArgumentType.getDouble(ctx, "x"), DoubleArgumentType.getDouble(ctx, "y"),
                                                                                        DoubleArgumentType.getDouble(ctx, "z"), FloatArgumentType.getFloat(ctx, "yaw"),
                                                                                        FloatArgumentType.getFloat(ctx, "pitch"))))))))
                                        .then(argument("param", StringArgumentType.word())
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
                                                                    Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                    if (warp != null) {
                                                                        builder.suggest(Double.toString(warp.getPos().x));
                                                                    }
                                                                    if (ctx.getSource().getPlayer() != null) {
                                                                        builder.suggest(Double.toString(ctx.getSource().getPlayer().getPos().x));
                                                                    }
                                                                }
                                                                case "y" -> {
                                                                    Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                    if (warp != null) {
                                                                        builder.suggest(Double.toString(warp.getPos().y));
                                                                    }
                                                                    if (ctx.getSource().getPlayer() != null) {
                                                                        builder.suggest(Double.toString(ctx.getSource().getPlayer().getPos().y));
                                                                    }
                                                                }
                                                                case "z" -> {
                                                                    Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                    if (warp != null) {
                                                                        builder.suggest(Double.toString(warp.getPos().z));
                                                                    }
                                                                    if (ctx.getSource().getPlayer() != null) {
                                                                        builder.suggest(Double.toString(ctx.getSource().getPlayer().getPos().z));
                                                                    }
                                                                }
                                                                case "yaw" -> {
                                                                    Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                    if (warp != null) {
                                                                        builder.suggest(Float.toString(warp.getYaw()));
                                                                    }
                                                                    if (ctx.getSource().getPlayer() != null) {
                                                                        builder.suggest(Float.toString(ctx.getSource().getPlayer().getYaw()));
                                                                    }
                                                                }
                                                                case "pitch" -> {
                                                                    Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
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
                                                                Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                if (warp != null) {
                                                                    yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                            Double.parseDouble(StringArgumentType.getString(ctx, "value")), warp.getPos().y, warp.getPos().z, warp.getYaw(), warp.getPitch());
                                                                } else {
                                                                    ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                                    yield 1;
                                                                }
                                                            }
                                                            case "y" -> {
                                                                Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                if (warp != null) {
                                                                    yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                            warp.getPos().x, Double.parseDouble(StringArgumentType.getString(ctx, "value")), warp.getPos().z, warp.getYaw(), warp.getPitch());
                                                                } else {
                                                                    ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                                    yield 1;
                                                                }
                                                            }
                                                            case "z" -> {
                                                                Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                if (warp != null) {
                                                                    yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                            warp.getPos().x, warp.getPos().y, Double.parseDouble(StringArgumentType.getString(ctx, "value")), warp.getYaw(), warp.getPitch());
                                                                } else {
                                                                    ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                                    yield 1;
                                                                }
                                                            }
                                                            case "yaw" -> {
                                                                Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
                                                                if (warp != null) {
                                                                    yield updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                                            warp.getPos().x, warp.getPos().y, warp.getPos().z, Float.parseFloat(StringArgumentType.getString(ctx, "value")), warp.getPitch());
                                                                } else {
                                                                    ctx.getSource().sendMessage(Text.of("Warp not found"));
                                                                    yield 1;
                                                                }
                                                            }
                                                            case "pitch" -> {
                                                                Warp warp = WorldWarps.warpManager.getWarp(StringArgumentType.getString(ctx, "name"));
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
                                    updateWarp(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                            ctx.getSource().getPlayer().getPos().x, ctx.getSource().getPlayer().getPos().y, ctx.getSource().getPlayer().getPos().z,
                                            ctx.getSource().getPlayer().getYaw(), ctx.getSource().getPlayer().getPitch());
                                    return 1;
                                })
                        ));
    }


    private int tpwarp(ServerCommandSource source, String name) {
        Warp warp = WorldWarps.warpManager.getWarp(name);
        if (source.getPlayer() != null) {
            if (warp != null) {
                if (warp.isPublic() || warp.getOwner().equals(source.getPlayer().getUuid().toString())) {
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
        if (source.getPlayer() == null) {
            return 1;
        }
        String world = source.getWorld().getRegistryKey().getValue().toString();
        WorldWarps.warpManager.addWarp(new Warp(name, source.getPlayer().getUuid().toString(), false, source.getPlayer().getPos(), source.getPlayer().getYaw(), source.getPlayer().getPitch(), world));
        source.sendMessage(Text.of("Warp " + name + " created"));
        return 0;
    }

    private int createWarp(ServerCommandSource source, String name, double x, double y, double z, float yaw, float pitch, String world) {
        WorldWarps.warpManager.addWarp(new Warp(name, source.getPlayer().getUuid().toString(), false, new Vec3d(x, y, z), yaw, pitch, world));
        source.sendMessage(Text.of("Warp " + name + " created"));
        return 0;
    }

    private int removeWarp(ServerCommandSource source, String name) {
        WorldWarps.warpManager.removeWarp(name, source.getPlayer() != null ? source.getPlayer().getUuid().toString() : "CONSOLE");
        source.sendMessage(Text.of("Warp " + name + " removed"));
        return 0;
    }

    private int listWarps(ServerCommandSource source) {
        source.sendMessage(Text.of("Public warps:"));
        WorldWarps.warpManager.getPublicWarps().forEach(w -> source.sendMessage(Text.of(w.getName())));
        return 0;
    }

    private int listPWarps(ServerCommandSource source) {
        source.sendMessage(Text.of("Private warps:"));
        WorldWarps.warpManager.getWarpsByOwner(source.getPlayer() != null ? source.getPlayer().getUuid().toString() : "CONSOLE").forEach(w -> source.sendMessage(Text.of(w.getName())));
        return 0;
    }

    private int toggleWarpType(ServerCommandSource source, String name) {
        WorldWarps.warpManager.toggleWarpType(name, source.getPlayer() != null ? source.getPlayer().getUuid().toString() : "CONSOLE");
        source.sendMessage(Text.of("Warp " + name + " is now " + (WorldWarps.warpManager.getWarp(name).isPublic() ? "public" : "private")));
        return 0;
    }

    private int updateWarp(ServerCommandSource source, String name, double x, double y, double z, float yaw, float pitch) {
        String world = source.getWorld().getRegistryKey().getValue().toString();
        WorldWarps.warpManager.updateWarp(name, source.getPlayer() != null ? source.getPlayer().getUuid().toString() : "CONSOLE", new Vec3d(x, y, z), yaw, pitch, world);
        source.sendMessage(Text.of("Warp " + name + " updated"));
        return 0;
    }
}
