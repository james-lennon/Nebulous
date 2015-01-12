package com.jameslennon.nebulous.singleplayer;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.GridMap;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.ShipData;
import com.jameslennon.nebulous.util.UserData;

public class SinglePlayerManager {

	private static ArrayList<Ship> ships;
	private static int kills, lives, seconds;
	private static Ship playerShip;
	private static Random rand;
	private static int score;
	private static long starttime, lastupdate;
	private static int numupdates;
	private static long lastscore;

	public static void init() {
		ships = new ArrayList<Ship>();
		kills = 0;
		lives = 5;
		rand = new Random();
		score = 0;
		seconds = 0;
	}

	public static ArrayList<Ship> getShips() {
		return ships;
	}

	public static Ship instantiatePlayerShip(GridMap gm) {
		Vector2 spawn = gm.getRandomSpawn(false);
		Ship s = ShipData.ships.get(UserData.getShipType()).instantiate(
				new Peer(UserData.getName(), 0), spawn.x, spawn.y);
		s.setPlayer(true);
		s.spawn();
		playerShip = s;
		return s;
	}

	public static void addKill() {
		kills++;
		score += 50;
	}

	public static void removeLife() {
		lives--;
	}

	public static int getKills() {
		return kills;
	}

	public static int getLives() {
		return lives;
	}

	public static int getScore() {
		return score;
	}

	public static int getSeconds() {
		return seconds;
	}

	public static void addShip(int tier) {
		Ship s = null;
		Vector2 spawn = Globals.getMap().getRandomSpawn(true);
		if (tier == 4) {
			s = new InfantryAI(null, spawn.x, spawn.y);
		} else if (tier == 3) {
			int v = rand.nextInt(2);
			if (v == 0)
				s = new LightAI(null, spawn.x, spawn.y);
			else
				s = new HeavyAI(null, spawn.x, spawn.y);
		} else if (tier == 2) {
			int v = rand.nextInt(3);
			if (v == 0)
				s = new EngineerAI(null, spawn.x, spawn.y);
			else if (v == 1)
				s = new AssaultAI(null, spawn.x, spawn.y);
			else {
				s = new SaboteurAI(null, spawn.x, spawn.y);
			}
		}

		Globals.getMap().addItem(s);
		s.show(Globals.getStage());
		ships.add(s);
	}

	public static Ship getPlayerShip() {
		return playerShip;
	}

	public static void start() {
		lastupdate = System.currentTimeMillis() - 13000;
		numupdates = 0;
		addShip(4);
		addShip(4);
		addShip(4);
	}

	public static void update() {
		if (System.currentTimeMillis() - lastscore >= 1000) {
			lastscore = System.currentTimeMillis();
			seconds++;
			score++;
		}
		if (System.currentTimeMillis() - lastupdate >= 7000) {
			if (numupdates < 2) {
				addShip(4);
			} else if (numupdates < 4) {
				addShip(4);
				addShip(4);
			} else if (numupdates < 7) {
				addShip(4);
				addShip(3);
			} else if (numupdates < 10) {
				addShip(2);
				addShip(4);
			} else if (numupdates < 15) {
				addShip(2);
				addShip(3);
			} else if (numupdates < 17) {
				addShip(4);
				addShip(4);
				addShip(4);
			} else {
				addShip(2);
				addShip(2);
			}
			numupdates++;
			lastupdate = System.currentTimeMillis();
		}
	}

	public static void endGame() {
		Globals.getNebulous().setState(Nebulous.STATE_SINGLE_OVER);
	}

}
