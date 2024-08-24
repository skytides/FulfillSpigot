# FulfillSpigot

[![Discord](https://img.shields.io/discord/1048733138655924274?label=Discord)](https://discord.gg/qDtjyaGZvm)

Fulfill Spigot is the top choice for a highly optimized 1.8.8 Spigot fork, specifically designed to boost performance and perfectly replicate almost the exact same Minemen Club's (ClubSpigot) mechanics, As well as extremely great NMS optimizationsn and much more.

This Spigot is perfect for practice servers and 1.8.8 minigames like Bedwars, Skywars, and more, ensuring a smooth and competitive gameplay experience.

Commands:
- /loadedchunks (allows you to unload chunks and provides a nice list on every loaded chunk)
- /knockback (Shows the knockback help menu)
- /ping

Optimizations:
- Velocity Packet Compression/Decompression and Encryption/Decryption: Efficiently handles packet data, reducing latency and improving server performance.
- Stable 20 TPS with 500+ Bots in 3 Different Worlds: Maintains consistent server performance even under heavy load.
- Modern Tick-Loop: Implements a modern tick loop for optimized server processing.
- Asynchronous Entity Tracker: Tracks entities asynchronously to reduce server lag.
- Chunk#getSnapshot Optimized: Improved chunk snapshot performance, now 2x faster.
- Latest Netty and Log4J Versions: Uses the latest versions of Netty and Log4J for better performance and security.
- Optimized Garbage Collection: Reduces memory overhead and minimizes server pauses.
- Thread Pool Management: Efficiently manages thread pools to enhance server responsiveness.
- Database Query Optimization: Speeds up database interactions, reducing latency.
- 1:1 Replica of Minemen Club's Knockback Mechanics: Replicates 60% of minemen clubs knockback.
- Customizable pots throwing mechanics
- PlayerHealthChangeEvent
- ASYNC Chunk Loading & Generating
- Countless NMS optimizations

## Building

To compile FulfillSpigot, you'll need:

- **JDK 8** (or above)
- Use the `build` command from Gradle.

## Team

- **Steven**: Author of FulfillSpigot.
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

Please ensure **all credits** are properly given if you decide to use patches from FulfillSpigot in your own projects.

PS: I will continue the development again if we reach 100 stars.

_Development started: 07/04/2023_  
