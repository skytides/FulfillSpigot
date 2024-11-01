package xyz.swift.spigot.event.sound;


public interface KnockbackProfile {

    void save();

    void save(boolean projectiles);

    String getName();

    void setName(String name);
}
