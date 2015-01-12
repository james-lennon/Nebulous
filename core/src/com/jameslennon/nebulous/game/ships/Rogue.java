package com.jameslennon.nebulous.game.ships;

import com.badlogic.gdx.math.MathUtils;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.game.shots.SmallLaser;
import com.jameslennon.nebulous.net.Peer;

public class Rogue extends Ship {

	public Rogue(Peer p, float x, float y) {
		super(p, x, y, "Rogue2", 6, .025f, .75f, .1f, 50);
	}

	@Override
	public int getWaitTime() {
		return 100;
	}

	@Override
	public Projectile getShot(float ang) {
		return new SmallLaser(
				getX(),
				getY(),
				ang
						+ (float) (Math.random() * MathUtils.PI / 6 - MathUtils.PI / 12),
				img.getHeight() / 2 + 10, getPeer().getId());
	}

}
