package com.useful.ucars;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class PlaceManager {

    private static final UCars uCars = JavaPlugin.getPlugin(UCars.class);

    /**
     * Deprecated, use placeableOn(String materialName, byte data) instead.
     */
    @Deprecated
    public static boolean placeableOn(int id, byte data) {
        String materialName = Material.getMaterial(id).name().toUpperCase();
        return placeableOn(materialName, data);
    }

    public static boolean placeableOn(String materialName, byte data) {
        boolean placeable = false;
        if (!uCars.getConfig().getBoolean("general.cars.roadBlocks.enable")) {
            return true;
        }
        List<String> rBlocks = uCars.getConfig().getStringList("general.cars.roadBlocks.ids");
        for (String raw : rBlocks) {
            if (ItemStackFromId.equals(raw, materialName, data)) {
                placeable = true; // Placing on a road block
            }
        }
        return placeable;
    }

}
