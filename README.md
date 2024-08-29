# FulfillSpigot [![Discord](https://img.shields.io/discord/1276852858221887508?label=Discord)](https://discord.gg/qDtjyaGZvm)

Discord Server: https://discord.gg/qDtjyaGZvm

FulfillSpigot is the top choice for a highly optimized 1.8.8 Spigot fork, specifically designed to boost performance and perfectly replicate almost the exact same Minemen Club's (ClubSpigot) mechanics, As well as extremely great NMS optimizationsn and much more.

This Spigot is perfect for practice servers and 1.8.8 minigames like Bedwars, Skywars, and more, ensuring a smooth and competitive gameplay experience.

Commands:
- /loadedchunks (allows you to unload chunks and provides a nice list on every loaded chunk)
- /knockback (Shows the knockback help menu)
- /ping

Optimizations:
- Velocity packet compression and decompression, along with encryption and decryption, efficiently handles packet data, reducing latency and boosting server performance.
- The server maintains stable 20 TPS even with over 500 bots across three different worlds.
- A modern tick loop is implemented to optimize server processing.
- Entity tracking is done asynchronously to minimize server lag.
- Chunk snapshots are now processed twice as fast.
- The latest versions of Netty and Log4J are used for better performance and security.
- Garbage collection is optimized to reduce memory overhead and minimize server pauses.
- Thread pools are managed efficiently to enhance server responsiveness.
- Database queries are optimized to reduce latency (fully customizable).
- The knockback mechanics are similar to those found in MinemenClub, replicating 60% of their functionality.
- Potion throwing mechanics are customizable.
- Player health changes are handled through the PlayerHealthChangeEvent.
- Chunk loading and generation are done asynchronously.
- Countless NMS optimizations.

## Building

To compile FulfillSpigot, you'll need:

- **JDK 8** (or above)
- Use the `build` command from Gradle.

## Team

- **Tavius**: Author of FulfillSpigot.
- **Savrien**: Original creator of the knockback system and a valued member of our development team.
- **Venyo**: Maintainer.

## Patch Credits

FulfillSpigot includes patches from other spigots as well:

- **Panda**
- **KigPaper**
- **Tuinity**
- **FlamePaper**
- **FulfillSpigot** (containing its own custom patches)

- Note: The ServerConnection class is from another Spigot, though I can't remember its name. This class was given to me by someone who may own that Spigot, so credit goes to them.

To get the FulfillSpigot patches, please check out the patches branch.

Special thanks to Bukkit, Spigot and Paper!

Please ensure **all credits** are properly given if you decide to use patches from FulfillSpigot in your own projects.

PS: I will continue the development again if we reach 100 stars.

_Development started: 07/04/2023_  
