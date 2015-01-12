package com.jameslennon.nebulous.game.shots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.ExplodeEffect;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class GravityProjectile extends Projectile {

	private ParticleEffectActor pea;
	private int force;
	private float step = 2f;

	public GravityProjectile(float x, float y, float ang, float hf, byte b) {
		super(x, y, ang, 25, "GravityShot", hf, 20, 20, 15, b);
		ParticleEffect peEffect = new ParticleEffect();
		peEffect.load(Gdx.files.internal("data/Gravity.p"), Images.getAtlas());
		pea = new ParticleEffectActor(peEffect, x * Nebulous.PIXELS_PER_METER,
				y * Nebulous.PIXELS_PER_METER);
		Globals.getStage().addActor(pea);
		force = 0;
	}

	@Override
	public void update() {
		super.update();
		pea.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
				* Nebulous.PIXELS_PER_METER);
		float o = img.getWidth() / (2 * Nebulous.PIXELS_PER_METER);
		Globals.getMap().doExplosion(getX() + o, getY() + o, force += step, 3,
				true, false, getOwner());
		// em.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
		// * Nebulous.PIXELS_PER_METER);
	}

	@Override
	public void die() {
		super.die();
		pea.getEffect().getEmitters().get(0).allowCompletion();
		Globals.getMap().addItem(new ExplodeEffect(getX(), getY(), 15));
		Globals.getMap().doExplosion(getX(), getY(), 20, 2, false, true,
				getOwner());
	}

}
