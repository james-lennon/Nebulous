package com.jameslennon.nebulous.game.grid;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.jameslennon.nebulous.game.Globals;

public abstract class GridItem {
	protected Body body;
	protected Image img;
	private int health = 1;
	private boolean dead;

	public Body getBody() {
		return body;
	}

	public void show(Stage s) {
		s.addActor(img);
		dead = false;
		// img.setZIndex(105);
	}

	public void update() {
		if (health <= 0) {
			die();
		}
	}

	public void setHealth(int amt) {
		health = amt;
	}

	public void changeHealth(int amt, byte id) {
		health += amt;
	}

	public int getHealth() {
		return health;
	}

	public void die() {
		if (!dead) {
			img.setVisible(false);
			img.remove();
			Globals.getMap().removeItem(this);
			Globals.getMap().removeBody(getBody());
			dead = true;
		}
	}

	public void collide(GridItem other) {
	}
	
	public boolean isDead(){
		return dead;
	}

	public Image getImage() {
		return img;
	}

}
