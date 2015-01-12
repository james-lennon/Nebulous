package com.jameslennon.nebulous.singleplayer;

import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.game.shots.SmallLaser;
import com.jameslennon.nebulous.net.Peer;

public class InfantryAI extends AIShip {

	public InfantryAI(Peer p, float x, float y) {
		super(p, x, y, "Infantry", 5, .001f, .5f, 15);
	}

	public Projectile getShot(float ang) {
		return new SmallLaser(getX(), getY(), ang,
				img.getHeight() / 2 + .1f, getPeer().getId());
	}

	@Override
	public int getWaitTime() {
		return 500;
	}

}
