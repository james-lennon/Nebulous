package com.jameslennon.nebulous.game.ships;

import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.game.shots.SniperShot;
import com.jameslennon.nebulous.net.Peer;

public class Sniper extends Ship {

	public Sniper(Peer p, float x, float y) {
		super(p, x, y, "Sniper2", 5, .025f, .75f, .1f, 50);
	}

	@Override
	public int getWaitTime() {
		return 2000;
	}

	@Override
	public Projectile getShot(float ang) {
		return new SniperShot(getX(), getY(), ang, img.getHeight() / 2 + 10,
				getPeer().getId());
	}

}
