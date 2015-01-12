package com.jameslennon.nebulous.net;

import java.nio.ByteBuffer;

import sun.security.util.Length;

import com.badlogic.gdx.Gdx;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
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
import com.jameslennon.nebulous.util.UserData;

public abstract class BluetoothClient {

	protected byte playerid;
	private BluetoothCallback bc;
	protected boolean started;
	private int map;
	private long ping, lastping;
	private Lobby mLobby;
	private long length;

	public BluetoothClient() {
		mLobby = new Lobby((byte) 0);
	}

	public abstract void init();

	public void setCallback(BluetoothCallback bc) {
		this.bc = bc;
	}

	public abstract void dispose();

	public BluetoothCallback getCallback() {
		return bc;
	}

	public abstract void sendToServer(byte[] data, boolean reliable);

	public abstract void sendToClients(byte[] data, boolean reliable,
			boolean toHost);

	public void update() {
		getLobby().update();
	}

	public abstract void startGame();

	public Lobby getLobby() {
		return mLobby;
	}

	public byte getPlayerId() {
		return playerid;
	}

	public void setPlayerId(byte b) {
		playerid = b;
	}

	public void sendPosToGame(float x, float y, float dx, float dy, float rot,
			float drot) {
		ByteBuffer bf = ByteBuffer.allocate(27);
		bf.put((byte) 4);
		bf.put((byte) 0);
		bf.put(playerid);
		bf.putFloat(x);
		bf.putFloat(y);
		bf.putFloat(dx);
		bf.putFloat(dy);
		bf.putFloat(rot);
		bf.putFloat(drot);
		byte[] data = bf.array();
		sendToServer(data, false);
	}

	public abstract void disconnect();

	public void sendKill(byte id) {
		sendToServer(new byte[] { 16, id, playerid }, true);
	}

	public void sendDie() {
		sendToServer(new byte[] { 11, playerid }, true);
	}

	public void sendShot(Projectile p) {
		ByteBuffer bf = ByteBuffer.allocate(16);
		bf.put((byte) 8);
		bf.put((byte) 0);
		bf.put(playerid);
		bf.putFloat(p.getBody().getPosition().x);
		bf.putFloat(p.getBody().getPosition().y);
		bf.putFloat(p.getBody().getAngle());
		byte type = 0;
		if (p instanceof LaserProjectile) {
			type = Projectile.TYPE_LASER;
		} else if (p instanceof WaveProjectile) {
			type = Projectile.TYPE_WAVE;
		} else if (p instanceof Missile) {
			type = Projectile.TYPE_MISSILE;
		} else if (p instanceof EMP) {
			type = Projectile.TYPE_EMP;
		} else if (p instanceof MineProjectile) {
			type = Projectile.TYPE_MINE;
		} else if (p instanceof GravityProjectile) {
			type = Projectile.TYPE_GRAVITY;
		} else if (p instanceof SniperShot) {
			type = Projectile.TYPE_SNIPER;
		} else if (p instanceof SmallLaser)
			type = Projectile.TYPE_SMALL_LASER;
		bf.put(type);
		byte[] data = bf.array();
		sendToServer(data, false);
	}

	public void sendHealth(int h) {
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.put((byte) 12);
		buffer.put(playerid);
		buffer.putInt(2, h);
		sendToServer(buffer.array(), true);
	}

	// public abstract void start();

	public abstract void startServer();

	public abstract void startClient();

	public void sendInfoToServer() {
		String name = UserData.getName();
		byte[] buff = new byte[UserData.getName().length() + 3];
		buff[0] = 5;
		buff[1] = (byte) name.length();
		System.arraycopy(name.getBytes(), 0, buff, 2, name.length());
		int i = 2 + name.length();
		buff[i++] = (byte) UserData.getShipType();
		sendToServer(buff, true);
	}

	public void getData(byte[] data) {
		int h = data[0];
		if (h == NetworkManager.UPDATEPOS) {
			// System.out.println("got pos from " + data[2]);
			if (data[2] != playerid) {
				getLobby().receiveData(data);
				// System.out.println("updating pos");
			} else {
				updatePing();
				// System.out.println("ignoring pos from " + data[2]);
				// System.out.println("updating ping");
			}
		} else if (h == NetworkManager.ADDSHOT) {
			if (data[2] != playerid)
				getLobby().addShot(data);
			else {
				updatePing();
			}
		} else if (h == 10) {
			// getLobby().removePeer(data[1]);
			getLobby().getMembers().get((int) data[1]).disconnect();
			// if (p == null)
			// return;
			// Ship s = p.getShip();
			// Globals.getMiniMap().remove(s);
//			s.disconnect();
		} else if (h == NetworkManager.UPDATEHEALTH) {
			getLobby().updateHealth(data);
		} else if (h == NetworkManager.KILL) {
			receiveKill(data);
		} else if (h == NetworkManager.DIE) {
			Peer peer = getLobby().getMembers().get((int) data[1]);
			peer.addDeath();
		} else if (h == NetworkManager.ADDPEER) {
			final Peer peer = new Peer(data);
			if (mLobby.getMembers().containsKey((int) peer.getId()))
				return;
			mLobby.addPeer(peer);
			Gdx.app.log(Nebulous.log,
					"Got peer " + peer.getName() + "," + peer.getShipType()
							+ "," + peer.getId());
			if (peer.getName().equals(UserData.getName())) {
				playerid = peer.getId();
				Gdx.app.log(Nebulous.log, "My id is " + playerid);
			}
			if (started) {
				peer.initShip();
				peer.getShip().show(Globals.getStage());
			}
		} else if (h == NetworkManager.START) {
			if (started)
				return;
			Gdx.app.log(Nebulous.log, "Received Start game...");
			setMap(data[2]);
			setLength(ByteBuffer.wrap(data, 3, 8).getLong());
			started = true;
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					Globals.getNebulous().setState(Nebulous.STATE_BT_PLAY);
				}
			});
		} else {
			Gdx.app.log(Nebulous.log, "Received erroneous header: " + h);
		}
	}

	private void setLength(long l) {
		length = l;
	}

	public long getLength() {
		return length;
	}

	private void setMap(byte b) {
		map = b;
	}

	public int getMap() {
		return map;
	}

	private void updatePing() {
		long now = System.currentTimeMillis();
		ping = now - lastping;
		lastping = now;
	}

	private void receiveKill(byte[] buff) {
		// Gdx.app.log(Nebulous.log, "got kill");
		Peer peer = getLobby().getMembers().get((int) buff[1]);
		if (peer == null)
			return;
		peer.addKill();
		if (buff[1] == getPlayerId())
			Globals.getScreen().message(
					"Killed "
							+ getLobby().getMembers().get((int) buff[2])
									.getName() + "!");
		getLobby().getMembers().get((int) buff[2]).addDeath();
	}

	public void reset() {
		if (getLobby() != null)
			getLobby().reset();
	}

	public long getPing() {
		return ping;
	}

	public void endGame() {
		started = false;
	}

}
