package com.jameslennon.nebulous;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.net.BluetoothClient;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.screens.BTOverScreen;
import com.jameslennon.nebulous.screens.BluetoothPlayScreen;
import com.jameslennon.nebulous.screens.BluetoothSearchScreen;
import com.jameslennon.nebulous.screens.ChooseGameScreen;
import com.jameslennon.nebulous.screens.ConnectingScreen;
import com.jameslennon.nebulous.screens.CreditsScreen;
import com.jameslennon.nebulous.screens.GameOverScreen;
import com.jameslennon.nebulous.screens.LobbyScreen;
import com.jameslennon.nebulous.screens.LoginScreen;
import com.jameslennon.nebulous.screens.MenuScreen;
import com.jameslennon.nebulous.screens.OnlinePlayScreen;
import com.jameslennon.nebulous.screens.ProfileScreen;
import com.jameslennon.nebulous.screens.PurchaseLoadScreen;
import com.jameslennon.nebulous.screens.PurchaseScreen;
import com.jameslennon.nebulous.screens.ShipChooseScreen;
import com.jameslennon.nebulous.screens.SingleGameOver;
import com.jameslennon.nebulous.screens.SinglePlayScreen;
import com.jameslennon.nebulous.screens.SplashScreen;
import com.jameslennon.nebulous.screens.StartingGameScreen;
import com.jameslennon.nebulous.util.IAPClient;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ShipData;
import com.jameslennon.nebulous.util.SocialManager;
import com.jameslennon.nebulous.util.UserData;

/*
 * Q's
 */

public class Nebulous extends Game {
	public static final String log = "Nebulous";
	public static final float PIXELS_PER_METER = 30;
	public static BluetoothClient bluetooth;
	public static SocialManager social;
	public static IAPClient iap;

	private Screen splash, conn, menu, singleplay, lobby, profile, login,
			choose, over, onlineplay, btsearch, pload, btlobby, singleover,
			btplay, btover, chooseship, startGame, purchase, credits;

	public final static int STATE_SPLASH = 0, STATE_CONNECTING = 1,
			STATE_MENU = 2, STATE_PLAY_ONLINE = 3, STATE_LOBBY = 4,
			STATE_PROFILE = 5, STATE_LOGIN = 6, STATE_CHOOSE_GAME = 7,
			STATE_GAME_OVER = 8, STATE_CREDITS = 9, STATE_PLAY_SINGLE = 10,
			STATE_BLUETOOTH_SEARCH = 11, STATE_PURCHASE_LOAD = 12,
			STATE_SINGLE_OVER = 13, STATE_BT_LOBBY = 14, STATE_BT_PLAY = 15,
			STATE_BT_OVER = 16, STATE_CHOOSE_SHIP = 17, STATE_START_GAME = 18,
			STATE_PURCHASE = 19;
	public static final int APP_WIDTH = 800, APP_HEIGHT = 500;

	@Override
	public void create() {
		NetworkManager.init(this);
		UserData.load();
		Images.load();
		ShipData.init();
		if (Nebulous.bluetooth != null)
			Nebulous.bluetooth.init();
		if (Nebulous.social != null)
			Nebulous.social.init();
		setState(STATE_SPLASH);
		Globals.setNebulous(this);
	}

	public void setState(int state) {
		Screen s = null;
		switch (state) {
		case STATE_SPLASH:
			if (splash == null) {
				splash = new SplashScreen(this);
			}
			s = splash;
			break;
		case STATE_CONNECTING:
			if (conn == null) {
				conn = new ConnectingScreen(this);
			}
			s = conn;
			break;
		case STATE_MENU:
			if (menu == null) {
				menu = new MenuScreen(this);
			}
			s = menu;
			break;
		case STATE_PLAY_ONLINE:
			if (onlineplay == null) {
				onlineplay = new OnlinePlayScreen(this);
			}
			s = onlineplay;
			break;
		case STATE_PLAY_SINGLE:
			if (singleplay == null) {
				singleplay = new SinglePlayScreen(this);
			}
			s = singleplay;
			break;
		case STATE_LOBBY:
			if (lobby == null) {
				lobby = new LobbyScreen(this);
			}
			s = lobby;
			break;
		case STATE_PROFILE:
			if (profile == null) {
				profile = new ProfileScreen(this);
			}
			s = profile;
			break;
		case STATE_LOGIN:
			if (login == null)
				login = new LoginScreen(this);
			s = login;
			break;
		case STATE_CHOOSE_GAME:
			if (choose == null)
				choose = new ChooseGameScreen(this);
			s = choose;
			break;
		case STATE_GAME_OVER:
			if (over == null)
				over = new GameOverScreen(this);
			s = over;
			break;
		case STATE_BLUETOOTH_SEARCH:
			if (btsearch == null)
				btsearch = new BluetoothSearchScreen(this);
			s = btsearch;
			break;
		case STATE_PURCHASE_LOAD:
			if (pload == null)
				pload = new PurchaseLoadScreen(this);
			s = pload;
			break;
//		case STATE_BT_LOBBY:
//			if (btlobby == null)
//				btlobby = new BTLobbyScreen(this);
//			s = btlobby;
//			break;
		case STATE_SINGLE_OVER:
			if (singleover == null)
				singleover = new SingleGameOver(this);
			s = singleover;
			break;
		case STATE_BT_PLAY:
			if (btplay == null)
				btplay = new BluetoothPlayScreen(this);
			s = btplay;
			break;
		case STATE_BT_OVER:
			if (btover == null)
				btover = new BTOverScreen(this);
			s = btover;
			break;
		case STATE_CHOOSE_SHIP:
			if (chooseship == null)
				chooseship = new ShipChooseScreen(this);
			s = chooseship;
			break;
		case STATE_START_GAME:
			if (startGame == null)
				startGame = new StartingGameScreen(this);
			s = startGame;
			break;
		case STATE_PURCHASE:
			if (purchase == null)
				purchase = new PurchaseScreen(this);
			s = purchase;
			break;
		case STATE_CREDITS:
			if (credits == null)
				credits = new CreditsScreen(this);
			s = credits;
			break;
		default:
			break;
		}
		setScreen(s);
	}

	@Override
	public void dispose() {
		super.dispose();
		Images.dispose();
		NetworkManager.dispose();
		UserData.save();
		if (Nebulous.bluetooth != null)
			Nebulous.bluetooth.dispose();
		Gdx.app.log(log, "Disposing application...");
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		Gdx.app.log(Nebulous.log, "Resizing application to: " + width + " x "
				+ height);
	}

	@Override
	public void pause() {
		super.pause();
		// Gdx.app.log(log, "Pausing application...");
	}

	@Override
	public void resume() {
		super.resume();
		// Gdx.app.log(log, "Resuming application...");
	}
}
