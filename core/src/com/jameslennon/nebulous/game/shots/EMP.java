package com.jameslennon.nebulous.game.shots;

import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.Tile;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.singleplayer.SinglePlayerManager;
import com.jameslennon.nebulous.util.GameType;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;

public class EMP extends Projectile {

	private ParticleEffectActor pea;

	public EMP(float x, float y, float ang, float hf, byte b) {
		super(x, y, ang, 40, "EMP", hf, 8, 8, 10, b);
		ParticleEffect peEffect = new ParticleEffect();
		peEffect.load(Gdx.files.internal("data/EMPTrail.p"), Images.getAtlas());
		pea = new ParticleEffectActor(peEffect, x * Nebulous.PIXELS_PER_METER,
				y * Nebulous.PIXELS_PER_METER);
		Globals.getStage().addActor(pea);
	}

	@Override
	public void update() {
		super.update();
		pea.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
				* Nebulous.PIXELS_PER_METER);
	}

	@Override
	public void die() {
		super.die();
		float r = 1.5f * Tile.WIDTH;
		if (GameType.getType() == GameType.online) {
			TreeMap<Integer, Peer> map = NetworkManager.getLobby().getMembers();
			Set<Integer> st = map.keySet();
			for (int x : st) {
				Ship s = map.get(x).getShip();
				if (s.getBody().getPosition().dst(getBody().getPosition()) <= r
						&& !s.isPlayer()) {
					s.stun();
				}
			}
		} else if (GameType.getType() == GameType.singleplayer) {
			for (Ship s : SinglePlayerManager.getShips()) {
				if (s.getBody().getPosition().dst(getBody().getPosition()) <= r) {
					s.stun();
				}
			}
		} else if (GameType.getType() == GameType.bluetooth) {
			TreeMap<Integer, Peer> map = Nebulous.bluetooth.getLobby()
					.getMembers();
			Set<Integer> st = map.keySet();
			for (int x : st) {
				Ship s = map.get(x).getShip();
				if (s.getBody().getPosition().dst(getBody().getPosition()) <= r
						&& !s.isPlayer()) {
					s.stun();
				}
			}
		}

		pea.getEffect().getEmitters().get(0).allowCompletion();
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal("data/Electricity.p"), Images.getAtlas());
		Globals.getStage().addActor(
				new ParticleEffectActor(effect, getX()
						* Nebulous.PIXELS_PER_METER, getY()
						* Nebulous.PIXELS_PER_METER));
		Globals.getMap().doExplosion(getX(), getY(), 3, 2, false, true,
				getOwner());
	}
}
