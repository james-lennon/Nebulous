package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.util.Images;

public class Tile {

	public static final int NONE = 0, BLOCK = 1, MINE = 2, SPAWN = 3;
	public static final float WIDTH = 2f;
	private float x, y;
	private Image img;

	public Tile(float x, float y) {
		int id = (int) (Math.random() * 6);
		img = new Image(Images.getImage("stars" + id));
		img.setWidth(Tile.WIDTH * Nebulous.PIXELS_PER_METER);
		img.setHeight(Tile.WIDTH * Nebulous.PIXELS_PER_METER);
		img.setX(x * Nebulous.PIXELS_PER_METER);
		img.setY(y * Nebulous.PIXELS_PER_METER);
		this.x = x * Nebulous.PIXELS_PER_METER;
		this.y = y * Nebulous.PIXELS_PER_METER;
	}

	public void show(Stage s) {
		s.addActor(img);
		img.setZIndex(1);
	}
}
