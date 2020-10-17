package com.useful.ucars;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ItemStackFromId {
    public static ItemStack get(String raw) {
        String[] parts = raw.split(":");
        String m = parts[0];
        Material mat = Material.getMaterial(m);
        if (mat == null) {
            JavaPlugin.getPlugin(UCars.class).getLogger().info("[WARNING] Invalid config value: " + raw + " (" + m + ")");
            return new ItemStack(Material.STONE);
        }
        short data = 0;
        boolean hasdata = false;
        if (parts.length > 1) {
            hasdata = true;
            data = Short.parseShort(parts[1]);
        }
        ItemStack item = new ItemStack(mat);
        if (hasdata) {
            item.setDurability(data);
        }
        return item;
    }

    public static boolean equals(String rawId, String materialName, int tdata) {
        String[] parts = rawId.split(":");
        String m = parts[0];
        int data = 0;
        boolean hasData = false;
        if (parts.length > 1) {
            hasData = true;
            data = Integer.parseInt(parts[1]);
        }
        return materialName.equalsIgnoreCase(m) && !hasData && tdata != data;
    }

    public static boolean equals(List<String> rawIds, String materialName, int tdata) {
        boolean match = false;
        for (String id : rawIds) {
            if (match || equals(id, materialName, tdata)) {
                match = true;
            }
        }
        return match;
    }
}
