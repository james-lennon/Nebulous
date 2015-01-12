package com.jameslennon.nebulous.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.grid.GridMap;
import com.jameslennon.nebulous.screens.PlayScreen;
import com.jameslennon.nebulous.util.MiniMap;

public class Globals {
	private static Skin skin;
	private static Stage stage, gui;
	private static World world;
	private static GridMap map;
	private static PlayScreen screen;
	private static MiniMap miniMap;
	private static Nebulous nebulous;
	private static BitmapFont font;

	public static Nebulous getNebulous() {
		return nebulous;
	}

	public static void setNebulous(Nebulous nebulous) {
		Globals.nebulous = nebulous;
	}

	public static MiniMap getMiniMap() {
		return miniMap;
	}

	public static void setMiniMap(MiniMap miniMap) {
		Globals.miniMap = miniMap;
	}

	public static GridMap getMap() {
		return map;
	}

	public static void setMap(GridMap map) {
		Globals.map = map;
	}

	public static Skin getSkin() {
		return skin;
	}

	public static void setSkin(Skin skin) {
		Globals.skin = skin;
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setStage(Stage stage) {
		Globals.stage = stage;
	}

	public static World getWorld() {
		return world;
	}

	public static void setWorld(World world) {
		Globals.world = world;
	}

	public static Stage getGui() {
		return gui;
	}

	public static void setGui(Stage gui) {
		Globals.gui = gui;
	}

	public static void setScreen(PlayScreen sc) {
		Globals.screen = sc;
	}

	public static PlayScreen getScreen() {
		return screen;
	}

	public static void setFont(BitmapFont f) {
		font = f;
	}
	
	public static BitmapFont getFont(){
		return font;
	}

}
