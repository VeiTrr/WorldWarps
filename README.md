[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1093051?style=for-the-badge&logo=curseforge&logoColor=%230d0d0d&labelColor=%23f16436&color=%230d0d0d)](https://www.curseforge.com/minecraft/mc-mods/worldwarps)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/eYfW6K2F?style=for-the-badge&logo=modrinth&color=%231bd96a)](https://modrinth.com/mod/worldwarps)


# WorldWarps

WorldWarps is a Minecraft mod designed to manage warp points in the game, allowing players to create, remove, and teleport to various locations. This mod is especially useful for servers where players need to quickly navigate between different areas.

## Installation

1. Ensure you have Forge installed.
2. Download the latest version of WorldWarps from the [releases page](https://github.com/VeiTrr/WorldWarps/releases).
3. Place the downloaded .jar file into your `mods` folder.
4. Start your server or game.

## Commands

Write `/warp` in chat to see all available commands.

The main command provided by WorldWarps is `/warp`, which has several subcommands:

- `/warp <name>`: Teleports the player to the specified warp.
- `/warp list`: Lists all available warps.
- `/warp list public`: Lists all public warps.
- `/warp list personal`: Lists all personal warps.
- `/warp info <name>`: Displays information about the specified warp.

The `/warpmgr` command is used for managing warps:

- `/warpmgr create <name>`: Creates a new warp at the player's current location.
- `/warpmgr create <name> <x> <y> <z> <yaw> <pitch> <world>`: Creates a new warp at the specified coordinates.
- `/warpmgr create <name> <owner> <x> <y> <z> <yaw> <pitch> <world>`: Creates a new warp with the specified owner and coordinates.
- `/warpmgr remove <name>`: Removes the specified warp.
- `/warpmgr list`: Lists all warps.
- `/warpmgr list public`: Lists all public warps.
- `/warpmgr list personal`: Lists all personal warps.
- `/warpmgr list all`: Lists all warps.
- `/warpmgr visibility <name>`: Toggles the visibility of the specified warp.
- `/warpmgr update <name> <x> <y> <z> <yaw> <pitch>`: Updates the specified warp with new coordinates.

## Configuration

WorldWarps has a configuration file located at `world/serverconfig/worldwarps-server.toml`. Here are the available configuration options:

- `warpLimit`: The maximum number of warps a player can have. (WIP)
- `canWarpPermissionLevel`: The permission level required to warp.
- `canChangeTypePermissionLevel`: The permission level required to change warp type.
- `canCreateWarpPermissionLevel`: The permission level required to create a warp.
- `warpAdminPermissionLevel`: The permission level required to be a warp admin.

## Permissions

WorldWarps has a permissions integration, which allows you to set permissions for each command. Here are the permissions:

- `worldwarps.can_warp`: Allows the player to use the `/warp` command.
- `worldwarps.can_create_warp`: Allows the player to use the `/warpmgr create` command.
- `worldwarps.can_change_type`: Allows the player to use the `/warpmgr visibility` command.
- `worldwarps.warp_admin`: Allows the player to use admin-level warp commands.

## License

WorldWarps is licensed under the MIT License. See the `LICENSE` file for more details.