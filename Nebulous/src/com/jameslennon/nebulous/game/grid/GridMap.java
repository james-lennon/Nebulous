package com.jameslennon.nebulous.game.grid;

import java.util.ArrayList;
import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.util.ActionCam;
import com.jameslennon.nebulous.util.GameType;

public class GridMap {

	private Tile[][] tiles;
	private int w, h;
	private ArrayList<Explosion> explosions;
	private ArrayList<Body> bodiesToRemove;
	private ArrayList<Vector2> spawns1, spawns2;
	private ArrayList<GridItem> items, itemsToRemove;
	private ArrayList<Flag> flags;

	public GridMap(int k, int l) {
		w = k;
		h = l;
		tiles = new Tile[w][h];
		explosions = new ArrayList<GridMap.Explosion>();
		spawns1 = new ArrayList<Vector2>();
		spawns2 = new ArrayList<Vector2>();
		items = new ArrayList<GridItem>();
		bodiesToRemove = new ArrayList<Body>();
		itemsToRemove = new ArrayList<GridItem>();
		flags = new ArrayList<Flag>();
	}

	public static GridMap loadFromFile(FileHandle f) {
		try {
			Scanner in = new Scanner(f.read());
			int w = Integer.parseInt(in.next()), h = Integer
					.parseInt(in.next());
			byte flagid = 0;
			int p = 0;
			GridMap gm = new GridMap(w + 2, h + 2);
			String[] g = new String[h];
			for (int i = 0; i < h; i++)
				g[i] = in.next();
			for (int j = 0; j < h + 2; j++) {
				for (int i = 0; i < w + 2; i++) {
					if (i == 0 || j == 0 || i == w + 1 || j == h + 1) {
						gm.set(i, j, new Tile((i * Tile.WIDTH),
								((h - j) * Tile.WIDTH)));
						gm.addItem(new Block((i * Tile.WIDTH), (h - j)
								* Tile.WIDTH));
					} else {
						String s = g[j - 1].substring(i - 1, i);
						GridItem gi = null;
						float x = (i * Tile.WIDTH), y = (h - j) * Tile.WIDTH;
						if (s.equals(".")) {

						} else if (s.equals("b")) {
							gi = new Block(x, y);
						} else if (s.equals("x")) {
							gi = new Mine(x, y, gm);
						} else if (s.equals("s1") || s.equals("s")) {
							gm.addSpawn(i * Tile.WIDTH + Tile.WIDTH / 2,
									(j + 1) * Tile.WIDTH + Tile.WIDTH / 2, 0);
						} else if (s.equals("e")) {
							gm.addSpawn(i * Tile.WIDTH + Tile.WIDTH / 2,
									(j + 1) * Tile.WIDTH + Tile.WIDTH / 2, 1);
						} else if (s.equals("a")) {
							gi = new Asteroid(x, y, Asteroid.SIZE_BIG);
						} else if (s.equals("w")) {
							gi = new Wormhole(x, y);
						} else if (s.equals("f")) {
							gi = new Flag(x, y, flagid++);
							gm.addFlag((Flag) gi);
						} else if (s.equals("p")) {
							gi = new Powerup(x, y, p++);
						}
						gm.set(i, j, new Tile((i * Tile.WIDTH),
								((h - j) * Tile.WIDTH)));
						if (gi != null)
							gm.addItem(gi);
					}
				}
			}
			in.close();
			return gm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void set(int i, int j, Tile tile) {
		tiles[i][j] = tile;
	}

	public void show(Stage s) {
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				tiles[i][j].show(s);
			}
		}
		// for (GridItem g : items) {
		// g.show(s);
		// }
	}

	private void doExplosion(Explosion e) {
		float maxdist = e.rad * Tile.WIDTH, maxforce = e.force;
		// Array<Body> bodies = new Array<Body>();
		// Globals.getWorld().getBodies(bodies);
		float maxdamage = 20;
		for (GridItem gi : items) {
			Body b = gi.getBody();
			if (b == null)
				continue;
			if (b.getUserData() instanceof Projectile && e.suck)
				continue;
			float dx = b.getPosition().x - e.point.x, dy = b.getPosition().y
					- e.point.y;
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			if (dist >= maxdist)
				continue;
			float force = (maxdist - dist) / maxdist;
			if (e.suck) {
				force = -force;
			}
			float damage = force * maxdamage;
			if (e.damage) {
				gi.changeHealth((int) -damage, e.id);
			}
			force *= maxforce;
			float ang;
			ang = MathUtils.atan2(b.getPosition().y - e.point.y,
					b.getPosition().x - e.point.x);
			// if (e.suck)
			// ang += MathUtils.PI;
			if (e.suck)
				b.applyForceToCenter(new Vector2(MathUtils.cos(ang) * force,
						MathUtils.sin(ang) * force), true);
			else {
				b.applyLinearImpulse(new Vector2(MathUtils.cos(ang) * force,
						MathUtils.sin(ang) * force), b.getPosition(), true);
				if (b.getUserData() instanceof Ship) {
					Ship s = (Ship) b.getUserData();
					if (GameType.getType() == GameType.online) {
						if (s.getPeer().getId() == NetworkManager.getPlayerId()) {
							((ActionCam) Globals.getStage().getCamera())
									.startShake(force * 2, 2, .5f);
						}
					} else {
						if (s.isPlayer()) {
							((ActionCam) Globals.getStage().getCamera())
									.startShake(force * 2, 2, .5f);
						}
					}
				}
			}
		}
	}

	public void update() {
		for (Explosion e : explosions) {
			doExplosion(e);
		}
		for (int i = 0; i < itemsToRemove.size(); i++) {
			items.remove(itemsToRemove.get(i));
		}
		itemsToRemove.clear();
		for (int i = 0; i < bodiesToRemove.size(); i++) {
			Globals.getWorld().destroyBody(bodiesToRemove.get(i));
		}
		bodiesToRemove.clear();
		for (int i = 0; i < items.size(); i++) {
			items.get(i).update();
		}
	}

	public void doExplosion(float x, float y, float force, int rad,
			boolean suck, boolean damage, byte id) {
		doExplosion(new Explosion(x, y, force, rad, suck, damage, id));
	}

	public void addExplosion(float x, float y, float force, int rad,
			boolean suck, boolean damage) {
		explosions
				.add(new Explosion(x, y, force, rad, suck, damage, (byte) -1));
	}

	public Vector2 getSpawn(byte b) {
		if (!NetworkManager.isOnline())
			return spawns1.get(0);
		return spawns1.get(b % 4);
		// int half = NetworkManager.getGameSize() / 2;
		// int index = b % half;
		// if (b < half)
		// return spawns1.get(index);
		// else
		// return spawns2.get(index);
	}

	public void addSpawn(float x, float y, int team) {
		Vector2 sp = new Vector2(x, h * Tile.WIDTH - y);
		if (team == 0)
			spawns1.add(sp);
		else
			spawns2.add(sp);
	}

	private class Explosion {
		public Vector2 point;
		public float force;
		public int rad;
		public boolean suck, damage;
		private byte id;

		public Explosion(float x, float y, float f, int r, boolean s,
				boolean d, byte b) {
			point = new Vector2(x, y);
			force = f;
			rad = r;
			suck = s;
			damage = d;
			id = b;
		}
	}

	public void addItem(GridItem gi) {
		gi.show(Globals.getStage());
		if (gi instanceof Block)
			return;
		items.add(gi);
	}

	public void addFlag(Flag f) {
		flags.add(f);
	}

	public void removeItem(GridItem g) {
		itemsToRemove.add(g);
	}

	public void removeBody(Body body) {
		if (body != null)
			bodiesToRemove.add(body);
	}

	public void updateFlag(float x, float y, float dx, float dy) {
		flags.get(0).setUpdateData(x, y, dx, dy);
		// savedPos.set(x, y);
		// savedVel.set(dx, dy);
		// shouldUpdateFlag = true;
	}

	public Flag getFlag(int b) {
		if (b >= flags.size())
			return null;
		return flags.get(b);
	}

	public ArrayList<GridItem> getItems() {
		return items;
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public Vector2 getRandomSpawn(boolean enemy) {
		if (enemy) {
			return spawns2.get((int) (Math.random() * spawns2.size()));
		} else {
			return spawns1.get((int) (Math.random() * spawns1.size()));
		}
	}

}
