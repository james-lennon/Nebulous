package com.jameslennon.nebulous.singleplayer;

import com.jameslennon.nebulous.game.shots.EMP;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.Peer;

public class SaboteurAI extends AIShip {

	public SaboteurAI(Peer p, float x, float y) {
		super(p, x, y, "Saboteur", 5, .025f, .5f, .1f, 40);
	}

	@Override
	public int getWaitTime() {
		return 600;
	}

	@Override
	public Projectile getShot(float ang) {
		return new EMP(getX(), getY(), ang, img.getHeight() / 2 + 10, getPeer()
				.getId());
	}

}
