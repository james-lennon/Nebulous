package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.util.StarBackground;
import com.jameslennon.nebulous.util.UserData;

public class LoginScreen extends AbstractScreen {
	private Nebulous n;
	private TextField nameField;

	public LoginScreen(Nebulous g) {
		super(g);
		n = g;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		StarBackground.initialize(stage, width, height);
		Table table = new Table(skin);
		// table.setFillParent(true);
		Label label = new Label("Enter Username:", skin);
		label.setScale(2);
		table.add(label).pad(10);
		nameField = new TextField("", skin);
		nameField.setScale(2.0f);
		table.add(nameField).pad(5).size(300, 60);
		TextButton tb = new TextButton("Next", skin);
		tb.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				next();
			}

		});
		table.add(tb).size(75,50);
		table.setX(stage.getCamera().viewportWidth / 2);
		table.setY(2 * stage.getCamera().viewportHeight / 3);
		stage.addActor(table);
	}

	protected void next() {
		UserData.setName(nameField.getText());
		n.setState(Nebulous.STATE_MENU);
	}

	@Override
	public void show() {
		super.show();
	}
	
	public void back(){
		Gdx.app.exit();
	}

}
