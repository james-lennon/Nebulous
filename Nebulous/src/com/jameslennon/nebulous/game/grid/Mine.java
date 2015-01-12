package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.util.Images;

public class Mine extends GridItem {
	private BodyDef bd;
	private GridMap gm;
	private float x, y;
	private boolean rem;
	private long start;

	public Mine(float x, float y, GridMap gm) {
		img = new Image(Images.getImage("mine"));
		img.setWidth(Tile.WIDTH * .75f * Nebulous.PIXELS_PER_METER);
		img.setHeight(Tile.WIDTH * .75f * Nebulous.PIXELS_PER_METER);
		img.setX((x + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER
				- img.getWidth() / 2);
		img.setY((y + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER
				- img.getHeight() / 2);

		bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position.set(new Vector2(x + Tile.WIDTH / 2, y + Tile.WIDTH / 2));
		CircleShape shape = new CircleShape();
		shape.setRadius(Tile.WIDTH / 2f * .75f);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.restitution = .1f;
		body = Globals.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setUserData(this);
		// body.setTransform(new Vector2(10000, 10000), 0);
		this.x = x;
		this.y = y;
		this.gm = gm;
	}

	public void update() {
		if (rem) {
			body.setTransform(new Vector2(-100000, -100000), 0);
			rem = false;
			img.setColor(1, 1, 1, 0);
		}
		if (System.currentTimeMillis() - start >= 25000) {
			// body = world.createBody(bd);
			body.setUserData(this);
			img.setColor(1, 1, 1, 1);
			body.setTransform(new Vector2((x + Tile.WIDTH / 2), y + Tile.WIDTH
					/ 2), 0);
			rem = false;
		}
	}

	@Override
	public void collide(GridItem other) {
		byte id = -1;
		if (other instanceof Projectile)
			id = ((Projectile) other).getOwner();
		else if (other instanceof Ship)
			id = ((Ship) other).getPeer().getId();
		explode(id);
	}

	public void explode(byte id) {
		if (!rem && body != null) {
			Globals.getMap().addItem(
					new ExplodeEffect(body.getPosition().x,
							body.getPosition().y, img.getWidth() * .5f));
			gm.doExplosion(x, y, 30f, 3, false, true, id);
			rem = true;
			start = System.currentTimeMillis();
			// body = null;
		}
	}

}
