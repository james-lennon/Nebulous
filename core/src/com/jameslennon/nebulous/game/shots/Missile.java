package com.jameslennon.nebulous.game.shots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class Missile extends Projectile {

	private ParticleEffectActor pea;

	public Missile(float x, float y, float ang, float hf, byte b) {
		super(x, y, ang, 40, "Missile2", hf, 20, 20, 8, b);
		ParticleEffect peEffect = new ParticleEffect();
		peEffect.load(Gdx.files.internal("data/Trail.p"), Images.getAtlas());
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
		pea.getEffect().getEmitters().get(0).allowCompletion();
		// Globals.getMap().addItem(new ExplodeEffect(getX(), getY(), 15));
		ParticleEffect exp = new ParticleEffect();
		exp.load(Gdx.files.internal("data/MissileExplode.p"), Images.getAtlas());
		Globals.getStage().addActor(
				new ParticleEffectActor(exp,
						getX() * Nebulous.PIXELS_PER_METER, getY()
								* Nebulous.PIXELS_PER_METER));
		Globals.getMap().doExplosion(getX(), getY(), 20, 2, false, true,
				getOwner());
	}

}
