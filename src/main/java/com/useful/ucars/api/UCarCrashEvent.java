package com.useful.ucars.api;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UCarCrashEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Entity car;
    private Entity hit;
    private double damageToEntity;

    public UCarCrashEvent(Entity vehicle, Entity hit, double damageToEntity) {
        this.car = vehicle;
        this.hit = hit;
        this.damageToEntity = damageToEntity;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

    public Entity getEntityCrashedInto() {
        return this.hit;
    }

    public double getDamageToBeDoneToTheEntity() {
        return this.damageToEntity;
    }

    public void setDamageToBeDoneToTheEntity(double dmg) {
        this.damageToEntity = dmg;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
