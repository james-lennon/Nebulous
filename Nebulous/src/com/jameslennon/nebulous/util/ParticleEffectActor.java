package com.jameslennon.nebulous.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ParticleEffectActor extends Actor {
	private ParticleEffect effect;
	private float x, y;
	private boolean remove;

	public ParticleEffectActor(ParticleEffect effect, float x, float y,
			boolean rem) {
		this.effect = effect;
		this.x = x;
		this.y = y;
		effect.start();
		remove = rem;
	}

	public ParticleEffectActor(ParticleEffect effect, float x, float y) {
		this(effect, x, y, true);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (!effect.isComplete())
			effect.draw(batch);
	}

	public void act(float delta) {
		super.act(delta);
		if (effect.isComplete() && remove) {
			effect.dispose();
			this.remove();
		} else {
			effect.setPosition(x, y);
			effect.update(delta);
		}
	}

	@Override
	public void setColor(Color c) {
		super.setColor(c);
		for (ParticleEmitter p : effect.getEmitters()) {
			p.getTransparency().setHigh(c.a, c.a);
		}
	}

	@Override
	public void setColor(float r, float g, float b, float a) {
		super.setColor(r, g, b, a);
		for (ParticleEmitter p : effect.getEmitters()) {
			p.getTransparency().setHigh(a, a);
		}
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		effect.setPosition(x, y);
		this.x = x;
		this.y = y;
	}

	public ParticleEffect getEffect() {
		return effect;
	}
}