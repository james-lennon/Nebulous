package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.GridMap;
import com.jameslennon.nebulous.singleplayer.SinglePlayerManager;
import com.jameslennon.nebulous.util.MiniMap;

public class SinglePlayScreen extends PlayScreen {

	private Label score;
	private Label lives;

	public SinglePlayScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void show() {
		super.show();
		int map = (int) (Math.random() * 9) + 1;
		//map = 10;
		gm = GridMap.loadFromFile(Gdx.files.internal("data/" + map + ".map"));
		Globals.setMap(gm);

		SinglePlayerManager.init();
		s = SinglePlayerManager.instantiatePlayerShip(gm);
		s.setVibrate(true);
		s.setPlayer(true);
		SinglePlayerManager.getShips().add(s);
		Globals.getMap().addItem(s);
		SinglePlayerManager.start();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		s.show(stage);

		Table t = new Table(skin);

		score = new Label("Score: 0", style);
		lives = new Label("Deaths: 0", style);
		t.add(score).pad(1).colspan(2).row();
		t.add(lives).pad(1).colspan(2);
		t.setX(guiStage.getCamera().viewportWidth / 2);
		t.setY(guiStage.getCamera().viewportHeight - t.getHeight() - 70);
		guiStage.addActor(t);

		mm = new MiniMap(guiStage);
		Globals.setMiniMap(mm);
	}

	@Override
	public void resume() {
		super.resume();
		s.updateHealth();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		score.setText("Score: " + SinglePlayerManager.getScore());
		lives.setText("Lives: " + SinglePlayerManager.getLives());
		SinglePlayerManager.update();
	}

	@Override
	public void hide() {
		super.hide();
	}

}
