//package com.jameslennon.nebulous.screens;
//
//import java.util.ArrayList;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.scenes.scene2d.InputEvent;
//import com.badlogic.gdx.scenes.scene2d.actions.Actions;
//import com.badlogic.gdx.scenes.scene2d.ui.Label;
//import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
//import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
//import com.badlogic.gdx.scenes.scene2d.ui.Table;
//import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
//import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//import com.jameslennon.nebulous.Nebulous;
//import com.jameslennon.nebulous.game.Globals;
//import com.jameslennon.nebulous.net.BluetoothCallback;
//import com.jameslennon.nebulous.net.Peer;
//import com.jameslennon.nebulous.util.StarBackground;
//
//public class BTHostScreen extends AbstractScreen {
//
//	private Nebulous n;
//	private final int BUTTON_WIDTH = 300, BUTTON_HEIGHT = 60;
//	private Table players;
//	private ArrayList<String> pList;
//
//	public BTHostScreen(Nebulous g) {
//		super(g);
//		n = g;
//		pList = new ArrayList<String>();
//	}
//
//	@Override
//	public void resize(int width, int height) {
//		super.resize(width, height);
//		stage.clear();
//
//		StarBackground.initialize(stage, width, height);
//		Table t = new Table(skin);
//		t.setFillParent(true);
//
//		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);
//		Label label = new Label("Players:", style);
////		label.setFontScale(2);
//		t.add(label).row();
//		stage.addActor(t);
//		players = new Table(skin);
//		players.top();
//		ScrollPane pane = new ScrollPane(players, skin);
//		t.add(pane).height(300).width(300).row();
//		TextButton start = new TextButton("Start Game", skin);
//		start.addListener(new ClickListener() {
//
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				super.clicked(event, x, y);
//				Nebulous.bluetooth.startGame();
//			}
//
//		});
//
//		t.add(start).size(BUTTON_WIDTH / 2, BUTTON_HEIGHT).uniform().pad(10);
//
//		Label back = new Label("Back", style);
//		back.addListener(new ClickListener() {
//
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				super.clicked(event, x, y);
//				back();
//			}
//
//		});
////		back.setSize(75, 50);
//		back.setPosition(5, stage.getHeight() - back.getHeight() - 5);
//		stage.addActor(back);
//		setupTable();
//		
//		stage.getRoot().setColor(1, 1, 1, 0);
//		stage.addAction(Actions.fadeIn(.25f));
//	}
//
//	@Override
//	public void show() {
//		super.show();
//		Gdx.app.log(Nebulous.log, "setting callback");
//		Nebulous.bluetooth.setCallback(new BluetoothCallback() {
//
//			@Override
//			public void onFindHost(String name, int id) {
//			}
//
//			@Override
//			public void onError() {
//			}
//
//			@Override
//			public void onAddPeer(Peer p) {
//				Gdx.app.log(Nebulous.log, "got peer " + p.getName());
//				addPlayer(p.getName());
//			}
//
//			@Override
//			public void onPeerExit(int id) {
//				removePlayer(Nebulous.bluetooth.getLobby().getMembers().get(id)
//						.getName());
//			}
//
//			@Override
//			public void onHostExit(int id) {
//			}
//
//			@Override
//			public void onGameStart() {
//				Gdx.app.postRunnable(new Runnable() {
//
//					@Override
//					public void run() {
//						n.setState(Nebulous.STATE_BT_PLAY);
//					}
//				});
//			}
//		});
//		pList.clear();
//		Nebulous.bluetooth.hostGame();
//	}
//
//	@Override
//	public void hide() {
//		super.hide();
//		Nebulous.bluetooth.stopHosting();
//	}
//
//	public synchronized void removePlayer(String name) {
//		pList.remove(name);
//		setupTable();
//	}
//
//	public synchronized void addPlayer(String name) {
//		pList.add(name);
//		setupTable();
//	}
//
//	private void setupTable() {
//		if (players == null)
//			return;
//		players.clear();
//		for (int i = 0; i < pList.size(); i++) {
//			players.add(pList.get(i)).pad(10).row();
//		}
//	}
//
//	public void back() {
//		n.setState(Nebulous.STATE_BLUETOOTH_SEARCH);
//	}
//}
