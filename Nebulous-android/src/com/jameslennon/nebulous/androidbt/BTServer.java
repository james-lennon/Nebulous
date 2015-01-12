package com.jameslennon.nebulous.androidbt;

import java.util.ArrayList;

import android.R.integer;
import android.bluetooth.BluetoothSocket;

import com.badlogic.gdx.Gdx;
import com.jameslennon.nebulous.BluetoothManager;
import com.jameslennon.nebulous.Nebulous;

public class BTServer {

	private ArrayList<AndroidBTPeer> peers;
	private boolean[] ids;
	private BluetoothManager m;

	public BTServer(BluetoothManager bluetoothManager) {
		peers = new ArrayList<AndroidBTPeer>();
		ids = new boolean[20];
		m = bluetoothManager;
		AndroidBTPeer s = new AndroidBTPeer();
		addPeer(s);
	}

	public void addSocket(BluetoothSocket socket) {
		Gdx.app.log(Nebulous.log, "Added Peer to server");
		AndroidBTPeer p = new AndroidBTPeer(socket, this);
		p.listen();
		// BTPeer peer = new BTPeer(data);
		// mLobby.addPeer(peer);
		// // Gdx.app.log(Nebulous.log, "Added peer " + peer);
		// ls.addMember(peer);
		// if (peer.getId() == playerId) {
		// // Gdx.app.log(Nebulous.log, "^This is me");
		// myPeer = peer;
		// }
	}

	public void addPeer(AndroidBTPeer p) {
		Gdx.app.log(Nebulous.log, "Added " + p.getName());
		peers.add(p);
		p.setId(getNextID());
		p.write(new byte[] { 3, p.getId() });
		Gdx.app.log(Nebulous.log,
				"Setting " + p.getName() + "'s id to " + p.getId());
		// m.getCallback().onAddPeer(p);
		for (AndroidBTPeer peer : peers) {
			if (peer != p) {
				p.write(peer.toByteArray());
				peer.write(p.toByteArray());
			} else {
				p.write(peer.toByteArray());
			}
		}
	}

	private byte getNextID() {
		for (int i = 0; i < ids.length; i++) {
			if (!ids[i]) {
				ids[i] = true;
				return (byte) i;
			}
		}
		return (byte) -1;
	}

	public void removePeer(AndroidBTPeer p) {
		peers.remove(p);
		ids[p.getId()] = false;
		broadcast(new byte[] { 10, p.getId() }, (byte) -1);
	}

	public void onReceive(byte[] data, AndroidBTPeer peer) {
		if (data[0] == 5) {
			peer.initialize(data);
			addPeer(peer);
		} else if (data[0] == 4) {
			broadcast(data, data[2]);
		} else if (data[0] == 8) {
			broadcast(data, data[2]);
		} else {
			broadcast(data, (byte) -1);
		}
	}

	public void broadcast(byte[] bs, byte not) {
		for (int i = 0; i < peers.size(); i++) {
			if (peers.get(i).getId() == not)
				continue;
			peers.get(i).write(bs);
		}
	}

}
