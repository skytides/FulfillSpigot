package xyz.tavenservices.spigot.event.sound;


public interface KnockbackProfile {

    void save();

    void save(boolean projectiles);

    String getName();

    void setName(String name);
}
