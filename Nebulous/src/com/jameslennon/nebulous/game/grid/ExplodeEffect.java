package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.Images;

public class ExplodeEffect extends GridItem {

	public ExplodeEffect(float x, float y, float w) {
		img = new Image(Images.getImage("Circle"));
		img.setScaling(Scaling.stretch);
		img.setWidth(w);
		img.setHeight(w);
		img.setX(x * Nebulous.PIXELS_PER_METER - img.getWidth() / 2);
		img.setY(y * Nebulous.PIXELS_PER_METER - img.getHeight() / 2);
		img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
		img.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeOut(.5f)));
		img.addAction(Actions.sequence(Actions.scaleBy(3, 3, 1),
				Actions.run(new Runnable() {

					@Override
					public void run() {
						die();
					}
				})));
		show(Globals.getStage());
	}

}
