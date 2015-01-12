package com.jameslennon.nebulous.singleplayer;

import com.jameslennon.nebulous.game.shots.LaserProjectile;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.Peer;

public class LightAI extends AIShip {

	public LightAI(Peer p, float x, float y) {
		super(p, x, y, "Light", 5, .001f, .5f, 30);
	}

	public Projectile getShot(float ang) {
		return new LaserProjectile(getX(), getY(), ang,
				img.getHeight() / 2 + .1f, this.getPeer().getId());
	}

	@Override
	public int getWaitTime() {
		return 250;
	}

}
