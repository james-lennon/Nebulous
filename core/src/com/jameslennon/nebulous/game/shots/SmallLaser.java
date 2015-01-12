package com.jameslennon.nebulous.game.shots;

import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.ExplodeEffect;

public class SmallLaser extends Projectile {
//	private ParticleEffectActor pea;

	public SmallLaser(float x, float y, float ang, float o, byte b) {
		super(x, y, ang, 70, "SmallLaser", o, 8, 8, 4, b);
//		ParticleEffect peEffect = new ParticleEffect();
//		peEffect.load(Gdx.files.internal("data/LaserTrail.p"),
//				Images.getAtlas());
//		pea = new ParticleEffectActor(peEffect, x * Nebulous.PIXELS_PER_METER,
//				y * Nebulous.PIXELS_PER_METER);
//		Globals.getStage().addActor(pea);
	}

	@Override
	public void update() {
		super.update();
//		pea.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
//				* Nebulous.PIXELS_PER_METER);
	}

	@Override
	public void die() {
		super.die();
		Globals.getMap().addItem(new ExplodeEffect(getX(), getY(), 10));
//		pea.getEffect().allowCompletion();
	}
}
