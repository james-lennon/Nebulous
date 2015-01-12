package com.jameslennon.nebulous.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader.PreloaderCallback;
import com.badlogic.gdx.backends.gwt.preloader.Preloader.PreloaderState;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.jameslennon.nebulous.Nebulous;

public class GwtLauncher extends GwtApplication {
	protected static final int APP_WIDTH = 1024;
	protected static final int APP_HEIGHT = 640;

	@Override
	public GwtApplicationConfiguration getConfig() {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(
				APP_WIDTH, APP_HEIGHT);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return new Nebulous();
	}

	@Override
	public PreloaderCallback getPreloaderCallback() {
		final Canvas canvas = Canvas.createIfSupported();
		canvas.setWidth("" + (int) (APP_WIDTH * 0.7f) + "px");
		canvas.setHeight("70px");
		getRootPanel().add(canvas);
		final Context2d context = canvas.getContext2d();
		context.setTextAlign(TextAlign.CENTER);
		context.setTextBaseline(TextBaseline.MIDDLE);
		context.setFont("18pt Calibri");
		context.setFillStyle(CssColor.make(0, 0, 0));
		context.fillRect(0, 0, getConfig().width, getConfig().height);

		return new PreloaderCallback() {
			// @Override
			// public void loaded (String file, int loaded, int total) {
			// System.out.println("loaded " + file + "," + loaded + "/" +
			// total);
			// String color = Pixmap.make(30, 30, 30, 1);
			// context.setFillStyle(color);
			// context.setStrokeStyle(color);
			// context.fillRect(0, 0, 300, 70);
			// color = Pixmap.make(200, 200, 200, (((TimeUtils.nanoTime() -
			// loadStart) % 1000000000) / 1000000000f));
			// context.setFillStyle(color);
			// context.setStrokeStyle(color);
			// context.fillRect(0, 0, 300 * (loaded / (float)total) * 0.97f,
			// 70);
			//
			// context.setFillStyle(Pixmap.make(50, 50, 50, 1));
			// context.fillText("loading", 300 / 2, 70 / 2);
			// }

			@Override
			public void error(String file) {
				System.out.println("error: " + file);
			}

			@Override
			public void update(PreloaderState state) {
			}
		};
	}

}