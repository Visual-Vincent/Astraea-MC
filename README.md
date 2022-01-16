# Astraea Server Administration
A Minecraft Forge mod providing tools to aid the administration of your server.

This mod is server-side only. Clients do not need to have this mod installed in order to join your server (installing this mod on a client does nothing).

## Requirements
For running:
- Minecraft Forge Server 39.0.36 or higher (MC version 1.18.1)

For building:
- Java SE Development Kit 17 / OpenJDK 17 or higher

## Commands

- `<>` denotes required arguments
- `[]` denotes optional arguments

### Region Protection

Protected Regions are parts of the world that are owned by certain players only, and can therefore be used for grief protection. They are defined by a set of XYZ coordinates - a `Start` and `End` position - forming a cube or a cuboid in the 3D world.

| Command | Arguments | Description |
| ------- | --------- | ----------- |
| `/protectregion` | `<owner>`<br>`<protection_level>`<br>`<region name>` | Starts drawing a protected region at the player's current XYZ coordinates |
| `/cancelprotect` | (none) | Cancels a protected region drawing started by `/protectregion` |
| `/endprotect` | (none) | Finishes a protected region drawing at the player's current XYZ coordinates |
| `/unprotectregion` | (none) | Removes the currently protected region |
| `/regiontrust` | `<player>` | Give a player access to the current protected region (considers them 'trusted') |
| `/regiondistrust` | `<player>` | Revoke access to the current protected region for a previously trusted player |
| `/regioninfo` | (none) | Displays info about the current protected region |
| `/regionplayers` | (none) | Displays a list of all players who have access to the current region |
| `/regionprotectionlevel` | `[new_level]` | Displays the current region's protection level, or changes it to the specified level |
| `/reloadprotectedregions` | (none) | Reloads all protected regions |

Protected Regions can only be created and modified by:
- Super Admins
- The player who owns the region (if they are OP)
- The player who created the region (if they are OP)
- Server Operators/"OPs" (create only)

#### Protection levels

| Protection level | Description |
| ---------------- | ----------- |
| `logging`        | The protected region is accessible to all players, but the actions of players, who are not explicitly added as 'trusted' to the region, will be logged in a logfile.
| `griefing`       | Untrusted players will not be able to place or break any blocks, nor interact with any containers such as chests or utility blocks (however, they will still be able to open doors, use buttons, etc.)

### Super Admins

Super Admins are considered 'trusted' in all protected regions and can also perform administrative tasks on them. Be careful to whom you assign this privilege.

Only the Server Console, RCON clients or other Super Admins have the ability to execute these commands.

| Command | Arguments | Description |
| ------- | --------- | ----------- |
| `/makesuperadmin`   | `<player>` | Make a player a Super Admin |
| `/removesuperadmin` | `<player>` | Revokes a player's Super Admin access |