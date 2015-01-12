package com.jameslennon.nebulous.game.ships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.TimeUtils;
import com.jameslennon.nebulous.Nebulous;
import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.game.grid.Flag;
import com.jameslennon.nebulous.game.grid.GridItem;
import com.jameslennon.nebulous.game.shots.Projectile;
import com.jameslennon.nebulous.net.NetworkManager;
import com.jameslennon.nebulous.net.Peer;
import com.jameslennon.nebulous.singleplayer.SinglePlayerManager;
import com.jameslennon.nebulous.util.GameType;
import com.jameslennon.nebulous.util.Images;
import com.jameslennon.nebulous.util.ParticleEffectActor;
import com.jameslennon.nebulous.util.UserData;

public class Ship extends GridItem {
	public static final int STATE_ALIVE = 0, STATE_DEAD = 1,
			STATE_SPAWNING = 2;

	private float acc;
	private boolean thrust;
	private Label nameLabel;
	private int state;
	private long startSpawn, startDeath, startStun, lasthit, startAtk,
			startInv, startFast, lastShot;
	private byte lasthitter;
	private int maxhealth;
	private boolean vibrate;
	private float dx, dy;
	private Peer peer;
	private int prevhealth;
	private Flag flag;
	private String imgName;
	private ParticleEffect death;
	private ParticleEffectActor pea;

	private long flagstart;

	private ParticleEffectActor stunned;

	private boolean mainplayer;

	public Ship(Peer p, float x, float y, String imgName, float acc,
			float angleAcc, float density, int health) {
		this(p, x, y, imgName, acc + 4, angleAcc, density, .95f, health);

	}

	public Ship(Peer p, float x, float y, String imgName, float acc,
			float angleAcc, float density, float damp, int health) {
		this.imgName = imgName;

		// PolygonShape circle = new PolygonShape();
		// circle.setAsBox(img.getWidth() / 2, img.getHeight() / 2);

		this.acc = acc;
		// this.angleAcc = angleAcc;
		// if (p != null) {
		// nameLabel = new Label(p.getName(), Globals.getSkin());
		// // nameLabel.setX(getX());
		// // nameLabel.setY(getY());
		// } else if (isPlayer()) {
		// nameLabel = new Label(UserData.getName(), Globals.getSkin());
		// } else {
		// nameLabel = new Label("", Globals.getSkin());
		// }
		// nameLabel
		// .setOrigin(nameLabel.getWidth() / 2, nameLabel.getHeight() / 2);

		// nameLabel.setZIndex(120);
		// if (!NetworkManager.isOnline()
		// || p.getTeam() == NetworkManager.getPeer().getTeam())
		// nameLabel.setColor(0, 1, 0, 1);
		// else {
		// nameLabel.setColor(1, 0, 0, 1);
		// }

		ParticleEffect trail = new ParticleEffect();
		trail.load(Gdx.files.internal("data/Trail.p"), Images.getAtlas());
		// trail.allowCompletion();
		pea = new ParticleEffectActor(trail, x * Nebulous.PIXELS_PER_METER, y
				* Nebulous.PIXELS_PER_METER, false);
		pea.setPosition(-10000, -100000);

		maxhealth = health;
		updateHealth();

		BodyDef c = new BodyDef();
		c.type = BodyType.DynamicBody;
		c.position.set(new Vector2((float) x / Nebulous.PIXELS_PER_METER,
				(float) y / Nebulous.PIXELS_PER_METER));
		// c.linearDamping = damp;
		c.linearDamping = damp;
		c.angularDamping = 1.0f;
		CircleShape circle = new CircleShape();
		circle.setRadius((img.getWidth() / Nebulous.PIXELS_PER_METER + img
				.getHeight() / Nebulous.PIXELS_PER_METER) / 4);
		FixtureDef fixture = new FixtureDef();
		fixture.density = density;
		fixture.friction = .1f;
		fixture.shape = circle;
		fixture.restitution = .1f;
		fixture.filter.categoryBits = 1;
		fixture.filter.maskBits = 3;
		body = Globals.getWorld().createBody(c);
		body.createFixture(fixture);
		body.setUserData(this);

		peer = p;
		spawn();

		death = new ParticleEffect();

		// pl = new PointLight(Globals.getRayHandler(), 50);
		// pl.setDistance(20);
	}

	public void updateHealth() {
		if (img != null)
			img.remove();
		Pixmap src = new Pixmap(Gdx.files.internal("res/" + imgName + ".png"));
		int w = src.getWidth(), h = src.getHeight();
		Pixmap pm = new Pixmap(w, h, Format.RGBA8888);
		float val = getHealth() / (float) maxhealth;
		float r, g;
		if (val > .5) {
			r = 1 - 2 * (val - .5f);
			g = 1;
		} else {
			r = 1;
			g = 2 * val;
		}
		Color color = new Color();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int v = src.getPixel(i, j);
				// int a = 255 - ((v >> 0) & 255);
				Color.rgba8888ToColor(color, v);
				// b = v >> 24 & 255, g = v >> 16 & 255 & 255, r = v >> 8 & 255,
				if (color.a != 0) {
					// Gdx.app.log(Nebulous.log, a + "");
					pm.setColor(r, g, 0, color.a);
					pm.drawPixel(i, j);
				}
			}
		}
		img = new Image(new Texture(pm));
		// img.setWidth(img.getWidth() / Nebulous.PIXELS_PER_METER);
		// img.setHeight(img.getHeight() / Nebulous.PIXELS_PER_METER);
		img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
		// img.setZIndex(1000);
		show(Globals.getStage());
	}

	public void update() {
		if (isDead())
			return;
		super.update();
		if (getHealth() != prevhealth) {
			prevhealth = getHealth();
			updateHealth();
		}
		if (state == STATE_DEAD) {
			body.setLinearVelocity(0, 0);
			body.setAngularVelocity(0);
			body.setTransform(1000000 * Nebulous.PIXELS_PER_METER,
					10000000 * Nebulous.PIXELS_PER_METER, 0);
			pea.setPosition(100000 * Nebulous.PIXELS_PER_METER,
					10000000 * Nebulous.PIXELS_PER_METER);
			img.setColor(1, 1, 1, 0);
			if (TimeUtils.millis() - startDeath >= 2000) {
				spawn();
			}
			return;
		}
		if (System.currentTimeMillis() - startStun >= 1000 && stunned != null) {
			stunned.getEffect().allowCompletion();
			stunned.remove();
			stunned = null;
		}
		if (stunned != null)
			stunned.setPosition(getX() * Nebulous.PIXELS_PER_METER, getY()
					* Nebulous.PIXELS_PER_METER);

		// pl.setPosition(body.getPosition());

		Vector2 vel = body.getLinearVelocity();
		if (Math.abs(vel.x) > 1 || Math.abs(vel.y) > 1) {
			float ox = MathUtils.cos(getAngle() - MathUtils.PI / 2), oy = MathUtils
					.sin(getAngle() - MathUtils.PI / 2);
			pea.setPosition(
					getX() * Nebulous.PIXELS_PER_METER + ox * img.getHeight()
							/ 2,
					getY() * Nebulous.PIXELS_PER_METER + oy * img.getHeight()
							/ 2);
		} else {
			pea.setPosition(10000 * Nebulous.PIXELS_PER_METER,
					10000 * Nebulous.PIXELS_PER_METER);
		}

		img.setX((body.getPosition().x * Nebulous.PIXELS_PER_METER - img
				.getWidth() / 2));
		img.setY((body.getPosition().y * Nebulous.PIXELS_PER_METER - img
				.getHeight() / 2));
		img.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		if (state == STATE_ALIVE || state == STATE_SPAWNING) {
			if (thrust) {
				body.applyForceToCenter(dx * getAcc(), dy * getAcc(), true);
			}
		}
		Vector2 pos = body.getPosition();
		Vector3 loc = new Vector3(pos.x * Nebulous.PIXELS_PER_METER, pos.y
				* Nebulous.PIXELS_PER_METER, 0);
		Globals.getStage().getCamera().project(loc);
		Globals.getGui().getCamera().unproject(loc);
		loc.y = Globals.getGui().getHeight() - loc.y;
		// if (nameLabel.getStage() == null) {
		// Globals.getGui().addActor(nameLabel);
		// }
		// nameLabel.setText(peer.getName());
		if (nameLabel != null)
			nameLabel.setPosition(loc.x - nameLabel.getWidth() / 2,
					loc.y - img.getHeight() - 10);
		if (flag != null && peer.getId() == NetworkManager.getPlayerId()
				&& System.currentTimeMillis() - flagstart >= 2000) {
			NetworkManager.sendAwardFlag();
			flagstart = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - startInv <= 15000) {
			if (isPlayer()) {
				img.setColor(1, 1, 1, .55f);
			} else {
				img.setColor(1, 1, 1, .1f);
			}
			pea.setVisible(false);
			nameLabel.setColor(1, 1, 1, 0);
		} else {
			img.setColor(1, 1, 1, 1);
			// nameLabel.setColor(1, 1, 1, 1);
			pea.setVisible(true);
		}
		if (state == STATE_SPAWNING) {
			// getBody().setLinearVelocity(0, 0);
			// getBody().setAngularVelocity(0);
			img.setColor(1, 1, 1, .5f);
			if (TimeUtils.millis() - startSpawn >= 3000) {
				state = STATE_ALIVE;
				img.setColor(1, 1, 1, 1);
			}
		}
	}

	private float getAcc() {
		if (System.currentTimeMillis() - startFast < 15000) {
			return 2 * acc;
		} else {
			return acc;
		}
	}

	public void setState(float x, float y, float dx, float dy, float angle,
			float da) {
		body.setTransform(x, y, angle);
		body.setLinearVelocity(dx, dy);
		body.setAngularVelocity(da);
	}

	public void show(Stage s) {
		super.show(s);
		// if (nameLabel != null)
		// Globals.getGui().addActor(nameLabel);
		s.addActor(pea);
	}

	public float getX() {
		return body.getPosition().x;
	}

	public float getY() {
		return body.getPosition().y;
	}

	public float getAngle() {
		return body.getAngle();
	}

	public void setThrust(float amt) {
		thrust = true;
		// linAmt = amt;
	}

	public void removeThrust() {
		thrust = false;
		// trail.allowCompletion();
	}

	// public void setRotate(float amt) {
	// if (System.currentTimeMillis() - startStun < 2000) {
	// return;
	// }
	// rotate = true;
	// angAmt = amt;
	// }
	//
	// public void removeRotate() {
	// rotate = false;
	// }

	public Projectile getShot(float ang) {
		return null;
	}

	public void shoot() {
		if (state == STATE_DEAD || flag != null)
			return;
		if (System.currentTimeMillis() - startStun < 2000) {
			return;
		}
		if (System.currentTimeMillis() - lastShot < getWaitTime())
			return;
		lastShot = System.currentTimeMillis();
		addShot(getAngle());
		if (System.currentTimeMillis() - startAtk < 15000) {
			addShot(getAngle() + MathUtils.PI / 15);
			addShot(getAngle() - MathUtils.PI / 15);
		}

	}

	private void addShot(float ang) {
		Projectile p = getShot(ang);
		Globals.getMap().addItem(p);
		p.show();
		if (GameType.getType() == GameType.online) {
			NetworkManager.sendShot(p);
		} else if (GameType.getType() == GameType.bluetooth) {
			Nebulous.bluetooth.sendShot(p);
		}
	}

	public int getWaitTime() {
		return 500;
	}

	@Override
	public void die() {
		if (state == STATE_DEAD)
			return;
		// Globals.getMap().addItem(
		// new ExplodeEffect(getX(), getY(), (img.getWidth() + img
		// .getHeight()) / 2));
		death.load(Gdx.files.internal("data/Explosion2.p"), Images.getAtlas());
		ParticleEffectActor da = new ParticleEffectActor(death, getX()
				* Nebulous.PIXELS_PER_METER - img.getWidth() / 2, getY()
				* Nebulous.PIXELS_PER_METER - img.getHeight() / 2);
		Globals.getStage().addActor(da);
		if (stunned != null)
			stunned.getEffect().allowCompletion();
		removeThrust();
		setHealth(1);
		img.setVisible(false);
		state = STATE_DEAD;
		startDeath = TimeUtils.millis();
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				// nameLabel.setVisible(false);
				nameLabel.remove();
				nameLabel = null;
			}
		});
		if (GameType.getType() == GameType.online) {
			if (peer.getId() == NetworkManager.getPlayerId()) {
				if (System.currentTimeMillis() - lasthit <= 2000
						&& lasthitter != getPeer().getId()) {
					NetworkManager.sendKill(lasthitter);
					Globals.getScreen().message(
							NetworkManager.getLobby().getMembers()
									.get((int) lasthitter).getName()
									+ " killed you.");
				} else {
					Globals.getScreen().message("Suicide!");
					NetworkManager.sendDie();
				}
			}
		} else if (GameType.getType() == GameType.singleplayer) {
			if (isPlayer())
				SinglePlayerManager.removeLife();
			else if (lasthitter != -1
					&& System.currentTimeMillis() - lasthit <= 2000) {
				SinglePlayerManager.addKill();
				Globals.getScreen().message("Kill!");
			}
		} else if (GameType.getType() == GameType.bluetooth) {
			if (isPlayer()) {
				if (System.currentTimeMillis() - lasthit <= 2000
						&& lasthitter != getPeer().getId()) {
					Nebulous.bluetooth.sendKill(lasthitter);
					Globals.getScreen().message(
							Nebulous.bluetooth.getLobby().getMembers()
									.get((int) lasthitter).getName()
									+ " killed you.");
				} else {
					Globals.getScreen().message("Suicide!");
					Nebulous.bluetooth.sendDie();
				}
			}
		}
		if (flag != null) {
			flag.die();
			flag = null;
		}

	}

	public int getState() {
		return state;
	}

	public void spawn() {
		state = STATE_SPAWNING;
		startSpawn = TimeUtils.millis();
		img.setVisible(true);
		if (peer != null) {
			nameLabel = new Label(peer.getName(), Globals.getSkin());
			// nameLabel.setX(getX());
			// nameLabel.setY(getY());
		} else if (isPlayer()) {
			nameLabel = new Label(UserData.getName(), Globals.getSkin());
		}
		nameLabel
				.setOrigin(nameLabel.getWidth() / 2, nameLabel.getHeight() / 2);
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				Globals.getGui().addActor(nameLabel);
			}
		});

		Vector2 spawn;
		if (GameType.getType() == GameType.online) {
			spawn = Globals.getMap().getSpawn(peer.getId());
		} else if (GameType.getType() == GameType.singleplayer) {
			if (isPlayer() && SinglePlayerManager.getLives() == 0)
				SinglePlayerManager.endGame();
			spawn = Globals.getMap().getRandomSpawn(!isPlayer());
		} else if (GameType.getType() == GameType.bluetooth) {
			spawn = Globals.getMap().getSpawn(peer.getId());
		} else {
			spawn = new Vector2();
		}
		body.setTransform(spawn, 0);
		setHealth(maxhealth);
	}

	public void disconnect() {
		// super.die();
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				Ship.super.die();
				if (nameLabel != null)
					nameLabel.remove();
				pea.getEffect().allowCompletion();
			}
		});
	}

	public void setVibrate(boolean b) {
		vibrate = b;
	}

	@Override
	public void changeHealth(int amt, byte id) {
		if (getState() != STATE_ALIVE)
			return;
		if (GameType.getType() == GameType.online) {
			if (peer != null && peer.getId() == NetworkManager.getPlayerId()) {
				// super.changeHealth(amt);
				peer.sendHealth(getHealth() + amt);
			}
		} else if (GameType.getType() == GameType.singleplayer)
			super.changeHealth(amt, (byte) -1);
		else if (GameType.getType() == GameType.bluetooth) {
			if (peer != null
					&& peer.getId() == Nebulous.bluetooth.getPlayerId()) {
				Nebulous.bluetooth.sendHealth(getHealth() + amt);
			}
		}
		lasthitter = id;
		lasthit = System.currentTimeMillis();
//		if (vibrate)
//			Gdx.input.vibrate(500);
		
		// updateHealth();
	}

	public void setRotation(float ang) {
		if (System.currentTimeMillis() - startStun < 2000) {
			return;
		}
		getBody().setAngularVelocity(0);
		getBody().setTransform(getX(), getY(), ang);
	}

	public void thrust(float dx, float dy) {
		if (System.currentTimeMillis() - startStun < 2000) {
			thrust = false;
			return;
		}
		if (state == STATE_ALIVE || state == STATE_SPAWNING) {
			pea.getEffect().start();
			thrust = true;
			this.dx = dx;
			this.dy = dy;
		}
	}

	public Peer getPeer() {
		return peer;
	}

	public void setFlag(Flag f) {
		flag = f;
		flagstart = System.currentTimeMillis();
	}

	public void stun() {
		if (state != STATE_ALIVE)
			return;
		startStun = System.currentTimeMillis();
		if (stunned == null) {
			ParticleEffect peEffect = new ParticleEffect();
			peEffect.load(Gdx.files.internal("data/EMPTrail.p"),
					Images.getAtlas());
			stunned = new ParticleEffectActor(peEffect, getX()
					* Nebulous.PIXELS_PER_METER, getY()
					* Nebulous.PIXELS_PER_METER);
			Globals.getStage().addActor(stunned);
		}
	}

	public void boostAttack() {
		startAtk = System.currentTimeMillis();
	}

	public void setInvisible() {
		startInv = System.currentTimeMillis();
	}

	public boolean isInvisible() {
		return System.currentTimeMillis() - startInv < 15000;
	}

	public void boostSpeed() {
		startFast = System.currentTimeMillis();
	}

	public void setPlayer(boolean b) {
		mainplayer = b;
	}

	public boolean isPlayer() {
		if (GameType.getType() == GameType.singleplayer)
			return mainplayer;
		else if (GameType.getType() == GameType.online)
			return getPeer().getId() == NetworkManager.getPlayerId();
		else if (GameType.getType() == GameType.bluetooth)
			return getPeer().getId() == Nebulous.bluetooth.getPlayerId();
		return false;
	}
}
