package com.jameslennon.nebulous.singleplayer;

import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.game.shots.WaveProjectile;
import com.jameslennon.nebulous.net.Peer;

public class HeavyAI extends AIShip {

	public HeavyAI(Peer p, float x, float y) {
		super(p, x, y, "Heavy", 7, .025f, .75f, 50);
	}

	public Projectile getShot(float ang) {
		return new WaveProjectile(getX(), getY(), ang,
				img.getHeight() / 2 + .1f, this.getPeer().getId());
	}

	@Override
	public int getWaitTime() {
		return 750;
	}

}
