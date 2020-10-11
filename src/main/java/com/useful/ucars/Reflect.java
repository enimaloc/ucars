package com.useful.ucars;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Reflect {
	
	public static String version;
	public static boolean newProtocol;
	
	static {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		String mcVersion = name.substring(name.lastIndexOf('.') + 1);
		String[] versions = mcVersion.split("_");

		if (versions[0].equals("v1") && Integer.parseInt(versions[1]) > 6) {
			newProtocol = true;
		}

		version = mcVersion + ".";
	}
	
	// Reflection Util
	public static void sendPacket(Player p, Object packet) {
			try {
				Object nmsPlayer = getHandle(p);
				Field connectionField = nmsPlayer.getClass().getField("playerConnection");
				Object connection = connectionField.get(nmsPlayer);
				Method sendPacketMethod = getMethod(connection.getClass(), "sendPacket");
				sendPacketMethod.invoke(connection, packet);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
	}
	
	public static Class<?> getNMSClass(String ClassName) {
		String className = "net.minecraft.server." + version + ClassName;
		Class<?> c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Class<?> getCBClass(String ClassName) {
		String className = "org.bukkit.craftbukkit." + version + ClassName;
		Class<?> c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return c;
	}

	public static Object getHandle(World world) {
		Object nmsEntity = null;
		Method entityGetHandle = getMethod(world.getClass(), "getHandle");
		try {
			nmsEntity = entityGetHandle.invoke(world);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return nmsEntity;
	}

	public static Object getHandle(Entity entity) {
		Object nmsEntity = null;
		Method entityGetHandle = getMethod(entity.getClass(), "getHandle");
		try {
			nmsEntity = entityGetHandle.invoke(entity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return nmsEntity;
	}

	public static Field getField(Class<?> cl, String field_name) {
		try {
			Field field = cl.getDeclaredField(field_name);
			return field;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
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
			if (clazzMethod.getName().equals(method) && args.equals(new Integer(clazzMethod.getParameterTypes().length))) {
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
