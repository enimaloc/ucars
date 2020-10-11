package com.useful.ucars;

import com.useful.ucars.api.CarRespawnReason;
import com.useful.ucars.api.UCarCrashEvent;
import com.useful.ucars.api.uCarRespawnEvent;
import com.useful.ucars.api.UCarsAPI;
import com.useful.ucars.controls.ControlSchemeManager;
import com.useful.ucars.util.UEntityMeta;
import com.useful.ucars.common.StatValue;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class UCarsListener implements Listener {
	private UCars plugin;
	private List<String> ignoreJump = null;
	
	private Boolean carsEnabled = true;
	private Boolean licenseEnabled = false;
	private Boolean roadBlocksEnabled = false;
	private Boolean trafficLightsEnabled = true;
	private Boolean effectBlocksEnabled = true;
	private Boolean usePerms = false;
	private Boolean fuelEnabled = false;
	private Boolean fuelUseItems = false;
	
	private double defaultSpeed = 30;
	private static double defaultHealth = 10;
	private double damageWater = 0;
	private double damageLava = 10;
	private double damageCactus = 5;
	private double UCarJumpAmount = 5;
	private double crashDamage = 0;
	private double hitByCrashDamage = 0;
	
	private String fuelBypassPerm = "ufuel.bypass";
	
    private List<String> roadBlocks = new ArrayList<String>(); //Road blocks
    private List<String> trafficLightRawIds = new ArrayList<String>(); //Traffic lights
    private List<String> blockBoost = new ArrayList<String>(); //Gold booster blocks
    private List<String> highBlockBoost = new ArrayList<String>(); //Diamond booster blocks
    private List<String> resetBlockBoost = new ArrayList<String>(); //Emerald booster blocks
    private List<String> jumpBlock = new ArrayList<String>(); //Jump blocks (Iron)
    private List<String> teleportBlock = new ArrayList<String>(); //Teleport blocks (purple clay)
    private List<String> barriers = new ArrayList<String>();
    
    private ConcurrentHashMap<String, Double> speedMods = new ConcurrentHashMap<String, Double>();

	public UCarsListener(UCars plugin) {
		this.plugin = UCars.plugin;
		ignoreJump = new ArrayList<String>();
		ignoreJump.add("AIR"); //Air
		ignoreJump.add("LAVA"); //Lava
        ignoreJump.add("STATIONARY_LAVA"); //Lava
        ignoreJump.add("WATER"); //Water
        ignoreJump.add("STATIONARY_WATER"); //Water
        ignoreJump.add("COBBLE_WALL"); //Cobble wall
        ignoreJump.add("FENCE"); //fence
        ignoreJump.add("NETHER_FENCE"); //Nether fence
        ignoreJump.add("STONE_PLATE"); //Stone pressurepad
        ignoreJump.add("WOOD_PLATE"); //Wood pressurepad
		ignoreJump.add("TRIPWIRE"); // tripwires
		ignoreJump.add("TRIPWIRE_HOOK"); // tripwires
		ignoreJump.add("TORCH"); // torches
		ignoreJump.add("REDSTONE_TORCH_ON"); // redstone torches
		ignoreJump.add("REDSTONE_TORCH_OFF"); // redstone off torches
		ignoreJump.add("DIODE_BLOCK_OFF"); // repeater off
		ignoreJump.add("DIODE_BLOCK_ON"); // repeater on
		ignoreJump.add("REDSTONE_COMPARATOR_OFF"); // comparator off
		ignoreJump.add("REDSTONE_COMPARATOR_ON"); // comparator on
		ignoreJump.add("VINE"); // vines
		ignoreJump.add("LONG_GRASS"); // Tall grass
		ignoreJump.add("STONE_BUTTON"); // stone button
		ignoreJump.add("WOOD_BUTTON"); // wood button
		ignoreJump.add("FENCE_GATE"); // fence gate
		ignoreJump.add("LEVER"); // lever
		ignoreJump.add("SNOW"); // snow
		ignoreJump.add("DAYLIGHT_DETECTOR"); // daylight detector
		ignoreJump.add("SIGN_POST"); // sign
		ignoreJump.add("WALL_SIGN"); // sign on the side of a block
		ignoreJump.add(Material.ACACIA_FENCE.name());
		ignoreJump.add(Material.ACACIA_FENCE_GATE.name());
		ignoreJump.add(Material.BIRCH_FENCE.name());
		ignoreJump.add(Material.BIRCH_FENCE_GATE.name());
		ignoreJump.add(Material.JUNGLE_FENCE.name());
		ignoreJump.add(Material.JUNGLE_FENCE_GATE.name());
		/*ignoreJump.add("CARPET"); // carpet
*/		
		usePerms = UCars.config.getBoolean("general.permissions.enable");
		carsEnabled = UCars.config.getBoolean("general.cars.enable");
		defaultHealth = UCars.config.getDouble("general.cars.health.default");
		
		damageWater = UCars.config
				.getDouble("general.cars.health.underwaterDamage");
		damageLava = UCars.config
				.getDouble("general.cars.health.lavaDamage");
		damageCactus = UCars.config
				.getDouble("general.cars.health.cactusDamage");
		defaultSpeed = UCars.config
				.getDouble("general.cars.defSpeed");
		fuelBypassPerm = UCars.config
				.getString("general.cars.fuel.bypassPerm");
		UCarJumpAmount = UCars.config
				.getDouble("general.cars.jumpAmount");
		crashDamage = UCars.config
				.getDouble("general.cars.health.crashDamage");
		
		hitByCrashDamage = UCars.config
				.getDouble("general.cars.hitBy.damage");
		
		licenseEnabled = UCars.config.getBoolean("general.cars.licenses.enable");
		roadBlocksEnabled = UCars.config.getBoolean("general.cars.roadBlocks.enable");
		trafficLightsEnabled = UCars.config.getBoolean("general.cars.trafficLights.enable");
		effectBlocksEnabled = UCars.config.getBoolean("general.cars.effectBlocks.enable");
		fuelEnabled = UCars.config.getBoolean("general.cars.fuel.enable");
		fuelUseItems = UCars.config.getBoolean("general.cars.fuel.items.enable");
		
		if(roadBlocksEnabled){
		    List<String> ids = UCars.config
					.getStringList("general.cars.roadBlocks.ids");
			ids.addAll(UCars.config.getStringList("general.cars.blockBoost"));
			ids.addAll(UCars.config.getStringList("general.cars.HighblockBoost"));
			ids.addAll(UCars.config.getStringList("general.cars.ResetblockBoost"));
			ids.addAll(UCars.config.getStringList("general.cars.jumpBlock"));
			ids.add("AIR");
			ids.add("LAVA");
			ids.add("STATIONARY_LAVA");
			ids.add("WATER");
			ids.add("STATIONARY_WATER");
			roadBlocks = ids;
		}
		if(trafficLightsEnabled){
			trafficLightRawIds = UCars.config.getStringList("general.cars.trafficLights.waitingBlock");
		}
		if(effectBlocksEnabled){
			blockBoost = UCars.config.getStringList("general.cars.blockBoost");
			highBlockBoost = UCars.config.getStringList("general.cars.HighblockBoost");
			resetBlockBoost = UCars.config.getStringList("general.cars.ResetblockBoost");
			jumpBlock = UCars.config.getStringList("general.cars.jumpBlock");
			teleportBlock = UCars.config.getStringList("general.cars.teleportBlock");
		}
		
		barriers = UCars.config.getStringList("general.cars.barriers"); //Load specified barriers
		
		//SpeedMods
		List<String> units = UCars.config.getStringList("general.cars.speedMods");
		for (String unit : units) {
			String[] sections = unit.split("-");
			try {
				String rawMaterials = sections[0];
				double multiplier = Double.parseDouble(sections[1]);
				speedMods.put(rawMaterials, multiplier);
			} catch (NumberFormatException e) {
				//Invalid speed mod
			}
		}
		//No longer speedmods
	}

	/*
	 * Asks the API to calculate car stats (Such as velocity mods, etc...)
	 */
	public Vector calculateCarStats(Entity car, Player player,
			Vector velocity, double currentMult) {
		if (UEntityMeta.hasMetadata(car, "car.frozen")) {
			velocity = new Vector(0, 0, 0);
			return velocity;
		}
		velocity = plugin.getAPI().getTravelVector(car, velocity, currentMult);
		return velocity;
	}

	/*
	 * Checks if a trafficlight sign is attached to the given block
	 */
	public boolean trafficLightSignOn(Block block) {
		if (block.getRelative(BlockFace.NORTH).getState() instanceof Sign) {
			Sign sign = (Sign) block.getRelative(BlockFace.NORTH).getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
					ChatColor.stripColor("[TrafficLight]"))) {
				return true;
			}
		} else if (block.getRelative(BlockFace.EAST).getState() instanceof Sign) {
			Sign sign = (Sign) block.getRelative(BlockFace.EAST).getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
					ChatColor.stripColor("[TrafficLight]"))) {
				return true;
			}
		} else if (block.getRelative(BlockFace.SOUTH).getState() instanceof Sign) {
			Sign sign = (Sign) block.getRelative(BlockFace.SOUTH).getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
					ChatColor.stripColor("[TrafficLight]"))) {
				return true;
			}
		} else if (block.getRelative(BlockFace.WEST).getState() instanceof Sign) {
			Sign sign = (Sign) block.getRelative(BlockFace.WEST).getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
					ChatColor.stripColor("[TrafficLight]"))) {
				return true;
			}
		} else if (block.getRelative(BlockFace.DOWN).getState() instanceof Sign) {
			Sign sign = (Sign) block.getRelative(BlockFace.DOWN).getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
					ChatColor.stripColor("[TrafficLight]"))) {
				return true;
			}
		} else if (block.getRelative(BlockFace.UP).getState() instanceof Sign) {
			Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
					ChatColor.stripColor("[TrafficLight]"))) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Checks if the specified player is inside a ucar (public for traincarts
	 * support)
	 */
	public boolean inACar(String playerName) {
		try {
			Player p = plugin.getServer().getPlayer(playerName);
			return inACar(p);
		} catch (Exception e) {
			// Server reloading
			return false;
		}
	}

	/*
	 * Checks if a minecart is a car (Public for traincarts support)
	 */
	public boolean isACar(Entity cart) {
		if(cart.hasMetadata("ucars.ignore") || UEntityMeta.hasMetadata(cart, "ucars.ignore")){
			return false; //Not a car
		}
		Location location = cart.getLocation();
		Block block = location.getBlock();
		String material = block.getType().name().toUpperCase();
		String underMaterial = block.getRelative(BlockFace.DOWN).getType().name().toUpperCase();
		String underUnderMaterial = block.getRelative(BlockFace.DOWN, 2).getType().name().toUpperCase();
		List<String> checks = new ArrayList<String>();
		if(UCars.ignoreRails){
			checks.add("POWERED_RAIL");
			checks.add("RAILS");
			checks.add("DETECTOR_RAIL");
			checks.add("ACTIVATOR_RAIL");
		}
		if(checks.contains(material)
				|| checks.contains(underMaterial)
				|| checks.contains(underUnderMaterial)){
			return false;
		}
		if (!plugin.getAPI().runCarChecks(cart)) {
			return false;
		}
		return true;
	}

	/*
	 * Resets any boosts the given car may have
	 */
	public void ResetCarBoost(String playername, Vehicle car,
			double defaultSpeed) {
		String p = playername;
		World w = plugin.getServer().getPlayer(p).getLocation().getWorld();
		w.playSound(plugin.getServer().getPlayer(p).getLocation(),
				Sound.ENTITY_BAT_TAKEOFF, 1.5f, -2);
		if (UCars.carBoosts.containsKey(p)) {
			UCars.carBoosts.remove(p);
		}
		return;
	}

	/*
	 * Applies a boost to the car mentioned
	 */
	public boolean carBoost(String playerName, final double power,
			final long lengthMillis, double defaultSpeed) {
		final String player = playerName;
		final double defMultiplier = defaultSpeed;
		double current = defMultiplier;
		if (UCars.carBoosts.containsKey(player)) {
			current = UCars.carBoosts.get(player);
		}
		if (current > defMultiplier) {
			// Already boosting!
			return false;
		}
		final double finalCurrent = current;
		if (plugin == null) {
			plugin.getLogger().log(Level.SEVERE,
					Lang.get("lang.error.pluginNull"));
		}
		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {
					public void run() {
						World world = plugin.getServer().getPlayer(player).getLocation()
								.getWorld();
						world.playSound(plugin.getServer().getPlayer(player)
								.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.5f, -2);
						double speed = finalCurrent + power;
						UCars.carBoosts.put(player, speed);
						// Boosting!
						try {
							Thread.sleep(lengthMillis);
						} catch (InterruptedException e) {
							UCars.carBoosts.remove(player);
							return;
						}
						// paused for set time!
						UCars.carBoosts.remove(player);
						// resumed normal speed!
						return;
					}
				});
		return true;
	}

	/*
	 * Checks if the specified player is inside a ucars (public for traincarts
	 * support)
	 */
	public boolean inACar(Player player) {
		try {
			if (player == null) {
				// Should NEVER happen(It means they r offline)
				return false;
			}
			if (player.getVehicle() == null) {
				return false;
			}
			Entity entity = player.getVehicle();
			if (!(entity instanceof Vehicle)) {
				while (!(entity instanceof Vehicle) && entity.getVehicle() != null) {
					entity = entity.getVehicle();
				}
				if (!(entity instanceof Vehicle)) {
					return false;
				}
			}
			Vehicle cart = (Vehicle) entity;
			return isACar(cart);
		} catch (Exception e) {
			// Server reloading
			return false;
		}
	}
	
	public Entity getDrivingPassengerOfCar(Vehicle vehicle){ //Get the PLAYER passenger of the car
		Entity passenger = vehicle.getPassenger(); //The vehicle's lowest passenger; may be a pig, etc... if pigucarting
		if (passenger == null || !(vehicle instanceof Entity)) { //If it has nobody riding it, ignore it
			return null;
		}
		if (!(passenger instanceof Player)) { //If not a player riding it; then keep looking until we find a player
			while (!(passenger instanceof Player)
					&& passenger.getPassenger() != null) { //While there's more entities above this in the 'stack'
				passenger = passenger.getPassenger(); //Keep iterating
			}
		}
		return passenger;
	}
	
	@EventHandler
	void carExit(VehicleExitEvent event){
		UEntityMeta.removeMetadata(event.getVehicle(), "car.vec");
		UEntityMeta.removeMetadata(event.getExited(), "ucars.smooth");
		event.getVehicle().removeMetadata("car.vec", UCars.plugin);
		event.getExited().removeMetadata("ucars.smooth", UCars.plugin);
		if(event.getVehicle().hasMetadata("safeExit.ignore")
				|| UEntityMeta.hasMetadata(event.getVehicle(), "safeExit.ignore")){
			return;
		}
		if(!(event.getVehicle() instanceof Vehicle) || !isACar((Vehicle) event.getVehicle())){
			return;
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	void carRemove(VehicleDestroyEvent event){
		if(event.isCancelled()){
			return;
		}
		UEntityMeta.removeMetadata(event.getVehicle(), "car.vec");
		event.getVehicle().removeMetadata("car.vec", UCars.plugin);
		final Vehicle vehicle = event.getVehicle();
		Bukkit.getScheduler().runTaskLaterAsynchronously(UCars.plugin, new Runnable(){

			@Override
			public void run() {
				UEntityMeta.removeAllMeta(vehicle);
				return;
			}}, 100l);
	}
	
	@EventHandler
	void entityDeath(EntityDeathEvent event){
		final Entity entity = event.getEntity();
		if(entity instanceof Player){
			return;
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(UCars.plugin, new Runnable(){

			@Override
			public void run() {
				UEntityMeta.removeAllMeta(entity);
				return;
			}}, 100l);
	}

	/*
	 * Standardises the text on some effect signs
	 */
	@EventHandler
	public void signWriter(SignChangeEvent event) {
		String[] lines = event.getLines();
		if (ChatColor.stripColor(lines[1]).equalsIgnoreCase("[TrafficLight]")) {
			lines[1] = "[TrafficLight]";
		}
		if (ChatColor.stripColor(lines[0]).equalsIgnoreCase("[uFuel]")) {
			lines[0] = "[uFuel]";
		}
		if (ChatColor.stripColor(lines[0]).equalsIgnoreCase("[Teleport]")) {
			lines[0] = "[Teleport]";
		}
		if(ChatColor.stripColor(lines[0]).equalsIgnoreCase("[wir]")){
			if(!event.getPlayer().hasPermission("wirelessredstone")){
				event.getPlayer().sendMessage(ChatColor.RED+"Sorry you need the permisson 'wirelessredstone' to do this!");
				lines[0] = "";
			}
		}
		return;
	}
	
	@EventHandler(priority = EventPriority.LOWEST) //Called first
	public void playerJoinControlsUnlock(PlayerJoinEvent event){
		ControlSchemeManager.setControlsLocked(event.getPlayer(), false);
	}

	/*
	 * Alert op's if no protocolLib found
	 */
	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {

		if (event.getPlayer().isOp()) {
			if (!plugin.protocolLib) {
				event.getPlayer().sendMessage(
						UCars.colors.getError()
								+ Lang.get("lang.messages.noProtocolLib"));
			}
		}

	}

	/*
	 * Performs on-vehicle-tick calculations(even when stationary) and also
	 * allows for old versions of minecraft AND ucars to access the new features
	 * through this 'bridge' (in theory) But in practice they need protocol to
	 * get past the bukkit dependency exception so it uses that anyway! (Kept
	 * for old version hybrids, eg. tekkti)
	 */
	@EventHandler
	public void tickCalculationsAndLegacy(VehicleUpdateEvent event) {
		// start vehicleupdate mechs
		Vehicle vehicle = event.getVehicle();
		if(!(vehicle instanceof Vehicle)){
			return;
		}
		Entity passenger = getDrivingPassengerOfCar(vehicle); //Gets the entity highest in the passenger 'stack' (Allows for pigucart, etc...)
		Boolean driven = passenger != null && passenger instanceof Player;
		if(!driven){
			return; //Forget extra car physics if minecart isn't manned, takes too much strain with extra entities
		}

		Vector travel = vehicle.getVelocity();

		if(event instanceof UCarUpdateEvent){
			travel = ((UCarUpdateEvent) event).getTravelVector().clone();
		}

		if(!(event instanceof UCarUpdateEvent)){ //If it's just the standard every tick vehicle update event...
			if(UEntityMeta.hasMetadata(vehicle, "car.vec")){ //If it has the 'car.vec' meta, we need to use RACE CONTROLS on this vehicle
				UCarUpdateEvent newEvent = (UCarUpdateEvent) UEntityMeta.getMetadata(vehicle, "car.vec").get(0).value(); //Handle the update event (Called here not directly because otherwise ppl with acceleration better connection fire more control events and move marginally faster)
				newEvent.player = ((Player)passenger); //Set the player (in the car) onto the event so it can be handled by uCarUpdate handlers
				newEvent.incrementRead(); //Register that the control input update has been executed (So if no new control input event within 2 ticks; we know to stop the car)
				UEntityMeta.removeMetadata(vehicle, "car.vec"); //Update the 'car.vec' metadata with an otherwise identical event; but without the player object attached
				UCarUpdateEvent et = new UCarUpdateEvent(vehicle, newEvent.getTravelVector().clone(), null, newEvent.getDir()); //Clone of the other event, except no player object attached
				et.setRead(newEvent.getReadCount()); //Make sure it IS acceleration clone (With correct variable values)
				UEntityMeta.setMetadata(vehicle, "car.vec", new StatValue(et, UCars.plugin)); //Update the meta on the car
				/*ucars.plugin.getServer().getPluginManager().callEvent(newEvent); //Actually handle the uCarUpdateEvent
*/
				if(!UCars.fireUpdateEvent){
					onUcarUpdate(newEvent);
				}
				else {
					UCars.plugin.getServer().getPluginManager().callEvent(newEvent);
				}
				/*return;*/
			}
		}	
		//Everything below this (in this method) is executed EVERY MC vehicle update (every tick) and every ucar update
	
		Block normalBlock = vehicle.getLocation().getBlock();

		Player player = null;
		if (driven) {
			player = (Player) passenger;
		}
		if (!carsEnabled) {
			return;
		}

		Vehicle car = (Vehicle) vehicle;
		if (!isACar(car)) {
			return;
		}
		
		Vector velocity = car.getVelocity();
		
		if (car.getVelocity().getY() > 0.1
				&& !UEntityMeta.hasMetadata(car, "car.falling")
				&& !UEntityMeta.hasMetadata(car, "car.ascending")) { // Fix jumping bug (Where car just flies up infinitely high when clipping acceleration block)
														// in most occasions
			if (UEntityMeta.hasMetadata(car, "car.jumping")) {
				/*velocity.setY(2.5);*/
				UEntityMeta.removeMetadata(car, "car.jumping");
			} else if (UEntityMeta.hasMetadata(car, "car.jumpFull")) {
				// Jumping acceleration full block
				if (car.getVelocity().getY() > 10) {
					velocity.setY(5);
				}
				UEntityMeta.removeMetadata(car, "car.jumpFull");
			} else {
				velocity.setY(0);
			}
			car.setVelocity(velocity);
		}
		
		// Make jumping work when not moving
		// Calculate jumping gravity
		if(UEntityMeta.hasMetadata(car, "car.jumpUp")){
			double amount = (Double) UEntityMeta.getMetadata(car, "car.jumpUp").get(0).value();
			UEntityMeta.removeMetadata(car, "car.jumpUp");
			if(amount >= 1.5){
				double y = amount * 0.1;
				UEntityMeta.setMetadata(car, "car.jumpUp", new StatValue(amount-y, plugin));
				velocity.setY(y);
				car.setVelocity(velocity);
				return; //We don't want any further calculations
			}
			else{ //At the peak of ascent
				UEntityMeta.setMetadata(car, "car.falling", new StatValue(0.01, plugin));
				//car.setMetadata("car.fallingPause", new StatValue(1, plugin));
			}
			
		}
		if (UEntityMeta.hasMetadata(car, "car.falling")) {
			double gravity = (Double) UEntityMeta.getMetadata(car, "car.falling").get(0).value();
			double newGravity = gravity + (gravity * 0.6);
			UEntityMeta.removeMetadata(car, "car.falling");
			if ((gravity <= 0.6)) {
				UEntityMeta.setMetadata(car, "car.falling", new StatValue(
						newGravity, UCars.plugin));
				velocity.setY(-(gravity * 1.333 + 0.2d));
				car.setVelocity(velocity);
			}
		}

		//Start health calculations
		CarHealthData health = getCarHealthHandler(car);
		Boolean recalculateHealth = false;
		// Calculate health based on location
		if (normalBlock.getType().equals(Material.WATER)
				|| normalBlock.getType().equals(Material.STATIONARY_WATER)) {
			double damage = damageWater;
			if (damage > 0) {
				if (driven) {
					double max = defaultHealth;
					double left = health.getHealth() - damage;
					ChatColor color = ChatColor.YELLOW;
					if (left > (max * 0.66)) {
						color = ChatColor.GREEN;
					}
					if (left < (max * 0.33)) {
						color = ChatColor.RED;
					}
					player.sendMessage(ChatColor.RED + "-" + damage + "["
							+ Material.WATER.name().toLowerCase() + "]"
							+ color + " (" + left + ")");
				}
				health.damage(damage, car);
				recalculateHealth = true;
			}
		}
		if (normalBlock.getType().equals(Material.LAVA)
				|| normalBlock.getType().equals(Material.STATIONARY_LAVA)) {
			double damage = damageLava;
			if (damage > 0) {
				if (driven) {
					double max = defaultHealth;
					double left = health.getHealth() - damage;
					ChatColor color = ChatColor.YELLOW;
					if (left > (max * 0.66)) {
						color = ChatColor.GREEN;
					}
					if (left < (max * 0.33)) {
						color = ChatColor.RED;
					}
					player.sendMessage(ChatColor.RED + "-" + damage + "["
							+ Material.LAVA.name().toLowerCase() + "]"
							+ color + " (" + left + ")");
				}
				health.damage(damage, car);
				recalculateHealth = true;
			}
		}
		if (recalculateHealth) {
			updateCarHealthHandler(car, health);
		}

		float acceleration = 1;
		if(event instanceof UCarUpdateEvent && UCars.smoothDrive){ //If acceleration is enabled
			acceleration = ControlInput.getAccel(((UCarUpdateEvent)event).getPlayer(), ((UCarUpdateEvent)event).getDir()); //Find out the multiplier to use for accelerating the car 'naturally'
			CarDirection direction = ControlInput.getCurrentDriveDir(player);
			if(direction.equals(CarDirection.BACKWARDS)){
				acceleration *= 0.2; //0.2 speed backwards
			}
			travel.setX(travel.getX() * acceleration); //Multiple only x
			travel.setZ(travel.getZ() * acceleration); //and z with it (No y acceleration)
		}
		Vector directionVector = travel.clone().setY(0).normalize();

		if(directionVector.lengthSquared() > 0.01 /*directionVector.lengthSquared() > 0.1 && Math.abs(acceleration) > 0.2 && *//*event.getDir() != null && !event.getDir().equals(CarDirection.NONE)*/){
			Location dirLoc = new Location(car.getWorld(), 0, 0, 0); //Make sure car always faces the RIGHT "forwards"
			if(event instanceof UCarUpdateEvent && ((UCarUpdateEvent) event).getDir().equals(CarDirection.BACKWARDS)){
				directionVector = directionVector.multiply(-1);
			}
			CarDirection driveDir = ControlInput.getCurrentDriveDir(player);
			if(driveDir.equals(CarDirection.BACKWARDS)){
				directionVector = directionVector.multiply(-1);
			}
			dirLoc.setDirection(directionVector);
			float yaw = dirLoc.getYaw()+90;
			/*if(event.getDir().equals(CarDirection.BACKWARDS)){
				yaw += 180;
			}*/
			if(acceleration < 0){
				yaw -= 180;
			}
			while(yaw < 0){
				yaw = 360 + yaw;
			}
			while(yaw >= 360){
				yaw = yaw - 360;
			}
			CartOrientationUtil.setYaw(car, yaw);
			/*WrapperPlayServerEntityLook p = new WrapperPlayServerEntityLook();
			p.setEntityID(car.getEntityId());
			p.setYaw(yaw);
			p.setPitch(car.getLocation().getPitch());
			p.sendPacket(player);*/
		}
		// End health calculations

		if (plugin.protocolLib) {
			return;
		}
		/*// Attempt pre-protocollib controls (Broken in MC 1.6.0 and probably above)

		Vector playerVelocity = car.getPassenger().getVelocity();
		ucarUpdateEvent ucarupdate = new ucarUpdateEvent(car,
				playerVelocity, player, CarDirection.NONE);
		plugin.getServer().getPluginManager().callEvent(ucarupdate);
		return;*/
	}

	/*
	 * Performs the actually mechanic for making the cars move
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onUcarUpdate(UCarUpdateEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Boolean modY = true;
		Vehicle vehicle = event.getVehicle();
		
		if(event.getReadCount() > 2){
			UEntityMeta.removeMetadata(vehicle, "car.vec");
			vehicle.removeMetadata("car.vec", UCars.plugin);
		}
		
		Location under = vehicle.getLocation();
		under.setY(vehicle.getLocation().getY() - 1);
		Block underBlock = under.getBlock();
		Block underUnderBlock = underBlock.getRelative(BlockFace.DOWN);
		Block normalBlock = vehicle.getLocation().getBlock();
		// Block up = normalBlock.getLocation().add(0, 1, 0).getBlock();
		final Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		
		if(!(vehicle instanceof Vehicle)){
			return;
		}
		
		if (!carsEnabled) {
			return;
		}
		
		try {
			if (licenseEnabled
					&& !plugin.licensedPlayers.contains(player.getName())) {
				player.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.licenses.noLicense"));
				return;
			}
		} catch (Exception e1) {
		}
		
		Vehicle car = (Vehicle) vehicle;
		
		if(!isACar(car)){
			return;
		}
		
		if (!(player.isInsideVehicle())) {
			return;
		}
		
		//Valid vehicle!
		
		/*Block next = car.getLocation().clone().add(event.getTravelVector().clone().setY(0)).getBlock();
		Block underNext = next.getRelative(BlockFace.DOWN);
		Block underUnderNext = next.getRelative(BlockFace.DOWN, 2);*/
		
		CarHealthData health = this.getCarHealthHandler(car);
		Boolean recalculateHealth = false;
		
		if (car.getVelocity().getY() > 0.01
				&& !UEntityMeta.hasMetadata(car, "car.falling")
				&& !UEntityMeta.hasMetadata(car, "car.ascending")) {
			modY = false;
		}
		if (UEntityMeta.hasMetadata(car, "car.jumping")) {
			if (!UEntityMeta.hasMetadata(car, "car.ascending")) {
				modY = false;
			}
			UEntityMeta.removeMetadata(car, "car.jumping");
		}
		if(car instanceof Minecart) {
			((Minecart)car).setMaxSpeed(5); // Don't allow game breaking speed - but faster
			// than default
		}
		
		// Calculate road blocks
		if (roadBlocksEnabled) {
			/*Location location = car.getLocation().getBlock()
					.getRelative(BlockFace.DOWN).getLocation();*/
			if(!plugin.isBlockEqualToConfigIds(roadBlocks, underBlock)){
				//Not acceleration road block being driven on, so don't move
				return;
			}
		}
		
		Location location = car.getLocation();
		if (!UCars.playersIgnoreTrafficLights && atTrafficLight(car, underBlock, underUnderBlock, location)){
			return; //Being told to wait at acceleration traffic light, don't move
		}
		
		// Calculate default effect blocks
		if (effectBlocksEnabled) {
			if (plugin.isBlockEqualToConfigIds(blockBoost,
					underBlock)
					|| plugin.isBlockEqualToConfigIds(
							blockBoost, underUnderBlock)) {
				carBoost(player.getName(), 20, 6000,
						defaultSpeed);
			}
			if (plugin.isBlockEqualToConfigIds(
					highBlockBoost, underBlock)
					|| plugin.isBlockEqualToConfigIds(
							highBlockBoost, underUnderBlock)) {
				carBoost(player.getName(), 50, 8000,
						defaultSpeed);
			}
			if (plugin.isBlockEqualToConfigIds(
					resetBlockBoost, underBlock)
					|| plugin
							.isBlockEqualToConfigIds(
									resetBlockBoost,
									underUnderBlock)) {
				ResetCarBoost(player.getName(), car,
						defaultSpeed);
			}
		}
		
		Vector travel = event.getTravelVector(); // Travel Vector,
															// fixes
															// controls for
															// 1.6
		float acceleration = 1;
		if(UCars.smoothDrive){ //If acceleration is enabled
			acceleration = ControlInput.getAccel(event.getPlayer(), event.getDir()); //Find out the multiplier to use for accelerating the car 'naturally'
			CarDirection driveDir = ControlInput.getCurrentDriveDir(event.getPlayer());
			if(driveDir.equals(CarDirection.BACKWARDS)){
				acceleration *= 0.2; //0.2 speed backwards
			}
			travel.setX(travel.getX() * acceleration); //Multiple only x
			travel.setZ(travel.getZ() * acceleration); //and z with it (No y acceleration)
		}
		
		Vector directionVector = travel.clone().setY(0).normalize();
		/*try {
			directionVector = (Vector) (car.hasMetadata("ucarsSteeringDir") ? car.getMetadata("ucarsSteeringDir").get(0).value() : travel.clone().normalize());
		} catch (Exception e2) {
			directionVector = travel.clone().normalize();
		}*/

		
		double multiplier = defaultSpeed;
		try {
			if (UCars.carBoosts.containsKey(player.getName())) { // Use the
																	// boost
																	// allocated
				multiplier = UCars.carBoosts.get(player.getName());
			}
		} catch (Exception e1) {
			return;
		}
		
		String underMaterial = under.getBlock().getType().name().toUpperCase();
		int underData = under.getBlock().getData();
		// calculate speedmods
		String key = underMaterial+":"+underData;
		if(speedMods.containsKey(key)){
			if(!UCars.carBoosts.containsKey(player.getName())){
				multiplier = speedMods.get(key);
			}
			else{
				multiplier = (speedMods.get(key)+multiplier)*0.5; //Mean Average of both
			}
		}
		if (event.getDoDivider()) { // Braking or going slower
			multiplier = multiplier * event.getDivider();
		}
		
		travel = travel.setX(travel.getX()*multiplier);
		travel = travel.setZ(travel.getZ()*multiplier); 
		if (usePerms) {
			if (!player.hasPermission("ucars.cars")) {
				player.sendMessage(UCars.colors.getInfo()
						+ Lang.get("lang.messages.noDrivePerm"));
				return;
			}
		}
		
		/*if (normalBlock.getType() != Material.AIR //Air
				&& normalBlock.getType() != Material.WATER //Water
				&& normalBlock.getType() != Material.STATIONARY_WATER //Water
				&& normalBlock.getType() != Material.STEP //Slab
				&& normalBlock.getType() != Material.DOUBLE_STEP //Double slab
				&& normalBlock.getType() != Material.LONG_GRASS //Long grass
				&& !normalBlock.getType().name().toLowerCase()
						.contains("stairs")) {
			// Stuck in acceleration block
			car.setVelocity(new Vector(0, 0.5, 0));
		}*/
		
		Location before = car.getLocation();
		//float direction = player.getLocation().getYaw();
		float direction = car.getLocation().clone().setDirection(travel).getYaw();
		BlockFace face = ClosestFace.getClosestFace(direction);
		// before.add(face.getModX(), face.getModY(),
		// face.getModZ());

		//Read the vehicle length if it exists
		double length = 0;
		Object carNMSHandle = Reflect.getHandle(car);
		Field lenField = Reflect.getField(Reflect.getNMSClass("Entity"),"width");
		if(!lenField.isAccessible()){
			lenField.setAccessible(true);
		}
		try {
			length = lenField.getDouble(carNMSHandle);
		} catch (Exception e) {
			e.printStackTrace();
			length = 0;
		}

		double fx = travel.getX()*1;
		if (Math.abs(fx) > 1) {
			fx = face.getModX();
		}
		double fz = travel.getZ()*1;
		if (Math.abs(fz) > 1) {
			fz = face.getModZ();
		}

		//Compute unit vector in car direction of travel
		Vector unitVector = new Vector(face.getModX(),0,face.getModZ());
		if(unitVector.lengthSquared() > 1){
			unitVector.multiply(1/(double)Math.sqrt(2));
		}

		Vector faceDirVector = new Vector(fx, face.getModY(), fz);
		before=before.add(faceDirVector);
		//Add the length of the car in so that we are able to climb up blocks with an entity that has length
		Vector toRightOfFaceDir = unitVector.clone().crossProduct(new Vector(0,1,0));
		before=before.add(unitVector.clone().multiply(length*0.5));
		Location frontRight = before.clone().add(unitVector.clone().multiply(length*0.5));
		Location frontLeft = before.clone().add(unitVector.clone().multiply(length*-0.5));
		Block block = before.getBlock(); //Block we're driving into
		Block frontRightInFront = frontRight.getBlock();
		Block frontLeftInFront = frontLeft.getBlock();
		//Hackish way to make this able to jump for wider vehicles
		if(ignoreJump.contains(block.getType().name())){
			if(!ignoreJump.contains(frontRightInFront.getType().name())){
				block = frontRightInFront;
			}
			else if(!ignoreJump.contains(frontLeftInFront.getType().name())){
				block = frontLeftInFront;
			}
		}
		Block above = block.getRelative(BlockFace.UP);
		
		/*if((!(block.isEmpty() || block.isLiquid())
				&& !(above.isEmpty() || above.isLiquid())
				&& !(block.getType().name().toLowerCase().contains("step"))
				*//*&& !(above.getType().name().toLowerCase().contains("step"))*//*)
		){
			*//*ControlInput.setAccel(player, 0); //They hit acceleration wall head on*//*
		}*/
		
		// Calculate collision health
		if (block.getType().equals(Material.CACTUS)) {
			double damage = damageCactus;
			if (damage > 0) {
				double max = defaultHealth;
				double left = health.getHealth() - damage;
				ChatColor color = ChatColor.YELLOW;
				if (left > (max * 0.66)) {
					color = ChatColor.GREEN;
				}
				if (left < (max * 0.33)) {
					color = ChatColor.RED;
				}
				player.sendMessage(ChatColor.RED + "-" + damage + "["
						+ Material.CACTUS.name().toLowerCase() + "]"
						+ color + " (" + left + ")");
				health.damage(damage, car);
				recalculateHealth = true;
			}
		}
		// End calculations for collision health
		
		if (fuelEnabled
				&& !fuelUseItems
				&& !player.hasPermission(fuelBypassPerm)) {
			double fuel = 0;
			if (UCars.fuel.containsKey(player.getName())) {
				fuel = UCars.fuel.get(player.getName());
			}
			if (fuel < 0.1) {
				player.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.fuel.empty"));
				return;
			}
			int amount = 0 + (int) (Math.random() * 250);
			if (amount == 10) {
				fuel = fuel - 0.1;
				fuel = (double) Math.round(fuel * 10) / 10;
				UCars.fuel.put(player.getName(), fuel);
			}
		}
		else if (fuelEnabled
				&& fuelUseItems
				&& !player.hasPermission(fuelBypassPerm)) {
			// item fuel - Not for laggy servers!!!
			double fuel = 0;
			ArrayList<ItemStack> items = plugin.UFuelItems;
			Inventory inventory = player.getInventory();
			for (ItemStack item : items) {
				if (inventory.contains(item.getType(), 1)) {
					fuel = fuel + 0.1;
				}
			}
			if (fuel < 0.1) {
				player.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.fuel.empty"));
				return;
			}
			int amount = 0 + (int) (Math.random() * 150);
			if (amount == 10) {
				// remove item
				Boolean taken = false;
				Boolean last = false;
				int toUse = 0;
				for (int i = 0; i < inventory.getContents().length; i++) {
					ItemStack item = inventory.getItem(i);
					Boolean ignore = false;
					try {
						item.getType();
					} catch (Exception e) {
						ignore = true;
					}
					if (!ignore) {
						if (!taken) {
							if(plugin.isItemOnList(items, item)){
								taken = true;
								if (item.getAmount() < 2) {
									last = true;
									toUse = i;
								}
								item.setAmount((item.getAmount() - 1));
							}
						}
					}
				}
				if (last) {
					inventory.setItem(toUse, new ItemStack(Material.AIR));
				}
			}
		}
		
		/*if (travel.getY() < 0) { //Custom gravity
			double a1 = multiplier*acceleration;
			if(a1 < 1){
				a1 = 1;
			}
			double newy = travel.getY() - (Math.abs(travel.getY())*0.02d)/a1;
			if(newy < -5){
				newy = -5;
			}
			if(newy > 0){
				newy = -0.2;
			}
			travel.setY(newy);
		}*/
		
		Material blockType = block.getType();
		int blockData = block.getData();
		Boolean fly = false; // Fly is the 'easter egg' slab elevator
		if (normalBlock.getRelative(face).getType() == Material.STEP) {
			// If looking at slabs
			fly = true;
		}
		/*
		 * if(bbb.getType()==Material.STEP && !(bbb.getData() != 0)){ //If
		 * in acceleration slab block fly = true; }
		 */
		if (effectBlocksEnabled) { //Has to be in this order for things to function properly - Cannot be merged with earlier effect block handling
			if (plugin.isBlockEqualToConfigIds(jumpBlock,
					underBlock)
					|| plugin.isBlockEqualToConfigIds(
							jumpBlock, underUnderBlock)) {
				double y = UCarJumpAmount;
				UEntityMeta.setMetadata(car, "car.jumpUp", new StatValue(UCarJumpAmount, plugin));
				travel.setY(y);
				car.setVelocity(travel);
			}
			if (plugin.isBlockEqualToConfigIds(
					teleportBlock, underBlock)
					|| plugin.isBlockEqualToConfigIds(
							teleportBlock, underUnderBlock)) {
				// teleport the player
				Sign sign = null;
				if (underUnderBlock.getState() instanceof Sign) {
					sign = (Sign) underUnderBlock.getState();
				}
				if (underUnderBlock.getRelative(BlockFace.DOWN).getState() instanceof Sign) {
					sign = (Sign) underUnderBlock.getRelative(BlockFace.DOWN)
							.getState();
				}
				if (sign != null) {
					String[] lines = sign.getLines();
					if (lines[0].equalsIgnoreCase("[Teleport]")) {
						Boolean raceCar = false;
						if (car.hasMetadata("kart.racing")
								|| UEntityMeta.hasMetadata(car, "kart.racing")) {
							raceCar = true;
						}
						UEntityMeta.setMetadata(car, "safeExit.ignore", new StatValue(null, plugin));
						car.eject();
						
						UUID carId = car.getUniqueId();
						
						car.remove();
						
						final Vehicle finalCar = car;
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

							@Override
							public void run() {
								if(finalCar != null){
									finalCar.remove(); //For uCarsTrade
								}
								return;
							}}, 2l);
						
						String xs = lines[1];
						String ys = lines[2];
						String zs = lines[3];
						Boolean valid = true;
						double x = 0, y = 0, z = 0;
						try {
							x = Double.parseDouble(xs);
							y = Double.parseDouble(ys);
							y = y + 0.5;
							z = Double.parseDouble(zs);
						} catch (NumberFormatException e) {
							valid = false;
						}
						if (valid) {
							List<MetadataValue> metas = null;
							if (player.hasMetadata("car.stayIn") || UEntityMeta.hasMetadata(player, "car.stayIn")) {
								metas = player.getMetadata("car.stayIn");
								List<MetadataValue> others = UEntityMeta.getMetadata(player, "car.stayIn");
								if(others != null){
									metas.addAll(others);
								}
								for (MetadataValue val : metas) {
									player.removeMetadata("car.stayIn",
											val.getOwningPlugin());
									UEntityMeta.removeMetadata(player, "car.stayIn");
								}
							}
							Location toTele = new Location(sign.getWorld(), x,
									y, z);
							Chunk ch = toTele.getChunk();
							if (ch.isLoaded()) {
								ch.load(true);
							}
							car = (Vehicle) sign.getWorld().spawnEntity(
									toTele, EntityType.MINECART);
							final Vehicle vehicule = car;
							UEntityMeta.setMetadata(car, "carhealth", health);
							if (raceCar) {
								UEntityMeta.setMetadata(car, "kart.racing", new StatValue(null, plugin));
							}
							uCarRespawnEvent newEvent = new uCarRespawnEvent(car, carId, car.getUniqueId(),
									CarRespawnReason.TELEPORT);
							plugin.getServer().getPluginManager().callEvent(newEvent);
							if(newEvent.isCancelled()){
								car.remove();
							}
							else{
								player.sendMessage(UCars.colors.getTp()
										+ "Teleporting...");
								car.setPassenger(player);
								final Vehicle finalCar1 = car;
								Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){

									@Override
									public void run() {
										finalCar1.setPassenger(player); //For the sake of uCarsTrade
										return;
									}}, 2l);
								car.setVelocity(travel);
								if (metas != null) {
									for (MetadataValue val : metas) {
										UEntityMeta.setMetadata(player, "car.stayIn", val);
										player.setMetadata("car.stayIn", val);
									}
								}
								plugin.getAPI().updateUCarMeta(carId,
										car.getUniqueId());
							}
						}
					}
				}
			}
		}
		
		// actually jump up acceleration block if needed:
		Location newLocation = block.getLocation();
		Location newLocationUp = block.getLocation().add(0, 1, 0);
		Material upBlockMaterial = newLocationUp.getBlock().getType();
		Boolean cont = true;
		// check it's not acceleration barrier
		cont = !plugin.isBlockEqualToConfigIds(barriers, block) && !plugin.isBlockEqualToConfigIds(barriers, frontLeftInFront) && !plugin.isBlockEqualToConfigIds(barriers, frontRightInFront);
		
		Boolean inStairs = false;
		Material carBlock = car.getLocation().getBlock().getType();
		if (carBlock.name().toLowerCase().contains("stairs")) {
			inStairs = true;
		}
		if (UEntityMeta.hasMetadata(car, "car.ascending")) {
			UEntityMeta.removeMetadata(car, "car.ascending");
		}
		//player.sendMessage(blockType+" "+face+" "+fx+" "+fz);
		// Make cars jump if needed
		if (inStairs ||
				 (!ignoreJump.contains(blockType.name().toUpperCase()) && cont && modY)) { //Should jump
/*			player.sendMessage("Obstruction ahead");
			player.sendMessage("above: "+upBlockMaterial);*/
			if (upBlockMaterial == Material.AIR || upBlockMaterial == Material.LAVA
					|| upBlockMaterial == Material.STATIONARY_LAVA || upBlockMaterial == Material.WATER
					|| upBlockMaterial == Material.STATIONARY_WATER /*|| upBlockMaterial == Material.STEP */
					|| upBlockMaterial == Material.CARPET
					/*|| upBlockMaterial == Material.DOUBLE_STEP*/ || inStairs) { //Clear air above
				newLocation.add(0, 1.5d, 0);
				Boolean calculated = false;
				double y = 1.1;
				if (block.getType().name().toLowerCase().contains("step")) {
					calculated = true;
					y = 1.2;
				}
				if (carBlock.name().toLowerCase().contains("step")) { // In
																		// acceleration
																		// step
																		// block
																		// and
																		// trying
																		// to
																		// jump
					calculated = true;
					y = 0.6;
				}
				if (carBlock.name().toLowerCase()
						.contains(Pattern.quote("stairs"))
						// ||
						// underBlock.getType().name().toLowerCase().contains(Pattern.quote("stairs"))
						|| block.getType().name().toLowerCase()
								.contains(Pattern.quote("stairs"))
						|| inStairs) {
					calculated = true;
					y = 0.6;
					// ascend stairs
				}
				Boolean ignore = false;
				if (car.getVelocity().getY() > 4) {
					// if car is going up already then dont do ascent
					ignore = true;
				}
				if (!ignore) {
					// Do ascent
					travel.setY(block.getY()+y-car.getLocation().getY());
					if (calculated) {
						UEntityMeta.setMetadata(car, "car.jumping", new StatValue(null,
								plugin));
					} else {
						UEntityMeta.setMetadata(car, "car.jumpFull", new StatValue(null,
								plugin));
					}
				}
			}
			if (fly && cont) {
				// Make the car ascend (easter egg, slab elevator)
				travel.setY(0.1); // Make acceleration little easier
				UEntityMeta.setMetadata(car, "car.ascending", new StatValue(null, plugin));
			}
			// Move the car and adjust vector to fit car stats
			car.setVelocity(calculateCarStats(car, player, travel,
					multiplier));
		} else {
			if (fly) {
				// Make the car ascend (easter egg, slab elevator)
				travel.setY(0.1); // Make acceleration little easier
				UEntityMeta.setMetadata(car, "car.ascending", new StatValue(null, plugin));
			}
			// Move the car and adjust vector to fit car stats
			car.setVelocity(calculateCarStats(car, player, travel,
					multiplier));
		}
		
		// Recalculate car health
		if (recalculateHealth) {
			updateCarHealthHandler(car, health);
		}
		return;
	}

	/*
	 * This disables fall damage whilst driving a car
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	void safeFly(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player) || event.getCause() != DamageCause.FALL) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (inACar(player.getName())) {
			Vector vel = player.getVehicle().getVelocity();
			if (!(vel.getY() > -0.1 && vel.getY() < 0.1)) {
				event.setCancelled(true);
			} /*else {
				try {
					player.damage(event.getDamage());
				} catch (Exception e) {
					// Damaging failed
				}
			}*/

		}
		return;
	}

	/*
	 * This provides effects and health changes when cars collide with entities
	 */
	@EventHandler
	void hitByCar(VehicleEntityCollisionEvent event) {
		if(event.isCancelled()){
			return;
		}
		Vehicle vehicle = event.getVehicle();
		if (!(vehicle instanceof Vehicle)) {
			return;
		}
		final Vehicle cart = (Vehicle) vehicle;
		if (!isACar(cart)) {
			return;
		}
		Entity entity = event.getEntity(); //copCar
		if(((cart.hasMetadata("trade.npc") && entity.hasMetadata("trade.npcvillager"))
				|| UEntityMeta.hasMetadata(cart, "trade.npc") && UEntityMeta.hasMetadata(entity, "trade.npcvillager"))
				|| ((cart.hasMetadata("trade.npc") && entity.getVehicle() != null && entity.getVehicle().hasMetadata("trade.npc"))
						&& UEntityMeta.hasMetadata(cart, "trade.npc") && entity.getVehicle() != null && UEntityMeta.hasMetadata(entity.getVehicle(), "trade.npc"))){
			event.setCancelled(true);
			event.setCollisionCancelled(false);
			return;
		}
		if(UEntityMeta.hasMetadata(entity, "IGNORE_COLLISIONS")){
			event.setCancelled(true);
			event.setCollisionCancelled(false);
			return;
		}
		/*if(cart.hasMetadata("copCar") || UEntityMeta.hasMetadata(cart, "copCar")){ 
			Bukkit.broadcastMessage("CANCELLED AS COP CAR");
			event.setCancelled(true);
			event.setCollisionCancelled(false);
			return;
		}*/
		if (cart.getPassenger() == null) { //Don't both to calculate with PiguCarts, etc...
			return;
		}
		
		Entity passenger = cart.getPassenger();
		while (passenger.getPassenger() != null) {
			passenger = passenger.getPassenger();
		}
		if(passenger.equals(entity) || cart.getPassengers().contains(entity)){
			return; //Player being hit is in the car
		}
		
		if(entity.hasMetadata("copCar") || UEntityMeta.hasMetadata(entity, "copCar") || (entity.getVehicle() != null && (entity.getVehicle().hasMetadata("copCar") || UEntityMeta.hasMetadata(entity.getVehicle(), "copCar")))){
			if(!(passenger instanceof Player)){
				event.setCancelled(true);
				event.setCollisionCancelled(false);
				return;
			}
		}
		
		if(UEntityMeta.hasMetadata(entity, "hitByLast")){
			try {
				long l = (Long) UEntityMeta.getMetadata(entity, "hitByLast").get(0).value();
				long pastTime = System.currentTimeMillis() - l;
				if(pastTime < 500){
					return; //Don't get hit by more than once at a time
				}
				else {
					UEntityMeta.removeMetadata(entity, "hitByLast");
				}
			} catch (Exception e) {
				UEntityMeta.removeMetadata(entity, "hitByLast");
			}
		}
		UEntityMeta.removeMetadata(entity, "hitByLast");
		UEntityMeta.setMetadata(entity, "hitByLast", new StatValue(System.currentTimeMillis(), UCars.plugin));
		
		/*double accel = 1;
		if(passenger instanceof Player){
			accel = ControlInput.getAccel(((Player)passenger), CarDirection.FORWARDS);
		}
		else {
			accel = UEntityMeta.hasMetadata(cart, "currentlyStopped") ? 0:1;
		}*/
		Vector velocity = cart.getVelocity();
		double speed = velocity.length() * 1.6; /*
		if(passenger instanceof Villager){ //NPC car from UT
			speed = cart.getVelocity().length()*1.6;
		}*/

		double damage = hitByCrashDamage;
		double passengerDamage = (damage * speed * 2);
		if(passengerDamage < 1){
			passengerDamage = 1;
		}
		if(passengerDamage > (hitByCrashDamage * 1.5)){
			passengerDamage = hitByCrashDamage * 1.5;
		}
		if(passengerDamage > 8){
			passengerDamage = 8;
		}
		
		Entity driver = getDrivingPassengerOfCar(vehicle);
		
		if (speed > 0) {
			CarHealthData health = getCarHealthHandler(cart);
			double damage1 = crashDamage;
			if (damage1 > 0) {
				if (cart.getPassenger() instanceof Player) {
					double max = defaultHealth;
					double left = health.getHealth() - damage1;
					ChatColor color = ChatColor.YELLOW;
					if (left > (max * 0.66)) {
						color = ChatColor.GREEN;
					}
					if (left < (max * 0.33)) {
						color = ChatColor.RED;
					}
					((Player) cart.getPassenger())
							.sendMessage(ChatColor.RED + "-" + ((int)damage1) + "[crash]"
									+ color + " (" + ((int)left) + ")");
				}
				health.damage(damage1, cart);
			}
			updateCarHealthHandler(cart, health);
		}
		if (speed <= 0) {
			return;
		}
		if (!UCars.config.getBoolean("general.cars.hitBy.enable")) {
			return;
		}
		if (UCars.config.getBoolean("general.cars.hitBy.enableMonsterDamage")) {
			if (entity instanceof Monster || (UCars.config.getBoolean("general.cars.hitBy.enableAllMonsterDamage") && entity instanceof Damageable)) {
				if(entity instanceof Villager && entity.getVehicle() != null && passenger instanceof Villager){
					return;
				}
				UCarCrashEvent evt = new UCarCrashEvent(cart, entity, passengerDamage);
				if(evt.isCancelled()){
					return;
				}
				passengerDamage = evt.getDamageToBeDoneToTheEntity();
				
				double multiplier = UCars.config
						.getDouble("general.cars.hitBy.power") / 7;
				entity.setVelocity(cart.getVelocity().clone().setY(0.5).multiply(multiplier));
				
				if(driver != null && driver.equals(entity)){
					
				}
				else if(driver != null){
					((Damageable) entity).damage(passengerDamage, driver);
				}
				else {
					((Damageable) entity).damage(passengerDamage);
				}
			}
		}
		
		boolean player = entity instanceof Player;
		Player player1 = null;
		if(player){
			player1 = (Player) entity;
		}
		if (!(player)) {
			return;
		}
		if(player1 != null){
			if (inACar(player1)) {
				return;
			}
		}
		
		UCarCrashEvent evt = new UCarCrashEvent(cart, entity, passengerDamage);
		Bukkit.getPluginManager().callEvent(evt);
		if(evt.isCancelled()){
			return;
		}
		passengerDamage = evt.getDamageToBeDoneToTheEntity();
		
		double multiplier = UCars.config.getDouble("general.cars.hitBy.power") / 5;
		entity.setVelocity(cart.getVelocity().clone().setY(0.5).multiply(multiplier));
		if(player1 != null){
			player1.sendMessage(UCars.colors.getInfo()
				+ Lang.get("lang.messages.hitByCar"));
		}
		/*player1.sendMessage("Speed: "+speed);
		player1.sendMessage("Crash dmg def: "+hitby_crash_damage);
		player1.sendMessage("Damage to do: "+passengerDamage);*/
		if(entity instanceof LivingEntity){
			((LivingEntity)entity).damage(passengerDamage, driver);
		}
		return;
	}

	/*
	 * This places cars and other interacting features
	 */
	@EventHandler
	void interact(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if(event.getHand() == null){
			return;
		}
		if(event.getHand().equals(EquipmentSlot.OFF_HAND)){
			return;
		}
		if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		Block block = event.getClickedBlock();
		if (UCarsAPI.getAPI().isuCarsHandlingPlacingCars() && (plugin.API.hasItemCarCheckCriteria() || event.getPlayer().getItemInHand().getType() == Material.MINECART)) {
			// Its a minecart!
			Material iar = block.getType();
			if (UCars.ignoreRails && (iar == Material.RAILS || iar == Material.ACTIVATOR_RAIL
					|| iar == Material.POWERED_RAIL || iar == Material.DETECTOR_RAIL)) {
				return;
			}
			if (!PlaceManager.placeableOn(iar.name().toUpperCase(), block.getData())) {
				return;
			}
			if (!UCars.config.getBoolean("general.cars.enable")) {
				return;
			}
			if (UCars.config.getBoolean("general.cars.placePerm.enable")) {
				String permission = UCars.config
						.getString("general.cars.placePerm.perm");
				if (!event.getPlayer().hasPermission(permission)) {
					String noPermission = Lang.get("lang.messages.noPlacePerm");
					noPermission = noPermission.replaceAll("%perm%", permission);
					event.getPlayer().sendMessage(
							UCars.colors.getError() + noPermission);
					return;
				}
			}
			if (event.isCancelled()) {
				event.getPlayer().sendMessage(
						UCars.colors.getError()
								+ Lang.get("lang.messages.noPlaceHere"));
				return;
			}
			if(!plugin.API.runCarChecks(event.getPlayer().getItemInHand())){
				return;
			}
			Location location = block.getLocation().add(0, 1.5, 0);
			location.setYaw(event.getPlayer().getLocation().getYaw() + 270);
			final Minecart car = (Minecart) event.getPlayer().getWorld()
					.spawnEntity(location, EntityType.MINECART);
			float yaw = event.getPlayer().getLocation().getYaw()+90;
			if(yaw < 0){
				yaw = 360 + yaw;
			}
			else if(yaw >= 360){
				yaw = yaw - 360;
			}
			CartOrientationUtil.setYaw(car, yaw);
			updateCarHealthHandler(car, getCarHealthHandler(car));
			/*
			 * Location carloc = car.getLocation();
			 * carloc.setYaw(event.getPlayer().getLocation().getYaw() + 270);
			 * car.setVelocity(new Vector(0,0,0)); car.teleport(carloc);
			 * car.setVelocity(new Vector(0,0,0));
			 */
			event.getPlayer().sendMessage(
					UCars.colors.getInfo() + Lang.get("lang.messages.place"));
			event.getPlayer().sendMessage(UCars.colors.getInfo()+"You can also use 'jump' to change driving mode!");
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
				ItemStack placed = event.getPlayer().getItemInHand();
				placed.setAmount(placed.getAmount() - 1);
				event.getPlayer().getInventory().setItemInHand(placed);
			}
		}
		if (inACar(event.getPlayer())) {
			if (UCars.config.getBoolean("general.cars.fuel.enable")) {
				if (plugin.isItemEqualToConfigIds(UCars.config.getStringList(
						"general.cars.fuel.check"), event.getPlayer().getItemInHand())) {
					event.getPlayer().performCommand("ufuel view");
				}
			}
		}
		if(UCars.config.getBoolean("general.cars.boostsEnable")){
			return;
		}
		List<String> lowBoostRaw = UCars.config.getStringList("general.cars.lowBoost");
		List<String> medBoostRaw = UCars.config.getStringList("general.cars.medBoost");
		List<String> highBoostRaw = UCars.config.getStringList("general.cars.highBoost");
		// int LowBoostId = ucars.config.getInt("general.cars.lowBoost");
		// int MedBoostId = ucars.config.getInt("general.cars.medBoost");
		// int HighBoostId = ucars.config.getInt("general.cars.highBoost");
		ItemStack inHand = event.getPlayer().getItemInHand();
		String boosterMaterial = inHand.getType().name().toUpperCase(); // booster material name
		int boosterData = inHand.getDurability();
		ItemStack remove = inHand.clone();
		remove.setAmount(1);
		if (ItemStackFromId.equals(lowBoostRaw, boosterMaterial, boosterData)) {
			if (inACar(event.getPlayer())) {
				boolean boosting = carBoost(event.getPlayer().getName(), 10,
						3000, UCars.config.getDouble("general.cars.defSpeed"));
				if (boosting) {
					if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
						// they r in survival
						event.getPlayer().getInventory()
								.removeItem(remove);
					}
					event.getPlayer().sendMessage(
							UCars.colors.getSuccess()
									+ Lang.get("lang.boosts.low"));
					return;
				} else {
					event.getPlayer().sendMessage(
							UCars.colors.getError()
									+ Lang.get("lang.boosts.already"));
				}
				return;
			}
		}
		if (ItemStackFromId.equals(medBoostRaw, boosterMaterial, boosterData)) {
			if (inACar(event.getPlayer())) {
				boolean boosting = carBoost(event.getPlayer().getName(), 20,
						6000, UCars.config.getDouble("general.cars.defSpeed"));
				if (boosting) {
					if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
						// they r in survival
						event.getPlayer().getInventory()
								.removeItem(remove);
					}
					event.getPlayer().sendMessage(
							UCars.colors.getSuccess()
									+ Lang.get("lang.boosts.med"));
					return;
				} else {
					event.getPlayer().sendMessage(
							UCars.colors.getError()
									+ Lang.get("lang.boosts.already"));
				}
				return;
			}
		}
		if (ItemStackFromId.equals(highBoostRaw, boosterMaterial, boosterData)) {
			if (inACar(event.getPlayer())) {
				boolean boosting = carBoost(event.getPlayer().getName(), 50,
						10000, UCars.config.getDouble("general.cars.defSpeed"));
				if (boosting) {
					if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
						// they r in survival
						event.getPlayer().getInventory()
								.removeItem(remove);
					}
					event.getPlayer().sendMessage(
							UCars.colors.getSuccess()
									+ Lang.get("lang.boosts.high"));
					return;
				} else {
					event.getPlayer().sendMessage(
							UCars.colors.getError()
									+ Lang.get("lang.boosts.already"));
				}
				return;
			}
		}

		return;
	}

	/*
	 * This controls the [ufuel] signs
	 */
	@EventHandler
	void signInteract(PlayerInteractEvent event) {
		if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		Block block = event.getClickedBlock();
		// [ufuel]
		// buy/sell
		// how many litres
		if (!(block.getState() instanceof Sign)) {
			return;
		}
		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		if (!lines[0].equalsIgnoreCase("[uFuel]")) {
			return;
		}
		event.setCancelled(true);
		String action = lines[1];
		String quantity = lines[2];
		double amount = 0;
		try {
			amount = Double.parseDouble(quantity);
		} catch (NumberFormatException e) {
			return;
		}
		if (action.equalsIgnoreCase("buy")) {
			String[] args = new String[] { "buy", "" + amount };
			plugin.cmdExecutor.ufuel(event.getPlayer(), args);
		} else if (action.equalsIgnoreCase("sell")) {
			String[] args = new String[] { "sell", "" + amount };
			plugin.cmdExecutor.ufuel(event.getPlayer(), args);
		} else {
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	void minecartBreak(VehicleDamageEvent event) {
		if (!(event.getVehicle() instanceof Vehicle)
				|| !(event.getAttacker() instanceof Player)) {
			return;
		}
		if (event.isCancelled()) {
			return;
		}
		final Vehicle car = (Vehicle) event.getVehicle();
		Player player = (Player) event.getAttacker();
		if (!isACar(car)) {
			return;
		}
		if (!UCars.config.getBoolean("general.cars.health.overrideDefault")) {
			return;
		}
		CarHealthData health = getCarHealthHandler(car);
		double damage = UCars.config
				.getDouble("general.cars.health.punchDamage");
		if (event.getDamage() > 0 && damage > 0) {
			double max = UCars.config.getDouble("general.cars.health.default");
			double left = health.getHealth() - damage;
			ChatColor color = ChatColor.YELLOW;
			if (left > (max * 0.66)) {
				color = ChatColor.GREEN;
			}
			if (left < (max * 0.33)) {
				color = ChatColor.RED;
			}
			if (left < 0) {
				left = 0;
			}
			player.sendMessage(ChatColor.RED + "-" + damage + ChatColor.YELLOW
					+ "[" + player.getName() + "]" + color + " (" + left + ")");
			health.damage(damage, car, player);
			updateCarHealthHandler(car, health);
			event.setCancelled(true);
			event.setDamage(0);
		} else{
			event.setCancelled(true);
			event.setDamage(0);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	void carDeath(UCarDeathEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Entity cart = event.getCar();
		if(cart.hasMetadata("car.destroyed") || UEntityMeta.hasMetadata(cart, "car.destroyed")){
			return;
		}
		UEntityMeta.setMetadata(cart, "car.destroyed", new StatValue(true, UCars.plugin));
		cart.removeMetadata("car.vec", UCars.plugin);
		UEntityMeta.removeMetadata(cart, "car.vec");
		cart.eject();
		Location location = cart.getLocation();
		cart.remove();
		location.getWorld().dropItemNaturally(location, new ItemStack(Material.MINECART));
		return;
	}
	
	@EventHandler
	void wirelessRedstone(BlockRedstoneEvent event){
		Block block = event.getBlock();
		if(!block.getType().equals(Material.REDSTONE_LAMP_ON) && !block.getType().equals(Material.REDSTONE_LAMP_OFF)){
			return;
		}
		boolean powered = block.isBlockPowered();
		Sign sign = null;
		for(BlockFace dir:BlockFace.values()){
			Block bd = block.getRelative(dir);
			if(bd.getState() instanceof Sign){
				sign = (Sign) bd.getState();
			}
		}
		if(sign == null){
			return;
		}
		
		if(sign.getLine(0) == null || !sign.getLine(0).equalsIgnoreCase("[wir]")){ //Not wireless redstone
			return;
		}
		String otherLoc = sign.getLine(1);
		if(otherLoc == null){ //Match positive and negative numbers
			return; //Invalid sign
		}
		String[] parts = otherLoc.split(",");
		if(parts.length < 3){
			return;
		}
		try {
			int x,y,z;
			if(otherLoc.matches("-*\\d+,-*\\d+,-*\\d+")){
				x = Integer.parseInt(parts[0]);
				y = Integer.parseInt(parts[1]);
				z = Integer.parseInt(parts[2]);
			}
			else {
				//Invalid pattern
				return;
			}
			
			Block otherBlock = block.getWorld().getBlockAt(x, y, z);
			otherBlock.getLocation().getChunk(); //Make sure it's loaded
			if(powered){ //Set to redstone block
				otherBlock.setType(Material.REDSTONE_BLOCK);
			}
			else { //Set to glass
				otherBlock.setType(Material.AIR);
			}
		} catch (Exception e) {
			//Not integers
			return;
		}
	}
	
	private int getCoordinate(String input, int current) throws Exception{
		if(input.matches("-*\\d+")){
			try {
				return Integer.parseInt(input);
			} catch (Exception e) {
				//Not an int
				throw new Exception();
			}
		}
		else if(input.matches("~-*\\d+") && input.length() > 1){
			try {
				return Integer.parseInt(input.substring(1))+current;
			} catch (Exception e) {
				//Not an int
				throw new Exception();
			}
		}
		else {
			//Not formatted right
			throw new Exception();
		}
	}
	
	@EventHandler
	void quit(PlayerQuitEvent event){
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLaterAsynchronously(UCars.plugin, new Runnable(){

			@Override
			public void run() {
				if(!player.isOnline()) {
					UEntityMeta.removeAllMeta(player);
				}
				return;
			}}, 100l);
	}

	@EventHandler
	void quit(PlayerKickEvent event){
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(UCars.plugin, new Runnable(){

			@Override
			public void run() {
				UEntityMeta.removeAllMeta(player);
				return;
			}}, 100l);
	}
	
	@EventHandler
	void trafficIndicators(BlockRedstoneEvent event){
		Block block = event.getBlock();
		if(!block.getType().equals(Material.REDSTONE_LAMP_ON) && !block.getType().equals(Material.REDSTONE_LAMP_OFF)){
			return;
		}
		boolean powered = block.isBlockPowered();
		Sign sign = null;
		for(BlockFace dir: directions()){
			Block bd = block.getRelative(dir);
			if(bd.getState() instanceof Sign){
				sign = (Sign) bd.getState();
			}
		}
		if(sign == null){
			return;
		}
		
		if(sign.getLine(1) == null || !sign.getLine(1).equalsIgnoreCase("[trafficlight]")){ //Not wireless redstone
			return;
		}
		String otherLocation = sign.getLine(2);
		if(otherLocation == null){ //Match positive and negative numbers
			return; //Invalid sign
		}
		String[] parts = otherLocation.split(",");
		if(parts.length < 3){
			return;
		}
		try {
			int x,y,z;
			if(otherLocation.matches(".+,.+,.+")){
				try {
					x = getCoordinate(parts[0], sign.getX());
					y = getCoordinate(parts[1], sign.getY());
					z = getCoordinate(parts[2], sign.getZ());
				} catch (Exception e1) { //Badly formatted
					return;
				}
			}
			else {
				//Invalid pattern
				return;
			}
			
			Block otherBlock = block.getWorld().getBlockAt(x, y, z);
			for(Entity e:otherBlock.getLocation().getChunk().getEntities()){
				if(e.getLocation().distanceSquared(otherBlock.getLocation()) < 4){ //Within 2 blocks of the loc given
					if(e instanceof ItemFrame){
						ItemFrame itemFrame = (ItemFrame) e;
						if(powered){
							itemFrame.setItem(new ItemStack(Material.EMERALD_BLOCK));
						}
						else {
							itemFrame.setItem(new ItemStack(Material.REDSTONE_BLOCK));
						}
					}
				}
			}
		} catch (Exception e) {
			//Not integers
			return;
		}
	}
	
	public Boolean atTrafficLight(Entity car, Block underBlock, Block underUnderBlock, Location location){
		if (trafficLightsEnabled) {
			if (plugin.isBlockEqualToConfigIds(
					trafficLightRawIds, underBlock)
					|| plugin.isBlockEqualToConfigIds(
							trafficLightRawIds,
							underUnderBlock)
							|| plugin.isBlockEqualToConfigIds(
									trafficLightRawIds,
									underUnderBlock.getRelative(BlockFace.DOWN))
									|| plugin.isBlockEqualToConfigIds(
											trafficLightRawIds,
											underUnderBlock.getRelative(BlockFace.DOWN, 2))
											) {
				
				Boolean found = false;
				Boolean powered = false;
				int radius = 3;
				int radiusSquared = radius * radius;
				for (int x = -radius; x <= radius && !found; x++) {
					for (int z = -radius; z <= radius && !found; z++) {
						if ((x * x) + (z * z) <= radiusSquared) {
							double locX = location.getX() + x;
							double locZ = location.getZ() + z;
							for (int y = (int) Math.round((location.getY() - 4)); y < (location
									.getY() + 4) && !found; y++) {
								Location light = new Location(
										location.getWorld(), locX, y, locZ);
								if (light.getBlock().getType() == Material.REDSTONE_LAMP_OFF) {
									if (trafficLightSignOn(light.getBlock())) {
										found = true;
										powered = false;
									}
								} else if (light.getBlock().getType() == Material.REDSTONE_TORCH_ON) {
									if (trafficLightSignOn(light.getBlock())) {
										found = true;
										powered = true;
									}
								}
							}
						}
					}
				}
				if (found) {
					if (!powered) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static BlockFace[] directions(){
		return new BlockFace[]{
				BlockFace.NORTH,
				BlockFace.EAST,
				BlockFace.SOUTH,
				BlockFace.WEST,
				BlockFace.NORTH_WEST,
				BlockFace.NORTH_EAST,
				BlockFace.SOUTH_EAST,
				BlockFace.NORTH_WEST,
		};
	}
	
	public void updateCarHealthHandler(Entity car, CarHealthData handler){
		UEntityMeta.removeMetadata(car, "carhealth");
		UEntityMeta.setMetadata(car, "carhealth", new StatValue(handler, UCars.plugin));
	}
	
	public CarHealthData getCarHealthHandler(final Entity car){
		CarHealthData health = null;
		if (UEntityMeta.hasMetadata(car, "carhealth")) {
			try {
				List<MetadataValue> values = UEntityMeta.getMetadata(car, "carhealth");
				for (MetadataValue value : values) {
					if (value.value() != null && value.value() instanceof CarHealthData) {
						health = (CarHealthData) value.value();
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				UEntityMeta.removeMetadata(car, "carhealth");
				health = null;
			}
		}
		if(health == null){ //Not yet set on cart
			health = new CarHealthData(
					defaultHealth,
					plugin);
		}
		return health;
	}
	
	public static void showCarDamageMessage(Player player, double damage, String cause, double remainingHealth){
		double max = defaultHealth;
		ChatColor color = ChatColor.YELLOW;
		if (remainingHealth > (max * 0.66)) {
			color = ChatColor.GREEN;
		}
		if (remainingHealth < (max * 0.33)) {
			color = ChatColor.RED;
		}
		player.sendMessage(ChatColor.RED + "-" + damage + "["
				+ cause + "]"
				+ color + " (" + ((int)remainingHealth) + ")");
	}
	
	public static void showCarDamageMessage(Player player, double damage, double remainingHealth){
		double max = defaultHealth;
		ChatColor color = ChatColor.YELLOW;
		if (remainingHealth > (max * 0.66)) {
			color = ChatColor.GREEN;
		}
		if (remainingHealth < (max * 0.33)) {
			color = ChatColor.RED;
		}
		player.sendMessage(ChatColor.RED + "-" + damage + "["
				+ "Car Health" + "]"
				+ color + " (" + ((int)remainingHealth) + ")");
	}
	
	/*public Runnable defaultDeathHandler(final Minecart cart){
		return new Runnable() {
			// @Override
			public void run() {
				plugin.getServer().getPluginManager()
						.callEvent(new ucarDeathEvent(cart));
			}
		};
	}*/

}
