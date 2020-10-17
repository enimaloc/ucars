package com.useful.ucars;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UCarDeathEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    public Boolean cancelled = false;
    private final Entity car;
    private final Player player;

    public UCarDeathEvent(Entity vehicle) {
        this(vehicle, null);
    }

    public UCarDeathEvent(Entity vehicle, Player whoKilled) {
        this.car = vehicle;
        this.player = whoKilled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayerWhoKilled() {
        return player;
    }

    public boolean didPlayerKill() {
        return player != null;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not be executed in the server, but will still pass to other plugins
     *
     * @param cancelled true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Entity getCar() {
        return car;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
