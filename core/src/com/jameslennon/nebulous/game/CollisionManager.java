package com.jameslennon.nebulous.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.jameslennon.nebulous.game.grid.GridItem;
import com.jameslennon.nebulous.game.shots.Projectile;

public class CollisionManager implements ContactListener {

	public CollisionManager() {

	}

	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody(), b = contact.getFixtureB()
				.getBody();

		if (a.getUserData() instanceof GridItem
				&& b.getUserData() instanceof GridItem) {
			GridItem ga = (GridItem) a.getUserData(), gb = (GridItem) b
					.getUserData();
			// if(!ga.isDead() && !gb.isDead()){
			ga.collide(gb);
			gb.collide(ga);
			// }
			// .collide((GridItem) b.getUserData());
			// ((GridItem) b.getUserData()).collide((GridItem) a.getUserData());
		}
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Body a = contact.getFixtureA().getBody(), b = contact.getFixtureB()
				.getBody();
		if (a.getUserData() instanceof Projectile
				&& b.getUserData() instanceof Projectile) {
			contact.setEnabled(false);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

}
