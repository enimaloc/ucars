package com.useful.ucars;

import com.useful.ucars.common.StatValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CarHealthData extends StatValue {
    private double health;

    public CarHealthData(Plugin plugin, double health) {
        super(plugin, health);
        this.health = health;
        this.plugin = plugin;
    }

    @Override
    public int asInt() {
        return (int) Math.floor(health + 0.5f);
    }

    @Override
    public long asLong() {
        return Math.round(health);
    }

    @Override
    public void invalidate() {
        health = 0;
    }

    @Override
    public Object value() {
        return health;
    }

    public void damage(double amount, Entity carEntity) {
        health = ((int) this.health - amount);
        if (health <= 0) {
            die(carEntity);
        }
    }

    public void damage(double amount, Entity carEntity, Player whoHurt) {
        health = ((int) this.health - amount);
        if (health <= 0) {
            die(carEntity, whoHurt);
        }
    }

    public double getHealth() {
        return this.health;
    }

    public void setHealth(double amount) {
        this.health = ((int) amount);
    }

    public void die(Entity m, Player whoHurt) {
        if (m == null || !m.isValid() || m.isDead()) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new UCarDeathEvent(m, whoHurt));
    }

    public void die(Entity carEntity) {
        if (carEntity == null || !carEntity.isValid() || carEntity.isDead()) {
            return;
        }
        Bukkit.getPluginManager().callEvent(new UCarDeathEvent(carEntity));
    }
}
