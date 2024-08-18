package xyz.zenithdev.spigot.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class FulfillSpigotWorldConfig {
    @Comment("How many ticks in between sending time updates to players?\n" +
        "\n" +
        "The vanilla option is 20 (every second), but PandaSpigot sets the default\n" +
        "to 100 (every 5 seconds). You would probably be fine setting this even\n" +
        "higher, unless you're constantly changing the time, or the server is lagging.")
    public int timeUpdateFrequency = 100;

    @Comment("This option makes it so that when players are teleported to a location\n" +
        "with the same rotation they currently have, the server will send a special\n" +
        "packet indicating that the client should not update it's rotation at all.\n" +
        "\n" +
        "For example, normally constantly teleporting a player to their own location\n" +
        "will make moving their head very difficult, especially for players with higher latency.\n" +
        "With this option enabled, they will be able to move their head just like normal.")
    public boolean smoothTeleportation = false;

    @Comment("When enabled, this option disables reading and writing player data such as:\n" +
        "- Position\n" +
        "- Inventory\n" +
        "- Enderchest")
    public boolean disablePlayerData = false;

    @Comment("When enabled, this option will disable saving world chunks.")
    public boolean disableChunkSaving = false;

    @Comment("When enabled, entity AI will be disabled.\n" +
        "\n" +
        "This has the same effect as Spigot's \"nerf-spawner-mobs\" option, but applies to all entities.")
    public boolean disableEntityAi = false;

    @Comment("This option controls whether or not to add a bit of randomness to an arrow's trajectory.\n" +
        "By default, this is true (vanilla behaviour)")
    public boolean randomArrowTrajectory = true;

    public boolean optimizeTntMovement = false; // May not fully emulate vanilla behavior

    public boolean optimizeLiquidExplosions = true; // This seems like a pretty sane default

    public boolean optimizeArmorStandMovement = false; // Doesn't fully emulate vanilla behavior, see TacoSpigot issue #1

    @Comment("This option controls whether or not there is a chance for arrow crits to deal extra damage.\n" +
        "By default, this is true (vanilla behaviour)")
    public boolean randomArrowDamage = true;

}
