package com.jameslennon.nebulous.game.shots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.ExplodeEffect;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class MineProjectile extends Projectile {

	private ParticleEffectActor pea;
	private long starttime;

	public MineProjectile(float x, float y, float ang, float hf, byte b) {
		super(x, y, ang, 60, "MineShot", hf, 10, 10, 10, b);
		ParticleEffect peEffect = new ParticleEffect();
		peEffect.load(Gdx.files.internal("data/LaserTrail.p"),
				Images.getAtlas());
		pea = new ParticleEffectActor(peEffect, x * Nebulous.PIXELS_PER_METER,
				y * Nebulous.PIXELS_PER_METER);
		Globals.getStage().addActor(pea);
		starttime = System.currentTimeMillis();
	}

	@Override
	public void update() {
		super.update();
		pea.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
				* Nebulous.PIXELS_PER_METER);
		Vector2 vel = body.getLinearVelocity();
		vel.scl(.85f);
		body.setLinearVelocity(vel);
		if (System.currentTimeMillis() - starttime > 10000) {
			die();
		}
		// em.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
		// * Nebulous.PIXELS_PER_METER);
	}

	@Override
	public void die() {
		super.die();
		pea.getEffect().getEmitters().get(0).allowCompletion();
		// ParticleEffect effect = new ParticleEffect();
		// effect.load(Gdx.files.internal("data/Electricity.p"),
		// Gdx.files.internal("data"));
		// Globals.getStage().addActor(
		// new ParticleEffectActor(effect, getX()
		// * Nebulous.PIXELS_PER_METER, getY()
		// * Nebulous.PIXELS_PER_METER));
		Globals.getMap().addItem(new ExplodeEffect(getX(), getY(), 15));
		Globals.getMap().doExplosion(getX(), getY(), 10, 2, false, true,
				getOwner());
	}
}
