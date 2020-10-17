package com.useful.ucars;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

public class UCarUpdateEvent extends VehicleUpdateEvent implements Cancellable {
    public Vector toTravel;
    public Boolean changePlayerYaw = false;
    public float yaw = 90;
    public Boolean doDivider = false;
    ;
    public double divider = 1;
    public Boolean cancelled = false;
    public Player player = null;
    private int readCount = 0;
    private CarDirection dir;

    public UCarUpdateEvent(Vehicle vehicle, Vector toTravel, Player player, CarDirection dir) {
        super(vehicle);
        this.toTravel = toTravel;
        this.player = player;
        this.dir = dir;
    }

    public void setRead(int r) {
        this.readCount = r;
    }

    public void incrementRead() {
        readCount++;
    }

    public int getReadCount() {
        return readCount;
    }

    public Player getPlayer() {
        return player;
    }

    public Vector getTravelVector() {
        return this.toTravel;
    }

    public Boolean getChangePlayerYaw() {
        return this.changePlayerYaw;
    }

    public void setChangePlayerYaw(Boolean change) {
        this.changePlayerYaw = change;
    }

    public Boolean getDoDivider() {
        return this.doDivider;
    }

    public void setDoDivider(Boolean doDivider) {
        this.doDivider = doDivider;
    }

    public double getDivider() {
        return this.divider;
    }

    public void setDivider(double divider) {
        this.divider = divider;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean arg0) {
        this.cancelled = arg0;
    }

    public CarDirection getDir() {
        return dir;
    }

    public void setDir(CarDirection dir) {
        this.dir = dir;
    }

}
