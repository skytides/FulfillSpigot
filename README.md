# FulfillSpigot [![Discord](https://img.shields.io/discord/1294830969626558465?label=Discord)](https://discord.gg/QMAHrmDhrF)

### This project is now abandoned

This project has been officially abandoned due to numerous unresolved bugs and the lack of active maintainers.

Discord Server: https://discord.gg/QMAHrmDhrF

## FulfillSpigot

FulfillSpigot brings significant performance upgrades and bug fixes, optimizing server stability even with high player counts. While we haven't conducted formal benchmarks yet, the server has already handled over 100 real players and 1000+ bots while maintaining consistent 20 TPS.

FulfillSpigot has also been thoroughly tested on a practice server.

## Download
The latest stable release can be downloaded from the releases section.
API can be downloaded from the discord server

## Building

To compile FulfillSpigot, you'll need:

- **JDK 8** (or above)
- mvn clean install (from maven).

## Patches
**All credit goes to the people that made these patches.**<br>
*Ensure all credits are properly given if you decide to use these in your own projects!*
```
[FulfillSpigot-0001] Range reduction fully configurable knockback
[FulfillSpigot-0002] Optimized Hit detection
[FulfillSpigot-0003] Proper block ticking configurable
[FulfillSpigot-0004] Optimized Packet Timestamp Handling for improved hit registration and processing (PacketPlayInArmAnimation)
[FulfillSpigot-0005] FastUtil for PlayerChunkMap for faster chunk loading
[FulfillSpigot-0006] Remove Spigot metrics
[FulfillSpigot-0007] Configurable tab packet spam.disconnect
[FulfillSpigot-0009] Heavily optimize RegionFile and RegionFileCache
[FulfillSpigot-0010] Faster packets
[FulfillSpigot-0011] Optimize BiomeDecorator
[FulfillSpigot-0012] Minor getCubesEntity() optimization
[FulfillSpigot-0013] Queue and teleport entities after all worlds have been ticked
[FulfillSpigot-0014] Backport FileIO thread from paper
[FulfillSpigot-0015] Dont parse itemstacks
[FulfillSpigot-0016] Fix untracked and tracked player memory leaks
[FulfillSpigot-0017] Configurable hit delay
[FulfillSpigot-0018] Fix eat while running bug configurable
[FulfillSpigot-0019] Max cache size limit for RegionFileCache
[FulfillSpigot-0020] Knockback Command and Knockback Reload command
[FulfillSpigot-0021] Loaded chunks command
[FulfillSpigot-0022] Remove automatic chunk unloading (FulfillSpigot-0025)
[FulfillSpigot-0023] Remove dead NMS code

[PandaSpigot-0015] Configuration Patch
[PandaSpigot-0004] Backport modern tick loop
[PandaSpigot-0005] Backport handshake event
[PandaSpigot-0010] Avoid blocking on network manager creation
[PandaSpigot-0019] Smooth-teleportation.patch
[PandaSpigot-0020] Configurable-data-saving.patch
[PandaSpigot-0021] Set-cap-on-JDK-per-thread-native-byte-buffer-cache.patch
[PandaSpigot-0022] Branchless-NibbleArray.patch
[PandaSpigot-0023] EntityMoveEvent.patch
[PandaSpigot-0024] Player-Chunk-Load-Unload-Events.patch
[PandaSpigot-0025] Configurable-entity-AI.patch
[PandaSpigot-0026] Fix-SPIGOT-2380.patch
[PandaSpigot-0027] Use-a-Shared-Random-for-Entities.patch
[PandaSpigot-0028] Reduce-IO-ops-opening-a-new-region-file.patch
[PandaSpigot-0029] Optimize-World.isLoaded-BlockPosition-Z.patch
[PandaSpigot-0030] Cleanup-allocated-favicon-ByteBuf.patch
[PandaSpigot-0031] Optimize-BlockPosition-helper-methods.patch
[PandaSpigot-0032] Configurable-arrow-trajectory.patch
[PandaSpigot-0033] Prevent-fishing-hooks-from-using-portals.patch
[PandaSpigot-0034] Cache-user-authenticator-threads.patch
[PandaSpigot-0035] Optimize-Network-Queue.patch
[PandaSpigot-0036] Backport-PlayerProfile-API.patch
[PandaSpigot-0037] Sound-events.patch
[PandaSpigot-0038] Optimize-VarInt-reading-and-writing.patch

[????-????] Better tablist API and fix tcp overhead

[Spigot-0097] Remove DataWatcher Locking by spottedleaf
[Spigot-0138] Branchless NibbleArray by md5
[Spigot-2380] Hitting in the air will always load the chunk at 0,0 by md_5

[Paper-0021] Implement Paper VersionChecker
[Paper-0033] Optimize explosions
[Paper-0044] Use UserCache for player heads
[Paper-0072] Fix Furnace cook time bug when lagging by Aikar
[Paper-0076] Optimized Light Level Comparisons by Aikar
[Paper-0083] Waving banner workaround by Gabscap
[Paper-0068] Use a Shared Random for Entities by Aikar
[Paper-0085] Add handshake event to allow plugins to handle client handshaking logic themselves
[Paper-0093] Don't save empty scoreboard teams to scoreboard.dat by Aikar
[Paper-0097] Faster redstone torch rapid clock removal by Martin Panzer
[Paper-0100] Avoid blocking on Network Manager creation by Aikar
[Paper-0103] Add setting for proxy online mode status
[Paper-0112] Reduce IO ops opening a new region file by Antony Riley
[Paper-0122] Don't let fishinghooks use portals by Zach Brown
[Paper-0125] Optimize World.isLoaded(BlockPosition)Z by Aikar
[Paper-0125] Improve Maps (in item frames) performance and bug fixes by Aikar
[Paper-0141] Do not let armorstands drown
[Paper-0144] Improve Minecraft Hopper Performance by  Aikar
[Paper-0152] Disable ticking of snow blocks by killme
[Paper-0164] [MC-117075] TE Unload Lag Spike by mezz
[Paper-0168] Cache user authenticator threads by vemacs
[Paper-0207] Shame on you Mojang moves chunk loading off https thread by Aikar
[Paper-0249] Improve BlockPosition inlining by Techcable
[Paper-0254] Don't blindly send unlit chunks when lighting updates are allowed by Shane Freeder
[Paper-0266] [MC-99321] Dont check for blocked double chest for hoppers
[Paper-0301] Optimize Region File Cache
[Paper-0302] Don't load chunks for villager door checks by Aikar
[Paper-0313] Optimize World Time Updates by Aikar
[Paper-0321] Server Tick Events
[Paper-0342] Always process chunk removal in removeEntity by Aikar 2018
[Paper-0344] [MC-111480] Start Entity ID's at 1
[Paper-0346] [MC-135506] Experience should save as Integers
[Paper-0347] don't go below 0 for pickupDelay, breaks picking up items by Aikar
[Paper-0350] use a Queue for Queueing Commands by Aikar
[Paper-0352] Optimize BlockPosition helper methods by Spottedleaf
[Paper-0353] Send nearby packets from world player list not server list by Mystiflow
[Paper-0389] performance improvement for Chunk.getEntities by wea_ondara
[Paper-0539] Optimize NetworkManager Exception Handling by Andrew Steinborn
[Paper-0451] Reduce memory footprint of NBTTagCompound by spottedleaf
[Paper-0797] Use Velocity compression and cipher natives

[Nacho-0039] Fixed a bug in Netty's epoll when using Windows

[Yatopia-0047] Smarter statistics ticking
[Yatopia-0050] Smol entity optimisation

[IonSpigot-0003] Explosion Improvements
[IonSpigot-0006] Fix Chunk Loading

[InsanePaper-269] Cache Chunk Coordinations
[InsanePaper-390] Heavily optimize Tuinity controlled flush patch

[Akarin-0001] Avoid double I/O operation on load player file by tsao chi

[Tuinity-????] Skip updating entity tracker without players
[Tuinity-0017] Allow controlled flushing for network manager by Spottedleaf
[Tuinity-0018] Consolidate flush calls for entity tracker packets
[Tuinity-0052] Optimise non-flush packet sending

[SportPaper-0171] Fix NPE in CraftChunk#getBlocks

[KigPaper-0129] Fix more EnchantmentManager leaks
[KigPaper-0138] Fix some more memory leaks
[KigPaper-0161] Fix CraftingManager memory leak

[FlamePaper-0015] Patch Book Exploits
```

