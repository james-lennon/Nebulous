package com.jameslennon.nebulous.game.ships;

import com.jameslennon.nebulous.game.shots.GravityProjectile;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.Peer;

public class TacticalShip extends Ship {

	public TacticalShip(Peer p, float x, float y) {
		super(p, x, y, "Tactical", 7, .025f, .75f, .1f, 50);
	}

	@Override
	public int getWaitTime() {
		return 1000;
	}

	@Override
	public Projectile getShot(float ang) {
		return new GravityProjectile(getX(), getY(), ang,
				img.getHeight() / 2 + 10, getPeer().getId());
	}

}
