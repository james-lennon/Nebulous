package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.GridMap;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.MiniMap;

public class OnlinePlayScreen extends PlayScreen {

	private Label timeLabel, kills, deaths;
	private Vector2 vel;
	private Vector2 pos;
	private long end;

	public OnlinePlayScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void show() {
		super.show();

		gm = GridMap.loadFromFile(Gdx.files.internal("data/"
				+ NetworkManager.getMap() + ".map"));
		Globals.setMap(gm);
		
		NetworkManager.getLobby().initShips(gm, stage);
		s = NetworkManager.getPeer().getShip();
		s.setVibrate(true);

		end = System.currentTimeMillis() + 180000;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		// NetworkManager.getLobby().initShips(gm, stage);
		// s = NetworkManager.getPeer().getShip();
		// s.setVibrate(true);
		NetworkManager.getLobby().showShips(stage);
		// NetworkManager.listenForUpdates();
		// NetworkManager.listenOnTCP();

		Table t = new Table(skin);

		timeLabel = new Label("Time Left", style);
		// timeLabel.setFontScale(2);
		t.add(timeLabel).colspan(2).row();
		kills = new Label("Kills: 0", skin);
		deaths = new Label("Deaths: 0", skin);
		t.add(kills).pad(5).colspan(2).row();
		t.add(deaths).pad(5).colspan(2);
		t.setX(guiStage.getCamera().viewportWidth / 2);
		t.setY(guiStage.getCamera().viewportHeight - t.getHeight() - 70);
		guiStage.addActor(t);
		mm = new MiniMap(guiStage);
		Globals.setMiniMap(mm);
	}

	@Override
	public void resume() {
		super.resume();
		NetworkManager.getLobby().resumeShips();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		NetworkManager.updatePlayers();
		pos = s.getBody().getPosition();
		vel = s.getBody().getLinearVelocity();
		// renderer.render(worldInstance, stage.getCamera().combined);
		NetworkManager.sendPosToGame(pos.x, pos.y, vel.x, vel.y, s.getBody()
				.getAngle(), s.getBody().getAngularVelocity());
		int diff = (int) ((end - System.currentTimeMillis()) / 1000);

		timeLabel.setText(String.format("%d:%02d", diff / 60, diff % 60));
		Peer peer = NetworkManager.getPeer();
		kills.setText("Kills: " + peer.getKills());
		deaths.setText("Deaths: " + peer.getDeaths());
		if (diff <= 0) {
			// end game
			nebulous.setState(Nebulous.STATE_GAME_OVER);
		}
	}

	@Override
	public void hide() {
		super.hide();
		NetworkManager.quitGame();
	}

}
