package com.jameslennon.nebulous.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.NetworkManager.LobbyCallback;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.StarBackground;

public class LobbyScreen extends AbstractScreen {

	private Label messageLabel;
	private Thread connThread;
	private boolean leave;
	private Table members;
	private ArrayList<Peer> peers;
	private LobbyCallback lc = new LobbyCallback() {

		@Override
		public void start() {
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					leave = false;
					game.setState(Nebulous.STATE_PLAY_ONLINE);
				}
			});
		}

		@Override
		public void removeMember(Peer peer) {
			peers.remove(peer);
//			initMembers();
		}

		@Override
		public void onFindGame() {
			setMessage("Waiting for players...");
		}

		@Override
		public void addMember(Peer peer) {
			peers.add(peer);
//			initMembers();
		}
	};

	public LobbyScreen(Nebulous g) {
		super(g);
		leave = true;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		StarBackground.initialize(stage, width, height);
		messageLabel = new Label("Finding Game...", skin);
		stage.addActor(messageLabel);

		TextButton back = new TextButton("Back", skin);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				back();
			}

		});
		back.setSize(75, 50);
		back.setPosition(5, stage.getHeight() - back.getHeight() - 5);
		stage.addActor(back);

		members = new Table(skin);
//		initMembers();
		stage.addActor(members);
	}

	private void initMembers() {
		if (peers == null)
			return;
		members.clear();
		members.setWidth(100);
		members.setHeight(600);
		for (int i = 0; i < peers.size(); i++) {
			members.add(peers.get(i).getName()).row();
		}
		members.setX(stage.getWidth() / 2 - members.getWidth() / 2);
		members.setY(stage.getHeight() / 2 - members.getHeight() / 2);
	}

	@Override
	public void show() {
		super.show();
		leave = true;
		messageLabel = new Label("Finding Game...", skin);
		connThread = new Thread(new Runnable() {
			@Override
			public void run() {
				peers = new ArrayList<Peer>();
				Gdx.app.log(Nebulous.log, "Starting thread");
				NetworkManager.joinLobby(lc);

			}
		});
		connThread.start();
	}

	@Override
	public void hide() {
		super.hide();
		NetworkManager.stopJoining();
		if (leave) {
			NetworkManager.quitGame();
		}
		leave = true;
	}

	public void setMessage(String s) {
		messageLabel.setText(s);
	}

	public void back() {
		game.setState(Nebulous.STATE_MENU);
	}

}
