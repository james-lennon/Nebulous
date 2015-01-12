package com.jameslennon.nebulous.game.ships;

import com.jameslennon.nebulous.game.shots.Missile;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.Peer;

public class AssaultShip extends Ship {

	public AssaultShip(Peer p, float x, float y) {
		super(p, x, y, "Assault", 9, .025f, .5f, 40);
	}

	@Override
	public int getWaitTime() {
		return 750;
	}

	@Override
	public Projectile getShot(float ang) {
		return new Missile(getX(), getY(), ang, img.getHeight() / 2 + .1f, this
				.getPeer().getId());
	}

}
