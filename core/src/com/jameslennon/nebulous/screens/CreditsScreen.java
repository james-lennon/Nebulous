package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.StarBackground;

public class CreditsScreen extends AbstractScreen {

	public CreditsScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		StarBackground.initialize(stage, width, height);

		Table table = new Table(skin);
		table.setFillParent(true);

		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);
		Label label = new Label("Credits:", style);
		table.add(label).pad(60).colspan(4).row();

		Label dd = new Label("Design & Development: James Lennon", style);
		table.add(dd).pad(20).colspan(4).row();
		Label g = new Label("Graphics: Philippe Chabot & Daniel Cook", style);
		table.add(g).pad(20).colspan(4).row();
//		Label m = new Label("Music: Alex Lennon", style);
//		table.add(m).pad(20).colspan(4).row();

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
