package com.jameslennon.nebulous.singleplayer;

import com.jameslennon.nebulous.game.shots.Missile;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.Peer;

public class AssaultAI extends AIShip {

	public AssaultAI(Peer p, float x, float y) {
		super(p, x, y, "Assault", 9, .025f, .5f, .1f, 40);
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
