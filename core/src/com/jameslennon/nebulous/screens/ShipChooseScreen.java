package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ShipData;
import com.jameslennon.nebulous.util.StarBackground;
import com.jameslennon.nebulous.util.UserData;

public class ShipChooseScreen extends AbstractScreen {

	public ShipChooseScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		StarBackground.initialize(stage, width, height);

		Table table = new Table(skin);
		table.setFillParent(true);
		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);
		Label label = new Label("Choose Your Ship:", style);
		// label.setFontScale(2);
		table.add(label).pad(20).colspan(4).row();
		for (int i = 0; i < ShipData.ships.size(); i++) {
			ShipData sData = ShipData.ships.get(i);
			Table mini = new Table(skin);

			Image img = sData.getImage();
			img.setColor(Color.GREEN);
			// img.setSize(50, 50);

			Button button = new Button(new ButtonStyle(
					new TextureRegionDrawable(Images.getImage("Gallery")),
					new TextureRegionDrawable(Images.getImage("Gallery")),
					new TextureRegionDrawable(Images.getImage("Gallery"))));
			// button.setColor(Color.WHITE);
			// button.setSize(60, 60);
			Group group = new Group();
			group.setSize(button.getWidth(), button.getHeight());
			img.setScale(1.5f);
			img.setX(button.getWidth() / 2 - img.getWidth() / 2);
			img.setY(button.getHeight() / 2 - img.getHeight() / 2);
			img.addAction(Actions.forever(Actions.rotateBy(360, 30)));
			group.addActor(img);
			group.addActor(button);
			mini.add(group);// .padLeft(50).padRight(50);
			mini.row();
			mini.add(sData.name);

			if (sData.premium) {
				mini.setColor(1, 1, 1, .5f);
				button.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						game.setState(Nebulous.STATE_PURCHASE);
					}

				});
			} else {
				final int index = i;
				button.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						UserData.setShipType(index);
						game.setState(Nebulous.STATE_CHOOSE_GAME);
						Gdx.app.log(Nebulous.log, "Selected: " + index);
					}

				});
			}
			table.add(mini).pad(20);
			if (i == 3)
				table.row();
		}
		stage.addActor(table);

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

		stage.getRoot().setColor(1, 1, 1, 0);
		stage.addAction(Actions.fadeIn(.25f));
	}

	@Override
	public void hide() {
		super.hide();
	}

	public void back() {
		game.setState(Nebulous.STATE_MENU);
	}

}
