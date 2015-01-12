package com.jameslennon.nebulous.util;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.game.ships.AssaultShip;
import com.jameslennon.nebulous.game.ships.EngineerShip;
import com.jameslennon.nebulous.game.ships.HeavyShip;
import com.jameslennon.nebulous.game.ships.LightShip;
import com.jameslennon.nebulous.game.ships.Rogue;
import com.jameslennon.nebulous.game.ships.SaboteurShip;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.game.ships.Sniper;
import com.jameslennon.nebulous.game.ships.TacticalShip;
import com.jameslennon.nebulous.net.Peer;

public class ShipData {

	public static ArrayList<ShipData> ships;

	public static void init() {
		ships = new ArrayList<ShipData>();
		add("Light", "", LightShip.class, "Light", false);
		add("Heavy", "", HeavyShip.class, "Heavy", false);
		add("Assault", "", AssaultShip.class, "Assault", false);
		add("Tactical", "", TacticalShip.class, "Tactical", !UserData.isPro());
		add("Saboteur", "", SaboteurShip.class, "Saboteur", !UserData.isPro());
		add("Engineer", "", EngineerShip.class, "Engineer", !UserData.isPro());
		add("Sniper", "", Sniper.class, "Sniper2", !UserData.isPro());
		add("Rogue", "", Rogue.class, "Rogue2", !UserData.isPro());
	}

	private static void add(String name, String desc, Class c, String i,
			boolean p) {
		ships.add(new ShipData(name, desc, c, i, p));
	}

	public String name, desc;
	public Class c;
	public String imageName;
	public boolean premium;

	public ShipData(String n, String d, Class class1, String imgName,
			boolean premium) {
		name = n;
		desc = d;
		c = class1;
		imageName = imgName;
		this.premium = premium;
	}

	public Ship instantiate(Peer p, float x, float y) {
		try {
			return (Ship) c.getConstructors()[0].newInstance(p, x, y);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Image getImage() {
		Pixmap src = new Pixmap(Gdx.files.internal("res/" + imageName + ".png"));
		int w = src.getWidth(), h = src.getHeight();
		Pixmap pm = new Pixmap(w, h, Format.RGBA8888);
		Color color = new Color();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int v = src.getPixel(i, j);
				Color.rgba8888ToColor(color, v);
				if (color.a != 0) {
					pm.setColor(0, 1, 0, color.a);
					pm.drawPixel(i, j);
				}
			}
		}
		Image img = new Image(new Texture(pm));
		img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
		return img;
	}
}
