package com.useful.ucars.controls;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;

import com.useful.ucars.UCars;

public enum ControlScheme {	
	MOUSE(0,
			drivingInfoText()), 
	KEYBOARD(1,
			keyboardInfoText());
	
	private static BaseComponent[] drivingInfoText(){
		List<BaseComponent> infos = new ArrayList<BaseComponent>();
		TextComponent line0 = new TextComponent(UCars.colors.getTitle()+"W = "+ UCars.colors.getInfo()+"Forwards\n");
		infos.add(line0);
		TextComponent line1 = new TextComponent(UCars.colors.getTitle()+"S = "+ UCars.colors.getInfo()+"Backwards\n");
		infos.add(line1);
		TextComponent line2 = new TextComponent(UCars.colors.getTitle()+"A = "+ UCars.colors.getInfo()+"'Action'\n");
		infos.add(line2);
		TextComponent line3 = new TextComponent(UCars.colors.getTitle()+"D = "+ UCars.colors.getInfo()+"Brake (Hold to go slower)\n");
		infos.add(line3);
		TextComponent line5 = new TextComponent(UCars.colors.getTitle()+"Mouse = "+ UCars.colors.getInfo()+"Steering\n");
		infos.add(line5);
		TextComponent line4 = new TextComponent(UCars.colors.getTitle()+"Jump = "+ UCars.colors.getInfo()+"Switch controls");
		infos.add(line4);
		return infos.toArray(new BaseComponent[]{});
	}
	
	private static BaseComponent[] keyboardInfoText(){
		List<BaseComponent> infos = new ArrayList<BaseComponent>();
		TextComponent line0 = new TextComponent(UCars.colors.getTitle()+"W = "+ UCars.colors.getInfo()+"Forwards\n");
		infos.add(line0);
		TextComponent line1 = new TextComponent(UCars.colors.getTitle()+"S = "+ UCars.colors.getInfo()+"Backwards\n");
		infos.add(line1);
		TextComponent line2 = new TextComponent(UCars.colors.getTitle()+"A = "+ UCars.colors.getInfo()+"Turn Left\n");
		infos.add(line2);
		TextComponent line3 = new TextComponent(UCars.colors.getTitle()+"D = "+ UCars.colors.getInfo()+"Turn Right\n");
		infos.add(line3);
		TextComponent line4 = new TextComponent(UCars.colors.getTitle()+"Jump = "+ UCars.colors.getInfo()+"Switch controls");
		infos.add(line4);
		return infos.toArray(new BaseComponent[]{});
	}

	private int pos = 0;
	private BaseComponent[] infoText;
	private ControlScheme(int pos, BaseComponent... info){
		this.pos = pos;
		this.infoText = info;
	}
	
	public void showInfo(Player player){
		TextComponent message = new TextComponent("Steering: ");
		message.setColor(ChatColor.GREEN);
		TextComponent name = new TextComponent(name());
		name.setColor(ChatColor.YELLOW);
		name.setBold(true);
		if(infoText.length > 0){
			name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
					infoText
					));
		}
		message.addExtra(name);
		player.spigot().sendMessage(message);
	}
	
	public ControlScheme getNext(){
		int nextPos = this.pos + 1;
		return get(nextPos) == null ? get(0) : get(nextPos);
	}

	private static ControlScheme get(int pos){
		for(ControlScheme controlScheme : values()){
			if(controlScheme.pos == pos){
				return controlScheme;
			}
		}
		return null;
	}
	
	public static ControlScheme getDefault(){
		return ControlScheme.MOUSE;
	}
}
