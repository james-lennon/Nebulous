package com.jameslennon.nebulous.game.shots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class SniperShot extends Projectile {

	public SniperShot(float x, float y, float ang, float hf, byte b) {
		super(x, y, ang, 80, "SniperShot", hf, 4, 4, 50, b);
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void die() {
		super.die();
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal("data/SniperExplosion.p"),
				Images.getAtlas());
		Globals.getStage().addActor(
				new ParticleEffectActor(effect, getX()
						* Nebulous.PIXELS_PER_METER, getY()
						* Nebulous.PIXELS_PER_METER));
	}

//	@Override
//	public void collide(GridItem other) {
//		if (isDead())
//			return;
//		if (other instanceof Block) {
//			die();
//			return;
//		}
//		other.changeHealth(-getDamage(), super.getOwner());
//	}
}
