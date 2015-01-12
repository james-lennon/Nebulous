package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.MiniMap;

public class Block extends GridItem {

	public Block(float x, float y) {
		img = new Image(Images.getImage("Block2"));
		img.setWidth(Tile.WIDTH * Nebulous.PIXELS_PER_METER);
		img.setHeight(Tile.WIDTH * Nebulous.PIXELS_PER_METER);
		img.setX(x * Nebulous.PIXELS_PER_METER);
		img.setY(y * Nebulous.PIXELS_PER_METER);

		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		bd.position
				.set(new Vector2((x + Tile.WIDTH / 2), (y + Tile.WIDTH / 2)));
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(Tile.WIDTH / 2f, Tile.WIDTH / 2f);
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.restitution = .01f;
		body = Globals.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setUserData(this);

		MiniMap.addBlock(new Vector2(x, y));
	}

	@Override
	public void update() {
	}

}
