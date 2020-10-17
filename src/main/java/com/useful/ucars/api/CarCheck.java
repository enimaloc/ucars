package com.useful.ucars.api;

import org.bukkit.entity.Entity;

/**
 * Provides an interface to add custom checking to cars
 *
 * @author storm345
 */
public interface CarCheck {
    /**
     * Called to check if a (uCars checked and valid) car is a car (According to
     * your plugin)
     *
     * @param car The uCars-valid car
     * @return true if it is a car
     */
    boolean isACar(Entity car);
}
