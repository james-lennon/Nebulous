package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;

public class Asteroid extends GridItem {
	public static final int SIZE_BIG = 0, SIZE_MED = 1, SIZE_SMALL = 2;

	private BodyDef bd;
	private int size;

	public Asteroid(float x, float y, int size) {
		float r = Tile.WIDTH / 3;
		if (size == SIZE_BIG) {
			img = new Image(Images.getImage("AsteroidBig"));
			r *= 3;
			setHealth(20);
		} else if (size == SIZE_MED) {
			int id = MathUtils.random(1, 2);
			img = new Image(Images.getImage("AsteroidMedium" + id));
			r *= 2;
			setHealth(10);
		} else if (size == SIZE_SMALL) {
			int id = MathUtils.random(1, 5);
			img = new Image(Images.getImage("AsteroidSmall" + id));
			setHealth(5);
		}
		img.setWidth(r * Nebulous.PIXELS_PER_METER);
		img.setHeight(r * Nebulous.PIXELS_PER_METER);
		img.setX(x + Tile.WIDTH / 2 - img.getWidth() / 2);
		img.setY(y + Tile.WIDTH / 2 - img.getHeight() / 2);

		bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.position.set(new Vector2(x + Tile.WIDTH / 2, y + Tile.WIDTH / 2));
		bd.angularDamping = 0;
		bd.linearDamping = 0;
		CircleShape shape = new CircleShape();
		shape.setRadius(r / 2f);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.restitution = .5f;
		// fd.friction =
		body = Globals.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setUserData(this);
		// body.setTransform(new Vector2(10000, 10000), 0);
		this.size = size;
	}

	@Override
	public void die() {
		super.die();
		if (size == SIZE_BIG) {
			int numAsteroids = 4;
			float power = 500f;
			for (int i = 0; i < numAsteroids; i++) {
				float angle = 2 * MathUtils.PI * i / numAsteroids
						- MathUtils.PI / 2;
				float x = MathUtils.cos(angle), y = MathUtils.sin(angle);
				Vector2 force = new Vector2(x * power, y * power);
				Asteroid a = new Asteroid(body.getPosition().x + x
						* img.getWidth() / 2, body.getPosition().y + y
						* img.getHeight() / 2, SIZE_MED);
				a.getBody().applyForceToCenter(force, true);
				Globals.getMap().addItem(a);
				a.show(Globals.getStage());
			}
		} else if (size == SIZE_MED) {
			int numAsteroids = 4;
			float power = 500f;
			for (int i = 0; i < numAsteroids; i++) {
				float angle = 2 * MathUtils.PI * i / numAsteroids
						- MathUtils.PI / 2;
				float x = MathUtils.cos(angle), y = MathUtils.sin(angle);
				Vector2 force = new Vector2(x * power, y * power);
				Asteroid a = new Asteroid(body.getPosition().x + x
						* img.getWidth() / 2, body.getPosition().y + y
						* img.getHeight() / 2, SIZE_SMALL);
				a.getBody().applyForceToCenter(force, true);
				Globals.getMap().addItem(a);
				a.show(Globals.getStage());
			}
		}
	}

	@Override
	public void update() {
		super.update();
		Vector2 pos = getBody().getPosition().scl(Nebulous.PIXELS_PER_METER);
		// img.setZIndex(2000);
		img.setX(pos.x - img.getWidth() / 2);
		img.setY(pos.y - img.getHeight() / 2);
		img.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
	}

}
