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

public class PurchaseScreen extends AbstractScreen {

	public PurchaseScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.clear();

		StarBackground.initialize(stage, width, height);
		Table table = new Table(skin);
		table.setFillParent(true);

		LabelStyle labelStyle = new LabelStyle(Globals.getFont(), Color.WHITE);

		Label title = new Label("Pay $0.99 to unlock all ships!", labelStyle);
		table.add(title).padBottom(10).row();
		table.add(
				"Gain access to all 8 ships to use in survival\n mode or playing against your friends!")
				.padBottom(20).row();

		Label buy = new Label("Buy Now", labelStyle);
		buy.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				// NetworkManager.setIP(ip.getText());
				Nebulous.iap.goPro();
				game.setState(Nebulous.STATE_PURCHASE_LOAD);
				// game.setState(Nebulous.STATE_CONNECTING);
			}

		});
		table.add(buy).spaceBottom(20);
		table.row();
		Label restore = new Label("Restore Purchase", labelStyle);
		restore.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				// NetworkManager.setIP(ip.getText());
				Nebulous.iap.restore();
				game.setState(Nebulous.STATE_PURCHASE_LOAD);
			}

		});
		table.add(restore).spaceBottom(20);
		table.row();

		Label later = new Label("Back", labelStyle);
		later.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				back();
				super.clicked(event, x, y);
			}

		});
		table.add(later).spaceBottom(10);
		table.row();
		stage.addActor(table);

		stage.getRoot().setColor(1, 1, 1, 0);
		stage.addAction(Actions.fadeIn(.5f));
	}

	@Override
	public void render(float delta) {
		super.render(delta);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void back() {
		game.setState(Nebulous.STATE_MENU);
	}

}
