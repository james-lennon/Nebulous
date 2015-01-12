package com.jameslennon.nebulous.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.jameslennon.nebulous.game.ships.Ship;
import com.jameslennon.nebulous.util.Images;

public class InputManager extends InputAdapter {
	private Stage gui;
	private Ship ship;
	private Touchpad touchpad, firePad;
	private float w, h;
	private boolean mobile;
	private boolean keyup, keydown, keyleft, keyright, fire;
	private long lastShot;
	private boolean mousedown;
	private boolean mousemoved;
	private float mouseang;

	public InputManager(Stage g, Ship s, int width, int height, Skin skin) {
		gui = g;
		ship = s;
		w = width;
		h = height;
		mobile = Gdx.app.getType() == ApplicationType.Android
				|| Gdx.app.getType() == ApplicationType.iOS;
		// mobile = !mobile;
		Gdx.input.setInputProcessor(new InputMultiplexer(gui, this));
	}

	@Override
	public boolean keyDown(int keycode) {
		gui.keyDown(keycode);
		switch (keycode) {
		case Keys.UP:
		case Keys.W:
			keyup = true;
			break;
		case Keys.DOWN:
		case Keys.S:
			keydown = true;
			break;
		case Keys.RIGHT:
		case Keys.D:
			keyright = true;
			break;
		case Keys.LEFT:
		case Keys.A:
			keyleft = true;
			break;
		case Keys.SPACE:
			fire = true;
			break;
		default:
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		gui.keyUp(keycode);
		switch (keycode) {
		case Keys.UP:
		case Keys.W:
			keyup = false;
			break;
		case Keys.DOWN:
		case Keys.S:
			keydown = false;
			break;
		case Keys.RIGHT:
		case Keys.D:
			keyright = false;
			break;
		case Keys.LEFT:
		case Keys.A:
			keyleft = false;
			break;
		case Keys.SPACE:
			fire = false;
			break;
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!gui.touchDown(screenX, screenY, pointer, button)) {
			if (!mobile) {
				fire = true;
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!gui.touchUp(screenX, screenY, pointer, button)) {
			if (!mobile) {
				fire = false;
			}
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		gui.touchDragged(screenX, screenY, pointer);
		if (mobile && screenX < w / 2) {
			// fakeTouchDownEvent.setStageX(screenX);
			// fakeTouchDownEvent.setStageY(screenY);
			// touchpad.fire(fakeTouchDownEvent);
		} else if (!mobile) {
			float ang = MathUtils.atan2(screenY - h / 2, screenX - w / 2);
			ship.setRotation(-ang - MathUtils.PI / 2);
		}
		return true;
	}

	public void update() {
		if (mobile) {
			float dx = touchpad.getKnobPercentX(), dy = touchpad
					.getKnobPercentY();
			if (!(dx != dx || dy != dy || (dx == 0 && dy == 0))) {
				double dist = Math.sqrt(dx * dx + dy * dy);
				if (dist > .75) {
					ship.thrust(dx, dy);
				}
			} else {
				ship.removeThrust();
			}
			fire = firePad.isTouched();
			float fdx = firePad.getKnobPercentX(), fdy = firePad
					.getKnobPercentY();
			if (fire && Math.abs(fdx) >= .5 || Math.abs(fdy) >= .5) {
				float ang = MathUtils.atan2(fdy, fdx) - MathUtils.PI / 2;
				ship.setRotation(ang);
			} else if (touchpad.isTouched()) {
				float ang = MathUtils.atan2(dy, dx) - MathUtils.PI / 2;
				ship.setRotation(ang);
			}

		} else {
			float dx = 0, dy = 0;
			if (keyup)
				dy = 1;
			if (keydown)
				dy = -1;
			if (keyright)
				dx = 1;
			if (keyleft)
				dx = -1;
			if (mousedown) {
				dx = (float) (MathUtils.cos(ship.getAngle() + MathUtils.PI / 2) * Math
						.sqrt(2));
				dy = (float) (MathUtils.sin(ship.getAngle() + MathUtils.PI / 2) * Math
						.sqrt(2));
			}
			// if (mousemoved) {
			// ship.setRotation(-mouseang - MathUtils.PI / 2);
			// mousemoved = false;
			// }
			ship.setRotation(-mouseang - MathUtils.PI / 2);

			if (dx == 0 && dy == 0)
				ship.removeThrust();
			else {
				ship.thrust(dx, dy);
				// if (keypressed) {
				// float ang = MathUtils.atan2(dy, dx) - MathUtils.PI / 2;
				// ship.setRotation(ang);
				// keypressed = false;
				// }
			}
		}
		if (fire && System.currentTimeMillis() - lastShot > ship.getWaitTime()) {
			ship.shoot();
			lastShot = System.currentTimeMillis();
			if (mobile)
				fire = false;
		}
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouseang = MathUtils.atan2(screenY - h / 2, screenX - w / 2);
		// ship.setRotation(-ang - MathUtils.PI / 2);
		mousemoved = true;
		return true;
	}

	public void resize(int width, int height, Stage s) {
		if (mobile) {
			// fakeTouchDownEvent = new InputEvent();
			// fakeTouchDownEvent.setType(Type.touchDown);
			// fakeTouchUpEvent = new InputEvent();
			// fakeTouchUpEvent.setType(Type.touchUp);
			if (touchpad != null) {
				touchpad.remove();
				firePad.remove();
			}
			TouchpadStyle ts = new TouchpadStyle(new TextureRegionDrawable(
					Images.getImage("TouchpadBackground")),
					new TextureRegionDrawable(Images.getImage("TouchpadKnob")));
			touchpad = new Touchpad(0, ts);
			touchpad.setPosition(20, 20);
			gui.addActor(touchpad);
			ButtonStyle bs = new ButtonStyle(new TextureRegionDrawable(
					Images.getImage("FireBtnUp")), new TextureRegionDrawable(
					Images.getImage("FireBtnDown")), new TextureRegionDrawable(
					Images.getImage("FireBtnUp")));
			firePad = new Touchpad(50, new TouchpadStyle(
					new TextureRegionDrawable(Images.getImage("FireBtnUp")),
					new TextureRegionDrawable(Images.getImage("FireBtnKnob"))));
			// fireBtn.addListener(new InputListener() {
			//
			// @Override
			// public boolean touchDown(InputEvent event, float x, float y,
			// int pointer, int button) {
			// fire = true;
			// return super.touchDown(event, x, y, pointer, button);
			// }
			//
			// @Override
			// public void touchUp(InputEvent event, float x, float y,
			// int pointer, int button) {
			// super.touchUp(event, x, y, pointer, button);
			// }
			//
			// });
			// fireBtn.setVisible(true);
			// fireBtn.setZIndex(1000);
			gui.addActor(firePad);
		}
		// w = width;
		// h = height;
		w = s.getWidth();
		h = s.getHeight();
		if (mobile) {
			firePad.setX(w - firePad.getWidth() - 20);
			// fireBtn.setX(200);
			firePad.setY(20);
		}
	}

}
