package com.useful.ucars;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigVersionConverter {

    private static final UCars uCars = JavaPlugin.getPlugin(UCars.class);

    public static FileConfiguration convert(FileConfiguration config, double target) {
        uCars.getLogger().info("Converting config to new format...");
        double td = target * 10; //Target is in format n.n
        int t = (int) td;
        if (t == 11) {
            fromV16ToV17(config);
            config.set("misc.configVersion", 1.1); //Save that it has been converted
        }
        return config;
    }

    public static FileConfiguration fromV16ToV17(FileConfiguration config) {
        convertItemFormat(config, "general.cars.lowBoost");
        convertItemFormat(config, "general.cars.medBoost");
        convertItemFormat(config, "general.cars.highBoost");
        convertItemFormat(config, "general.cars.blockBoost");
        convertItemFormat(config, "general.cars.HighblockBoost");
        convertItemFormat(config, "general.cars.ResetblockBoost");
        convertItemFormat(config, "general.cars.jumpBlock");
        convertItemFormat(config, "general.cars.teleportBlock");
        convertItemFormat(config, "general.cars.trafficLights.waitingBlock");
        convertItemFormat(config, "general.cars.roadBlocks.ids");
        convertItemFormat(config, "general.cars.fuel.check");
        convertItemFormat(config, "general.cars.fuel.items.ids");
        convertItemFormat(config, "general.cars.barriers");
        convertSpeedModsFormat(config, "general.cars.speedMods");
        uCars.getLogger().info("Config successfully converted!");
        return config;
    }

    public static FileConfiguration convertItemFormat(FileConfiguration config, String configKey) {
        String[] rawIds = config.getString(configKey).split(",");
        List<String> newIds = convertItemsToNewFormat(rawIds);
        config.set(configKey, null); //Remove from config
        config.set(configKey, newIds); //Save as a stringList
        return config;
    }

    public static FileConfiguration convertSpeedModsFormat(FileConfiguration config, String configKey) {
        String[] rawIds = config.getString(configKey).split(",");
        List<String> newIds = convertSpeedModsToNewFormat(rawIds);
        config.set(configKey, null); //Remove from config
        config.set(configKey, newIds); //Save as a stringList
        return config;
    }

    public static List<String> convertItemsToNewFormat(String[] rawIds) {
        List<String> newIds = new ArrayList<String>();
        for (String raw : rawIds) {
            try {
                final String[] parts = raw.split(":");
                if (parts.length < 1) {
                } else if (parts.length < 2) {
                    final int id = Integer.parseInt(parts[0]);
                    Material material = Material.getMaterial(id);
                    newIds.add(material.name().toUpperCase());
                    continue; //Next iteration
                } else {
                    final int id = Integer.parseInt(parts[0]);
                    Material material = Material.getMaterial(id);
                    final int data = Integer.parseInt(parts[1]);
                    String newFormat = material.name().toUpperCase() + ":" + data;
                    newIds.add(newFormat);
                    continue;
                }
            } catch (Exception e) {
                //Incorrect format also
            }
            uCars.getLogger().info("Invalid config value: " + raw + ", skipping...");
        }
        return newIds;
    }

    public static List<String> convertSpeedModsToNewFormat(String[] rawIds) {
        List<String> newIds = new ArrayList<String>();
        for (String raw : rawIds) {
            try {
                String[] segments = raw.split(Pattern.quote("-"));
                final String[] parts = segments[0].split(":");
                String mod = segments[1];
                if (parts.length < 2) {
                    final int id = Integer.parseInt(parts[0]);
                    Material mat = Material.getMaterial(id);
                    newIds.add(mat.name().toUpperCase() + "-" + mod);
                    continue; //Next iteration
                } else {
                    final int id = Integer.parseInt(parts[0]);
                    Material mat = Material.getMaterial(id);
                    final int data = Integer.parseInt(parts[1]);
                    String newFormat = mat.name().toUpperCase() + ":" + data;
                    newIds.add(newFormat + "-" + mod);
                    continue;
                }
            } catch (Exception e) {
                //Incorrect format also
            }
            uCars.getLogger().info("Invalid config speed modifier: " + raw + ", skipping...");
        }
        return newIds;
    }
}
