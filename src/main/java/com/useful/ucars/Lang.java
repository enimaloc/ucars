package com.useful.ucars;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Lang {

    private final YamlConfiguration langConfiguration;
    private final File file;

    public Lang(String filePath) throws IOException, InvalidConfigurationException {
        this(new File(filePath));
    }
    public Lang(File langFile) throws IOException, InvalidConfigurationException {
        this.file = langFile;
        this.langConfiguration = new YamlConfiguration();
        this.langConfiguration.load(langFile);

        // Error
        init("error.prefix",              "&4");
        init("error.playerOnly",          "%color%Player only!");
        init("error.noProtocolLib",       "%color%ProtocolLib (http://dev.bukkit.org/bukkit-plugins/protocollib/) was not detected and is required for ucars in MC 1.6 or higher. Please install it if necessary!");
        init("error.noDrivePermission",   "%color%You don't have the permission ucars.ucar required to drive a car!");
        init("error.noPlacePermission",   "%color%You don't have the permission %permission% required to place a car!");
        init("error.noPlaceHere",         "%color%You are not allowed to place a car here!");
        init("error.fuel.empty",          "%color%You don't have any fuel left!");
        init("error.fuel.invalidAmount",  "%color%Amount invalid!");
        init("error.fuel.noMoney",        "%color%You have no money!");
        init("error.fuel.notEnoughMoney", "%color%That purchase costs %amount% %unit%! You have only %balance% %unit%!");
        init("error.fuel.disabled",       "%color%Fuel is not enabled!");
        init("error.boost.already",       "%color%Already boosting!");
        init("error.licenses.nocheat",    "%color%You need to do all the stages of ulicense to obtain a license! You need to do %command%!");
        init("error.licenses.noLicense",  "%color%To drive a car you need a license, do /ulicense to obtain one!");

        // Warning
        init("warning.prefix",            "");
        init("warning.hitByCar",          "%color%You were hit by car!");

        // Info
        init("info.prefix",               "&r");
        init("info.reload",               "%color%The config as been reloaded!");
        init("info.cars.remove",          "%color%&e%amount%&a cars in the world &e%world%&a were removed!");
        init("info.boost.low",            "%color%Initiated low level boost!");
        init("info.boost.medium",         "%color%Initiated medium level boost!");
        init("info.boost.high",           "%color%Initiated high level boost!");
        init("info.place",                "%color%You placed a car! Cars can be driven with similar controls to a horse!");

        // Success
        init("success.prefix",            "");
        init("success.fuel.success",      "%color%Successfully purchased %quantity% of fuel for %amount% %unit%! You now have %balance% %unit% left!");
        init("success.fuel.sellSuccess",  "%color%Successfully sold %quantity% of fuel for %amount% %unit%! You now have %balance% %unit% left!");
        init("success.licenses.success",  "%color%Congratulations! You can now drive a ucar!");

        // None
        init("none.prefix",               "");
        init("none.fuel.unit",            "%color%litre");
        init("none.licenses.next",        "%color%Now do %command% to continue!");
        init("none.licenses.basic",       "%color%A car is just a minecart placed on the ground, not rails. To place a car simply look and the floor while holding a minecart and right click!");
        init("none.licenses.control",     "%color%1) Look where you would like to go. 2) Use the 'w' key to go forward and 's' to go backwards. 3) Use the 'd' key to slow down/brake and the 'a' key to activate any action assgined to the car!");
        init("none.licenses.effects",     "%color%Car speed can change depending on what block you may drive over. These can be short term boosts or a speedmod block. Do /ucars for more info on boosts!");
        init("none.licenses.itemBoost",   "%color%Right clicking with certain items can give you different boosts. Do /ucars for more info!");
        init("none.rightClickWith",       "%color%Right click with ");
        init("none.driveOver",            "%color%Drive over ");

        // Important
        init("important.prefix",         "&9");
        init("important.fuel.isItem",    "%color%[Important] &eItem fuel is enabled-The above is irrelevant");
    }

    private void init(String key, Object value) {
        if (!langConfiguration.contains(key)) {
            langConfiguration.set(key, value);
        }
    }

    public String get(String key) {
        return get(key, false);
    }
    public String get(String key, boolean noPrefix) {
        return ChatColor.translateAlternateColorCodes('&',
                !langConfiguration.contains(key) ? key :
                noPrefix ? langConfiguration.getString(key) :
                get(
                    langConfiguration.getString(key),
                    true,
                    new String[]{"%color%", langConfiguration.getString(key.substring(0, key.indexOf("."))+".prefix")}
                ));
    }

    public String get(String path, String[]... values) {
        return get(path, false, values);
    }
    public String get(String path, boolean noPrefix, String[]... values) {
        String lang = this.get(path, noPrefix);
        for (String[] value : values) {
            lang = lang.replaceAll(Pattern.quote(value[0]), Pattern.quote(value[1]));
        }
        return lang;
    }

    public void save() throws IOException {
        langConfiguration.save(file);
    }
}
