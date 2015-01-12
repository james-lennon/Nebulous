package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.singleplayer.SinglePlayerManager;
import com.jameslennon.nebulous.util.StarBackground;
import com.sun.rowset.internal.Row;

public class SingleGameOver extends AbstractScreen {

	private Nebulous n;
	private Label messageLabel;
	private long starttime;
	private float curscore, rate;
	private Label kills, time;
	private Label again, back;

	public SingleGameOver(Nebulous g) {
		super(g);
		n = g;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		StarBackground.initialize(stage, width, height);
		Table t = new Table(skin);
		t.setFillParent(true);

		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);

		messageLabel = new Label("Score: ", style);
		t.add(messageLabel).colspan(2).row();
		kills = new Label("Kills: " + SinglePlayerManager.getKills(), skin);
		kills.addAction(Actions.fadeOut(0));
		t.add(kills).pad(5).colspan(2).row();
		time = new Label("Time Survived: "
				+ String.format("%d mins %02d secs",
						SinglePlayerManager.getSeconds() / 60,
						SinglePlayerManager.getSeconds() % 60), skin);
		time.addAction(Actions.fadeOut(0));
		t.add(time).colspan(2).pad(5).padBottom(100).row();
		again = new Label("Play Again", style);
		again.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				n.setState(Nebulous.STATE_PLAY_SINGLE);
			}

		});
		again.addAction(Actions.fadeOut(0));
		t.add(again).pad(20);
		back = new Label("Back", style);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				back();
			}

		});
		back.addAction(Actions.fadeOut(0));
		t.add(back).pad(20);
		stage.addActor(t);

		// back.setPosition(5, stage.getHeight() - back.getHeight() - 5);
		// stage.addActor(back);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (System.currentTimeMillis() - starttime < 3000) {
			messageLabel.setText("Score: " + (int) curscore);
			curscore += rate * delta * 1000;
		} else {
			messageLabel.setText("Score: " + SinglePlayerManager.getScore());
		}
		if (System.currentTimeMillis() - starttime > 3000) {
			kills.addAction(Actions.fadeIn(1));
			time.addAction(Actions.fadeIn(1));
		}
		if (System.currentTimeMillis() - starttime > 5000) {
			again.addAction(Actions.fadeIn(1));
			back.addAction(Actions.fadeIn(1));
		}
		// if (end - System.currentTimeMillis() <= 0)
		// n.setState(Nebulous.STATE_LOBBY);
	}

	@Override
	public void show() {
		super.show();
		if (Nebulous.social != null)
			Nebulous.social.singlePlayerScore(SinglePlayerManager.getScore());
		starttime = System.currentTimeMillis();
		curscore = 0;
		rate = (float) SinglePlayerManager.getScore() / 3000;
	}

	public void back() {
		n.setState(Nebulous.STATE_CHOOSE_GAME);
	}

}
