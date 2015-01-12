package com.jameslennon.nebulous.singleplayer;

import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.net.Peer;

public class AIShip extends Ship {
	private ShipController sc;
	private Peer peer;

	public AIShip(Peer p, float x, float y, String imgName, float acc,
			float angleAcc, float density, float damp, int health) {
		super(new Peer("",0), x, y, imgName, acc, angleAcc, density, damp, health);
		this.sc = new ShipController(this);
		peer = super.getPeer();
		peer.setId((byte) -1);
	}

	public AIShip(Peer p, float x, float y, String string, int acc,
			float angleAcc, float density, int health) {
		this(p, x, y, string, acc, angleAcc, density, .1f, health);
	}

	@Override
	public void update() {
		if (getState() != STATE_DEAD)
			sc.update();
		super.update();
	}

	@Override
	public void die() {
		super.die();
		Globals.getMiniMap().remove(this);
		super.disconnect();
	}

	@Override
	public Peer getPeer() {
		return peer;
	}

}
