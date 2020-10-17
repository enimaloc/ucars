package com.useful.ucars;

import org.bukkit.ChatColor;

public class Colors {
    private final String success;
    private final String error;
    private final String info;
    private final String title;
    private final String tp;

    public Colors(String success, String error, String info, String title, String tp) {
        this.success = ChatColor.translateAlternateColorCodes('&', success);
        this.error = ChatColor.translateAlternateColorCodes('&', error);
        this.info = ChatColor.translateAlternateColorCodes('&', info);
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.tp = ChatColor.translateAlternateColorCodes('&', tp);
    }

    public String getSuccess() {
        return this.success;
    }

    public String getError() {
        return this.error;
    }

    public String getInfo() {
        return this.info;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTp() {
        return this.tp;
    }
}
