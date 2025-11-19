## Mass Renaming Plugin  **Spigot/Paper/Purpur/Bukkit 1.21.x** [![Modrinth](https://img.shields.io/modrinth/dt/massrenamegui?logo=modrinth)](https://modrinth.com/mod/massrenamegui)

This is a Plugin i made for the StarnixMC Minecraft Server.

I found it annoying to always have to rename items one by one, especially Shulker Boxes or other unstackable Items.

This Plugin does exactly that and nothing more. 

I was astonished to not find a simple Plugin like that already.

### Usage

Simply drag and drop an Item into the Input Slot, put the Items you want to rename in the GUI above and, when you click the "Rename All" Button, the Items in the GUI will be renamed to whatever your Input Items Name is. Successfully tested with Legacy Colorcodes and HEX.

**New in Version 1.1:**

Copy the Lore of an Item additionally to the name!
Simply click the the Toggle Button in the GUI to enable/disable Lore duplication. (requires its own permission, see below)

### Commands and Permissions

As simple as the plugin itself are also its commands and permissions:

**Command**:

/massrename <reload>

**Permissions**:

massrename.use - Allows the Usage of the GUI, defaults to OP.

massrename.lore - Allows usage of new Lore Copy feature (version 1.1), defaults to OP

massrename.reload - Reloads the Config File, defaults to OP.

The Config consists of 3 Options: Gui Size(18-**54**), enable Lore Copy(**on**/off), and default Lore Copy Toggle state(on/**off**)

```
# Gui size, from 18-54, must be a multiple of 9. will need at least 2 rows to work
gui-size: 54

# Enables or disables the Lore Copy feature.
lore-copy-enabled: true

# Default lore copy state.
default-lore-copy: false

```

No bStats - Use it or don't, that's none of my business :D

**Minecraft Server Plug (shameless)**

Primary: play.starnixmc.xyz

Secondary: ind.starnixmc.xyz

Bedrock Port: 25565

> https://www.youtube.com/@StarnixMC
> 
> https://discord.gg/mxSPg9fxET
