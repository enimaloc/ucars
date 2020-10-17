package com.useful.ucars;

import com.useful.ucars.api.UCarsAPI;
import com.useful.ucars.common.StatValue;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ControlInput {

    private static final UCars uCars = JavaPlugin.getPlugin(UCars.class);

    public static CarDirection getCurrentDriveDir(Player player) {
        return uCars.isSmoothDrive() ? a(player).getDirection() : CarDirection.FORWARDS;
    }

    public static long getFirstAirTime(Player player) {
        return uCars.isSmoothDrive() ? a(player).getFirstAirTime() : System.currentTimeMillis();
    }

    public static void setFirstAirTime(Player player, long time) {
        if (!uCars.isSmoothDrive()) { //Return "1" (No multiplier) if accelerating vehicles is disabled
            return;
        }
        a(player).setFirstAirTime(time);
    }

    public static float getCurrentAccel(Player player) {
        return uCars.isSmoothDrive() ? a(player).getCurrentSpeedFactor() : 1;
    }

    public static void setAccel(Player player, float accel) {
        if (!uCars.isSmoothDrive()) { //Return "1" (No multiplier) if accelerating vehicles is disabled
            return;
        }
        a(player).setCurrentSpeedFactor(accel);
    }

    public static float getAccel(Player player, CarDirection dir) { //Returns a multiplier to multiply with the x and z of the movement vector so the car appears to accelerate smoothly
        if (!uCars.isSmoothDrive()) { //Return "1" (No multiplier) if accelerating vehicles is disabled
            return 1;
        }
        float accMod = UCarsAPI.getAPI().getAcceleration(player, 1); //The multiplier to multiply our acceleration by from the API (Eg. another plugin can say "0.5" as the value here for accelerating at half the usual speed)
        float decMod = UCarsAPI.getAPI().getDeceleration(player, 1); //The multiplier to multiply our acceleration by from the API (Eg. another plugin can say "0.5" as the value here for accelerating at half the usual speed)

        SmoothMeta smooth = a(player);

        smooth.updateAccelerationFactor(accMod); //Update onto the Acceleration meta (Which does all the calculation for smooth accelerating) what the API wants in terms of accelerating speed - Allows it to be dynamic
        smooth.updateDecelerationFactor(decMod);

        return smooth.getFactor(dir); //Get the acceleration factor
    }

    public static void input(Entity car, UCarUpdateEvent event) { //Take our inputted
		/*if(ucars.smoothDrive){
			float a = getAccel(event.getPlayer());
			travel.setX(travel.getX() * a);
			travel.setZ(travel.getZ() * a);
		}*/

        UCarsAPI api = UCarsAPI.getAPI();
        StatValue controlScheme = api.getUCarMeta(uCars, "car.controls", car.getUniqueId());
        if (controlScheme == null && !uCars.isForcedRaceControls()) {
            //Default control scheme
            if (!uCars.hasFireUpdateEvent()) {
                uCars.getListener().onUcarUpdate(event);
            } else {
                uCars.getServer().getPluginManager().callEvent(event);
            }
        } else if (uCars.isForcedRaceControls() || ((String) controlScheme.value()).equalsIgnoreCase("race")) {
            //Use race oriented control scheme
            event.player = null; //Remove memory leak
            car.removeMetadata("car.vec", uCars); //Clear previous vector
            car.setMetadata("car.vec", new StatValue(uCars, event));
        }
    }

    private static SmoothMeta a(Player player) {
        float accMod = UCarsAPI.getAPI().getAcceleration(player, 1); //The multiplier to multiply our acceleration by from the API (Eg. another plugin can say "0.5" as the value here for accelerating at half the usual speed)
        float decMod = UCarsAPI.getAPI().getDeceleration(player, 1); //The multiplier to multiply our acceleration by from the API (Eg. another plugin can say "0.5" as the value here for accelerating at half the usual speed)
        SmoothMeta smooth; //Metadata saved to the player for tracking their acceleration
        if (!player.hasMetadata("ucars.smooth")) { //Setting the metadata onto the player if it's not already set
            smooth = new SmoothMeta(accMod, decMod);
            player.setMetadata("ucars.smooth", new StatValue(uCars, smooth));
        } else { //Metadata already set, lets attempt to read it
            try {
                Object o = player.getMetadata("ucars.smooth").get(0).value(); //Get the smooth meta set on the player
                if (o instanceof SmoothMeta) {
                    smooth = (SmoothMeta) o;
                } else { //Meta incorrectly set, plugin conflict? Just overwriting it with out own, correct, meta
                    smooth = new SmoothMeta(accMod, decMod);
                    player.removeMetadata("ucars.smooth", uCars);
                    player.setMetadata("ucars.smooth", new StatValue(uCars, smooth));
                }
            } catch (Exception e) { //Meta incorrectly set, plugin conflict? Just overwriting it with out own, correct, meta
                smooth = new SmoothMeta(accMod, decMod);
                player.removeMetadata("ucars.smooth", uCars);
                player.setMetadata("ucars.smooth", new StatValue(uCars, smooth));
            }
        }
        return smooth;
    }
}
