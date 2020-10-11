package com.useful.ucars.api;

import com.useful.ucars.UCars;
import com.useful.ucars.common.StatValue;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Some basic API tools that allow plugins to manipulate uCars for their use
 * 
 * @author storm345
 * 
 */
public class UCarsAPI {

	private UCars plugin = null;
	private Map<Plugin, CarCheck> carChecks = new HashMap<Plugin, CarCheck>();
	private Map<Plugin, ItemCarCheck> itemCarChecks = new HashMap<Plugin, ItemCarCheck>();
	private Map<Plugin, CarSpeedModifier> carSpeedMods = new HashMap<Plugin, CarSpeedModifier>();
	private Map<Plugin, CarTurningModifier> carRotMods = new HashMap<Plugin, CarTurningModifier>();
	private Map<Plugin, CarAccelerationModifier> carAccelMods = new HashMap<Plugin, CarAccelerationModifier>();
	private Map<Plugin, CarDecelerationModifier> carDecelMods = new HashMap<Plugin, CarDecelerationModifier>();
	private Map<UUID, Map<String, StatValue>> ucarsMeta = new HashMap<UUID, Map<String, StatValue>>();
	private boolean uCarsHandlesPlacingCars = true;

	public UCarsAPI() {
		this.plugin = UCars.plugin;
	}

	public boolean hasItemCarCheckCriteria(){
		return this.itemCarChecks.size() > 0;
	}

	/**
	 * Get the running instance of the API implementation
	 * 
	 * @return Returns the API
	 */
	public static UCarsAPI getAPI() {
		return UCars.plugin.getAPI();
	}

	/**
	 * Will hook your plugin into uCars. This first step must be done before any
	 * API calls can be made.
	 * 
	 * @param plugin
	 *            Your plugin
	 */
	public void hookPlugin(Plugin plugin) {
		UCars.plugin.getLogger().info(
				"Successfully hooked into by: " + plugin.getName());
		UCars.plugin.hookedPlugins.add(plugin);
		return;
	}

	/**
	 * Call onDisable() to show the user your plugin being unhooked. Else it
	 * will be unhooked automatically anyway.
	 * 
	 * @param plugin
	 *            Your plugin
	 */
	public void unhookPlugin(Plugin plugin) {
		UCars.plugin.getLogger().info("Successfully unhooked: " + plugin.getName());
		UCars.plugin.hookedPlugins.remove(plugin);
		return;
	}

	/**
	 * Will reset all hooked plugins -Don't call unless absolutely needed
	 * 
	 */
	public void unhookPlugins() {
		plugin.hookedPlugins.removeAll(plugin.hookedPlugins); 
		plugin.getLogger().info("Successfully unhooked all plugins!");
		return;
	}

	/**
	 * Checks if your plugin is hooked, your plugin needs to be hooked to use
	 * the API
	 * 
	 * @param plugin
	 *            Your plugin
	 * @return True if the plugin is hooked, False if not
	 */
	public Boolean isPluginHooked(Plugin plugin) {
		if (plugin == UCars.plugin) {
			return true;
		}
		return UCars.plugin.hookedPlugins.contains(plugin);
	}

	/**
	 * Registers an isACar() check for your plugin, only one check is allowed
	 * per hooked plugin. Trying to add more than one check will result in
	 * overriding the original
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param carCheck
	 *            The CarCheck to perform
	 * @return True if registered, False if not because plugin isn't hooked
	 */
	public Boolean registerCarCheck(Plugin plugin, CarCheck carCheck) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carChecks.put(plugin, carCheck);
		return true;
	}
	
	/**
	 * Registers an item isACar() check for your plugin, only one check is allowed
	 * per hooked plugin. Trying to add more than one check will result in
	 * overriding the original
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param carCheck
	 *            The CarCheck to perform
	 * @return True if registered, False if not because plugin isn't hooked
	 * @since v17
	 */
	public Boolean registerItemCarCheck(Plugin plugin, ItemCarCheck carCheck) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		itemCarChecks.put(plugin, carCheck);
		return true;
	}

	/**
	 * Removes a carCheck registered by a plugin
	 * 
	 * @param plugin
	 *            Your plugin
	 * @return True if unregistered, False if not because plugin isn't hooked
	 */
	public Boolean unregisterCarCheck(Plugin plugin) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carChecks.remove(plugin);
		return true;
	}
	
	/**
	 * Removes an item carCheck registered by a plugin
	 * 
	 * @param plugin
	 *            Your plugin
	 * @return True if unregistered, False if not because plugin isn't hooked
	 * @since v17
	 */
	public Boolean unregisterItemCarCheck(Plugin plugin) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		itemCarChecks.remove(plugin);
		return true;
	}

	public synchronized Boolean runCarChecks(Entity car) {
		for (CarCheck c : carChecks.values()) {
			if (!c.isACar(car)) {
				return false;
			}
		}
		return true;
	}
	
	public synchronized Boolean runCarChecks(ItemStack carStack) {
		for (ItemCarCheck c : itemCarChecks.values()) {
			if (!c.isACar(carStack)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Registers an car turning mod for your plugin so you can manipulate the amount the car turns per tick
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param mod
	 *            The turningMod to add
	 * @return True if registered, False if not because plugin isn't hooked
	 */
	public Boolean registerTurningMod(Plugin plugin, CarTurningModifier mod) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carRotMods.put(plugin, mod);
		return true;
	}

	/**
	 * Registers an car speed mod check for your plugin, only one speed mod is
	 * allowed per hooked plugin. Trying to add more than one check will result
	 * in overriding the original
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param speedMod
	 *            The speedMod to add
	 * @return True if registered, False if not because plugin isn't hooked
	 */
	public Boolean registerSpeedMod(Plugin plugin, CarSpeedModifier speedMod) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carSpeedMods.put(plugin, speedMod);
		return true;
	}
	
	/**
	 * Removes a turning mod registered by a plugin
	 * 
	 * @param plugin
	 *            Your plugin
	 * @return True if unregistered, False if not because plugin isn't hooked
	 */
	public Boolean unregisterTurningMod(Plugin plugin) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carRotMods.remove(plugin);
		return true;
	}

	/**
	 * Removes a speed mod registered by a plugin
	 * 
	 * @param plugin
	 *            Your plugin
	 * @return True if unregistered, False if not because plugin isn't hooked
	 */
	public Boolean unregisterSpeedMod(Plugin plugin) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carSpeedMods.remove(plugin);
		return true;
	}
	
	/**
	 * Registers a car deceleration modifier to your plugin, only one is
	 * allowed per hooked plugin. Trying to add more than one check will result
	 * in overriding the original
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param accelerationModifier
	 *            The Acceleration Mod to add
	 * @return True if registered, False if not because plugin isn't hooked
	 */
	public Boolean registerDecelerationMod(Plugin plugin, CarDecelerationModifier accelerationModifier) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carDecelMods.put(plugin, accelerationModifier);
		return true;
	}

	/**
	 * Removes an deceleration mod registered by a plugin
	 * 
	 * @param plugin
	 *            Your plugin
	 * @return True if unregistered, False if not because plugin isn't hooked
	 */
	public Boolean unregisterDecelerationMod(Plugin plugin) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carDecelMods.remove(plugin);
		return true;
	}
	
	public float getDeceleration(Player driver, float currentMultiplier) {
		if(carDecelMods.size() < 1){
			return currentMultiplier;
		}
		for (CarDecelerationModifier m : new ArrayList<CarDecelerationModifier>(carDecelMods.values())) {
			currentMultiplier = m.getAccelerationDecimal(driver, currentMultiplier);
		}
		return currentMultiplier;
	}
	
	/**
	 * Registers a car acceleration modifier to your plugin, only one is
	 * allowed per hooked plugin. Trying to add more than one check will result
	 * in overriding the original
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param accelerationModifier
	 *            The Acceleration Mod to add
	 * @return True if registered, False if not because plugin isn't hooked
	 */
	public Boolean registerAccelerationMod(Plugin plugin, CarAccelerationModifier accelerationModifier) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carAccelMods.put(plugin, accelerationModifier);
		return true;
	}

	/**
	 * Removes an acceleration mod registered by a plugin
	 * 
	 * @param plugin
	 *            Your plugin
	 * @return True if unregistered, False if not because plugin isn't hooked
	 */
	public Boolean unregisterAccelerationMod(Plugin plugin) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		carAccelMods.remove(plugin);
		return true;
	}
	
	public float getAcceleration(Player driver, float currentMultiplier) {
		if(carAccelMods.size() < 1){
			return currentMultiplier;
		}
		for (CarAccelerationModifier m : new ArrayList<CarAccelerationModifier>(carAccelMods.values())) {
			currentMultiplier = m.getAccelerationDecimal(driver, currentMultiplier);
		}
		return currentMultiplier;
	}
	
	public synchronized double getMaxCarTurnAmountDegrees(Entity car, double normalAmount){
		double m = normalAmount;
		for (CarTurningModifier mod : carRotMods.values()) {
			m = mod.getModifiedTurningSpeed(car, m);
		}
		return m;
	}

	public synchronized Vector getTravelVector(Entity car, Vector travelVector, double currentMultiplier) {
		for (CarSpeedModifier m : carSpeedMods.values()) {
			travelVector = m.getModifiedSpeed(car, travelVector, currentMultiplier);
		}
		return travelVector;
	}

	/**
	 * Gets all non-permanent uCarMeta for a car
	 * 
	 * -uCarMeta is removed each time the server restarts so use for temporary
	 * buffs such as speed-buffs or upgrades (If you track and save them)
	 * 
	 * @param entityId
	 *            Id of the uCar entity (ID can be changed without your notice
	 *            So don't use this for tracking cars)
	 * @return All the meta set for that car
	 */
	public Map<String, StatValue> getUCarMeta(UUID entityId) {
		if (!ucarsMeta.containsKey(entityId)) {
			return new HashMap<String, StatValue>();
		}
		return ucarsMeta.get(entityId);
	}
	
	/**
	 * Sets a uCar to use a control scheme where the car motion is not based of user input, but is 
	 * played out through the server to ensure absolute fair speeds between players.
	 * 
	 * -uCarMeta is removed each time the server restarts so use for temporary
	 * buffs such as speed-buffs or upgrades (If you track and save them)
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param id
	 *            Id of the uCar entity (ID can be changed without your notice
	 *            So don't use this for tracking cars)
	 * @return True is successful and False if plugin is not hooked or
	 *         unsuccessful
	 *         
	 * @since v17
	 */
	public boolean setUseRaceControls(UUID id, Plugin plugin){
		return addUCarsMeta(plugin, id, "car.controls", new StatValue("race", UCars.plugin));
	}

	/**
	 * Sets uCarMeta for a car
	 * 
	 * -uCarMeta is removed each time the server restarts so use for temporary
	 * buffs such as speed-buffs or upgrades (If you track and save them)
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param entityId
	 *            Id of the uCar entity (ID can be changed without your notice
	 *            So don't use this for tracking cars)
	 * @param toAdd
	 *            The ucarMeta to add to the car
	 * @param statName
	 *            The key/Name of the stat eg. 'myPlugin.myStat'
	 * @return True is successful and False if plugin is not hooked or
	 *         unsuccessful
	 */
	public Boolean addUCarsMeta(Plugin plugin, UUID entityId, String statName, StatValue toAdd) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		Map<String, StatValue> stats = new HashMap<String, StatValue>();
		if (ucarsMeta.containsKey(entityId)) {
			stats = ucarsMeta.get(entityId);
		}
		stats.put(statName, toAdd);
		ucarsMeta.put(entityId, stats);
		return true;
	}

	/**
	 * Gets uCarMeta for a car
	 * 
	 * -uCarMeta is removed each time the server restarts so use for temporary
	 * buffs such as speed-buffs or upgrades (If you track and save them)
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param entityId
	 *            Id of the uCar entity (ID can be changed without your notice
	 *            So don't use this for tracking cars)
	 * @param statName
	 *            The key/Name of the stat eg. 'myPlugin.myStat'
	 * @return ucarMeta is successful and null if not existing on car or plugin
	 *         is not hooked or unsuccessful
	 */
	public StatValue getUCarMeta(Plugin plugin, String statName, UUID entityId) {
		if (!isPluginHooked(plugin) || !ucarsMeta.containsKey(entityId)) {
			return null;
		}
		Map<String, StatValue> metas = ucarsMeta.get(entityId);
		if (!metas.containsKey(statName)) {
			return null;
		}
		return metas.get(statName);
	}

	/**
	 * Remove a meta value of a car
	 * 
	 * -uCarMeta is removed each time the server restarts so use for temporary
	 * buffs such as speed-buffs or upgrades (If you track and save them)
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param statName
	 *            The name of the stat to remove
	 * @param entityId
	 *            The entityId of the car
	 * @return True if removed, False if plugin not hooked or removal
	 *         unsuccessful (Not set)
	 */
	public Boolean removeUCarMeta(Plugin plugin, String statName, UUID entityId) {
		if (!isPluginHooked(plugin) || !ucarsMeta.containsKey(entityId)) {
			return false;
		}
		Map<String, StatValue> metas = ucarsMeta.get(entityId);
		if (!metas.containsKey(statName)) {
			return false;
		}
		metas.remove(statName);
		ucarsMeta.put(entityId, metas);
		return true;
	}

	/**
	 * Clears uCarMeta for a car
	 * 
	 * -uCarMeta is removed each time the server restarts so use for temporary
	 * buffs such as speed-buffs or upgrades (If you track and save them)
	 * 
	 * @param plugin
	 *            Your plugin
	 * @param entityId
	 *            Id of the uCar entity (ID can be changed without your notice
	 *            So don't use this for tracking cars)
	 * @return True is successful and False is plugin is not hooked or
	 *         unsuccessfull
	 */
	public Boolean clearCarMeta(Plugin plugin, UUID entityId) {
		if (!isPluginHooked(plugin)) {
			return false;
		}
		ucarsMeta.remove(entityId);
		return true;
	}

	/**
	 * Updates the ID of a car entity
	 * 
	 * @param previousId
	 *            The old ID of the entity
	 * @param newId
	 *            The new ID of the entity
	 */
	public void updateUCarMeta(UUID previousId, UUID newId) {
		if (!ucarsMeta.containsKey(previousId)) {
			return;
		}
		ucarsMeta.put(newId,
				new HashMap<String, StatValue>(ucarsMeta.get(previousId)));
		ucarsMeta.remove(previousId);
		return;
	}

	/**
	 * Checks if the given vehicle is a car
	 * 
	 * @param car
	 *            The Minecart to check if it's a car
	 * @return True if it's a car
	 */
	public Boolean checkIfCar(Entity car) {
		return UCars.listener.isACar(car);
	}

	/**
	 * Checks if the given player is in a car
	 * 
	 * @param player
	 *            The player to check
	 * @return True if they're in a car
	 */
	public Boolean checkInCar(Player player) {
		return UCars.listener.inACar(player);
	}

	/**
	 * Checks if the given player is in a car
	 * 
	 * @param player
	 *            The player to check
	 * @return True if they're in a car
	 */
	public Boolean checkInCar(String player) {
		return UCars.listener.inACar(player);
	}
	
    /**
     * Get's the version of uCars
     * 
     * @return Return's the version of uCars used
     */
	public String getUCarsVersion(){
		return plugin.getDescription().getVersion();
	}
	
	/**
	 * Returns if the car should be waiting at traffic lights
	 * 
	 * @param car The car to check
	 * @return True if it should stop, false else
	 * 
	 * @since v17
	 */
	public Boolean atTrafficLight(Entity car){
		Location loc = car.getLocation();
		Block under = loc.getBlock().getRelative(BlockFace.DOWN);
		Block underUnder = under.getRelative(BlockFace.DOWN);
		return UCars.listener.atTrafficLight(car, under, underUnder, loc);
	}

	public boolean isuCarsHandlingPlacingCars() {
		return uCarsHandlesPlacingCars;
	}

	public void setUCarsHandlesPlacingCars(boolean uCarsHandlesPlacingCars) {
		this.uCarsHandlesPlacingCars = uCarsHandlesPlacingCars;
	}
}
