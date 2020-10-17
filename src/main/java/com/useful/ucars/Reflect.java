package com.useful.ucars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflect {

    public static String version;
    public static boolean newProtocol;

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String minecraftVersion = name.substring(name.lastIndexOf('.') + 1);
        String[] versions = minecraftVersion.split("_");

        if (versions[0].equals("v1") && Integer.parseInt(versions[1]) > 6) {
            newProtocol = true;
        }

        version = minecraftVersion + ".";
    }

    // Reflection Util
    public static void sendPacket(Player p, Object packet) {
        try {
            Object nmsPlayer = getHandle(p);
            Field connectionField = nmsPlayer.getClass().getField("playerConnection");
            Object connection = connectionField.get(nmsPlayer);
            Method sendPacketMethod = getMethod(connection.getClass(), "sendPacket");
            sendPacketMethod.invoke(connection, packet);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String className) {
        className = "net.minecraft.server." + version + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Class<?> getCBClass(String className) {
        className = "org.bukkit.craftbukkit." + version + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Object getHandle(World world) {
        return getHandle((Object) world);
    }

    public static Object getHandle(Entity entity) {
        return getHandle((Object) entity);
    }

    private static Object getHandle(Object object) {
        if (!(object instanceof Entity || object instanceof World)) {
            return null;
        }
        Object nmsEntity = null;
        Method entityGetHandle = getMethod(object.getClass(), "getHandle");
        try {
            nmsEntity = entityGetHandle.invoke(object);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nmsEntity;
    }

    public static Field getField(Class<?> cl, String field_name) {
        try {
            Field field = cl.getDeclaredField(field_name);
            return field;
        } catch (SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>[] args) {
        for (Method clazzMethod : clazz.getMethods()) {
            if (clazzMethod.getName().equals(method) && classListEqual(args, clazzMethod.getParameterTypes())) {
                return clazzMethod;
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String method, Integer args) {
        for (Method clazzMethod : clazz.getMethods()) {
            if (clazzMethod.getName().equals(method) && args.equals(clazzMethod.getParameterTypes().length)) {
                return clazzMethod;
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String method) {
        for (Method clazzMethod : clazz.getMethods()) {
            if (clazzMethod.getName().equals(method)) {
                return clazzMethod;
            }
        }
        return null;
    }

    public static boolean classListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;

        if (l1.length != l2.length)
            return false;
        for (int i = 0; i < l1.length; i++) {
            if (l1[i] != l2[i]) {
                equal = false;
                break;
            }
        }

        return equal;
    }
}
