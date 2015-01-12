package com.jameslennon.nebulous.game.ships;

import com.jameslennon.nebulous.game.shots.EMP;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.Peer;

public class SaboteurShip extends Ship {

	public SaboteurShip(Peer p, float x, float y) {
		super(p, x, y, "Saboteur", 5, .025f, .5f, .1f, 50);
	}

	@Override
	public int getWaitTime() {
		return 700;
	}

	@Override
	public Projectile getShot(float ang) {
		return new EMP(getX(), getY(), ang, img.getHeight() / 2 + 10,
				getPeer().getId());
	}

}
