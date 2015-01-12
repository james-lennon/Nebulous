package com.jameslennon.nebulous.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.GridMap;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.game.shots.EMP;
import com.jameslennon.nebulous.game.shots.GravityProjectile;
import com.jameslennon.nebulous.game.shots.LaserProjectile;
import com.jameslennon.nebulous.game.shots.MineProjectile;
import com.jameslennon.nebulous.game.shots.Missile;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.game.shots.SmallLaser;
import com.jameslennon.nebulous.game.shots.SniperShot;
import com.jameslennon.nebulous.game.shots.WaveProjectile;
import com.jameslennon.nebulous.util.GameType;

public class Lobby {

	private final byte id;
	private String name;
	private TreeMap<Integer, Peer> members;
	private ArrayList<SavedProjectile> savedProjectiles;

	public Lobby(byte n) {
		members = new TreeMap<Integer, Peer>();
		id = n;
		savedProjectiles = new ArrayList<Lobby.SavedProjectile>();
	}

	public void addPeer(Peer p) {
		members.put((int) p.getId(), p);
	}

	public TreeMap<Integer, Peer> getMembers() {
		return members;
	}

	public String getName() {
		return name;
	}

	public void send(byte[] array) {
	}

	public void initShips(GridMap gm, Stage stage) {
		Set<Integer> keys = members.keySet();
		for (int k : keys) {
			Peer peer = members.get(k);
			peer.initShip();
		}
	}

	public void showShips(Stage stage) {
		Set<Integer> keys = members.keySet();
		for (int k : keys) {
			members.get(k).getShip().show(stage);
		}
	}

	public void resumeShips() {
		Set<Integer> keys = members.keySet();
		for (int k : keys) {
			members.get(k).getShip().updateHealth();
		}
	}

	public void receiveData(byte[] data) {
		int header = data[0];
		if (header != 4) {
			Gdx.app.log(Nebulous.log, "Invalid header received!");
			return;
		}
		int id = data[2];
		if (GameType.getType() == GameType.online) {
			if (members.containsKey(id) && id != NetworkManager.getPlayerId())
				members.get(id).setUpdateData(data);
		} else if (GameType.getType() == GameType.bluetooth) {
			if (members.containsKey(id) && id != Nebulous.bluetooth.getPlayerId())
				members.get(id).setUpdateData(data);
		}
	}

	public void update() {
		Set<Integer> keySet = members.keySet();
		for (int k : keySet) {
			Peer peer = members.get(k);
			peer.updateShip();
		}
		for (int i = 0; i < savedProjectiles.size(); i++) {
			SavedProjectile sp = savedProjectiles.get(i);
			Projectile p = null;
			if (sp.type == Projectile.TYPE_LASER)
				p = new LaserProjectile(sp.x, sp.y, sp.rot, 0, sp.id);
			else if (sp.type == Projectile.TYPE_WAVE)
				p = new WaveProjectile(sp.x, sp.y, sp.rot, 0, sp.id);
			else if (sp.type == Projectile.TYPE_MISSILE)
				p = new Missile(sp.x, sp.y, sp.rot, 0, sp.id);
			else if (sp.type == Projectile.TYPE_EMP)
				p = new EMP(sp.x, sp.y, sp.rot, 0, sp.id);
			else if (sp.type == Projectile.TYPE_MINE)
				p = new MineProjectile(sp.x, sp.y, sp.rot, 0, sp.id);
			else if (sp.type == Projectile.TYPE_GRAVITY)
				p = new GravityProjectile(sp.x, sp.y, sp.rot, 0, sp.id);
			else if (sp.type == Projectile.TYPE_SNIPER)
				p = new SniperShot(sp.x, sp.y, sp.rot, 0, sp.id);
			else if (sp.type == Projectile.TYPE_SMALL_LASER)
				p = new SmallLaser(sp.x, sp.y, sp.rot, 0, sp.id);

			// ProjectileManager.getInstance().addProjectile(p);
			Globals.getMap().addItem(p);
			p.show();
		}
		savedProjectiles.clear();
	}

	public byte getId() {
		return id;
	}

	public void addShot(byte[] data) {
		SavedProjectile sp = new SavedProjectile();
		sp.id = ByteBuffer.wrap(data, 2, 1).get();
		sp.x = ByteBuffer.wrap(data, 3, 4).getFloat();
		sp.y = ByteBuffer.wrap(data, 7, 4).getFloat();
		sp.rot = ByteBuffer.wrap(data, 11, 4).getFloat();
		sp.type = ByteBuffer.wrap(data, 15, 1).get();
		savedProjectiles.add(sp);
	}

	private class SavedProjectile {
		float x, y, rot;
		byte type, id;
	}

	public void removePeer(int b) {
		Peer peer = members.get(b);
		if (peer == null)
			return;
		peer.disconnect();
	}

	public void updateHealth(byte[] buff) {
		byte id = buff[1];
		if (members.containsKey((int) id)) {
			final Ship ship = members.get((int) id).getShip();
			final int h = Math.min(ByteBuffer.wrap(buff, 2, 4).getInt(),
					ship.getHealth());
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					ship.setHealth(h);
				}
			});
		}
	}

	public void reset() {
		members = new TreeMap<Integer, Peer>();
	}
}
