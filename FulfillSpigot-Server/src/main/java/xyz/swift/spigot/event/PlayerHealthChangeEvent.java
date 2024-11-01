package xyz.swift.spigot.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerHealthChangeEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final double previousHealth;
    private final double newHealth;

    public PlayerHealthChangeEvent(Player who, double previousHealth, double newHealth) {
        super(who);

        this.previousHealth = previousHealth;
        this.newHealth = newHealth;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public double getPreviousHealth() {
        return previousHealth;
    }

    public double getNewHealth() {
        return newHealth;
    }
}

