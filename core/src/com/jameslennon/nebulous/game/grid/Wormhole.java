package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;

public class Wormhole extends GridItem {
	private float x, y;

	@Override
	public void collide(GridItem other) {
		other.die();
	}

	public Wormhole(float x, float y) {
		img = new Image(Images.getImage("Wormhole"));
		img.setWidth(Tile.WIDTH * Nebulous.PIXELS_PER_METER);
		img.setHeight(Tile.WIDTH * Nebulous.PIXELS_PER_METER);
		img.setX((x + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER
				- img.getWidth() / 2);
		img.setY((y + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER
				- img.getHeight() / 2);

		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position.set(new Vector2(x + Tile.WIDTH / 2, y + Tile.WIDTH / 2));
		bd.angularDamping = 0;
		bd.linearDamping = 0;
		CircleShape shape = new CircleShape();
		shape.setRadius(Tile.WIDTH * .75f / 2f);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.restitution = .5f;
		body = Globals.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setUserData(this);
		this.x = x;
		this.y = y;
		// sr = new ShapeRenderer();
	}

	@Override
	public void update() {
		Globals.getMap().doExplosion(x + Tile.WIDTH / 2, y + Tile.WIDTH / 2,
				10, 3, true, false, (byte) -1);
		// sr.setProjectionMatrix(Globals.getStage().getCamera().combined);
		// sr.begin(ShapeType.Line);
		// sr.circle((x + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER,
		// (y + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER, 3
		// * Tile.WIDTH * Nebulous.PIXELS_PER_METER);
		// sr.end();
	}

}
