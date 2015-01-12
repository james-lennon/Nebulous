package com.jameslennon.nebulous.util;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class StarBackground extends Image {

	public StarBackground(int n, float xscale, float yscale) {
		super(Images.getImage("background"));
		setWidth(getWidth() * xscale);
		setHeight(getHeight() * yscale);
		setZIndex(0);
		// setY(-786*n);
		// final SequenceAction sa = Actions.sequence(Actions.moveBy(0, -768,
		// 30),Actions.moveBy(0, 1536));
		// final SequenceAction run = Actions.sequence(sa, Actions.run(new
		// Runnable() {
		//
		// @Override
		// public void run() {
		// addAction(run);
		// }
		// }));
		// addAction(run);
	}

	public static void initialize(Stage s, int width, int height) {
		s.addActor(new StarBackground(0, width / 1024f, height / 640f));
		// s.addActor(new StarBackground(1));
	}

}
