package com.jameslennon.nebulous.net;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;

public class BTPeerManager extends Lobby {

	public BTPeerManager() {
		super((byte) 0);
	}

	public void addPeer(Peer p) {
		super.addPeer(p);
		Gdx.app.log(Nebulous.log, "adding peer: " + p.getName());
//		Nebulous.bluetooth.getCallback().onAddPeer(p);
	}

	public Peer getPeer(int id) {
		return getMembers().get(id);
	}

	public void onReceive(byte[] buff) {
		if (buff[0] == 5) {
			addPeer(new Peer(buff));
		} else if (buff[0] == 2) {
//			Nebulous.bluetooth.getCallback().onGameStart();
		} else if (buff[0] == 3) {
			Gdx.app.log(Nebulous.log, "Setting id to " + buff[1]);
			Nebulous.bluetooth.setPlayerId(buff[1]);
		} else if (buff[0] == 10) {
			removePeer(buff[1]);
//			Nebulous.bluetooth.getCallback().onPeerExit(buff[1]);
		} else if (buff[0] == 12) {
			updateHealth(buff);
		} else if (buff[0] == 13) {
			receiveScores(buff);
		} else if (buff[0] == 16) {
			receiveKill(buff);
		} else if (buff[0] == 11) {
			Peer peer = getMembers().get((int) buff[1]);
			peer.addDeath();
		} else if (buff[0] == 4) {
			receiveData(buff);
		} else if (buff[0] == 8) {
			addShot(buff);
		} else {
			Gdx.app.log(Nebulous.log, "Received erroneous header: " + buff[0]
					+ "\n" + Arrays.toString(buff));
		}
	}

	private void receiveKill(byte[] buff) {
		Peer p1 = getMembers().get((int) buff[1]), p2 = getMembers().get(
				(int) buff[2]);
		p1.addKill();
		p2.addDeath();
		if (buff[1] == Nebulous.bluetooth.getPlayerId())
			Globals.getScreen().message("Killed " + p2.getName() + "!");
	}

	private void receiveScores(byte[] buff) {

	}

}
