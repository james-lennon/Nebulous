package com.jameslennon.nebulous;

import java.util.ArrayDeque;
import java.util.ArrayList;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.bindings.apple.gamekit.GKAchievement;
import org.robovm.bindings.apple.gamekit.GKLeaderboard;
import org.robovm.bindings.apple.gamekit.GKLocalPlayer;
import org.robovm.bindings.gamecenter.GameCenterListener;
import org.robovm.bindings.gamecenter.GameCenterManager;
import org.robovm.objc.annotation.Method;

import com.badlogic.gdx.Gdx;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.GameType;
import com.jameslennon.nebulous.util.SocialManager;
import com.jameslennon.nebulous.util.UserData;

public class IosSocialManager extends NSObject implements SocialManager,
		GameKitHelperDelegate {

	private GameCenterManager gm;
	private GameKitHelper gkhelper;
	private boolean host, started;
	private byte currID = 0;
	private ArrayDeque<NSData> msgqueue;
	private int size;
	private boolean invited;

	@Override
	public void init() {
		gkhelper = new GameKitHelper();
		gkhelper.setDelegate(this);
		gm = new GameCenterManager(UIApplication.getSharedApplication()
				.getKeyWindow(), new GameCenterListener() {
			@Override
			public void playerLoginFailed() {
				System.out.println("playerLoginFailed");
			}

			@Override
			public void playerLoginCompleted() {
				System.out.println("playerLoginCompleted");
				gkhelper.setup();
				UserData.setName(GKLocalPlayer.getLocalPlayer().getAlias());
				gm.loadLeaderboards();
			}

			@Override
			public void achievementReportCompleted() {
				System.out.println("achievementReportCompleted");
			}

			@Override
			public void achievementReportFailed() {
				System.out.println("achievementReportFailed");
			}

			@Override
			public void achievementsLoadCompleted(
					ArrayList<GKAchievement> achievements) {
				System.out.println("achievementsLoadCompleted: "
						+ achievements.size());
			}

			@Override
			public void achievementsLoadFailed() {
				System.out.println("achievementsLoadFailed");
			}

			@Override
			public void achievementsResetCompleted() {
				System.out.println("achievementsResetCompleted");
			}

			@Override
			public void achievementsResetFailed() {
				System.out.println("achievementsResetFailed");
			}

			@Override
			public void scoreReportCompleted() {
				System.out.println("scoreReportCompleted");
			}

			@Override
			public void scoreReportFailed() {
				System.out.println("scoreReportFailed");
			}

			@Override
			public void leaderboardsLoadCompleted(
					ArrayList<GKLeaderboard> scores) {
				System.out.println("scoresLoadCompleted: " + scores.size());
			}

			@Override
			public void leaderboardsLoadFailed() {
				System.out.println("scoresLoadFailed");
			}
		});
		gm.login();
	}

	@Override
	public void showLeaderboard() {
		gm.showLeaderboardsView();
	}

	@Override
	public void finish() {

	}

	@Override
	public void singlePlayerScore(long score) {
		gm.reportScore("NebulousHighScoreLeaderboard", score);
	}

	@Override
	public void onAchievement(String name) {
		gm.reportAchievement(name);
	}

	@Override
	public void startMatchmaking() {
		gkhelper.findMatchWithMinPlayers(2, 4);

	}

	@Override
	public void checkInvite() {
		if (invited) {
			GameType.setType(GameType.online);
			invited = false;
			gkhelper.joinInvite();
		}
	}

	@Override
	public void disconnect() {
		gkhelper.disconnect();
	}

	private void handleData(NSData data) {
		byte[] buff = data.getBytes();
		// System.out.println(Arrays.toString(buff));
		if (host) {
			if (buff[0] == 5) {
				Peer p = new Peer(buff);
				p.setId(currID++);
				gkhelper.sendDataToClients(new NSData(p.toByteArray()), true,
						true);
				if (currID == size) {
					byte map = (byte) (Math.random() * 9 + 1);
					gkhelper.sendDataToClients(new NSData(new byte[] {
							NetworkManager.START, (byte) size, map }), true,
							true);
				}
			} else if (buff[0] == NetworkManager.UPDATEPOS) {
//				System.out.println("sending pos from " + buff[2]);
				gkhelper.sendDataToClients(data, false, false);
			} else if (buff[0] == NetworkManager.UPDATEHEALTH) {
				gkhelper.sendDataToClients(data, true, false);
			} else if (buff[0] == NetworkManager.ADDSHOT) {
				gkhelper.sendDataToClients(data, false, false);
			} else if (buff[0] == NetworkManager.KILL) {
				gkhelper.sendDataToClients(data, true, false);
			} else if (buff[0] == NetworkManager.DIE) {
				gkhelper.sendDataToClients(data, true, false);
			}
		}
		NetworkManager.getData(data.getBytes());
	}

	@Override
	@Method(selector = "onReceiveData:")
	public void onReceive(NSData data) {
		// System.out.println("received, started == " + started);
		if (!started) {
			msgqueue.add(data);
			return;
		}
		handleData(data);
	}

	@Override
	@Method(selector = "matchStartedAsHost:")
	public void matchStarted(boolean isHost) {
		started = true;
		System.out.println("Host == " + isHost);
		host = isHost;

		while (!msgqueue.isEmpty()) {
			NSData d = msgqueue.poll();
			handleData(d);
		}

		String name = UserData.getName();
		byte[] buff = new byte[UserData.getName().length() + 3];
		buff[0] = 5;
		buff[1] = (byte) name.length();
		System.arraycopy(name.getBytes(), 0, buff, 2, name.length());
		int i = 2 + name.length();
		buff[i++] = (byte) UserData.getShipType();
		gkhelper.sendDataToHost(new NSData(buff), true);
		// Globals.getNebulous().setState(Nebulous.STATE_PLAY_ONLINE);
	}

	@Override
	@Method(selector = "foundMatchWithSize:")
	public void foundMatch(int size) {
		Gdx.app.log(Nebulous.log, "size: " + size);
		this.size = size;
		currID = 0;
		started = false;
		msgqueue = new ArrayDeque<NSData>();
		Globals.getNebulous().setState(Nebulous.STATE_START_GAME);
	}

	@Override
	public void send(byte[] data, boolean b) {
		gkhelper.sendDataToHost(new NSData(data), b);
	}

	@Override
	@Method(selector = "onMatchInvite")
	public void onMatchInvite() {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				Globals.getNebulous().setState(Nebulous.STATE_CHOOSE_SHIP);
			}
		});
		invited = true;
	}

}
