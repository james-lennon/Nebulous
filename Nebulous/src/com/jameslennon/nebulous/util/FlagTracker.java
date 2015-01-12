package com.jameslennon.nebulous.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.Flag;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.net.NetworkManager;

public class FlagTracker {
	private Flag flag;
	private Image img;
	private Camera camera;

	public FlagTracker(Flag f, Camera c) {
		flag = f;
		img = new Image(Images.getImage("Arrow"));
		Globals.getGui().addActor(img);
		camera = c;
	}

	public void update() {
		Ship s = flag.getShip();
		if (s != null && s.getPeer().getId() == NetworkManager.getPlayerId()) {
			img.setVisible(false);
		} else {
			img.setVisible(true);
			Vector2 cam = new Vector2(camera.position.x, camera.position.y);
			Vector2 tar = flag.getPos().scl(Nebulous.PIXELS_PER_METER);
			float ang = MathUtils.atan2(cam.y - tar.y, cam.x - tar.x);
			float off = 100;
			float dx = MathUtils.cos(ang) * off, dy = MathUtils.sin(ang) * off;
			img.setPosition(camera.viewportWidth / 2 - dx,
					camera.viewportHeight / 2 - dy);
			img.setRotation((ang + MathUtils.PI) * MathUtils.radiansToDegrees);
		}
	}

}
