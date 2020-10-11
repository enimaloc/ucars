package com.useful.ucars;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import com.useful.ucars.common.StatValue;

public class UCarsCommandExecutor implements CommandExecutor {
	private Plugin plugin;

	public UCarsCommandExecutor(UCars instance) {
		this.plugin = UCars.plugin;
	}

	// @Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ucars")) {
			sender.sendMessage(UCars.colors.getInfo() + "Ucars v"
					+ plugin.getDescription().getVersion()
					+ " -by storm345- is working!");
			sender.sendMessage(UCars.colors.getTitle() + "[Low Boost:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.rightClickWith")
					+ UCars.getIdList("general.cars.lowBoost"));
			sender.sendMessage(UCars.colors.getTitle() + "[Medium Boost:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.rightClickWith")
					+ UCars.getIdList("general.cars.medBoost"));
			sender.sendMessage(UCars.colors.getTitle() + "[High Boost:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.rightClickWith")
					+ UCars.getIdList("general.cars.highBoost"));
			sender.sendMessage(UCars.colors.getTitle()
					+ "[Medium block Boost:]" + UCars.colors.getInfo()
					+ Lang.get("lang.messages.driveOver")
					+ UCars.getIdList("general.cars.blockBoost"));
			sender.sendMessage(UCars.colors.getTitle() + "[High block Boost:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.driveOver")
					+ UCars.getIdList("general.cars.HighblockBoost"));
			sender.sendMessage(UCars.colors.getTitle() + "[Reset block Boost:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.driveOver")
					+ UCars.getIdList("general.cars.ResetblockBoost"));
			sender.sendMessage(UCars.colors.getTitle() + "[Jump block:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.driveOver")
					+ UCars.getIdList("general.cars.jumpBlock"));
			sender.sendMessage(UCars.colors.getTitle() + "[Teleport block:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.driveOver")
					+ UCars.getIdList("general.cars.teleportBlock"));
			sender.sendMessage(UCars.colors.getTitle()
					+ "[Traffic light waiting block:]"
					+ UCars.colors.getInfo()
					+ Lang.get("lang.messages.driveOver")
					+ UCars.getIdList("general.cars.trafficLights.waitingBlock"));
			sender.sendMessage(UCars.colors.getTitle() + "[Default speed:]"
					+ UCars.colors.getInfo()
					+ UCars.config.getDouble("general.cars.defSpeed"));
			if (UCars.config.getBoolean("general.cars.fuel.enable")
					&& !UCars.config
							.getBoolean("general.cars.fuel.items.enable")) {
				sender.sendMessage(UCars.colors.getTitle()
						+ "[Fuel cost (Per litre):]" + UCars.colors.getInfo()
						+ UCars.config.getDouble("general.cars.fuel.price"));
			}
			if (UCars.config.getBoolean("general.cars.fuel.enable")
					&& UCars.config
							.getBoolean("general.cars.fuel.items.enable")) {
				sender.sendMessage(UCars.colors.getTitle() + "[Fuel items:]"
						+ UCars.colors.getInfo()
						+ UCars.getIdList("general.cars.fuel.items.ids"));
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("ufuel")) {
			if (!UCars.config.getBoolean("general.cars.fuel.enable")) {
				sender.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.fuel.disabled"));
				return true;
			}
			if (!sender.hasPermission(UCars.config
					.getString("general.cars.fuel.cmdPerm"))) {
				sender.sendMessage(UCars.colors.getError() + "No permission!");
				return true;
			}
			return ufuel(sender, args);
		} else if (cmd.getName().equalsIgnoreCase("reloaducars")) {
			// plugin.onDisable();
			try {
				UCars.config.load(new File(plugin.getDataFolder()
						+ File.separator + "config.yml"));
			} catch (Exception e) {
				// Load config
				e.printStackTrace();
			}
			// plugin.onEnable();
			// plugin.onLoad();
			sender.sendMessage(UCars.colors.getInfo()
					+ Lang.get("lang.messages.reload"));
			return true;
		} else if (cmd.getName().equalsIgnoreCase("cars")) {
			if (args.length < 1) {
				return false;
			}
			String action = args[0];
			if (action.equalsIgnoreCase("remove")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(UCars.colors.getError()
							+ Lang.get("lang.messages.playersOnly"));
					return true;
				}
				Player player = (Player) sender;
				World world = player.getWorld();
				List<Entity> entities = world.getEntities();
				int removed = 0;
				for (Entity entity : entities) {
					if (entity instanceof Vehicle) {
						Vehicle cart = (Vehicle) entity;
						if (new UCarsListener(UCars.plugin).isACar(cart)) {
							entity.eject();
							if (entity.getPassenger() != null) {
								entity.getPassenger().eject();
							}
							entity.remove();
							removed++;
						}
					}
				}
				String success = Lang.get("lang.cars.remove");
				success = success.replaceAll("%world%", world.getName());
				success = success.replaceAll("%amount%", "" + removed);
				sender.sendMessage(UCars.colors.getSuccess() + success);
				return true;
			}
			return false;
		} else if (cmd.getName().equalsIgnoreCase("ulicense")) {
			if (!UCars.config.getBoolean("general.cars.licenses.enable") || !(sender instanceof Player)) {
				return true;
			}
			Player player = (Player) sender;
			int stage = 1;
			if (args.length > 0) {
				try {
					stage = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					return false;
				}
			}
			
			String noSkip = Lang.get("lang.licenses.nocheat");
			
			if (stage == 1) {
				sender.sendMessage(UCars.colors.getInfo()
						+ Lang.get("lang.licenses.basics"));
				String next = Lang.get("lang.licenses.next");
				next = next.replaceAll(Pattern.quote("%command%"),
						"/ulicense 2");
				sender.sendMessage(UCars.colors.getTitle() + next);
				player.setMetadata("ulicense1", new StatValue(true, UCars.plugin));
			} else if (stage == 2) {
				if(!player.hasMetadata("ulicense1")){
					noSkip = noSkip.replaceAll(Pattern.quote("%command%"), "/ulicense");
					sender.sendMessage(UCars.colors.getError()+noSkip);
					return true;
				}
				sender.sendMessage(UCars.colors.getInfo()
						+ Lang.get("lang.licenses.controls"));
				String next = Lang.get("lang.licenses.next");
				next = next.replaceAll(Pattern.quote("%command%"),
						"/ulicense 3");
				sender.sendMessage(UCars.colors.getTitle() + next);
				player.setMetadata("ulicense2", new StatValue(true, UCars.plugin));
			} else if (stage == 3) {
				if(!player.hasMetadata("ulicense2")){
					noSkip = noSkip.replaceAll(Pattern.quote("%command%"), "/ulicense 2");
					sender.sendMessage(UCars.colors.getError()+noSkip);
					return true;
				}
				sender.sendMessage(UCars.colors.getInfo()
						+ Lang.get("lang.licenses.effects"));
				String next = Lang.get("lang.licenses.next");
				next = next.replaceAll(Pattern.quote("%command%"),
						"/ulicense 4");
				sender.sendMessage(UCars.colors.getTitle() + next);
				player.setMetadata("ulicense3", new StatValue(true, UCars.plugin));
			} else if (stage == 4) {
				if(!player.hasMetadata("ulicense3")){
					noSkip = noSkip.replaceAll(Pattern.quote("%command%"), "/ulicense 3");
					sender.sendMessage(UCars.colors.getError()+noSkip);
					return true;
				}
				sender.sendMessage(UCars.colors.getInfo()
						+ Lang.get("lang.licenses.itemBoosts"));
				if (sender instanceof Player) {
					if (!((UCars) plugin).licensedPlayers.contains(sender
							.getName())) {
						sender.sendMessage(UCars.colors.getSuccess()
								+ Lang.get("lang.licenses.success"));
						((UCars) plugin).licensedPlayers.add(sender.getName());
						((UCars) plugin).licensedPlayers.save();
					}
				}
			} else {
				return false;
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("pigucart")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.messages.playersOnly"));
				return true;
			}
			Player player = (Player) sender;
			Location spawn = player.getLocation().add(0, 2, 0);
			Entity cart = player.getWorld().spawnEntity(spawn,
					EntityType.MINECART);
			if(args.length > 0 && player.isOp()){
				Material mat;
				try {
					mat = Material.valueOf(args[0]);
				} catch (Exception e) {
					mat = null;
				}
				if(mat != null && cart instanceof Minecart){
					((Minecart)cart).setDisplayBlock(new MaterialData(mat));
				}
			}
			else {
				Entity pig = player.getWorld().spawnEntity(spawn, EntityType.PIG);
				cart.setPassenger(pig);
				pig.setPassenger(player);
			}
			sender.sendMessage(UCars.colors.getSuccess() + "PiguCart!");
			return true;
		}
		return false;
	}

	public Boolean ufuel(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.get("lang.messages.playersOnly"));
			return true;
		}
		if (args.length < 1) {
			return false;
		}
		String action = args[0];
		if (action.equalsIgnoreCase("view")) {
			sender.sendMessage(UCars.colors.getTitle()
					+ "[Fuel cost (Per litre):]" + UCars.colors.getInfo()
					+ UCars.config.getDouble("general.cars.fuel.price"));
			double fuel = 0;
			if (UCars.fuel.containsKey(sender.getName())) {
				fuel = UCars.fuel.get(sender.getName());
			}
			sender.sendMessage(UCars.colors.getTitle() + "[Your fuel:]"
					+ UCars.colors.getInfo() + fuel + " "
					+ Lang.get("lang.fuel.unit"));
			if (UCars.config.getBoolean("general.cars.fuel.items.enable")) {
				sender.sendMessage(UCars.colors.getTitle()
						+ Lang.get("lang.fuel.isItem"));
			}
			return true;
		} else if (action.equalsIgnoreCase("buy")) {
			if (args.length < 2) {
				return false;
			}
			double amount = 0;
			try {
				amount = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.fuel.invalidAmount"));
				return true;
			}
			double fuel = 0;
			if (UCars.fuel.containsKey(sender.getName())) {
				fuel = UCars.fuel.get(sender.getName());
			}
			double cost = UCars.config.getDouble("general.cars.fuel.price");
			double value = cost * amount;
			double bal = 0.0;
			try {
				bal = UCars.economy.getBalance(sender.getName());
			} catch (Exception e) {
				if (!UCars.plugin.setupEconomy()) {
					sender.sendMessage(UCars.colors.getError()
							+ "Error finding economy plugin");
					return true;
				} else {
					try {
						bal = UCars.economy.getBalance(sender.getName());
					} catch (Exception e1) {
						sender.sendMessage(UCars.colors.getError()
								+ "Error finding economy plugin");
						return true;
					}
				}
			}
			if (bal <= 0) {
				sender.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.fuel.noMoney"));
				return true;
			}
			if (bal < value) {
				String notEnough = Lang.get("lang.fuel.notEnoughMoney");
				notEnough = notEnough.replaceAll("%amount%", "" + value);
				notEnough = notEnough.replaceAll("%unit%",
						"" + UCars.economy.currencyNamePlural());
				notEnough = notEnough.replaceAll("%balance%", "" + bal);
				sender.sendMessage(UCars.colors.getError() + notEnough);
				return true;
			}
			UCars.economy.withdrawPlayer(sender.getName(), value);
			bal = bal - value;
			fuel = fuel + amount;
			UCars.fuel.put(sender.getName(), fuel);
			UCars.saveHashMap(UCars.fuel, plugin.getDataFolder()
					.getAbsolutePath() + File.separator + "fuel.bin");
			String success = Lang.get("lang.fuel.success");
			success = success.replaceAll("%amount%", "" + value);
			success = success.replaceAll("%unit%",
					"" + UCars.economy.currencyNamePlural());
			success = success.replaceAll("%balance%", "" + bal);
			success = success.replaceAll("%quantity%", "" + amount);
			sender.sendMessage(UCars.colors.getSuccess() + success);
			return true;
		} else if (action.equalsIgnoreCase("sell")) {
			if (!UCars.config.getBoolean("general.cars.fuel.sellFuel")) {
				sender.sendMessage(UCars.colors.getError()
						+ "Not allowed to sell fuel!");
				return true;
			}
			if (args.length < 2) {
				return false;
			}
			double amount = 0;
			try {
				amount = Double.parseDouble(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.fuel.invalidAmount"));
				return true;
			}
			double fuel = 0;
			if (UCars.fuel.containsKey(sender.getName())) {
				fuel = UCars.fuel.get(sender.getName());
			}
			if ((fuel - amount) <= 0) {
				sender.sendMessage(UCars.colors.getError()
						+ Lang.get("lang.fuel.empty"));
				return true;
			}
			double cost = UCars.config.getDouble("general.cars.fuel.price");
			double value = cost * amount;
			double balance = UCars.economy.getBalance(sender.getName());
			UCars.economy.depositPlayer(sender.getName(), value);
			balance = balance + value;
			fuel = fuel - amount;
			UCars.fuel.put(sender.getName(), fuel);
			UCars.saveHashMap(UCars.fuel, plugin.getDataFolder()
					.getAbsolutePath() + File.separator + "fuel.bin");
			String success = Lang.get("lang.fuel.sellSuccess");
			success = success.replaceAll("%amount%", "" + value);
			success = success.replaceAll("%unit%",
					"" + UCars.economy.currencyNamePlural());
			success = success.replaceAll("%balance%", "" + balance);
			success = success.replaceAll("%quantity%", "" + amount);
			sender.sendMessage(UCars.colors.getSuccess() + success);
			return true;
		} else {
			return false;
		}
	}
}
