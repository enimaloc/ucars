package com.useful.ucars;

import com.useful.ucars.api.UCarsAPI;
import com.useful.ucars.common.StatValue;
import com.useful.ucars.controls.ControlScheme;
import com.useful.ucars.controls.ControlSchemeManager;
import com.useful.ucars.util.UEntityMeta;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MotionManager {

    private static final UCars uCars = JavaPlugin.getPlugin(UCars.class);

    public static Vector rotateXZVector3dDegrees(Vector original, double degrees) {
        double[] out = rotateVector2dRadians(original.getX(), original.getZ(), Math.toRadians(degrees));
        original.setX(out[0]);
        original.setZ(out[1]);
        return original;
    }

    public static double[] rotateVector2dDegrees(double x, double y, double degrees) {
        return rotateVector2dRadians(x, y, Math.toRadians(degrees));
    }

    public static double[] rotateVector2dRadians(double x, double y, double radians) {
        double[] result = new double[2];
        result[0] = x * Math.cos(radians) - y * Math.sin(radians);
        result[1] = x * Math.sin(radians) + y * Math.cos(radians);
        return result;
    }

    public static void move(Player player, float f, float s, boolean jumping) { // 'f' and 's' are values taken in by the vehicle control packet
        Vector vector;
        Entity entity = player.getVehicle();
        if (entity == null) {
            return;
        }
        while (!(entity instanceof Vehicle) && entity.getVehicle() != null) {
            entity = entity.getVehicle();
        }
        if (!uCars.getListener().inACar(player) || !(entity instanceof Vehicle)) {
            return;
        }
        final Vehicle car = (Vehicle) entity;
        if (!player.equals(car.getPassengers().get(0)) || car.getPassengers().size() != 1) {
            return; //Only allow 1 driver
        }
        // Location loc = car.getLocation();
        // Vector carD = loc.getDirection();
        Vector playerDirection = player.getEyeLocation().getDirection();

        if (jumping) {
            if (!UEntityMeta.hasMetadata(player, "ucarsToggleControls")) {
                /*player.setMetadata("ucarsToggleControls", new StatValue(true, ucars.plugin));*/
                UEntityMeta.setMetadata(player, "ucarsToggleControls", new StatValue(uCars, true));
                if (ControlSchemeManager.isControlsLocked(player)) {
                    player.sendMessage(uCars.getUnsafe().colors.getError() + "Cannot toggle control scheme right now! (It's been locked by another plugin)");
                } else {
                    ControlSchemeManager.toggleControlScheme(player);
                    if (!uCars.isTurningCircles() && ControlSchemeManager.getScheme(player).equals(ControlScheme.KEYBOARD)) {
                        UEntityMeta.removeMetadata(car, "ucarsSteeringDir");
                        UEntityMeta.setMetadata(car, "ucarsSteeringDir", new StatValue(uCars, playerDirection.clone().setY(0).normalize()));
                    }
                }
            }
        } else { // !jumping
            if (UEntityMeta.hasMetadata(player, "ucarsToggleControls")) {
                UEntityMeta.removeMetadata(player, "ucarsToggleControls");
            }
        }

        ControlScheme controls = ControlSchemeManager.getScheme(player);
        boolean keyboardSteering = controls.equals(ControlScheme.KEYBOARD);

        Vector carDirection;
        carDirection = getVector(car, null);
        if (keyboardSteering || uCars.isTurningCircles()) {
            carDirection = getVector(car, carDirection);
        }

        CarDirection dir = CarDirection.NONE;

        if (f == 0 && !uCars.isSmoothDrive()) {
            return;
        }

        boolean inAir = car.getLocation().clone().add(0, -1, 0).getBlock().isEmpty();
        if (uCars.isSmoothDrive() && inAir) {
            f = 0;
            s = 0;
        } else if (uCars.isSmoothDrive()) {
            //Not in air
            ControlInput.setFirstAirTime(player, System.currentTimeMillis());
        }

        boolean forwards = f > 0; // if true, forwards, else backwards
        boolean turning = s != 0;
        int side = s > 0 ? -1 : s < 0 ? 1 : 0; // -1=left, 0=straight, 1=right

        //player.sendMessage(""+ControlInput.getCurrentAccel(player)+" "+ControlInput.getCurrentDriveDir(player));

        long timeSinceOnGround = System.currentTimeMillis() - ControlInput.getFirstAirTime(player);

        double y = -0.1 + (-0.003 * timeSinceOnGround); // rough gravity of minecraft
        if (y < -1) {
            y = -1;
        }
        double d = 27;
        boolean doDivider = false;
        boolean doAction = false;
        double divider = 0.5; // x of the (1) speed
        double rotMod = UCarsAPI.getAPI().getMaxCarTurnAmountDegrees(car, 5);
        if (turning) {
            if (side < 0) {// do left action
                if (!keyboardSteering) {
                    doAction = true;
                    UEntityMeta.setMetadata(car, "car.action", new StatValue(uCars, true));
                } else {
                    rotateXZVector3dDegrees(carDirection, ControlInput.getCurrentDriveDir(player).equals(CarDirection.BACKWARDS) ? rotMod : -rotMod);
                }
            } else if (side > 0) {// do right action
                if (!keyboardSteering) {
                    doDivider = true;
                    UEntityMeta.setMetadata(car, "car.action", new StatValue(uCars, true));
                } else {
                    rotateXZVector3dDegrees(carDirection, ControlInput.getCurrentDriveDir(player).equals(CarDirection.BACKWARDS) ? -rotMod : rotMod);
                }
            }
        }
        if (!keyboardSteering && uCars.isTurningCircles() && (!uCars.isSmoothDrive() || !inAir)) {
            //Rotate 'carDirection' vector according to where they're looking; max of rotMod degrees
            float pYaw = (float) Math.toDegrees(Math.atan2(playerDirection.getX(), -playerDirection.getZ())); //Calculate yaw from 'player direction' vector
            float cYaw = (float) Math.toDegrees(Math.atan2(carDirection.getX(), -carDirection.getZ())); //Calculate yaw from 'carDirection' vector
            /*if(ControlInput.getCurrentDriveDir(player).equals(CarDirection.BACKWARDS)*//* && ControlInput.getCurrentAccel(player) > 0*//*){
				pYaw += 180;
			}*/
            float yawDiff = pYaw - cYaw;
            if (yawDiff <= -180) {
                yawDiff += 360;
            } else if (yawDiff > 180) {
                yawDiff -= 360;
            }
            /*Bukkit.broadcastMessage(yawDiff+"");*/
            if (yawDiff < -rotMod) {
                yawDiff = (float) -rotMod;
            } else if (yawDiff > rotMod) {
                yawDiff = (float) rotMod;
            }
            rotateXZVector3dDegrees(carDirection, yawDiff/*ControlInput.getCurrentDriveDir(player).equals(CarDirection.BACKWARDS) ? -yawDiff : yawDiff*/);
        }
        if (keyboardSteering || uCars.isTurningCircles()) {
            UEntityMeta.removeMetadata(car, "ucarsSteeringDir");
            UEntityMeta.setMetadata(car, "ucarsSteeringDir", new StatValue(uCars, carDirection.normalize()));
            playerDirection = carDirection.clone();
        }
        double x = playerDirection.getX() / d;
        double z = playerDirection.getZ() / d;
        if (!doDivider) {
            if (UEntityMeta.hasMetadata(car, "car.braking")) {
                UEntityMeta.removeMetadata(car, "car.braking");
            }
        }
        if (forwards) {
            if (!doAction) {
                if (UEntityMeta.hasMetadata(car, "car.action")) {
                    UEntityMeta.removeMetadata(car, "car.action");
                }
            }
        } else {
            x = 0 - x;
            z = 0 - z;
        }
        vector = new Vector(x, y, z);
        final UCarUpdateEvent event = new UCarUpdateEvent(car, vector, player, dir);
        event.setDoDivider(doDivider);
        event.setDivider(divider);
        Bukkit.getScheduler().runTask(uCars, () -> ControlInput.input(car, event));
    }

    private static Vector getVector(Vehicle car, Vector carDirection) {
        try {
            if (UEntityMeta.hasMetadata(car, "ucarsSteeringDir")/*car.hasMetadata("ucarsSteeringDir")*/) {
                carDirection = (Vector) UEntityMeta.getMetadata(car, "ucarsSteeringDir")/*car.getMetadata("ucarsSteeringDir")*/.get(0).value();
            }
        } catch (Exception e) {
            carDirection = null;
        }
        if (carDirection == null) {
            carDirection = car.getLocation().getDirection();
            //carDirection = playerDirection.clone().setY(0).normalize();
        }
        return carDirection;
    }

}
