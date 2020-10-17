package com.useful.ucars.common;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class StatValue implements MetadataValue {
    public Object value;
    public Plugin plugin;

    public StatValue(Plugin plugin, Object value) {
        this.value = value;
        this.plugin = plugin;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Object value() {
        return this.value;
    }

    @Override
    public Plugin getOwningPlugin() {
        return plugin;
    }

    @Override
    public int asInt() {
        try {
            return value instanceof Integer ?
                    (int) value :
                    value instanceof String ?
                            Integer.parseInt((String) value) :
                            0;
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }

    @Override
    public float asFloat() {
        try {
            return value instanceof Float ?
                    (float) value :
                    value instanceof String ?
                            Float.parseFloat((String) value) :
                            0;
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }

    @Override
    public double asDouble() {
        try {
            return value instanceof Double ?
                    (double) value :
                    value instanceof String ?
                            Double.parseDouble((String) value) :
                            0;
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }

    @Override
    public long asLong() {
        try {
            return value instanceof Long ?
                    (long) value :
                    value instanceof String ?
                            Long.parseLong((String) value) :
                            0;
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }

    @Override
    public short asShort() {
        try {
            return value instanceof Short ?
                    (short) value :
                    value instanceof String ?
                            Short.parseShort((String) value) :
                            0;
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }

    @Override
    public byte asByte() {
        try {
            return value instanceof Byte ?
                    (byte) value :
                    value instanceof String ?
                            Byte.parseByte((String) value) :
                            0;
        } catch (NumberFormatException ignored) {
        }
        return 0;
    }

    @Override
    public boolean asBoolean() {
        return value instanceof Boolean ?
                (boolean) value :
                value instanceof String && Boolean.parseBoolean((String) value);
    }

    @Override
    public String asString() {
        return value instanceof String ?
                (String) value :
                String.valueOf(value);
    }

    @Override
    public void invalidate() {
    }
}
