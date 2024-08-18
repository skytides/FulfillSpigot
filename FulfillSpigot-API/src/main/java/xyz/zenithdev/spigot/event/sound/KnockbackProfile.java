package xyz.zenithdev.spigot.event.sound;


public interface KnockbackProfile {

    void save();

    void save(boolean projectiles);

    String getName();

    void setName(String name);

    KnockbackType getType();
}
