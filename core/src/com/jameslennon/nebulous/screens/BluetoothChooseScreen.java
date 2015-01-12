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
//public class BluetoothChooseScreen extends AbstractScreen {
//
//	private Nebulous n;
//	private final int BUTTON_WIDTH = 300, BUTTON_HEIGHT = 60;
//	private Table rooms;
//	private ArrayList<Host> hosts;
//
//	public BluetoothChooseScreen(Nebulous g) {
//		super(g);
//		n = g;
//		hosts = new ArrayList<BluetoothChooseScreen.Host>();
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
//		stage.addActor(t);
//		rooms = new Table(skin);
//		rooms.top();
//		ScrollPane pane = new ScrollPane(rooms, skin);
//		t.add(pane).height(300).width(300).row();
//		
//		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);
//
//		TextButton host = new TextButton("Host Game", skin);
//		host.addListener(new ClickListener() {
//
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				super.clicked(event, x, y);
//				n.setState(Nebulous.STATE_BT_HOST);
//			}
//
//		});
//
//		t.add(host).size(BUTTON_WIDTH / 2, BUTTON_HEIGHT).uniform().pad(10);
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
//
//		setupTable();
//
//		stage.getRoot().setColor(1, 1, 1, 0);
//		stage.addAction(Actions.fadeIn(.25f));
//	}
//
//	public synchronized void addRoom(String name, int id) {
//		hosts.add(new Host(name, id));
//		setupTable();
//	}
//
//	public synchronized void removeRoom(int id) {
//		for (int i = 0; i < hosts.size(); i++) {
//			if (hosts.get(i).id == id) {
//				hosts.remove(i);
//				break;
//			}
//		}
//		setupTable();
//	}
//
//	@Override
//	public void show() {
//		super.show();
//		// BT stuff
//		Nebulous.bluetooth.setCallback(new BluetoothCallback() {
//
//			@Override
//			public void onFindHost(String name, int id) {
//				addRoom(name, id);
//			}
//
//			@Override
//			public void onError() {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onAddPeer(Peer p) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onPeerExit(int id) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onHostExit(int id) {
//				removeRoom(id);
//			}
//
//			@Override
//			public void onGameStart() {
//			}
//		});
//		Nebulous.bluetooth.setup();
//		hosts.clear();
//		Nebulous.bluetooth.findGames();
//	}
//
//	private void setupTable() {
//		rooms.clear();
//		for (int i = 0; i < hosts.size(); i++) {
//			final Host host = hosts.get(i);
//			Label l = new Label(host.name + "'s Room", skin);
//			l.addListener(new ClickListener() {
//
//				@Override
//				public void touchUp(InputEvent arg0, float arg1, float arg2,
//						int arg3, int arg4) {
//					super.touchUp(arg0, arg1, arg2, arg3, arg4);
//					Gdx.app.log(Nebulous.log, "room " + host.id + " pressed");
//					game.setState(Nebulous.STATE_BT_LOBBY);
//					Nebulous.bluetooth.joinHost(host.id);
//				}
//
//			});
//			l.setFontScale(1.5f);
//			rooms.add(l).pad(10);
//		}
//	}
//
//	public void back() {
//		n.setState(Nebulous.STATE_CHOOSE_GAME);
//	}
//
//	@Override
//	public void hide() {
//		super.hide();
//	}
//
//	private class Host {
//		String name;
//		int id;
//
//		public Host(String a, int b) {
//			name = a;
//			id = b;
//		}
//	}
//}
