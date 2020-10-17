package com.useful.ucars.controls;

import com.useful.ucars.UCars;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public enum ControlScheme {
    MOUSE(0,
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "W = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Forwards\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "S = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Backwards\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "A = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "'Action'\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "D = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Brake (Hold to go slower)\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "Mouse = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Steering\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "Jump = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Switch controls")
    ),
    KEYBOARD(1,
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "W = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Forwards\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "S = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Backwards\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "A = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Turn Left\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "D = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Turn Right\n"),
            new TextComponent(JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getTitle() + "Jump = " + JavaPlugin.getPlugin(UCars.class).getUnsafe().colors.getInfo() + "Switch controls")
    );

    private final BaseComponent[] infoText;
    private int pos = 0;

    private ControlScheme(int pos, BaseComponent... info) {
        this.pos = pos;
        this.infoText = info;
    }

    private static ControlScheme get(int pos) {
        for (ControlScheme controlScheme : values()) {
            if (controlScheme.pos == pos) {
                return controlScheme;
            }
        }
        return null;
    }

    public static ControlScheme getDefault() {
        return ControlScheme.MOUSE;
    }

    public void showInfo(Player player) {
        TextComponent message = new TextComponent("Steering: ");
        message.setColor(ChatColor.GREEN);
        TextComponent name = new TextComponent(name());
        name.setColor(ChatColor.YELLOW);
        name.setBold(true);
        if (infoText.length > 0) {
            name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    infoText
            ));
        }
        message.addExtra(name);
        player.spigot().sendMessage(message);
    }

    public ControlScheme getNext() {
        int nextPos = this.pos + 1;
        return get(nextPos) == null ? get(0) : get(nextPos);
    }
}
