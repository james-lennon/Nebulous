package com.jameslennon.nebulous.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.CollisionManager;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.InputManager;
import com.jameslennon.nebulous.game.grid.GridMap;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.util.ActionCam;
import com.jameslennon.nebulous.util.FlagTracker;
import com.jameslennon.nebulous.util.GameType;
import com.jameslennon.nebulous.util.MiniMap;

public class PlayScreen extends AbstractScreen {

	public static ArrayList<Body> rem;

	protected World worldInstance;
	protected Ship s;
	protected GridMap gm;
	protected InputManager im;
	protected Stage guiStage;
	protected Nebulous nebulous;
	private Label fps, ping;
	private FlagTracker ft;
	private float accum = 0;
	private long lastping;
	private ActionCam ac;
	protected MiniMap mm;
	protected LabelStyle style;

	public PlayScreen(Nebulous g) {
		super(g);
		if (Gdx.graphics.getWidth() >= Nebulous.APP_WIDTH
				&& Gdx.graphics.getHeight() >= Nebulous.APP_HEIGHT) {
			guiStage = new Stage(new ExtendViewport(Nebulous.APP_WIDTH,
					Nebulous.APP_HEIGHT));
			((ExtendViewport) stage.getViewport()).setWorldSize(
					Nebulous.APP_WIDTH / Nebulous.PIXELS_PER_METER,
					Nebulous.APP_HEIGHT / Nebulous.PIXELS_PER_METER);
		} else {
			float h = Gdx.graphics.getHeight();
			float w = h * 16 / 10;
			guiStage = new Stage(new ExtendViewport(w, h));
			((ExtendViewport) stage.getViewport()).setWorldSize(w
					/ Nebulous.PIXELS_PER_METER, h / Nebulous.PIXELS_PER_METER);
		}
		stage.getViewport().setCamera(ac = new ActionCam());
		
		ac.shakeEnabled = true;
		nebulous = g;
	}

	@Override
	public void show() {
		super.show();
		if (worldInstance != null)
			worldInstance.dispose();
		rem = new ArrayList<Body>();

		worldInstance = new World(new Vector2(), true);
		worldInstance.setContactListener(new CollisionManager());
		// rayHandler = new RayHandler(worldInstance);
		// rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 0.1f);
		// rayHandler.setCulling(true);
		// // rayHandler.setBlur(false);
		// rayHandler.setBlurNum(1);
		// Globals.setRayHandler(rayHandler);
		Globals.setSkin(skin);
		Globals.setStage(stage);
		Globals.setWorld(worldInstance);
		Globals.setGui(guiStage);
		Globals.setScreen(this);
		MiniMap.clear();
		// gm = GridMap.loadFromFile(Gdx.files.internal("data/big.map"));
		// Globals.setMap(gm);
		// if (NetworkManager.isOnline()) {
		// NetworkManager.getGame().initShips(gm, stage);
		// s = NetworkManager.getPeer().getShip();
		// } else {
		// // Vector2 sp = gm.getSpawn((byte) 0);
		// // s = new LightShip(NetworkManager.getOfflinePeer(), sp.x, sp.y);
		// s = NetworkManager.getOfflinePeer().initShip();
		// // gm.addItem(s);
		// }
		// s.setVibrate(true);
		ac.stopShake();

		lastping = System.currentTimeMillis() - 3000;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		guiStage.clear();
		guiStage.getViewport().update(width, height, true);
		im = new InputManager(guiStage, s, 0, 0, skin);
		im.resize(width, height, stage);

		gm.show(stage);
		style = new LabelStyle(Globals.getFont(), Color.WHITE);
		Label back = new Label("Back", style);
		back.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				back();
			}

		});
		// back.setSize(75, 50);
		back.setPosition(5, guiStage.getHeight() - back.getHeight() - 5);
		guiStage.addActor(back);

		fps = new Label("FPS: ", skin);
		fps.setPosition(5, back.getY() - 5 - fps.getHeight());
		guiStage.addActor(fps);
		if (GameType.getType() == GameType.online||GameType.getType()==GameType.bluetooth) {
			ping = new Label("Ping: ", skin);
			ping.setPosition(5,
					back.getY() - 10 - fps.getHeight() - ping.getHeight());
			guiStage.addActor(ping);
		}
		if (gm.getFlag(0) != null)
			ft = new FlagTracker(gm.getFlag(0), ac);

		stage.getRoot().setColor(1, 1, 1, 0);
		stage.addAction(Actions.fadeIn(.25f));
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		guiStage.act(delta);
		guiStage.draw();

		for (Body b : rem) {
			worldInstance.destroyBody(b);
		}
		rem.clear();
		float step = 1f / 60f;
		// accum += delta;
		// System.out.println(accum + "," + step);
		// while (accum >= step) {
		// System.out.print("step;");
		worldInstance.step(1f / 60f, 6, 2);
		if (delta <= step / 2) {
			worldInstance.step(1f / 60f, 6, 2);
		}
		// accum -= step;
		// }
		im.update();
		if (s.getState() != Ship.STATE_DEAD)
			ac.follow(s);
		else
			ac.stopShake();

		gm.update();
		if (ft != null)
			ft.update();
		if (mm != null)
			mm.update();
		fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		if (ping != null && System.currentTimeMillis() - lastping >= 3000) {
			long val;
			if(GameType.getType()==GameType.online){
				val = NetworkManager.getPing();
			}else {
				val = Nebulous.bluetooth.getPing();
			}
			ping.setText("Ping: " +val);
			lastping = System.currentTimeMillis();
		}
	}

	@Override
	public void dispose() {
		worldInstance.dispose();
		worldInstance = null;
		guiStage.dispose();
		super.dispose();
	}

	@Override
	public void hide() {
		super.hide();
		guiStage.clear();
	}

	@Override
	public void back() {
		nebulous.setState(Nebulous.STATE_CHOOSE_GAME);
	}

	public void message(String msg) {
		style = new LabelStyle(Globals.getFont(), Color.WHITE);
		final Label l = new Label(msg, style);
		l.setOrigin(l.getWidth() / 2, l.getHeight() / 2);
		l.setPosition(stage.getWidth() / 2 - l.getWidth() / 2,
				stage.getHeight() / 2 + l.getHeight());
		l.addAction(Actions.sequence(Actions.delay(1f), Actions.fadeOut(1f)));
		l.addAction(Actions.sequence(Actions.delay(1f),
				Actions.moveBy(0, -300, 1f), Actions.removeActor()));
		guiStage.addActor(l);
	}
}
