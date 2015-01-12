package com.jameslennon.nebulous.singleplayer;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.GridItem;
import com.jameslennon.nebulous.game.grid.Mine;
import com.jameslennon.nebulous.game.grid.Tile;
import com.jameslennon.nebulous.game.grid.Wormhole;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.game.shots.MineProjectile;

public class ShipController {

	private Ship ship, player;
	private long lastchange;
	private final int num_angles = 10;
	private final float scan_radius = 6 * Tile.WIDTH,
			rot_amt = MathUtils.PI / 20, err = MathUtils.PI / 4;
	private hit[] hits;
	private float angle;
	ShapeRenderer sr = new ShapeRenderer();

	public ShipController(Ship s) {
		this.ship = s;
		player = SinglePlayerManager.getPlayerShip();
		hits = new hit[num_angles];
		angle = 0;
	}

	public void update() {
		World world = Globals.getWorld();
		Vector2 pos = ship.getBody().getPosition();
		float acc = .5f;
		boolean turn = false;
		while (angle > MathUtils.PI * 2) {
			angle -= MathUtils.PI * 2;
		}
		if (angle < 0)
			angle += MathUtils.PI * 2;
		// pos = pos.scl(Nebulous.PIXELS_PER_METER);
		for (int i = 0; i < num_angles; i++) {
			final int index = i;
			float ang = i * 2 * MathUtils.PI / num_angles;
			world.rayCast(new RayCastCallback() {

				@Override
				public float reportRayFixture(Fixture f, Vector2 pt,
						Vector2 norm, float frac) {
					hit h = new hit(frac, (GridItem) f.getBody().getUserData());
					hits[index] = h;
					return 0;
				}
			}, pos, new Vector2(pos.x + MathUtils.cos(ang) * scan_radius, pos.y
					+ MathUtils.sin(ang) * scan_radius));
			hit h = hits[i];
			// float ang = i * 2 * MathUtils.PI / num_angles;
			if (h == null)
				continue;
			double diff = Math.min(Math.abs(ang - angle),
					Math.abs(ang - angle + 2 * MathUtils.PI));
			if (h.f <= 1.5 && h.data instanceof Ship) {
				// && System.currentTimeMillis() - lastchange >= 1000) {
				Ship s = (Ship) h.data;
				if (s.isPlayer() && !s.isInvisible()
						&& s.getState() != Ship.STATE_SPAWNING
						&& ship.getState() != Ship.STATE_SPAWNING) {
					Vector2 pos2 = s.getBody().getPosition()
							.scl(Nebulous.PIXELS_PER_METER);
					angle = MathUtils.atan2(pos.y * Nebulous.PIXELS_PER_METER
							- pos2.y, pos.x * Nebulous.PIXELS_PER_METER
							- pos2.x)
							+ MathUtils.PI;
					float chg = (float) (Math.random() * err - err / 2);
					angle += chg;
					ship.shoot();
					angle -= chg;
					hits[i] = null;
					turn = true;
					lastchange = System.currentTimeMillis();
					break;
				}
			} else if (diff <= MathUtils.PI / 9
					&& h.f < .75f
					&& (h.data instanceof Wormhole || h.data instanceof Mine || h.data instanceof MineProjectile)) {
				float amt = angle > ang ? 1 : -1;
				angle = ang + MathUtils.PI * 9 / 10 * amt;
				// angle = ang + MathUtils.PI / 2;
				lastchange = System.currentTimeMillis();
				turn = true;
				hits[i] = null;
				// System.out.println("avoiding thing");
				acc = 1;
				break;
			}
			if (diff <= MathUtils.PI / 9 && h.f < .5) {
				if (System.currentTimeMillis() - lastchange < 100)
					continue;
				// angle -= 2 * rot_amt;
				if (hits[(i + 1) % num_angles] == null) {
					angle -= rot_amt;
				} else if (hits[((i - 1) + num_angles) % num_angles] == null) {
					angle += rot_amt;
				} else {
					angle += 3 * rot_amt;
				}
				turn = true;
				angle %= 2 * MathUtils.PI;
				// if (ship.getBody().getLinearVelocity().len() >= .1)
				// acc = 0.f;
				lastchange = System.currentTimeMillis();
				// break;
				hits[i] = null;
			}
		}
		if (!turn && player.getState() != Ship.STATE_DEAD
				&& !player.isInvisible()
				&& System.currentTimeMillis() - lastchange >= 500) {
			Vector2 pos2 = player.getBody().getPosition()
					.scl(Nebulous.PIXELS_PER_METER);
			float dst = MathUtils.atan2(pos.y * Nebulous.PIXELS_PER_METER
					- pos2.y, pos.x * Nebulous.PIXELS_PER_METER - pos2.x)
					+ MathUtils.PI;
			float amt = angle > dst ? -rot_amt : rot_amt;
			// System.out.println("turning to ship");
			// angle += amt;
			angle = dst;
		}
		ship.thrust(acc * MathUtils.cos(angle), acc * MathUtils.sin(angle));
		ship.setRotation(angle - MathUtils.PI / 2);
	}

	private class hit {
		public GridItem data;
		public float f;

		public hit(float frac, GridItem data) {
			this.f = frac;
			this.data = data;
		}
	}

}
