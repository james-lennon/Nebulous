package com.jameslennon.nebulous.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.util.StarBackground;

public class GameOverScreen extends AbstractScreen {

	private Nebulous n;
	private long end;

	public GameOverScreen(Nebulous g) {
		super(g);
		n = g;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		StarBackground.initialize(stage, width, height);
		Table t = new Table(skin);
		t.setFillParent(true);
		ArrayList<Peer> list = new ArrayList<Peer>();
		Set<Integer> st = NetworkManager.getLobby().getMembers().keySet();
		for (int x : st) {
			Peer p = NetworkManager.getLobby().getMembers().get(x);
			list.add(p);
		}
		Collections.sort(list);
		int place = list.indexOf(NetworkManager.getPeer()) + 1;
		LabelStyle style = new LabelStyle(Globals.getFont(), Color.WHITE);
		Label messageLabel = new Label("_ Place", style);
		messageLabel.setFontScale(2);
		t.add(messageLabel).colspan(3).row();
		for (Peer p : list) {
			t.add(p.getName()).pad(5);
			t.add("Kills: " + p.getKills()).pad(5);
			t.add("Deaths: " + p.getDeaths()).pad(5).row();
		}
		messageLabel.setColor(Color.GREEN);
		if (place == 1) {
			messageLabel.setText(place + "st Place");
		} else if (place == 2) {
			messageLabel.setText(place + "nd Place");
		} else if (place == 3) {
			messageLabel.setText(place + "rd Place");
		} else {
			messageLabel.setText(place + "th Place");
			messageLabel.setColor(Color.RED);
		}

		stage.addActor(t);

		end = System.currentTimeMillis() + 10000;

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
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (end - System.currentTimeMillis() <= 0) {
			n.setState(Nebulous.STATE_CHOOSE_GAME);
			if (Nebulous.social != null)
				Nebulous.social.startMatchmaking();
		}
	}

	@Override
	public void show() {
		super.show();
	}

	public void back() {
		n.setState(Nebulous.STATE_MENU);
	}

}
