package com.jameslennon.nebulous.game.grid;

import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.net.Lobby;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.Images;

public class Flag extends GridItem {

	private Vector2 spawn;
	private byte id;
	private boolean dead = false;
	private Ship ship, lastShip;
	private long lastget;
	private Vector2 savedPos = new Vector2(), savedVel = new Vector2();
	private boolean update;

	public Flag(float x, float y, byte id) {
		spawn = new Vector2(x, y);
		this.id = id;

		img = new Image(Images.getImage("Flag"));
		img.setWidth(img.getWidth());
		img.setHeight(img.getHeight());
		img.setX((x + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER
				- img.getWidth() / 2);
		img.setY((y + Tile.WIDTH / 2) * Nebulous.PIXELS_PER_METER
				- img.getHeight() / 2);

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.position.set(new Vector2(x + Tile.WIDTH / 2, y + Tile.WIDTH / 2));
		bd.angularDamping = 0;
		bd.linearDamping = 0;
		CircleShape shape = new CircleShape();
		shape.setRadius(img.getHeight() / (2f * Nebulous.PIXELS_PER_METER));
		FixtureDef fd = new FixtureDef();
		fd.shape = shape;
		fd.restitution = .5f;
		body = Globals.getWorld().createBody(bd);
		body.createFixture(fd);
		body.setUserData(this);
	}

	@Override
	public void die() {
		dead = true;
		ship = null;
	}

	@Override
	public void collide(GridItem other) {
		if(dead)
			return;
		if (other instanceof Ship) {
			ship = (Ship) other;
			ship.setFlag(this);
			lastget = System.currentTimeMillis();
		}
	}

	@Override
	public void changeHealth(int amt, byte b) {
	}

	@Override
	public void update() {
		if (update) {
			body.setTransform(savedPos, 0);
			body.setLinearVelocity(savedVel);
			update = false;
		}
		super.update();
		if (ship == null) {
			Vector2 pos = getBody().getPosition();
			img.setX(pos.x * Nebulous.PIXELS_PER_METER - img.getWidth() / 2);
			img.setY(pos.y * Nebulous.PIXELS_PER_METER - img.getHeight() / 2);
		} else {
			img.setZIndex(ship.img.getZIndex() + 1);
			img.setX(ship.getX() * Nebulous.PIXELS_PER_METER - img.getWidth()
					/ 2);
			img.setY(ship.getY() * Nebulous.PIXELS_PER_METER - img.getHeight()
					/ 2);
			body.setTransform(1000000, 10000000, 0);
		}
		if (NetworkManager.isOnline()) {
			Lobby l = NetworkManager.getLobby();
			TreeMap<Integer, Peer> map = l.getMembers();
			Set<Integer> set = map.keySet();
			float min = Float.MAX_VALUE;
			Peer minPeer = null;
			for (int x : set) {
				Peer p = map.get(x);
				float f = p.getShip().getBody().getPosition()
						.dst(getBody().getPosition());
				if (f < min) {
					min = f;
					minPeer = p;
				}
			}
			if (minPeer.getId() == NetworkManager.getPlayerId() && ship == null) {
				NetworkManager.sendUpdateFlag(this);
			}
		}
		if (dead) {
			body.setTransform(spawn, 0);
			body.setLinearVelocity(0, 0);
			ship = null;
			dead = false;
		}
	}

	public byte getId() {
		return id;
	}

	public Vector2 getPos() {
		if (ship == null)
			return getBody().getPosition();
		return ship.getBody().getPosition();
	}

	public void setUpdateData(float x, float y, float dx, float dy) {
		savedPos.set(x, y);
		savedVel.set(dx, dy);
		update = true;
	}

	public void setState(float x, float y, float dx, float dy) {
		body.setTransform(x, y, 0);
		body.setLinearVelocity(dx, dy);
	}

	public Ship getShip() {
		return ship;
	}

}
