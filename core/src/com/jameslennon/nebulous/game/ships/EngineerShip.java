package com.jameslennon.nebulous.game.ships;

import com.jameslennon.nebulous.game.shots.MineProjectile;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.Peer;

public class EngineerShip extends Ship {

	public EngineerShip(Peer p, float x, float y) {
		super(p, x, y, "Engineer", 6, .025f, .75f, .1f, 50);
	}

	@Override
	public int getWaitTime() {
		return 1000;
	}

	@Override
	public Projectile getShot(float ang) {
		return new MineProjectile(getX(), getY(), ang,
		// + (float) (Math.random() * MathUtils.PI / 3 - MathUtils.PI / 6),
				img.getHeight() / 2 + 10, getPeer().getId());
	}

}
