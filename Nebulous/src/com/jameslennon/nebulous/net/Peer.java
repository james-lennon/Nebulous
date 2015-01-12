package com.jameslennon.nebulous.net;

import java.nio.ByteBuffer;

import com.badlogic.gdx.math.Vector2;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.util.ShipData;

/**
 * Created by jameslennon on 3/5/14.
 */
public class Peer implements Comparable<Peer> {

	public static final byte SHIP_LIGHT = 0, SHIP_HEAVY = 1, SHIP_ASSAULT = 2,
			SHIP_TACTICAL = 3, SHIP_SNIPER = 4;

	private String name;
	private Ship ship;
	private byte id, team;
	public float x, y, dx, dy, rot, drot;
	public boolean shouldUpdate;
	private int shipType, kills, deaths;

	private boolean disconnected;

	public String getName() {
		return name;
	}

	public Peer() {

	}

	public Peer(byte[] data) {
		initialize(data);
	}

	public void initialize(byte[] data) {
		int ulen = data[1];
		try {
			char[] buff = new char[ulen];
			for (int i = 0; i < ulen; i++) {
				buff[i] = (char) data[i + 2];
			}
			name = new String(buff);
			// setId(data[2 + ulen]);
			setShipType(data[2 + ulen]);
			if (data.length > 3 + ulen)
				setId(data[3 + ulen]);
			// team = (byte) (id / (NetworkManager.getGameSize() / 2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Peer(String str, int type) {
		name = str;
		id = 0;
		team = 0;
		shipType = type;
	}

	public void setUpdateData(byte[] data) {
		if (ship == null) {
//			System.out.println("ship null");
			return;
		}
		x = ByteBuffer.wrap(data, 3, 4).getFloat();
		y = ByteBuffer.wrap(data, 7, 4).getFloat();
		dx = ByteBuffer.wrap(data, 11, 4).getFloat();
		dy = ByteBuffer.wrap(data, 15, 4).getFloat();
		rot = ByteBuffer.wrap(data, 19, 4).getFloat();
		drot = ByteBuffer.wrap(data, 23, 4).getFloat();
		shouldUpdate = true;
//		System.out.println("updating " + id + " to " + x + "," + y);
	}

	public void updateShip() {
		if (shouldUpdate) {
			ship.setState(x, y, dx, dy, rot, drot);
			shouldUpdate = false;
		}
	}

	public String toString() {
		return "[ Name:" + name + " ]";
	}

	public void setShip(Ship s) {
		ship = s;
	}

	public void setId(byte n) {
		this.id = n;
	}

	public byte getId() {
		return id;
	}

	public Ship getShip() {
		return ship;
	}

	public int getShipType() {
		return shipType;
	}

	public void setShipType(int type) {
		shipType = type;
	}

	public void sendHealth(int health) {
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.put((byte) 12);
		buffer.put(id);
		buffer.putInt(2, health);
		NetworkManager.sendToServerTCP(buffer.array());
	}

	public Ship initShip() {
		Vector2 spawn = Globals.getMap().getSpawn(getId());
//		System.out.println(getId() + "'s ship spawning at " + spawn);
		Ship s = ShipData.ships.get(getShipType()).instantiate(this, spawn.x,
				spawn.y);

		// if (getShipType() == Peer.SHIP_LIGHT) {
		// s = new LightShip(this, spawn.x, spawn.y);
		// } else if (getShipType() == Peer.SHIP_HEAVY) {
		// s = new HeavyShip(this, spawn.x, spawn.y);
		// } else if (getShipType() == Peer.SHIP_ASSAULT) {
		// s = new AssaultShip(this, spawn.x, spawn.y);
		// } else {
		// s = new LightShip(this, spawn.x, spawn.y);
		// Gdx.app.log(Nebulous.log, "Unrecognized Ship type");
		// }

		setShip(s);
		Globals.getMap().addItem(s);
		return s;
	}

	public byte getTeam() {
		return team;
	}

	public void setName(String s) {
		name = s;
	}

	public int getKills() {
		return kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void addKill() {
		kills++;
	}

	public void addDeath() {
		deaths++;
	}

	@Override
	public int compareTo(Peer o) {
		int c = o.getKills() - getKills();
		if (c == 0)
			c = getDeaths() - o.getDeaths();
		return c;
	}

	public void disconnect() {
		disconnected = true;
		if (getShip() != null)
			getShip().disconnect();
	}

	public boolean isDisconnected() {
		return disconnected;
	}

	public void setKills(int k) {
		kills = k;
	}

	public void setDeaths(int d) {
		deaths = d;
	}

	public byte[] toByteArray() {
		String name = getName();
		byte[] buff = new byte[name.length() + 4];
		buff[0] = 6;
		buff[1] = (byte) name.length();
		System.arraycopy(name.getBytes(), 0, buff, 2, name.length());
		int i = 2 + name.length();
		buff[i++] = (byte) getShipType();
		buff[i++] = (byte) getId();
		return buff;
	}
}
