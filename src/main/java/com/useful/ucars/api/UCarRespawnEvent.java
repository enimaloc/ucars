package com.useful.ucars.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Called when a new car is made in place of an old, identical one.
 * Eg. called when using teleport blocks.
 *
 * @author storm345
 */
public class UCarRespawnEvent extends Event implements Cancellable {
    private final Entity newCar;
    private final UUID oldId;
    private final UUID newId;
    private final CarRespawnReason reason;
    private final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;


    /**
     * Called when a new car is made in place of an old, identical one.
     * Eg. called when using teleport blocks.
     *
     * @param newCar The new Car Entity.
     * @param oldId  The UUID of the old Car entity.
     * @param newId  The UUID of the new Car entity.
     */
    public UCarRespawnEvent(Entity newCar, UUID oldId, UUID newId, CarRespawnReason reason) {
        this.newCar = newCar;
        this.oldId = oldId;
        this.newId = newId;
        this.reason = reason;
    }

    /**
     * Get's the new car entity.
     *
     * @return The new car entity.
     */
    public Entity getNewCar() {
        return newCar;
    }

    /**
     * Get's the old car entity's UUID
     *
     * @return The old car entity's UUID
     */
    public UUID getOldEntityId() {
        return oldId;
    }

    /**
     * Get's the new car entity's UUID
     *
     * @return The new car entity's UUID
     */
    public UUID getNewEntityId() {
        return newId;
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

    public CarRespawnReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
