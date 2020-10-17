package com.useful.ucars;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CartOrientationUtil {
    private static CartOrientationUtilOverride cartOrientationUtilOverride = null;

    public static void setCartOrientationUtilOverride(CartOrientationUtilOverride override) {
        CartOrientationUtil.cartOrientationUtilOverride = override;
    }

    public static void setRoll(Entity cart, float roll) {
        if (cartOrientationUtilOverride != null) {
            cartOrientationUtilOverride.setRoll(cart, roll);
        }
    }

    public static void setPitch(Entity cart, float pitch) {
        if (cartOrientationUtilOverride != null) {
            cartOrientationUtilOverride.setPitch(cart, pitch);
            return;
        }
        if (!(cart instanceof Minecart)) {
            throw new RuntimeException("Non Minecart cars not supported yet!");
        }
        try {
            Class<?> cmr = cart.getClass();
            Method getHandle = cmr.getMethod("getHandle");
            Class<?> ema = Reflect.getNMSClass("EntityMinecartAbstract");
            Object nmsCart = getHandle.invoke(cmr.cast(cart));
            Field p = ema.getField("pitch");
            p.setAccessible(true);
            p.set(ema.cast(nmsCart), -pitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setYaw(Entity cart, float yaw) {
        if (cartOrientationUtilOverride != null) {
            cartOrientationUtilOverride.setYaw(cart, yaw);
            return;
        }
        if (!(cart instanceof Minecart)) {
            throw new RuntimeException("Non Minecart cars not supported yet!");
        }
        try {
            Class<?> cmr = cart.getClass();
            Method getHandle = cmr.getMethod("getHandle");
            Class<?> ema = Reflect.getNMSClass("EntityMinecartAbstract");
            Object nmsCart = getHandle.invoke(cmr.cast(cart));
            Field p = ema.getField("yaw");
            p.setAccessible(true);
            p.set(ema.cast(nmsCart), yaw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CartOrientationUtilOverride {
        void setPitch(Entity cart, float pitch);

        void setYaw(Entity cart, float yaw);

        void setRoll(Entity cart, float roll);
    }
}
