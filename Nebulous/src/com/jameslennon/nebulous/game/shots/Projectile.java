package com.jameslennon.nebulous.game.shots;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.GridItem;
import com.jameslennon.nebulous.util.Images;

public class Projectile extends GridItem {
	public static final byte TYPE_LASER = 0, TYPE_WAVE = 1, TYPE_MISSILE = 2,
			TYPE_SMALL_LASER = 3, TYPE_SNIPER = 4, TYPE_EMP = 5,
			TYPE_MINE = 6, TYPE_GRAVITY = 7;

	private float dx, dy, ang;
	private int damage;
	private byte id;

	public Projectile(float x, float y, float ang, float speed, String imgName,
			float offset, float w, float h, int damage, byte b) {
		this(x, y, ang, speed, imgName, offset, w, h, damage, b, .5f);
	}

	public Projectile(float x, float y, float ang, float speed, String imgName,
			float offset, float w, float h, int damage, byte b, float density) {
		this.damage = damage;
		ang += MathUtils.PI / 2;
		w /= Nebulous.PIXELS_PER_METER;
		h /= Nebulous.PIXELS_PER_METER;
		dx = MathUtils.cos(ang) * speed;
		dy = MathUtils.sin(ang) * speed;
		img = new Image(Images.getImage(imgName));
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.bullet = true;
		bd.linearDamping = 0;
		offset /= Nebulous.PIXELS_PER_METER;
		offset += h / 2;
		float shiftx = MathUtils.cos(ang) * offset, shifty = MathUtils.sin(ang)
				* offset;
		bd.position.set(new Vector2(x + shiftx, y + shifty));
		FixtureDef fd = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
		img.setRotation(ang * MathUtils.radiansToDegrees - 90);
		shape.setAsBox(w / 2, h / 2);
		fd.shape = shape;
		fd.friction = 1f;
		fd.density = .75f;
		fd.filter.categoryBits = 2;
		fd.filter.maskBits = 1;
		body = Globals.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setLinearVelocity(dx, dy);
		body.setTransform(x + shiftx, y + shifty, ang - MathUtils.PI / 2);
		body.setUserData(this);
		this.ang = ang - MathUtils.PI / 2;
		img.setPosition(
				body.getPosition().x * Nebulous.PIXELS_PER_METER
						- img.getWidth() / 2, body.getPosition().y
						* Nebulous.PIXELS_PER_METER - img.getHeight() / 2);
		id = b;
	}

	public void show() {
		Globals.getStage().addActor(img);
	}

	public void update() {
//		img.setZIndex(1010);
		body.setTransform(body.getPosition(), ang);
		img.setPosition(
				body.getPosition().x * Nebulous.PIXELS_PER_METER
						- img.getWidth() / 2, body.getPosition().y
						* Nebulous.PIXELS_PER_METER - img.getHeight() / 2);
	}

	public Image getImage() {
		return img;
	}

	@Override
	public void collide(GridItem other) {
		if (isDead())
			return;
//		if (other instanceof Ship && id == NetworkManager.getPlayerId()) {
//			if (other.getHealth() - getDamage() <= 0
//					&& id == NetworkManager.getPlayerId()) {
//				byte oid = ((Ship) other).getPeer().getId();
//				NetworkManager.sendKill(oid);
//			}
//		}
		other.changeHealth(-getDamage(), id);
		die();
	}

	public float getX() {
		return body.getPosition().x;
	}

	public float getY() {
		return body.getPosition().y;
	}

	public void setDamage(int d) {
		damage = d;
	}

	public int getDamage() {
		return damage;
	}

	public byte getOwner() {
		return id;
	}
}
