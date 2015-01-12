package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.StarBackground;
import com.jameslennon.nebulous.util.UserData;

public class ProfileScreen extends AbstractScreen {

	private final int BUTTON_WIDTH = 300, BUTTON_HEIGHT = 60;
	private Nebulous n;
	private TextField username;

	public ProfileScreen(Nebulous g) {
		super(g);
		n = g;
	}

	@Override
	public void hide() {
		super.hide();
		if (username.getText().length() > 0)
			UserData.setName(username.getText());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.clear();
		
//		List l = new List(skin);
//		Array<Actor> a = new Array<Actor>(new Actor[]{new Label("Test", skin),new Label("Test2", skin),});
//		l.setItems(a);
//		l.setTouchable(Touchable.childrenOnly);

		Table table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);
		table.add("Username:");
		username = new TextField(UserData.getName(), skin);
		table.add(username).row();
		table.add("Choose Your Ship Class").spaceBottom(50);
		table.row();

		TextButton selectLight = new TextButton("Select Light Ship", skin);
		selectLight.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// NetworkManager.sendShipType(Peer.SHIP_LIGHT);
				UserData.setShipType(Peer.SHIP_LIGHT);
				super.clicked(event, x, y);
			}

		});
		table.add(selectLight).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform()
				.spaceBottom(10);
		table.row();

		TextButton selectHeavy = new TextButton("Select Heavy Ship", skin);
		selectHeavy.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// NetworkManager.sendShipType(Peer.SHIP_HEAVY);
				UserData.setShipType(Peer.SHIP_HEAVY);
				super.clicked(event, x, y);
			}

		});
		table.add(selectHeavy).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform()
				.spaceBottom(10);
		table.row();
		TextButton selectAssault = new TextButton("Select Assault Ship", skin);
		selectAssault.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// NetworkManager.sendShipType(Peer.SHIP_ASSAULT);
				UserData.setShipType(Peer.SHIP_ASSAULT);
				super.clicked(event, x, y);
			}

		});
		table.add(selectAssault).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform()
				.spaceBottom(10);
		table.row();
//		TextButton back = new TextButton("Back", skin);
//		back.addListener(new ClickListener() {
//
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				n.setState(Nebulous.STATE_MENU);
//				super.clicked(event, x, y);
//			}
//
//		});
//		table.add(back).size(BUTTON_WIDTH / 2, BUTTON_HEIGHT).uniform()
//				.spaceBottom(10);
		StarBackground.initialize(stage, width, height);
		table.setZIndex(100);

		TextButton back = new TextButton("Back", skin);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				back();
			}

		});
		back.setSize(75,50);
		back.setPosition(5, height - back.getHeight() - 5);
		stage.addActor(back);
	}
	
	public void back(){
		n.setState(Nebulous.STATE_MENU);
	}
}
