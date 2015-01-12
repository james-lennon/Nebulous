package com.jameslennon.nebulous.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.StarBackground;
import com.jameslennon.nebulous.util.UserData;

public class MenuScreen extends AbstractScreen {

	public MenuScreen(Nebulous g) {
		super(g);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.clear();

		StarBackground.initialize(stage, width, height);
		Table table = new Table(skin);
		table.setFillParent(true);
		// ip = new TextField(NetworkManager.getAddress(), skin);
		// table.add(ip).size(150, 30).uniform().pad(20).colspan(1).center();

		Image name = new Image(Images.getImage("splash2")), circ = new Image(
				new Texture(Gdx.files.internal("res/splashCircle.png")));
		Group group = new Group();
		group.addActor(name);
		circ.setSize(90, 86);
		circ.setPosition(315, 20);
		circ.setOrigin(43f, 43);
		circ.addAction(Actions.forever(Actions.rotateBy(-360, 20)));
		group.addActor(circ);
		group.setPosition(stage.getWidth() / 2 - name.getWidth() / 2,
				stage.getHeight() - name.getHeight() - 30);
		stage.addActor(group);
		table.add().padBottom(150).row();

		// table.add(name).colspan(2).padBottom(150).row();
		LabelStyle labelStyle = new LabelStyle(Globals.getFont(), Color.WHITE);
		Label play = new Label("Play", labelStyle);
		play.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// NetworkManager.setIP(ip.getText());
				next(new Runnable() {
					@Override
					public void run() {
						game.setState(Nebulous.STATE_CHOOSE_SHIP);
					}
				});
				// game.setState(Nebulous.STATE_CONNECTING);
				super.clicked(event, x, y);
			}

		});
		table.add(play).spaceBottom(20);
		table.row();
		Label leaderboard = new Label("Leaderboard", labelStyle);
		leaderboard.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Nebulous.social.showLeaderboard();
				super.clicked(event, x, y);
			}

		});
		table.add(leaderboard).spaceBottom(10);
		table.row();

		Label buy = new Label("Unlock All Ships", labelStyle);
		buy.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setState(Nebulous.STATE_PURCHASE);
				super.clicked(event, x, y);
			}

		});
		if (!UserData.isPro()) {
			table.add(buy).spaceBottom(10).row();
		}

		stage.addActor(table);

		

		Label credits = new Label("More Credits", skin);
		credits.setPosition(stage.getWidth() - credits.getWidth(),0);
		stage.addActor(credits);
		credits.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				game.setState(Nebulous.STATE_CREDITS);
			}
			
		});
		
		Label credit = new Label("By James Lennon", skin);
		credit.setPosition(stage.getWidth() - credit.getWidth(), credits.getHeight()+2);
		stage.addActor(credit);

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
		Gdx.app.exit();
	}

}
