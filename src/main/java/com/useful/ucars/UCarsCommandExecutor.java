package com.useful.ucars;

import com.useful.ucars.common.StatValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class UCarsCommandExecutor implements CommandExecutor {
    private UCars uCars;

    public UCarsCommandExecutor(UCars uCars) {
        this.uCars = uCars;
    }

    // @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ucars")) {
            return noArgs(sender);
        } else if (cmd.getName().equalsIgnoreCase("ufuel")) {
            return ufuelArgs(sender, args);
        } else if (cmd.getName().equalsIgnoreCase("reload")) {
            return reloadArgs(sender);
        } else if (cmd.getName().equalsIgnoreCase("cars")) {
            return carsArgs(sender, args);
        } else if (cmd.getName().equalsIgnoreCase("ulicense")) {
            return ulicenseArgs(sender, args);
        } else if (cmd.getName().equalsIgnoreCase("pigucart")) {
            return pigucartArgs(sender, args);
        }
        return false;
    }

    private boolean noArgs(CommandSender sender) {
        sender.sendMessage(uCars.getUnsafe().colors.getInfo() + "Ucars v"
                + uCars.getDescription().getVersion()
                + " -by storm345- is working!");
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Low Boost:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.rightClickWith")
                + uCars.getIdList("general.cars.lowBoost"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Medium Boost:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.rightClickWith")
                + uCars.getIdList("general.cars.medBoost"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[High Boost:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.rightClickWith")
                + uCars.getIdList("general.cars.highBoost"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle()
                + "[Medium block Boost:]" + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.driveOver")
                + uCars.getIdList("general.cars.blockBoost"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[High block Boost:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.driveOver")
                + uCars.getIdList("general.cars.HighblockBoost"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Reset block Boost:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.driveOver")
                + uCars.getIdList("general.cars.ResetblockBoost"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Jump block:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.driveOver")
                + uCars.getIdList("general.cars.jumpBlock"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Teleport block:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.driveOver")
                + uCars.getIdList("general.cars.teleportBlock"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle()
                + "[Traffic light waiting block:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.driveOver")
                + uCars.getIdList("general.cars.trafficLights.waitingBlock"));
        sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Default speed:]"
                + uCars.getUnsafe().colors.getInfo()
                + uCars.getConfig().getDouble("general.cars.defSpeed"));
        if (uCars.getConfig().getBoolean("general.cars.fuel.enable")
                && !uCars.getConfig()
                .getBoolean("general.cars.fuel.items.enable")) {
            sender.sendMessage(uCars.getUnsafe().colors.getTitle()
                    + "[Fuel cost (Per litre):]" + uCars.getUnsafe().colors.getInfo()
                    + uCars.getConfig().getDouble("general.cars.fuel.price"));
        }
        if (uCars.getConfig().getBoolean("general.cars.fuel.enable")
                && uCars.getConfig()
                .getBoolean("general.cars.fuel.items.enable")) {
            sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Fuel items:]"
                    + uCars.getUnsafe().colors.getInfo()
                    + uCars.getIdList("general.cars.fuel.items.ids"));
        }
        return true;
    }
    private boolean ufuelArgs(CommandSender sender, String[] args) {
        if (!uCars.getConfig().getBoolean("general.cars.fuel.enable")) {
            sender.sendMessage(uCars.getUnsafe().colors.getError()
                    + uCars.getLang().get("lang.fuel.disabled"));
            return true;
        }
        if (!sender.hasPermission(uCars.getConfig()
                .getString("general.cars.fuel.cmdPerm"))) {
            sender.sendMessage(uCars.getUnsafe().colors.getError() + "No permission!");
            return true;
        }
        return ufuel(sender, args);
    }
    private boolean reloadArgs(CommandSender sender) {
        // uCars.onDisable();
        try {
            uCars.getConfig().load(new File(uCars.getDataFolder()
                    + File.separator + "getConfig().yml"));
        } catch (Exception e) {
            // Load getConfig()
            e.printStackTrace();
        }
        // uCars.onEnable();
        // uCars.onLoad();
        sender.sendMessage(uCars.getUnsafe().colors.getInfo()
                + uCars.getLang().get("lang.messages.reload"));
        return true;
    }
    private boolean carsArgs(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }
        String action = args[0];
        if (action.equalsIgnoreCase("remove")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(uCars.getUnsafe().colors.getError()
                        + uCars.getLang().get("lang.messages.playersOnly"));
                return true;
            }
            Player player = (Player) sender;
            World world = player.getWorld();
            List<Entity> entities = world.getEntities();
            int removed = 0;
            for (Entity entity : entities) {
                if (entity instanceof Vehicle) {
                    Vehicle cart = (Vehicle) entity;
                    if (new UCarsListener(uCars).isACar(cart)) {
                        entity.eject();
                        if (entity.getPassenger() != null) {
                            entity.getPassenger().eject();
                        }
                        entity.remove();
                        removed++;
                    }
                }
            }
            String success = uCars.getLang().get("lang.cars.remove");
            success = success.replaceAll("%world%", world.getName());
            success = success.replaceAll("%amount%", "" + removed);
            sender.sendMessage(uCars.getUnsafe().colors.getSuccess() + success);
            return true;
        }
        return false;
    }
    private boolean ulicenseArgs(CommandSender sender, String[] args) {
        if (!uCars.getConfig().getBoolean("general.cars.licenses.enable") || !(sender instanceof Player)) {
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

        String noSkip = uCars.getLang().get("lang.licenses.nocheat");

        if (stage == 1) {
            sender.sendMessage(uCars.getUnsafe().colors.getInfo()
                    + uCars.getLang().get("lang.licenses.basics"));
            String next = uCars.getLang().get("lang.licenses.next");
            next = next.replaceAll(Pattern.quote("%command%"),
                    "/ulicense 2");
            sender.sendMessage(uCars.getUnsafe().colors.getTitle() + next);
            player.setMetadata("ulicense1", new StatValue(uCars, true));
        } else if (stage == 2) {
            if (!player.hasMetadata("ulicense1")) {
                noSkip = noSkip.replaceAll(Pattern.quote("%command%"), "/ulicense");
                sender.sendMessage(uCars.getUnsafe().colors.getError() + noSkip);
                return true;
            }
            sender.sendMessage(uCars.getUnsafe().colors.getInfo()
                    + uCars.getLang().get("lang.licenses.controls"));
            String next = uCars.getLang().get("lang.licenses.next");
            next = next.replaceAll(Pattern.quote("%command%"),
                    "/ulicense 3");
            sender.sendMessage(uCars.getUnsafe().colors.getTitle() + next);
            player.setMetadata("ulicense2", new StatValue(uCars, true));
        } else if (stage == 3) {
            if (!player.hasMetadata("ulicense2")) {
                noSkip = noSkip.replaceAll(Pattern.quote("%command%"), "/ulicense 2");
                sender.sendMessage(uCars.getUnsafe().colors.getError() + noSkip);
                return true;
            }
            sender.sendMessage(uCars.getUnsafe().colors.getInfo()
                    + uCars.getLang().get("lang.licenses.effects"));
            String next = uCars.getLang().get("lang.licenses.next", new String[]{"%command%", "/ulicense 4"});
            sender.sendMessage(uCars.getUnsafe().colors.getTitle() + next);
            player.setMetadata("ulicense3", new StatValue(uCars, true));
        } else if (stage == 4) {
            if (!player.hasMetadata("ulicense3")) {
                noSkip = noSkip.replaceAll(Pattern.quote("%command%"), "/ulicense 3");
                sender.sendMessage(uCars.getUnsafe().colors.getError() + noSkip);
                return true;
            }
            sender.sendMessage(uCars.getUnsafe().colors.getInfo()
                    + uCars.getLang().get("lang.licenses.itemBoosts"));
            if (!(uCars.getLicensedPlayers().contains(sender
                    .getName()))) {
                sender.sendMessage(uCars.getUnsafe().colors.getSuccess()
                        + uCars.getLang().get("lang.licenses.success"));
                uCars.getLicensedPlayers().add(sender.getName());
                uCars.getLicensedPlayers().save();
            }
        } else {
            return false;
        }
        return true;
    }
    private boolean pigucartArgs(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(uCars.getUnsafe().colors.getError()
                    + uCars.getLang().get("lang.messages.playersOnly"));
            return true;
        }
        Player player = (Player) sender;
        Location spawn = player.getLocation().add(0, 2, 0);
        Entity cart = player.getWorld().spawnEntity(spawn,
                EntityType.MINECART);
        if (args.length > 0 && player.isOp()) {
            Material mat;
            try {
                mat = Material.valueOf(args[0]);
            } catch (Exception e) {
                mat = null;
            }
            if (mat != null && cart instanceof Minecart) {
                ((Minecart) cart).setDisplayBlock(new MaterialData(mat));
            }
        } else {
            Entity pig = player.getWorld().spawnEntity(spawn, EntityType.PIG);
            cart.setPassenger(pig);
            pig.setPassenger(player);
        }
        sender.sendMessage(uCars.getUnsafe().colors.getSuccess() + "PiguCart!");
        return true;
    }

    public boolean ufuel(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(uCars.getLang().get("lang.messages.playersOnly"));
            return true;
        }
        if (args.length < 1) {
            return false;
        }
        String action = args[0];
        if (action.equalsIgnoreCase("view")) {
            sender.sendMessage(uCars.getUnsafe().colors.getTitle()
                    + "[Fuel cost (Per litre):]" + uCars.getUnsafe().colors.getInfo()
                    + uCars.getConfig().getDouble("general.cars.fuel.price"));
            double fuel = 0;
            if (uCars.getFuel().containsKey(sender.getName())) {
                fuel = uCars.getFuel().get(sender.getName());
            }
            sender.sendMessage(uCars.getUnsafe().colors.getTitle() + "[Your fuel:]"
                    + uCars.getUnsafe().colors.getInfo() + fuel + " "
                    + uCars.getLang().get("lang.fuel.unit"));
            if (uCars.getConfig().getBoolean("general.cars.fuel.items.enable")) {
                sender.sendMessage(uCars.getUnsafe().colors.getTitle()
                        + uCars.getLang().get("lang.fuel.isItem"));
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
                sender.sendMessage(uCars.getUnsafe().colors.getError()
                        + uCars.getLang().get("lang.fuel.invalidAmount"));
                return true;
            }
            double fuel = 0;
            if (uCars.getFuel().containsKey(sender.getName())) {
                fuel = uCars.getFuel().get(sender.getName());
            }
            double cost = uCars.getConfig().getDouble("general.cars.fuel.price");
            double value = cost * amount;
            double balance = 0.0;
            try {
                balance = uCars.getEconomy().getBalance(sender.getName());
            } catch (Exception e) {
                if (!uCars.setupEconomy()) {
                    sender.sendMessage(uCars.getUnsafe().colors.getError()
                            + "Error finding economy uCars");
                    return true;
                } else {
                    try {
                        balance = uCars.getEconomy().getBalance(sender.getName());
                    } catch (Exception e1) {
                        sender.sendMessage(uCars.getUnsafe().colors.getError()
                                + "Error finding economy uCars");
                        return true;
                    }
                }
            }
            if (balance <= 0) {
                sender.sendMessage(uCars.getUnsafe().colors.getError()
                        + uCars.getLang().get("lang.fuel.noMoney"));
                return true;
            }
            if (balance < value) {
                String notEnough =
                        uCars.getLang().get(
                                "lang.fuel.notEnoughMoney",
                                new String[]{"%amount%", value+""},
                                new String[]{"%unit%", uCars.getEconomy().currencyNamePlural()},
                                new String[]{"%balance", balance+""},
                                new String[]{"%quantity%", value+""}
                        );
                sender.sendMessage(uCars.getUnsafe().colors.getError() + notEnough);
                return true;
            }
            uCars.getEconomy().withdrawPlayer(sender.getName(), value);
            balance = balance - value;
            fuel = fuel + amount;
            uCars.getFuel().put(sender.getName(), fuel);
            UCars.saveHashMap(uCars.getFuel(), uCars.getDataFolder()
                    .getAbsolutePath() + File.separator + "fuel.bin");
            String success =
                    uCars.getLang().get(
                            "lang.fuel.sell",
                            new String[]{"%amount%", value+""},
                            new String[]{"%unit%", uCars.getEconomy().currencyNamePlural()},
                            new String[]{"%balance", balance+""},
                            new String[]{"%quantity%", value+""}
                    );
            sender.sendMessage(uCars.getUnsafe().colors.getSuccess() + success);
            return true;
        } else if (action.equalsIgnoreCase("sell")) {
            if (!uCars.getConfig().getBoolean("general.cars.fuel.sellFuel")) {
                sender.sendMessage(uCars.getUnsafe().colors.getError()
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
                sender.sendMessage(uCars.getUnsafe().colors.getError()
                        + uCars.getLang().get("lang.fuel.invalidAmount"));
                return true;
            }
            double fuel = 0;
            if (uCars.getFuel().containsKey(sender.getName())) {
                fuel = uCars.getFuel().get(sender.getName());
            }
            if ((fuel - amount) <= 0) {
                sender.sendMessage(uCars.getUnsafe().colors.getError()
                        + uCars.getLang().get("lang.fuel.empty"));
                return true;
            }
            double cost = uCars.getConfig().getDouble("general.cars.fuel.price");
            double value = cost * amount;
            double balance = uCars.getEconomy().getBalance(sender.getName());
            uCars.getEconomy().depositPlayer(sender.getName(), value);
            balance = balance + value;
            fuel = fuel - amount;
            uCars.getFuel().put(sender.getName(), fuel);
            UCars.saveHashMap(uCars.getFuel(), uCars.getDataFolder()
                    .getAbsolutePath() + File.separator + "fuel.bin");
            String success =
                    uCars.getLang().get(
                    "lang.fuel.sellSuccess",
                    new String[]{"%amount%", value+""},
                    new String[]{"%unit%", uCars.getEconomy().currencyNamePlural()},
                    new String[]{"%balance", balance+""},
                    new String[]{"%quantity%", value+""}
                );
            sender.sendMessage(uCars.getUnsafe().colors.getSuccess() + success);
            return true;
        }
        return false;
    }
}
