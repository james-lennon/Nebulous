package com.jameslennon.nebulous.game.shots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.ExplodeEffect;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class WaveProjectile extends Projectile {

	private ParticleEffectActor pea;

	public WaveProjectile(float x, float y, float ang, float o, byte b) {
		// super(x, y, ang, 20, "EnergyWave", o, 77, 27, 20, b);
		super(x, y, ang, 20, "EnergyWave2", o, 38, 13, 15, b);
		ParticleEffect peEffect = new ParticleEffect();
		peEffect.load(Gdx.files.internal("data/LaserTrail.p"),
				Images.getAtlas());
		pea = new ParticleEffectActor(peEffect, x * Nebulous.PIXELS_PER_METER,
				y * Nebulous.PIXELS_PER_METER);
		Globals.getStage().addActor(pea);
	}

	@Override
	public void update() {
		super.update();
		pea.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
				* Nebulous.PIXELS_PER_METER);
	}

	@Override
	public void die() {
		super.die();
		Globals.getMap().addItem(new ExplodeEffect(getX(), getY(), 10));
		pea.getEffect().allowCompletion();
	}
}
