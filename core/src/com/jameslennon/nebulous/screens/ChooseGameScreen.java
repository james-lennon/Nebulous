package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.GameType;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.StarBackground;

public class ChooseGameScreen extends AbstractScreen {

	private Nebulous n;

	public ChooseGameScreen(Nebulous g) {
		super(g);
		n = g;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.clear();

		StarBackground.initialize(stage, width, height);

		Table t = new Table(skin);
		t.setFillParent(true);
		stage.addActor(t);

		Table mini = new Table(skin);
		Button online = new Button(new ButtonStyle(new TextureRegionDrawable(
				Images.getImage("online")), null, null));
		online.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				GameType.setType(GameType.online);
				Nebulous.social.startMatchmaking();
			}

		});
		mini.add(online).size(250, 250);
		mini.row();
		mini.add("Online Multiplayer");
		t.add(mini).pad(15);
		t.add(new Image(Images.getImage("Separator"))).height(200);

		mini = new Table(skin);
		Button local = new Button(new ButtonStyle(new TextureRegionDrawable(
				Images.getImage("local")), null, null));
		local.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				GameType.setType(GameType.bluetooth);
				game.setState(Nebulous.STATE_BLUETOOTH_SEARCH);
			}

		});
		mini.add(local).size(250, 250).row();
		mini.add("Local Multiplayer");
		t.add(mini).pad(15);
		t.add(new Image(Images.getImage("Separator"))).height(200);

		mini = new Table(skin);
		Button single = new Button(new ButtonStyle(new TextureRegionDrawable(
				Images.getImage("single")), null, null));
		single.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				GameType.setType(GameType.singleplayer);
				game.setState(Nebulous.STATE_PLAY_SINGLE);
			}

		});
		mini.add(single).size(250, 250).row();
		mini.add("Singleplayer");
		t.add(mini).pad(15);
		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);
		Label back = new Label("Back", style);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				back();
			}

		});
		// back.setSize(75, 50);
		back.setPosition(5, stage.getHeight() - back.getHeight() - 5);
		stage.addActor(back);
		if (Nebulous.social != null)
			Nebulous.social.checkInvite();

		stage.getRoot().setColor(1, 1, 1, 0);
		stage.addAction(Actions.fadeIn(.25f));
	}

	public void back() {
		n.setState(Nebulous.STATE_CHOOSE_SHIP);
	}
}
