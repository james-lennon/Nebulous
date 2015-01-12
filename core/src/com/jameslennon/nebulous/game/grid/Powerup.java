package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class Powerup extends GridItem {

	public static final int ATK = 0, INV = 1, FAST = 2;

	private float x, y;
	private boolean collide;
	private long lastcollide;
	private int type;

	private boolean hidden;

	public Powerup(float x, float y, int t) {
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position.set(new Vector2(x + Tile.WIDTH / 2, y + Tile.WIDTH / 2));
		bd.angularDamping = 0;
		bd.linearDamping = 0;
		CircleShape shape = new CircleShape();
		shape.setRadius(Tile.WIDTH * .75f / 2f);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.density = 0;
		fd.restitution = 0;
		fd.filter.categoryBits = 2;
		fd.filter.maskBits = 1;
		body = Globals.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setUserData(this);
		this.x = x + Tile.WIDTH / 2;
		this.y = y + Tile.WIDTH / 2;
		type = t;
		initType();
	}

	private void initType() {
		if (img != null)
			img.remove();
		type = (type + 1) % 3;
		if (type == ATK) {
			img = new Image(Images.getImage("AtkPowerup2"));
		} else if (type == INV) {
			img = new Image(Images.getImage("InvPowerup2"));
		} else if (type == FAST) {
			img = new Image(Images.getImage("FastPowerup2"));
		}
		img.setPosition((x - Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER,
				(y - Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER);
		// img.setSize(Tile.WIDTH * Nebulous.PIXELS_PER_METER, Tile.WIDTH
		// * Nebulous.PIXELS_PER_METER);
		Globals.getStage().addActor(img);
	}

	@Override
	public void update() {
		super.update();
		if (collide) {
			// ParticleEffect effect = new ParticleEffect();
			// effect.load(Gdx.files.internal("data/Powerup.p"),
			// Images.getAtlas());
			// ParticleEffectActor pea = new ParticleEffectActor(effect,
			// body.getPosition().x * Nebulous.PIXELS_PER_METER,
			// body.getPosition().y * Nebulous.PIXELS_PER_METER);
			// Globals.getStage().addActor(pea);
			body.setTransform(-10000, -10000, 0);
			lastcollide = System.currentTimeMillis();
			collide = false;
			img.setVisible(false);
			hidden = true;
		}
		if (hidden && System.currentTimeMillis() - lastcollide >= 30000) {
			body.setTransform(x, y, 0);
			initType();
			hidden = false;
		}
	}

	@Override
	public void collide(GridItem other) {
		if (other instanceof Ship) {
			collide = true;
			Ship s = (Ship) other;
			ParticleEffect effect = new ParticleEffect();
			effect.load(Gdx.files.internal("data/Powerup.p"), Images.getAtlas());
			ParticleEffectActor pea = new ParticleEffectActor(effect, s
					.getBody().getPosition().x * Nebulous.PIXELS_PER_METER, s
					.getBody().getPosition().y * Nebulous.PIXELS_PER_METER);
			Globals.getStage().addActor(pea);
			if (type == ATK) {
				s.boostAttack();
			} else if (type == INV) {
				s.setInvisible();
			} else if (type == FAST) {
				s.boostSpeed();
			}
		}
	}

	@Override
	public void changeHealth(int amt, byte id) {
	}
}
