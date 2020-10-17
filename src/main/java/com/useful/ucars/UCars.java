package com.useful.ucars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.useful.ucars.api.UCarsAPI;
import com.useful.ucars.util.UEntityMeta;
import com.useful.ucars.util.UMeta;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class UCars extends JavaPlugin {
    private final Unsafe unsafe = new Unsafe();

    @SuppressWarnings("unchecked")
    public static HashMap<String, Double> loadHashMapDouble(String path) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    path));
            Object result = ois.readObject();
            ois.close();
            // you can feel free to cast result to HashMap<String, Integer> if
            // you know there's that HashMap in the file
            return (HashMap<String, Double>) result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveHashMap(HashMap<String, Double> map, String path) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(path));
            oos.writeObject(map);
            oos.flush();
            oos.close();
            // Handle I/O exceptions
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ListStore getLicensedPlayers() {
        return unsafe.licensedPlayers;
    }

    public void setLicensedPlayers(ListStore licensed) {
        unsafe.licensedPlayers = licensed;
    }

    protected void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                // System.out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer()
                .getServicesManager().getRegistration(
                        net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            unsafe.economy = economyProvider.getProvider();
        }
        return (unsafe.economy != null);
    }

    protected boolean setupProtocol() {
        try {
            unsafe.protocolLib = true;
            unsafe.protocolManager = ProtocolLibrary.getProtocolManager();
            /*
             * ((ProtocolManager)unsafe.protocolManager).addPacketListener(new
             * PacketAdapter(plugin, ConnectionSide.CLIENT_SIDE,
             * ListenerPriority.NORMAL, 0x1b) {
             */

            ((ProtocolManager) unsafe.protocolManager).addPacketListener(
                    new PacketAdapter(this, PacketType.Play.Client.STEER_VEHICLE) {
                        @Override
                        public void onPacketReceiving(final PacketEvent event) {
                            PacketContainer packet = event.getPacket();
                            final float sideways = packet.getFloat().read(0);
                            final float forwards = packet.getFloat().read(1);
                            final boolean jumping = packet.getBooleans().read(0);
                            Bukkit.getScheduler().runTask(plugin, () -> MotionManager.move(event.getPlayer(), forwards, sideways, jumping));
                        }
                    });
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void onEnable() {
        // Lang
        File langFile = new File(getDataFolder().getAbsolutePath()+File.separator + "lang.yml");
        if (!langFile.exists() || langFile.length() < 1) {
            try {
                //noinspection ResultOfMethodCallIgnored
                langFile.createNewFile();
                // newC.save(configFile);
            } catch (IOException ignored) {
            }
        }
        try {
            unsafe.lang = new Lang(langFile);
        } catch (Exception exception) {
            getLogger().log(Level.WARNING, "Error creating/loading lang file! Regenerating..");
        }

        //
        File configFile = new File(getDataFolder().getAbsolutePath()
                + File.separator + "this.getConfig().yml");
        if (!configFile.exists() || configFile.length() < 1) {
            // YamlConfiguration newC = new YamlConfiguration();
            // newC.set("time.created", System.currentTimeMillis());
            try {
                //noinspection ResultOfMethodCallIgnored
                configFile.createNewFile();
                // newC.save(configFile);
            } catch (IOException ignored) {
            }
            copy(getResource("ucarsConfigHeader.yml"), configFile);
        }

        try {
            // this.getConfig().load(unsafe.getDataFolder().getAbsolutePath() +
            // File.separator + "this.getConfig().yml");
            if (!this.getConfig().contains("general.cars.# description")) {
                this.getConfig().set("general.cars.# description",
                        "If enabled unsafe.will allow for drivable cars(Minecarts not on rails)");
            }
            if (!this.getConfig().contains("general.cars.enable")) {
                this.getConfig().set("general.cars.enable", true);
            } else {
                //Existing this.getConfig()
                if (!this.getConfig().contains("misc.configVersion")) {
                    //Config part of old format and mark as so to convert it later
                    this.getConfig().set("misc.configVersion", 1.0);
                }
            }
            if (!this.getConfig().contains("misc.configVersion")) {
                this.getConfig().set("misc.configVersion", 1.1);
            }
            if (!this.getConfig().contains("general.permissions.enable")) {
                this.getConfig().set("general.permissions.enable", true);
            }
            if (!this.getConfig().contains("general.cars.defSpeed")) {
                this.getConfig().set("general.cars.defSpeed", (double) 30);
            }
            if (!this.getConfig().contains("general.cars.smooth")) {
                this.getConfig().set("general.cars.smooth", true);
            }
            if (!this.getConfig().contains("general.cars.turningCircles")) {
                this.getConfig().set("general.cars.turningCircles", true);
            }
            unsafe.turningCircles = this.getConfig().getBoolean("general.cars.turningCircles");
            if (!this.getConfig().contains("general.cars.effectBlocks.enable")) {
                this.getConfig().set("general.cars.effectBlocks.enable", true);
            }
            if (!this.getConfig().contains("general.cars.boostsEnable")) {
                this.getConfig().set("general.cars.boostsEnable", true);
            }
            if (!this.getConfig().contains("general.cars.lowBoost")) {
                this.getConfig().set("general.cars.lowBoost", new String[]{"COAL"});
            }
            if (!this.getConfig().contains("general.cars.medBoost")) {
                this.getConfig().set("general.cars.medBoost", new String[]{"IRON_INGOT"});
            }
            if (!this.getConfig().contains("general.cars.highBoost")) {
                this.getConfig().set("general.cars.highBoost", new String[]{"DIAMOND"});
            }
            if (!this.getConfig().contains("general.cars.blockBoost")) {
                this.getConfig().set("general.cars.blockBoost", new String[]{"GOLD_BLOCK"});
            }
            if (!this.getConfig().contains("general.cars.highBlockBoost")) {
                this.getConfig().set("general.cars.highBlockBoost", new String[]{"DIAMOND_BLOCK"});
            }
            if (!this.getConfig().contains("general.cars.resetBlockBoost")) {
                this.getConfig().set("general.cars.resetBlockBoost", new String[]{"EMERALD_BLOCK"});
            }
            if (!this.getConfig().contains("general.cars.turret")) {
                this.getConfig().set("general.cars.turret", null); //Remove if set
            }
            if (!this.getConfig().contains("general.cars.ignoreVehiclesOnRails")) {
                this.getConfig().set("general.cars.ignoreVehiclesOnRails", true);
            } else {
                unsafe.ignoreRails = this.getConfig().getBoolean("general.cars.ignoreVehiclesOnRails");
            }
            if (!this.getConfig().contains("general.cars.jumpBlock")) {
                this.getConfig().set("general.cars.jumpBlock", new String[]{"IRON_BLOCK"});
            }
            if (!this.getConfig().contains("general.cars.jumpAmount")) {
                this.getConfig().set("general.cars.jumpAmount", (double) 30);
            }
            if (!this.getConfig().contains("general.cars.teleportBlock")) {
                this.getConfig().set("general.cars.teleportBlock", new String[]{"STAINED_CLAY:2"});
            }
            if (!this.getConfig().contains("general.cars.fireUpdateEvent")) {
                this.getConfig().set("general.cars.fireUpdateEvent", unsafe.fireUpdateEvent);
            } else {
                unsafe.fireUpdateEvent = this.getConfig().getBoolean("general.cars.fireUpdateEvent");
            }
            if (!this.getConfig().contains("general.cars.trafficLights.enable")) {
                this.getConfig().set("general.cars.trafficLights.enable", true);
            }
            if (!this.getConfig().contains("general.cars.trafficLights.waitingBlock")) {
                this.getConfig().set("general.cars.trafficLights.waitingBlock", new String[]{"QUARTZ_BLOCK"});
            }
            if (!this.getConfig().contains("general.cars.hitBy.enable")) {
                this.getConfig().set("general.cars.hitBy.enable", false);
            }
            if (!this.getConfig().contains("general.cars.hitBy.enableMonsterDamage")) {
                this.getConfig().set("general.cars.hitBy.enableMonsterDamage", true);
            }
            if (!this.getConfig().contains("general.cars.hitBy.enableAllMonsterDamage")) {
                this.getConfig().set("general.cars.hitBy.enableAllMonsterDamage", true);
            }
            if (!this.getConfig().contains("general.cars.hitBy.power")) {
                this.getConfig().set("general.cars.hitBy.power", (double) 5);
            }
            if (!this.getConfig().contains("general.cars.hitBy.damage")) {
                this.getConfig().set("general.cars.hitBy.damage", 1.5);
            }
            if (!this.getConfig().contains("general.cars.roadBlocks.enable")) {
                this.getConfig().set("general.cars.roadBlocks.enable", false);
            }
            if (!this.getConfig().contains("general.cars.roadBlocks.ids")) {
                this.getConfig().set("general.cars.roadBlocks.ids", new String[]{
                        "WOOL:15", "WOOL:8", "WOOL:0", "WOOL:7"});
            }
            if (!this.getConfig().contains("general.cars.licenses.enable")) {
                this.getConfig().set("general.cars.licenses.enable", false);
            }
            if (!this.getConfig().contains("general.cars.fuel.enable")) {
                this.getConfig().set("general.cars.fuel.enable", false);
            }
            if (!this.getConfig().contains("general.cars.fuel.price")) {
                this.getConfig().set("general.cars.fuel.price", (double) 2);
            }
            if (!this.getConfig().contains("general.cars.fuel.check")) {
                this.getConfig().set("general.cars.fuel.check", new String[]{"FEATHER"});
            }
            if (!this.getConfig().contains("general.cars.fuel.cmdPerm")) {
                this.getConfig().set("general.cars.fuel.cmdPerm", "ucars.ucars");
            }
            if (!this.getConfig().contains("general.cars.fuel.bypassPerm")) {
                this.getConfig().set("general.cars.fuel.bypassPerm", "ucars.bypassfuel");
            }
            if (!this.getConfig().contains("general.cars.fuel.items.enable")) {
                this.getConfig().set("general.cars.fuel.items.enable", false);
            }
            if (!this.getConfig().contains("general.cars.fuel.items.ids")) {
                this.getConfig().set("general.cars.fuel.items.ids", new String[]{
                        "WOOD", "COAL:0", "COAL:1"});
            }
            if (!this.getConfig().contains("general.cars.fuel.sellFuel")) {
                this.getConfig().set("general.cars.fuel.sellFuel", true);
            }
            if (!this.getConfig().contains("general.cars.barriers")) {
                this.getConfig().set("general.cars.barriers", new String[]{
                        "COBBLE_WALL", "FENCE", "FENCE_GATE", "NETHER_FENCE"});
            }
            if (!this.getConfig().contains("general.cars.speedMods")) {
                this.getConfig().set("general.cars.speedMods", new String[]{
                        "SOUL_SAND:0-10", "SPONGE:0-20"});
            }
            if (!this.getConfig().contains("general.cars.placePerm.enable")) {
                this.getConfig().set("general.cars.placePerm.enable", false);
            }
            if (!this.getConfig().contains("general.cars.placePerm.perm")) {
                this.getConfig().set("general.cars.placePerm.perm", "ucars.place");
            }
            if (!this.getConfig().contains("general.cars.health.default")) {
                this.getConfig().set("general.cars.health.default", 10.0);
            }
            if (!this.getConfig().contains("general.cars.health.max")) {
                this.getConfig().set("general.cars.health.max", 100.0);
            }
            if (!this.getConfig().contains("general.cars.health.min")) {
                this.getConfig().set("general.cars.health.min", 5.0);
            }
            if (!this.getConfig().contains("general.cars.health.overrideDefault")) {
                this.getConfig().set("general.cars.health.overrideDefault", true);
            }
            if (!this.getConfig().contains("general.cars.health.underwaterDamage")) {
                this.getConfig().set("general.cars.health.underwaterDamage", 0.0);
            }
            if (!this.getConfig().contains("general.cars.health.lavaDamage")) {
                this.getConfig().set("general.cars.health.lavaDamage", 0.0);
            }
            if (!this.getConfig().contains("general.cars.health.punchDamage")) {
                this.getConfig().set("general.cars.health.punchDamage", 50.0);
            }
            if (!this.getConfig().contains("general.cars.health.cactusDamage")) {
                this.getConfig().set("general.cars.health.cactusDamage", 0.0);
            }
            if (!this.getConfig().contains("general.cars.health.crashDamage")) {
                this.getConfig().set("general.cars.health.crashDamage", 0.0);
            }
            if (!this.getConfig().contains("general.cars.forceRaceControlSystem")) {
                this.getConfig().set("general.cars.forceRaceControlSystem", false);
            }
            unsafe.forceRaceControls = this.getConfig().getBoolean("general.cars.forceRaceControlSystem");
            if (!this.getConfig().contains("general.cars.playersIgnoreTrafficLights")) {
                this.getConfig().set("general.cars.playersIgnoreTrafficLights", false);
            }
            unsafe.playersIgnoreTrafficLights = this.getConfig().getBoolean("general.cars.playersIgnoreTrafficLights");
//            if (!this.getConfig().contains("colorScheme.success")) {
//                this.getConfig().set("colorScheme.success", "&a");
//            }
//            if (!this.getConfig().contains("colorScheme.error")) {
//                this.getConfig().set("colorScheme.error", "&c");
//            }
//            if (!this.getConfig().contains("colorScheme.info")) {
//                this.getConfig().set("colorScheme.info", "&e");
//            }
//            if (!this.getConfig().contains("colorScheme.title")) {
//                this.getConfig().set("colorScheme.title", "&9");
//            }
//            if (!this.getConfig().contains("colorScheme.tp")) {
//                this.getConfig().set("colorScheme.tp", "&5");
//            }

            if (this.getConfig().getBoolean("general.cars.fuel.enable")
                    && !this.getConfig().getBoolean("general.cars.fuel.items.enable")) {
                try {
                    if (!setupEconomy()) {
                        this.getLogger()
                                .warning(
                                        "Attempted to enable fuel but vault NOT found. Please install vault to use fuel!");
                        this.getLogger().warning("Disabling fuel system...");
                        this.getConfig().set("general.cars.fuel.enable", false);
                    } else {
                        unsafe.vault = true;
                        unsafe.fuel = new HashMap<String, Double>();
                        File fuels = new File(this.getDataFolder()
                                .getAbsolutePath()
                                + File.separator
                                + "fuel.bin");
                        if (fuels.exists() && fuels.length() > 1) {
                            unsafe.fuel = loadHashMapDouble(this.getDataFolder()
                                    .getAbsolutePath()
                                    + File.separator
                                    + "fuel.bin");
                            if (unsafe.fuel == null) {
                                unsafe.fuel = new HashMap<String, Double>();
                            }
                        }
                    }
                } catch (Exception e) {
                    this.getLogger()
                            .warning(
                                    "Attempted to enable fuel but vault NOT found. Please install vault to use fuel!");
                    this.getLogger().warning("Disabling fuel system...");
                    this.getConfig().set("general.cars.fuel.enable", false);
                }
            }
        } catch (Exception ignored) {}
        //Before saving, convert old this.getConfig()s
        double latestConfigVersion = 1.1;
        double configVersion = this.getConfig().getDouble("misc.configVersion");
        while (configVersion < latestConfigVersion) {
            configVersion += 0.1; //Add 0.1 to this.getConfig() version
            ConfigVersionConverter.convert(this.getConfig(), configVersion);//Convert to next increment in this.getConfig() versioning
        }
        saveConfig();
        try {
            unsafe.lang.save();
        } catch (IOException e1) {
            getLogger().info("Error parsing lang file!");
        }
        List<String> ids = this.getConfig().getStringList("general.cars.fuel.items.ids");
        unsafe.uFuelItems = new ArrayList<>();
        for (String raw : ids) {
            ItemStack stack = ItemStackFromId.get(raw);
            unsafe.uFuelItems.add(stack);
        }
        unsafe.colors = new Colors(
                this.getConfig().getString("colorScheme.success"),
                this.getConfig().getString("colorScheme.error"),
                this.getConfig().getString("colorScheme.info"),
                this.getConfig().getString("colorScheme.title"),
                this.getConfig().getString("colorScheme.title")
        );
        PluginDescriptionFile pluginDescription = this.getDescription();
        Map<String, Map<String, Object>> commands = pluginDescription.getCommands();
        Set<String> keys = commands.keySet();
        for (String k : keys) {
            try {
                unsafe.cmdExecutor = new UCarsCommandExecutor(this);
                getCommand(k).setExecutor(unsafe.cmdExecutor);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE,
                        "Error registering command " + k.toString());
                e.printStackTrace();
            }
        }
        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            boolean success = setupProtocol();
            if (!success) {
                unsafe.protocolLib = false;
                getLogger()
                        .log(Level.WARNING,
                                "ProtocolLib (http://http://dev.bukkit.org/bukkit-plugins/protocollib/) was not found! For servers running MC 1.6 or above unsafe.is required for ucars to work!");
            }
        } else {
            unsafe.protocolLib = false;
            getLogger()
                    .log(Level.WARNING,
                            "ProtocolLib (http://http://dev.bukkit.org/bukkit-plugins/protocollib/) was not found! For servers running MC 1.6 or above unsafe.is required for ucars to work!");
        }
        unsafe.licensedPlayers = new ListStore(new File(getDataFolder()
                + File.separator + "licenses.txt"));
        unsafe.licensedPlayers.load();
        unsafe.listener = new UCarsListener(this);
        getServer().getPluginManager().registerEvents(unsafe.listener, this);
        unsafe.API = new UCarsAPI(this);
        unsafe.smoothDrive = this.getConfig().getBoolean("general.cars.smooth");

        UCars uCars = this;

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            UEntityMeta.cleanEntityObjs(uCars);
            UMeta.clean();
        }, 20 * 20L, 20 * 20L);
        // Note: System.gc is called automatically by the JVM
//		Bukkit.getScheduler().runTaskTimerAsynchronously(unsafe. () -> {
//            if(Runtime.getRuntime().maxMemory()-Runtime.getRuntime().freeMemory() > 1000){
//                System.gc();
//            }
//        }, 20* 20L, 120* 20L);
    }

    @Override
    public void onDisable() {
        saveHashMap(unsafe.fuel, this.getDataFolder().getAbsolutePath()
                + File.separator + "fuel.bin");
        unsafe.licensedPlayers.save();
        unhookPlugins();
        getLogger().info("uCars has been disabled!");
    }

    public boolean isIgnoreRails() {
        return unsafe.ignoreRails;
    }

    public HashMap<String, Double> getCarBoosts() {
        return unsafe.carBoosts;
    }

    public boolean haveProtocolLib() {
        return unsafe.protocolLib;
    }

    public String getIdList(final String configKey) {
        final List<String> s = this.getConfig().getStringList(configKey);
        StringBuilder msg = new StringBuilder();
        for (String str : s) {
            if (msg.length() < 1) {
                msg = new StringBuilder(str);
                continue; //Next iteration
            }
            msg.append(", ").append(str); //Append it
        }
        return msg.toString();
    }

    public final boolean isBlockEqualToConfigIds(final String configKey, Block block) {
        return isBlockEqualToConfigIds(this.getConfig().getStringList(configKey), block);
    }

    public final boolean isBlockEqualToConfigIds(List<String> rawIds, Block block) {
        // split by : then compare!
        for (String raw : rawIds) {
            final String[] parts = raw.split(":");
            if (parts.length < 1) {
            } else if (parts.length < 2) {
                if (parts[0].equalsIgnoreCase(block.getType().name())) {
                    return true;
                }
            } else {
                final String mat = parts[0];
                final int data = Integer.parseInt(parts[1]);
                final int blockData = block.getData(); //TODO Alternative to .getData()
                if (mat.equalsIgnoreCase(block.getType().name()) && blockData == data) {
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isItemEqualToConfigIds(List<String> rawIds, ItemStack item) {
        // split by : then compare!
        for (String raw : rawIds) {
            final String[] parts = raw.split(":");
            if (parts.length < 1) {
            } else if (parts.length < 2) {
                if (parts[0].equalsIgnoreCase(item.getType().name())) {
                    return true;
                }
            } else {
                final String mat = parts[0];
                final int data = Integer.parseInt(parts[1]);
                final int bData = item.getDurability();
                if (mat.equalsIgnoreCase(item.getType().name()) && bData == data) {
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isItemOnList(ArrayList<ItemStack> items, ItemStack item) {
        // split by : then compare!
        for (ItemStack raw : items) {
            final String mat = raw.getType().name().toUpperCase();
            final int data = raw.getDurability();
            final int bData = item.getDurability();
            if (mat.equalsIgnoreCase(item.getType().name()) && bData == data) {
                return true;
            }
        }
        return false;
    }

    public UCarsAPI getAPI() {
        return unsafe.API;
    }

    public void hookPlugin(Plugin plugin) {
        getAPI().hookPlugin(plugin);
    }

    public void unhookPlugin(Plugin plugin) {
        getAPI().unhookPlugin(plugin);
    }

    public void unhookPlugins() {
        getAPI().unhookPlugins();
    }

    public boolean isPluginHooked(Plugin plugin) {
        return getAPI().isPluginHooked(plugin);
    }

    public Plugin getPlugin(String name) {
        try {
            for (Plugin p : unsafe.hookedPlugins) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return p;
                }
            }
        } catch (Exception e) {
            //Concurrent error
            return null;
        }
        return null;
    }

    public boolean hasFireUpdateEvent() {
        return unsafe.fireUpdateEvent;
    }

    public boolean isSmoothDrive() {
        return unsafe.smoothDrive;
    }

    public boolean playersCanIgnoreTrafficLights() {
        return unsafe.playersIgnoreTrafficLights;
    }

    public HashMap<String, Double> getFuel() {
        return unsafe.fuel;
    }

    public ArrayList<ItemStack> getUFuelItems() {
        return unsafe.uFuelItems;
    }

    public UCarsCommandExecutor getCmdExecutor() {
        return unsafe.cmdExecutor;
    }

    public UCarsListener getListener() {
        return unsafe.listener;
    }

    public boolean isForcedRaceControls() {
        return unsafe.forceRaceControls;
    }

    public Lang getLang() {
        return unsafe.lang;
    }

    public boolean isTurningCircles() {
        return unsafe.turningCircles;
    }

    public Economy getEconomy() {
        return unsafe.economy;
    }

    public Unsafe getUnsafe() {
        return unsafe;
    }

    public static class Unsafe {
        public UCarsAPI API = null;
        // The main file
        public HashMap<String, Double> carBoosts = new HashMap<>();
        public HashMap<String, Double> fuel = new HashMap<>();
        public ArrayList<ItemStack> uFuelItems = new ArrayList<>();
        public ArrayList<Plugin> hookedPlugins = new ArrayList<>();

        public Object protocolManager = null;
        public Economy economy = null;

        //    public YamlConfiguration lang = new YamlConfiguration();
        public Lang lang;

        public UCarsCommandExecutor cmdExecutor = null;
        public UCarsListener listener = null;
        public ListStore licensedPlayers;
        public Colors colors;

        public boolean uCarsTrade = false;
        public boolean forceRaceControls = false;
        public boolean smoothDrive = true;
        public boolean playersIgnoreTrafficLights = false;
        public boolean turningCircles = true;
        public boolean fireUpdateEvent = false;
        public boolean ignoreRails = true;
        public boolean protocolLib = false;
        public boolean vault = false;

    }

}
