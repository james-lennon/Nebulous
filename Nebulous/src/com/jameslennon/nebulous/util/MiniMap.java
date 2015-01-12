package com.jameslennon.nebulous.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.Tile;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.singleplayer.SinglePlayerManager;

public class MiniMap {
	private static ArrayList<Vector2> blocks;

	public static void clear() {
		blocks = new ArrayList<Vector2>();
	}

	public static void addBlock(Vector2 v) {
		blocks.add(v);
	}

	private Image backg;
	private HashMap<Integer, Image> map;
	private TreeMap<Integer, Peer> peers;
	private HashMap<Ship, Image> omap;
	private float mapw, maph;

	public MiniMap(Stage s) {
		backg = new Image(Images.getImage("MiniMap"));
		map = new HashMap<Integer, Image>();
		omap = new HashMap<Ship, Image>();
		backg.setPosition(s.getWidth() - 5 - backg.getWidth(), s.getHeight()
				- 5 - backg.getHeight());
		s.addActor(backg);
		mapw = Globals.getMap().getWidth() * Tile.WIDTH;
		maph = Globals.getMap().getHeight() * Tile.WIDTH;
		for (Vector2 pos : blocks) {
			Image i = new Image(Images.getImage("MiniBlock"));
			i.setWidth(Tile.WIDTH * Nebulous.PIXELS_PER_METER
					* backg.getWidth() / (mapw * Nebulous.PIXELS_PER_METER)
					+ .5f);
			i.setHeight(Tile.WIDTH * Nebulous.PIXELS_PER_METER
					* backg.getHeight() / (maph * Nebulous.PIXELS_PER_METER)
					+ .5f);
			i.setX(backg.getX() + pos.x * backg.getWidth() / mapw);
			i.setY(backg.getY() + pos.y * backg.getHeight() / maph
					+ i.getHeight());
			s.addActor(i);
		}
		/*
		 * for (GridItem gi : Globals.getMap().getItems()) { if (!(gi instanceof
		 * Wormhole || gi instanceof Mine)) continue; Vector2 pos =
		 * gi.getBody().getPosition(); Image i = new
		 * Image(gi.getImage().getDrawable()); i.setWidth(Tile.WIDTH *
		 * Nebulous.PIXELS_PER_METER backg.getWidth() / (mapw *
		 * Nebulous.PIXELS_PER_METER) + 2f); i.setHeight(Tile.WIDTH *
		 * Nebulous.PIXELS_PER_METER backg.getHeight() / (maph *
		 * Nebulous.PIXELS_PER_METER) + 2f); i.setX(backg.getX() + pos.x *
		 * backg.getWidth() / mapw - i.getWidth() / 2); i.setY(backg.getY() +
		 * pos.y * backg.getHeight() / maph); s.addActor(i); }
		 */
		if (GameType.getType() == GameType.online) {
			peers = NetworkManager.getLobby().getMembers();
			for (Entry<Integer, Peer> e : peers.entrySet()) {
				map.put(e.getKey(), null);
			}
		} else if (GameType.getType() == GameType.bluetooth) {
			peers = Nebulous.bluetooth.getLobby().getMembers();
			for (Entry<Integer, Peer> e : peers.entrySet()) {
				map.put(e.getKey(), null);
			}
		}
	}

	public void update() {
		if (GameType.getType() == GameType.online) {
			for (Entry<Integer, Peer> e : peers.entrySet()) {
				Image i = map.get(e.getKey());
				if (e.getValue().isDisconnected()) {
					if (i != null) {
						i.remove();
					}
					map.remove(e.getKey());
					continue;
				}
				if (i == null) {
					if (e.getValue().getId() == NetworkManager.getPlayerId()) {
						i = new Image(Images.getImage("MapFriend"));
					} else {
						i = new Image(Images.getImage("MapEnemy"));
					}
					map.put(e.getKey(), i);
					Globals.getGui().addActor(i);
				}

				if (e.getValue().getShip() == null)
					continue;
				Vector2 pos = e.getValue().getShip().getBody().getPosition();
				try {
					i.setPosition(
							i.getWidth() + backg.getX() + pos.x
									* backg.getWidth() / mapw, i.getHeight()
									+ backg.getY() + pos.y * backg.getHeight()
									/ maph);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (GameType.getType() == GameType.singleplayer) {
			for (Ship s : SinglePlayerManager.getShips()) {
				Image i = omap.get(s);
				if (i == null) {
					if (s.isPlayer())
						i = new Image(Images.getImage("MapFriend"));
					else {
						i = new Image(Images.getImage("MapEnemy"));
					}
					Globals.getGui().addActor(i);
					omap.put(s, i);
				}
				Vector2 pos = s.getBody().getPosition();
				i.setPosition(backg.getX() + pos.x * backg.getWidth() / mapw,
						backg.getY() + pos.y * backg.getHeight() / maph);
			}
		} else if (GameType.getType() == GameType.bluetooth) {
			for (Entry<Integer, Peer> e : peers.entrySet()) {
				Image i = map.get(e.getKey());
				if (e.getValue().isDisconnected()) {
					if (i != null) {
						i.remove();
					}
					map.remove(e.getKey());
					continue;
				}
				if (i == null) {
					if (e.getValue().getId() == Nebulous.bluetooth
							.getPlayerId()) {
						i = new Image(Images.getImage("MapFriend"));
					} else {
						i = new Image(Images.getImage("MapEnemy"));
					}
					map.put(e.getKey(), i);
					Globals.getGui().addActor(i);
				}

				if (e.getValue().getShip() == null)
					continue;
				Vector2 pos = e.getValue().getShip().getBody().getPosition();
				try {
					i.setPosition(backg.getX() + pos.x * backg.getWidth()
							/ mapw, backg.getY() + pos.y * backg.getHeight()
							/ maph);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void remove(Ship s) {
		if (GameType.getType() == GameType.singleplayer) {
			SinglePlayerManager.getShips().remove(s);
			Image i = omap.remove(s);
			i.remove();
		} else if (GameType.getType() == GameType.online) {
			Image i = omap.remove(s);
			i.remove();
		} else if (GameType.getType() == GameType.bluetooth) {
			Image i = omap.remove(s);
			i.remove();
		}
	}

}
