package com.jameslennon.nebulous;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class Main {
	public static void main(String[] args) {
		Settings settings = new Settings();
		settings.maxWidth = 2048;
		settings.maxHeight = 1024;

//		TexturePacker.process(settings, "../images",
//				"../Nebulous-android/assets/res", "game");

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Nebulous";
		cfg.width = Nebulous.APP_WIDTH;
		cfg.height = Nebulous.APP_HEIGHT;
		// cfg.width = 1136;
		// cfg.height = 640;
		cfg.resizable = false;
		cfg.useGL30 = false;
		// cfg.fullscreen=true;
		new LwjglApplication(new Nebulous(), cfg);
	}
}
